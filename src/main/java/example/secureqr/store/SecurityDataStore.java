package example.secureqr.store;

import example.secureqr.model.MerchantSession;
import example.secureqr.model.QRTransaction;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class SecurityDataStore {

    private final Map<String, QRTransaction> transactions = new ConcurrentHashMap<String, QRTransaction>();
    private final Map<String, MerchantSession> activeSessions = new ConcurrentHashMap<String, MerchantSession>();
    private final Set<String> usedTokens = ConcurrentHashMap.newKeySet();
    private final Map<String, List<Integer>> merchantHourlyQrFrequency = new ConcurrentHashMap<String, List<Integer>>();
    private final Map<String, List<Double>> merchantRiskTrend = new ConcurrentHashMap<String, List<Double>>();

    public Map<String, QRTransaction> getTransactions() {
        return transactions;
    }

    public Map<String, MerchantSession> getActiveSessions() {
        return activeSessions;
    }

    public Set<String> getUsedTokens() {
        return usedTokens;
    }

    public Map<String, List<Integer>> getMerchantHourlyQrFrequency() {
        return merchantHourlyQrFrequency;
    }

    public Map<String, List<Double>> getMerchantRiskTrend() {
        return merchantRiskTrend;
    }

    public List<Integer> getOrCreateFrequencyBucket(String merchantId) {
        List<Integer> frequencies = merchantHourlyQrFrequency.get(merchantId);
        if (frequencies == null) {
            frequencies = new ArrayList<Integer>();
            for (int i = 0; i < 24; i++) {
                frequencies.add(0);
            }
            merchantHourlyQrFrequency.put(merchantId, frequencies);
        }
        return frequencies;
    }

    public List<Double> getOrCreateRiskTrendBucket(String merchantId) {
        List<Double> trend = merchantRiskTrend.get(merchantId);
        if (trend == null) {
            trend = new ArrayList<Double>();
            merchantRiskTrend.put(merchantId, trend);
        }
        return trend;
    }
}
