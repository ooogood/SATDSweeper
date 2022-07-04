module fx.satds_fx {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires org.kordamp.bootstrapfx.core;
    requires org.apache.pdfbox;
    requires java.desktop;
    requires lombok;
    requires windmill;
    requires markdowngenerator;
    requires com.github.javaparser.core;
    requires com.github.javaparser.symbolsolver.core;

    opens fx.satds_fx to javafx.fxml;
    exports fx.satds_fx;
}