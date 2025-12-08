package com.comp2042.model;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;

import java.util.HashMap;
import java.util.Map;

/**
 * Manages the player's economic resources related to power-ups, including
 * skill point accumulation, power-up inventory tracking, purchasing, and usage.
 * This class is central to the meta-game progression system.
 *
 * @author COMP2042 Coursework
 */
public class PowerUpManager {
    /** Map storing the inventory: associates each PowerUp type with its current quantity owned by the player. */
    private final Map<PowerUp, Integer> powerUpInventory;

    /** Observable property for the player's current total skill points, allowing for reactive UI binding. */
    private final IntegerProperty skillPoints;

    /** Tracks fractional skill points to ensure precise accumulation of small score contributions. */
    private double fractionalSkillPoints = 0.0;

    /**
     * Constructs a new PowerUpManager instance.
     * Initializes the player's skill points to zero and sets the initial quantity
     * of all available power-ups in the inventory to zero.
     */
    public PowerUpManager() {
        this.skillPoints = new SimpleIntegerProperty(0);
        this.powerUpInventory = new HashMap<>();

        // Initialize all defined power-ups in the inventory with an owned quantity of 0
        for (PowerUp powerUp : PowerUp.values()) {
            powerUpInventory.put(powerUp, 0);
        }
    }

    /**
     * Provides access to the observable {@code IntegerProperty} representing the player's
     * current skill points. This is used primarily for binding with JavaFX UI components.
     *
     * @return The {@code IntegerProperty} tracking current skill points.
     */
    public IntegerProperty skillPointsProperty() {
        return skillPoints;
    }

    /**
     * Retrieves the current integer value of the player's skill points.
     *
     * @return The total current number of skill points.
     */
    public int getSkillPoints() {
        return skillPoints.get();
    }


    /**
     * Awards skill points to the player based on the score earned during a segment of gameplay.
     * The conversion rate is 1 skill point for every 10 score points earned. Fractional accumulation
     * is handled internally to prevent loss of precision from small scores.
     *
     * @param scoreEarned The score points accumulated by the player.
     */
    public void awardSkillPoints(int scoreEarned) {
        // Add fractional points based on a 1:10 score-to-skill-point ratio.
        fractionalSkillPoints += scoreEarned / 10.0;

        // Convert the accumulated fractional points into whole skill points.
        int wholePoints = (int) fractionalSkillPoints;
        if (wholePoints > 0) {
            // Update the observable skill points property.
            skillPoints.set(skillPoints.get() + wholePoints);
            // Deduct the whole points awarded, retaining any remainder for future accumulation.
            fractionalSkillPoints -= wholePoints;
        }
    }

    /**
     * Attempts to purchase a specified power-up. The transaction is successful only if
     * the player has sufficient skill points to cover the cost.
     *
     * @param powerUp The {@code PowerUp} type to be purchased.
     * @return {@code true} if the purchase was successful and the inventory was updated;
     * {@code false} otherwise (due to insufficient funds).
     */
    public boolean purchasePowerUp(PowerUp powerUp) {
        if (skillPoints.get() >= powerUp.getCost()) {
            // Deduct cost and update inventory count.
            skillPoints.set(skillPoints.get() - powerUp.getCost());
            powerUpInventory.put(powerUp, powerUpInventory.get(powerUp) + 1);
            return true;
        }
        return false;
    }

    /**
     * Consumes one instance of the specified power-up from the player's inventory.
     *
     * @param powerUp The {@code PowerUp} type to be used.
     * @return {@code true} if the power-up was available in the inventory and successfully used
     * (quantity decreased); {@code false} if the inventory for that power-up was empty.
     */
    public boolean usePowerUp(PowerUp powerUp) {
        if (powerUpInventory.get(powerUp) > 0) {
            // Decrease the quantity in the inventory.
            powerUpInventory.put(powerUp, powerUpInventory.get(powerUp) - 1);
            return true;
        }
        return false;
    }

    /**
     * Retrieves the current quantity of a specific power-up held in the player's inventory.
     *
     * @param powerUp The {@code PowerUp} type to query.
     * @return The integer count of the specified power-up.
     */
    public int getPowerUpQuantity(PowerUp powerUp) {
        // The map is guaranteed to contain all PowerUp values due to constructor initialization.
        return powerUpInventory.get(powerUp);
    }

    /**
     * Resets the entire power-up management system to its initial state, typically
     * called at the start of a new game session. All power-up quantities and skill
     * points are set back to zero.
     */
    public void reset() {
        // Reset inventory quantities to zero
        for (PowerUp powerUp : PowerUp.values()) {
            powerUpInventory.put(powerUp, 0);
        }
        // Reset observable skill points and fractional tracker
        skillPoints.set(0);
        fractionalSkillPoints = 0.0;
    }
}

