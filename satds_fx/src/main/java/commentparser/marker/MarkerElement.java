package commentparser.marker;

import com.github.javaparser.Range;
import com.github.javaparser.ast.Node;
import lombok.Data;

import java.nio.file.Path;

/**
 * Base class for marker elements. (e.q CommentElement)
 */
@Data
public abstract class MarkerElement {

    private Range range;
    private Node nodeDeclaration;
    private String description;
    private Path path;
}
