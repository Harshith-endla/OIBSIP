
package com.reservation;

import com.reservation.database.DatabaseConnection;
import java.sql.Connection;
import java.sql.SQLException;

public class Main {

    public static void main(String[] args) {

        System.out.println("==================================");
        System.out.println("     ONLINE RESERVATION SYSTEM");
        System.out.println("==================================");

        try (Connection connection = DatabaseConnection.getConnection()) {

            if (connection != null && !connection.isClosed()) {
                System.out.println("Database connection successful!");
            }

        } catch (SQLException e) {
            System.out.println("Database connection failed!");
            e.printStackTrace();
        }
    }
}
