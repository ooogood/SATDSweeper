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
			CommentDB db = Model.getInst().getDB();
			// *** test data *** //
			for( int i = 0; i < 50; ++i ) {
				db.insert("// this is a comment hahahahahahahahahahahahahahahahahahahahahahaahhahah", 
						  "Main.java:L123", "01/01/1999" );
			}
			// test data end //
			Thread.currentThread().sleep( 500 );
		} catch( Exception e ) { e.printStackTrace(); }
		listener.onAnalysingEnd();
	}
}
