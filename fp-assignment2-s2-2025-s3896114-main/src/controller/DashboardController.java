package controller;

import model.*;
import util.DateUtil;
import util.ValidationUtil;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import java.io.FileWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class DashboardController {
    @FXML
    private Label welcomeLabel;
    @FXML
    private TableView<Project> projectTable;
    @FXML
    private TableColumn<Project, String> titleColumn;
    @FXML
    private TableColumn<Project, String> locationColumn;
    @FXML
    private TableColumn<Project, String> dayColumn;
    @FXML
    private TableColumn<Project, Integer> hourlyValueColumn;
    @FXML
    private TableColumn<Project, Integer> availableSlotsColumn;
    @FXML
    private ListView<CartItem> cartListView;
    @FXML
    private Label cartTotalLabel;
    @FXML
    private Button addToCartButton;
    @FXML
    private Button removeFromCartButton;
    @FXML
    private Button confirmButton;
    @FXML
    private Button viewHistoryButton;
    @FXML
    private Button changePasswordButton;
    @FXML
    private Button logoutButton;

    private Model model;
    private Stage stage;

    public DashboardController(Stage stage, Model model) {
        this.stage = stage;
        this.model = model;
    }

    @FXML
    public void initialize() {
        setupWelcomeLabel();
        setupProjectTable();
        setupCartListView();
        loadProjects();
        updateCartTotal();

        addToCartButton.setOnAction(event -> handleAddToCart());
        removeFromCartButton.setOnAction(event -> handleRemoveFromCart());
        confirmButton.setOnAction(event -> handleConfirmRegistration());
        viewHistoryButton.setOnAction(event -> showHistoryView());
        changePasswordButton.setOnAction(event -> showPasswordUpdateDialog());
        logoutButton.setOnAction(event -> handleLogout());
    }

    private void setupWelcomeLabel() {
        welcomeLabel.setText("Welcome, " + model.getCurrentUser().getUsername() + "!");
    }
    // Connects project table columns to Project fields.
    private void setupProjectTable() {
        titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        locationColumn.setCellValueFactory(new PropertyValueFactory<>("location"));
        dayColumn.setCellValueFactory(new PropertyValueFactory<>("day"));
        hourlyValueColumn.setCellValueFactory(new PropertyValueFactory<>("hourlyValue"));
        availableSlotsColumn.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleIntegerProperty(cellData.getValue().getAvailableSlots()).asObject());
    }
//Sets up the cart view and keeps the total updated.
    private void setupCartListView() {
        cartListView.setItems(model.getCart());
        cartListView.setCellFactory(param -> new ListCell<CartItem>() {
            @Override
            protected void updateItem(CartItem item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(String.format("%s (%s) - %d slots × %d hrs = $%d",
                            item.getProject().getTitle(),
                            item.getProject().getDay(),
                            item.getSlots(),
                            item.getHoursPerSlot(),
                            item.getTotalContributions()));
                }
            }
        });

        model.getCart().addListener((javafx.collections.ListChangeListener<CartItem>) c -> updateCartTotal());
    }
//Loads available projects into the table
    private void loadProjects() {
        try {
            List<Project> projects = model.getProjectDao().getEnabledProjects();
            List<String> availableDays = DateUtil.getAvailableDays();

            ObservableList<Project> availableProjects = FXCollections.observableArrayList();
            for (Project project : projects) {
                if (availableDays.contains(project.getDay()) && project.getAvailableSlots() > 0) {
                    availableProjects.add(project);
                }
            }

            projectTable.setItems(availableProjects);
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to load projects: " + e.getMessage());
        }
    }
