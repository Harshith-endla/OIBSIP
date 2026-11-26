
package com.reservation.model;

public class User {

    private int userId;
    private String fullName;
    private String email;
    private String phone;
    private String passwordHash;
    private String role;

    // Constructor for existing user creation.
    // Defaults the role to USER.
    public User(
            int userId,
            String fullName,
            String email,
            String phone,
            String passwordHash) {

        this(
                userId,
                fullName,
                email,
                phone,
                passwordHash,
                "USER");
    }

    // Constructor that accepts an explicit user role.
    public User(
            int userId,
            String fullName,
            String email,
            String phone,
            String passwordHash,
            String role) {

        this.userId = userId;
        this.fullName = fullName;
        this.email = email;
        this.phone = phone;
        this.passwordHash = passwordHash;
        this.role = role;
    }

    public int getUserId() {
        return userId;
    }

    public String getFullName() {
        return fullName;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public String getRole() {
        return role;
    }
}
