package de.fhdo.sama.capstone;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

public class AppTest {

    @BeforeAll
    public static void initToolkit() throws Exception {
        try {
            CountDownLatch latch = new CountDownLatch(1);
            Platform.startup(latch::countDown);
            if (!latch.await(5, TimeUnit.SECONDS)) {
                throw new IllegalStateException("JavaFX Platform failed to start in time");
            }
        } catch (IllegalStateException e) {
            // Platform already started -> ignore
        }
    }

    @Test
    public void fxmlLoadsAndControllerPresent() throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/medi_track_view.fxml"));
        Parent root = loader.load();
        assertNotNull(root, "FXML root should not be null");
        Object controller = loader.getController();
        assertNotNull(controller, "Controller should be created by FXMLLoader");
        assertTrue(controller instanceof MediTrackController, "Controller should be MediTrackController");
    }
}

