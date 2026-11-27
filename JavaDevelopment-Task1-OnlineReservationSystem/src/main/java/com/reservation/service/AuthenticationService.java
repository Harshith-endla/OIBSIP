
package com.reservation.service;

import com.reservation.model.User;
import com.reservation.repository.UserRepository;
import com.reservation.security.PasswordUtil;

import java.security.GeneralSecurityException;
import java.sql.SQLException;
import java.util.regex.Pattern;

public class AuthenticationService {

    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    private static final Pattern PHONE_PATTERN = Pattern.compile(
            "^(?:\\+91[ -]?)?[6-9]\\d{9}$");

    private final UserRepository userRepository;

    // Uses the default user repository.
    public AuthenticationService() {
        this.userRepository = new UserRepository();
    }

    // Allows a repository to be supplied, especially for testing.
    public AuthenticationService(UserRepository userRepository) {

        if (userRepository == null) {
            throw new IllegalArgumentException(
                    "User repository cannot be null.");
        }

        this.userRepository = userRepository;
    }

    // Registers a new user.
    // Public registration always creates a USER account.
    public User registerUser(
            String fullName,
            String email,
            String phone,
            char[] password)
            throws SQLException, GeneralSecurityException {

        // Validate required fields.
        if (fullName == null || fullName.isBlank()) {
            throw new IllegalArgumentException(
                    "Full name cannot be empty.");
        }

        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException(
                    "Email address cannot be empty.");
        }

        if (phone == null || phone.isBlank()) {
            throw new IllegalArgumentException(
                    "Phone number cannot be empty.");
        }

        if (password == null || password.length == 0) {
            throw new IllegalArgumentException(
                    "Password cannot be empty.");
        }

        // Remove unnecessary spaces from text fields.
        String cleanedName = fullName.trim();
        String cleanedEmail = email.trim();
        String cleanedPhone = phone.trim();

        // Validate email format.
        if (!isValidEmail(cleanedEmail)) {
            throw new IllegalArgumentException(
                    "Please enter a valid email address.");
        }

        // Validate Indian mobile number format.
        if (!isValidPhone(cleanedPhone)) {
            throw new IllegalArgumentException(
                    "Please enter a valid Indian mobile number.");
        }

        // Validate password complexity.
        if (!isStrongPassword(password)) {
            throw new IllegalArgumentException(
                    "Password must contain at least 8 characters, "
                            + "one uppercase letter, one lowercase letter, "
                            + "one digit, and one special character.");
        }

        // Register the user through the repository.
        // The repository assigns the USER role.
        return userRepository.registerUser(
                cleanedName,
                cleanedEmail,
                cleanedPhone,
                password);
    }

    // Authenticates a user using email and password.
    public User login(String email, char[] password)
            throws SQLException, GeneralSecurityException {

        if (email == null || email.isBlank()
                || password == null || password.length == 0) {
            return null;
        }

        String cleanedEmail = email.trim();

        // Reject invalid email formats before accessing the repository.
        if (!isValidEmail(cleanedEmail)) {
            return null;
        }

        User user = userRepository.findByEmail(cleanedEmail);

        if (user == null) {
            return null;
        }

        // Reject accounts with missing or unsupported roles.
        if (!"USER".equals(user.getRole())
                && !"ADMIN".equals(user.getRole())) {
            return null;
        }

        boolean passwordMatches = PasswordUtil.verifyPassword(
                password,
                user.getPasswordHash());

        if (passwordMatches) {
            return user;
        }

        return null;
    }

    // Validates the email address format.
    private boolean isValidEmail(String email) {

        return email != null
                && EMAIL_PATTERN.matcher(email).matches();
    }

    // Validates an Indian mobile phone number.
    private boolean isValidPhone(String phone) {

        return phone != null
                && PHONE_PATTERN.matcher(phone).matches();
    }

    // Checks password complexity without converting char[] to String.
    private boolean isStrongPassword(char[] password) {

        if (password == null || password.length < 8) {
            return false;
        }

        boolean hasUppercase = false;
        boolean hasLowercase = false;
        boolean hasDigit = false;
        boolean hasSpecialCharacter = false;

        for (char character : password) {

            if (Character.isUpperCase(character)) {
                hasUppercase = true;
            } else if (Character.isLowerCase(character)) {
                hasLowercase = true;
            } else if (Character.isDigit(character)) {
                hasDigit = true;
            } else {
                hasSpecialCharacter = true;
            }
        }

        return hasUppercase
                && hasLowercase
                && hasDigit
                && hasSpecialCharacter;
    }
}
