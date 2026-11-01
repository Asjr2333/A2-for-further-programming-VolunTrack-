package dao;

import model.Registration;
import java.sql.SQLException;
import java.util.List;


public interface RegistrationDao {
    void setup() throws SQLException;
    // Add a new registration
    void addRegistration(Registration registration) throws SQLException;
    // Get all registrations for a user
    List<Registration> getRegistrationsByUserId(int userId) throws SQLException;
    // Get all registrations in the system
    List<Registration> getAllRegistrations() throws SQLException;
    // Get next registration ID number
    int getNextRegistrationNumber() throws SQLException;

}
