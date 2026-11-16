import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.math.BigDecimal;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.Map;

public class Bank implements Serializable {

    private static final long serialVersionUID = 1L;

    private static final Path DATA_FILE = Path.of("bank-data.ser");
    private static final Path TEMP_FILE = Path.of("bank-data.tmp");

    private final HashMap<String, Account> accounts = new HashMap<>();

    public Bank() {

        accounts.put("USER001",
                new Account("USER001", new BigDecimal("50000")));

        accounts.put("USER002",
                new Account("USER002", new BigDecimal("50000")));

        accounts.put("USER003",
                new Account("USER003", new BigDecimal("50000")));
    }

    // Loads saved bank data or creates and saves the initial demo accounts.
    public static Bank loadBank()
            throws IOException, ClassNotFoundException {

        if (Files.notExists(DATA_FILE)) {
            Bank newBank = new Bank();
            newBank.saveBank();
            return newBank;
        }

        if (!Files.isRegularFile(DATA_FILE)) {
            throw new IOException(
                    "The bank data path exists but is not a regular file: "
                            + DATA_FILE.toAbsolutePath());
        }

        try (ObjectInputStream input = new ObjectInputStream(
                new FileInputStream(DATA_FILE.toFile()))) {

            Object savedData = input.readObject();

            if (!(savedData instanceof Bank)) {
                throw new IOException("Invalid bank data file.");
            }

            Bank loadedBank = (Bank) savedData;
            loadedBank.validateAccounts();
            return loadedBank;
        }
    }

    // Saves all accounts, balances, and transaction histories.
    public void saveBank() throws IOException {

        try (ObjectOutputStream output = new ObjectOutputStream(
                new FileOutputStream(TEMP_FILE.toFile()))) {

            output.writeObject(this);
            output.flush();
        }

        try {
            Files.move(
                    TEMP_FILE,
                    DATA_FILE,
                    StandardCopyOption.REPLACE_EXISTING,
                    StandardCopyOption.ATOMIC_MOVE);

        } catch (AtomicMoveNotSupportedException e) {

            Files.move(
                    TEMP_FILE,
                    DATA_FILE,
                    StandardCopyOption.REPLACE_EXISTING);
        }
    }

    // Rejects structurally invalid data instead of starting with bad accounts.
    private void validateAccounts() throws IOException {

        if (accounts == null || accounts.isEmpty()) {
            throw new IOException("Saved bank data contains no accounts.");
        }

        for (Map.Entry<String, Account> entry : accounts.entrySet()) {

            Account savedAccount = entry.getValue();

            if (entry.getKey() == null
                    || savedAccount == null
                    || !entry.getKey().equals(savedAccount.getAccountId())
                    || savedAccount.getBalance() == null
                    || savedAccount.getBalance().compareTo(BigDecimal.ZERO) < 0
                    || savedAccount.getTransactionHistory() == null) {

                throw new IOException(
                        "Saved bank data contains an invalid account record.");
            }
        }
    }

    public Account findAccount(String accountId) {

        if (accountId == null) {
            return null;
        }

        return accounts.get(accountId.trim().toUpperCase());
    }
}
