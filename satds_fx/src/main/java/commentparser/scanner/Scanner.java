package commentparser.scanner;

import commentparser.scanner.adapter.ParseProcessAdapter;
import commentparser.scanner.adapter.Progress;
import commentparser.configuration.Configuration;
import commentparser.visitor.CommentVisitor;
import commentparser.visitor.ClassVisitor;
import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.comments.Comment;
import com.github.javaparser.symbolsolver.JavaSymbolSolver;
import com.github.javaparser.symbolsolver.model.resolution.TypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.CombinedTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.JavaParserTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.ReflectionTypeSolver;
import lombok.Getter;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Getter
public class Scanner {

    private volatile Configuration configuration;
    private ClassVisitor classVisitor = new ClassVisitor();
    private CommentVisitor commentVisitor = new CommentVisitor();

    public Scanner() {
        this(new Configuration());
    }

    public Scanner(Configuration configuration) {
        this.configuration = configuration;

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
    public CommentStore parse() throws IOException {

        List<Path> files = getFiles();

        CommentStore commentStore = new CommentStore();
        ScannerContext scannerContext = new ScannerContext(commentStore, this.configuration);

        //Process each java file
        files.forEach(path -> {
            try {
                scannerContext.setCurrentPath(path);
                CompilationUnit compilationUnit = StaticJavaParser.parse(path);
                List<Comment> comments = compilationUnit.getAllContainedComments();
                for( var cm : comments ) {
                    commentVisitor.processComments(cm, scannerContext);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        this.postProcess(scannerContext);

        return scannerContext.getCommentStore();

    }

    private void postProcess(ScannerContext context) {
        //Apply inheritance for the created comment collections
        context.getCommentStore().processInheritance();
        context.getCommentStore().sort();
    }

    private List<Path> getFiles() {
        List<Path> files = new ArrayList<>();
        this.configuration.getBaseDirs().forEach(s -> {
            try {
                Stream<Path> walk = Files.walk(Paths.get(s));
                files.addAll(walk.filter(path -> path.toString().endsWith(".java")).collect(Collectors.toList()));
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        return files;
    }

}
