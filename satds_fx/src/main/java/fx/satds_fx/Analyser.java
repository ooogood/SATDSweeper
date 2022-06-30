package fx.satds_fx;

public class Analyser implements Runnable {
	
	AnalysingController listener;
	public Analyser() {
	}

	public void setEndListener( AnalysingController ac ) {
		listener = ac;
	}

	public void run() {
		try {
			Thread.currentThread().sleep( 500 );
		} catch( Exception e ) { e.printStackTrace(); }
		listener.onAnalysingEnd();
	}
}
