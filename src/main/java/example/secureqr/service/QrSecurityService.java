package example.secureqr.service;

import example.secureqr.dto.QrGenerationRequest;
import example.secureqr.dto.QrScanRequest;
import example.secureqr.dto.SignedPayload;
import example.secureqr.model.MerchantSession;
import example.secureqr.model.QRTransaction;
import example.secureqr.model.QrStatus;
import example.secureqr.store.SecurityDataStore;
import example.secureqr.util.GeoLocationResolver;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class QrSecurityService {

    private final SecurityDataStore dataStore;
    private final GeoLocationResolver geoLocationResolver;
    private final SignedTokenService signedTokenService;

    public QrSecurityService(SecurityDataStore dataStore,
                             GeoLocationResolver geoLocationResolver,
                             SignedTokenService signedTokenService) {
        this.dataStore = dataStore;
        this.geoLocationResolver = geoLocationResolver;
        this.signedTokenService = signedTokenService;
    }

    public Map<String, Object> generateSecureQr(QrGenerationRequest request, String ipAddress) {
        QRTransaction transaction = new QRTransaction();
        transaction.setTransactionId(UUID.randomUUID().toString());
        transaction.setMerchantId(request.getMerchantId());
        transaction.setAmount(request.getAmount());
        transaction.setTimestamp(Instant.now());
        transaction.setDeviceFingerprint(request.isDeviceBound() ? request.getDeviceFingerprint() : null);
        transaction.setIpAddress(ipAddress);
        transaction.setLocation(geoLocationResolver.approximateLocation(ipAddress));
        transaction.setNonce(UUID.randomUUID().toString());
        transaction.setStatus(QrStatus.CREATED);

        dataStore.getTransactions().put(transaction.getTransactionId(), transaction);
        incrementFrequency(request.getMerchantId());

        SignedPayload payload = new SignedPayload();
        payload.setMerchantId(transaction.getMerchantId());
        payload.setAmount(transaction.getAmount());
        payload.setTimestamp(transaction.getTimestamp().toEpochMilli());
        payload.setUniqueTransactionId(transaction.getTransactionId());
        payload.setNonce(transaction.getNonce());

        String token = signedTokenService.generateSignedToken(payload);

        Map<String, Object> response = new HashMap<String, Object>();
        response.put("transactionId", transaction.getTransactionId());
        response.put("token", token);
        response.put("expiresInSeconds", 120);
        response.put("status", transaction.getStatus().name());
        return response;
    }

    public Map<String, Object> validateScan(QrScanRequest request) {
        QRTransaction transaction = dataStore.getTransactions().get(request.getTransactionId());
        if (transaction == null) {
            throw new IllegalArgumentException("Unknown transaction");
        }

        SignedPayload payload = signedTokenService.decodeAndVerify(request.getEncodedToken());
        if (dataStore.getUsedTokens().contains(payload.getUniqueTransactionId())) {
            transaction.setReplayAttempts(transaction.getReplayAttempts() + 1);
            transaction.setStatus(QrStatus.SUSPICIOUS);
            return buildResult(transaction, false, "Replay attempt detected");
        }

        long seconds = Duration.between(transaction.getTimestamp(), Instant.now()).getSeconds();
        if (seconds > 120) {
            transaction.setExpiredAttempts(transaction.getExpiredAttempts() + 1);
            transaction.setStatus(QrStatus.EXPIRED);
            return buildResult(transaction, false, "QR expired due to timeout");
        }

        if (Double.compare(transaction.getAmount(), request.getScannedAmount()) != 0) {
            transaction.setAmountMismatchAttempts(transaction.getAmountMismatchAttempts() + 1);
            transaction.setStatus(QrStatus.SUSPICIOUS);
            return buildResult(transaction, false, "Amount mismatch detected");
        }

        if (transaction.getDeviceFingerprint() != null
                && !transaction.getDeviceFingerprint().equals(request.getScannedDeviceFingerprint())) {
            transaction.setDeviceMismatchAttempts(transaction.getDeviceMismatchAttempts() + 1);
            transaction.setStatus(QrStatus.SUSPICIOUS);
            return buildResult(transaction, false, "Device mismatch detected");
        }

        dataStore.getUsedTokens().add(payload.getUniqueTransactionId());
        transaction.setStatus(QrStatus.SCANNED);
        return buildResult(transaction, true, "Payment verification successful");
    }

    private Map<String, Object> buildResult(QRTransaction transaction, boolean valid, String reason) {
        Map<String, Object> result = new HashMap<String, Object>();
        result.put("valid", valid);
        result.put("reason", reason);
        result.put("status", transaction.getStatus().name());
        result.put("expiredAttempts", transaction.getExpiredAttempts());
        result.put("deviceMismatchAttempts", transaction.getDeviceMismatchAttempts());
        result.put("amountMismatchAttempts", transaction.getAmountMismatchAttempts());
        result.put("replayAttempts", transaction.getReplayAttempts());
        return result;
    }

    private void incrementFrequency(String merchantId) {
        List<Integer> frequency = dataStore.getOrCreateFrequencyBucket(merchantId);
        int hour = Instant.now().atZone(java.time.ZoneOffset.UTC).getHour();
        frequency.set(hour, frequency.get(hour) + 1);

        MerchantSession session = dataStore.getActiveSessions().get(merchantId);
        if (session != null) {
            session.setQrRegenerationCount(session.getQrRegenerationCount() + 1);
        }
    }
}
