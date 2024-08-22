package com.example;

public class Move {
    private final int fromRow;
    private final int fromCol;
    private final int toRow;
    private final int toCol;
    private final int capturePieceRow;
    private final int capturePieceCol;
    private final Piece capturedPiece;
    private final Piece movedPiece;
    private final String fen; // FEN string representing the board state after this move

    public Move(int fromRow, int fromCol, int toRow, int toCol, Piece capturedPiece, Piece movedPiece, 
                int capturePieceRow, int capturePieceCol, String fen) {
        this.fromRow = fromRow;
        this.fromCol = fromCol;
        this.toRow = toRow;
        this.toCol = toCol;
        this.capturedPiece = capturedPiece;
        this.movedPiece = movedPiece;
        this.capturePieceCol = capturePieceCol;
        this.capturePieceRow = capturePieceRow;
        this.fen = fen; // Set the FEN string
    }

    public int getFromRow() { return fromRow; }
    public int getFromCol() { return fromCol; }
    public int getToRow() { return toRow; }
    public int getToCol() { return toCol; }
    public Piece getCapturedPiece() { return capturedPiece; }
    public Piece getMovedPiece() { return movedPiece; }
    public int getCapturePieceRow() { return capturePieceRow; }
    public int getCapturePieceCol() { return capturePieceCol; }
    public String getFEN() { return fen; } // Get the FEN string

    @Override
    public String toString() {
        return "Move from (" + fromRow + ", " + fromCol + ") to (" + toRow + ", " + toCol + ") "
                + "with " + movedPiece + ", captured " + capturedPiece + " at (" + capturePieceRow + ", " + capturePieceCol + "). "
                + "FEN: " + fen;
    }
}


