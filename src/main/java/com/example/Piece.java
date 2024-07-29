package com.example;

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
}


