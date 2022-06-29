module fx.satds_fx {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires org.kordamp.bootstrapfx.core;

    opens fx.satds_fx to javafx.fxml;
    exports fx.satds_fx;
}