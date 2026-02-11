package example.secureqr.model;

import java.time.Instant;

public class MerchantSession {
    private String sessionId;
    private String merchantId;
    private String deviceFingerprint;
    private String ipAddress;
    private String location;
    private Instant loginTimestamp;
    private int qrRegenerationCount;
    private int locationChangeCount;
    private int repeatedDeviceChangeCount;
    private double fraudProbabilityScore;
    private RiskLevel riskLevel;

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getMerchantId() {
        return merchantId;
    }

    public void setMerchantId(String merchantId) {
        this.merchantId = merchantId;
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

    public Instant getLoginTimestamp() {
        return loginTimestamp;
    }

    public void setLoginTimestamp(Instant loginTimestamp) {
        this.loginTimestamp = loginTimestamp;
    }

    public int getQrRegenerationCount() {
        return qrRegenerationCount;
    }

    public void setQrRegenerationCount(int qrRegenerationCount) {
        this.qrRegenerationCount = qrRegenerationCount;
    }

    public int getLocationChangeCount() {
        return locationChangeCount;
    }

    public void setLocationChangeCount(int locationChangeCount) {
        this.locationChangeCount = locationChangeCount;
    }

    public int getRepeatedDeviceChangeCount() {
        return repeatedDeviceChangeCount;
    }

    public void setRepeatedDeviceChangeCount(int repeatedDeviceChangeCount) {
        this.repeatedDeviceChangeCount = repeatedDeviceChangeCount;
    }

    public double getFraudProbabilityScore() {
        return fraudProbabilityScore;
    }

    public void setFraudProbabilityScore(double fraudProbabilityScore) {
        this.fraudProbabilityScore = fraudProbabilityScore;
    }

    public RiskLevel getRiskLevel() {
        return riskLevel;
    }

    public void setRiskLevel(RiskLevel riskLevel) {
        this.riskLevel = riskLevel;
    }
}
