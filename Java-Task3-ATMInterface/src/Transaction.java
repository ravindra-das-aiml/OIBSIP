import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Represents a single ATM transaction.
 * Carries a "flagged" status set by FraudDetector for suspicious activity.
 */
public class Transaction {

    public enum Type {
        WITHDRAW, DEPOSIT, TRANSFER_OUT, TRANSFER_IN
    }

    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final Type type;
    private final double amount;
    private final LocalDateTime timestamp;
    private String flagReason; // null if not flagged

    public Transaction(Type type, double amount) {
        this.type = type;
        this.amount = amount;
        this.timestamp = LocalDateTime.now();
        this.flagReason = null;
    }

    public Type getType() {
        return type;
    }

    public double getAmount() {
        return amount;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public boolean isFlagged() {
        return flagReason != null;
    }

    public void flag(String reason) {
        this.flagReason = reason;
    }

    public String getFlagReason() {
        return flagReason;
    }

    @Override
    public String toString() {
        String base = String.format("[%s] %-13s ₹%.2f",
                timestamp.format(FORMATTER), type, amount);
        if (isFlagged()) {
            base += "  ⚠️ FLAGGED: " + flagReason;
        }
        return base;
    }
}
