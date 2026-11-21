
package com.reservation;

import com.reservation.database.DatabaseSchema;
import com.reservation.ui.LoginFrame;

import javax.swing.*;
import java.sql.SQLException;

public class Main {

    public static void main(String[] args) {

        try {
            DatabaseSchema.createTables();

        } catch (SQLException e) {

            System.out.println("Database initialization failed!");
            e.printStackTrace();
            return;
        }

        SwingUtilities.invokeLater(() -> {
            LoginFrame loginFrame = new LoginFrame();
            loginFrame.setVisible(true);
        });
    }
}
