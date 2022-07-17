package fx.satds_fx;

import classifier.trainer.Trainer;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
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
        stage.getIcons().add( new Image(System.getProperty("user.dir") +"\\satds_fx\\src\\main\\resources\\fx\\satds_fx\\Icon.png"));
        primary = stage;
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("input.fxml"));
        Scene scene = new Scene( fxmlLoader.load() );
        primary.setScene(scene);
        primary.show();
    }

    public static void main(String[] args) {

//        try {
//            Trainer.retrain();
//        } catch ( Exception e ) {e.printStackTrace();}

        launch();
    }
}