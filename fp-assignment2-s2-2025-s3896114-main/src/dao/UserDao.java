package dao;

import java.sql.SQLException;
import model.User;

public interface UserDao {
    // Create users table if not exists
    void setup() throws SQLException;
    // Get user by username and password
    User getUser(String username, String password) throws SQLException;
    // Create a new user account
    User createUser(String fullName, String username, String email, String password) throws SQLException;
    // Check if a username already exists
    boolean usernameExists(String username) throws SQLException;
    // Update user's password
    void updatePassword(int userId, String newPassword) throws SQLException;

}
