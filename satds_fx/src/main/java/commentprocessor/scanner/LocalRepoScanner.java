package commentprocessor.scanner;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseResult;
import com.github.javaparser.ast.comments.JavadocComment;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.comments.Comment;
import fx.satds_fx.Model;
import lombok.Getter;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.blame.BlameResult;
import org.eclipse.jgit.diff.RawTextComparator;
import org.eclipse.jgit.lib.PersonIdent;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Getter
public class LocalRepoScanner extends Scanner {

    public LocalRepoScanner( String pt, String bch, List<String> kws ) {
        super( pt, bch, kws );

        // try open git
        File repo = new File( path + "\\.git" );
        if( repo.isDirectory() ) {
            try {
                git = new Git( new FileRepositoryBuilder()
                        .setGitDir( repo )
                        .build() );
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else {
            // do nothing, repo is null
            System.out.println("This path does not have .git folder.");
            Model.getInst().setErrorMessage("This path does not have .git folder.", false );
        }
    }

    /**
     * Process all java file in the given directory and build environment.a CommentStore.
     *
     * @return CommentStore
     */
    @Override
    public CommentStore parse() {

        List<Path> files = getFiles();

        CommentStore commentStore = new CommentStore();
        ScannerContext scannerContext = new ScannerContext(commentStore, this.configuration);

        //Process each java file
        files.forEach(path -> {
            try {
                scannerContext.setCurrentPath(path);
                JavaParser jp = new JavaParser();
                ParseResult<CompilationUnit> parseResult = jp.parse( path );
                if( !parseResult.isSuccessful() ) {
                    System.out.println("Cannot parse this file: " + path.toString() );
                }
                else {
                    CompilationUnit compilationUnit = parseResult.getResult().get();
                    List<Comment> comments = compilationUnit.getAllContainedComments();
                    for (var cm : comments) {
                        // workaround: ignore javadoc comment because @author will be seen as SATD
                        // also, you shouldn't write a SATD in a javadoc.
                        if (cm instanceof JavadocComment)
                            continue;
                        processComments(cm, scannerContext);
                    }
                }
            } catch (IOException e) {
                System.out.println("Cannot parse this file: " + path.toString() );
            }
        });

        commentStore.sort();
        return scannerContext.getCommentStore();

    }

    private List<Path> getFiles() {
        List<Path> files = new ArrayList<>();
        this.configuration.getPath().forEach(s -> {
            try {
                Stream<Path> walk = Files.walk(Paths.get(s));
                files.addAll(walk.filter(path -> path.toString().endsWith(".java")).collect(Collectors.toList()));
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        return files;
    }

    @Override
    protected void blameComment(Git git, fx.satds_fx.Comment cm, Map<Path, BlameResult> fileBlameMap) {
        try {
            Path filePath = cm.getPath();
            if( git != null ) {
                BlameResult br = null;
                if (fileBlameMap.containsKey(filePath))
                    br = fileBlameMap.get(filePath);
                else {
                    String relativePath = filePath.toString()
                            .replace(path+"\\", "" )
                            .replace("\\", "/"); // git blame takes '/' as separator
                    br = git.blame().setFilePath( relativePath )
                            .setTextComparator(RawTextComparator.WS_IGNORE_ALL)
                            .call();
                    fileBlameMap.put( filePath, br );
                }
                // extract info from blame result
                if( br != null ) {
                    PersonIdent person = br.getSourceAuthor( cm.getLineNum() );
                    cm.setAuthor( person.getName() );
                    cm.setDate( dateFormat.format(person.getWhen()) );
                }
            }
        } catch( GitAPIException e ) { e.printStackTrace(); }
    }

}
