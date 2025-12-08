package com.comp2042.controller.gui;

import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.ScaleTransition;
import javafx.scene.control.Label;
import javafx.util.Duration;

/**
 * Handles all visual effects used by the GUI layer.
 * This helper class focuses purely on animations such as explosions
 * and layout adjustments, keeping GuiController cleaner and more focused
 * on gameplay logic and interaction.
 */
class GuiControllerEffectManager {

    private static final int BRICK_SIZE = 22;

    private final GuiController guiController;

    /**
     * Creates an effect manager that works alongside the main GUI controller.
     *
     * @param guiController the controller that owns the board and visual elements
     */
    GuiControllerEffectManager(GuiController guiController) {
        this.guiController = guiController;
    }

    /**
     * Repositions the game board so that it appears centered inside its parent
     * container. The method measures the board and its parent and calculates the
     * correct layout offsets horizontally and vertically.
     */
    void centerGameBoard() {
        javafx.scene.layout.Pane root = (javafx.scene.layout.Pane) guiController.gameBoard.getParent();
        javafx.geometry.Bounds b = guiController.gameBoard.getBoundsInParent();
        double w = b.getWidth();
        double h = b.getHeight();

        double x = Math.max(0, (root.getWidth() - w) / 2);
        double y = Math.max(0, (root.getHeight() - h) / 2);
        guiController.gameBoard.setLayoutX(x);
        guiController.gameBoard.setLayoutY(y);
    }

    /**
     * Displays a short explosion animation at the given grid coordinates.
     * Converts grid positions into screen space, creates a temporary "BOOM!"
     * label, and plays a scaling and fading sequence before removing it.
     *
     * @param gridX column index of the explosion
     * @param gridY row index of the explosion (includes hidden top rows)
     */
    void showBoomEffect(int gridX, int gridY) {
        if (guiController.gameBoard == null || guiController.boardStack == null) return;

        // Convert grid coordinates to screen coordinates
        double cellW = BRICK_SIZE + guiController.gamePanel.getHgap();
        double cellH = BRICK_SIZE + guiController.gamePanel.getVgap();

        // Adjust for two hidden rows at the top
        double x = gridX * cellW + cellW / 2;
        double y = (gridY - 2) * cellH + cellH / 2;

        // Explosion label
        Label boomLabel = new Label("BOOM!");
        boomLabel.setStyle(
                "-fx-font-size: 48px; " +
                        "-fx-font-weight: bold; " +
                        "-fx-text-fill: #FF4500; " +
                        "-fx-effect: dropshadow(gaussian, rgba(255,69,0,0.9), 15, 0.8, 0, 4);"
        );

        boomLabel.setLayoutX(x - 60);
        boomLabel.setLayoutY(y - 24);

        guiController.boardStack.getChildren().add(boomLabel);
        boomLabel.toFront();

        // Animation sequence
        ScaleTransition scaleTransition = new ScaleTransition(Duration.millis(300), boomLabel);
        scaleTransition.setFromX(0.5);
        scaleTransition.setFromY(0.5);
        scaleTransition.setToX(1.5);
        scaleTransition.setToY(1.5);

        FadeTransition fadeTransition = new FadeTransition(Duration.millis(800), boomLabel);
        fadeTransition.setFromValue(1.0);
        fadeTransition.setToValue(0.0);

        ParallelTransition parallelTransition = new ParallelTransition(scaleTransition, fadeTransition);
        parallelTransition.setOnFinished(e ->
                guiController.boardStack.getChildren().remove(boomLabel)
        );
        parallelTransition.play();
    }
}