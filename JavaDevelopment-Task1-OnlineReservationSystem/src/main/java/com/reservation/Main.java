
package com.reservation;

import com.reservation.database.DatabaseSchema;
import java.sql.SQLException;

public class Main {

    public static void main(String[] args) {

        System.out.println("==================================");
        System.out.println("     ONLINE RESERVATION SYSTEM");
        System.out.println("==================================");

        try {
            DatabaseSchema.createTables();

        } catch (SQLException e) {
            System.out.println("Database initialization failed!");
            e.printStackTrace();
        }
    }
}
