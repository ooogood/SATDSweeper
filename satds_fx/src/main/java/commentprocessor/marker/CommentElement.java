package commentprocessor.marker;

import lombok.Data;

import java.util.Objects;

@Data
public class CommentElement extends MarkerElement {

    private String value;
    private String marker;

    public CommentElement(String value) {
        this.value = value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CommentElement that = (CommentElement) o;
        return Objects.equals(value, that.value) &&
                Objects.equals(marker, that.marker) &&
                Objects.equals(getPath(), that.getPath());
    }

    @Override
    public int hashCode() {
        return Objects.hash(value, marker, getPath(), getRange().begin.line);
    }
}
