import java.util.Scanner;

/**
 * Main — entry point. Sets up a Bank with sample accounts and starts the ATM session.
 *
 * Sample login for testing:
 *   Account ID: 1001   PIN: 1234
 *   Account ID: 1002   PIN: 5678
 */
public class Main {
    public static void main(String[] args) {
        Bank bank = new Bank();
        bank.addAccount(new Account("1001", "1234", 50000.0));
        bank.addAccount(new Account("1002", "5678", 20000.0));

        Scanner scanner = new Scanner(System.in);
        ATM atm = new ATM(bank, scanner);

        System.out.println("======================================");
        System.out.println(" WELCOME TO OIBSIP SMART ATM INTERFACE ");
        System.out.println("======================================\n");

        Account loggedInAccount = atm.login();
        if (loggedInAccount != null) {
            atm.runMenu(loggedInAccount);
        }

        scanner.close();
    }
}
