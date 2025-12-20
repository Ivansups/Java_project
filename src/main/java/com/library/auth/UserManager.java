package com.library.auth;

import com.library.database.DatabaseManager;

import java.util.List;

public class UserManager {
    private static UserManager instance;
    private DatabaseManager dbManager;

    private UserManager() {
        dbManager = DatabaseManager.getInstance();
    }

    public static synchronized UserManager getInstance() {
        if (instance == null) {
            instance = new UserManager();
        }
        return instance;
    }

    public boolean authenticate(String username, String password) {
        return dbManager.authenticateUser(username, password);
    }

    public String getUserRole(String username) {
        return dbManager.getUserRole(username);
    }

    public List<User> getAllUsers() {
        List<DatabaseManager.SystemUser> systemUsers = dbManager.getAllSystemUsers();
        return systemUsers.stream()
                .map(su -> new User(su.getUsername(), su.getPassword(), su.getRole()))
                .collect(java.util.stream.Collectors.toList());
    }

    public void addUser(String username, String password, String role) {
        dbManager.addSystemUser(username, password, role);
    }

    public void updateUser(String username, String newPassword, String newRole) {
        dbManager.updateSystemUser(username, newPassword, newRole);
    }

    public void deleteUser(String username) {
        dbManager.deleteSystemUser(username);
    }

    public boolean userExists(String username) {
        return dbManager.systemUserExists(username);
    }

    public static class User {
        private String username;
        private String password;
        private String role;

        public User(String username, String password, String role) {
            this.username = username;
            this.password = password;
            this.role = role;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public String getRole() {
            return role;
        }

        public void setRole(String role) {
            this.role = role;
        }
    }
}
