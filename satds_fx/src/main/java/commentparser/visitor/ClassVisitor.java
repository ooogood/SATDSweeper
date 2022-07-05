package commentparser.visitor;

import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.comments.Comment;
import commentparser.scanner.ScannerContext;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

public class ClassVisitor extends VoidVisitorAdapter<ScannerContext> {

//    private CommentVisitor commentVisitor = new CommentVisitor();
//
//    @Override
//    public void visit(ClassOrInterfaceDeclaration cd, ScannerContext arg) {
//        var children = cd.getChildNodes();
//        for( var child : children ) {
//            if( child instanceof ClassOrInterfaceDeclaration )
//                child.accept( new ClassVisitor(), arg );
//            else if( child instanceof Comment )
//                commentVisitor.processComments( (Comment) child, arg);
//            else
//                child.accept( commentVisitor, arg );
//        }
//    }

}
