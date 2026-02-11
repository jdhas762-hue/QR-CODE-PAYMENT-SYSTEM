package example.secureqr.controller;

import example.secureqr.dto.QrGenerationRequest;
import example.secureqr.dto.QrScanRequest;
import example.secureqr.service.QrSecurityService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/v2/secure-qr")
public class SecureQrController {

    private final QrSecurityService qrSecurityService;

    public SecureQrController(QrSecurityService qrSecurityService) {
        this.qrSecurityService = qrSecurityService;
    }

    @PostMapping("/generate")
    public Map<String, Object> generate(@RequestBody QrGenerationRequest request,
                                        @RequestHeader(value = "X-Forwarded-For", required = false) String ipAddress) {
        String clientIp = ipAddress == null ? "127.0.0.1" : ipAddress;
        return qrSecurityService.generateSecureQr(request, clientIp);
    }

    @PostMapping("/scan")
    public Map<String, Object> scan(@RequestBody QrScanRequest request) {
        return qrSecurityService.validateScan(request);
    }
}
