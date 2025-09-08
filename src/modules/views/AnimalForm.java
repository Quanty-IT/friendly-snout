package modules.views;

import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import modules.controllers.AnimalController;
import modules.models.Animal;
import config.Database;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class AnimalForm extends GridPane {

    private TextField nameField;
    private ComboBox<String> sexComboBox;
    private ComboBox<String> speciesComboBox;
    private ComboBox<String> breedComboBox;
    private ComboBox<String> sizeComboBox;
    private ComboBox<String> colorComboBox;
    private DatePicker birthdateField;
    private TextField microchipField;
    private TextField rgaField;
    private ComboBox<String> fivComboBox;
    private ComboBox<String> felvComboBox;
    private ComboBox<String> statusComboBox;
    private TextArea notesField;

    private Connection conn;

    public AnimalForm() {
        nameField = new TextField();

        sexComboBox = new ComboBox<>();
        speciesComboBox = new ComboBox<>();
        breedComboBox = new ComboBox<>();
        sizeComboBox = new ComboBox<>();
        colorComboBox = new ComboBox<>();
        fivComboBox = new ComboBox<>();
        felvComboBox = new ComboBox<>();
        statusComboBox = new ComboBox<>();

        sexComboBox.getItems().addAll("Macho", "Fêmea");
        speciesComboBox.getItems().addAll("Cachorro", "Gato");
        breedComboBox.getItems().addAll(
                "S.R.D",
                "Shih-tzu",
                "Yorkshire Terrier",
                "Spitz Alemão",
                "Buldogue Francês",
                "Poodle",
                "Lhasa Apso",
                "Golden Retriever",
                "Rottweiler",
                "Labrador Retriever",
                "Pug",
                "Pastor Alemão",
                "Border Collie",
                "Chihuahua de Pelo Longo",
                "Pastor Belga Malinois",
                "Maltês"
        );
        sizeComboBox.getItems().addAll("Pequeno", "Médio", "Grande");
        colorComboBox.getItems().addAll("Preto", "Branco", "Cinza", "Marrom", "Dourado", "Creme", "Canela", "Malhado");
        fivComboBox.getItems().addAll("Sim", "Não", "Não testado");
        felvComboBox.getItems().addAll("Sim", "Não", "Não testado");
        statusComboBox.getItems().addAll("Quarentena", "Abrigado", "Adotado", "Perdido");

        birthdateField = new DatePicker();
        microchipField = new TextField();
        rgaField = new TextField();
        notesField = new TextArea();

        try {
            this.conn = Database.getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        Button saveButton = new Button("Salvar");
        saveButton.setOnAction(e -> saveAnimal());

        this.setVgap(10);
        this.setHgap(10);
        this.setPadding(new Insets(20, 20, 20, 20));

        int row = 0;
        this.add(new Label("Nome:"), 0, row); this.add(nameField, 1, row++);
        this.add(new Label("Sexo:"), 0, row); this.add(sexComboBox, 1, row++);
        this.add(new Label("Espécie:"), 0, row); this.add(speciesComboBox, 1, row++);
        this.add(new Label("Raça:"), 0, row); this.add(breedComboBox, 1, row++);
        this.add(new Label("Tamanho:"), 0, row); this.add(sizeComboBox, 1, row++);
        this.add(new Label("Cor:"), 0, row); this.add(colorComboBox, 1, row++);
        this.add(new Label("Nascimento:"), 0, row); this.add(birthdateField, 1, row++);
        this.add(new Label("Microchip:"), 0, row); this.add(microchipField, 1, row++);
        this.add(new Label("RGA:"), 0, row); this.add(rgaField, 1, row++);
        this.add(new Label("FIV:"), 0, row); this.add(fivComboBox, 1, row++);
        this.add(new Label("FeLV:"), 0, row); this.add(felvComboBox, 1, row++);
        this.add(new Label("Status:"), 0, row); this.add(statusComboBox, 1, row++);
        this.add(new Label("Observações:"), 0, row); this.add(notesField, 1, row++);
        this.add(saveButton, 1, row);

        nameField.setPrefWidth(240);
        sexComboBox.setPrefWidth(240);
        speciesComboBox.setPrefWidth(240);
        breedComboBox.setPrefWidth(240);
        sizeComboBox.setPrefWidth(240);
        colorComboBox.setPrefWidth(240);
        microchipField.setPrefWidth(240);
        rgaField.setPrefWidth(240);
        notesField.setPrefWidth(240);
        notesField.setPrefHeight(100);

        this.setPrefSize(520, 560);
    }

    private void saveAnimal() {
        String name = nameField.getText();
        String sex = sexComboBox.getValue() != null ? convertSex(sexComboBox.getValue()) : null;
        String species = speciesComboBox.getValue() != null ? convertSpecies(speciesComboBox.getValue()) : null;
        String breed = breedComboBox.getValue() != null ? convertBreed(breedComboBox.getValue()) : null;
        String size = sizeComboBox.getValue() != null ? convertSize(sizeComboBox.getValue()) : null;
        String color = colorComboBox.getValue() != null ? convertColor(colorComboBox.getValue()) : null;
        LocalDate birthdate = birthdateField.getValue();
        String microchip = microchipField.getText();
        String rga = rgaField.getText();
        String fiv = fivComboBox.getValue() != null ? convertYesNoNotTested(fivComboBox.getValue()) : null;
        String felv = felvComboBox.getValue() != null ? convertYesNoNotTested(felvComboBox.getValue()) : null;
        String status = statusComboBox.getValue() != null ? convertStatus(statusComboBox.getValue()) : null;
        String notes = notesField.getText();

        Animal a = new Animal(
                null, name, sex, species, breed, size, color, birthdate,
                microchip, rga, fiv, felv, status, notes,
                LocalDateTime.now(), LocalDateTime.now()
        );

        try {
            AnimalController.addAnimal(conn, a);
            System.out.println("Animal cadastrado com sucesso!");
            clearForm();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void clearForm() {
        nameField.clear();
        sexComboBox.getSelectionModel().clearSelection();
        speciesComboBox.getSelectionModel().clearSelection();
        breedComboBox.getSelectionModel().clearSelection();
        sizeComboBox.getSelectionModel().clearSelection();
        colorComboBox.getSelectionModel().clearSelection();
        birthdateField.setValue(null);
        microchipField.clear();
        rgaField.clear();
        fivComboBox.getSelectionModel().clearSelection();
        felvComboBox.getSelectionModel().clearSelection();
        statusComboBox.getSelectionModel().clearSelection();
        notesField.clear();
    }

    private String convertSex(String v) {
        return switch (v) {
            case "Macho" -> "male";
            case "Fêmea" -> "female";
            default -> null;
        };
    }

    private String convertSpecies(String v) {
        return switch (v) {
            case "Cachorro" -> "dog";
            case "Gato" -> "cat";
            default -> null;
        };
    }

    private String convertBreed(String v) {
        return switch (v) {
            case "S.R.D" -> "mixed-breed";
            case "Shih-tzu" -> "shih-tzu";
            case "Yorkshire Terrier" -> "yorkshire-terrier";
            case "Spitz Alemão" -> "german-spitz";
            case "Buldogue Francês" -> "french-bulldog";
            case "Poodle" -> "poodle";
            case "Lhasa Apso" -> "lhasa-apso";
            case "Golden Retriever" -> "golden-retriever";
            case "Rottweiler" -> "rottweiler";
            case "Labrador Retriever" -> "labrador-retriever";
            case "Pug" -> "pug";
            case "Pastor Alemão" -> "german-shepherd";
            case "Border Collie" -> "border-collie";
            case "Chihuahua de Pelo Longo" -> "long-haired-chihuahua";
            case "Pastor Belga Malinois" -> "belgian-malinois";
            case "Maltês" -> "maltese";
            default -> null;
        };
    }

    private String convertSize(String v) {
        return switch (v) {
            case "Pequeno" -> "small";
            case "Médio" -> "medium";
            case "Grande" -> "large";
            default -> null;
        };
    }

    private String convertColor(String v) {
        return switch (v) {
            case "Preto" -> "black";
            case "Branco" -> "white";
            case "Cinza" -> "gray";
            case "Marrom" -> "brown";
            case "Dourado" -> "golden";
            case "Creme" -> "cream";
            case "Canela" -> "tan";
            case "Malhado" -> "speckled";
            default -> null;
        };
    }

    private String convertYesNoNotTested(String v) {
        return switch (v) {
            case "Sim" -> "yes";
            case "Não" -> "no";
            case "Não testado" -> "not-tested";
            default -> null;
        };
    }

    private String convertStatus(String v) {
        return switch (v) {
            case "Quarentena" -> "quarantine";
            case "Abrigado" -> "sheltered";
            case "Adotado" -> "adopted";
            case "Perdido" -> "lost";
            default -> null;
        };
    }
}
