package fx.satds_fx;

import commentparser.marker.CommentMarkerParser;

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
	public void insert( String kw, String content, String location, String author, String date ) {
		Comment cm = new Comment( id_root, content, location, author, date, kw);
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
