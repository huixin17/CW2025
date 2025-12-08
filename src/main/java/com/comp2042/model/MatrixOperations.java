package com.comp2042.model;

import com.comp2042.view.ClearRow;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Provides static utility methods for performing core matrix manipulation operations,
 * such as collision detection, deep copying, merging, and row removal logic,
 * typically used in grid-based games like Tetris.
 */
public class MatrixOperations {


    /**
     * Private constructor to prevent instantiation of this utility class.
     */
    private MatrixOperations(){

    }

    /**
     * Determines if a mobile structure (brick) intersects with the stationary
     * game matrix or the boundary at a given translation (x, y).
     *
     * @param matrix The fixed game grid representing placed blocks.
     * @param brick The 2D array representing the mobile block's structure.
     * @param x The column index (x-coordinate) of the top-left corner of the brick within the matrix.
     * @param y The row index (y-coordinate) of the top-left corner of the brick within the matrix.
     * @return {@code true} if any non-zero element of the brick overlaps with a boundary or an
     * occupied cell in the matrix, {@code false} otherwise.
     */
    public static boolean intersect(final int[][] matrix, final int[][] brick, int x, int y) {
        for (int i = 0; i < brick.length; i++) {
            for (int j = 0; j < brick[i].length; j++) {
                int targetX = x + i;
                int targetY = y + j;
                // Check if the current brick element is occupied (non-zero) AND
                // if the target position is out of bounds OR already occupied in the matrix.
                if (brick[j][i] != 0 && (checkOutOfBound(matrix, targetX, targetY) || matrix[targetY][targetX] != 0)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Checks if a target coordinate (x, y) falls outside the valid bounds of the game matrix.
     *
     * @param matrix The 2D game grid.
     * @param targetX The column index to check.
     * @param targetY The row index to check.
     * @return {@code true} if the coordinate is outside the matrix boundaries, {@code false} otherwise.
     */
    private static boolean checkOutOfBound(int[][] matrix, int targetX, int targetY) {
        boolean returnValue = true;
        // Check if targetY is within row bounds AND targetX is within column bounds for that row
        if (targetX >= 0 && targetY >= 0 && targetY < matrix.length && targetX < matrix[0].length) {
            returnValue = false;
        }
        return returnValue;
    }

    /**
     * Creates a deep copy of a 2D integer array (matrix).
     *
     * @param original The source 2D array.
     * @return A new 2D array with all elements copied recursively, ensuring independence from the original.
     */
    public static int[][] copy(int[][] original) {
        int[][] myInt = new int[original.length][];
        for (int i = 0; i < original.length; i++) {
            int[] aMatrix = original[i];
            int aLength = aMatrix.length;
            myInt[i] = new int[aLength];
            // Use System.arraycopy for efficient element-level copying
            System.arraycopy(aMatrix, 0, myInt[i], 0, aLength);
        }
        return myInt;
    }

    /**
     * Merges the structure of a brick into the main game matrix at a specified position.
     *
     * @param filledFields The stationary background matrix.
     * @param brick The mobile block's structure.
     * @param x The column offset for merging.
     * @param y The row offset for merging.
     * @return A new matrix representing the result of the merge operation.
     */
    public static int[][] merge(int[][] filledFields, int[][] brick, int x, int y) {
        int[][] copy = copy(filledFields);
        for (int i = 0; i < brick.length; i++) {
            for (int j = 0; j < brick[i].length; j++) {
                int targetX = x + i;
                int targetY = y + j;
                // Check bounds before accessing array and ensure brick element is non-empty
                if (targetY >= 0 && targetY < copy.length &&
                        targetX >= 0 && targetX < copy[targetY].length &&
                        brick[j][i] != 0) {
                    copy[targetY][targetX] = brick[j][i];
                }
            }
        }
        return copy;
    }

    /**
     * Analyzes the matrix for completed rows, removes them, and generates the new state
     * and associated score bonus.
     *
     * @param matrix The current game grid state.
     * @return A {@code ClearRow} object containing the number of cleared rows, the resulting
     * matrix after removal, and the calculated score bonus.
     */
    public static ClearRow checkRemoving(final int[][] matrix) {
        // Temporary structure to hold rows that are NOT cleared
        Deque<int[]> newRows = new ArrayDeque<>();
        List<Integer> clearedRows = new ArrayList<>();

        // Iterate through the matrix from top to bottom
        for (int i = 0; i < matrix.length; i++) {
            int[] tmpRow = new int[matrix[i].length];
            boolean rowToClear = true;
            // Check if the row is completely filled (no zeros)
            for (int j = 0; j < matrix[0].length; j++) {
                if (matrix[i][j] == 0) {
                    rowToClear = false;
                }
                tmpRow[j] = matrix[i][j];
            }
            // If the row is filled, record its index; otherwise, queue the row for the new matrix
            if (rowToClear) {
                clearedRows.add(i);
            } else {
                newRows.add(tmpRow);
            }
        }

        // Initialize the resulting matrix with the original dimensions, pre-filled with empty (zero) rows
        int[][] tmp = new int[matrix.length][matrix[0].length];

        // Fill the new matrix from the bottom up using the queued non-cleared rows.
        // This effectively moves existing blocks down to fill the gaps created by cleared rows.
        for (int i = matrix.length - 1; i >= 0; i--) {
            int[] row = newRows.pollLast();
            if (row != null) {
                tmp[i] = row;
            } else {
                // Once the queue is empty, the remaining top rows are left as zeroed (empty)
                break;
            }
        }

        // Calculate score bonus using a quadratic formula (e.g., 1 row = 50, 4 rows = 50*16 = 800)
        int scoreBonus = 50 * clearedRows.size() * clearedRows.size();
        return new ClearRow(clearedRows.size(), tmp, scoreBonus);
    }

    /**
     * Creates a deep copy of a list of 2D integer arrays (matrices) using Java Streams.
     * This ensures that modifying the matrices in the new list does not affect the originals.
     *
     * @param list The original list of 2D arrays.
     * @return A new list containing deep copies of all matrices.
     */
    public static List<int[][]> deepCopyList(List<int[][]> list){
        return list.stream().map(MatrixOperations::copy).collect(Collectors.toList());
    }

}