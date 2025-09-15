import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import modules.views.AnimalForm;
import modules.views.LoginForm;
import modules.models.User;

public class MainApp extends Application {

    @Override
    public void start(Stage stage) {
        stage.setTitle("Pata Amiga");

        LoginForm login = new LoginForm((User u) -> {
            AnimalForm animalForm = new AnimalForm();
            stage.getScene().setRoot(animalForm);
            stage.sizeToScene();
        });

        Scene scene = new Scene(login, 420, 220);
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
