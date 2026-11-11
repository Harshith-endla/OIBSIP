
import java.util.Scanner;
import java.io.Console;

public class Main {

    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);

        // Create the user repository.
        UserRepository userRepository = new UserRepository();

        // Create the authentication service.
        AuthenticationService authenticationService = new AuthenticationService(userRepository);

        // Create the bank.
        Bank bank = new Bank();

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
