package fx.satds_fx;

/** Singleton Model */
public class Model {
	private static Model instance = new Model();
	private Analyser analyser = new Analyser();
	private CommentDB db = new CommentDB();
	private String errorMessage = "Successful!";
	private boolean isError = false;
	private Model() {
	}
	public static Model getInst() {
		return instance;
	}
	public Analyser getAnalyser() {
		return analyser;
	}
	public CommentDB getDB() {
		return db;
	}
	public String getErrorMessage() { return errorMessage; }
	public void setErrorMessage( String str, boolean b ) { errorMessage = str; isError = b; }
	public boolean getIsError() { return isError; }

}
