import java.io.Console;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class Login {

    private static final int MAX_ATTEMPTS = 3;

    // Demo account data.
    private static final Map<String, String> registeredUsers = new HashMap<>();

    static {
        registeredUsers.put("USER001", "1234");
        registeredUsers.put("USER002", "5678");
        registeredUsers.put("USER003", "9012");
    }

    public static void main(String[] args) {

        Console console = System.console();

        if (console == null) {
            System.out.println("Console input is unavailable.");
            System.out.println("Please run this program using Command Prompt or PowerShell.");
            return;
        }

        System.out.println("================================");
        System.out.println("          ATM LOGIN");
        System.out.println("================================");

        String userId = authenticateUserId(console);

        if (userId == null) {
            System.out.println("Maximum User ID attempts reached.");
            System.out.println("Login terminated.");
            return;
        }

        boolean authenticated = authenticatePin(console, userId);

        if (!authenticated) {
            System.out.println("Maximum PIN attempts reached.");
            System.out.println("Login terminated.");
            return;
        }

        System.out.println("\nLogin successful!");
        System.out.println("Welcome, " + userId + ".");

        console.readLine("\nPress Enter to continue to the ATM interface...");

        System.out.println("\nOpening ATM interface...");

        Scanner scanner = new Scanner(System.in);
        Atm atm = new Atm(scanner, userId);
        atm.displayMenu();
    }

    private static String authenticateUserId(Console console) {

        for (int attempt = 1; attempt <= MAX_ATTEMPTS; attempt++) {

            String userId = console.readLine("\nEnter User ID: ").trim();

            if (!userId.matches("^[A-Za-z]{4}[0-9]{3}$")) {
                System.out.println("Invalid User ID format. Example: USER001");
            } else if (!registeredUsers.containsKey(userId.toUpperCase())) {
                System.out.println("User ID is not registered.");
            } else {
                System.out.println("User ID verified successfully.");
                return userId.toUpperCase();
            }

            System.out.println("Attempts remaining: " + (MAX_ATTEMPTS - attempt));
        }

        return null;
    }

    private static boolean authenticatePin(Console console, String userId) {

        String registeredPin = registeredUsers.get(userId);

        for (int attempt = 1; attempt <= MAX_ATTEMPTS; attempt++) {

            char[] enteredPin = console.readPassword("Enter 4-digit PIN: ");

            String pin = new String(enteredPin);
            java.util.Arrays.fill(enteredPin, ' ');

            if (!pin.matches("^[0-9]{4}$")) {
                System.out.println("Invalid PIN. Enter exactly 4 digits.");
            } else if (!pin.equals(registeredPin)) {
                System.out.println("Incorrect PIN.");
            } else {
                return true;
            }

            System.out.println("Attempts remaining: " + (MAX_ATTEMPTS - attempt));
        }

        return false;
    }
}
