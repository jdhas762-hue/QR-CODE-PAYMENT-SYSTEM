package example.secureqr.service;

import example.secureqr.dto.RiskScoreBreakdown;
import example.secureqr.model.MerchantSession;
import example.secureqr.model.RiskLevel;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Service
public class RiskScoringService {

    public RiskScoreBreakdown evaluate(MerchantSession session) {
        RiskScoreBreakdown breakdown = new RiskScoreBreakdown();

        double deviceChangeScore = Math.min(100.0, session.getRepeatedDeviceChangeCount() * 25.0);
        double locationDeviationScore = Math.min(100.0, session.getLocationChangeCount() * 20.0);
        double qrFrequencyScore = Math.min(100.0, session.getQrRegenerationCount() * 5.0);

        LocalDateTime time = LocalDateTime.ofInstant(session.getLoginTimestamp(), ZoneOffset.UTC);
        int hour = time.getHour();
        double timePatternScore = (hour >= 0 && hour <= 5) ? 80.0 : 25.0;

        breakdown.setDeviceChangeScore(deviceChangeScore);
        breakdown.setLocationDeviationScore(locationDeviationScore);
        breakdown.setQrFrequencyScore(qrFrequencyScore);
        breakdown.setTimePatternAnomalyScore(timePatternScore);

        double weighted = (deviceChangeScore * 0.30)
                + (locationDeviationScore * 0.25)
                + (qrFrequencyScore * 0.25)
                + (timePatternScore * 0.20);

        breakdown.setFinalFraudProbability(Math.min(100.0, weighted));
        session.setFraudProbabilityScore(breakdown.getFinalFraudProbability());
        session.setRiskLevel(resolveRiskLevel(weighted));

        return breakdown;
    }

    private RiskLevel resolveRiskLevel(double score) {
        if (score >= 70.0) {
            return RiskLevel.HIGH_RISK;
        }
        if (score >= 40.0) {
            return RiskLevel.MEDIUM_RISK;
        }
        return RiskLevel.LOW_RISK;
    }
}
