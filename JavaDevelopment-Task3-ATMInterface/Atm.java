import java.math.BigDecimal;
import java.util.Scanner;

public class Atm {

    private final Scanner scanner;
    private final String userId;
    private BigDecimal balance = new BigDecimal("50000");

    public Atm(Scanner scanner, String userId) {
        this.scanner = scanner;
        this.userId = userId;
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
                    Withdraw withdraw = new Withdraw(scanner, balance);
                    balance = withdraw.startWithdrawal();

                    break;

                case "2":
                    System.out.println("\nOpening Deposit page...");
                    Deposit deposit = new Deposit(scanner, balance);
                    balance = deposit.startDeposit();

                    break;

                case "3":
                    System.out.println("\nOpening Balance page...");
                    Balance balancePage = new Balance(scanner, balance);
                    balancePage.displayBalance();

                    break;

                case "4":
                    System.out.println("\nOpening Enquire page....");
                    Enquire enquire = new Enquire(scanner, userId, balance);
                    enquire.displayEnquiry();
                    ;

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
