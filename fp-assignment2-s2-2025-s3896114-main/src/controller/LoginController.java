package controller;



import model.Model;
import model.User;
import util.PasswordUtil;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

public class LoginController {
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Label messageLabel;
    @FXML private Button loginButton;
    @FXML private Hyperlink signupLink;

    private Model model;
    private Stage stage;

    public LoginController(Stage stage, Model model) {
        this.stage = stage;
        this.model = model;
    }

    @FXML
    public void initialize() {
        loginButton.setOnAction(event -> handleLogin());
        signupLink.setOnAction(event -> showSignupView());

        passwordField.setOnAction(event -> handleLogin());
    }
//Checks username and password and logs in the user.
    private void handleLogin() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText();
       // Check for empty fields
        if (username.isEmpty() || password.isEmpty()) {
            showError("Please enter both username and password");
            return;
        }
        // Hash password and check user in database
        try {
            String hashedPassword = PasswordUtil.hashPassword(password);
            User user = model.getUserDao().getUser(username, hashedPassword);

            if (user != null) {
                model.setCurrentUser(user);
                // Go to dashboard depending on user type
                if (user.isAdmin()) {
                    showAdminDashboard();
                } else {
                    showDashboard();
                }
            } else {
                showError("Invalid username or password");
            }
        } catch (Exception e) {
            showError("Login error: " + e.getMessage());
        }
    }
//Opens the normal user dashboard
    private void showDashboard() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/dashboard.fxml"));
            controller.DashboardController controller = new controller.DashboardController(stage, model);
            loader.setController(controller);
            Parent root = loader.load();

            Scene scene = new Scene(root, 1000, 700);
            stage.setScene(scene);
            stage.setTitle("VolunTrack - Dashboard");
        } catch (Exception e) {
            showError("Error loading dashboard: " + e.getMessage());
        }
    }
    //Opens the admin dashboard for admin users
    private void showAdminDashboard() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/admin-dashboard.fxml"));
            controller.AdminDashboardController controller = new controller.AdminDashboardController(stage, model);
            loader.setController(controller);
            Parent root = loader.load();

            Scene scene = new Scene(root, 1200, 750);
            stage.setScene(scene);
            stage.setTitle("VolunTrack - Admin Dashboard");
        } catch (Exception e) {
            showError("Error loading admin dashboard: " + e.getMessage());
        }
    }
//Opens the signup page for new users
    private void showSignupView() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/signup.fxml"));
            controller.SignupController controller = new controller.SignupController(stage, model);
            loader.setController(controller);
            Parent root = loader.load();

            Scene scene = new Scene(root, 500, 600);
            stage.setScene(scene);
            stage.setTitle("VolunTrack - Sign Up");
        } catch (Exception e) {
            showError("Error loading signup page: " + e.getMessage());
        }
    }
    //Shows an error message on the login screen
    private void showError(String message) {
        messageLabel.setText(message);
        messageLabel.setStyle("-fx-text-fill: #ef4444;");
    }

    public void showStage(Parent root) {
        Scene scene = new Scene(root, 500, 400);
        stage.setScene(scene);
        stage.setTitle("VolunTrack - Login");
        stage.show();
    }
}
