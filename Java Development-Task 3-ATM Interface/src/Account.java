
import java.math.BigDecimal;

public class Account {

    private final String accountId;
    private final String userId;
    private final String accountHolderName;
    private final String pin;

    private BigDecimal balance;

    public static void main(String[] args) {
        Account[] accounts = {
                new Account("ACC1001", "USER1001", "Sample Account Holder", "1234",
                        new BigDecimal("1000.00")),
                new Account("ACC1002", "USER1002", "Harshith", "2345",
                        new BigDecimal("2450.75")),
                new Account("ACC1003", "USER1003", "Varma", "3456",
                        new BigDecimal("3780.50"))
        };

        for (Account account : accounts) {
            System.out.println("Account ID: " + account.getAccountId());
            System.out.println("Account holder: " + account.getAccountHolderName());
            System.out.println("Balance: $" + account.getBalance());
            System.out.println();
        }
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
            throw new IllegalArgumentException("Account details cannot be empty.");
        }

        if (pin == null || !pin.matches("\\d{4}")) {
            throw new IllegalArgumentException("PIN must contain exactly 4 digits.");
        }

        if (initialBalance == null || initialBalance.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Initial balance cannot be negative.");
        }

        this.accountId = accountId;
        this.userId = userId;
        this.accountHolderName = accountHolderName;
        this.pin = pin;
        this.balance = initialBalance;
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

    private void validateAmount(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount must be greater than zero.");
        }
    }
}
