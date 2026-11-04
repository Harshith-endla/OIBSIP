import java.util.Scanner;

public class AtmIndex {

    private final Scanner scanner;

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        AtmIndex atmIndex = new AtmIndex(scanner);

        try {
            atmIndex.displayMenu();
        } finally {
            scanner.close();
        }
    }

    public AtmIndex(Scanner scanner) {
        this.scanner = scanner;
    }

    public void displayMenu() {

        boolean running = true;

        while (running) {

            System.out.println("\n================================");
            System.out.println("          ATM SERVICES");
            System.out.println("================================");
            System.out.println("1. Withdraw");
            System.out.println("2. Deposit");
            System.out.println("3. Balance");
            System.out.println("4. Enquire");
            System.out.println("0. Exit");
            System.out.println("================================");

            System.out.print("Enter your choice: ");
            String choice = scanner.nextLine().trim();

            switch (choice) {

                case "1":
                    System.out.println("\nOpening Withdraw page...");
                    System.out.println("Withdraw page will be implemented in the next phase.");
                    break;

                case "2":
                    System.out.println("\nOpening Deposit page...");
                    System.out.println("Deposit page will be implemented in the next phase.");
                    break;

                case "3":
                    System.out.println("\nOpening Balance page...");
                    System.out.println("Balance page will be implemented in the next phase.");
                    break;

                case "4":
                    System.out.println("\nOpening Enquire page...");
                    System.out.println("Enquire page will be implemented in its phase.");
                    break;

                case "0":
                    System.out.println("\nThank you for using our ATM service.");
                    running = false;
                    break;

                default:
                    System.out.println("\nInvalid option. Please select 0, 1, 2, 3 or 4.");
            }
        }
    }
}
