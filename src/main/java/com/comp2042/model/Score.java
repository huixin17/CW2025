package com.comp2042.model;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;

/**
 * Manages the player's game score. This class utilizes a JavaFX {@code IntegerProperty}
 * to make the score observable, allowing for automatic and immediate updates of
 * graphical user interface (UI) elements whenever the score changes.
 *
 * @author COMP2042 Coursework
 */
public final class Score {

    /**
     * The observable property holding the current score value. Initialized to 0.
     * This field enables reactive UI data binding.
     */
    private final IntegerProperty score = new SimpleIntegerProperty(0);

    /**
     * Retrieves the observable score property.
     *
     * @return The {@code IntegerProperty} instance representing the current score,
     * suitable for JavaFX binding operations.
     */
    public IntegerProperty scoreProperty() {
        return score;
    }

    /**
     * Increments the current score by the specified number of points.
     *
     * @param pointsToAdd The integer number of points to be added to the current score.
     */
    public void add(int pointsToAdd){
        score.setValue(score.getValue() + pointsToAdd);
    }

    /**
     * Resets the game score back to its initial value of zero.
     */
    public void reset() {
        score.setValue(0);
    }
}