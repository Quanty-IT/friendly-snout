package modules.views;

import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import modules.controllers.MedicineBrandController;
import modules.models.MedicineBrand;
import config.Database;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.UUID;

public class MedicineBrandForm extends GridPane {

    private TextField nameField;
    private Connection conn;

    public MedicineBrandForm() {
        this.setVgap(10);
        this.setHgap(10);
        this.setPadding(new Insets(20, 20, 20, 20));

        Label nameLabel = new Label("Nome da Marca:");
        nameField = new TextField();
        nameField.setPrefWidth(240);

        try {
            conn = Database.getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        Button saveButton = new Button("Salvar");
        saveButton.setOnAction(e -> saveBrand());

        this.add(nameLabel, 0, 0);
        this.add(nameField, 1, 0);
        this.add(saveButton, 1, 1);
    }

    private void saveBrand() {
        String name = nameField.getText().trim();
        if (name.isEmpty()) {
            new Alert(Alert.AlertType.WARNING, "Digite um nome!").showAndWait();
            return;
        }
        try {
            MedicineBrandController controller = new MedicineBrandController(conn);
            controller.insert(name);
            new Alert(Alert.AlertType.INFORMATION, "Marca cadastrada!").showAndWait();
            nameField.clear();
        } catch (SQLException e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Erro ao salvar a marca.").showAndWait();
        }
    }
}
