import java.io.Console;
import java.io.IOException;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);

        // Load saved bank data and create the initial data file when needed.
        Bank bank;

        try {
            bank = Bank.loadBank();

        } catch (IOException | ClassNotFoundException e) {

            System.out.println("\nUnable to load bank data.");
            System.out.println(
                    "The ATM cannot start because account data could not be read safely.");
            System.out.println("Details: " + e.getMessage());

            scanner.close();
            return;
        }

        // Create the authentication service.
        AuthenticationService authenticationService =
                new AuthenticationService();

        // Get the system console for secure PIN input.
        Console console = System.console();

        // Authenticate the user.
        String authenticatedUser = authenticationService.login(console);

        if (authenticatedUser == null) {

            System.out.println("\nAccess denied. Exiting ATM.");
            scanner.close();
            return;
        }

        // Find the authenticated user's account.
        Account account = bank.findAccount(authenticatedUser);

        if (account == null) {

            System.out.println("\nAccount not found. Contact the bank.");
            scanner.close();
            return;
        }

        // Start the ATM application.
        Atm atm = new Atm(scanner, account, bank);
        atm.displayMenu();

        scanner.close();
    }
}
