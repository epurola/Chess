package com.example;

import java.util.ArrayList;
import java.util.List;

public class Game {
    private Board board;
    private boolean whiteTurn; // true if it's white's turn, false if black's turn

    public Game() {
        board = new Board();
        whiteTurn = true; // White always starts first
    }

    public Game(Board board) {
        this.board = board.copyBoard(); // Use deep copy constructor
        this.whiteTurn = true; // White always starts first
    }

    public Board getBoard() {
        return this.board;
    }

    public Piece getPiece(int row, int col) {
        return board.getPiece(row, col);
    }

    public void setPiece(int row, int col, Piece piece) {
        board.setPiece(row, col, piece);
    }

    public boolean isWhiteTurn() {
        return whiteTurn;
    }

    public void setWhiteTurn(boolean whiteTurn) {
        this.whiteTurn = whiteTurn;
    }

    public boolean isInCheck(boolean isWhite) {
        boolean isInCheck = false;

        int[] kingPosition = findKing(isWhite);
        if (kingPosition == null) {
            return false; // King not found
        }

        int kingRow = kingPosition[0];
        int kingCol = kingPosition[1];
        List<int[]> possibleMoves;

        // Check all opponent's pieces
        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                Piece piece = board.getPiece(r, c);
                if (piece != null && piece.isWhite() != isWhite) {
                    possibleMoves = piece.getPossibleMoves(board);
                    for (int[] move : possibleMoves) {
                        if (move[0] == kingRow && move[1] == kingCol) {
                            isInCheck = true;
                            break;
                        }
                    }
                    if (isInCheck) break;
                }
            }
            if (isInCheck) break;
        }

        return isInCheck;
    }

    public int[] findKing(boolean isWhite) {
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
    public List<Piece> getAllPieces(boolean isWhite) {
        List<Piece> pieces = new ArrayList<>();
        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                Piece piece = board.getPiece(r, c);
                if (piece != null && piece.isWhite() == isWhite) {
                    pieces.add(piece);
                }
            }
        }
        return pieces;
    }
    

    public List<int[]> getLegalMovesToResolveCheck(boolean isWhite) {
        List<int[]> legalMoves = new ArrayList<>();
    
        // Find the king's position
        int[] kingPosition = findKing(isWhite);
        if (kingPosition == null) {
            return legalMoves; // King not found
        }
    
        int kingRow = kingPosition[0];
        int kingCol = kingPosition[1];
    
        // Iterate over all pieces for the player whose turn it is
        List<Piece> pieces = getAllPieces(isWhite);
    
        for (Piece piece : pieces) {
            int pieceRow = piece.getRow();
            int pieceCol = piece.getCol();
    
            // Get all possible moves for this piece
            List<int[]> possibleMoves = piece.getPossibleMoves(board);
    
            for (int[] move : possibleMoves) {
                int targetRow = move[0];
                int targetCol = move[1];
    
                // Create a copy of the board to simulate the move
                Game boardCopy = copyGame();
                
                // Perform the move on the copied game
                Piece pieceToMove = boardCopy.getPiece(pieceRow, pieceCol);
                Piece capturedPiece = boardCopy.getPiece(targetRow, targetCol); // Piece that will be captured
                
                boardCopy.setPiece(targetRow, targetCol, pieceToMove);
                boardCopy.setPiece(pieceRow, pieceCol, null); // Remove piece from the original position
    
                // Check if the king is still in check after the move
                if (!boardCopy.isInCheck(isWhite)) {
                    // Add the move to legalMoves if it resolves the check
                    legalMoves.add(move);
                }
    
                // Revert the move on the copied board
                boardCopy.setPiece(pieceRow, pieceCol, pieceToMove);
                boardCopy.setPiece(targetRow, targetCol, capturedPiece);
            }
        }
    
        return legalMoves;
    }

    public boolean chechMate(Game game){

       int[] kingPosition = game.findKing(whiteTurn);

       int row= kingPosition[0];
       int col = kingPosition[1];
    
       Piece king = board.getPiece(row, col);

       // List to hold all legal moves for the current player's pieces
       List<int[]> allLegalMoves = new ArrayList<>();

       // Retrieve all pieces of the current player
       List<Piece> pieces = game.getAllPieces(whiteTurn);

        // Iterate over each piece and collect its legal moves
      for (Piece piece : pieces) {
         List<int[]> legalMovesForPiece = piece.getLegalMovesWithoutCheck(game);
         allLegalMoves.addAll(legalMovesForPiece);
        }
       
        if(isInCheck(whiteTurn) && allLegalMoves.isEmpty() )
        {
            return true;
        }

        return false;
    }

    public boolean checkDraw(Game game){
        // List to hold all legal moves for the current player's pieces
        List<int[]> allLegalMoves = new ArrayList<>();
        // Retrieve all pieces of the current player
        List<Piece> pieces = game.getAllPieces(whiteTurn);
         // Iterate over each piece and collect its legal moves
       for (Piece piece : pieces) {
          List<int[]> legalMovesForPiece = piece.getLegalMovesWithoutCheck(game);
          allLegalMoves.addAll(legalMovesForPiece);
         }
        
         if(!isInCheck(whiteTurn) && allLegalMoves.isEmpty() )
         {
             return true;
         }
         return false;
     }
    
    
    
    

    public Game copyGame() {
        // Create a new Game object with a deep copy of the board
        return new Game(this.board.copyBoard()); 
    }
}

