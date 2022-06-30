package fx.satds_fx;

/** Singleton Model */
public class Model {
	private static Model instance = new Model();
	private Analyser analyser = new Analyser();
	private Model() {
	}
	public static Model getInst() {
		return instance;
	}
	public Analyser getAnalyser() {
		return analyser;
	}

}
