package fx.satds_fx;
import java.util.ArrayList;

public class KwList {
	ArrayList<String> list;
	private KwList instance;
	private KwList() {
		list = new ArrayList<>();
	}
	public KwList getInst() {
		return instance;
	}

}
