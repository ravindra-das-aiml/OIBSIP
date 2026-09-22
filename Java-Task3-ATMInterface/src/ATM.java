import java.time.LocalDateTime;
import java.util.Scanner;

/**
 * ATM — the user-facing menu and transaction flow.
 * Every withdrawal is passed through FraudDetector before being finalised.
 */
public class ATM {

    private static final int MAX_LOGIN_ATTEMPTS = 3;

    private final Bank bank;
    private final Scanner scanner;

    public ATM(Bank bank, Scanner scanner) {
        this.bank = bank;
        this.scanner = scanner;
    }

    /** Handles login with a 3-attempt limit. Returns the authenticated Account, or null. */
    public Account login() {
        for (int attempt = 1; attempt <= MAX_LOGIN_ATTEMPTS; attempt++) {
            System.out.print("Enter Account ID: ");
            String id = scanner.nextLine().trim();
            System.out.print("Enter PIN: ");
            String pin = scanner.nextLine().trim();

            Account acc = bank.getAccount(id);
            if (acc != null && acc.validatePin(pin)) {
                System.out.println("✅ Login successful. Welcome, " + id + "!\n");
                return acc;
            }
            System.out.println("❌ Invalid credentials. Attempts left: " + (MAX_LOGIN_ATTEMPTS - attempt) + "\n");
        }
        System.out.println("🚫 Too many failed attempts. Card retained. Exiting.");
        return null;
    }

    public void runMenu(Account account) {
        boolean running = true;
        while (running) {
            printMenu();
            String choice = scanner.nextLine().trim();
            switch (choice) {
                case "1" -> showHistory(account);
                case "2" -> withdraw(account);
                case "3" -> deposit(account);
                case "4" -> transfer(account);
                case "5" -> {
                    System.out.println("👋 Thank you for using our ATM. Goodbye!");
                    running = false;
                }
                default -> System.out.println("Invalid option, try again.\n");
            }
        }
    }

    private void printMenu() {
        System.out.println("========= MAIN MENU =========");
        System.out.println("1. Transaction History");
        System.out.println("2. Withdraw");
        System.out.println("3. Deposit");
        System.out.println("4. Transfer");
        System.out.println("5. Quit");
        System.out.print("Choose an option: ");
    }

    private void showHistory(Account account) {
        System.out.println("\n--- Transaction History ---");
        if (account.getTransactionHistory().isEmpty()) {
            System.out.println("No transactions yet.");
        } else {
            account.getTransactionHistory().forEach(t -> System.out.println("  " + t));
        }
        System.out.println();
    }

    private void withdraw(Account account) {
        System.out.print("Enter amount to withdraw: ₹");
        double amount;
        try {
            amount = Double.parseDouble(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("Invalid amount.\n");
            return;
        }
        if (amount <= 0) {
            System.out.println("Amount must be positive.\n");
            return;
        }

        LocalDateTime now = LocalDateTime.now();

        // --- Fraud check runs BEFORE the money moves ---
        String flagReason = FraudDetector.evaluateWithdrawal(account, amount, now);

        if (!account.debit(amount)) {
            System.out.println("❌ Insufficient Funds. Current balance: ₹" + account.getBalance() + "\n");
            return;
        }

        Transaction txn = new Transaction(Transaction.Type.WITHDRAW, amount);
        if (flagReason != null) {
            txn.flag(flagReason);
            System.out.println("⚠️  This transaction has been flagged for review: " + flagReason);
        }
        account.addTransaction(txn);

        System.out.printf("✅ Withdrawal successful. New balance: ₹%.2f%n%n", account.getBalance());
    }

    private void deposit(Account account) {
        System.out.print("Enter amount to deposit: ₹");
        double amount;
        try {
            amount = Double.parseDouble(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("Invalid amount.\n");
            return;
        }
        if (amount <= 0) {
            System.out.println("Amount must be positive.\n");
            return;
        }
        account.credit(amount);
        account.addTransaction(new Transaction(Transaction.Type.DEPOSIT, amount));
        System.out.printf("✅ Deposit successful. New balance: ₹%.2f%n%n", account.getBalance());
    }

    private void transfer(Account account) {
        System.out.print("Enter recipient Account ID: ");
        String toId = scanner.nextLine().trim();
        if (!bank.accountExists(toId)) {
            System.out.println("❌ Recipient account not found.\n");
            return;
        }
        System.out.print("Enter amount to transfer: ₹");
        double amount;
        try {
            amount = Double.parseDouble(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("Invalid amount.\n");
            return;
        }

        boolean success = bank.transfer(account, toId, amount);
        if (success) {
            System.out.printf("✅ Transfer successful. New balance: ₹%.2f%n%n", account.getBalance());
        } else {
            System.out.println("❌ Transfer failed (insufficient funds or invalid recipient).\n");
        }
    }
}
