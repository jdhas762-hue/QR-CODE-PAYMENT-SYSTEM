package example.secureqr.service;

import example.secureqr.dto.MerchantLoginRequest;
import example.secureqr.dto.RiskScoreBreakdown;
import example.secureqr.model.MerchantSession;
import example.secureqr.model.QRTransaction;
import example.secureqr.model.RiskLevel;
import example.secureqr.store.SecurityDataStore;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class MerchantRiskService {

    private final SecurityDataStore dataStore;
    private final RiskScoringService riskScoringService;

    public MerchantRiskService(SecurityDataStore dataStore, RiskScoringService riskScoringService) {
        this.dataStore = dataStore;
        this.riskScoringService = riskScoringService;
    }

    public Map<String, Object> registerLogin(MerchantLoginRequest request) {
        MerchantSession previousSession = dataStore.getActiveSessions().get(request.getMerchantId());

        MerchantSession session = new MerchantSession();
        session.setSessionId(UUID.randomUUID().toString());
        session.setMerchantId(request.getMerchantId());
        session.setDeviceFingerprint(request.getDeviceFingerprint());
        session.setIpAddress(request.getIpAddress());
        session.setLocation(request.getLocation());
        session.setLoginTimestamp(Instant.now());
        session.setQrRegenerationCount(previousSession != null ? previousSession.getQrRegenerationCount() : 0);

        if (previousSession != null) {
            if (!previousSession.getLocation().equalsIgnoreCase(request.getLocation())) {
                session.setLocationChangeCount(previousSession.getLocationChangeCount() + 1);
            } else {
                session.setLocationChangeCount(previousSession.getLocationChangeCount());
            }

            if (!previousSession.getDeviceFingerprint().equals(request.getDeviceFingerprint())) {
                session.setRepeatedDeviceChangeCount(previousSession.getRepeatedDeviceChangeCount() + 1);
            } else {
                session.setRepeatedDeviceChangeCount(previousSession.getRepeatedDeviceChangeCount());
            }
        }

        RiskScoreBreakdown breakdown = riskScoringService.evaluate(session);
        dataStore.getActiveSessions().put(request.getMerchantId(), session);

        List<Double> trend = dataStore.getOrCreateRiskTrendBucket(request.getMerchantId());
        trend.add(session.getFraudProbabilityScore());

        Map<String, Object> response = new HashMap<String, Object>();
        response.put("sessionId", session.getSessionId());
        response.put("riskLevel", session.getRiskLevel().name());
        response.put("fraudProbability", session.getFraudProbabilityScore());
        response.put("scoreBreakdown", breakdown);
        return response;
    }

    public List<Map<String, Object>> merchantDashboard() {
        List<Map<String, Object>> dashboardRows = new ArrayList<Map<String, Object>>();
        for (Map.Entry<String, MerchantSession> entry : dataStore.getActiveSessions().entrySet()) {
            String merchantId = entry.getKey();
            MerchantSession session = entry.getValue();

            int suspiciousAttempts = 0;
            int replayAttempts = 0;
            int expiredAttempts = 0;
            int deviceMismatch = 0;

            for (QRTransaction transaction : dataStore.getTransactions().values()) {
                if (!merchantId.equals(transaction.getMerchantId())) {
                    continue;
                }
                suspiciousAttempts += transaction.getAmountMismatchAttempts() + transaction.getDeviceMismatchAttempts();
                replayAttempts += transaction.getReplayAttempts();
                expiredAttempts += transaction.getExpiredAttempts();
                deviceMismatch += transaction.getDeviceMismatchAttempts();
            }

            Map<String, Object> row = new HashMap<String, Object>();
            row.put("merchantId", merchantId);
            row.put("riskScore", session.getFraudProbabilityScore());
            row.put("fraudProbability", session.getFraudProbabilityScore());
            row.put("suspiciousAttemptCount", suspiciousAttempts);
            row.put("replayAttemptCount", replayAttempts);
            row.put("expiredQrMisuseAttempts", expiredAttempts);
            row.put("deviceMismatchCount", deviceMismatch);
            row.put("locationChangeCount", session.getLocationChangeCount());
            row.put("riskStatus", session.getRiskLevel() != null ? session.getRiskLevel().name() : RiskLevel.LOW_RISK.name());
            row.put("qrGenerationFrequency", dataStore.getOrCreateFrequencyBucket(merchantId));
            row.put("riskScoreTrend", dataStore.getOrCreateRiskTrendBucket(merchantId));
            dashboardRows.add(row);
        }
        return dashboardRows;
    }
}
