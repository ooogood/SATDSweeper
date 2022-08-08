package fx.satds_fx.model;

import java.io.File;
import java.nio.file.attribute.FileTime;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;


import classifier.trainer.Trainer;
import commentprocessor.marker.CommentMarkerParser;
import commentprocessor.scanner.LocalRepoScanner;
import commentprocessor.scanner.RemoteRepoScanner;
import commentprocessor.scanner.Scanner;
import commentprocessor.scanner.CommentStore;
import commentprocessor.marker.CommentElement;
import fx.satds_fx.controller.AnalysingController;
import fx.satds_fx.model.Comment;
import fx.satds_fx.model.CommentDB;
import fx.satds_fx.model.Model;


public class Analyser implements Runnable {
	
	private AnalysingController listener;
	private String targetPath;
	private String branch = "master";
	// sourceType 0: local file, 1: github remote
	private int sourceType = 0;
	private List<String> keywords;
	private	Scanner scanner = null;
	public Analyser() {
	}

	public void run() {
		initScanner();
		listener.initScannerEnd();

		readAllComments();
		listener.parsingCommentsEnd();

		classifyUnmarkedComments();
		listener.classifyingEnd();

		scanner.blameAllComments( Model.getInst().getDB() );
		listener.onAnalysingEnd();
	}

	public void setProgressListener( AnalysingController ac ) {
		listener = ac;
	}

	public void setKeywordList( List<String> list ) {
		// keywords are case-insensitive
		keywords = new LinkedList<>();
		for( String kw : list ) {
			keywords.add( kw.toLowerCase() );
		}
	}

	public void setTargetPath( String path ) {
		targetPath = path;
		// this is where we decide if it is a github repo
		if( targetPath.startsWith("http") && targetPath.endsWith(".git") )
			sourceType = 1;
		else {
			sourceType = 0;
			targetPath = path.replace("/", File.separator);
		}
	}
	public void setBranch( String bch ) {
		branch = bch;
	}
	protected void initScanner() {
		if( sourceType == 1 )
			scanner = new RemoteRepoScanner( targetPath, branch, keywords );
		else
			scanner = new LocalRepoScanner( targetPath, branch, keywords );
	}


	protected void readAllComments() {
		// extract comments
		CommentStore cmtStore = scanner.parse();

		// store all comments into db
		CommentDB db = Model.getInst().getDB();
		Map<String, LinkedHashSet<CommentElement>> comments = cmtStore.getComments();
		for( String key : comments.keySet() ) {
			for( CommentElement ce : comments.get(key) ) {
				// filter out empty comments
				if( ce.getValue().equals("") ) continue;

				// put a comment into db
				db.insert( key,
						ce.getValue(),
						ce.getPath(),
						ce.getRange().begin.line );

			}
		}
	}

	// classify comments with no marker
	protected void classifyUnmarkedComments() {
		CommentDB db = Model.getInst().getDB();
		Set<Comment> unmarked = db.getKeywordGroup(CommentMarkerParser.DEFAULT_MARKER);
		if( unmarked == null ) return;
		
		List<Comment> commentList = new ArrayList<>( unmarked );
		try {
			List<Long> tobeRemove = Trainer.classify( commentList );
//			List<Long> tobeRemove = Trainer.classifyWithOneClassifier( commentList );
			for( Long i : tobeRemove ) {
				db.remove( commentList.get( i.intValue() ).getId() );
			}
		} catch (Exception e ) {e.printStackTrace();}
	}

	private static final DateTimeFormatter DATE_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy/MM/dd");
	private String formatDateTime(FileTime fileTime) {

        LocalDateTime localDateTime = fileTime
                .toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();

        return localDateTime.format(DATE_FORMATTER);
    }

}
