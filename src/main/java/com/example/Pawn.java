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

        // Right diagonal
        newRow = getRow() + direction;
        newCol = getCol() + 1;
        if (isValidPosition(newRow, newCol) && board.getPiece(newRow, newCol) != null 
                && board.getPiece(newRow, newCol).isWhite() != isWhite()) {
            moves.add(new int[]{newRow, newCol});
        }

        // Left diagonal
        newCol = getCol() - 1;
        if (isValidPosition(newRow, newCol) && board.getPiece(newRow, newCol) != null 
                && board.getPiece(newRow, newCol).isWhite() != isWhite()) {
            moves.add(new int[]{newRow, newCol});
        }



        return moves;
    }

    private boolean isValidPosition(int row, int col) {
        return row >= 0 && row < 8 && col >= 0 && col < 8;
    }

    @Override
    public Piece copy() {
        return new Pawn(getRow(), getCol(), isWhite());
    }

    public List<int[]> getPossibleMoveswithEnPassant(Board board, Game game) {
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
    
        // Right diagonal capture
        newRow = getRow() + direction;
        newCol = getCol() + 1;
        if (isValidPosition(newRow, newCol) && board.getPiece(newRow, newCol) != null 
                && board.getPiece(newRow, newCol).isWhite() != isWhite()) {
            moves.add(new int[]{newRow, newCol});
        }
    
        // Left diagonal capture
        newCol = getCol() - 1;
        if (isValidPosition(newRow, newCol) && board.getPiece(newRow, newCol) != null 
                && board.getPiece(newRow, newCol).isWhite() != isWhite()) {
            moves.add(new int[]{newRow, newCol});
        }
    
        // En Passant
        if (game.getLastMove() != null && getRow() == game.getLastMove().getToRow() &&
            Math.abs(getCol() - game.getLastMove().getToCol()) == 1) {
            Move lastMove = game.getLastMove();
            Piece lastPiece = board.getPiece(lastMove.getToRow(), lastMove.getToCol());
    
            // Check if the last move was by a pawn that moved two squares forward
            if (lastPiece instanceof Pawn && lastPiece.isWhite() != this.isWhite()) {
                int lastPieceRow = lastMove.getToRow();
                int lastPieceCol = lastMove.getToCol();
    
                // Ensure the pawn moved two squares forward
                if (Math.abs(lastPieceRow - lastMove.getFromRow()) == 2) {
                    int enPassantCol = lastPieceCol;
                    int enPassantRow = getRow() + direction;
    
                    if (isValidPosition(enPassantRow, enPassantCol)) {
                        moves.add(new int[]{enPassantRow, enPassantCol});
                    }
                }
            }
        }
    
        // Filter out moves that result in check
        List<int[]> legalMoves = new ArrayList<>();
        for (int[] move : moves) {
            int targetRow = move[0];
            int targetCol = move[1];
    
            // Save the current state of the board
            Piece originalPieceAtTarget = board.getPiece(targetRow, targetCol);
            Piece pieceToMove = copy();
    
            // Create a copy of the board to simulate the move
            Board boardCopy = board.copyBoard();
            boardCopy.setPiece(targetRow, targetCol, pieceToMove);
            boardCopy.setPiece(getRow(), getCol(), null);
    
            // Create a new Game instance with the copied board
            Game copyGame = new Game(boardCopy);
            // Check if the move results in a check on the player's own king
            if (!copyGame.isInCheck(isWhite())) {
                legalMoves.add(move);
            }
    
            // Restore the board state
            boardCopy.setPiece(getRow(), getCol(), pieceToMove);
            boardCopy.setPiece(targetRow, targetCol, originalPieceAtTarget);
        }
    
        return legalMoves;
    }
    
    

   
}




