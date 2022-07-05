package commentparser.visitor;

import commentparser.scanner.ScannerContext;
import commentparser.marker.group.GroupMarkerParser;
import commentparser.marker.Marker;
import commentparser.util.NodeUtil;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.comments.BlockComment;
import com.github.javaparser.ast.comments.Comment;
import com.github.javaparser.ast.comments.JavadocComment;
import com.github.javaparser.ast.comments.LineComment;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import commentparser.marker.CommentMarkerParser;
import commentparser.marker.CommentElement;

/**
 * Only parse the non method comments
 * Get comments if configuration "getCommentsOnlyWithinMethod" is false
 */
public class CommentVisitor extends VoidVisitorAdapter<ScannerContext> {

//    @Override
//    public void visit(LineComment lineComment, ScannerContext arg) {
//        this.processComments(lineComment, arg);
//    }
//
//    @Override
//    public void visit(BlockComment blockComment, ScannerContext arg) {
//        this.processComments(blockComment, arg);
//
//    }
//
//    @Override
//    public void visit(JavadocComment javadocComment, ScannerContext arg) {
//        this.processComments(javadocComment, arg);
//    }


    /**
     * Process all comment which are not inside environment.a method.
     * @param comment
     * @param arg
     */
    public void processComments(Comment comment, ScannerContext arg) {
        if (NodeUtil.isInBoundaries(arg.getConfiguration(), comment) ) {
            CommentMarkerParser commentMarkerParser = new CommentMarkerParser(arg.getConfiguration(), arg.getConfiguration().getCommentMarkerConfiguration().getIncludeWithoutMarker());
            CommentElement commentElement = commentMarkerParser.parse(comment);
            if (commentElement != null) {
                // commentElement.setParent(marker);
                commentElement.setPath(arg.getCurrentPath());
                commentElement.setRange(comment.getRange().orElse(null));
                commentElement.setNodeDeclaration(comment.getCommentedNode().orElse(null));
                arg.getCommentStore().addComment(arg.getConfiguration().getGroupMarkerConfiguration().getDefaultGroupName(), commentElement);
            }
        }
    }

    // private Boolean isMethodComment(Comment comment) {
    //     if (comment.getCommentedNode().isPresent()) {
    //         Node targetNode = comment.getCommentedNode().get();
    //         return !(targetNode instanceof MethodDeclaration || targetNode instanceof FieldDeclaration || targetNode instanceof ClassOrInterfaceDeclaration);
    //     }
    //     return false;
    // }

}
