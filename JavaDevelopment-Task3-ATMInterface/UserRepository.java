import java.util.HashMap;
import java.util.Map;

public class UserRepository {

    // Stores registered User IDs and their PINs.
    private final Map<String, String> registeredUsers = new HashMap<>();

    // Constructor initializes demo account credentials.
    public UserRepository() {
        registeredUsers.put("USER001", "1234");
        registeredUsers.put("USER002", "5678");
        registeredUsers.put("USER003", "9012");
    }

    // Checks whether the User ID and PIN combination is valid.
    public boolean credentialsMatch(String userId, String pin) {

        if (userId == null || pin == null) {
            return false;
        }

        String normalizedUserId = userId.trim().toUpperCase();

        String registeredPin = registeredUsers.get(normalizedUserId);

        return registeredPin != null && registeredPin.equals(pin);
    }
}
