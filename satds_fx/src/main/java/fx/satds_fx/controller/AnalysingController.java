package fx.satds_fx.controller;

import fx.satds_fx.model.Analyser;
import fx.satds_fx.Main;
import fx.satds_fx.model.Model;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;

import java.net.URL;
import java.util.ResourceBundle;
import java.io.IOException;

public class AnalysingController implements Initializable {

    @FXML
    private Button next;

    @FXML
    private Label finishSign;

    @FXML
    private Label progressSign;

    @FXML
    private ImageView loading;

    private Thread analyser_thread;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        finishSign.setVisible( false );
        loading.setVisible( true );
        progressSign.setVisible( true );
        next.setDisable( true );
        // run analysation task in another thread
        Analyser analyser = Model.getInst().getAnalyser();
        analyser.setProgressListener( this );
        analyser_thread = new Thread( analyser );
        analyser_thread.start();
    }

    public void initScannerEnd() {
        Platform.runLater( ()->{
            progressSign.setText("parsing comments...");
        });
    }
    public void parsingCommentsEnd() {
        Platform.runLater( ()->{
            progressSign.setText("classifying comments...");
        });
    }

    public void classifyingEnd() {
        Platform.runLater( ()->{
            progressSign.setText("blaming comments...");
        });
    }

    public void onAnalysingEnd() {
        loading.setVisible( false );
        progressSign.setVisible( false );
        finishSign.setVisible( true );
        next.setDisable( false );
    }

    /* next */
    @FXML
    protected void onNextButtonClick() throws IOException {
        // switch to next scene
        Stage primary = Main.getPrimeStage();
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("manage.fxml"));
        Scene scene = new Scene( fxmlLoader.load() );
        // relocate the stage because the next scene is bigger
        primary.setX(50.0);
        primary.setY(50.0);

        primary.setScene(scene);
        primary.show();
    }
}