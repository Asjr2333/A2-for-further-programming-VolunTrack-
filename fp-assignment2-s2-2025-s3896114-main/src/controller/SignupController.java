package controller;

import model.Model;
import model.User;
import util.PasswordUtil;
import util.ValidationUtil;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

public class SignupController {
    @FXML private TextField fullNameField;
    @FXML private TextField usernameField;
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private PasswordField confirmPasswordField;
    @FXML private Label messageLabel;
    @FXML private Button signupButton;
    @FXML private Hyperlink loginLink;

    private Model model;
    private Stage stage;

    public SignupController(Stage stage, Model model) {
        this.stage = stage;
        this.model = model;
    }

    @FXML
    public void initialize() {
        signupButton.setOnAction(event -> handleSignup());
        loginLink.setOnAction(event -> showLoginView());
    }
    //handles user signup checks input, validates data, and creates a new account
    private void handleSignup() {
        String fullName = fullNameField.getText().trim();
        String username = usernameField.getText().trim();
        String email = emailField.getText().trim();
        String password = passwordField.getText();
        String confirmPassword = confirmPasswordField.getText();

        if (fullName.isEmpty() || username.isEmpty() || email.isEmpty() ||
                password.isEmpty() || confirmPassword.isEmpty()) {
            showError("All fields are required");
            return;
        }

        if (!ValidationUtil.validateEmail(email)) {
            showError("Invalid email format");
            return;
        }

        if (!password.equals(confirmPassword)) {
            showError("Passwords do not match");
            return;
        }

        if (!PasswordUtil.validatePassword(password)) {
            showError("Password must be at least 8 characters long, include uppercase, lowercase, numbers, and special characters (!@#$, etc.)");
            return;
        }

        try {
            if (model.getUserDao().usernameExists(username)) {
                showError("Username already exists");
                return;
            }
            //Hash password and create user
            String hashedPassword = PasswordUtil.hashPassword(password);
            User user = model.getUserDao().createUser(fullName, username, email, hashedPassword);

            if (user != null) {
                showSuccess("Account created successfully! Please login.");
                clearFields();
            } else {
                showError("Failed to create account");
            }
        } catch (Exception e) {
            showError("Signup error: " + e.getMessage());
        }
    }
        //Opens the login page
    private void showLoginView() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/login.fxml"));
            LoginController controller = new LoginController(stage, model);
            loader.setController(controller);
            Parent root = loader.load();

            controller.showStage(root);
        } catch (Exception e) {
            showError("Error loading login page: " + e.getMessage());
        }
    }

    private void showError(String message) {
        messageLabel.setText(message);
        messageLabel.setStyle("-fx-text-fill: #ef4444;");
    }

    private void showSuccess(String message) {
        messageLabel.setText(message);
        messageLabel.setStyle("-fx-text-fill: #10b981;");
    }
    //Clears all form fields
    private void clearFields() {
        fullNameField.clear();
        usernameField.clear();
        emailField.clear();
        passwordField.clear();
        confirmPasswordField.clear();
    }
}
