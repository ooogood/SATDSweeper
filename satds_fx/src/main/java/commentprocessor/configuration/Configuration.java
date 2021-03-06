package commentprocessor.configuration;

import lombok.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Configuration class for comment parser.
 */

@Getter
@Setter
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class Configuration {

    @NonNull
    private CommentMarkerConfiguration commentMarkerConfiguration = new CommentMarkerConfiguration();
    // path can be a local dir or remote url
    @NonNull
    private List<String> path = new ArrayList<>(Arrays.asList(System.getProperty("user.dir")));
    @NonNull
    private List<String> sourceRoots = new ArrayList<>(Arrays.asList("src/main/java"));
}
