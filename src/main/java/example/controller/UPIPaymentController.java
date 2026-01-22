package example.controller;

import com.google.zxing.WriterException;
import example.model.UPIPaymentRequest;
import example.model.Transaction;
import example.service.QRCodeGenerator;
import example.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutionException;

@RestController
@CrossOrigin
public class UPIPaymentController {

    @Autowired
    QRCodeGenerator qrCodeGenerator;

    @Autowired
    TransactionService transactionService;

    @PostMapping(value = "/generateUPIQR")
    public byte[] generateUPIQR(@RequestBody UPIPaymentRequest upiPaymentRequest) throws IOException, WriterException, ExecutionException, InterruptedException {
        System.out.println("UPI QR generation initiated");

        // Format amount to 2 decimal places
        String amount = String.format("%.2f", Double.parseDouble(upiPaymentRequest.getAmount()));

        // URL-encode merchant name and transaction note
        String merchantName = encodeValue(upiPaymentRequest.getMerchantName());
        String transactionNote = encodeValue(upiPaymentRequest.getTransactionNote());

        // Enforce INR currency
        String currency = "INR";

        // Create UPI payment string
        String upiPaymentString = String.format("upi://pay?pa=%s&pn=%s&am=%s&cu=%s&tn=%s",
                upiPaymentRequest.getUpiId(),
                merchantName,
                amount,
                currency,
                transactionNote
        );

        System.out.println("UPI Payment String: " + upiPaymentString);

        // Create and save transaction record
        Transaction transaction = new Transaction();
        transaction.setMerchantEmail(upiPaymentRequest.getMerchantEmail());
        transaction.setMerchantName(upiPaymentRequest.getMerchantName());
        transaction.setUpiId(upiPaymentRequest.getUpiId());
        transaction.setAmount(Double.parseDouble(upiPaymentRequest.getAmount()));
        transaction.setCurrency(currency);
        transaction.setTransactionNote(upiPaymentRequest.getTransactionNote());
        transaction.setQrCodeData(upiPaymentString);

        transactionService.saveTransaction(transaction);

        return qrCodeGenerator.getQRCodeImage(upiPaymentString);
    }

    private String encodeValue(String value) {
        try {
            return URLEncoder.encode(value, StandardCharsets.UTF_8.toString());
        } catch (UnsupportedEncodingException e) {
            // This should never happen with UTF-8
            throw new RuntimeException(e);
        }
    }
}
