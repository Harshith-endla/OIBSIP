
package com.reservation.service;

import com.reservation.model.User;
import com.reservation.repository.UserRepository;
import com.reservation.security.PasswordUtil;

import java.security.GeneralSecurityException;
import java.sql.SQLException;

public class AuthenticationService {

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

        // Register the user through the repository.
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

        User user = userRepository.findByEmail(email.trim());

        if (user == null) {
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
}
