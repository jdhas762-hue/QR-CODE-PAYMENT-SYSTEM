package example.secureqr.model;

import java.time.Instant;

public class QRTransaction {
    private String transactionId;
    private String merchantId;
    private double amount;
    private Instant timestamp;
    private String deviceFingerprint;
    private String ipAddress;
    private String location;
    private String nonce;
    private QrStatus status;
    private int expiredAttempts;
    private int deviceMismatchAttempts;
    private int amountMismatchAttempts;
    private int replayAttempts;

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getMerchantId() {
        return merchantId;
    }

    public void setMerchantId(String merchantId) {
        this.merchantId = merchantId;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }

    public String getDeviceFingerprint() {
        return deviceFingerprint;
    }

    public void setDeviceFingerprint(String deviceFingerprint) {
        this.deviceFingerprint = deviceFingerprint;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getNonce() {
        return nonce;
    }

    public void setNonce(String nonce) {
        this.nonce = nonce;
    }

    public QrStatus getStatus() {
        return status;
    }

    public void setStatus(QrStatus status) {
        this.status = status;
    }

    public int getExpiredAttempts() {
        return expiredAttempts;
    }

    public void setExpiredAttempts(int expiredAttempts) {
        this.expiredAttempts = expiredAttempts;
    }

    public int getDeviceMismatchAttempts() {
        return deviceMismatchAttempts;
    }

    public void setDeviceMismatchAttempts(int deviceMismatchAttempts) {
        this.deviceMismatchAttempts = deviceMismatchAttempts;
    }

    public int getAmountMismatchAttempts() {
        return amountMismatchAttempts;
    }

    public void setAmountMismatchAttempts(int amountMismatchAttempts) {
        this.amountMismatchAttempts = amountMismatchAttempts;
    }

    public int getReplayAttempts() {
        return replayAttempts;
    }

    public void setReplayAttempts(int replayAttempts) {
        this.replayAttempts = replayAttempts;
    }
}
