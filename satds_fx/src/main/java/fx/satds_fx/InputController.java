package fx.satds_fx;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;
import javafx.scene.control.cell.TextFieldListCell;
import java.net.URL;
import java.util.ResourceBundle;
import java.io.File;

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
    protected void onAnalyseButtonClick() {
        // todo: generate keyword list
        //       switch to analyse

        /* test code */
        System.out.print( "source code path: " );
        System.out.println( tarSrcPath.getText() );
        ObservableList<String> oblist = keywordList.getItems();
        for( int i = 0; i < oblist.size(); ++i ) {
            System.out.println( oblist.get( i ) );
        }
        /* test code end */
    }
}