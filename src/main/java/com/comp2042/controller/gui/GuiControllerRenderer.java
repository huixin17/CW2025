package com.comp2042.controller.gui;


import javafx.geometry.Point2D;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import com.comp2042.model.MatrixOperations;
import com.comp2042.model.ViewData;

/**
 * Manages all visual rendering aspects for the Tetris game GUI.
 * Handles the display of the main game board, the active falling brick,
 * piece previews, the ghost piece projection, and board sizing/positioning.
 *
 * @author COMP2042 Coursework
 */
class GuiControllerRenderer {

    // Defines the uniform size (in pixels) for all individual blocks.
    private static final int BRICK_SIZE = 22;

    private final GuiController guiController;

    /**
     * Constructs the renderer, linking it to the controlling instance.
     *
     * @param guiController the GuiController instance managing the game state
     */
    GuiControllerRenderer(GuiController guiController) {
        this.guiController = guiController;
    }

    /**
     * Initializes the entire game view based on the starting state.
     * Sets up the display matrices for the board and the current brick,
     * configures the size of the game panel, and renders initial previews.
     *
     * @param boardMatrix the 2D array representing the game board (including hidden rows)
     * @param brick the initial active brick's view data
     */
    void initGameView(int[][] boardMatrix, ViewData brick) {
        guiController.currentBoardMatrix = boardMatrix;
        guiController.displayMatrix = new Rectangle[boardMatrix.length][boardMatrix[0].length];

        // Initialize Rectangles for the visible board area (starting at row index 2)
        for (int i = 2; i < boardMatrix.length; i++) {
            for (int j = 0; j < boardMatrix[i].length; j++) {
                Rectangle rectangle = new Rectangle(BRICK_SIZE, BRICK_SIZE);
                rectangle.setFill(Color.TRANSPARENT);
                guiController.displayMatrix[i][j] = rectangle;
                guiController.gamePanel.add(rectangle, j, i - 2);
            }
        }

        // Calculate and set the preferred size of the main game panel
        int cols = boardMatrix[0].length;
        int rowsVisible = boardMatrix.length - 2;
        double w = cols * BRICK_SIZE + (cols - 1) * guiController.gamePanel.getHgap();
        double h = rowsVisible * BRICK_SIZE + (rowsVisible - 1) * guiController.gamePanel.getVgap();
        w += 2; h += 4;
        guiController.gamePanel.setPrefSize(w, h);
        guiController.gamePanel.setMinSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
        guiController.gamePanel.setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);

        // Initialize Rectangles for the current active brick
        guiController.rectangles = new Rectangle[brick.getBrickData().length][brick.getBrickData()[0].length];
        for (int i = 0; i < brick.getBrickData().length; i++) {
            for (int j = 0; j < brick.getBrickData()[i].length; j++) {
                Rectangle rectangle = new Rectangle(BRICK_SIZE, BRICK_SIZE);
                setRectangleData(brick.getBrickData()[i][j], rectangle);
                guiController.rectangles[i][j] = rectangle;
                guiController.brickPanel.add(rectangle, j, i);
            }
        }

        // Create and set up the shadow panel for ghost piece projection
        guiController.shadowPanel = new GridPane();
        guiController.shadowPanel.setHgap(guiController.brickPanel.getHgap());
        guiController.shadowPanel.setVgap(guiController.brickPanel.getVgap());
        guiController.shadowPanel.setMouseTransparent(true);
        int[][] brickData = brick.getBrickData();
        if (brickData != null && brickData.length > 0 && brickData[0].length > 0) {
            guiController.shadowRectangles = new Rectangle[brickData.length][brickData[0].length];
            for (int i = 0; i < brickData.length; i++) {
                for (int j = 0; j < brickData[i].length; j++) {
                    Rectangle shadowRect = new Rectangle(BRICK_SIZE, BRICK_SIZE);
                    shadowRect.setFill(Color.GRAY);
                    shadowRect.setOpacity(0.55);
                    shadowRect.setStroke(Color.DARKGRAY);
                    shadowRect.setStrokeWidth(1.0);
                    guiController.shadowRectangles[i][j] = shadowRect;
                    guiController.shadowPanel.add(shadowRect, j, i);
                }
            }
        }

        // Add shadow panel to the main root pane for absolute positioning
        Pane root = (Pane) guiController.gameBoard.getParent();
        if (root != null && !root.getChildren().contains(guiController.shadowPanel)) {
            root.getChildren().add(guiController.shadowPanel);
        }

        // Set initial absolute position of the falling brick
        Point2D origin = gamePanelOriginInRoot();
        guiController.brickPanel.setLayoutX(origin.getX() + brick.getxPosition() * guiController.brickPanel.getVgap() + brick.getxPosition() * BRICK_SIZE);
        int displayRow = brick.getyPosition() - 2;
        guiController.brickPanel.setLayoutY(origin.getY() + displayRow * (guiController.brickPanel.getHgap() + BRICK_SIZE));

