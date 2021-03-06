package fx.satds_fx.controller;

import fx.satds_fx.*;
import fx.satds_fx.model.Comment;
import fx.satds_fx.model.CommentDB;
import fx.satds_fx.model.Model;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;

import java.io.File;

import java.net.URL;
import java.util.ResourceBundle;

import java.io.IOException;
import java.util.Set;

public class ManageController implements Initializable {

    @FXML
    TableView<Comment> table;
    @FXML
    TableColumn<Comment, String> col_mark;
    @FXML
    TableColumn<Comment, String> col_content;
    @FXML
    TableColumn<Comment, String> col_location;
    @FXML
    TableColumn<Comment, String> col_auth;
    @FXML
    TableColumn<Comment, String> col_date;
    @FXML
    TableColumn<Comment, String> col_keyword;
    @FXML
    TableColumn<Comment, String> col_priority;
    @FXML
    TableColumn<Comment, String> col_estimate;
    @FXML
    Label errorMessage;

    private ReportWriter rw = new ReportWriter();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // set all columns
        col_mark.setCellValueFactory( new PropertyValueFactory<Comment, String>("mark"));
        col_content.setCellValueFactory( new PropertyValueFactory<Comment, String>("content"));
        col_location.setCellValueFactory( new PropertyValueFactory<Comment, String>("location"));
        col_auth.setCellValueFactory( new PropertyValueFactory<Comment, String>("author"));
        col_date.setCellValueFactory( new PropertyValueFactory<Comment, String>("date"));
        col_keyword.setCellValueFactory( new PropertyValueFactory<Comment, String>("keyword"));
        col_priority.setCellValueFactory( new PropertyValueFactory<Comment, String>("priority"));
        col_estimate.setCellValueFactory( new PropertyValueFactory<Comment, String>("estimate"));

        CommentDB db = Model.getInst().getDB();
        Set<String> keywordSet = db.getKeywordSet();
        for( String kw : keywordSet ) {
            Set<Comment> cms = db.getKeywordGroup( kw );
            for( Comment cm : cms ) {
                table.getItems().add( cm );
            }
        }
        // show error message
        setErrorMessage(Model.getInst().getErrorMessage(), Model.getInst().getIsError());
    }
    public void setErrorMessage( String msg, boolean isError ) {
        errorMessage.setText( msg );
        if( isError ) {
            errorMessage.setTextFill(Color.color(1.0,0,0));
        }
        else {
            errorMessage.setTextFill(Color.color(0,1.0,0));
        }
    }

    /* generate */
    @FXML
    protected void onGenerateButtonClick() throws IOException {
        // generate pdf
        try {
            FileChooser fc = new FileChooser();
            fc.getExtensionFilters().add(new ExtensionFilter("PDF Files (*.pdf)", "*.pdf"));
            File selectedDirectory = fc.showSaveDialog( Main.getPrimeStage() );
            if( selectedDirectory == null ) return;
            rw.write( selectedDirectory.getAbsolutePath() );
        } catch( Exception e ) { e.printStackTrace(); }
        System.out.println( "pdf generated!" );
    }
}