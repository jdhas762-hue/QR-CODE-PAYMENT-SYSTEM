package example.secureqr.util;

import org.springframework.stereotype.Component;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Component
public class HmacSignatureUtil {

    private static final String ALGORITHM = "HmacSHA256";

    public String sign(String payload, String secretKey) {
        try {
            Mac mac = Mac.getInstance(ALGORITHM);
            SecretKeySpec keySpec = new SecretKeySpec(secretKey.getBytes(StandardCharsets.UTF_8), ALGORITHM);
            mac.init(keySpec);
            byte[] hmac = mac.doFinal(payload.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hmac);
        } catch (Exception e) {
            throw new IllegalStateException("Unable to generate HMAC signature", e);
        }
    }

    public boolean verify(String payload, String expectedSignature, String secretKey) {
        String generated = sign(payload, secretKey);
        return generated.equals(expectedSignature);
    }
}
