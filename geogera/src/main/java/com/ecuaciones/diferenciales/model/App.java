//codigo para iniciar la UI en java fx

package com.ecuaciones.diferenciales.model;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;
/** 
 * JavaFX App 
 */
public class App extends Application {
    private static Scene scene;

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("primary.fxml"));
        scene = new Scene(fxmlLoader.load(), 800, 600);
        stage.setTitle("Ecuaciones Diferenciales");
        stage.setScene(scene);
        stage.show();
    }
}