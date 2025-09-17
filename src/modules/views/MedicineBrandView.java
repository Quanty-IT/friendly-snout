package modules.views;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import modules.controllers.MedicineBrandController;
import modules.models.MedicineBrand;
import config.Database;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

public class MedicineBrandView extends VBox {

    private TableView<MedicineBrand> table;
    private ObservableList<MedicineBrand> brandsList;
    private Connection conn;
    private MedicineBrandController controller;

    public MedicineBrandView() {
        try {
            this.conn = Database.getConnection();
            this.controller = new MedicineBrandController(conn);
        } catch (SQLException e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Erro ao conectar com o banco de dados.").showAndWait();
            return;
        }

        initializeComponents();
        setupLayout();
        loadData();
    }

    private void initializeComponents() {
        table = new TableView<>();
        brandsList = FXCollections.observableArrayList();
        table.setItems(brandsList);

        TableColumn<MedicineBrand, Integer> idColumn = new TableColumn<>("ID");
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        idColumn.setPrefWidth(50);

        TableColumn<MedicineBrand, String> nameColumn = new TableColumn<>("Nome");
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        nameColumn.setPrefWidth(200);

        TableColumn<MedicineBrand, LocalDateTime> createdAtColumn = new TableColumn<>("Data Criação");
        createdAtColumn.setCellValueFactory(new PropertyValueFactory<>("createdAt"));
        createdAtColumn.setPrefWidth(150);
        createdAtColumn.setCellFactory(column -> new TableCell<MedicineBrand, LocalDateTime>() {
            private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

            @Override
            protected void updateItem(LocalDateTime item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(formatter.format(item));
                }
            }
        });

        table.getColumns().addAll(idColumn, nameColumn, createdAtColumn);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }

    private void setupLayout() {
        Button addButton = new Button("Adicionar Marca");
        addButton.setOnAction(e -> openAddForm());

        Button editButton = new Button("Editar");
        editButton.setOnAction(e -> editSelected());

        Button deleteButton = new Button("Excluir");
        deleteButton.setOnAction(e -> deleteSelected());

        Button refreshButton = new Button("Atualizar");
        refreshButton.setOnAction(e -> loadData());

        HBox buttonBox = new HBox(10);
        buttonBox.getChildren().addAll(addButton, editButton, deleteButton, refreshButton);
        buttonBox.setPadding(new Insets(10, 0, 10, 0));

        this.setPadding(new Insets(20));
        this.setSpacing(10);
        this.getChildren().addAll(
                new Label("Marcas de Remédio"),
                buttonBox,
                table
        );

        this.setPrefSize(500, 400);
    }

    private void loadData() {
        try {
            brandsList.clear();
            brandsList.addAll(controller.listAll());
        } catch (SQLException e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Erro ao carregar marcas: " + e.getMessage()).showAndWait();
        }
    }

    private void openAddForm() {
        Stage formStage = new Stage();
        formStage.setTitle("Adicionar Marca de Remédio");
        formStage.initModality(Modality.APPLICATION_MODAL);

        MedicineBrandForm form = new MedicineBrandForm();
        Scene scene = new Scene(form, 400, 200);

        formStage.setScene(scene);
        formStage.setResizable(false);

        formStage.setOnHidden(e -> loadData());

        formStage.showAndWait();
    }

    private void editSelected() {
        MedicineBrand selected = table.getSelectionModel().getSelectedItem();
        if (selected == null) {
            new Alert(Alert.AlertType.WARNING, "Selecione uma marca para editar.").showAndWait();
            return;
        }

        TextInputDialog dialog = new TextInputDialog(selected.getName());
        dialog.setTitle("Editar Marca");
        dialog.setHeaderText("Editar nome da marca:");
        dialog.setContentText("Nome:");

        Optional<String> result = dialog.showAndWait();
        if (result.isPresent() && !result.get().trim().isEmpty()) {
            try {
                controller.update(selected.getId(), result.get().trim());
                new Alert(Alert.AlertType.INFORMATION, "Marca editada com sucesso!").showAndWait();
                loadData();
            } catch (SQLException e) {
                e.printStackTrace();
                new Alert(Alert.AlertType.ERROR, "Erro ao editar marca: " + e.getMessage()).showAndWait();
            }
        }
    }

    private void deleteSelected() {
        MedicineBrand selected = table.getSelectionModel().getSelectedItem();
        if (selected == null) {
            new Alert(Alert.AlertType.WARNING, "Selecione uma marca para excluir.").showAndWait();
            return;
        }

        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Confirmar Exclusão");
        confirmAlert.setHeaderText("Excluir marca?");
        confirmAlert.setContentText("Deseja realmente excluir a marca '" + selected.getName() + "'?");

        Optional<ButtonType> result = confirmAlert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                controller.delete(selected.getId());
                new Alert(Alert.AlertType.INFORMATION, "Marca excluída com sucesso!").showAndWait();
                loadData();
            } catch (SQLException e) {
                e.printStackTrace();
                new Alert(Alert.AlertType.ERROR, "Erro ao excluir marca: " + e.getMessage()).showAndWait();
            }
        }
    }
}