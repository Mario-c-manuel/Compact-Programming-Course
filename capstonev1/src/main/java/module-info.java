/**
 * Module descriptor for the Meditrack Capstone Application.
 * This explicitly defines which JavaFX modules are required and
 * opens the packages containing controllers and FXML files.
 */
module de.fhdo.sama.capstone {
    // 1. Required JavaFX Modules
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    
    // 2. Open packages for runtime access by JavaFX (CRITICAL)
    // The FXML loader needs access to instantiate the App and Controllers.
    opens de.fhdo.sama.capstone to javafx.fxml, javafx.graphics;
    
    // 3. Export the main application package
    exports de.fhdo.sama.capstone;
    
    // 4. USES directive to allow FXML to find resource files.
    // This is often needed when resources are not in a package-matched folder.
    // The previous error indicates the FXML loader itself couldn't resolve the location.
    uses javafx.fxml.FXMLLoader; 
}