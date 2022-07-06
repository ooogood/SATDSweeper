package fx.satds_fx;

import java.util.HashMap;
import java.util.Map;
import java.util.LinkedHashSet;

public class CommentDB {
	private long id_root = 0;
	Map<Long, Comment> db;
	Map<String, LinkedHashSet<Comment>> keywordGroup;
	public CommentDB() {
		db = new HashMap<>();
		keywordGroup = new HashMap<>();
	}
	public long size() {
		return db.size();
	}
	public void insert( String kw, String content, String location, String date ) {
		Comment cm = new Comment( id_root, content, location, date, kw);
		db.put(id_root, cm);
		++id_root;
		// add into keyword group
		if( !keywordGroup.containsKey( kw ) ) {
			keywordGroup.put( kw, new LinkedHashSet<>() );
		}
		keywordGroup.get( kw ).add( cm );
	}
	public Comment get( long id ) {
		return db.get( id );
	}
	// TODO: complete db functions.
}
