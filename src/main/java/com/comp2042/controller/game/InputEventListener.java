package com.comp2042.controller.game;

import com.comp2042.model.DownData;
import com.comp2042.model.ViewData;

/**
 * Interface used by the game controller to react to player inputs.
 * Each method represents a specific in-game action such as moving,
 * rotating, dropping, or starting a new session.
 */
public interface InputEventListener {

    /**
     * Triggered when the piece attempts to move one row down.
     *
     * @param event details about the movement request
     * @return DownData with any cleared lines and updated piece view
     */
    DownData onDownEvent(MoveEvent event);

    /**
     * Moves the active piece one space to the left.
     *
     * @param event movement info
     * @return updated view of the piece
     */
    ViewData onLeftEvent(MoveEvent event);

    /**
     * Moves the active piece one space to the right.
     *
     * @param event movement info
     * @return updated view of the piece
     */
    ViewData onRightEvent(MoveEvent event);

    /**
     * Rotates the current piece.
     *
     * @param event movement info
     * @return updated rotation data for the piece
     */
    ViewData onRotateEvent(MoveEvent event);

    /**
     * Performs a hard drop (piece falls directly to the bottom).
     *
     * @param event movement info
     * @return DownData with updated board state after landing
     */
    DownData onHardDropEvent(MoveEvent event);

    /**
     * Stores the current piece in the hold slot or swaps with the held one.
     *
     * @param event movement info
     * @return updated view after holding or swapping
     */
    ViewData onHoldEvent(MoveEvent event);

    /**
     * Resets the game board and starts a new game session.
     */
    void createNewGame();
}