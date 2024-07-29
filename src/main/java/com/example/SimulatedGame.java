package com.example;

import java.util.List;

public class SimulatedGame {
    private Board board;
    private boolean whiteTurn;

    public SimulatedGame(Board board, boolean whiteTurn) {
        this.board = board.copyBoard(); // Deep copy of the board
        this.whiteTurn = whiteTurn;
    }

    public boolean isMoveValid(Piece piece, int toRow, int toCol) {
        // Get the current position
        int fromRow = piece.getRow();
        int fromCol = piece.getCol();
        Piece pieceToMove = board.getPiece(fromRow, fromCol);

        if (pieceToMove == null) {
            return false;
        }

        // Simulate the move
        board.setPiece(toRow, toCol, pieceToMove);
        board.setPiece(fromRow, fromCol, null);

        // Check if the king is in check after the move
        boolean isValid = !isKingInCheck(pieceToMove.isWhite());

        // Revert the move
        board.setPiece(fromRow, fromCol, pieceToMove);
        board.setPiece(toRow, toCol, null);

        return isValid;
    }

    private boolean isKingInCheck(boolean isWhite) {
        int[] kingPosition = findKing(isWhite);
        if (kingPosition == null) {
            return false; // King not found
        }

        int kingRow = kingPosition[0];
        int kingCol = kingPosition[1];

        // Check if any opponent's piece can move to the king's position
        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                Piece piece = board.getPiece(r, c);
                
                // Check if the piece is an opponent's piece
                if (piece != null && piece.isWhite() != isWhite) {
                    List<int[]> possibleMoves = piece.getPossibleMoves(board);
                    for (int[] move : possibleMoves) {
                        if (move[0] == kingRow && move[1] == kingCol) {
                            return true; // King is in check
                        }
                    }
                }
            }
        }

        return false; // King is not in check
    }

    private int[] findKing(boolean isWhite) {
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                Piece piece = board.getPiece(row, col);
                if (piece != null && piece.isWhite() == isWhite && piece instanceof King) {
                    return new int[]{row, col};
                }
            }
        }
        return null; // King not found
    }
}


