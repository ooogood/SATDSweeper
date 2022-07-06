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
    private Set<String> contains = new HashSet<>();
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

        public CommentMarkerConfigurationBuilder addContains(List<String> list) {
            this.contains.addAll(list);
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

        public CommentMarkerConfigurationBuilder addExcludeContains(List<String> list) {
            this.excludeContains.addAll(list);
            return this;
        }
    }
}
