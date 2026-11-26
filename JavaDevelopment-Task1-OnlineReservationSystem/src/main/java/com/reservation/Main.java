
package com.reservation;

import com.reservation.database.DatabaseSchema;
import com.reservation.repository.UserRepository;
import com.reservation.service.AdminProvisioningService;
import com.reservation.ui.LoginFrame;

import javax.swing.*;
import java.security.GeneralSecurityException;
import java.sql.SQLException;

public class Main {

    public static void main(String[] args) {

        try {
            // Step 1: Initialize the database and apply migrations.
            DatabaseSchema.createTables();

            // Step 2: Create the administrator if configured.
            AdminProvisioningService adminProvisioningService = new AdminProvisioningService(new UserRepository());

            adminProvisioningService.provisionAdmin();

        } catch (SQLException
                | GeneralSecurityException
                | IllegalStateException e) {

            System.out.println("Application initialization failed!");
            e.printStackTrace();
            return;
        }

        // Step 3: Open the login screen.
        SwingUtilities.invokeLater(() -> {
            LoginFrame loginFrame = new LoginFrame();
            loginFrame.setVisible(true);
        });
    }
}
