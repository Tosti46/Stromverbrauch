package zaehlerstand;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.util.stream.IntStream;

public class EingabeFenster {

    public void zeigeFenster() {
        Stage stage = new Stage();
        stage.setTitle("Zählerstand erfassen");

        // Zählerstand
        TextField feldZählerstand = new TextField();
        feldZählerstand.setPromptText("Zählerstand");
        beschränkeAufInteger(feldZählerstand);
        feldZählerstand.setMaxWidth(200);
        feldZählerstand.setAlignment(Pos.CENTER_RIGHT);

        Label labelKwh = new Label("kWh");
        HBox zaehlerBox = new HBox(5, feldZählerstand, labelKwh);
        zaehlerBox.setAlignment(Pos.CENTER);

        // Monat + Jahr ComboBox
        ComboBox<String> monatsBox = new ComboBox<>();
        monatsBox.getItems().addAll(
                "Januar", "Februar", "März", "April", "Mai", "Juni",
                "Juli", "August", "September", "Oktober", "November", "Dezember"
        );
        monatsBox.setValue("Januar");

        ComboBox<String> jahresBox = new ComboBox<>();
        IntStream.range(2020, 2041).forEach(j -> jahresBox.getItems().add(String.valueOf(j)));
        jahresBox.setValue("2025");

        zentriereComboBoxInhalt(monatsBox);
        zentriereComboBoxInhalt(jahresBox);
        monatsBox.setPrefWidth(130);
        jahresBox.setPrefWidth(130);

        HBox datumBox = new HBox(10, monatsBox, jahresBox);
        datumBox.setAlignment(Pos.CENTER);

        // Strompreis
        Label stromLabel = new Label("Preis pro kWh in Cent:");
        stromLabel.setAlignment(Pos.CENTER);
        TextField feldStrompreis = new TextField("38");
        feldStrompreis.setMaxWidth(200);
        feldStrompreis.setAlignment(Pos.CENTER_RIGHT);
        beschränkeAufInteger(feldStrompreis);
        Label labelCent = new Label("Cent");

        HBox preisBox = new HBox(5, feldStrompreis, labelCent);
        preisBox.setAlignment(Pos.CENTER);

        // Buttons
        Button speichern = new Button("Speichern");
        Button anzeigen = new Button("Verbrauch anzeigen");
        speichern.getStyleClass().add("glanz-button");
        anzeigen.getStyleClass().add("glanz-button");
        speichern.setPrefWidth(130);
        anzeigen.setPrefWidth(130);

        Button schließen = new Button("Schließen");
        schließen.getStyleClass().add("red-close-button");
        schließen.setPrefWidth(270);
        schließen.setOnAction(_ -> stage.close());

        speichern.setOnAction(_ -> {
            try {
                int stand = Integer.parseInt(feldZählerstand.getText());
                int monat = monatsBox.getSelectionModel().getSelectedIndex() + 1;
                int jahr = Integer.parseInt(jahresBox.getValue());
                int preisCent = Integer.parseInt(feldStrompreis.getText());
                double strompreisEuro = preisCent / 100.0;

                LocalDate datum = LocalDate.of(jahr, monat, 1);
                VerbrauchSpeicher.speichernEintrag(stand, datum, strompreisEuro);

                feldZählerstand.clear();
                feldStrompreis.setText("38");
            } catch (Exception e) {
                zeigeFehler("Bitte gültige Zahlen eingeben.");
            }
        });

        anzeigen.setOnAction(_ -> new VerbrauchTabelle().zeigeFenster());

        HBox buttonBox = new HBox(15, speichern, anzeigen);
        buttonBox.setAlignment(Pos.CENTER);

        VBox buttonBox1 = new VBox(10, buttonBox, schließen);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox1.setAlignment(Pos.CENTER);
        buttonBox1.setPadding(new Insets(10));

        // Gesamtlayout
        VBox vbox = new VBox(10,
                createZentriertesLabel("Zählerstand erfassen:"),
                zaehlerBox,
                createZentriertesLabel("Monat und Jahr:"),
                datumBox,
                createZentriertesLabel("Preis pro kWh in Cent:"),
                preisBox,
                buttonBox1
        );
        vbox.setPadding(new Insets(20));
        vbox.setAlignment(Pos.CENTER);

        Scene scene = new Scene(vbox, 300, 350);
        scene.getStylesheets().add(getClass().getResource("style.css").toExternalForm());
        stage.setScene(scene);
        stage.show();
    }

    private Label createZentriertesLabel(String text) {
        Label label = new Label(text);
        label.setMaxWidth(Double.MAX_VALUE);
        label.setAlignment(Pos.CENTER);
        return label;
    }

    private void beschränkeAufInteger(TextField feld) {
        feld.textProperty().addListener((obs, alt, neu) -> {
            if (!neu.matches("\\d*")) {
                feld.setText(neu.replaceAll("[^\\d]", ""));
            }
        });
    }

    private void zeigeFehler(String nachricht) {
        Alert alert = new Alert(Alert.AlertType.ERROR, nachricht, ButtonType.OK);
        alert.setHeaderText("Eingabefehler");
        alert.showAndWait();
    }

    private void zentriereComboBoxInhalt(ComboBox<String> comboBox) {
        comboBox.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item);
                setAlignment(Pos.CENTER);
            }
        });

        comboBox.setCellFactory(cb -> new ListCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item);
                setAlignment(Pos.CENTER);
            }
        });
    }
}