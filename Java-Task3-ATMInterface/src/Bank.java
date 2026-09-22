import java.util.HashMap;
import java.util.Map;

/**
 * Bank — holds all accounts and handles cross-account operations like transfers.
 */
public class Bank {

    private final Map<String, Account> accounts;

    public Bank() {
        this.accounts = new HashMap<>();
    }

    public void addAccount(Account account) {
        accounts.put(account.getAccountId(), account);
    }

    public Account getAccount(String accountId) {
        return accounts.get(accountId);
    }

    public boolean accountExists(String accountId) {
        return accounts.containsKey(accountId);
    }

    /**
     * Transfers money from one account to another.
     * Returns true on success, false if insufficient funds or invalid recipient.
     */
    public boolean transfer(Account from, String toAccountId, double amount) {
        Account to = accounts.get(toAccountId);
        if (to == null) return false;
        if (!from.debit(amount)) return false;

        to.credit(amount);

        from.addTransaction(new Transaction(Transaction.Type.TRANSFER_OUT, amount));
        to.addTransaction(new Transaction(Transaction.Type.TRANSFER_IN, amount));
        return true;
    }
}
