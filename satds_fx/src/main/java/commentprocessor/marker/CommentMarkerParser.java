package commentprocessor.marker;

import commentprocessor.configuration.Configuration;
import com.github.javaparser.ast.comments.Comment;

import java.util.*;

public class CommentMarkerParser {

    private Configuration configuration;
    private Boolean includeAll = false;

    public static final String DEFAULT_MARKER = "";

    public CommentMarkerParser(Configuration configuration, Boolean includeAll) {
        this.configuration = configuration;
        this.includeAll = includeAll;
    }

    public CommentMarkerParser(Configuration configuration) {
        this.configuration = configuration;
    }

    /**
     * Parse three type of comment (inline comment, block comment, javadoc comment)
     * Search for comment markers with the given contains and regex configuration.
     *
     * @param comment
     * @return Parsed comment
     */
    public CommentElement parse(Comment comment) {
        if (this.isExcluded(comment)) {
            return null;
        }

        CommentElement commentElement;
        String commentText;

        commentText = comment.getContent();
        commentElement = new CommentElement(commentText);
        commentElement.setRange(comment.getRange().orElse(null));
        commentElement.setMarker(DEFAULT_MARKER);

        // process keywords
        if (this.configuration.getCommentMarkerConfiguration().getEnableContains()) {
            commentText = commentText.toLowerCase(); // keywords are case-insensitive
            Optional<String> containMarker = this.configuration.getCommentMarkerConfiguration().getContains().stream().filter(commentText::contains).findFirst();
            if (containMarker.isPresent()) {
                commentElement.setMarker(containMarker.get());
                return commentElement;
            }
        }

        // process key regex 
        if (this.configuration.getCommentMarkerConfiguration().getRegex() != null) {
            Boolean isMatchWithMarker = commentText.matches(this.configuration.getCommentMarkerConfiguration().getRegex());
            if (isMatchWithMarker) {
                commentElement.setValue(commentText.trim());
                commentElement.setMarker(this.configuration.getCommentMarkerConfiguration().getRegex());
            }
        }

        return this.includeAll ? commentElement : null;
    }

    /**
     * Check exclude markers in the comment.
     *
     * @param comment
     * @return Boolean
     */
    private Boolean isExcluded(Comment comment) {
        Optional<String> containMarker = this.configuration.getCommentMarkerConfiguration().getExcludeContains().stream().filter(comment.getContent()::contains).findFirst();
        if (containMarker.isPresent()) {
            return true;
        }
        if (this.configuration.getCommentMarkerConfiguration().getExcludeRegex() != null) {
            return comment.getContent().matches(this.configuration.getCommentMarkerConfiguration().getExcludeRegex());
        }
        return false;
    }
}