//Adds a selected project to the cart
    private void handleAddToCart() {
        Project selectedProject = projectTable.getSelectionModel().getSelectedItem();
        if (selectedProject == null) {
            showAlert(Alert.AlertType.WARNING, "No Selection", "Please select a project to add to cart");
            return;
        }

        if (!DateUtil.canRegisterForDay(selectedProject.getDay())) {
            showAlert(Alert.AlertType.ERROR, "Invalid Date",
                    "Cannot register for projects on " + selectedProject.getDay() + " (already passed this week)");
            return;
        }

        Dialog<CartItem> dialog = createAddToCartDialog(selectedProject);
        dialog.showAndWait().ifPresent(cartItem -> {
            for (CartItem existing : model.getCart()) {
                if (existing.getProject().getId() == selectedProject.getId()) {
                    showAlert(Alert.AlertType.WARNING, "Already in Cart", "This project is already in your cart");
                    return;
                }
            }

            int totalSlotsInCart = model.getCart().stream()
                    .filter(item -> item.getProject().getId() == selectedProject.getId())
                    .mapToInt(CartItem::getSlots)
                    .sum();

            if (totalSlotsInCart + cartItem.getSlots() > 3) {
                showAlert(Alert.AlertType.ERROR, "Slot Limit", "Maximum 3 slots per project");
                return;
            }

            model.getCart().add(cartItem);
        });
    }
//Creates the pop-up dialog to choose slots/hours before adding to cart.
    private Dialog<CartItem> createAddToCartDialog(Project project) {
        Dialog<CartItem> dialog = new Dialog<>();
        dialog.setTitle("Add to Cart");
        dialog.setHeaderText("Configure registration for: " + project.getTitle());

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));

        Spinner<Integer> slotsSpinner = new Spinner<>(1, Math.min(3, project.getAvailableSlots()), 1);
        Spinner<Integer> hoursSpinner = new Spinner<>(1, 3, 1);

        grid.add(new Label("Number of slots:"), 0, 0);
        grid.add(slotsSpinner, 1, 0);
        grid.add(new Label("Hours per slot:"), 0, 1);
        grid.add(hoursSpinner, 1, 1);

        Label contributionLabel = new Label();
        updateContributionLabel(contributionLabel, project, slotsSpinner.getValue(), hoursSpinner.getValue());

        slotsSpinner.valueProperty().addListener((obs, old, newVal) ->
                updateContributionLabel(contributionLabel, project, newVal, hoursSpinner.getValue()));
        hoursSpinner.valueProperty().addListener((obs, old, newVal) ->
                updateContributionLabel(contributionLabel, project, slotsSpinner.getValue(), newVal));

        grid.add(new Label("Total contribution:"), 0, 2);
        grid.add(contributionLabel, 1, 2);

        dialog.getDialogPane().setContent(grid);

        ButtonType addButtonType = new ButtonType("Add", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(addButtonType, ButtonType.CANCEL);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == addButtonType) {
                return new CartItem(project, slotsSpinner.getValue(), hoursSpinner.getValue());
            }
            return null;
        });

        return dialog;
    }
//Updates the total contribution label in the add-to-cart dialog
    private void updateContributionLabel(Label label, Project project, int slots, int hours) {
        int total = project.getHourlyValue() * slots * hours;
        label.setText("$" + total);
    }
//Removes an item from the cart
    private void handleRemoveFromCart() {
        CartItem selectedItem = cartListView.getSelectionModel().getSelectedItem();
        if (selectedItem == null) {
            showAlert(Alert.AlertType.WARNING, "No Selection", "Please select an item to remove");
            return;
        }
        model.getCart().remove(selectedItem);
    }
//Confirms and saves all registrations from the cart
    private void handleConfirmRegistration() {
        if (model.getCart().isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Empty Cart", "Please add projects to cart before confirming");
            return;
        }

        TextInputDialog codeDialog = new TextInputDialog();
        codeDialog.setTitle("Confirmation");
        codeDialog.setHeaderText("Enter 6-digit confirmation code");
        codeDialog.setContentText("Code:");

        codeDialog.showAndWait().ifPresent(code -> {
            if (!ValidationUtil.validateConfirmationCode(code)) {
                showAlert(Alert.AlertType.ERROR, "Invalid Code", "Please enter a valid 6-digit code");
                return;
            }

            try {
                int totalContribution = 0;
                LocalDateTime now = LocalDateTime.now();
                int nextRegNumber = model.getRegistrationDao().getNextRegistrationNumber();

                for (CartItem item : model.getCart()) {
                    String regId = String.format("%04d", nextRegNumber++);
                    int contribution = item.getTotalContributions();

                    Registration registration = new Registration(
                            model.getCurrentUser().getId(),
                            item.getProject().getId(),
                            item.getSlots(),
                            item.getHoursPerSlot(),
                            contribution
                    );
                    registration.setRegistrationId(regId);
                    registration.setConfirmedAt(now);

                    model.getRegistrationDao().addRegistration(registration);
                    model.getProjectDao().updateProjectSlots(item.getProject().getId(), item.getSlots());

                    totalContribution += contribution;
                }

                model.clearCart();
                loadProjects();

                showAlert(Alert.AlertType.INFORMATION, "Success",
                        "Registration confirmed!\nTotal contribution value: $" + totalContribution);

            } catch (Exception e) {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to confirm registration: " + e.getMessage());
            }
        });
    }
