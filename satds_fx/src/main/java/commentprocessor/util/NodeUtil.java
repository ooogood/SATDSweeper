package commentprocessor.util;

import commentprocessor.configuration.Configuration;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;

import java.nio.file.Paths;
import java.util.Optional;

public class NodeUtil {

    /**
     * Chech that the given node is in the configured directory.
     * @param configuration
     * @param node
     * @return Boolean
     */
    public static Boolean isInBoundaries(Configuration configuration, Node node) {
        Optional<CompilationUnit> compUnitOpt = node.findCompilationUnit();
        if (compUnitOpt.isPresent()) {
            Optional<CompilationUnit.Storage> storageOpt = compUnitOpt.get().getStorage();
            storageOpt.ifPresent(CompilationUnit.Storage::getDirectory);
            if (storageOpt.isPresent()) {
                return configuration.getPath().stream().anyMatch(s -> storageOpt.get().getDirectory().startsWith(Paths.get(s)));
            }
        }
        return false;
    }

}
