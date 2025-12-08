package com.comp2042;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Main entry point for the Tetris JavaFX application.
 * This class extends {@code javafx.application.Application} and implements the standard
 * JavaFX lifecycle methods to initialize and display the game window.
 *
 * @author COMP2042 Coursework
 */
public class Main extends Application {

    /**
     * The primary entry method for all JavaFX applications. This method is called
     * after the application is initialized and is responsible for setting up the
     * primary stage and scene, and loading the user interface layout from FXML.
     *
     * @param primaryStage The primary stage (window) of the application, provided by the JavaFX runtime.
     * @throws Exception if the FXML file fails to load or if any initialization error occurs.
     */
    @Override
    public void start(Stage primaryStage) throws Exception {

        // 1. Locate the FXML layout file
        URL location = getClass().getClassLoader().getResource("gameLayout.fxml");
        ResourceBundle resources = null; // Resource bundle for localization, typically null for simple apps

        // 2. Initialize the FXMLLoader to load the scene graph from the FXML file
        FXMLLoader fxmlLoader = new FXMLLoader(location, resources);

        // 3. Load the root node of the scene graph
        Parent root = fxmlLoader.load();
        // Note: The controller associated with 'gameLayout.fxml' is automatically
        // instantiated and wired up by the FXMLLoader at this point.

        // 4. Configure the primary stage
        primaryStage.setTitle("TetrisJFX");

        // 5. Create the Scene with the loaded root node and initial dimensions
        // Dimensions set for a typical Tetris game board display
        Scene scene = new Scene(root, 300, 510);
        primaryStage.setScene(scene);

        // 6. Display the stage
        primaryStage.show();
        // The game logic will typically be initiated by a user action (e.g., clicking a "Start" button)
        // within the FXML's associated controller.
    }


    /**
     * Main method required for standard Java applications. It is used to launch the
     * JavaFX application by calling {@code Application.launch()}.
     *
     * @param args Command line arguments (not used in this application).
     */
    public static void main(String[] args) {
        launch(args);
    }
}