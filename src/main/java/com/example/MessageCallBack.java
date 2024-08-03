package com.example;

public interface MessageCallBack {
    void onMoveReceived(int fromRow, int fromCol, int toRow, int toCol,String capturedPiece, String movedPiece);
    void onBoardStateReceived(String[][] board, String currentTurn);
}

