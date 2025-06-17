package zaehlerstand;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.stage.Stage;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.util.Callback;

import javafx.scene.control.TableCell;

import java.util.List;

public class VerbrauchTabelle {

    public void zeigeFenster() {
        Stage stage = new Stage();
        stage.setTitle("Verbrauchsübersicht");

        TableView<VerbrauchEintrag> table = new TableView<>();
        List<VerbrauchEintrag> original = VerbrauchSpeicher.ladeEinträge();
        ObservableList<VerbrauchEintrag> berechnet = berechneVerbrauch(original);
        table.setItems(berechnet);

        TableColumn<VerbrauchEintrag, String> colMonat = createZentrierteSpalte("Monat", data ->
                new SimpleStringProperty(data.getValue().monat));
        TableColumn<VerbrauchEintrag, String> colStand = createZentrierteSpalte("Zählerstand", data ->
                new SimpleStringProperty(String.valueOf(data.getValue().zaehlerstand)));
        TableColumn<VerbrauchEintrag, String> colVerbrauch = createZentrierteSpalte("Verbrauch (kWh)", data ->
                new SimpleStringProperty(String.valueOf(data.getValue().verbrauch)));
        TableColumn<VerbrauchEintrag, String> colKosten = createZentrierteSpalte("Kosten (€)", data ->
                new SimpleStringProperty(String.valueOf(data.getValue().kosten)));
        TableColumn<VerbrauchEintrag, String> colProTag = createZentrierteSpalte("Verbrauch / Tag", data ->
                new SimpleStringProperty(String.valueOf(data.getValue().verbrauchProTag)));
        TableColumn<VerbrauchEintrag, String> colAbschlag = createZentrierteSpalte("Abweichung zu 330 €", data -> {
            int diff = data.getValue().kosten - 330;
            return new SimpleStringProperty((diff >= 0 ? "+" : "") + diff + " €");
        });

        table.getColumns().addAll(colMonat, colStand, colVerbrauch, colKosten, colProTag, colAbschlag);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        Button löschenButton = new Button("Eintrag löschen");
        löschenButton.setOnAction(_ -> {
            VerbrauchEintrag eintrag = table.getSelectionModel().getSelectedItem();
            if (eintrag != null && bestätigung("Eintrag wirklich löschen?")) {
                if (VerbrauchSpeicher.loescheEintrag(eintrag)) {
                    table.getItems().remove(eintrag);
                }
            }
        });

        Button zusammenfassungButton = new Button("Zusammenfassung");
        zusammenfassungButton.setOnAction(_ -> new VerbrauchZusammenfassung().zeigeFenster());

        Button schließenButton = new Button("Schließen");
        schließenButton.getStyleClass().add("red-close-button");
        schließenButton.setOnAction(_ -> stage.close());

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox unten = new HBox(10, löschenButton, zusammenfassungButton, spacer, schließenButton);
        unten.setPadding(new Insets(10));
        unten.setStyle("-fx-background-color: #f0f0f0;");

        BorderPane root = new BorderPane(table, null, null, unten, null);
        Scene scene = new Scene(root, 800, 450);
        scene.getStylesheets().add(getClass().getResource("style.css").toExternalForm());
        stage.setScene(scene);
        stage.show();
    }

    private TableColumn<VerbrauchEintrag, String> createZentrierteSpalte(
            String titel,
            Callback<TableColumn.CellDataFeatures<VerbrauchEintrag, String>, ObservableValue<String>> callback) {

        TableColumn<VerbrauchEintrag, String> col = new TableColumn<>(titel);
        col.setCellValueFactory(callback);
        col.setCellFactory(tc -> {
            TableCell<VerbrauchEintrag, String> cell = new TableCell<>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    setText(empty ? null : item);
                    setStyle("-fx-alignment: CENTER;");
                }
            };
            return cell;
        });
        return col;
    }

    private ObservableList<VerbrauchEintrag> berechneVerbrauch(List<VerbrauchEintrag> liste) {
        ObservableList<VerbrauchEintrag> result = FXCollections.observableArrayList();
        VerbrauchEintrag vorheriger = null;

        for (VerbrauchEintrag e : liste) {
            if (vorheriger == null) {
                e.verbrauch = 0;
                e.kosten = 0;
                e.verbrauchProTag = 0;
            } else {
                int diff = e.zaehlerstand - vorheriger.zaehlerstand;
                e.verbrauch = Math.max(0, diff);
                double preis = e.strompreis > 0 ? e.strompreis : 0.38; // Fallback bei fehlendem Preis
                e.kosten = (int) Math.round(e.verbrauch * preis);
                e.verbrauchProTag = e.verbrauch / 30;
            }
            result.add(e);
            vorheriger = e;
        }

        return result;
    }

    private boolean bestätigung(String nachricht) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, nachricht, ButtonType.YES, ButtonType.NO);
        alert.setHeaderText("Sicher?");
        alert.setTitle("Bestätigung");
        alert.showAndWait();
        return alert.getResult() == ButtonType.YES;
    }
}