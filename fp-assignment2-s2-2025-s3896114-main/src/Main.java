import controller.LoginController;
import model.Model;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

public class Main extends Application {
    private Model model;

    @Override
    public void init() {
        model = Model.getInstance();
    }

    @Override
    public void start(Stage primaryStage) {
        try {
            model.setup();

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/login.fxml"));
            LoginController controller = new LoginController(primaryStage, model);
            loader.setController(controller);
            Parent root = loader.load();

            controller.showStage(root);
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Application Error");
            alert.setContentText("Failed to start application: " + e.getMessage());
            alert.showAndWait();
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
