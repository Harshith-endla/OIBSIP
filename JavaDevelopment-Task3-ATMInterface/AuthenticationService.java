
import java.io.Console;

public class AuthenticationService {

    private static final int MAX_ATTEMPTS = 3;

    private final UserRepository userRepository;

    public AuthenticationService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public String login(Console console) {

        if (console == null) {
            System.out.println("Console input is unavailable.");
            System.out.println("Please run this program using Command Prompt or PowerShell.");
            return null;
        }

        for (int attempt = 1; attempt <= MAX_ATTEMPTS; attempt++) {

            System.out.println("\n================================");
            System.out.println("          ATM LOGIN");
            System.out.println("================================");

            String userId = console.readLine("Enter User ID: ").trim();

            char[] enteredPin = console.readPassword("Enter 4-digit PIN: ");

            String pin = new String(enteredPin);
            java.util.Arrays.fill(enteredPin, ' ');

            if (userRepository.credentialsMatch(userId, pin)) {

                System.out.println("\nLogin successful!");
                System.out.println("Welcome, " + userId.toUpperCase() + ".");

                return userId.toUpperCase();
            }

            System.out.println("\nInvalid credentials. Please check your User ID and PIN.");

            System.out.println("Attempts remaining: "
                    + (MAX_ATTEMPTS - attempt));
        }

        System.out.println("\nMaximum login attempts reached.");
        System.out.println("Login terminated.");

        return null;
    }
}
