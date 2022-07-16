package commentprocessor.scanner;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseProblemException;
import com.github.javaparser.ParseResult;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.comments.Comment;
import com.github.javaparser.ast.comments.JavadocComment;
import fx.satds_fx.Model;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.blame.BlameResult;
import org.eclipse.jgit.diff.RawTextComparator;
import org.eclipse.jgit.internal.storage.dfs.DfsRepositoryDescription;
import org.eclipse.jgit.internal.storage.dfs.InMemoryRepository;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.ObjectLoader;
import org.eclipse.jgit.lib.ObjectStream;
import org.eclipse.jgit.lib.PersonIdent;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevTree;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.transport.RefSpec;
import org.eclipse.jgit.treewalk.TreeWalk;

import java.io.IOException;
import java.nio.file.Path;
import java.util.*;

public class RemoteRepoScanner extends Scanner {
    private ObjectId lastCommitId;
    private InMemoryRepository repo;
    public RemoteRepoScanner(String pt, String bch, List<String> kws ) {
        super( pt, bch, kws );
        // try open git
        try {
            DfsRepositoryDescription repoDesc = new DfsRepositoryDescription();
            repo = new InMemoryRepository(repoDesc);
            git = new Git(repo);
            git.fetch()
                    .setRemote(configuration.getPath().get(0))
                    .setRefSpecs(new RefSpec("+refs/heads/*:refs/heads/*"))
                    .call();
            repo.getObjectDatabase();
            lastCommitId = repo.resolve("refs/heads/" + bch);
            if (lastCommitId == null) {
                System.out.println("No such repository or branch");
                Model.getInst().setErrorMessage("No such repository or branch", true );
            }
        }
        catch ( Exception e ) {
            System.out.println("No such repository or branch");
            Model.getInst().setErrorMessage("No such repository or branch", true );
        }
    }

    /**
     * Process all java file in the given directory and build environment.a CommentStore.
     *
     * @return CommentStore
     * @throws IOException
     */
    public CommentStore parse() {

        CommentStore commentStore = new CommentStore();
        // return empty store if fetch unsuccessful
        if( git == null || lastCommitId == null ) return commentStore;

        ScannerContext scannerContext = new ScannerContext(commentStore, this.configuration);
        try {
            RevWalk revWalk = new RevWalk(repo);
            RevCommit commit = revWalk.parseCommit(lastCommitId);
            RevTree tree = commit.getTree();
            TreeWalk treeWalk = new TreeWalk(repo);
            treeWalk.addTree(tree);
            treeWalk.setRecursive(true);
            JavaParser jp = new JavaParser();

            // go through each file
            while (treeWalk.next()) {
                String path = treeWalk.getPathString();
                // skip none java file
                if (!path.endsWith(".java")) continue;
                scannerContext.setCurrentPath(Path.of(path));

                // try to parse this file
                ObjectId objectId = treeWalk.getObjectId(0);
                ObjectLoader loader = repo.open(objectId);
                ObjectStream stream = loader.openStream();
                ParseResult<CompilationUnit> parseResult = null;
                try {
                    parseResult = jp.parse(stream);
                } catch (ParseProblemException e) {
                    System.out.println("Cannot parse this file: "+ path);
                }

                // process parse result of this file
                if ( parseResult != null && parseResult.isSuccessful() ) {
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
            }
        } catch ( IOException e ) {
            System.out.println("Cannot parse this branch");
            Model.getInst().setErrorMessage("Cannot parse this branch", true );
        }

        commentStore.sort();
        return scannerContext.getCommentStore();
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
                    br = git.blame()
                            // remember to replace windows path separator with url separator
                            .setFilePath(filePath.toString().replace("\\", "/"))
                            .setStartCommit(lastCommitId)
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
