package classifier.domain;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import classifier.util.WordSplit;


public class Document implements Comparable<Document>{
	
	private String content;
	private List<String> words;
	
	private String project,label;
	
	double scoreForRank;
	
	public int compareTo(Document doc){
		if(this.scoreForRank<doc.scoreForRank)
			return -1;
		else if(this.scoreForRank>doc.scoreForRank)
			return 1;
		else return 0;
	}

	public Document(String content){
		this.content = content;
		// tokenisation
		this.words=WordSplit.split(content);
		

	}

	public List<String> getWords() {
		return words;
	}

	public String getContent() {
		return content;
	}


	public String getProject() {
		return project;
	}

	public void setProject(String project) {
		this.project = project;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}


	
	

}
