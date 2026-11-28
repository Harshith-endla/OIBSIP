
package com.reservation.service;

import com.reservation.model.User;
import com.reservation.repository.UserRepository;
import com.reservation.util.ValidationUtil;

import java.security.GeneralSecurityException;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Map;

public class AdminProvisioningService {

    private static final String ADMIN_NAME = "RESERVATION_ADMIN_NAME";
    private static final String ADMIN_EMAIL = "RESERVATION_ADMIN_EMAIL";
    private static final String ADMIN_PHONE = "RESERVATION_ADMIN_PHONE";
    private static final String ADMIN_PASSWORD = "RESERVATION_ADMIN_PASSWORD";

    private final UserRepository userRepository;
    private final Map<String, String> environment;

    // Creates the repository for normal application usage.
    public AdminProvisioningService() {
        this(new UserRepository());
    }

    // Uses actual application environment variables.
    public AdminProvisioningService(UserRepository userRepository) {
        this(userRepository, System.getenv());
    }

    // Allows tests to provide environment variables.
    public AdminProvisioningService(
            UserRepository userRepository,
            Map<String, String> environment) {

        if (userRepository == null) {
            throw new IllegalArgumentException(
                    "User repository cannot be null.");
        }

        if (environment == null) {
            throw new IllegalArgumentException(
                    "Environment configuration cannot be null.");
        }

        this.userRepository = userRepository;
        this.environment = environment;
    }

    public void provisionAdmin()
            throws SQLException, GeneralSecurityException {

        String name = environment.get(ADMIN_NAME);
        String email = environment.get(ADMIN_EMAIL);
        String phone = environment.get(ADMIN_PHONE);
        String password = environment.get(ADMIN_PASSWORD);

        boolean allAbsent = name == null
                && email == null
                && phone == null
                && password == null;

        if (allAbsent) {
            System.out.println(
                    "Administrator configuration not found. "
                            + "Skipping administrator provisioning.");
            return;
        }

        if (ValidationUtil.isBlank(name)
                || ValidationUtil.isBlank(email)
                || ValidationUtil.isBlank(phone)
                || ValidationUtil.isBlank(password)) {

            throw new IllegalStateException(
                    "Incomplete administrator configuration. "
                            + "Please configure all four administrator "
                            + "environment variables.");
        }

        User existingUser = userRepository.findByEmail(email.trim());

        if (existingUser != null) {

            if ("ADMIN".equals(existingUser.getRole())) {
                System.out.println(
                        "Administrator account already exists. "
                                + "Skipping administrator provisioning.");
                return;
            }

            throw new IllegalStateException(
                    "The configured administrator email already belongs "
                            + "to a regular user. Use a different email.");
        }

        char[] adminPassword = password.toCharArray();

        try {
            userRepository.registerAdmin(
                    name.trim(),
                    email.trim(),
                    phone.trim(),
                    adminPassword);

            System.out.println(
                    "Administrator account created successfully.");

        } finally {
            Arrays.fill(adminPassword, '\0');
        }
    }
}
