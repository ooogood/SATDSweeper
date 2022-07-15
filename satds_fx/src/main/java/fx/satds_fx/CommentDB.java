package fx.satds_fx;

import java.nio.file.Path;
import java.util.*;

public class CommentDB {
	private long id_root = 0;
	Map<Long, Comment> db;
	Map<String, LinkedHashSet<Comment>> keywordGroup;
	public CommentDB() {
		db = new TreeMap<>();
		keywordGroup = new TreeMap<>();
	}
	public long size() {
		return db.size();
	}
	public void insert(String kw, String content, Path path, int lineNum ) {
		Comment cm = new Comment( id_root, content, path, lineNum, kw);

		db.put(id_root, cm);
		++id_root;
		// add into keyword group
		if( !keywordGroup.containsKey( kw ) ) {
			keywordGroup.put( kw, new LinkedHashSet<>() );
		}
		keywordGroup.get( kw ).add( cm );
	}
	public Comment get( long id ) {
		if( db.containsKey( id ) )
			return db.get( id );
		else
			return null;
	}
	public void remove( long id ) {
		if( !db.containsKey(id) ) return;
		Comment cm = db.get( id );
		db.remove( id );
		String kw = cm.getKeyword();
		keywordGroup.get( kw ).remove( cm );
	}
	public Set<Comment> getKeywordGroup( String kw ) {
		return keywordGroup.get( kw );
	}
	public Set<String> getKeywordSet() { return keywordGroup.keySet(); }
	// TODO: complete db functions.
}
