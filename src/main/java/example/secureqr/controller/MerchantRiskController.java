package example.secureqr.controller;

import example.secureqr.dto.MerchantLoginRequest;
import example.secureqr.service.MerchantRiskService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/api/v2/risk")
public class MerchantRiskController {

    private final MerchantRiskService merchantRiskService;

    public MerchantRiskController(MerchantRiskService merchantRiskService) {
        this.merchantRiskService = merchantRiskService;
    }

    @PostMapping("/merchant/login")
    @ResponseBody
    public Map<String, Object> merchantLogin(@RequestBody MerchantLoginRequest request) {
        return merchantRiskService.registerLogin(request);
    }

    @GetMapping("/dashboard")
    @ResponseBody
    public List<Map<String, Object>> dashboardData() {
        return merchantRiskService.merchantDashboard();
    }

    @GetMapping("/dashboard/view")
    public String dashboardPage(Model model) {
        model.addAttribute("dashboardEndpoint", "/api/v2/risk/dashboard");
        return "admin/risk-dashboard";
    }
}
