package commentparser.scanner;

import commentparser.marker.CommentElement;
import lombok.Getter;

import java.util.*;
import java.util.stream.Collectors;

@Getter
public class CommentStore {

    //Store comments based on keywords
    private Map<String, LinkedHashSet<CommentElement>> comments = new LinkedHashMap<>();

    public void addComment(String key, CommentElement comment) {
        if (this.comments.containsKey(key)) {
            this.comments.get(key).add(comment);
        } else {
            this.comments.put(key, new LinkedHashSet<>(Arrays.asList(comment)));
        }
    }

    public void addComment(String key, List<CommentElement> comments) {
        if (this.comments.containsKey(key)) {
            this.comments.get(key).addAll(comments);
        } else {
            this.comments.put(key, new LinkedHashSet<>(comments));
        }
    }

    // sort comments by filname and line number
    public void sort() {
        Comparator<CommentElement> commentCompare = Comparator
                .comparing((CommentElement o1) -> o1.getPath().getFileName())
                .thenComparing((CommentElement o1) -> o1.getRange().begin.line);

        comments.entrySet().forEach(stringLinkedHashSetEntry -> {
            comments.put(stringLinkedHashSetEntry.getKey(), stringLinkedHashSetEntry.getValue().stream().sorted(commentCompare)
                    .collect(Collectors.toCollection(LinkedHashSet::new)));
        });

    }

}