//Opens the participation history page
    private void showHistoryView() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/history.fxml"));
            HistoryController controller = new HistoryController(stage, model);
            loader.setController(controller);
            Parent root = loader.load();

            Scene scene = new Scene(root, 900, 600);
            stage.setScene(scene);
            stage.setTitle("VolunTrack - Participation History");
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to load history: " + e.getMessage());
        }
    }
//Opens a dialog for changing the user’s password
    private void showPasswordUpdateDialog() {
        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle("Change Password");
        dialog.setHeaderText("Update your password");

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));

        PasswordField oldPasswordField = new PasswordField();
        PasswordField newPasswordField = new PasswordField();
        PasswordField confirmPasswordField = new PasswordField();

        grid.add(new Label("Current Password:"), 0, 0);
        grid.add(oldPasswordField, 1, 0);
        grid.add(new Label("New Password:"), 0, 1);
        grid.add(newPasswordField, 1, 1);
        grid.add(new Label("Confirm Password:"), 0, 2);
        grid.add(confirmPasswordField, 1, 2);

        dialog.getDialogPane().setContent(grid);

        ButtonType updateButtonType = new ButtonType("Update", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(updateButtonType, ButtonType.CANCEL);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == updateButtonType) {
                return newPasswordField.getText();
            }
            return null;
        });

        dialog.showAndWait().ifPresent(newPassword -> {
            String oldPassword = oldPasswordField.getText();
            String confirmPassword = confirmPasswordField.getText();

            if (oldPassword.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty()) {
                showAlert(Alert.AlertType.ERROR, "Error", "All fields are required");
                return;
            }

            if (!newPassword.equals(confirmPassword)) {
                showAlert(Alert.AlertType.ERROR, "Error", "New passwords do not match");
                return;
            }

            if (!util.PasswordUtil.validatePassword(newPassword)) {
                showAlert(Alert.AlertType.ERROR, "Error",
                        "Password must be at least 8 characters long, include uppercase, lowercase, numbers, and special characters");
                return;
            }

            try {
                String hashedOldPassword = util.PasswordUtil.hashPassword(oldPassword);
                if (!hashedOldPassword.equals(model.getCurrentUser().getPassword())) {
                    showAlert(Alert.AlertType.ERROR, "Error", "Current password is incorrect");
                    return;
                }

                String hashedNewPassword = util.PasswordUtil.hashPassword(newPassword);
                model.getUserDao().updatePassword(model.getCurrentUser().getId(), hashedNewPassword);
                model.getCurrentUser().setPassword(hashedNewPassword);

                showAlert(Alert.AlertType.INFORMATION, "Success", "Password updated successfully");
            } catch (Exception e) {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to update password: " + e.getMessage());
            }
        });
    }
//Logs the user out and goes back to the login screen
    private void handleLogout() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/login.fxml"));
            LoginController controller = new LoginController(stage, model);
            loader.setController(controller);
            Parent root = loader.load();

            controller.showStage(root);
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to logout: " + e.getMessage());
        }
    }
//Updates the total contribution value in the cart
    private void updateCartTotal() {
        int total = model.getCart().stream()
                .mapToInt(CartItem::getTotalContributions)
                .sum();
        cartTotalLabel.setText("Total: $" + total);
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}

