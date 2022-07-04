package fx.satds_fx;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Map;
import java.util.LinkedHashSet;


import commentparser.configuration.Configuration;
import commentparser.configuration.CommentMarkerConfiguration;
import commentparser.configuration.GroupMarkerConfiguration;
import commentparser.scanner.Scanner;
import commentparser.scanner.CommentStore;
import commentparser.marker.CommentElement;


public class Analyser implements Runnable {
	
	AnalysingController listener;
	String targetPath;
	public Analyser() {
	}

	public void setEndListener( AnalysingController ac ) {
		listener = ac;
	}

	public void setTargetPath( String path ) {
		targetPath = path.replace("/", File.separator);
	}

	public void run() {
		CommentDB db = Model.getInst().getDB();

		// extract comments
		CommentMarkerConfiguration commentMarkerConfiguration = new CommentMarkerConfiguration()
							.toBuilder()
							.includeWithoutMarker(true)
							.includeOnlyWithinGroup(false)
							.includeOnlyWithinMethods(false)
							.build();
		GroupMarkerConfiguration groupMarkerConfiguration = new GroupMarkerConfiguration();
		Configuration config = new Configuration()
							.toBuilder()
							.baseDirs(Arrays.asList(targetPath))
							.sourceRoots(Arrays.asList(System.getProperty("user.dir")))
							.commentMarkerConfiguration(commentMarkerConfiguration)
							.groupMarkerConfiguration(groupMarkerConfiguration)
							.build();
		Scanner scanner = new Scanner( config );
		try {
			CommentStore cmtStore = scanner.parse();
			Map<String, LinkedHashSet<CommentElement>> comments;
			comments = cmtStore.getComments();
			System.out.println( comments );
			// *** test data *** //
			for( int i = 0; i < 50; ++i ) {
				db.insert("// this is a comment hahahahahahahahahahahahahahahahahahahahahahaahhahah",
						  "Main.java:L123", "01/01/1999" );
			}
			// test data end //

		} catch( IOException e ) { e.printStackTrace(); }
		// notify analysation end
		listener.onAnalysingEnd();
	}
}
