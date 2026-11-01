package controller;

import model.Model;
import model.Registration;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import java.io.FileWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class HistoryController {
    @FXML private TableView<Registration> historyTable;
    @FXML private TableColumn<Registration, String> regIdColumn;
    @FXML private TableColumn<Registration, String> titleColumn;
    @FXML private TableColumn<Registration, String> locationColumn;
    @FXML private TableColumn<Registration, String> dayColumn;
    @FXML private TableColumn<Registration, Integer> slotsColumn;
    @FXML private TableColumn<Registration, Integer> hoursColumn;
    @FXML private TableColumn<Registration, Integer> contributionColumn;
    @FXML private TableColumn<Registration, String> dateColumn;
    @FXML private Button exportButton;
    @FXML private Button backButton;

    private Model model;
    private Stage stage;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public HistoryController(Stage stage, Model model) {
        this.stage = stage;
        this.model = model;
    }

    @FXML
    public void initialize() {
        setupTableColumns();
        loadHistory();

        exportButton.setOnAction(event -> handleExport());
        backButton.setOnAction(event -> showDashboard());
    }
//Connects table columns to Registration fields
    private void setupTableColumns() {
        regIdColumn.setCellValueFactory(new PropertyValueFactory<>("registrationId"));
        titleColumn.setCellValueFactory(new PropertyValueFactory<>("projectTitle"));
        locationColumn.setCellValueFactory(new PropertyValueFactory<>("projectLocation"));
        dayColumn.setCellValueFactory(new PropertyValueFactory<>("projectDay"));
        slotsColumn.setCellValueFactory(new PropertyValueFactory<>("slots"));
        hoursColumn.setCellValueFactory(new PropertyValueFactory<>("hoursPerSlot"));
        contributionColumn.setCellValueFactory(new PropertyValueFactory<>("totalContribution"));
        dateColumn.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleStringProperty(
                        cellData.getValue().getConfirmedAt().format(DATE_FORMATTER)));
    }
//Loads the user’s past registrations into the table
    private void loadHistory() {
        try {
            List<Registration> registrations = model.getRegistrationDao()
                    .getRegistrationsByUserId(model.getCurrentUser().getId());
            historyTable.setItems(FXCollections.observableArrayList(registrations));
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to load history: " + e.getMessage());
        }
    }
//Exports the user’s registration history to a text file
    private void handleExport() {
        try {
            String filename = "history_" + model.getCurrentUser().getUsername() + ".txt";
            FileWriter writer = new FileWriter(filename);
// Header info
            writer.write("=================================================\n");
            writer.write("PARTICIPATION HISTORY\n");
            writer.write("User: " + model.getCurrentUser().getFullName() + " (" +
                    model.getCurrentUser().getUsername() + ")\n");
            writer.write("Export Date: " + LocalDateTime.now().format(DATE_FORMATTER) + "\n");
            writer.write("=================================================\n\n");
// Registration details
            List<Registration> registrations = model.getRegistrationDao()
                    .getRegistrationsByUserId(model.getCurrentUser().getId());

            for (Registration reg : registrations) {
                writer.write("Registration ID: " + reg.getRegistrationId() + "\n");
                writer.write("Project: " + reg.getProjectTitle() + "\n");
                writer.write("Location: " + reg.getProjectLocation() + "\n");
                writer.write("Day: " + reg.getProjectDay() + "\n");
                writer.write("Slots: " + reg.getSlots() + "\n");
                writer.write("Hours per Slot: " + reg.getHoursPerSlot() + "\n");
                writer.write("Total Contribution: $" + reg.getTotalContribution() + "\n");
                writer.write("Confirmed At: " + reg.getConfirmedAt().format(DATE_FORMATTER) + "\n");
                writer.write("-------------------------------------------------\n\n");
            }

            writer.close();

            showAlert(Alert.AlertType.INFORMATION, "Success",
                    "History exported to " + filename);
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to export history: " + e.getMessage());
        }
    }
//Returns to the main dashboard screen
    private void showDashboard() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/dashboard.fxml"));
            DashboardController controller = new DashboardController(stage, model);
            loader.setController(controller);
            Parent root = loader.load();

            Scene scene = new Scene(root, 1000, 700);
            stage.setScene(scene);
            stage.setTitle("VolunTrack - Dashboard");
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to return to dashboard: " + e.getMessage());
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
