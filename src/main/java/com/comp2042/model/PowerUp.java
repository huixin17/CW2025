package com.comp2042.model;

/**
 * Enumeration representing distinct power-up features available for player use.
 * Each constant defines a unique power-up, storing its display name, associated
 * skill point cost, and a brief functional description. These power-ups are
 * designed to offer temporary strategic advantages during gameplay.
 *
 * @author COMP2042 Coursework
 */
public enum PowerUp {
    /** Clears the bottom 3 rows of the game matrix. */
    ROW_CLEARER("Row Clearer", 0, "Clears the bottom 3 rows"),

    /** Temporarily reduces the rate at which blocks fall. */
    SLOW_MOTION("Slow Motion", 0, "Slows falling speed for 10 seconds"),

    /** The next placed piece triggers an explosion, clearing a surrounding 4x4 area. */
    BOMB_PIECE("Bomb Piece", 0, "Next piece explodes in 4x4 area on placement");

    private final String name;
    private final int cost;
    private final String description;

    /**
     * Constructs a PowerUp enum value, initializing its descriptive properties.
     *
     * @param name The human-readable name for display in the user interface.
     * @param cost The amount of skill points required to activate the power-up.
     * @param description A concise summary of the power-up's effect on gameplay.
     */
    PowerUp(String name, int cost, String description) {
        this.name = name;
        this.cost = cost;
        this.description = description;
    }

    /**
     * Retrieves the display name of the power-up.
     *
     * @return The descriptive name of the power-up.
     */
    public String getName() {
        return name;
    }

    /**
     * Retrieves the skill points cost associated with purchasing or activating this power-up.
     *
     * @return The integer cost in skill points.
     */
    public int getCost() {
        return cost;
    }

    /**
     * Retrieves the detailed explanation of the power-up's function.
     *
     * @return The descriptive text outlining the gameplay effect.
     */
    public String getDescription() {
        return description;
    }
}