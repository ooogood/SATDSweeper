package fx.satds_fx;

import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;

public class Comment {
	private CheckBox mark;
	private String content;
	private String location;
	private String date;
	private PriorityBox priority;
	private TextField estimate;
	public Comment( String cont, String loc, String dt ) {
		mark = new CheckBox();
		content = new String( cont );
		location = new String( loc );
		date = new String( dt );
		priority = new PriorityBox();
		estimate = new TextField();

		mark.setSelected( true );
	}
	// mark getter method
	public CheckBox getMark() {
		return this.mark;
	}
	// mark setter method
	public void setMark( CheckBox mark ) {
		this.mark = mark;
	}
	// content getter method
	public String getContent() {
		return this.content;
	}
	// content setter method
	public void setContent( String content ) {
		this.content = content;
	}
	// location getter method
	public String getLocation() {
		return this.location;
	}
	// location setter method
	public void setLocation( String location ) {
		this.location = location;
	}
	// date getter method
	public String getDate() {
		return this.date;
	}
	// date setter method
	public void setDate( String date ) {
		this.date = date;
	}
	// priority getter method
	public PriorityBox getPriority() {
		return this.priority;
	}
	// priority setter method
	public void setPriority( PriorityBox priority ) {
		this.priority = priority;
	}
	// estTime getter method
	public TextField getEstimate() {
		return this.estimate;
	}
	// estTime setter method
	public void setEstimate( TextField estimate ) {
		this.estimate = estimate;
	}
}
