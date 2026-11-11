
import java.util.Scanner;

public class Atm {

    private final Scanner scanner;
    private final Account account;
    private final Bank bank;

    public Atm(Scanner scanner, Account account, Bank bank) {

        this.scanner = scanner;
        this.account = account;
        this.bank = bank;
    }

    public void displayMenu() {

        boolean running = true;

        while (running) {

            System.out.println("\n================================");
            System.out.println("           ATM MENU");
            System.out.println("================================");
            System.out.println("Welcome, " + account.getAccountId());
            System.out.println("1. Transaction History");
            System.out.println("2. Withdraw");
            System.out.println("3. Deposit");
            System.out.println("4. Transfer");
            System.out.println("5. Quit");
            System.out.println("================================");

            System.out.print("Enter your choice: ");

            if (!scanner.hasNextLine()) {
                break;
            }

            String choice = scanner.nextLine().trim();

            switch (choice) {

                case "1":

                    System.out.println("\n================================");
                    System.out.println("       TRANSACTION HISTORY");
                    System.out.println("================================");

                    account.getTransactionHistory().displayHistory();

                    break;

                case "2":

                    Withdraw withdraw = new Withdraw(
                            scanner,
                            account.getBalance(),
                            account.getTransactionHistory());

                    account.setBalance(
                            withdraw.startWithdrawal());

                    break;

                case "3":

                    Deposit deposit = new Deposit(
                            scanner,
                            account.getBalance(),
                            account.getTransactionHistory());

                    account.setBalance(
                            deposit.startDeposit());

                    break;

                case "4":

                    Transfer transfer = new Transfer(
                            scanner,
                            account,
                            bank);

                    transfer.startTransfer();

                    break;

                case "5":

                    System.out.println("\nThank you for using our ATM.");
                    System.out.println("Have a nice day!");

                    running = false;

                    break;

                default:

                    System.out.println(
                            "\nInvalid choice. Please select 1 to 5.");
            }
        }
    }
}
