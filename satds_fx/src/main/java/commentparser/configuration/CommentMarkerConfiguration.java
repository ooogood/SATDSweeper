package commentparser.configuration;

import lombok.*;

import java.util.*;

@Getter
@Setter
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class CommentMarkerConfiguration {

    @NonNull
    private Boolean includeWithoutMarker = false;

    @NonNull
    private Set<String> contains = new HashSet<>(Arrays.asList("cmnt", "TODO", "todo", "@Comment"));
    private String regex;

    @NonNull
    private Set<String> excludeContains = new HashSet<>();
    private String excludeRegex;

    @NonNull
    private Boolean enableContains = true;

    @NonNull
    private Boolean removeMarkers = true;

    public static class CommentMarkerConfigurationBuilder {
        public CommentMarkerConfigurationBuilder addContains(String text) {
            this.contains.add(text);
            return this;
        }


        public CommentMarkerConfigurationBuilder addContains(String... text) {
            this.contains.addAll(Arrays.asList(text));
            return this;
        }

        public CommentMarkerConfigurationBuilder addExcludeContains(String text) {
            this.excludeContains.add(text);
            return this;
        }


        public CommentMarkerConfigurationBuilder addExcludeContains(String... text) {
            this.excludeContains.addAll(Arrays.asList(text));
            return this;
        }
    }
}
