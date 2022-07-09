package fx.satds_fx;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;


import classifier.trainer.Trainer;
import commentparser.configuration.Configuration;
import commentparser.configuration.CommentMarkerConfiguration;
import commentparser.marker.CommentMarkerParser;
import commentparser.scanner.Scanner;
import commentparser.scanner.CommentStore;
import commentparser.marker.CommentElement;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.blame.BlameResult;
import org.eclipse.jgit.diff.RawTextComparator;
import org.eclipse.jgit.lib.PersonIdent;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;


public class Analyser implements Runnable {
	
	private AnalysingController listener;
	private String targetPath;
	private Git git = null;
	private SimpleDateFormat dateFormat = new SimpleDateFormat( "yyyy/MM/dd" );
	private List<String> keywords;
	public Analyser() {
	}

	public void run() {
		git = null;

		tryOpenGit();

		readAllComments( targetPath );

		classifyUnmarkedComments();

		// notify analysation end
		listener.onAnalysingEnd();
	}

	public void setEndListener( AnalysingController ac ) {
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
		targetPath = path.replace("/", File.separator);
	}

	protected void readAllComments( String path ) {
		CommentDB db = Model.getInst().getDB();

		// configure marker mechanism
		CommentMarkerConfiguration commentMarkerConfiguration = new CommentMarkerConfiguration()
							.toBuilder()
							.addContains( keywords )
							.includeWithoutMarker(true)
							.build();
		// build overall configuration
		Configuration config = new Configuration()
							.toBuilder()
							.baseDirs(Arrays.asList( path ))
							.sourceRoots(Arrays.asList(System.getProperty("user.dir")))
							.commentMarkerConfiguration(commentMarkerConfiguration)
							.build();
		Scanner scanner = new Scanner( config );
		try {
			// extract comments
			CommentStore cmtStore = scanner.parse();
			Map<String, LinkedHashSet<CommentElement>> comments;
			comments = cmtStore.getComments();
			// store blame result to save blaming time
			// <path to file, blame result>
			Map<Path, BlameResult> fileBlameMap = new LinkedHashMap<>();
			for( String key : comments.keySet() ) {
				for( CommentElement ce : comments.get(key) ) {
					// filter out empty comments
					if( ce.getValue().equals("") ) continue;
					Path filePath = ce.getPath();
					String date = "";
					String author = "";
					int lineNum = ce.getRange().begin.line;
					/* blame */
					if( git != null ) {
						BlameResult br = null;
						if (fileBlameMap.containsKey(filePath))
							br = fileBlameMap.get(filePath);
						else {
							String relativePath = filePath.toString().replace(
									targetPath+"\\", "" );
							br = git.blame().setFilePath( relativePath )
									.setTextComparator(RawTextComparator.WS_IGNORE_ALL)
									.call();
							fileBlameMap.put( filePath, br );
						}
						// extract info from blame result
						PersonIdent person = br.getSourceAuthor( lineNum );
						author = person.getName();
						date = dateFormat.format(person.getWhen());
					}
					/* ***** */
					// put into db
					db.insert( key,
							ce.getValue(),
							filePath.getFileName().toString() + ":" + lineNum,
							author,
							date );

				}
			}
		} catch( IOException | GitAPIException e ) { e.printStackTrace(); }
	}

	// classify comments with no marker
	protected void classifyUnmarkedComments() {
		Set<Comment> unmarked = Model.getInst().getDB().getKeywordGroup(CommentMarkerParser.DEFAUL_MARKER);
		if( unmarked == null ) return;
		
		List<Comment> commentList = new ArrayList<>( );
		CommentDB db = Model.getInst().getDB();
		try {
			List<Long> tobeRemove = Trainer.classify( commentList );
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

	protected void tryOpenGit() {
		git = null;
		File repo = new File( targetPath + "\\.git" );
		if( repo.isDirectory() ) {
			try {
				git = new Git(new FileRepositoryBuilder()
						.setGitDir( repo )
						.build());
			} catch (IOException e) {
				// do nothing, repo is null
				System.out.println("This path does not have .git folder.");
			}
		}
	}

}
