
package com.reservation.repository;

import com.reservation.database.DatabaseConnection;
import com.reservation.model.User;
import com.reservation.security.PasswordUtil;

import java.security.GeneralSecurityException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserRepository {

    private final Connection testConnection;

    // Uses the application's database connection.
    public UserRepository() {
        this.testConnection = null;
    }

    // Accepts a connection for isolated database tests.
    public UserRepository(Connection connection) {
        if (connection == null) {
            throw new IllegalArgumentException(
                    "Database connection cannot be null.");
        }

        this.testConnection = connection;
    }

    // Registers a new user with the default USER role.
    // Only the password hash is stored in the database.
    public User registerUser(
            String fullName,
            String email,
            String phone,
            char[] password)
            throws SQLException, GeneralSecurityException {

        // Check whether an account already exists with this email.
        if (findByEmail(email) != null) {
            throw new SQLException(
                    "An account with this email already exists.");
        }

        String passwordHash = PasswordUtil.hashPassword(password);

        String sql = """
                INSERT INTO Users
                    (full_name, email, phone, password_hash, role)
                VALUES (?, ?, ?, ?, 'USER')
                """;

        if (testConnection != null) {
            return registerUserUsingConnection(
                    testConnection, sql, fullName, email, phone, passwordHash);
        }

        try (Connection connection = DatabaseConnection.getConnection()) {
            return registerUserUsingConnection(
                    connection, sql, fullName, email, phone, passwordHash);
        }
    }

    // Registers an administrator with the ADMIN role.
    // This method is intended only for trusted application provisioning.
    public User registerAdmin(
            String fullName,
            String email,
            String phone,
            char[] password)
            throws SQLException, GeneralSecurityException {

        if (findByEmail(email) != null) {
            throw new SQLException(
                    "An account with this email already exists.");
        }

        String passwordHash = PasswordUtil.hashPassword(password);

        String sql = """
                INSERT INTO Users
                    (full_name, email, phone, password_hash, role)
                VALUES (?, ?, ?, ?, 'ADMIN')
                """;

        if (testConnection != null) {
            return registerAdminUsingConnection(
                    testConnection, sql, fullName, email, phone, passwordHash);
        }

        try (Connection connection = DatabaseConnection.getConnection()) {
            return registerAdminUsingConnection(
                    connection, sql, fullName, email, phone, passwordHash);
        }
    }

    private User registerAdminUsingConnection(
            Connection connection,
            String sql,
            String fullName,
            String email,
            String phone,
            String passwordHash) throws SQLException {

        try (PreparedStatement statement = connection.prepareStatement(
                sql,
                PreparedStatement.RETURN_GENERATED_KEYS)) {

            statement.setString(1, fullName);
            statement.setString(2, email);
            statement.setString(3, phone);
            statement.setString(4, passwordHash);

            statement.executeUpdate();

            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {

                if (generatedKeys.next()) {
                    return new User(
                            generatedKeys.getInt(1),
                            fullName,
                            email,
                            phone,
                            passwordHash,
                            "ADMIN");
                }

                throw new SQLException(
                        "Administrator registration failed. No user ID was generated.");
            }
        }
    }

    private User registerUserUsingConnection(
            Connection connection,
            String sql,
            String fullName,
            String email,
            String phone,
            String passwordHash) throws SQLException {

        try (PreparedStatement statement = connection.prepareStatement(
                sql,
                PreparedStatement.RETURN_GENERATED_KEYS)) {

            statement.setString(1, fullName);
            statement.setString(2, email);
            statement.setString(3, phone);
            statement.setString(4, passwordHash);

            statement.executeUpdate();

            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {

                if (generatedKeys.next()) {

                    int userId = generatedKeys.getInt(1);

                    return new User(
                            userId,
                            fullName,
                            email,
                            phone,
                            passwordHash,
                            "USER");
                }

                throw new SQLException(
                        "User registration failed. No user ID was generated.");
            }
        }
    }

    // Finds a registered user using their email address.
    public User findByEmail(String email) throws SQLException {

        String sql = """
                SELECT user_id, full_name, email, phone, password_hash, role
                FROM Users
                WHERE email = ?
                """;

        if (testConnection != null) {
            return findUserUsingConnection(testConnection, sql, email);
        }

        try (Connection connection = DatabaseConnection.getConnection()) {
            return findUserUsingConnection(connection, sql, email);
        }
    }

    private User findUserUsingConnection(
            Connection connection,
            String sql,
            String email) throws SQLException {

        try (PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, email);

            try (ResultSet resultSet = statement.executeQuery()) {

                if (resultSet.next()) {

                    return new User(
                            resultSet.getInt("user_id"),
                            resultSet.getString("full_name"),
                            resultSet.getString("email"),
                            resultSet.getString("phone"),
                            resultSet.getString("password_hash"),
                            resultSet.getString("role"));
                }

                return null;
            }
        }
    }
}
