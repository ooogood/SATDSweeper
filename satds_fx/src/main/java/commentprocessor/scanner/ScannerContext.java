package commentprocessor.scanner;

import commentprocessor.configuration.Configuration;
import lombok.*;

import java.nio.file.Path;

@RequiredArgsConstructor
@Getter
@Setter
public class ScannerContext {
    @NonNull
    private CommentStore commentStore;
    @NonNull
    private Configuration configuration;
    private Path currentPath;
}
