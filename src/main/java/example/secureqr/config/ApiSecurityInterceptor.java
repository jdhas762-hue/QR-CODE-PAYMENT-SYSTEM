package example.secureqr.config;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.Instant;

@Component
public class ApiSecurityInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (!request.getRequestURI().startsWith("/api/v2")) {
            return true;
        }

        String nonce = request.getHeader("X-Request-Nonce");
        String timestamp = request.getHeader("X-Request-Timestamp");
        String merchantId = request.getHeader("X-Merchant-Id");

        if (nonce == null || timestamp == null || merchantId == null) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Missing security headers");
            return false;
        }

        long requestEpoch = Long.parseLong(timestamp);
        long driftSeconds = Math.abs(Instant.now().getEpochSecond() - requestEpoch);
        if (driftSeconds > 180) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Request timestamp drift too high");
            return false;
        }

        return true;
    }
}
