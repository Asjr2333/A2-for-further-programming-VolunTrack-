package dao;

import model.Registration;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class RegistrationDaoImpl implements RegistrationDao {
    private final String TABLE_NAME = "registrations";
    // Create the registrations table if it doesn't exist
    @Override
    public void setup() throws SQLException {
        try (Connection connection = Database.getConnection();
             Statement stmt = connection.createStatement()) {
            String sql = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "registration_id VARCHAR(10) NOT NULL," +
                    "user_id INTEGER NOT NULL," +
                    "project_id INTEGER NOT NULL," +
                    "slots INTEGER NOT NULL," +
                    "hours_per_slot INTEGER NOT NULL," +
                    "total_contribution INTEGER NOT NULL," +
                    "confirmed_at TIMESTAMP NOT NULL," +
                    "FOREIGN KEY (user_id) REFERENCES users(id)," +
                    "FOREIGN KEY (project_id) REFERENCES projects(id))";
            stmt.executeUpdate(sql);
        }
    }
    // Insert a new registration record
    @Override
    public void addRegistration(Registration registration) throws SQLException {
        String sql = "INSERT INTO " + TABLE_NAME +
                " (registration_id, user_id, project_id, slots, hours_per_slot, total_contribution, confirmed_at) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection connection = Database.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, registration.getRegistrationId());
            stmt.setInt(2, registration.getUserId());
            stmt.setInt(3, registration.getProjectId());
            stmt.setInt(4, registration.getSlots());
            stmt.setInt(5, registration.getHoursPerSlot());
            stmt.setInt(6, registration.getTotalContribution());
            stmt.setTimestamp(7, Timestamp.valueOf(registration.getConfirmedAt()));
            stmt.executeUpdate();
        }
    }
    // Get all registrations for a specific user
    @Override
    public List<Registration> getRegistrationsByUserId(int userId) throws SQLException {
        List<Registration> registrations = new ArrayList<>();
        String sql = "SELECT r.*, p.title, p.location, p.day FROM " + TABLE_NAME + " r " +
                "JOIN projects p ON r.project_id = p.id " +
                "WHERE r.user_id = ? ORDER BY r.confirmed_at DESC";

        try (Connection connection = Database.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, userId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Registration reg = extractRegistrationFromResultSet(rs);
                    registrations.add(reg);
                }
            }
        }
        return registrations;
    }
    // Get all registrations from all users
    @Override
    public List<Registration> getAllRegistrations() throws SQLException {
        List<Registration> registrations = new ArrayList<>();
        String sql = "SELECT r.*, p.title, p.location, p.day, u.username FROM " + TABLE_NAME + " r " +
                "JOIN projects p ON r.project_id = p.id " +
                "JOIN users u ON r.user_id = u.id " +
                "ORDER BY r.confirmed_at DESC";

        try (Connection connection = Database.getConnection();
             Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Registration reg = extractRegistrationFromResultSet(rs);
                registrations.add(reg);
            }
        }
        return registrations;
    }
    // Get the next registration sequence number
    @Override
    public int getNextRegistrationNumber() throws SQLException {
        String sql = "SELECT COUNT(*) FROM " + TABLE_NAME;

        try (Connection connection = Database.getConnection();
             Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                return rs.getInt(1) + 1;
            }
        }
        return 1;
    }
    // Convert DB row to Registration object
    private Registration extractRegistrationFromResultSet(ResultSet rs) throws SQLException {
        Registration reg = new Registration();
        reg.setId(rs.getInt("id"));
        reg.setRegistrationId(rs.getString("registration_id"));
        reg.setUserId(rs.getInt("user_id"));
        reg.setProjectId(rs.getInt("project_id"));
        reg.setSlots(rs.getInt("slots"));
        reg.setHoursPerSlot(rs.getInt("hours_per_slot"));
        reg.setTotalContribution(rs.getInt("total_contribution"));
        reg.setConfirmedAt(rs.getTimestamp("confirmed_at").toLocalDateTime());
        reg.setProjectTitle(rs.getString("title"));
        reg.setProjectLocation(rs.getString("location"));
        reg.setProjectDay(rs.getString("day"));
        return reg;
    }
}
