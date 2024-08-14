package com.example;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Bishop extends Piece {

    public Bishop(int row, int col, boolean isWhite) {
        super(row, col, isWhite);
    }

    @Override

    public List<int[]> getPossibleMoves(Game game) {
        List<int[]> possibleMoves = new ArrayList<>();
        long bishopBitboard = game.isWhiteTurn() ? game.getBoard().getWhiteBishops() : game.getBoard().getBlackBishops(); // Get the bitboard of bishops
        long ownBitboard = game.isWhiteTurn() ? game.getBoard().getWhitePieces() : game.getBoard().getBlackPieces(); // Get the bitboard of own pieces
        long bishopMask = 1L << (getRow() * 8 + getCol()); // Bitmask for the current bishop's position
        long attackMask = 0L;
    
        // Calculate the attack positions by checking each direction
        int[][] directions = {{1, 1}, {-1, -1}, {1, -1}, {-1, 1}};
    
        for (int[] direction : directions) {
            int row = getRow();
            int col = getCol();
    
            while (true) {
                row += direction[0];
                col += direction[1];
    
                if (!isValidPosition(row, col)) {
                    break;
                }
    
                long mask = 1L << (row * 8 + col);
    
                // Stop if another piece is found in the way
                if ((ownBitboard & mask) != 0) {
                    // If the piece is of the same color, stop the attack in this direction
                    break;
                }
    
                if ((bishopBitboard & mask) != 0) {
                    attackMask |= mask;
                    break;
                }
    
                attackMask |= mask;
            }
        }
    
        // Convert attackMask to a list of coordinates
        for (int i = 0; i < 64; i++) {
            if ((attackMask & (1L << i)) != 0) {
                int row = i / 8;
                int col = i % 8;
                possibleMoves.add(new int[]{row, col});
            }
        }
    
        return possibleMoves;
    }
   
    private boolean isValidPosition(int row, int col) {
        return row >= 0 && row < 8 && col >= 0 && col < 8;
    }

    @Override
    public Piece copy() {
        // Create a new Bishop object with the same properties
        return new Bishop(getRow(), getCol(), isWhite());
    }
}



