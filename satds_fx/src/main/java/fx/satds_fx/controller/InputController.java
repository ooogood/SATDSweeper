package fx.satds_fx.controller;

import fx.satds_fx.model.Analyser;
import fx.satds_fx.Main;
import fx.satds_fx.model.Model;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.cell.TextFieldListCell;
import java.net.URL;
import java.nio.file.Files;
import java.util.ResourceBundle;
import java.io.File;
import java.io.IOException;

public class InputController implements Initializable {
    @FXML
    private TextField tarSrcPath;
    @FXML
    private TextField branchName;
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

    /* target source code path */
    @FXML
    protected void onBrowseKeyButtonClick() {
        // get directory path for target source code
        FileChooser fc = new FileChooser();
        fc.setTitle("Select keyword list");
        fc.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("CSV Files", "*.csv") );
        File selectedDirectory = fc.showOpenDialog(Main.getPrimeStage());
        try {
            String rawKeywordList = Files.readString(selectedDirectory.toPath());
            String[] keywords = rawKeywordList.split( ",");
            for( String k : keywords ) {
                keywordList.getItems().add( k );
            }
        } catch (Exception e ) {
            // do nothing
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
        analyser.setBranch( branchName.getText() );
        // switch to next scene
        Stage primary = Main.getPrimeStage();
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("analysing.fxml"));
        Scene scene = new Scene( fxmlLoader.load() );
        primary.setScene(scene);
        primary.show();
    }
}