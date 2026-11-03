import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class Account {

    private final String accountId;
    private final String userId;
    private final String accountHolderName;
    private final String pin;

    private BigDecimal balance;

    private final List<Transaction> transactionHistory;

    public static void main(String[] args) {
        Account account = new Account("ACC1001", "USER1001", "Kannaya", "1234",
            new BigDecimal("1000.00"));
        Account account2 = new Account("ACC1002", "USER1002", "Harshith", "2345",
            new BigDecimal("2450.75"));
        Account account3 = new Account("ACC1003", "USER1003", "Varma", "3456",
            new BigDecimal("3780.50"));
        System.out.println("Balance before deposit: INR " + account.getBalance());
        account.deposit(new BigDecimal("1000.00"));
        System.out.println("Deposited: INR 1000.00");
        System.out.println("Balance after deposit: INR " + account.getBalance());

        BigDecimal transferAmount = new BigDecimal("500.00");
        account.transferTo(account2, transferAmount);
        System.out.println("Transferred INR " + transferAmount + " from "
                + account.getAccountHolderName() + " to " + account2.getAccountHolderName());
        System.out.println(account.getAccountHolderName() + "'s balance: INR "
                + account.getBalance());
        System.out.println(account2.getAccountHolderName() + "'s balance: INR "
                + account2.getBalance());
    }

    public Account(
            String accountId,
            String userId,
            String accountHolderName,
            String pin,
            BigDecimal initialBalance) {

        if (accountId == null || accountId.isBlank()
                || userId == null || userId.isBlank()
                || accountHolderName == null || accountHolderName.isBlank()) {
            throw new IllegalArgumentException(
                    "Account details cannot be empty.");
        }

        if (pin == null || !pin.matches("\\d{4}")) {
            throw new IllegalArgumentException(
                    "PIN must contain exactly 4 digits.");
        }

        if (initialBalance == null
                || initialBalance.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException(
                    "Initial balance cannot be negative.");
        }

        this.accountId = accountId;
        this.userId = userId;
        this.accountHolderName = accountHolderName;
        this.pin = pin;
        this.balance = initialBalance;

        this.transactionHistory = new ArrayList<>();
    }

    public String getAccountId() {
        return accountId;
    }

    public String getUserId() {
        return userId;
    }

    public String getAccountHolderName() {
        return accountHolderName;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    boolean verifyPin(String enteredPin) {
        return pin.equals(enteredPin);
    }

    void deposit(BigDecimal amount) {
        validateAmount(amount);
        balance = balance.add(amount);
    }

    void withdraw(BigDecimal amount) {
        validateAmount(amount);

        if (amount.compareTo(balance) > 0) {
            throw new IllegalArgumentException("Insufficient balance.");
        }

        balance = balance.subtract(amount);
    }

    void transferTo(Account recipient, BigDecimal amount) {
        if (recipient == null) {
            throw new IllegalArgumentException("Recipient account cannot be null.");
        }
        if (recipient == this) {
            throw new IllegalArgumentException("Cannot transfer to the same account.");
        }

        validateAmount(amount);
        if (amount.compareTo(balance) > 0) {
            throw new IllegalArgumentException("Insufficient balance.");
        }

        balance = balance.subtract(amount);
        recipient.balance = recipient.balance.add(amount);
    }

    void addTransaction(Transaction transaction) {
        if (transaction == null) {
            throw new IllegalArgumentException(
                    "Transaction cannot be null.");
        }

        transactionHistory.add(transaction);
    }

    public List<Transaction> getTransactionHistory() {
        return List.copyOf(transactionHistory);
    }

    private void validateAmount(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException(
                    "Amount must be greater than zero.");
        }
    }
}