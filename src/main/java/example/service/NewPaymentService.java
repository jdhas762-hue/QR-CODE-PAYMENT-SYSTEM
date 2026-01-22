package example.service;

import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.cloud.FirestoreClient;
import example.model.MerchantUser;
import example.model.Payment;
import example.model.User;
import org.springframework.stereotype.Service;

@Service
public class NewPaymentService {
    public String addNewPayment(User user, MerchantUser merchantUser, Payment payment) {
        try {
            Firestore db = FirestoreClient.getFirestore();

            // Example: store in Payments -> userId -> payments subcollection
            DocumentReference userDoc = db.collection("Payments").document(user.getId());

            // Add the payment object to a subcollection
            userDoc.collection("payments").add(payment);

            // Optionally, also store in MerchantPayments
            DocumentReference merchantDoc = db.collection("MerchantPayments").document(merchantUser.getEmail());
            merchantDoc.collection("payments").add(payment);

            return "OK";
        } catch (Exception e) {
            e.printStackTrace();
            return "ERROR";
        }
    }

}
