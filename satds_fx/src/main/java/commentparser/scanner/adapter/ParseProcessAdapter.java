package commentparser.scanner.adapter;

import commentparser.scanner.CommentStore;

public interface ParseProcessAdapter {

    void onSuccess(CommentStore commentStore);

    default void onError(Exception exception) {
    }

    default void onProgress(Progress progress) {
    }

    default void onCancel() {
    }

    default boolean isCanceled() {
        return false;
    }

}
