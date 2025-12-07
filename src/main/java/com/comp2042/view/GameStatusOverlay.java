package com.comp2042.view;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import javafx.util.Duration;

// CRITICAL FIX: The class must extend BorderPane to inherit JavaFX layout and visibility methods.
public class GameStatusOverlay extends BorderPane {

    private final Label messageLabel = new Label();
    private Timeline autoHide;

    /**
     * Create an overlay.
     * @param text displayed text
     * @param styleClass CSS style class to apply to the Label (nullable)
     * @param autoHideAfter optional auto-hide duration; if null the overlay stays until manually hidden
     **/

    // Constructor with no arguments
    public GameStatusOverlay() {
        this("", null, null);
    }

    public GameStatusOverlay(String text, String styleClass, Duration autoHideAfter) {
        // Style
        messageLabel.setText(text);
        messageLabel.setFont(Font.font(24));
        if (styleClass != null && !styleClass.isEmpty()) {
            messageLabel.getStyleClass().add(styleClass);
        }

        messageLabel.setMaxWidth(Double.MAX_VALUE);
        messageLabel.setAlignment(Pos.CENTER);
        messageLabel.setTextAlignment(TextAlignment.CENTER);

        // Layout
        StackPane center = new StackPane(messageLabel);
        center.setAlignment(Pos.CENTER);
        setCenter(center); // This method is now visible because the class extends BorderPane

        if (autoHideAfter != null) {
            autoHide = new Timeline(new KeyFrame(autoHideAfter, ae -> hide()));
            autoHide.setCycleCount(1);
        }

        // Initial state
        setVisible(false); // This method is now visible because the class extends BorderPane (via Node)
        setMouseTransparent(false); // This method is now visible because the class extends BorderPane (via Node)
    }

    // Show the overlay, starting the auto-hide timer if applicable
    public void show() {
        if (autoHide != null) {
            autoHide.stop();
            autoHide.playFromStart();
        }
        setVisible(true);
    }

    // Hide the overlay and stop the auto-hide timer if running
    public void hide() {
        setVisible(false);
        if (autoHide != null) autoHide.stop();
    }

    // Update the displayed text
    public void setText(String text) {
        messageLabel.setText(text);
    }

    // Update the CSS style class applied to the label
    public void setStyleClass(String styleClass) {
        messageLabel.getStyleClass().clear();
        if (styleClass != null && !styleClass.isEmpty()) messageLabel.getStyleClass().add(styleClass);
    }
}