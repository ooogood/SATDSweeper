package fx.satds_fx;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.net.URL;
import java.util.ResourceBundle;

import java.io.IOException;

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
    TableColumn<Comment, String> col_date;
    @FXML
    TableColumn<Comment, String> col_priority;
    @FXML
    TableColumn<Comment, String> col_estimate;

    private ReportWriter rw = new ReportWriter();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // set all columns
        col_mark.setCellValueFactory( new PropertyValueFactory<Comment, String>("mark"));
        col_content.setCellValueFactory( new PropertyValueFactory<Comment, String>("content"));
        col_location.setCellValueFactory( new PropertyValueFactory<Comment, String>("location"));
        col_date.setCellValueFactory( new PropertyValueFactory<Comment, String>("date"));
        col_priority.setCellValueFactory( new PropertyValueFactory<Comment, String>("priority"));
        col_estimate.setCellValueFactory( new PropertyValueFactory<Comment, String>("estimate"));

        // *** test data *** //
        for( int i = 0; i < 50; ++i ) {
            table.getItems().add( new Comment("// this is a comment", "Main.java:Ln1", "01/01/1999" ) );
        }
        // test data end //
    }


    /* generate */
    @FXML
    protected void onGenerateButtonClick() throws IOException {
        ObservableList<Comment> comments = table.getItems();
        //* === test data === */
        // for( Comment c : comments ) {
        //     if( c.getMark().isSelected() ) {
        //         System.out.println( c.getContent() );
        //         System.out.print( "Loc.: " + c.getLocation() );
        //         System.out.print( ", Since: " + c.getDate() );
        //         System.out.print( ", Prio.: " + c.getPriority().getValue() );
        //         System.out.print( ", Est. Time: " + c.getEstimate().getText() );
        //         System.out.println( "day(s)" );
        //         System.out.println( "===========" );
        //     }
        // }
        /* test data end */
        // todo: generate pdf report and save to desktop.
        System.out.println( "pdf generated!" );

        // generate pdf
        try {
            // TODO: Save table information to model and call writer to write
            // TODO: prompt user for file save location
            rw.write( "mypdf.pdf" );
        } catch( Exception e ) { e.printStackTrace(); }
    }
}