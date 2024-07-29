package com.example;

public class Board {
    private Square[][] board;

    public Board() {
        board = new Square[8][8];
        for (int row = 0; row < 8; row++) {
            
            for (int col = 0; col < 8; col++) {
                board[row][col] = new Square(row, col);
            }
        }
        initialize();
    }

    public Square getSquare(int row, int col) {
        return board[row][col];
    }

    public Piece getPiece(int row, int col) {
        return board[row][col].getPiece();
    }

    public void setPiece(int row, int col, Piece piece) {
        board[row][col].setPiece(piece);
    }

    public Square[][] getBoard() {
        return board;
    }

    public void setBoard(Square[][] board) {
        this.board = board;
    }

    public void initialize() {
        // Initialize Bishops
        setPiece(0, 2, new Bishop(0, 2, false));
        setPiece(0, 5, new Bishop(0, 5, false));
        setPiece(7, 2, new Bishop(7, 2, true));
        setPiece(7, 5, new Bishop(7, 5, true));

        setPiece(1, 0, new Pawn(1, 0, false));
        setPiece(1, 1, new Pawn(1, 1, false));
        setPiece(1, 2, new Pawn(1, 2, false));
        setPiece(1, 3, new Pawn(1, 3, false));
        setPiece(1, 4, new Pawn(1, 4, false));
        setPiece(1, 5, new Pawn(1, 5, false));
        setPiece(1, 6, new Pawn(1, 6, false));
        setPiece(1, 7, new Pawn(1, 7, false));
    
        // Initialize black pawns
        setPiece(6, 0, new Pawn(6, 0, true));
        setPiece(6, 1, new Pawn(6, 1, true));
        setPiece(6, 2, new Pawn(6, 2, true));
        setPiece(6, 3, new Pawn(6, 3, true));
        setPiece(6, 4, new Pawn(6, 4, true));
        setPiece(6, 5, new Pawn(6, 5, true));
        setPiece(6, 6, new Pawn(6, 6, true));
        setPiece(6, 7, new Pawn(6, 7, true));

        setPiece(7, 0, new Rook(7, 0, true));
        setPiece(0, 0, new Rook(0, 0, false));
        setPiece(7, 7, new Rook(7, 7, true));
        setPiece(0, 7, new Rook(0, 7, false));

        setPiece(7, 4, new Queen(7, 4, true)); 
        setPiece(0, 4, new Queen(0, 4, false)); 
        setPiece(7, 3, new King(7, 3, true));  
        setPiece(0, 3, new King(0, 3, false));

        setPiece(7, 1, new Knight(7, 1, true));
        setPiece(7, 6, new Knight(7, 6, true));

        setPiece(0, 1, new Knight(0, 1, false));
        setPiece(0, 6, new Knight(0, 6, false));

        
        
       

        

        // Initialize other pieces similarlcol...
    }
}
