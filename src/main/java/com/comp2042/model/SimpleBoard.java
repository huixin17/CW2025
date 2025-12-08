package com.comp2042.model;

import com.comp2042.logic.bricks.Brick;
import com.comp2042.logic.bricks.BrickGenerator;
import com.comp2042.logic.bricks.RandomBrickGenerator;
import com.comp2042.view.ClearRow;
import com.comp2042.view.NextShapeInfo;

import java.awt.*;

/**
 * Implementation of the game board for Tetris, managing the complete state of the game.
 * This includes the static background matrix, the currently falling brick's state (position, rotation),
 * and integration with secondary systems like score tracking, power-up management, and the hold feature.
 * It encapsulates all core game mechanics: movement, collision, rotation, line clearing, and special piece effects.
 *
 * @author COMP2042 Coursework
 */
public class SimpleBoard implements Board {

    private final int width; // The number of rows in the game matrix (height of the visible board)
    private final int height; // The number of columns in the game matrix (width of the visible board)
    private final BrickGenerator brickGenerator;
    private final BrickRotator brickRotator;
    private int[][] currentGameMatrix; // The static background matrix (width x height)
    private Point currentOffset; // The (X, Y) offset of the current falling brick
    private final Score score;
    private final PowerUpManager powerUpManager;
    private Brick heldBrick; // The brick currently stored in the hold area
    private boolean canHold = true; // Flag: allows holding only once per new piece placement
    private boolean isBombPiece = false; // Flag: indicates if the current piece is the bomb power-up piece

    // Temporary storage for visual effects, specifically the bomb explosion location
    private int bombEffectX, bombEffectY;
    private boolean shouldShowBombEffect = false;

    /**
     * Constructs a new SimpleBoard with the specified dimensions.
     * Initializes the game matrix, components (generator, rotator), and management systems.
     *
     * @param width The number of rows (vertical size) of the game board matrix.
     * @param height The number of columns (horizontal size) of the game board matrix.
     */
    public SimpleBoard(int width, int height) {
        this.width = width;
        this.height = height;
        currentGameMatrix = new int[width][height];
        brickGenerator = new RandomBrickGenerator();
        brickRotator = new BrickRotator();
        score = new Score();
        powerUpManager = new PowerUpManager();
    }

    /**
     * Attempts to move the current brick down by one row.
     *
     * @return {@code true} if the movement was successful (no collision); {@code false} if a collision
     * occurs at the next position (meaning the brick should be locked).
     */
    @Override
    public boolean moveBrickDown() {
        Point p = new Point(currentOffset);
        p.translate(0, 1); // Test position is one row lower
        boolean conflict = MatrixOperations.intersect(currentGameMatrix, brickRotator.getCurrentShape(), (int) p.getX(), (int) p.getY());

        if (conflict) {
            return false;
        } else {
            currentOffset = p;
            return true;
        }
    }


    /**
     * Attempts to move the current brick one column to the left.
     *
     * @return {@code true} if the movement was successful; {@code false} if the movement
     * results in a collision with the matrix boundaries or existing blocks.
     */
    @Override
    public boolean moveBrickLeft() {
        Point p = new Point(currentOffset);
        p.translate(-1, 0); // Test position is one column left
        boolean conflict = MatrixOperations.intersect(currentGameMatrix, brickRotator.getCurrentShape(), (int) p.getX(), (int) p.getY());

        if (conflict) {
            return false;
        } else {
            currentOffset = p;
            return true;
        }
    }

    /**
     * Attempts to move the current brick one column to the right.
     *
     * @return {@code true} if the movement was successful; {@code false} if the movement
     * results in a collision with the matrix boundaries or existing blocks.
     */
    @Override
    public boolean moveBrickRight() {
        Point p = new Point(currentOffset);
        p.translate(1, 0); // Test position is one column right
        boolean conflict = MatrixOperations.intersect(currentGameMatrix, brickRotator.getCurrentShape(), (int) p.getX(), (int) p.getY());

        if (conflict) {
            return false;
        } else {
            currentOffset = p;
            return true;
        }
    }

