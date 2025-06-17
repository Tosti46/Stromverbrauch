package zaehlerstand;

import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.List;

public class VerbrauchZusammenfassung {

    public void zeigeFenster() {
        Stage stage = new Stage();
        stage.setTitle("Gesamtübersicht");

        List<VerbrauchEintrag> eintraege = VerbrauchSpeicher.ladeEinträge();

        int gesamtVerbrauch = 0;
        int gesamtKosten = 0;

        for (VerbrauchEintrag e : eintraege) {
            gesamtVerbrauch += e.verbrauch;
            gesamtKosten += e.kosten;
        }

        Label verbrauchLabel = new Label("Gesamtverbrauch: " + gesamtVerbrauch + " kWh");
        Label kostenLabel = new Label("Gesamtkosten: " + gesamtKosten + " €");

        VBox box = new VBox(10, verbrauchLabel, kostenLabel);
        box.setStyle("-fx-padding: 20;");
        Scene scene = new Scene(box, 300, 120);
        stage.setScene(scene);
        stage.show();
    }
}