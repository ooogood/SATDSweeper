package commentparser.export;

import commentparser.scanner.CommentStore;
import commentparser.configuration.ExportConfiguration;

import java.io.IOException;

public abstract class Exporter {

    private ExportConfiguration exportConfiguration;

    public Exporter() {
        this.exportConfiguration = new ExportConfiguration();
    }

    public Exporter(ExportConfiguration exportConfiguration) {
        this.exportConfiguration = exportConfiguration;
    }

    public abstract void export(CommentStore commentStore) throws IOException;

    public ExportConfiguration getExportConfiguration() {
        return exportConfiguration;
    }

}
