import java.io.Console;
import java.util.Arrays;

public class AuthenticationService {

    private static final int MAX_ATTEMPTS = 3;

    private final UserRepository userRepository;

    public AuthenticationService() {
        this.userRepository = new UserRepository();
    }

    public String login(Console console) {

        if (console == null) {
            System.out.println("Console input is unavailable.");
            System.out.println(
                    "Please run this program using Command Prompt or PowerShell.");
            return null;
        }

        for (int attempt = 1; attempt <= MAX_ATTEMPTS; attempt++) {

            System.out.println("\n================================");
            System.out.println("          ATM LOGIN");
            System.out.println("================================");

            String userId = console.readLine("Enter User ID: ");

            if (userId == null) {
                return null;
            }

            char[] enteredPin = console.readPassword("Enter 4-digit PIN: ");

            if (enteredPin == null) {
                return null;
            }

            String pin = new String(enteredPin);
            Arrays.fill(enteredPin, ' ');

            if (userRepository.credentialsMatch(userId, pin)) {

                System.out.println("\nLogin successful!");
                System.out.println("Welcome, " + userId.trim().toUpperCase() + ".");

                return userId.trim().toUpperCase();
            }

            System.out.println(
                    "\nInvalid credentials. Please check your User ID and PIN.");
            System.out.println("Attempts remaining: "
                    + (MAX_ATTEMPTS - attempt));
        }

        System.out.println("\nMaximum login attempts reached.");
        System.out.println("Login terminated.");

        return null;
    }
}
