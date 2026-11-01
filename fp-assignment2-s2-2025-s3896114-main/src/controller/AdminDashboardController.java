package controller;

import model.Model;
import model.Project;
import model.Registration;
import util.ValidationUtil;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class AdminDashboardController {
    @FXML private Label welcomeLabel;
    @FXML private TableView<Project> projectTable;
    @FXML private TableColumn<Project, String> titleColumn;
    @FXML private TableColumn<Project, String> locationColumn;
    @FXML private TableColumn<Project, String> dayColumn;
    @FXML private TableColumn<Project, Integer> hourlyValueColumn;
    @FXML private TableColumn<Project, Integer> registeredSlotsColumn;
    @FXML private TableColumn<Project, Integer> totalSlotsColumn;
    @FXML private TableColumn<Project, String> statusColumn;
    @FXML private Button addProjectButton;
    @FXML private Button modifyProjectButton;
    @FXML private Button toggleStatusButton;
    @FXML private Button viewAllRegistrationsButton;
    @FXML private Button logoutButton;

    private Model model;
    private Stage stage;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public AdminDashboardController(Stage stage, Model model) {
        this.stage = stage;
        this.model = model;
    }

    //this call the dash is loaded and set button and table
    @FXML
    public void initialize() {
        welcomeLabel.setText("Welcome, Administrator!");
        setupProjectTable();
        loadProjects();

        addProjectButton.setOnAction(event -> handleAddProject());
        modifyProjectButton.setOnAction(event -> handleModifyProject());
        toggleStatusButton.setOnAction(event -> handleToggleStatus());
        viewAllRegistrationsButton.setOnAction(event -> handleViewAllRegistrations());
        logoutButton.setOnAction(event -> handleLogout());
    }
// connects table to the project fields
    private void setupProjectTable() {
        titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        locationColumn.setCellValueFactory(new PropertyValueFactory<>("location"));
        dayColumn.setCellValueFactory(new PropertyValueFactory<>("day"));
        hourlyValueColumn.setCellValueFactory(new PropertyValueFactory<>("hourlyValue"));
        registeredSlotsColumn.setCellValueFactory(new PropertyValueFactory<>("registeredSlots"));
        totalSlotsColumn.setCellValueFactory(new PropertyValueFactory<>("totalSlots"));
        statusColumn.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleStringProperty(
                        cellData.getValue().isEnabled() ? "Enabled" : "Disabled"));
    }

    // load the projects from Database
    private void loadProjects() {
        try {
            List<Project> projects = model.getProjectDao().getAllProjects();
            projectTable.setItems(FXCollections.observableArrayList(projects));
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to load projects: " + e.getMessage());
        }
    }
// check if project is duplicates
    private void handleAddProject() {
        Dialog<Project> dialog = createProjectDialog(null);
        dialog.setTitle("Add New Project");
        dialog.setHeaderText("Enter project details");

        dialog.showAndWait().ifPresent(project -> {
            try {
                if (model.getProjectDao().projectExists(project.getTitle(),
                        project.getLocation(), project.getDay())) {
                    showAlert(Alert.AlertType.ERROR, "Duplicate",
                            "A project with the same title, location, and day already exists");
                    return;
                }

                model.getProjectDao().addProject(project);
                loadProjects();
                showAlert(Alert.AlertType.INFORMATION, "Success", "Project added successfully");
            } catch (Exception e) {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to add project: " + e.getMessage());
            }
        });
    }
// allow admin to change the info of project
    private void handleModifyProject() {
        Project selectedProject = projectTable.getSelectionModel().getSelectedItem();
        if (selectedProject == null) {
            showAlert(Alert.AlertType.WARNING, "No Selection", "Please select a project to modify");
            return;
        }

        Dialog<Project> dialog = createProjectDialog(selectedProject);
        dialog.setTitle("Modify Project");
        dialog.setHeaderText("Update project details");

        dialog.showAndWait().ifPresent(updatedProject -> {
            try {
                List<Project> allProjects = model.getProjectDao().getAllProjects();
                for (Project p : allProjects) {
                    if (p.getId() != selectedProject.getId() &&
                            p.getTitle().equals(updatedProject.getTitle()) &&
                            p.getLocation().equals(updatedProject.getLocation()) &&
                            p.getDay().equals(updatedProject.getDay())) {
                        showAlert(Alert.AlertType.ERROR, "Duplicate",
                                "Another project with these details already exists");
                        return;
                    }
                }

                updatedProject.setId(selectedProject.getId());
                updatedProject.setRegisteredSlots(selectedProject.getRegisteredSlots());
                model.getProjectDao().updateProject(updatedProject);
                loadProjects();
                showAlert(Alert.AlertType.INFORMATION, "Success", "Project updated successfully");
            } catch (Exception e) {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to update project: " + e.getMessage());
            }
        });
    }
//make the form to add or edited project
    private Dialog<Project> createProjectDialog(Project existingProject) {
        Dialog<Project> dialog = new Dialog<>();
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));

        TextField titleField = new TextField();
        TextField locationField = new TextField();
        ComboBox<String> dayComboBox = new ComboBox<>();
        dayComboBox.setItems(FXCollections.observableArrayList(
                "Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"));
        Spinner<Integer> hourlyValueSpinner = new Spinner<>(1, 100, 25);
        Spinner<Integer> totalSlotsSpinner = new Spinner<>(1, 100, 50);

        // fill in data if editing
        if (existingProject != null) {
            titleField.setText(existingProject.getTitle());
            locationField.setText(existingProject.getLocation());
            dayComboBox.setValue(existingProject.getDay());
            hourlyValueSpinner.getValueFactory().setValue(existingProject.getHourlyValue());
            totalSlotsSpinner.getValueFactory().setValue(existingProject.getTotalSlots());
        } else {
            dayComboBox.setValue("Mon");
        }
