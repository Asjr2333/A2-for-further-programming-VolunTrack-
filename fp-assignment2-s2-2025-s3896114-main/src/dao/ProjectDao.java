package dao;

import model.Project;
import java.sql.SQLException;
import java.util.List;


public interface ProjectDao {

    // Sets up the project table if it doesn't exist
    void setup() throws SQLException;

    // Gets all projects (both enabled and disabled)
    List<Project> getAllProjects() throws SQLException;

    // Gets only enabled (active) projects
    List<Project> getEnabledProjects() throws SQLException;

    // Finds a project by its ID
    Project getProjectById(int id) throws SQLException;

    // Updates project details
    void updateProject(Project project) throws SQLException;

    // Updates the number of registered slots for a project
    void updateProjectSlots(int projectId, int additionalSlots) throws SQLException;

    // Adds a new project to the database
    void addProject(Project project) throws SQLException;

    //Checks if a project already exists with the same title, location, and day
    boolean projectExists(String title, String location, String day) throws SQLException;

    //Enables or disables a project
    void toggleProjectStatus(int projectId, boolean enabled) throws SQLException;
}
