package fx.satds_fx;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.LinkedHashMap;
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
			// store last modified date to save file reading time
			// <file name, last modified date>
			Map<String, String> lastModified = new LinkedHashMap<>();
			for( String key : comments.keySet() ) {
				for( CommentElement ce : comments.get(key) ) {
					// filter out empty comments
					if( !ce.getValue().equals("") ) {
						String fileName = ce.getPath().getFileName().toString();
						String date;
						if( lastModified.containsKey(fileName)) 
							date = lastModified.get( fileName );
						else {
							date = formatDateTime(Files.readAttributes(ce.getPath(), BasicFileAttributes.class).lastModifiedTime());
							lastModified.put( fileName, date );
						}

						db.insert(ce.getValue(),
								fileName + ":" + ce.getLineNum(),
								date );
					}
				}
			}
		} catch( IOException e ) { e.printStackTrace(); }
		// notify analysation end
		listener.onAnalysingEnd();
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
