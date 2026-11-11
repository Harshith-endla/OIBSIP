
import java.math.BigDecimal;
import java.util.Scanner;

public class Transfer {

    private static final BigDecimal MIN_AMOUNT = new BigDecimal("100");

    private static final BigDecimal MAX_AMOUNT = new BigDecimal("25000");

    private static final BigDecimal DENOMINATION = new BigDecimal("100");

    private final Scanner scanner;
    private final Account sender;
    private final Bank bank;

    public Transfer(Scanner scanner, Account sender, Bank bank) {

        if (scanner == null || sender == null || bank == null) {
            throw new IllegalArgumentException(
                    "Invalid transfer details.");
        }

        this.scanner = scanner;
        this.sender = sender;
        this.bank = bank;
    }

    public void startTransfer() {

        System.out.println("\n================================");
        System.out.println("         MONEY TRANSFER");
        System.out.println("================================");

        System.out.println("Sender Account: "
                + sender.getAccountId());

        System.out.println("Available Balance: Rs "
                + sender.getBalance());

        System.out.print("\nEnter recipient User ID: ");

        if (!scanner.hasNextLine()) {
            return;
        }

        String recipientId = scanner.nextLine()
                .trim().toUpperCase();

        Account recipient = bank.findAccount(recipientId);

        if (recipient == null) {
            System.out.println(
                    "\nRecipient account not found.");
            return;
        }

        if (sender.getAccountId().equals(recipient.getAccountId())) {
            System.out.println(
                    "\nYou cannot transfer money to your own account.");
            return;
        }

        BigDecimal amount = getTransferAmount();

        if (amount == null) {
            return;
        }

        if (amount.compareTo(sender.getBalance()) > 0) {
            System.out.println("\nInsufficient Funds");
            System.out.println("Available Balance: Rs "
                    + sender.getBalance());
            return;
        }

        System.out.println("\n================================");
        System.out.println("       TRANSFER DETAILS");
        System.out.println("================================");
        System.out.println("From Account : "
                + sender.getAccountId());
        System.out.println("To Account   : "
                + recipient.getAccountId());
        System.out.println("Amount       : Rs " + amount);
        System.out.println("================================");

        System.out.print(
                "\nConfirm transfer? (Y/N): ");

        if (!scanner.hasNextLine()) {
            return;
        }

        String confirmation = scanner.nextLine().trim();

        if (confirmation.equalsIgnoreCase("Y")) {

            // Deduct money from sender.
            sender.withdrawFunds(amount);

            // Add money to recipient.
            recipient.depositFunds(amount);

            // Record sender's transaction.
            Transaction senderTransaction = new Transaction(
                    Transaction.TransactionType.TRANSFER,
                    amount,
                    "Transfer to " + recipient.getAccountId());

            sender.getTransactionHistory()
                    .addTransaction(senderTransaction);

            // Record recipient's transaction.
            Transaction recipientTransaction = new Transaction(
                    Transaction.TransactionType.TRANSFER,
                    amount,
                    "Transfer received from "
                            + sender.getAccountId());

            recipient.getTransactionHistory()
                    .addTransaction(recipientTransaction);

            System.out.println("\nTransfer successful!");
            System.out.println("Transferred Amount: Rs " + amount);
            System.out.println("Remaining Balance: Rs "
                    + sender.getBalance());

        } else if (confirmation.equalsIgnoreCase("N")) {

            System.out.println("\nTransfer cancelled.");

        } else {

            System.out.println(
                    "\nInvalid confirmation. Transfer cancelled.");
        }
    }

    private BigDecimal getTransferAmount() {

        while (true) {

            System.out.print(
                    "\nEnter transfer amount (Rs): ");

            if (!scanner.hasNextLine()) {
                return null;
            }

            String input = scanner.nextLine().trim();

            BigDecimal amount;

            try {
                amount = new BigDecimal(input);

            } catch (NumberFormatException e) {

                System.out.println(
                        "Invalid amount. Enter a numeric value.");
                continue;
            }

            if (amount.compareTo(BigDecimal.ZERO) <= 0) {

                System.out.println(
                        "Amount must be greater than zero.");

            } else if (amount.compareTo(MIN_AMOUNT) < 0) {

                System.out.println(
                        "Minimum transfer amount is Rs 100.");

            } else if (amount.compareTo(MAX_AMOUNT) > 0) {

                System.out.println(
                        "Maximum transfer amount is Rs 25,000.");

            } else if (amount.remainder(DENOMINATION)
                    .compareTo(BigDecimal.ZERO) != 0) {

                System.out.println(
                        "Transfer amount must be in multiples of Rs 100.");

            } else {

                return amount;
            }
        }
    }
}
