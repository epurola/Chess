package com.example;

import java.util.ArrayList;
import java.util.List;

public class Rook extends Piece {

    public Rook(int row, int col, boolean isWhite) {
        super(row, col, isWhite);
    }

    @Override
    public List<int[]> getPossibleMoves(Game game) {
        List<int[]> allMoves = new ArrayList<>();
        int[][] directions = {{1, 0}, {-1, 0}, {0, -1}, {0, 1}};
    
        // Normal king moves
        for (int[] direction : directions) {
            int newRow = getRow();
            int newCol = getCol();
    
            while (true) {
                newRow += direction[0];
                newCol += direction[1];
    
                if (!isValidPosition(newRow, newCol)) {
                    break;
                }
    
                Piece piece = game.getPiece(newRow, newCol);
    
                if (piece == null) {
                    allMoves.add(new int[]{newRow, newCol});
                } else {
                    if (piece.isWhite() != this.isWhite()) {
                        allMoves.add(new int[]{newRow, newCol});
                    }
                    break;
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
        return new Rook(getRow(), getCol(), isWhite());
    }

    @Override
    protected int getValue() {
        return 5;
    }
}