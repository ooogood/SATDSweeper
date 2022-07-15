package commentprocessor.scanner;

import commentprocessor.configuration.CommentMarkerConfiguration;
import commentprocessor.marker.CommentElement;
import commentprocessor.marker.CommentMarkerParser;
import commentprocessor.configuration.Configuration;
import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.comments.Comment;
import com.github.javaparser.symbolsolver.JavaSymbolSolver;
import com.github.javaparser.symbolsolver.model.resolution.TypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.CombinedTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.JavaParserTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.ReflectionTypeSolver;
import commentprocessor.util.NodeUtil;
import fx.satds_fx.CommentDB;
import lombok.Getter;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.blame.BlameResult;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.*;

@Getter
public abstract class Scanner {
    protected String path;
    protected List<String> keywords;
    protected Configuration configuration;
    protected Git git = null;

    protected SimpleDateFormat dateFormat = new SimpleDateFormat( "yyyy/MM/dd" );
    public Scanner( String pt, String bch, List<String> kws ) {
        path = pt;
        keywords = kws;

        // configure marker mechanism
        CommentMarkerConfiguration commentMarkerConfiguration = new CommentMarkerConfiguration()
                .toBuilder()
                .addContains( keywords )
                .includeWithoutMarker(true)
                .build();
        // build overall configuration
        Configuration config = new Configuration()
                .toBuilder()
                .path(Arrays.asList( path ))
                .sourceRoots(Arrays.asList(System.getProperty("user.dir")))
                .commentMarkerConfiguration(commentMarkerConfiguration)
                .build();

        this.configuration = config;

        List<TypeSolver> typeSolvers = new ArrayList<>();
        typeSolvers.add(new ReflectionTypeSolver());
        this.configuration.getSourceRoots().stream()
                .filter(s -> s != null && !s.isEmpty())
                .forEach(s -> typeSolvers.add(new JavaParserTypeSolver(new File(s))));

        TypeSolver[] typeSolversArray = new TypeSolver[typeSolvers.size()];
        typeSolversArray = typeSolvers.toArray(typeSolversArray);
        TypeSolver myTypeSolver = new CombinedTypeSolver(typeSolversArray);

        JavaSymbolSolver symbolSolver = new JavaSymbolSolver(myTypeSolver);
        StaticJavaParser.getConfiguration().setSymbolResolver(symbolSolver);
    }
    /**
     * Process all java file in the given directory and build environment.a CommentStore.
     *
     * @return CommentStore
     * @throws IOException
     */
     public abstract CommentStore parse();

     public void processComments(Comment comment, ScannerContext arg) {
         CommentMarkerParser commentMarkerParser = new CommentMarkerParser(arg.getConfiguration(), arg.getConfiguration().getCommentMarkerConfiguration().getIncludeWithoutMarker());
         CommentElement commentElement = commentMarkerParser.parse(comment);
         if (commentElement != null) {
            commentElement.setPath(arg.getCurrentPath());
            commentElement.setRange(comment.getRange().orElse(null));
            commentElement.setNodeDeclaration(comment.getCommentedNode().orElse(null));
            arg.getCommentStore().addComment(commentElement.getMarker(), commentElement);
         }
    }


    public void blameAllComments( CommentDB db ) {

        if( git == null ) return;

        Set<String> kwSet = db.getKeywordSet();

        // store blame result to save blaming time
        // <path to file, blame result>
        Map<Path, BlameResult> fileBlameMap = new LinkedHashMap<>();
        for( String kw : kwSet ) {
            Set<fx.satds_fx.Comment> comments = db.getKeywordGroup( kw );
            for( fx.satds_fx.Comment cm : comments ) {
                blameComment( git, cm, fileBlameMap );
            }
        }
    }
    protected abstract void blameComment(Git git, fx.satds_fx.Comment cm, Map<Path, BlameResult> fileBlameMap );
}
