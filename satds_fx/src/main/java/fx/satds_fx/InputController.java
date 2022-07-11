package fx.satds_fx;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.cell.TextFieldListCell;
import java.net.URL;
import java.util.ResourceBundle;
import java.io.File;
import java.io.IOException;

public class InputController implements Initializable {
    @FXML
    private TextField tarSrcPath;
    @FXML
    private ListView<String> keywordList;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        keywordList.setEditable( true );
        keywordList.setCellFactory(TextFieldListCell.forListView() );
    }

    /* target source code path */
    @FXML
    protected void onBrowseTarSrcButtonClick() {
        // get directory path for target source code
        DirectoryChooser dc = new DirectoryChooser();
        File selectedDirectory = dc.showDialog( Main.getPrimeStage() );
        if( selectedDirectory != null )
            tarSrcPath.setText( selectedDirectory.getAbsolutePath() );
    }

    /* keyword list callback */
    @FXML
    protected void onAddKeywordButtonClick() {
        keywordList.getItems().add( "TODO" );
    }
    @FXML
    protected void onRmvKeywordButtonClick() {
        int idx = keywordList.getSelectionModel().getSelectedIndex();
        if( idx >= 0 && idx < keywordList.getItems().size() ) {
            // remove selected item
            keywordList.getItems().remove( idx );
        }
    }
    /* next */
    @FXML
    protected void onAnalyseButtonClick() throws IOException {
        // todo: generate keyword list and send to Model

        // send target path to analyser
        Analyser analyser = Model.getInst().getAnalyser();
        analyser.setTargetPath( tarSrcPath.getText() );
        analyser.setKeywordList( keywordList.getItems() );
        // switch to next scene
        Stage primary = Main.getPrimeStage();
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("analysing.fxml"));
        Scene scene = new Scene( fxmlLoader.load() );
        primary.setScene(scene);
        primary.show();
    }
}