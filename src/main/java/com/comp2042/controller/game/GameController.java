package com.comp2042.controller.game;

import com.comp2042.controller.gui.GuiController;
import com.comp2042.model.Board;
import com.comp2042.model.DownData;
import com.comp2042.model.PowerUp;
import com.comp2042.model.SimpleBoard;
import com.comp2042.model.ViewData;
import com.comp2042.view.ClearRow;

/**
 * Controls the main gameplay flow.
 * Connects user inputs, the board logic, and the GUI updates.
 * Also handles scoring, power-ups, and transitions between game states.
 */
public class GameController implements InputEventListener {

    // Main game board instance (25 rows x 10 columns)
    private Board board = new SimpleBoard(25, 10);

    private final GuiController viewGuiController;

    /**
     * Sets up the controller and prepares the initial game state.
     * Creates the first piece, links UI events, and binds score/skill point labels.
     *
     * @param c the GUI controller that displays the game
     */
    public GameController(GuiController c) {
        viewGuiController = c;
        board.createNewBrick();
        viewGuiController.setEventListener(this);
        viewGuiController.initGameView(board.getBoardMatrix(), board.getViewData());
        viewGuiController.bindScore(board.getScore().scoreProperty());
        viewGuiController.bindSkillPoints(getPowerUpManager().skillPointsProperty());
    }

    /**
     * Called whenever the piece falls down by one step.
     * If the piece can’t move any further, it is locked in place,
     * cleared rows are handled, and a new piece is spawned.
     *
     * @return DownData including removed line info and updated view
     */
    @Override
    public DownData onDownEvent(MoveEvent event) {
        boolean canMove = board.moveBrickDown();
        ClearRow clearRow = null;

        if (!canMove) {
            // Piece has landed; merge into the grid
            board.mergeBrickToBackground();
            clearRow = board.clearRows();

            if (clearRow.getLinesRemoved() > 0) {
                int bonus = clearRow.getScoreBonus();
                board.getScore().add(bonus);
                getPowerUpManager().awardSkillPoints(bonus); // skill points based on score
            }

            // Check if new brick leads to game over
            if (board.createNewBrick()) {
                viewGuiController.gameOver();
            }

            viewGuiController.refreshGameBackground(board.getBoardMatrix());
        } else {
            // Reward player for manual soft-drop
            if (event.getEventSource() == EventSource.USER) {
                board.getScore().add(1);
                getPowerUpManager().awardSkillPoints(1);
            }
        }

        return new DownData(clearRow, board.getViewData());
    }

    /** Moves the active piece one column to the left. */
    @Override
    public ViewData onLeftEvent(MoveEvent event) {
        board.moveBrickLeft();
        return board.getViewData();
    }

    /** Moves the active piece one column to the right. */
    @Override
    public ViewData onRightEvent(MoveEvent event) {
        board.moveBrickRight();
        return board.getViewData();
    }

    /** Rotates the piece counter-clockwise. */
    @Override
    public ViewData onRotateEvent(MoveEvent event) {
        board.rotateLeftBrick();
        return board.getViewData();
    }

    /**
     * Handles hard-drop: instantly sends the piece to the lowest valid row.
     * Extra points are awarded based on how far the brick drops.
     */
    @Override
    public DownData onHardDropEvent(MoveEvent event) {
        int dropDistance = ((SimpleBoard) board).getHardDropDistance();
        boolean dropped = board.hardDropBrick();

        if (dropped) {
            int bonus = dropDistance * 2;  // scoring rule
            board.getScore().add(bonus);
            getPowerUpManager().awardSkillPoints(bonus);
        }

        board.mergeBrickToBackground();

        // Show explosion animation for bomb-type pieces
        SimpleBoard simpleBoard = (SimpleBoard) board;
        if (simpleBoard.shouldShowBombEffect()) {
            viewGuiController.showBoomEffect(simpleBoard.getBombEffectX(), simpleBoard.getBombEffectY());
            simpleBoard.clearBombEffectFlag();
        }

        ClearRow clearRow = board.clearRows();
        if (clearRow.getLinesRemoved() > 0) {
            int bonus = clearRow.getScoreBonus();
            board.getScore().add(bonus);
            getPowerUpManager().awardSkillPoints(bonus);
        }

        if (board.createNewBrick()) {
            viewGuiController.gameOver();
        }

        viewGuiController.refreshGameBackground(board.getBoardMatrix());
        return new DownData(clearRow, board.getViewData());
    }

    /**
     * Stores the current piece and swaps with the previously held piece (if any).
     */
    @Override
    public ViewData onHoldEvent(MoveEvent event) {
        board.holdBrick();
        return board.getViewData();
    }

    /**
     * Resets the entire game and starts from a clean board.
     */
    @Override
    public void createNewGame() {
        board.newGame();
        viewGuiController.refreshGameBackground(board.getBoardMatrix());
    }

    /** Shortcut to the PowerUpManager stored inside the board. */
    public com.comp2042.model.PowerUpManager getPowerUpManager() {
        return ((SimpleBoard) board).getPowerUpManager();
    }

    /** Returns the current board instance. */
    public Board getBoard() {
        return board;
    }

    /**
     * Attempts to purchase a power-up using available skill points.
     */
    public boolean purchasePowerUp(PowerUp powerUp) {
        return getPowerUpManager().purchasePowerUp(powerUp);
    }

    /**
     * Uses a power-up from the player's inventory and applies its effect.
     *
     * - ROW_CLEARER → removes bottom rows
     * - SLOW_MOTION → slows falling speed
     * - BOMB_PIECE → next piece becomes a bomb
     */
    public boolean activatePowerUp(PowerUp powerUp) {
        if (!getPowerUpManager().usePowerUp(powerUp)) {
            return false;
        }

        switch (powerUp) {
            case ROW_CLEARER:
                boolean cleared = ((SimpleBoard) board).clearRowsPowerUp(3);
                if (cleared) {
                    viewGuiController.refreshGameBackground(board.getBoardMatrix());
                    viewGuiController.refreshBrick(board.getViewData());
                }
                return cleared;

            case SLOW_MOTION:
                viewGuiController.applySlowMotion();
                return true;

            case BOMB_PIECE:
                ((SimpleBoard) board).setBombPiece(true);
                return true;

            default:
                return false;
        }
    }
}