package com.example;

import java.util.ArrayList;
import java.util.List;

public class Knight extends Piece {

    public Knight(int row, int col, boolean isWhite) {
        super(row, col, isWhite);
    }

    @Override
    public List<int[]> getPossibleMoves(Game game) {
        List<int[]> allMoves = new ArrayList<>();
        int[][] moves = {
            {2, 1}, {2, -1}, {-2, 1}, {-2, -1},
            {1, 2}, {1, -2}, {-1, 2}, {-1, -2}
        };

        for (int[] move : moves) {
            int newRow = getRow() + move[0];
            int newCol = getCol() + move[1];

            if (isValidPosition(newRow, newCol)) {
                Piece piece = game.getPiece(newRow, newCol);

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
        return new Knight(getRow(), getCol(), isWhite());
    }

}
