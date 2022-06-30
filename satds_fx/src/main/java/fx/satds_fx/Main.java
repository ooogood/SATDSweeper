package fx.satds_fx;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class Main extends Application {
    private static Stage primary;
    public static Stage getPrimeStage() {
        return primary;
    }

    @Override
    public void start(Stage stage) throws IOException {
        stage.setTitle("SATDSweeper");
        primary = stage;
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("input.fxml"));
        Scene scene = new Scene( fxmlLoader.load() );
        primary.setScene(scene);
        primary.show();
    }

    public static void main(String[] args) {
        launch();
    }
}