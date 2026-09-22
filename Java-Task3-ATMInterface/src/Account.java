import java.util.ArrayList;
import java.util.List;

/**
 * Represents a single bank account.
 * Stores balance, PIN, and the full transaction history for that account.
 */
public class Account {

    private final String accountId;
    private String pin;
    private double balance;
    private final List<Transaction> transactionHistory;

    public Account(String accountId, String pin, double initialBalance) {
        this.accountId = accountId;
        this.pin = pin;
        this.balance = initialBalance;
        this.transactionHistory = new ArrayList<>();
    }

    public String getAccountId() {
        return accountId;
    }

    public boolean validatePin(String enteredPin) {
        return this.pin.equals(enteredPin);
    }

    public double getBalance() {
        return balance;
    }

    public void credit(double amount) {
        this.balance += amount;
    }

    public boolean debit(double amount) {
        if (amount > balance) {
            return false; // insufficient funds
        }
        this.balance -= amount;
        return true;
    }

    public List<Transaction> getTransactionHistory() {
        return transactionHistory;
    }

    public void addTransaction(Transaction transaction) {
        transactionHistory.add(transaction);
    }

    /**
     * Returns the average withdrawal amount for this account so far.
     * Used by FraudDetector to spot unusually large withdrawals.
     */
    public double getAverageWithdrawal() {
        double total = 0;
        int count = 0;
        for (Transaction t : transactionHistory) {
            if (t.getType() == Transaction.Type.WITHDRAW) {
                total += t.getAmount();
                count++;
            }
        }
        return count == 0 ? 0 : total / count;
    }
}
