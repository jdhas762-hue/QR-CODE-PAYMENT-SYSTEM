package example.secureqr.dto;

public class QrScanRequest {
    private String transactionId;
    private double scannedAmount;
    private String scannedDeviceFingerprint;
    private String encodedToken;

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public double getScannedAmount() {
        return scannedAmount;
    }

    public void setScannedAmount(double scannedAmount) {
        this.scannedAmount = scannedAmount;
    }

    public String getScannedDeviceFingerprint() {
        return scannedDeviceFingerprint;
    }

    public void setScannedDeviceFingerprint(String scannedDeviceFingerprint) {
        this.scannedDeviceFingerprint = scannedDeviceFingerprint;
    }

    public String getEncodedToken() {
        return encodedToken;
    }

    public void setEncodedToken(String encodedToken) {
        this.encodedToken = encodedToken;
    }
}
