
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

public class Bank {

    private final Map<String, Account> accounts = new HashMap<>();

    public Bank() {

        accounts.put("USER001",
                new Account("USER001", new BigDecimal("50000")));

        accounts.put("USER002",
                new Account("USER002", new BigDecimal("50000")));

        accounts.put("USER003",
                new Account("USER003", new BigDecimal("50000")));
    }

    public Account findAccount(String accountId) {

        if (accountId == null) {
            return null;
        }

        return accounts.get(accountId.trim().toUpperCase());
    }

    public boolean accountExists(String accountId) {
        return findAccount(accountId) != null;
    }
}