    /**
     * Attempts to rotate the current brick one step to the left (counter-clockwise).
     * This method implements a basic wall-kick mechanism to allow rotation even if the
     * initial rotated position conflicts with the wall or other blocks.
     *
     * @return {@code true} if the rotation (with or without a wall-kick shift) was successful;
     * {@code false} if rotation is impossible from the current position.
     */
    @Override
    public boolean rotateLeftBrick() {
        NextShapeInfo nextShape = brickRotator.getNextShape();
        int[][] rotatedShape = nextShape.getShape();
        int currentX = (int) currentOffset.getX();
        int currentY = (int) currentOffset.getY();

        // 1. Try rotation at current position first
        if (!MatrixOperations.intersect(currentGameMatrix, rotatedShape, currentX, currentY)) {
            brickRotator.setCurrentShape(nextShape.getPosition());
            return true;
        }

        // 2. Wall kick: try shifting left and right to find a valid rotation position
        // Standard wall kick offsets: try 1 left, 1 right, 2 left, 2 right
        int[] kickOffsets = {-1, 1, -2, 2};

        for (int offset : kickOffsets) {
            int testX = currentX + offset;
            if (!MatrixOperations.intersect(currentGameMatrix, rotatedShape, testX, currentY)) {
                // Found a valid position, update offset and rotate
                currentOffset = new Point(testX, currentY);
                brickRotator.setCurrentShape(nextShape.getPosition());
                return true;
            }
        }

        // 3. All attempts failed
        return false;
    }

    /**
     * Immediately drops the current brick to the lowest possible position (hard drop).
     *
     * @return {@code true} always, as the drop distance will be at least 0.
     */
    @Override
    public boolean hardDropBrick() {
        int[][] brickShape = brickRotator.getCurrentShape();
        int currentX = (int) currentOffset.getX();
        int currentY = (int) currentOffset.getY();

        // Calculate drop position by simulating movement downwards until collision
        int dropY = currentY;
        while (true) {
            int testY = dropY + 1;
            // Check intersection at the next row (testY)
            if (MatrixOperations.intersect(currentGameMatrix, brickShape, currentX, testY)) {
                break; // Found collision, stop drop simulation
            }
            dropY = testY;
            // Safety check against matrix overflow
            if (dropY >= width) {
                break;
            }
        }

        // Move brick to the calculated drop position
        currentOffset = new Point(currentX, dropY);
        return true;
    }

    /**
     * Calculates the vertical distance (in rows) the current brick would fall if a hard drop were executed.
     * This is useful for displaying a ghost piece or calculating hard drop score bonuses.
     *
     * @return The number of rows the brick will drop. Returns 0 if already at the bottom.
     */
    public int getHardDropDistance() {
        int[][] brickShape = brickRotator.getCurrentShape();
        int currentX = (int) currentOffset.getX();
        int currentY = (int) currentOffset.getY();

        int dropY = currentY;
        while (true) {
            int testY = dropY + 1;
            if (MatrixOperations.intersect(currentGameMatrix, brickShape, currentX, testY)) {
                break;
            }
            dropY = testY;
            if (dropY >= width) { // Check against board height (number of rows)
                break;
            }
        }

        return Math.max(0, dropY - currentY);
    }

    /**
     * Swaps the currently falling brick with the brick held in the storage area.
     * This action is restricted to once per piece placement.
     *
     * @return {@code true} if the hold/swap was executed; {@code false} if the hold
     * functionality has already been used for the current piece.
     */
    @Override
    public boolean holdBrick() {
        // Restriction: Can only hold once per piece placement
        if (!canHold) {
            return false;
        }

        Brick currentBrick = brickRotator.getBrick();

        if (heldBrick == null) {
            // Case 1: No held brick - Store current, generate new piece
            heldBrick = currentBrick;
            Brick newBrick = brickGenerator.getBrick();
            brickRotator.setBrick(newBrick);
        } else {
            // Case 2: Held brick exists - Swap current with held
            Brick temp = heldBrick;
            heldBrick = currentBrick;
            brickRotator.setBrick(temp);
        }

        // Reset brick position and lock the hold feature
        currentOffset = new Point(4, 0);
        canHold = false;
        return true;
    }

    /**
     * Generates a new brick from the generator and places it at the starting position (X=4, Y=0).
     * Resets the {@code canHold} flag, allowing the player to use the hold feature for this new piece.
     *
     * @return {@code true} if the new brick creation resulted in an immediate collision (Game Over condition);
     * {@code false} otherwise.
     */
    @Override
    public boolean createNewBrick() {
        Brick currentBrick = brickGenerator.getBrick();
        brickRotator.setBrick(currentBrick);
        currentOffset = new Point(4, 0);
        canHold = true; // Reset hold ability when new piece is created

        // Check for immediate collision (Game Over)
        return MatrixOperations.intersect(currentGameMatrix, brickRotator.getCurrentShape(), (int) currentOffset.getX(), (int) currentOffset.getY());
    }

