package com.example;

import java.util.ArrayList;
import java.util.List;

public class King extends Piece {
    boolean check;
    boolean hasMoved;

    public King(int row, int col, boolean isWhite) {
        super(row, col, isWhite);
        check = false;
    }

    @Override
    public List<int[]> getPossibleMoves(Game game) {
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
                Piece piece = game.getPiece(newRow, newCol);

                if (piece == null || piece.isWhite() != this.isWhite()) {
                    allMoves.add(new int[]{newRow, newCol});
                }
            }
        }
           
  
        // Check for king-side castling
        if (game.canCastle(isWhite(), true)) { 
            int[] kingPosition = isWhite() ? game.getWhiteKingPosition() : game.getBlackKingPosition();
            int row = kingPosition[0];
            int col = isWhite() ? 6 : 6; 
            if (isValidPosition(row, col) && game.getPiece(row, col) == null) {
                allMoves.add(new int[]{row, col}); 
              
            }
        }

        // Check for queen-side castling
        if (game.canCastle(isWhite(), false)) { 
            int[] kingPosition = isWhite() ? game.getWhiteKingPosition() : game.getBlackKingPosition();
            int row = kingPosition[0];
            int col = isWhite() ? 2 : 2; 
            if (isValidPosition(row, col) && game.getPiece(row, col) == null) {
                allMoves.add(new int[]{row, col}); 
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

    @Override
    protected int getValue() {
       return 100;
    }

  


}


