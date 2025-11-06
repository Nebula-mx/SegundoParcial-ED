module com.ecuaciones.diferenciales {
    requires javafx.controls;
    requires javafx.fxml;

    opens com.ecuaciones.diferenciales to javafx.fxml;
    exports com.ecuaciones.diferenciales;
}

