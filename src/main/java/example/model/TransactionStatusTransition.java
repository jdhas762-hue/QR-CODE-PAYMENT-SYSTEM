package example.model;

import java.time.LocalDateTime;

public class TransactionStatusTransition {
    private TransactionStatus status;
    private LocalDateTime timestamp;

    public TransactionStatusTransition() {
    }

    public TransactionStatusTransition(TransactionStatus status) {
        this.status = status;
        this.timestamp = LocalDateTime.now();
    }

    public TransactionStatus getStatus() {
        return status;
    }

    public void setStatus(TransactionStatus status) {
        this.status = status;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}
