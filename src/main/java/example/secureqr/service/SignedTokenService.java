package example.secureqr.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import example.secureqr.dto.SignedPayload;
import example.secureqr.util.HmacSignatureUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Service
public class SignedTokenService {

    private final HmacSignatureUtil hmacSignatureUtil;
    private final ObjectMapper objectMapper;

    @Value("${app.security.hmac.secret:change-me-in-prod}")
    private String secretKey;

    public SignedTokenService(HmacSignatureUtil hmacSignatureUtil, ObjectMapper objectMapper) {
        this.hmacSignatureUtil = hmacSignatureUtil;
        this.objectMapper = objectMapper;
    }

    public String generateSignedToken(SignedPayload payload) {
        try {
            String canonicalPayload = payload.getMerchantId() + "|" + payload.getAmount() + "|" + payload.getTimestamp()
                    + "|" + payload.getUniqueTransactionId() + "|" + payload.getNonce();
            payload.setSignature(hmacSignatureUtil.sign(canonicalPayload, secretKey));
            String json = objectMapper.writeValueAsString(payload);
            return Base64.getEncoder().encodeToString(json.getBytes(StandardCharsets.UTF_8));
        } catch (Exception e) {
            throw new IllegalStateException("Unable to encode signed payload", e);
        }
    }

    public SignedPayload decodeAndVerify(String encodedToken) {
        try {
            byte[] decoded = Base64.getDecoder().decode(encodedToken);
            SignedPayload payload = objectMapper.readValue(new String(decoded, StandardCharsets.UTF_8), SignedPayload.class);

            String canonicalPayload = payload.getMerchantId() + "|" + payload.getAmount() + "|" + payload.getTimestamp()
                    + "|" + payload.getUniqueTransactionId() + "|" + payload.getNonce();
            boolean valid = hmacSignatureUtil.verify(canonicalPayload, payload.getSignature(), secretKey);
            if (!valid) {
                throw new IllegalArgumentException("Token signature mismatch");
            }
            return payload;
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid token format", e);
        }
    }
}
