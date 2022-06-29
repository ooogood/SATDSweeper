package fx.satds_fx;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class Main extends Application {
    private static Stage primary;
    @Override
    public void start(Stage stage) throws IOException {
        primary = stage;
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("input.fxml"));
        Scene scene = new Scene( fxmlLoader.load() );
        stage.setTitle("SATDSweeper");
        stage.setScene(scene);
        stage.show();
    }

    public static Stage getPrimeStage() {
        return primary;
    }

    public static void main(String[] args) {
        launch();
    }
}