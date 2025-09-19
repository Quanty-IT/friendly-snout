package modules.views;

import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import modules.controllers.MedicineBrandController;
import config.Database;

import java.sql.Connection;
import java.sql.SQLException;

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

        Button cancelButton = new Button("Cancelar");
        cancelButton.setOnAction(e -> closeWindow());

        this.add(nameLabel, 0, 0);
        this.add(nameField, 1, 0);
        this.add(saveButton, 1, 1);
        this.add(cancelButton, 0, 1);
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
            new Alert(Alert.AlertType.INFORMATION, "Marca cadastrada com sucesso!").showAndWait();
            closeWindow();
        } catch (SQLException e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Erro ao salvar a marca: " + e.getMessage()).showAndWait();
        }
    }

    private void closeWindow() {
        Stage stage = (Stage) this.getScene().getWindow();
        stage.close();
    }
}