//add fields to form
        grid.add(new Label("Title:"), 0, 0);
        grid.add(titleField, 1, 0);
        grid.add(new Label("Location:"), 0, 1);
        grid.add(locationField, 1, 1);
        grid.add(new Label("Day:"), 0, 2);
        grid.add(dayComboBox, 1, 2);
        grid.add(new Label("Hourly Value ($):"), 0, 3);
        grid.add(hourlyValueSpinner, 1, 3);
        grid.add(new Label("Total Slots:"), 0, 4);
        grid.add(totalSlotsSpinner, 1, 4);

        dialog.getDialogPane().setContent(grid);

        ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);
        //When Save is clicked, check input and create a Project
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                String title = titleField.getText().trim();
                String location = locationField.getText().trim();
                String day = dayComboBox.getValue();
                int hourlyValue = hourlyValueSpinner.getValue();
                int totalSlots = totalSlotsSpinner.getValue();
// input checks
                if (!ValidationUtil.validateProjectTitle(title)) {
                    showAlert(Alert.AlertType.ERROR, "Invalid Input",
                            "Title must be non-empty and at most 30 characters");
                    return null;
                }

                if (!ValidationUtil.validateProjectLocation(location)) {
                    showAlert(Alert.AlertType.ERROR, "Invalid Input",
                            "Location must be non-empty and at most 30 characters");
                    return null;
                }

                if (!ValidationUtil.validateDay(day)) {
                    showAlert(Alert.AlertType.ERROR, "Invalid Input", "Invalid day selected");
                    return null;
                }

                if (!ValidationUtil.validateHourlyValue(hourlyValue)) {
                    showAlert(Alert.AlertType.ERROR, "Invalid Input",
                            "Hourly value must be between 1 and 100");
                    return null;
                }

                if (!ValidationUtil.validateTotalSlots(totalSlots)) {
                    showAlert(Alert.AlertType.ERROR, "Invalid Input",
                            "Total slots must be between 1 and 100");
                    return null;
                }

                Project project = new Project();
                project.setTitle(title);
                project.setLocation(location);
                project.setDay(day);
                project.setHourlyValue(hourlyValue);
                project.setRegisteredSlots(existingProject != null ? existingProject.getRegisteredSlots() : 0);
                project.setTotalSlots(totalSlots);
                project.setEnabled(true);

                return project;
            }
            return null;
        });

        return dialog;
    }
//Turns a project enabled/disabled
    private void handleToggleStatus() {
        Project selectedProject = projectTable.getSelectionModel().getSelectedItem();
        if (selectedProject == null) {
            showAlert(Alert.AlertType.WARNING, "No Selection",
                    "Please select a project to enable/disable");
            return;
        }

        try {
            boolean newStatus = !selectedProject.isEnabled();
            model.getProjectDao().toggleProjectStatus(selectedProject.getId(), newStatus);
            loadProjects();

            String statusText = newStatus ? "enabled" : "disabled";
            showAlert(Alert.AlertType.INFORMATION, "Success",
                    "Project " + statusText + " successfully");
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error",
                    "Failed to toggle project status: " + e.getMessage());
        }
    }
//Shows a window with all volunteer registrations
    private void handleViewAllRegistrations() {
        try {
            List<Registration> allRegistrations = model.getRegistrationDao().getAllRegistrations();

            TableView<Registration> table = new TableView<>();
            table.setItems(FXCollections.observableArrayList(allRegistrations));

            TableColumn<Registration, String> regIdCol = new TableColumn<>("Reg ID");
            regIdCol.setCellValueFactory(new PropertyValueFactory<>("registrationId"));

            TableColumn<Registration, String> titleCol = new TableColumn<>("Project");
            titleCol.setCellValueFactory(new PropertyValueFactory<>("projectTitle"));

            TableColumn<Registration, String> locationCol = new TableColumn<>("Location");
            locationCol.setCellValueFactory(new PropertyValueFactory<>("projectLocation"));

            TableColumn<Registration, String> dayCol = new TableColumn<>("Day");
            dayCol.setCellValueFactory(new PropertyValueFactory<>("projectDay"));

            TableColumn<Registration, Integer> slotsCol = new TableColumn<>("Slots");
            slotsCol.setCellValueFactory(new PropertyValueFactory<>("slots"));

            TableColumn<Registration, Integer> hoursCol = new TableColumn<>("Hours");
            hoursCol.setCellValueFactory(new PropertyValueFactory<>("hoursPerSlot"));

            TableColumn<Registration, Integer> contributionCol = new TableColumn<>("Contribution");
            contributionCol.setCellValueFactory(new PropertyValueFactory<>("totalContribution"));

            TableColumn<Registration, String> dateCol = new TableColumn<>("Confirmed At");
            dateCol.setCellValueFactory(cellData ->
                    new javafx.beans.property.SimpleStringProperty(
                            cellData.getValue().getConfirmedAt().format(DATE_FORMATTER)));

            table.getColumns().addAll(regIdCol, titleCol, locationCol, dayCol,
                    slotsCol, hoursCol, contributionCol, dateCol);

            Stage dialogStage = new Stage();
            dialogStage.setTitle("All Registrations");
            dialogStage.setScene(new Scene(table, 1000, 600));
            dialogStage.show();

        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error",
                    "Failed to load registrations: " + e.getMessage());
        }
    }
//Logs out and returns to the login screen.
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

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
