package com.example;

import java.util.ArrayList;
import java.util.List;

public class Pawn extends Piece {
    public Pawn(int row, int col, boolean isWhite) {
        super(row, col, isWhite);
    }

    @Override
    public List<int[]> getPossibleMoves(Board board) {
        List<int[]> moves = new ArrayList<>();
        int direction = isWhite() ? -1 : 1; // Determine pawn direction based on color

        // One step forward
        int newRow = getRow() + direction;
        int newCol = getCol();
        if (isValidPosition(newRow, newCol) && board.getPiece(newRow, newCol) == null) {
            moves.add(new int[]{newRow, newCol});

            // Two steps forward for the first move
            if ((isWhite() && getRow() == 6) || (!isWhite() && getRow() == 1)) {
                newRow += direction;
                if (isValidPosition(newRow, newCol) && board.getPiece(newRow, newCol) == null) {
                    moves.add(new int[]{newRow, newCol});
                }
            }
        }

        // Diagonal captures
        // Right diagonal
        newRow = getRow() + direction;
        newCol = getCol() + 1;
        if (isValidPosition(newRow, newCol) && board.getPiece(newRow, newCol) != null && board.getPiece(newRow, newCol).isWhite() != isWhite()) {
            moves.add(new int[]{newRow, newCol});
        }

        // Left diagonal
        newCol = getCol() - 1;
        if (isValidPosition(newRow, newCol) && board.getPiece(newRow, newCol) != null && board.getPiece(newRow, newCol).isWhite() != isWhite()) {
            moves.add(new int[]{newRow, newCol});
        }

        return moves;
    }

    private boolean isValidPosition(int row, int col) {
        return row >= 0 && row < 8 && col >= 0 && col < 8;
    }
}