    /**
     * Retrieves the current state of the static background matrix.
     *
     * @return The {@code int[][]} array representing the game board's settled blocks.
     */
    @Override
    public int[][] getBoardMatrix() {
        return currentGameMatrix;
    }

    /**
     * Compiles all data necessary for rendering the game view.
     *
     * @return A {@code ViewData} object containing the current brick shape, offset,
     * the next brick shape, and the held brick shape (if any).
     */
    @Override
    public ViewData getViewData() {
        int[][] heldBrickData = null;
        if (heldBrick != null) {
            // Get the first rotation state for display purposes
            heldBrickData = heldBrick.getShapeMatrix().get(0);
        }
        return new ViewData(
                brickRotator.getCurrentShape(),
                (int) currentOffset.getX(),
                (int) currentOffset.getY(),
                brickGenerator.getNextBrick().getShapeMatrix().get(0),
                heldBrickData
        );
    }

    /**
     * Locks the current brick into the static background matrix. If the {@code isBombPiece}
     * flag is set, it executes the bomb explosion effect instead of merging the piece.
     */
    @Override
    public void mergeBrickToBackground() {
        // Special case: If the current piece is a bomb, execute the explosion
        if (isBombPiece) {
            Point bombCenter = getBombCenterPosition();
            if (bombCenter != null) {
                // Clear the 4x4 area around the bomb's center
                clearBombArea((int)bombCenter.getX(), (int)bombCenter.getY());
                // Store position for UI to render the explosion animation
                bombEffectX = (int)bombCenter.getX();
                bombEffectY = (int)bombCenter.getY();
                shouldShowBombEffect = true;
            }
            // Reset bomb flag after use
            isBombPiece = false;
            return; // The bomb piece disappears upon explosion, it is not merged
        }

        // Normal case: Merge the piece to the background matrix
        currentGameMatrix = MatrixOperations.merge(
                currentGameMatrix,
                brickRotator.getCurrentShape(),
                (int) currentOffset.getX(),
                (int) currentOffset.getY()
        );
    }


    /**
     * Checks the static background matrix for any completed rows and removes them.
     *
     * @return A {@code ClearRow} object containing the new matrix state and the number
     * of rows that were cleared.
     */
    @Override
    public ClearRow clearRows() {
        ClearRow clearRow = MatrixOperations.checkRemoving(currentGameMatrix);
        currentGameMatrix = clearRow.getNewMatrix();
        return clearRow;
    }

    /**
     * Retrieves the game's score manager.
     *
     * @return The {@code Score} object.
     */
    @Override
    public Score getScore() {
        return score;
    }

    /**
     * Initiates a new game state. Resets the board matrix, score, power-up manager,
     * and hold feature state, then creates the first brick.
     */
    @Override
    public void newGame() {
        currentGameMatrix = new int[width][height];
        score.reset();
        powerUpManager.reset();
        heldBrick = null;
        canHold = true;
        isBombPiece = false;
        createNewBrick();
    }

    /**
     * Retrieves the power-up manager for accessing inventory and performing transactions.
     *
     * @return The {@code PowerUpManager} object.
     */
    public PowerUpManager getPowerUpManager() {
        return powerUpManager;
    }

    /**
     * Activates the bomb piece flag, indicating that the next merged piece will trigger
     * a bomb explosion effect instead of locking normally.
     *
     * @param isBomb {@code true} to activate the bomb piece effect; {@code false} to deactivate.
     */
    public void setBombPiece(boolean isBomb) {
        this.isBombPiece = isBomb;
    }

    /**
     * Implements the "Clear Rows" power-up effect. It clears the specified number of rows
     * from the bottom of the board and shifts all blocks above downwards accordingly.
     *
     * @param numRows The number of rows to clear from the bottom (e.g., 4 for a Quad Clear).
     * @return {@code true} if the operation was executed; {@code false} if {@code numRows} is invalid.
     */
    public boolean clearRowsPowerUp(int numRows) {
        if (numRows <= 0 || numRows > width) {
            return false;
        }

        int destinationRow = width - 1; // Start filling from the absolute bottom

        // Iterate upward through the matrix rows
        for (int row = width - 1; row >= 0; row--) {
            // Check if the current row falls within the bottom 'numRows' that should be cleared
            boolean rowIsScheduledToClear = (row >= width - numRows);

            if (!rowIsScheduledToClear) {
                // This row should be kept: copy it down to the next available destinationRow
                for (int col = 0; col < height; col++) {
                    currentGameMatrix[destinationRow][col] = currentGameMatrix[row][col];
                }
                destinationRow--; // Move destination up for the next non-cleared row
            }
        }

        // Fill any remaining rows (from row 0 up to destinationRow) with zeros
        for (int row = destinationRow; row >= 0; row--) {
            for (int col = 0; col < height; col++) {
                currentGameMatrix[row][col] = 0;
            }
        }

        return true;
    }

