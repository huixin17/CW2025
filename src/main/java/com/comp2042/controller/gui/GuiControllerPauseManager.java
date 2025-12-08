package com.comp2042.controller.gui;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;

/**
 * Component dedicated to managing the operational state of the game
 * (paused or running). This includes managing the pause state, controlling
 * the visibility of pause overlays, and implementing the 3-second visual
 * countdown prior to re-entering active gameplay. This class was modularized
 * from GuiController to conform to the Single Responsibility Principle (SRP)
 * and enhance the overall structure and maintainability.
 *
 * @author COMP2042 Coursework
 */
class GuiControllerPauseManager {

    private final GuiController guiController;

    /**
     * Initializes the Pause Manager component.
     *
     * @param guiController The primary controller instance whose state (pause, timeline) this manager modifies.
     */
    GuiControllerPauseManager(GuiController guiController) {
        this.guiController = guiController;
    }

    /**
     * Toggles the running status of the game based on the input parameter.
     * If the intent is to pause (`true`), the game is halted instantly and the pause screen appears.
     * If the intent is to resume (`false`), it initiates the resume sequence, beginning with the 3-second countdown.
     *
     * @param paused A boolean flag: true to halt execution now, false to start the unpause sequence.
     */
    void setPaused(boolean paused) {
        if (!paused) {
            // Begin the resume sequence, starting the visual countdown
            showResumeCountdown();
        } else {
            // Initiate pause state instantly
            guiController.isPause.setValue(true);

            if (guiController.pauseOverlay != null) {
                guiController.pauseOverlay.setVisible(true);
                guiController.pauseOverlay.toFront();
            }

            if (guiController.timeLine != null) {
                guiController.timeLine.pause();
            }
        }
    }

    /**
     * Executes a visual 3-2-1 countdown sequence preceding the resumption of game activity.
     * Ensures the game timeline remains frozen during this period to prevent
     * any unexpected element movement (e.g., blocks dropping).
     */
    void showResumeCountdown() {
        // Prevent countdown re-initiation if one is already running
        if (guiController.countdownOverlay != null && guiController.countdownOverlay.isVisible()) {
            return;
        }
        // Guard against initiating countdown if the game is over
        if (guiController.isGameOver.getValue()) {
            return;
        }

        // Stop and clean up any preceding countdown timer
        if (guiController.countdownTimer != null) {
            guiController.countdownTimer.stop();
        }

        // Keep the game in the paused state while the countdown runs
        guiController.isPause.setValue(true);
        if (guiController.timeLine != null) {
            guiController.timeLine.pause();
        }

        // Ensure the countdown element is placed correctly on the root pane
        if (guiController.rootStackPane != null && !guiController.rootStackPane.getChildren().contains(guiController.countdownOverlay)) {
            guiController.rootStackPane.getChildren().add(guiController.countdownOverlay);
        }

        // Conceal the main pause overlay during the countdown display
        if (guiController.pauseOverlay != null) {
            guiController.pauseOverlay.setVisible(false);
        }

        // Display the countdown overlay prominently
        guiController.countdownOverlay.setVisible(true);
        guiController.countdownOverlay.toFront();

        // Initialize countdown counter starting from 3
        int[] countdownValue = {3};
        guiController.countdownLabel.setText(String.valueOf(countdownValue[0]));


        // Create the timeline for the countdown sequence
        guiController.countdownTimer = new Timeline(new KeyFrame(Duration.seconds(1), ae -> {
            countdownValue[0]--;
            if (countdownValue[0] <= 0) {
                // Countdown complete - proceed to resume
                guiController.countdownOverlay.setVisible(false);
                guiController.countdownTimer.stop();

                // Restore active gameplay state
                guiController.isPause.setValue(false);
                if (guiController.timeLine != null) {
                    guiController.timeLine.play();
                }
                guiController.gamePanel.requestFocus();
            } else {
                // Update the countdown number
                guiController.countdownLabel.setText(String.valueOf(countdownValue[0]));
            }
        }));
        guiController.countdownTimer.setCycleCount(3); // Total 3 steps: 3, 2, 1
        guiController.countdownTimer.play();
    }

    /**
     * Forces the game to unpause instantly, bypassing the standard countdown delay.
     * This is primarily used at the start of a new session to avoid unnecessary waiting.
     * Any active countdown timers are stopped and the overlays are hidden.
     */
    void resumeImmediately() {
        // Halt and dispose of any ongoing countdown process
        if (guiController.countdownTimer != null) {
            guiController.countdownTimer.stop();
        }
        if (guiController.countdownOverlay != null) {
            guiController.countdownOverlay.setVisible(false);
        }

        // Immediately switch state to running
        guiController.isPause.setValue(false);
        if (guiController.pauseOverlay != null) {
            guiController.pauseOverlay.setVisible(false);
        }

        if (guiController.timeLine != null) {
            guiController.timeLine.play();
        }
        guiController.gamePanel.requestFocus();
    }

    /**
     * Halts any ongoing resume countdown and reverts the display back to the main
     * pause menu overlay. Typically invoked when the pause key (ESC) is pressed
     * while the 3-2-1 sequence is running.
     */
    void cancelCountdown() {
        if (guiController.countdownTimer != null) {
            guiController.countdownTimer.stop();
        }

        if (guiController.countdownOverlay != null) {
            guiController.countdownOverlay.setVisible(false);
        }

        // Return visibility to the main pause screen
        if (guiController.pauseOverlay != null) {
            guiController.pauseOverlay.setVisible(true);
            guiController.pauseOverlay.toFront();
        }
    }
}