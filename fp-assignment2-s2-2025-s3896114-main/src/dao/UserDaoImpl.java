package dao;

import model.User;
import java.sql.*;

public class UserDaoImpl implements UserDao {
    private final String TABLE_NAME = "users";

    // Create users table and default admin if not exists
    @Override
    public void setup() throws SQLException {
        try (Connection connection = Database.getConnection();
             Statement stmt = connection.createStatement()) {
            // Create table if missing
            String sql = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "full_name VARCHAR(100) NOT NULL," +
                    "username VARCHAR(50) NOT NULL UNIQUE," +
                    "email VARCHAR(100) NOT NULL," +
                    "password VARCHAR(100) NOT NULL," +
                    "is_admin BOOLEAN DEFAULT 0)";
            stmt.executeUpdate(sql);
            // Add default admin user if none exists
            String checkAdmin = "SELECT COUNT(*) FROM " + TABLE_NAME + " WHERE username = 'admin'";
            ResultSet rs = stmt.executeQuery(checkAdmin);
            if (rs.next() && rs.getInt(1) == 0) {
                String insertAdmin = "INSERT INTO " + TABLE_NAME +
                        " (full_name, username, email, password, is_admin) VALUES (?, ?, ?, ?, ?)";
                try (PreparedStatement pstmt = connection.prepareStatement(insertAdmin)) {
                    pstmt.setString(1, "Administrator");
                    pstmt.setString(2, "admin");
                    pstmt.setString(3, "admin@voluntrack.com");
                    pstmt.setString(4, util.PasswordUtil.hashPassword("Admin654!@"));
                    pstmt.setBoolean(5, true);
                    pstmt.executeUpdate();
                }
            }
        }
    }

    // Get user by username and password
    @Override
    public User getUser(String username, String password) throws SQLException {
        String sql = "SELECT * FROM " + TABLE_NAME + " WHERE username = ? AND password = ? ";
        try (Connection connection = Database.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, username);
            stmt.setString(2, password);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new User(
                            rs.getInt("id"),
                            rs.getString("full_name"),
                            rs.getString("username"),
                            rs.getString("email"),
                            rs.getString("password"),
                            rs.getBoolean("is_admin")
                    );
                }
                return null;
            }
        }
    }

    // Add new user
    @Override
    public User createUser(String fullName, String username, String email, String password) throws SQLException {
        String sql = "INSERT INTO " + TABLE_NAME + " (full_name, username, email, password, is_admin) VALUES (?, ?, ?, ?, ?)";
        try (Connection connection = Database.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, fullName);
            stmt.setString(2, username);
            stmt.setString(3, email);
            stmt.setString(4, password);
            stmt.setBoolean(5, false);

            stmt.executeUpdate();
            // Return created user with generated ID
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    int id = generatedKeys.getInt(1);
                    return new User(id, fullName, username, email, password, false);
                }
            }
            return null;
        }
    }

    // Check if username already exists
    @Override
    public boolean usernameExists(String username) throws SQLException {
        String sql = "SELECT COUNT(*) FROM " + TABLE_NAME + " WHERE username = ?";
        try (Connection connection = Database.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, username);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        return false;
    }

    // Update user's password
    @Override
    public void updatePassword(int userId, String newPassword) throws SQLException {
        String sql = "UPDATE " + TABLE_NAME + " SET password = ? WHERE id = ?";
        try (Connection connection = Database.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, newPassword);
            stmt.setInt(2, userId);
            stmt.executeUpdate();
        }
    }
}