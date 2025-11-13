package de.fhdo.sama.capstone;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/medi_track_view.fxml"));
        Scene scene = new Scene(loader.load(), 1200, 800);
        primaryStage.setTitle("Medicine Delivery Dashboard");
        primaryStage.setScene(scene);
        // primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("/icon.png"))); // optional
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
