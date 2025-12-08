package com.comp2042.controller.gui;

import javafx.event.EventHandler;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import com.comp2042.controller.game.EventSource;
import com.comp2042.controller.game.EventType;
import com.comp2042.controller.game.MoveEvent;
import com.comp2042.model.PowerUp;

/**
 * Keyboard input handler for the GUI layer.
 * This class listens for user key presses and translates them into gameplay
 * actions—movement, rotation, drops, power-up usage, pausing, and menu toggles.
 * Separating this logic keeps GuiController focused on rendering and game flow.
 */
class GuiControllerKeyboardHandler {

    private final GuiController guiController;

    /**
     * Creates a keyboard handler tied to the given GUI controller.
     *
     * @param guiController the main controller responsible for the game screen
     */
    GuiControllerKeyboardHandler(GuiController guiController) {
        this.guiController = guiController;
    }

    /**
     * Builds and returns a unified key handler for the game area.
     * Every key event is processed here and mapped to an appropriate
     * game action through the GuiController and its event listener.
     *
     * @return a KeyEvent handler that manages all gameplay shortcuts
     */
    EventHandler<KeyEvent> createKeyHandler() {
        return new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent keyEvent) {

                // --- Gameplay controls (only when not paused or game-over) ---
                if (!guiController.isPause.getValue() && !guiController.isGameOver.getValue()) {

                    // Move left
                    if (keyEvent.getCode() == KeyCode.LEFT || keyEvent.getCode() == KeyCode.A) {
                        guiController.refreshBrick(
                                guiController.eventListener.onLeftEvent(
                                        new MoveEvent(EventType.LEFT, EventSource.USER)
                                )
                        );
                        keyEvent.consume();
                    }

                    // Move right
                    if (keyEvent.getCode() == KeyCode.RIGHT || keyEvent.getCode() == KeyCode.D) {
                        guiController.refreshBrick(
                                guiController.eventListener.onRightEvent(
                                        new MoveEvent(EventType.RIGHT, EventSource.USER)
                                )
                        );
                        keyEvent.consume();
                    }

                    // Rotate piece
                    if (keyEvent.getCode() == KeyCode.UP || keyEvent.getCode() == KeyCode.W) {
                        guiController.refreshBrick(
                                guiController.eventListener.onRotateEvent(
                                        new MoveEvent(EventType.ROTATE, EventSource.USER)
                                )
                        );
                        keyEvent.consume();
                    }

                    // Soft drop
                    if (keyEvent.getCode() == KeyCode.DOWN || keyEvent.getCode() == KeyCode.S) {
                        guiController.moveDown(new MoveEvent(EventType.DOWN, EventSource.USER));
                        keyEvent.consume();
                    }

                    // Hard drop
                    if (keyEvent.getCode() == KeyCode.SPACE) {
                        guiController.hardDrop(new MoveEvent(EventType.HARD_DROP, EventSource.USER));
                        keyEvent.consume();
                    }

                    // Hold piece
                    if (keyEvent.getCode() == KeyCode.C) {
                        guiController.refreshBrick(
                                guiController.eventListener.onHoldEvent(
                                        new MoveEvent(EventType.HOLD, EventSource.USER)
                                )
                        );
                        keyEvent.consume();
                    }

                    // --- Power-up usage (1–3) ---
                    if (keyEvent.getCode() == KeyCode.DIGIT1 || keyEvent.getCode() == KeyCode.NUMPAD1) {
                        if (guiController.gameController != null) {
                            PowerUp[] powerUps = PowerUp.values();
                            if (powerUps.length > 0) {
                                guiController.gameController.activatePowerUp(powerUps[0]);
                                guiController.updatePowerUpUI();

                                // Row clearer refreshes background
                                if (powerUps[0] == PowerUp.ROW_CLEARER) {
                                    guiController.refreshGameBackground(guiController.gameController.getBoard().getBoardMatrix());
                                }
                            }
                        }
                        keyEvent.consume();
                    }

                    if (keyEvent.getCode() == KeyCode.DIGIT2 || keyEvent.getCode() == KeyCode.NUMPAD2) {
                        if (guiController.gameController != null) {
                            PowerUp[] powerUps = PowerUp.values();
                            if (powerUps.length > 1) {
                                guiController.gameController.activatePowerUp(powerUps[1]);
                                guiController.updatePowerUpUI();
                            }
                        }
                        keyEvent.consume();
                    }

                    if (keyEvent.getCode() == KeyCode.DIGIT3 || keyEvent.getCode() == KeyCode.NUMPAD3) {
                        if (guiController.gameController != null) {
                            PowerUp[] powerUps = PowerUp.values();
                            if (powerUps.length > 2) {
                                guiController.gameController.activatePowerUp(powerUps[2]);
                                guiController.updatePowerUpUI();
                            }
                        }
                        keyEvent.consume();
                    }
                }

                // --- Power-up purchasing (Shift + 1/2/3) ---
                if (keyEvent.isShiftDown()) {

                    if (keyEvent.getCode() == KeyCode.DIGIT1 || keyEvent.getCode() == KeyCode.NUMPAD1) {
                        if (guiController.gameController != null) {
                            PowerUp[] powerUps = PowerUp.values();
                            if (powerUps.length > 0) {
                                guiController.gameController.purchasePowerUp(powerUps[0]);
                                guiController.updatePowerUpUI();
                            }
                        }
                        keyEvent.consume();
                    }

                    if (keyEvent.getCode() == KeyCode.DIGIT2 || keyEvent.getCode() == KeyCode.NUMPAD2) {
                        if (guiController.gameController != null) {
                            PowerUp[] powerUps = PowerUp.values();
                            if (powerUps.length > 1) {
                                guiController.gameController.purchasePowerUp(powerUps[1]);
                                guiController.updatePowerUpUI();
                            }
                        }
                        keyEvent.consume();
                    }

                    if (keyEvent.getCode() == KeyCode.DIGIT3 || keyEvent.getCode() == KeyCode.NUMPAD3) {
                        if (guiController.gameController != null) {
                            PowerUp[] powerUps = PowerUp.values();
                            if (powerUps.length > 2) {
                                guiController.gameController.purchasePowerUp(powerUps[2]);
                                guiController.updatePowerUpUI();
                            }
                        }
                        keyEvent.consume();
                    }
                }

                // Start new game
                if (keyEvent.getCode() == KeyCode.N) {
                    guiController.newGame(null);
                }

                // Pause / resume
                if (keyEvent.getCode() == KeyCode.P || keyEvent.getCode() == KeyCode.ESCAPE) {
                    if (guiController.countdownOverlay != null &&
                            guiController.countdownOverlay.isVisible()) {
                        guiController.cancelCountdown();
                        keyEvent.consume();
                    } else {
                        guiController.setPaused(!guiController.isPause.get());
                        keyEvent.consume();
                    }
                }

                // Toggle power-up shop
                if (keyEvent.getCode() == KeyCode.B) {
                    guiController.togglePowerUpsOverlay();
                    keyEvent.consume();
                }
            }
        };
    }
}