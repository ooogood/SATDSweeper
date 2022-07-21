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
    requires weka.stable;
    requires org.eclipse.jgit;


    opens fx.satds_fx to javafx.fxml;
    exports fx.satds_fx;
    exports fx.satds_fx.controller;
    opens fx.satds_fx.controller to javafx.fxml;
    exports fx.satds_fx.model;
    opens fx.satds_fx.model to javafx.fxml;
}