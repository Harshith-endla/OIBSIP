
import java.io.Console;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {

        Console console = System.console();

        if (console == null) {
            System.out.println("Console input is unavailable.");
            System.out.println("Please run this program using Command Prompt or PowerShell.");
            return;
        }

        Scanner scanner = new Scanner(System.in);

        UserRepository userRepository = new UserRepository();

        AuthenticationService authenticationService =
                new AuthenticationService(userRepository);

        String authenticatedUser = authenticationService.login(console);

        if (authenticatedUser == null) {
            scanner.close();
            return;
        }

        console.readLine("\nPress Enter to continue to the ATM interface...");

        System.out.println("\nOpening ATM interface...");

        Atm atm = new Atm(scanner, authenticatedUser);

        atm.displayMenu();

        scanner.close();
    }
}
