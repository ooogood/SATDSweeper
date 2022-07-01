package fx.satds_fx;

import java.util.HashMap;

public class CommentDB {
	private long id_root = 0;
	HashMap<Long, Comment> db;
	public CommentDB() {
		db = new HashMap<>();
	}
	public long size() {
		return db.size();
	}
	public void insert( String content, String location, String date ) {
		db.put(id_root, new Comment( id_root, content, location, date));
		++id_root;
	}
	public Comment get( long id ) {
		return db.get( id );
	}
	// TODO: complete db functions.
}
