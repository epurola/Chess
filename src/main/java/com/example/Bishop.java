package com.example;

import java.util.ArrayList;
import java.util.List;

public class Bishop extends Piece {

    public Bishop(int row, int col, boolean isWhite) {
        super(row, col, isWhite);
    }

    @Override

    public List<int[]> getPossibleMoves(Game game) {
        List<int[]> possibleMoves = new ArrayList<>();
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
    
                Piece piece = game.getPiece(row,col);
                if(piece == null)
                {
                    possibleMoves.add(new int[]{row,col});
                }
                else
                {
                    if(piece.isWhite() != this.isWhite()){
                        possibleMoves.add(new int[]{row,col});
                    }
                    break;
                }
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

    @Override
    protected int getValue() {
       return 3;
    }
}



