import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

/**
 * FraudDetector — rule-based anomaly detection layer.
 *
 * This is the "AI/ML enhancement" on top of the standard OIBSIP ATM task.
 * It is intentionally rule-based (not a black-box model) so every decision
 * is explainable — a common requirement in real fraud/risk systems, and a
 * good talking point in interviews: precision/recall trade-offs, false
 * positive cost, explainability, etc.
 *
 * Three independent rules are checked on every withdrawal:
 *   1. Velocity check      — too many withdrawals in a short time window
 *   2. Amount deviation    — withdrawal far above the account's historical average
 *   3. Off-hours check     — transaction happening late at night
 *
 * Each rule can fire independently; reasons are combined into one flag.
 */
public class FraudDetector {

    // ---- Tunable thresholds (documented so you can justify them in README/interview) ----
    private static final int VELOCITY_WINDOW_MINUTES = 5;
    private static final int VELOCITY_MAX_TXNS = 3;       // >=3 withdrawals in window => suspicious
    private static final double AMOUNT_DEVIATION_MULTIPLIER = 3.0; // 3x avg => suspicious
    private static final int OFF_HOURS_START = 0;  // 12 AM
    private static final int OFF_HOURS_END = 5;    // 5 AM

    /**
     * Runs all fraud rules against a proposed withdrawal and returns a
     * flag reason string, or null if the transaction looks normal.
     */
    public static String evaluateWithdrawal(Account account, double amount, LocalDateTime now) {
        StringBuilder reasons = new StringBuilder();

        if (isHighVelocity(account, now)) {
            reasons.append("High-frequency withdrawals (")
                   .append(VELOCITY_MAX_TXNS)
                   .append("+ in ")
                   .append(VELOCITY_WINDOW_MINUTES)
                   .append(" min); ");
        }

        if (isAmountDeviation(account, amount)) {
            reasons.append(String.format("Amount ₹%.2f is over %.1fx account average; ",
                    amount, AMOUNT_DEVIATION_MULTIPLIER));
        }

        if (isOffHours(now)) {
            reasons.append("Off-hours transaction (")
                   .append(OFF_HOURS_START).append(":00–")
                   .append(OFF_HOURS_END).append(":00); ");
        }

        return reasons.length() == 0 ? null : reasons.toString().trim();
    }

    /** Rule 1: Velocity — counts withdrawals in the last N minutes. */
    private static boolean isHighVelocity(Account account, LocalDateTime now) {
        List<Transaction> history = account.getTransactionHistory();
        long recentWithdrawals = history.stream()
                .filter(t -> t.getType() == Transaction.Type.WITHDRAW)
                .filter(t -> ChronoUnit.MINUTES.between(t.getTimestamp(), now) <= VELOCITY_WINDOW_MINUTES)
                .count();
        return recentWithdrawals >= VELOCITY_MAX_TXNS;
    }

    /** Rule 2: Amount deviation — compares against the account's historical average. */
    private static boolean isAmountDeviation(Account account, double amount) {
        double avg = account.getAverageWithdrawal();
        if (avg <= 0) return false; // not enough history to judge
        return amount > avg * AMOUNT_DEVIATION_MULTIPLIER;
    }

    /** Rule 3: Off-hours — flags transactions between midnight and 5 AM. */
    private static boolean isOffHours(LocalDateTime now) {
        int hour = now.getHour();
        return hour >= OFF_HOURS_START && hour < OFF_HOURS_END;
    }
}
