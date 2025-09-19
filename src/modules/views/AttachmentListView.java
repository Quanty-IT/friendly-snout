
package modules.views;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import modules.controllers.AttachmentController;
import modules.models.Attachment;
import config.Database;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class AttachmentListView extends VBox {

    private TableView<Attachment> tableView;
    private ObservableList<Attachment> attachmentList;
    private Connection conn;
    private final BorderPane mainLayout;
    private final UUID animalId;

    public AttachmentListView(BorderPane mainLayout, UUID animalId) {
        this.mainLayout = mainLayout;
        this.animalId = animalId;

        try {
            this.conn = Database.getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        tableView = new TableView<>();
        attachmentList = FXCollections.observableArrayList();

        Button addButton = new Button("Adicionar Anexo");
        addButton.setOnAction(e -> {
            this.mainLayout.setCenter(new AttachmentForm(this.mainLayout, this.animalId));
        });

        HBox buttonBox = new HBox(10, addButton);
        buttonBox.setPadding(new Insets(10, 10, 10, 10));

        TableColumn<Attachment, String> urlColumn = new TableColumn<>("URL");
        urlColumn.setCellValueFactory(new PropertyValueFactory<>("url"));

        TableColumn<Attachment, String> descriptionColumn = new TableColumn<>("Descrição");
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));

        TableColumn<Attachment, LocalDateTime> createdAtColumn = new TableColumn<>("Criado em");
        createdAtColumn.setCellValueFactory(new PropertyValueFactory<>("createdAt"));

        TableColumn<Attachment, Void> actionColumn = new TableColumn<>("Ações");
        actionColumn.setCellFactory(param -> new TableCell<>() {
            private final Button deleteButton = new Button("❌");
            private final HBox pane = new HBox(5, deleteButton);

            {
                deleteButton.setOnAction(event -> {
                    Attachment attachment = getTableView().getItems().get(getIndex());
                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Tem certeza que deseja deletar este anexo?", ButtonType.YES, ButtonType.NO);
                    alert.showAndWait().ifPresent(response -> {
                        if (response == ButtonType.YES) {
                            try {
                                AttachmentController.deleteAttachment(conn, attachment.getId());
                                getTableView().getItems().remove(attachment);
                                System.out.println("Anexo deletado com sucesso!");
                            } catch (SQLException e) {
                                System.out.println("Erro ao deletar anexo.");
                                e.printStackTrace();
                            }
                        }
                    });
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(pane);
                }
            }
        });

        tableView.getColumns().addAll(urlColumn, descriptionColumn, createdAtColumn, actionColumn);
        this.getChildren().addAll(buttonBox, tableView);
        loadAttachments();
    }

    private void loadAttachments() {
        try {
            List<Attachment> attachments = AttachmentController.getAttachmentsForAnimal(conn, this.animalId);
            attachmentList.clear();
            attachmentList.addAll(attachments);
            tableView.setItems(attachmentList);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