        // Initial rendering of auxiliary views
        renderNextPreview(brick.getNextBrickData());
        renderHoldPreview(brick.getHeldBrickData());
        updateShadow(brick);
    }

    /**
     * Updates the position and appearance of the active falling brick.
     * This method is called during the game loop if the game is not paused.
     *
     * @param brick The updated view data of the current brick.
     */
    void refreshBrick(ViewData brick) {
        if (guiController.isPause.getValue() == Boolean.FALSE) {
            // Update brick position
            Point2D origin = gamePanelOriginInRoot();
            guiController.brickPanel.setLayoutX(origin.getX() + brick.getxPosition() * guiController.brickPanel.getVgap() + brick.getxPosition() * BRICK_SIZE);
            int displayRow = brick.getyPosition() - 2;
            guiController.brickPanel.setLayoutY(origin.getY() + displayRow * (guiController.brickPanel.getHgap() + BRICK_SIZE));

            // Update brick block data (color, rotation)
            for (int i = 0; i < brick.getBrickData().length; i++) {
                for (int j = 0; j < brick.getBrickData()[i].length; j++) {
                    setRectangleData(brick.getBrickData()[i][j], guiController.rectangles[i][j]);
                }
            }

            // Refresh auxiliary views
            renderNextPreview(brick.getNextBrickData());
            renderHoldPreview(brick.getHeldBrickData());
            updateShadow(brick);
        }
    }

    /**
     * Updates the static background of the game board.
     * This is called when lines are cleared or a piece locks into place.
     *
     * @param board The updated 2D board matrix.
     */
    void refreshGameBackground(int[][] board) {
        guiController.currentBoardMatrix = board;
        for (int i = 2; i < board.length && i < guiController.displayMatrix.length; i++) {
            for (int j = 0; j < board[i].length && j < guiController.displayMatrix[i].length; j++) {
                if (guiController.displayMatrix[i][j] != null) {
                    setRectangleData(board[i][j], guiController.displayMatrix[i][j]);
                }
            }
        }
    }

    /**
     * Maps the integer color ID (from model) to a JavaFX Paint object.
     *
     * @param i The integer ID representing the brick type (0 for empty).
     * @return The corresponding JavaFX Color.
     */
    private Paint getFillColor(int i) {
        switch (i) {
            case 0: return Color.TRANSPARENT;
            case 1: return Color.AQUA;
            case 2: return Color.BLUEVIOLET;
            case 3: return Color.DARKGREEN;
            case 4: return Color.YELLOW;
            case 5: return Color.RED;
            case 6: return Color.BEIGE;
            case 7: return Color.BURLYWOOD;
            default: return Color.WHITE;
        }
    }

    /**
     * Applies the fill color, arc properties, and a glow effect to a Rectangle.
     *
     * @param color The integer color ID.
     * @param rectangle The Rectangle instance to style.
     */
    private void setRectangleData(int color, Rectangle rectangle) {
        rectangle.setFill(getFillColor(color));
        rectangle.setArcHeight(0);
        rectangle.setArcWidth(0);

        // Apply glow effect for non-empty bricks
        if (color != 0) {
            javafx.scene.effect.Glow glow = new javafx.scene.effect.Glow();
            glow.setLevel(0.6);
            rectangle.setEffect(glow);
        } else {
            rectangle.setEffect(null); // Remove effect for transparent bricks
        }
    }

    /**
     * Calculates the row position where the current brick will land (the ghost piece position).
     *
     * @param brickData The shape of the current brick.
     * @param x The current column position.
     * @param y The current row position.
     * @return The final row index where the brick will stop.
     */
    private int calculateDropPosition(int[][] brickData, int x, int y) {
        if (guiController.currentBoardMatrix == null) return y;

        int dropY = y;
        // Simulate downward movement until a collision is detected
        while (true) {
            int testY = dropY + 1;
            // Check collision with existing blocks or the floor
            if (MatrixOperations.intersect(guiController.currentBoardMatrix, brickData, x, testY)) {
                break; // Collision found
            }
            dropY = testY;
            // Loop termination safety check
            if (dropY >= guiController.currentBoardMatrix.length) {
                break;
            }
        }
        return dropY;
    }

    /**
     * Updates the visibility, shape, and position of the shadow (ghost) piece.
     *
     * @param brick The current active brick's view data.
     */
    private void updateShadow(ViewData brick) {
        if (guiController.shadowPanel == null || guiController.currentBoardMatrix == null) {
            return;
        }

        int[][] brickData = brick.getBrickData();
        int dropY = calculateDropPosition(brickData, brick.getxPosition(), brick.getyPosition());

        // Hide shadow if the drop position is the same as the current position or above
        if (dropY <= brick.getyPosition()) {
            guiController.shadowPanel.setVisible(false);
            return;
        }

        guiController.shadowPanel.setVisible(true);

        // Recreate shadow Rectangles if the brick shape/size has changed (due to rotation)
        if (brickData == null || brickData.length == 0 || brickData[0].length == 0) {
            guiController.shadowPanel.setVisible(false);
            return;
        }
        if (guiController.shadowRectangles == null || guiController.shadowRectangles.length != brickData.length ||
                (brickData.length > 0 && (guiController.shadowRectangles[0] == null || guiController.shadowRectangles[0].length != brickData[0].length))) {

            guiController.shadowPanel.getChildren().clear();
            guiController.shadowRectangles = new Rectangle[brickData.length][brickData[0].length];
            for (int i = 0; i < brickData.length; i++) {
                for (int j = 0; j < brickData[i].length; j++) {
                    Rectangle shadowRect = new Rectangle(BRICK_SIZE, BRICK_SIZE);
                    shadowRect.setFill(Color.GRAY);
                    shadowRect.setOpacity(0.7);
                    shadowRect.setStroke(Color.DARKGRAY);
                    shadowRect.setStrokeWidth(1.5);
                    guiController.shadowRectangles[i][j] = shadowRect;
                    guiController.shadowPanel.add(shadowRect, j, i);
                }
            }
        }

        // Apply visibility and fixed shadow styling to the ghost piece
        Color shadowGrey = Color.DARKGRAY;
        Color shadowStroke = Color.BLACK;
        for (int i = 0; i < brickData.length; i++) {
            for (int j = 0; j < brickData[i].length; j++) {
                Rectangle shadowRect = guiController.shadowRectangles[i][j];
                if (shadowRect != null) {
                    if (brickData[i][j] != 0) {
                        shadowRect.setVisible(true);
                        shadowRect.setFill(shadowGrey);
                        shadowRect.setStroke(shadowStroke);
                    } else {
                        shadowRect.setVisible(false);
                    }
                }
            }
        }

        // Position the shadow panel at the calculated drop location
        Point2D origin = gamePanelOriginInRoot();
        guiController.shadowPanel.setLayoutX(origin.getX() + brick.getxPosition() * guiController.shadowPanel.getVgap() + brick.getxPosition() * BRICK_SIZE);
        int displayRow = dropY - 2;
        guiController.shadowPanel.setLayoutY(origin.getY() + displayRow * (guiController.shadowPanel.getHgap() + BRICK_SIZE));

        // Ensure the shadow remains behind the active brick
        guiController.shadowPanel.toBack();
    }

    /**
     * Clears and redraws the 'Next Piece' preview panel based on the data provided.
     *
     * @param next The 2D array representing the next brick's shape.
     */
    private void renderNextPreview(int[][] next) {
        if (next == null || next.length == 0) return;
        guiController.nextPanel.getChildren().clear();
        guiController.nextPreview = new Rectangle[next.length][next[0].length];
        for (int i = 0; i < next.length; i++) {
            for (int j = 0; j < next[i].length; j++) {
                Rectangle r = new Rectangle(BRICK_SIZE, BRICK_SIZE);
                setRectangleData(next[i][j], r);
                guiController.nextPreview[i][j] = r;
                guiController.nextPanel.add(r, j, i);
            }
        }
    }

    /**
     * Clears and redraws the 'Held Piece' preview panel.
     * Renders a placeholder grid if no piece is currently held.
     *
     * @param hold The 2D array representing the held brick's shape.
     */
    private void renderHoldPreview(int[][] hold) {
        if (guiController.holdPanel == null) return;
        guiController.holdPanel.getChildren().clear();
        if (hold == null || hold.length == 0) {
            // Show empty placeholder (4x4 grid of transparent cells)
            guiController.holdPreview = new Rectangle[4][4];
            for (int i = 0; i < 4; i++) {
                for (int j = 0; j < 4; j++) {
                    Rectangle r = new Rectangle(BRICK_SIZE, BRICK_SIZE);
                    r.setFill(Color.TRANSPARENT);
                    r.setStroke(Color.rgb(255, 255, 255, 0.1));
                    r.setStrokeWidth(0.5);
                    guiController.holdPreview[i][j] = r;
                    guiController.holdPanel.add(r, j, i);
                }
            }
            return;
        }

        // Render the actual held piece
        guiController.holdPreview = new Rectangle[hold.length][hold[0].length];
        for (int i = 0; i < hold.length; i++) {
            for (int j = 0; j < hold[i].length; j++) {
                Rectangle r = new Rectangle(BRICK_SIZE, BRICK_SIZE);
                setRectangleData(hold[i][j], r);
                guiController.holdPreview[i][j] = r;
                guiController.holdPanel.add(r, j, i);
            }
        }
    }

    /**
     * Calculates the absolute screen coordinates of the gamePanel's top-left corner
     * relative to the root container. This is necessary for absolute positioning
     * of the floating brick and shadow panels.
     *
     * @return The Point2D coordinate of the gamePanel origin within the root pane.
     */
    private Point2D gamePanelOriginInRoot() {
        Pane root = (Pane) guiController.gameBoard.getParent();
        Point2D scenePt = guiController.gamePanel.localToScene(0, 0);
        return root.sceneToLocal(scenePt);
    }
}