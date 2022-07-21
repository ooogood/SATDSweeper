package fx.satds_fx.model;

import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;

import java.nio.file.Path;

public class Comment {
	private long id;
	private CheckBox mark;
	private String content;
	private String location;

	private String author = "";
	private String date = "";
	private String keyword;
	private PriorityBox priority;
	private TextField estimate;
	private Path path;
	private int lineNum;
	public Comment( Long i, String cont, Path pt, int ln, String kw ) {
		id = i;
		mark = new CheckBox();
		content = cont;
		path = pt;
		lineNum = ln;
		keyword = kw;
		priority = new PriorityBox();
		estimate = new TextField();

		mark.setSelected( true );
		location = path.getFileName().toString() + ":" + lineNum;
	}
	// id getter method
	public long getId() {
		return this.id;
	}
	// id setter method
	public void setId( long id ) {
		this.id = id;
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
	// author getter method
	public String getAuthor() {
		return this.author;
	}
	// author setter method
	public void setAuthor( String author ) {
		this.author = author;
	}
	// date getter method
	public String getDate() {
		return this.date;
	}
	// date setter method
	public void setDate( String date ) {
		this.date = date;
	}
	// keyword getter method
	public String getKeyword() {
		return this.keyword;
	}
	// keyword setter method
	public void setKeyword( String keyword ) {
		this.keyword = keyword;
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
	// path getter method
	public Path getPath() {
		return this.path;
	}
	// path setter method
	public void setPath( Path path ) {
		this.path = path;
	}
	// lineNum getter method
	public int getLineNum() {
		return this.lineNum;
	}
	// lineNum setter method
	public void setLineNum( int lineNum ) {
		this.lineNum = lineNum;
	}
}
