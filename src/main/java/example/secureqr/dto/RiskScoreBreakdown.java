package example.secureqr.dto;

public class RiskScoreBreakdown {
    private double deviceChangeScore;
    private double locationDeviationScore;
    private double qrFrequencyScore;
    private double timePatternAnomalyScore;
    private double finalFraudProbability;

    public double getDeviceChangeScore() {
        return deviceChangeScore;
    }

    public void setDeviceChangeScore(double deviceChangeScore) {
        this.deviceChangeScore = deviceChangeScore;
    }

    public double getLocationDeviationScore() {
        return locationDeviationScore;
    }

    public void setLocationDeviationScore(double locationDeviationScore) {
        this.locationDeviationScore = locationDeviationScore;
    }

    public double getQrFrequencyScore() {
        return qrFrequencyScore;
    }

    public void setQrFrequencyScore(double qrFrequencyScore) {
        this.qrFrequencyScore = qrFrequencyScore;
    }

    public double getTimePatternAnomalyScore() {
        return timePatternAnomalyScore;
    }

    public void setTimePatternAnomalyScore(double timePatternAnomalyScore) {
        this.timePatternAnomalyScore = timePatternAnomalyScore;
    }

    public double getFinalFraudProbability() {
        return finalFraudProbability;
    }

    public void setFinalFraudProbability(double finalFraudProbability) {
        this.finalFraudProbability = finalFraudProbability;
    }
}
