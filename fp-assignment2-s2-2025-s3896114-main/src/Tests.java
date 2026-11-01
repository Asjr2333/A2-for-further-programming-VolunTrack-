import model.Project;
import util.DateUtil;
import util.PasswordUtil;
import util.ValidationUtil;
import org.junit.jupiter.api.Test;
import java.time.DayOfWeek;
import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.*;

public class Tests {

    @Test
    public void testPasswordValidation() {
        assertTrue(PasswordUtil.validatePassword("Passw0rd!"),
                "Valid password should pass validation");

        assertFalse(PasswordUtil.validatePassword("pass"),
                "Password too short should fail");

        assertFalse(PasswordUtil.validatePassword("password123"),
                "Password without uppercase should fail");

        assertFalse(PasswordUtil.validatePassword("PASSWORD123!"),
                "Password without lowercase should fail");

        assertFalse(PasswordUtil.validatePassword("Password"),
                "Password without numbers should fail");

        assertFalse(PasswordUtil.validatePassword("Password123"),
                "Password without special characters should fail");
    }

    @Test
    public void testProjectSlotAvailability() {
        Project project = new Project("Test Project", "Test Location", "Mon", 25, 10, 50);

        assertEquals(40, project.getAvailableSlots(),
                "Available slots should be total minus registered");

        project.setRegisteredSlots(50);
        assertEquals(0, project.getAvailableSlots(),
                "Available slots should be 0 when fully booked");

        project.setRegisteredSlots(0);
        assertEquals(50, project.getAvailableSlots(),
                "Available slots should equal total when none registered");
    }

    @Test
    public void testConfirmationCodeValidation() {
        assertTrue(ValidationUtil.validateConfirmationCode("123456"),
                "Valid 6-digit code should pass");

        assertTrue(ValidationUtil.validateConfirmationCode("000000"),
                "All zeros should be valid");

        assertFalse(ValidationUtil.validateConfirmationCode("12345"),
                "5-digit code should fail");

        assertFalse(ValidationUtil.validateConfirmationCode("1234567"),
                "7-digit code should fail");

        assertFalse(ValidationUtil.validateConfirmationCode("12345a"),
                "Code with letters should fail");

        assertFalse(ValidationUtil.validateConfirmationCode("12.345"),
                "Code with special characters should fail");
    }

    @Test
    public void testProjectDateRestrictions() {
        DayOfWeek today = LocalDate.now().getDayOfWeek();
        int todayValue = today.getValue();

        String todayString = DateUtil.getDayOfWeekString(today);
        assertTrue(DateUtil.canRegisterForDay(todayString),
                "Should be able to register for today");

        if (todayValue < 7) {
            DayOfWeek tomorrow = DayOfWeek.of(todayValue + 1);
            String tomorrowString = DateUtil.getDayOfWeekString(tomorrow);
            assertTrue(DateUtil.canRegisterForDay(tomorrowString),
                    "Should be able to register for tomorrow");
        }

        if (todayValue > 1) {
            DayOfWeek yesterday = DayOfWeek.of(todayValue - 1);
            String yesterdayString = DateUtil.getDayOfWeekString(yesterday);
            assertFalse(DateUtil.canRegisterForDay(yesterdayString),
                    "Should not be able to register for yesterday");
        }
    }

    @Test
    public void testDuplicateProjectValidation() {
        String title = "Park Cleanup";
        String location = "Green Park";
        String day = "Mon";

        assertTrue(ValidationUtil.validateProjectTitle(title),
                "Valid title should pass");
        assertTrue(ValidationUtil.validateProjectLocation(location),
                "Valid location should pass");
        assertTrue(ValidationUtil.validateDay(day),
                "Valid day should pass");

        assertFalse(ValidationUtil.validateProjectTitle(""),
                "Empty title should fail");
        assertFalse(ValidationUtil.validateProjectTitle("This is a very long project title that exceeds thirty characters"),
                "Title over 30 characters should fail");

        assertFalse(ValidationUtil.validateDay("Monday"),
                "Full day name should fail");
        assertFalse(ValidationUtil.validateDay("mon"),
                "Lowercase day should fail");

        assertTrue(ValidationUtil.validateHourlyValue(50),
                "Valid hourly value should pass");
        assertFalse(ValidationUtil.validateHourlyValue(0),
                "Zero hourly value should fail");
        assertFalse(ValidationUtil.validateHourlyValue(101),
                "Hourly value over 100 should fail");

        assertTrue(ValidationUtil.validateTotalSlots(50),
                "Valid total slots should pass");
        assertFalse(ValidationUtil.validateTotalSlots(0),
                "Zero total slots should fail");
        assertFalse(ValidationUtil.validateTotalSlots(101),
                "Total slots over 100 should fail");
    }

    @Test
    public void testPasswordHashing() {
        String password = "TestPass123!";
        String hash1 = PasswordUtil.hashPassword(password);
        String hash2 = PasswordUtil.hashPassword(password);

        assertNotNull(hash1, "Hash should not be null");
        assertNotEquals(password, hash1, "Hash should not equal original password");
        assertEquals(hash1, hash2, "Same password should produce same hash");

        String differentPassword = "TestPass123@";
        String hash3 = PasswordUtil.hashPassword(differentPassword);
        assertNotEquals(hash1, hash3, "Different passwords should produce different hashes");
    }

    @Test
    public void testEmailValidation() {
        assertTrue(ValidationUtil.validateEmail("user@example.com"),
                "Valid email should pass");

        assertTrue(ValidationUtil.validateEmail("test.user@example.co.uk"),
                "Email with subdomain should pass");

        assertFalse(ValidationUtil.validateEmail("invalid"),
                "Email without @ should fail");

        assertFalse(ValidationUtil.validateEmail("@example.com"),
                "Email without username should fail");

        assertFalse(ValidationUtil.validateEmail("user@"),
                "Email without domain should fail");
    }

    @Test
    public void testSlotAndHourValidation() {
        assertTrue(ValidationUtil.validateHoursPerSlot(1),
                "1 hour per slot should be valid");
        assertTrue(ValidationUtil.validateHoursPerSlot(2),
                "2 hours per slot should be valid");
        assertTrue(ValidationUtil.validateHoursPerSlot(3),
                "3 hours per slot should be valid");

        assertFalse(ValidationUtil.validateHoursPerSlot(0),
                "0 hours per slot should be invalid");
        assertFalse(ValidationUtil.validateHoursPerSlot(4),
                "4 hours per slot should be invalid");

        assertTrue(ValidationUtil.validateSlotsPerProject(1),
                "1 slot per project should be valid");
        assertTrue(ValidationUtil.validateSlotsPerProject(3),
                "3 slots per project should be valid");

        assertFalse(ValidationUtil.validateSlotsPerProject(0),
                "0 slots per project should be invalid");
        assertFalse(ValidationUtil.validateSlotsPerProject(4),
                "4 slots per project should be invalid");
    }
}