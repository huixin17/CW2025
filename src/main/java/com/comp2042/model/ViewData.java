package com.comp2042.model;

/**
 * Data Transfer Object (DTO) used to convey the necessary state information
 * from the game model (Board) to the graphical user interface (GUI) view
 * for rendering the current game frame.
 *
 * This object ensures encapsulation by providing copies of the internal data arrays.
 *
 * @author COMP2042 Coursework
 */
public final class ViewData {

    private final int[][] brickData; // The 2D array matrix of the currently falling brick's shape.
    private final int xPosition; // The column index (x-coordinate) of the falling brick's top-left corner on the board.
    private final int yPosition; // The row index (y-coordinate) of the falling brick's top-left corner on the board.
    private final int[][] nextBrickData; // The 2D array matrix of the next piece in the queue (preview).
    private final int[][] heldBrickData; // The 2D array matrix of the piece currently in the hold queue, or null if empty.

    /**
     * Constructs a ViewData object encapsulating all primary and secondary brick information.
     *
     * @param brickData The 2D array representing the current falling brick's shape.
     * @param xPosition The x-coordinate (column) of the current brick's position.
     * @param yPosition The y-coordinate (row) of the current brick's position.
     * @param nextBrickData The 2D array representing the shape of the next brick.
     * @param heldBrickData The 2D array representing the held brick shape; {@code null} if no brick is held.
     */
    public ViewData(int[][] brickData, int xPosition, int yPosition, int[][] nextBrickData, int[][] heldBrickData) {
        this.brickData = brickData;
        this.xPosition = xPosition;
        this.yPosition = yPosition;
        this.nextBrickData = nextBrickData;
        this.heldBrickData = heldBrickData;
    }

    /**
     * Constructs a ViewData object without the held brick information.
     * This constructor is provided for scenarios where the hold feature is not yet available
     * or for backward compatibility.
     *
     * @param brickData The 2D array representing the current falling brick's shape.
     * @param xPosition The x-coordinate (column) of the current brick's position.
     * @param yPosition The y-coordinate (row) of the current brick's position.
     * @param nextBrickData The 2D array representing the shape of the next brick.
     */
    public ViewData(int[][] brickData, int xPosition, int yPosition, int[][] nextBrickData) {
        this(brickData, xPosition, yPosition, nextBrickData, null);
    }

    /**
     * Gets a deep copy of the current brick's shape data to prevent external modification of the internal state.
     *
     * @return A copy of the 2D array representing the current brick shape.
     */
    public int[][] getBrickData() {
        return MatrixOperations.copy(brickData);
    }

    /**
     * Gets the x-coordinate (column) of the current falling brick.
     *
     * @return The x position (column index).
     */
    public int getxPosition() {
        return xPosition;
    }

    /**
     * Gets the y-coordinate (row) of the current falling brick.
     *
     * @return The y position (row index).
     */
    public int getyPosition() {
        return yPosition;
    }

    /**
     * Gets a deep copy of the next brick's shape data.
     *
     * @return A copy of the 2D array representing the next brick shape.
     */
    public int[][] getNextBrickData() {
        return MatrixOperations.copy(nextBrickData);
    }

    /**
     * Gets a deep copy of the held brick's shape data.
     *
     * @return A copy of the 2D array representing the held brick shape, or {@code null} if no brick is currently held.
     */
    public int[][] getHeldBrickData() {
        return heldBrickData != null ? MatrixOperations.copy(heldBrickData) : null;
    }
}