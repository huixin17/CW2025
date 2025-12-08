package com.comp2042.controller.gui;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.application.Platform;

import java.net.URL;

/**
 * Manages the background video rendering and playback for the GuiController.
 * This includes loading the video resource, configuring the MediaPlayer for continuous
 * background looping, ensuring the video is muted, and binding the MediaView
 * dimensions to the primary scene container for full coverage.
 *
 * @author COMP2042 Coursework
 */
class GuiControllerVideoManager {

    private final GuiController guiController;

    /**
     * Constructs a new GuiControllerVideoManager, establishing a reference
     * to the associated GuiController instance.
     *
     * @param guiController the GuiController instance to manage video resources for
     */
    GuiControllerVideoManager(GuiController guiController) {
        this.guiController = guiController;
    }

    /**
     * Initializes and starts the background video playback.
     * Loads the "NeonLights.mp4" resource, configures the {@code MediaPlayer} for
     * silent, indefinite looping, and integrates the {@code MediaView} into the
     * root pane, positioned behind all other UI elements.
     */
    void setupVideoBackground() {
        try {
            // Attempt to load the video resource from the application's classpath
            URL videoUrl = getClass().getClassLoader().getResource("NeonLights.mp4");
            if (videoUrl != null) {
                Media media = new Media(videoUrl.toExternalForm());
                guiController.mediaPlayer = new MediaPlayer(media);
                guiController.mediaView = new MediaView(guiController.mediaPlayer);

                // Configure the MediaView properties
                guiController.mediaView.setPreserveRatio(true);
                guiController.mediaView.setSmooth(true);

                // Configure the MediaPlayer lifecycle and behavior
                guiController.mediaPlayer.setCycleCount(MediaPlayer.INDEFINITE); // Enable continuous looping
                guiController.mediaPlayer.setAutoPlay(true);
                guiController.mediaPlayer.setMute(true); // Suppress audio output

                // Execute UI updates on the JavaFX Application Thread
                Platform.runLater(() -> {
                    if (guiController.rootStackPane != null) {
                        // Insert the video view at index 0 to ensure it renders as the background layer
                        guiController.rootStackPane.getChildren().add(0, guiController.mediaView);

                        // Bind the video's fitted dimensions to the size properties of the root pane
                        guiController.mediaView.fitWidthProperty().bind(guiController.rootStackPane.widthProperty());
                        guiController.mediaView.fitHeightProperty().bind(guiController.rootStackPane.heightProperty());
                        guiController.mediaView.setPreserveRatio(false); // Allow stretching to cover the entire area
                    }
                });
            } else {
                // Report error if the video file resource is missing
                System.err.println("Video file NeonLights.mp4 not found in resources");
            }
        } catch (Exception e) {
            // Catch and report any exceptions during media loading or initialization
            System.err.println("Failed to load background video: " + e.getMessage());
            e.printStackTrace();
        }
    }
}