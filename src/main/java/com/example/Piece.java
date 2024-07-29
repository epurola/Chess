package com.example;

import java.util.ArrayList;
import java.util.List;

public abstract class Piece {
    private int row;
    private int col;
    private boolean isWhite;

    public Piece(int row, int col, boolean isWhite) {
        this.row = row;
        this.col = col;
        this.isWhite = isWhite;
    }

    // Abstract method to be implemented by subclasses
    public abstract Piece copy();

    public int getRow() {
        return row;
    }

    public void setRow(int row) {
        this.row = row;
    }

    public int getCol() {
        return col;
    }

    public void setCol(int col) {
        this.col = col;
    }

    public boolean isWhite() {
        return isWhite;
    }

    public void setWhite(boolean isWhite) {
        this.isWhite = isWhite;
    }

    public void setPosition(int row, int col) {
        this.row = row;
        this.col = col;
    }

    public abstract List<int[]> getPossibleMoves(Board board);

    
public List<int[]> getLegalMovesWithoutCheck(Game game) {
    Board board = game.getBoard();
    List<int[]> possibleMoves = getPossibleMoves(board);
    List<int[]> legalMoves = new ArrayList<>();
    boolean isWhite = isWhite(); // Determine the color

    // Find the king's position
    int[] kingPosition = findKing(board, isWhite);
    if (kingPosition == null) {
        return legalMoves; // King not found
    }

    int kingRow = kingPosition[0];
    int kingCol = kingPosition[1];

    for (int[] move : possibleMoves) {
        int targetRow = move[0];
        int targetCol = move[1];

        // Save the current state of the board
        Piece originalPieceAtTarget = board.getPiece(targetRow, targetCol);
        Piece pieceToMove = copy(); // Create a copy of the bishop
        
        // Create a copy of the board to simulate the move
        Board boardCopy = board.copyBoard();
        boardCopy.setPiece(targetRow, targetCol, pieceToMove);
        boardCopy.setPiece(getRow(), getCol(), null);

        // Create a new Game instance with the copied board
        Game copyGame = new Game(boardCopy);

        // Check if the move results in a check on the player's own king
        if (!copyGame.isInCheck(isWhite)) {
            legalMoves.add(move);
        }

        // Restore the board state
        boardCopy.setPiece(getRow(), getCol(), pieceToMove);
        boardCopy.setPiece(targetRow, targetCol, originalPieceAtTarget);
    }

    return legalMoves;
}


    private int[] findKing(Board board, boolean isWhite) {
        // Iterate over the board to find the king's position
        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                Piece piece = board.getPiece(r, c);
                if (piece instanceof King && piece.isWhite() == isWhite) {
                    return new int[] { r, c };
                }
            }
        }
        return null; // King not found
    }
}



