module com.ecuaciones.diferenciales {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    

    opens com.ecuaciones.diferenciales.model to javafx.fxml;
    
    exports com.ecuaciones.diferenciales.model;
    // Export the top-level package so a non-JavaFX console Main can be run as a module
    exports com.ecuaciones.diferenciales;
}

