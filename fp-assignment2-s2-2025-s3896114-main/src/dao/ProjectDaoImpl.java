package dao;

import model.Project;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProjectDaoImpl implements ProjectDao {
    private final String TABLE_NAME = "projects";
//Creates the projects table if it doesn't exist and loads sample data from CSV
    @Override
    public void setup() throws SQLException {
        try (Connection connection = Database.getConnection();
             Statement stmt = connection.createStatement()) {
            // Create table if missing
            String sql = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "title VARCHAR(50) NOT NULL," +
                    "location VARCHAR(50) NOT NULL," +
                    "day VARCHAR(10) NOT NULL," +
                    "hourly_value INTEGER NOT NULL," +
                    "registered_slots INTEGER NOT NULL," +
                    "total_slots INTEGER NOT NULL," +
                    "enabled BOOLEAN DEFAULT 1)";
            stmt.executeUpdate(sql);

            String checkData = "SELECT COUNT(*) FROM " + TABLE_NAME;
            ResultSet rs = stmt.executeQuery(checkData);
            if (rs.next() && rs.getInt(1) == 0) {
                loadProjectsFromCSV(connection);
            }
        }
    }
    //Loads project data from a CSV file if available
    private void loadProjectsFromCSV(Connection connection) throws SQLException {
        try {
            InputStream is = getClass().getClassLoader().getResourceAsStream("src/main/resources/projects.csv");
            if (is == null) {
                is  = new java.io.FileInputStream("src/main/resources/projects.csv");

            }

            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            String line;
            boolean firstLine = true;

            String insertSql = "INSERT INTO " + TABLE_NAME +
                    " (title, location, day, hourly_value, registered_slots, total_slots, enabled) VALUES (?, ?, ?, ?, ?, ?, ?)";

            while ((line = reader.readLine()) != null) {
                if (firstLine) {
                    firstLine = false;
                    continue;
                }

                String[] parts = line.split(",");
                if (parts.length == 6) {
                    try (PreparedStatement pstmt = connection.prepareStatement(insertSql)) {
                        pstmt.setString(1, parts[0].trim());
                        pstmt.setString(2, parts[1].trim());
                        pstmt.setString(3, parts[2].trim());
                        pstmt.setInt(4, Integer.parseInt(parts[3].trim()));
                        pstmt.setInt(5, Integer.parseInt(parts[4].trim()));
                        pstmt.setInt(6, Integer.parseInt(parts[5].trim()));
                        pstmt.setBoolean(7, true);
                        pstmt.executeUpdate();
                    }
                }
            }
            reader.close();
        } catch (Exception e) {
            throw new SQLException("Error loading projects from CSV", e);
        }
    }
    //Returns all projects
    @Override
    public List<Project> getAllProjects() throws SQLException {
        List<Project> projects = new ArrayList<>();
        String sql = "SELECT * FROM " + TABLE_NAME;

        try (Connection connection = Database.getConnection();
             Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                projects.add(extractProjectFromResultSet(rs));
            }
        }
        return projects;
    }
    //Returns only enabled projects
    @Override
    public List<Project> getEnabledProjects() throws SQLException {
        List<Project> projects = new ArrayList<>();
        String sql = "SELECT * FROM " + TABLE_NAME + " WHERE enabled = 1";

        try (Connection connection = Database.getConnection();
             Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                projects.add(extractProjectFromResultSet(rs));
            }
        }
        return projects;
    }
    //Gets one project by ID
    @Override
    public Project getProjectById(int id) throws SQLException {
        String sql = "SELECT * FROM " + TABLE_NAME + " WHERE id = ?";

        try (Connection connection = Database.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return extractProjectFromResultSet(rs);
                }
            }
        }
        return null;
    }
    //Updates all project fields
    @Override
    public void updateProject(Project project) throws SQLException {
        String sql = "UPDATE " + TABLE_NAME +
                " SET title = ?, location = ?, day = ?, hourly_value = ?, registered_slots = ?, total_slots = ? WHERE id = ?";

        try (Connection connection = Database.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, project.getTitle());
            stmt.setString(2, project.getLocation());
            stmt.setString(3, project.getDay());
            stmt.setInt(4, project.getHourlyValue());
            stmt.setInt(5, project.getRegisteredSlots());
            stmt.setInt(6, project.getTotalSlots());
            stmt.setInt(7, project.getId());
            stmt.executeUpdate();
        }
    }
    //Adds more registered slots to a project
    @Override
    public void updateProjectSlots(int projectId, int additionalSlots) throws SQLException {
        String sql = "UPDATE " + TABLE_NAME + " SET registered_slots = registered_slots + ? WHERE id = ?";

        try (Connection connection = Database.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, additionalSlots);
            stmt.setInt(2, projectId);
            stmt.executeUpdate();
        }
    }
    //Adds a new project to the database
    @Override
    public void addProject(Project project) throws SQLException {
        String sql = "INSERT INTO " + TABLE_NAME +
                " (title, location, day, hourly_value, registered_slots, total_slots, enabled) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection connection = Database.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, project.getTitle());
            stmt.setString(2, project.getLocation());
            stmt.setString(3, project.getDay());
            stmt.setInt(4, project.getHourlyValue());
            stmt.setInt(5, project.getRegisteredSlots());
            stmt.setInt(6, project.getTotalSlots());
            stmt.setBoolean(7, project.isEnabled());
            stmt.executeUpdate();
        }
    }
    //Checks if a project already exists with the same title, location, and day
    @Override
    public boolean projectExists(String title, String location, String day) throws SQLException {
        String sql = "SELECT COUNT(*) FROM " + TABLE_NAME + " WHERE title = ? AND location = ? AND day = ?";

        try (Connection connection = Database.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, title);
            stmt.setString(2, location);
            stmt.setString(3, day);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        return false;
    }
    //Enables or disables a project
    @Override
    public void toggleProjectStatus(int projectId, boolean enabled) throws SQLException {
        String sql = "UPDATE " + TABLE_NAME + " SET enabled = ? WHERE id = ?";

        try (Connection connection = Database.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setBoolean(1, enabled);
            stmt.setInt(2, projectId);
            stmt.executeUpdate();
        }
    }
    //Converts a database row into a Project object
    private Project extractProjectFromResultSet(ResultSet rs) throws SQLException {
        return new Project(
                rs.getInt("id"),
                rs.getString("title"),
                rs.getString("location"),
                rs.getString("day"),
                rs.getInt("hourly_value"),
                rs.getInt("registered_slots"),
                rs.getInt("total_slots"),
                rs.getBoolean("enabled")
        );
    }
}