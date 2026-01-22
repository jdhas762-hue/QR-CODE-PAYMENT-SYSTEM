package example.controller;

import example.model.MerchantUser;
import example.model.Payment;
import example.model.Transaction;
import example.model.User;
import example.service.HistoryService;
import example.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.ExecutionException;

@RestController
@CrossOrigin
public class HistoryController {

    @Autowired
    HistoryService historyService;

    @Autowired
    TransactionService transactionService;

    @PostMapping("/history")
    List<Payment> getHistory(@RequestBody User user) throws ExecutionException, InterruptedException {
        return historyService.getPayments(user);
    }

    @PostMapping("/merchantHistory")
    List<Transaction> getMerchantHistory (@RequestBody MerchantUser merchant) throws ExecutionException, InterruptedException {
        System.out.println("Merchant history requested");
        return transactionService.getMerchantTransactions(merchant.getEmail());
    }

    @GetMapping("/welcome")
    String welcomepage(Model model){
        model.addAttribute("welcomepage","hello");
        return "welcome";
    }
}
