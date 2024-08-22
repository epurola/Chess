package com.example;

import java.util.ArrayList;
import java.util.List;

public class Pawn extends Piece {
    public Pawn(int row, int col, boolean isWhite) {
        super(row, col, isWhite);
    }

    @Override
    public List<int[]> getPossibleMoves(Game game) {
        List<int[]> moves = new ArrayList<>();
        int direction = isWhite() ? -1 : 1; // Determine pawn direction based on color
        if(game.getIsOnline())
        {
            direction = -1;
        }

        // One step forward
        addMoveIfValid(moves, game, getRow() + direction, getCol());

        // Two steps forward (only if it's the pawn's first move)
        if(game.getIsOnline())
        {
            if ((isWhite() && getRow() == 6) || (!isWhite() && getRow() == 6)) {
                addMoveIfValid(moves, game, getRow() + 2 * direction, getCol());
            }
        }
        else{
           // Two steps forward (only if it's the pawn's first move)
        if ((isWhite() && getRow() == 6) || (!isWhite() && getRow() == 1)) {
            addMoveIfValid(moves, game, getRow() + 2 * direction, getCol());
        }
        }

        // Diagonal captures
        addCaptureIfValid(moves, game, getRow() + direction, getCol() + 1);
        addCaptureIfValid(moves, game, getRow() + direction, getCol() - 1);

        // En Passant
        addEnPassantIfValid(moves, game, direction);

        return moves;
    }

    private void addMoveIfValid(List<int[]> moves, Game game, int row, int col) {
        if (isValidPosition(row, col)) {
            Piece piece = game.getPiece(row, col);
            if (piece == null) {
                // Check for two-step move if the pawn is at its starting position
                if (Math.abs(row - getRow()) == 2) {
                    int midRow = getRow() + (row - getRow()) / 2;
                    Piece middlePiece = game.getPiece(midRow, col);
                    if (middlePiece == null) {
                        moves.add(new int[]{row, col});
                    }
                } else {
                    moves.add(new int[]{row, col});
                }
            }
        }
    }

    private void addCaptureIfValid(List<int[]> moves, Game game, int row, int col) {
        if (isValidPosition(row, col) && game.getPiece(row, col) != null
                && game.getPiece(row, col).isWhite() != isWhite()) {
            moves.add(new int[]{row, col});
        }
    }

    private void addEnPassantIfValid(List<int[]> moves, Game game, int direction) {
        if (game.getLastMove() != null && getRow() == game.getLastMove().getToRow() &&
            Math.abs(getCol() - game.getLastMove().getToCol()) == 1) {
            Move lastMove = game.getLastMove();
            Piece lastPiece = game.getBoard().getPiece(lastMove.getToRow(), lastMove.getToCol());

            // Check if the last move was by a pawn that moved two squares forward
            if (lastPiece instanceof Pawn && lastPiece.isWhite() != isWhite() &&
                    Math.abs(lastMove.getFromRow() - lastMove.getToRow()) == 2) {
                int enPassantRow = getRow() + direction;
                int enPassantCol = lastMove.getToCol();

                if (isValidPosition(enPassantRow, enPassantCol) && !isMovePuttingKingInCheck(game, enPassantRow, enPassantCol)) {
                    moves.add(new int[]{enPassantRow, enPassantCol});
                }
            }
        }
    }

    private boolean isMovePuttingKingInCheck(Game game, int row, int col) {
        // Save the current state of the board
        Piece originalPieceAtTarget = game.getBoard().getPiece(row, col);
        Piece pieceToMove = copy();
        int originalRow = getRow();
        int originalCol = getCol();

        // Simulate the move
        game.getBoard().setPiece(row, col, pieceToMove);
        game.getBoard().setPiece(originalRow, originalCol, null);

        // Create a new Game instance with the simulated board
        Game simulatedGame = new Game(game.getBoard());

        // Check if the king is in check after the move
        boolean isInCheck = simulatedGame.isInCheck(isWhite());

        // Restore the board state
        game.getBoard().setPiece(originalRow, originalCol, pieceToMove);
        game.getBoard().setPiece(row, col, originalPieceAtTarget);

        return isInCheck;
    }

    private boolean isValidPosition(int row, int col) {
        return row >= 0 && row < 8 && col >= 0 && col < 8;
    }

    @Override
    public Piece copy() {
        return new Pawn(getRow(), getCol(), isWhite());
    }

    @Override
    protected int getValue() {
        return 1;
    }
}