    /**
     * Implements the Bomb power-up effect by clearing a 4x4 area centered at the specified coordinates.
     * Coordinates are expected to be the world coordinates (after the piece has dropped).
     *
     * @param centerX The column index (X-coordinate) of the center of the bomb blast.
     * @param centerY The row index (Y-coordinate) of the center of the bomb blast.
     * @return {@code true} always, as the attempt to clear is made regardless of boundaries.
     */
    public boolean clearBombArea(int centerX, int centerY) {
        // Define the 4x4 area boundaries around the center (center +/- 1 cell on both axes, then adjust for 4 cells total)
        int startRow = centerY - 1;  // Row start: 1 row above center
        int endRow = centerY + 2;    // Row end: 2 rows below center (makes 4 rows total: c-1, c, c+1, c+2)
        int startCol = centerX - 1;  // Col start: 1 col left of center
        int endCol = centerX + 2;    // Col end: 2 cols right of center (makes 4 cols total: c-1, c, c+1, c+2)

        // Iterate and clear the 4x4 area, enforcing board boundaries
        for (int row = startRow; row <= endRow; row++) {
            for (int col = startCol; col <= endCol; col++) {
                // Check bounds: (row must be < width, col must be < height, both must be >= 0)
                if (row >= 0 && row < width && col >= 0 && col < height) {
                    currentGameMatrix[row][col] = 0; // Clear the block
                }
            }
        }

        return true;
    }

    /**
     * Calculates the world coordinates of the center point of the current falling brick.
     * This is crucial for determining the origin of the bomb explosion effect.
     *
     * @return A {@code Point} object representing the (X, Y) world coordinates of the
     * bomb's center of effect.
     */
    private Point getBombCenterPosition() {
        int[][] brickShape = brickRotator.getCurrentShape();
        int offsetX = (int) currentOffset.getX();
        int offsetY = (int) currentOffset.getY();

        // The simplest way to find a "center" is to find the first non-zero cell in the shape
        // and use its world position as the bomb's center.
        for (int i = 0; i < brickShape.length; i++) {
            for (int j = 0; j < brickShape[i].length; j++) {
                if (brickShape[i][j] != 0) {
                    int worldX = offsetX + j; // Column
                    int worldY = offsetY + i; // Row
                    return new Point(worldX, worldY);
                }
            }
        }

        // Fallback: use the brick's current offset position if the shape is somehow empty
        return new Point(offsetX, offsetY);
    }

    /**
     * Retrieves the calculated center position for the bomb visual effect.
     *
     * @return A {@code Point} representing the (X, Y) where the explosion animation should play.
     */
    public Point getBombCenterForEffect() {
        return getBombCenterPosition();
    }

    /**
     * Queries if the bomb explosion visual effect should be rendered.
     * This flag is set upon a successful bomb merge and cleared after the view consumes it.
     *
     * @return {@code true} if the bomb effect flag is currently active.
     */
    public boolean shouldShowBombEffect() {
        return shouldShowBombEffect;
    }

    /**
     * Retrieves the X-coordinate (column) of the last bomb effect's center.
     *
     * @return The column index.
     */
    public int getBombEffectX() {
        return bombEffectX;
    }

    /**
     * Retrieves the Y-coordinate (row) of the last bomb effect's center.
     *
     * @return The row index.
     */
    public int getBombEffectY() {
        return bombEffectY;
    }

    /**
     * Resets the flag indicating that the bomb effect has been displayed by the view.
     */
    public void clearBombEffectFlag() {
        shouldShowBombEffect = false;
    }

    /**
     * Checks if the current falling piece is designated as the bomb piece.
     *
     * @return {@code true} if the bomb power-up is active for the current piece.
     */
    public boolean isBombPieceActive() {
        return isBombPiece;
    }
}