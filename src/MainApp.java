import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import modules.views.LoginForm;
import modules.models.User;

public class MainApp extends Application {
    @Override
    public void start(Stage stage) {
        stage.setTitle("Pata Amiga");

        LoginForm login = new LoginForm((User u) -> {
            stage.getScene().setRoot(new StackPane(new Label("Logado como: " + u.getEmail())));
            stage.sizeToScene();
        });

        stage.setScene(new Scene(login, 420, 220));
        stage.show();
    }
    public static void main(String[] args) { launch(args); }
}
