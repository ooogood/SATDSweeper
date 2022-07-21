package fx.satds_fx.model;

import javafx.collections.FXCollections;
import javafx.scene.control.ComboBox;

public class PriorityBox extends ComboBox<Character> {
	public PriorityBox() {
		super( FXCollections.observableArrayList( ' ', 'M', 'S', 'C', 'W' ) );
		this.getSelectionModel().selectFirst();
	}
}
