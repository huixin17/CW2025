package com.comp2042.controller.gui;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;
import com.comp2042.controller.game.EventSource;
import com.comp2042.controller.game.EventType;
import com.comp2042.controller.game.MoveEvent;

/**
 * Manages the slow motion power-up effect, controlling game speed via the JavaFX Timeline.
 * This class handles the temporary adjustment of the game loop duration, manages the
 * countdown display, and ensures correct reversion to the normal speed setting upon expiration.
 *
 * @author COMP2042 Coursework
 */
class GuiControllerSlowMotionManager {

    // Defines the duration (in milliseconds) for the game loop during slow motion (e.g., 2x slower).
    private static final long SLOW_MOTION_SPEED_MS = 800;
    // Defines the standard duration (in milliseconds) for the game loop.
    private static final long NORMAL_SPEED_MS = 400;

    private final GuiController guiController;

    /**
     * Constructs the manager, initializing the association with the primary game controller.
     *
     * @param guiController the GuiController instance managing the overall game state
     */
    GuiControllerSlowMotionManager(GuiController guiController) {
        this.guiController = guiController;
    }

    /**
     * Initiates the slow motion power-up sequence.
     * It stops any existing timelines, replaces the main game loop with a slower timeline,
     * starts a visible countdown, and schedules a timer to restore the normal speed.
     */
    void applySlowMotion() {
        if (guiController.timeLine == null || guiController.isPause.getValue() || guiController.isGameOver.getValue()) {
            return;
        }

        // Stop and clean up any concurrent slow motion timers
        if (guiController.slowMotionRestoreTimer != null) {
            guiController.slowMotionRestoreTimer.stop();
        }
        if (guiController.slowMotionCountdownTimer != null) {
            guiController.slowMotionCountdownTimer.stop();
        }

        // Initialize the remaining time for the effect
        guiController.slowMotionRemainingSeconds = 10;

        // Ensure the countdown display is visible and updated
        if (guiController.slowMotionCountdownLabel != null) {
            guiController.slowMotionCountdownLabel.setVisible(true);
        }
        updateSlowMotionCountdown();

        // Halt the current game loop (timeLine)
        guiController.timeLine.stop();

        // Create and start a new game loop timeline at the slower speed
        guiController.timeLine = new Timeline(new KeyFrame(Duration.millis(SLOW_MOTION_SPEED_MS),
                ae -> guiController.moveDown(new MoveEvent(EventType.DOWN, EventSource.THREAD))));
        guiController.timeLine.setCycleCount(Timeline.INDEFINITE);
        guiController.timeLine.play();

        // Initialize and start the countdown timer that updates the UI every second
        guiController.slowMotionCountdownTimer = new Timeline(new KeyFrame(Duration.seconds(1), ae -> {
            guiController.slowMotionRemainingSeconds--;
            updateSlowMotionCountdown();
            if (guiController.slowMotionRemainingSeconds <= 0) {
                guiController.slowMotionCountdownTimer.stop();
            }
        }));
        guiController.slowMotionCountdownTimer.setCycleCount(10); // Matches the 10-second duration
        guiController.slowMotionCountdownTimer.play();

        // Initialize and start the single-cycle timer to revert the speed after the duration
        guiController.slowMotionRestoreTimer = new Timeline(new KeyFrame(Duration.seconds(10), ae -> restoreNormalSpeed()));
        guiController.slowMotionRestoreTimer.setCycleCount(1);
        guiController.slowMotionRestoreTimer.play();
    }

    /**
     * Reverts the game speed back to the {@code NORMAL_SPEED_MS} setting.
     * This method is triggered when the slow motion effect expires.
     */
    private void restoreNormalSpeed() {
        if (guiController.timeLine == null || guiController.isPause.getValue() || guiController.isGameOver.getValue()) {
            return;
        }

        // Stop the slow game loop
        guiController.timeLine.stop();

        // Re-initialize and start the game loop timeline at normal speed
        guiController.timeLine = new Timeline(new KeyFrame(Duration.millis(NORMAL_SPEED_MS),
                ae -> guiController.moveDown(new MoveEvent(EventType.DOWN, EventSource.THREAD))));
        guiController.timeLine.setCycleCount(Timeline.INDEFINITE);
        guiController.timeLine.play();

        // Hide the countdown label
        if (guiController.slowMotionCountdownLabel != null) {
            guiController.slowMotionCountdownLabel.setVisible(false);
        }
    }

    /**
     * Updates the text of the countdown label with the current number of remaining seconds.
     */
    private void updateSlowMotionCountdown() {
        if (guiController.slowMotionCountdownLabel != null) {
            guiController.slowMotionCountdownLabel.setText(String.valueOf(guiController.slowMotionRemainingSeconds));
        }
    }
}