package com.example;

public interface GameCallback {
    void onPlayerColorReceived(String color);
    void onBoardStateReceived(String[][] board, String currentTurn);
    void onMoveReceived(int fromRow, int fromCol, int toRow, int toCol, String capturedPiece, String movedPiece);
}

