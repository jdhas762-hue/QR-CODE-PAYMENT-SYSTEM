package example.secureqr.dto;

public class QrGenerationRequest {
    private String merchantId;
    private double amount;
    private String deviceFingerprint;
    private boolean deviceBound;

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

    public String getDeviceFingerprint() {
        return deviceFingerprint;
    }

    public void setDeviceFingerprint(String deviceFingerprint) {
        this.deviceFingerprint = deviceFingerprint;
    }

    public boolean isDeviceBound() {
        return deviceBound;
    }

    public void setDeviceBound(boolean deviceBound) {
        this.deviceBound = deviceBound;
    }
}
