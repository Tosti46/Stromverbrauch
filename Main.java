package zaehlerstand;

import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {
    @Override
    public void start(@SuppressWarnings("exports") Stage primaryStage) {
    	new EingabeFenster().zeigeFenster();
    }

    public static void main(String[] args) {
        launch(args);
    }
}