package de.fhdo.sama.capstone;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * The primary entry point for the Meditrack JavaFX application.
 * This replaces the old Swing/AWT entry point.
 */
public class App extends Application {

    /**
     * The main entry point for all JavaFX applications.
     * This method is called after the init() method returns, 
     * and after the system is ready to begin.
     *
     * @param primaryStage The primary stage for this application.
     */
    @Override
    public void start(Stage primaryStage) {
        try {
            // 1. Load the FXML file associated with the MeditrackController.
            // Adjust the path below to match the actual location of your FXML file.
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/medi_track_view.fxml"));
            Parent root = loader.load();
            
            // 2. Configure the primary stage (window)
            primaryStage.setTitle("Meditrack AGV & Inventory System");
            primaryStage.setScene(new Scene(root));
            primaryStage.show();
            
            // NOTE: The MeditrackController will be accessible via loader.getController()
            // if you need to pass initialization data, but typically FXML handles this.
            
        } catch (IOException e) {
            // Log the exception if the FXML file or other resources are missing
            System.err.println("Failed to load MeditrackView FXML.");
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("An error occurred during application startup.");
            e.printStackTrace();
        }
    }

    /**
     * Standard main method used to launch the JavaFX application.
     * This replaces the old SwingUtilities.invokeLater() call.
     *
     * @param args Command line arguments.
     */
    public static void main(String[] args) {
        // Launch the JavaFX application
        launch(args);
    }
}