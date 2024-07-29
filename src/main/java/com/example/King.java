package com.example;

import java.util.ArrayList;
import java.util.List;

public class King extends Piece {
    boolean check;

    public King(int row, int col, boolean isWhite) {
        super(row, col, isWhite);
        check = false;
    }

    @Override
    public List<int[]> getPossibleMoves(Board board) {
        List<int[]> allMoves = new ArrayList<>();
        int[][] directions = {
            {1, 0}, {-1, 0}, // vertical
            {0, 1}, {0, -1}, // horizontal
            {1, 1}, {1, -1}, // diagonal down
            {-1, 1}, {-1, -1} // diagonal up
        };

        for (int[] direction : directions) {
            int newRow = getRow() + direction[0];
            int newCol = getCol() + direction[1];

            if (isValidPosition(newRow, newCol)) {
                Piece piece = board.getPiece(newRow, newCol);

                if (piece == null || piece.isWhite() != this.isWhite()) {
                    allMoves.add(new int[]{newRow, newCol});
                }
            }
        }

        return allMoves;
    }

    private boolean isValidPosition(int row, int col) {
        return row >= 0 && row < 8 && col >= 0 && col < 8;
    }

    @Override
    public Piece copy() {
        // Create a new King object with the same properties
        return new King(getRow(), getCol(), isWhite());
    }


}


