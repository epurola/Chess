package com.example;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class Game {
    private Board board;
    private boolean whiteTurn; // true if it's white's turn, false if black's turn
    private Stack<Move> moveStack; // Stack to keep track of moves
    private int[] whiteKingPosition; // Variable to store the position of the white king
    private int[] blackKingPosition;
  

    public Game() {
        board = new Board();
        whiteTurn = true; // White always starts first
        moveStack = new Stack<>(); // Initialize the move stack
        whiteKingPosition = new int[2];
        blackKingPosition = new int[2];
        updateKingPositions();
    }

    public Game(Board board) {
        this.board = board.copyBoard(); // Use deep copy constructor
        this.whiteTurn = true; // White always starts first
        moveStack = new Stack<>(); // Initialize the move stack
        whiteKingPosition = new int[2];
        blackKingPosition = new int[2];
        updateKingPositions();
    }
    public Game(String[][] board2, String currentTurn) {
        this.board = parseBoardState(board2);
        this.whiteTurn = currentTurn.equalsIgnoreCase("white");
        moveStack = new Stack<>(); // Initialize the move stack
        whiteKingPosition = new int[2];
        blackKingPosition = new int[2];
        updateKingPositions();
    }
    //Board constructor initialises pieces in starting position....
    private Board parseBoardState(String[][] board2) {
        Board newBoard = new Board();
    
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                String pieceIdentifier = board2[row][col];
                // Create a Piece object from the identifier or null if empty
                Piece piece = createPieceFromIdentifier(pieceIdentifier, row, col);
                // Set the piece on the board
                newBoard.setPiece(row, col, piece);
            }
        }
    
        return newBoard;
    }
    

    private Piece createPieceFromIdentifier(String identifier, int row, int col) {
        if (identifier == null || identifier.startsWith("e-")) {
            return null; // No piece at this position
        }
    
        // The identifier should be in the format like "w-r" or "b-p"
        boolean isWhite = identifier.startsWith("w-");
        String pieceType = identifier.substring(2);
    
        switch (pieceType) {
            case "r":
                return new Rook(row, col, isWhite);
            case "n":
                return new Knight(row, col, isWhite);
            case "b":
                return new Bishop(row, col, isWhite);
            case "q":
                return new Queen(row, col, isWhite);
            case "k":
                return new King(row, col, isWhite);
            case "p":
                return new Pawn(row, col, isWhite);
            case "e":
                return null;
            default:
                System.err.println("Unknown piece identifier: " + identifier);
                return null; // Unknown piece type
        }
    }
    

    public int[] getWhiteKingPosition() {
        return whiteKingPosition;
    }

    // Setter for white king position
    public void setWhiteKingPosition(int row, int col) {
        this.whiteKingPosition[0] = row;
        this.whiteKingPosition[1] = col;
    }

    // Getter for black king position
    public int[] getBlackKingPosition() {
        return blackKingPosition;
    }

    // Setter for black king position
    public void setBlackKingPosition(int row, int col) {
        this.blackKingPosition[0] = row;
        this.blackKingPosition[1] = col;
    }
    public void updateKingPositions() {
        // Update white king position
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                Piece piece = board.getPiece(row, col);
                if (piece instanceof King) {
                    if (piece.isWhite()) {
                        setWhiteKingPosition(row, col);
                    } else {
                        setBlackKingPosition(row, col);
                    }
                }
            }
        }
    }

    public Board getBoard() {
        return this.board;
    }
    public void popMoveStack() {
         moveStack.pop();
    }
    public Move getLastMove() {
        if (moveStack.isEmpty()) {
            return null; // Or handle this case appropriately
        }
        return moveStack.peek();
   }
   public Move getSecondMove() {
    if (moveStack.size() < 2) {
        return null; // Or handle this case appropriately
    }

    Move topMove = moveStack.pop();
    Move secondMove = moveStack.peek();
    moveStack.push(topMove);

    return secondMove;
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
    public boolean makeMove(int fromRow, int fromCol, int toRow, int toCol) {
        int row = toRow;
        int col = toCol;
        Piece selectedPiece = getPiece(fromRow,fromCol);
        int originalRow = selectedPiece.getRow();
        int originalCol = selectedPiece.getCol();
        List<int[]> possibleMoves = new ArrayList<>();
        Piece capturedPiece = null;

        if (row >= 0 && row < 8 && col >= 0 && col < 8) {
            // Check if the move is valid
            if (selectedPiece instanceof Pawn) {
                if (isEnPassant(originalRow, originalCol) ) {
                    possibleMoves = ((Pawn) selectedPiece).getPossibleMoveswithEnPassant(getBoard(), this);
                    
                    capturedPiece = getPiece(getLastMove().getToRow() , getLastMove().getToCol()); 

                    if(capturedPiece != null && capturedPiece.isWhite() == selectedPiece.isWhite())
                    {
                        capturedPiece = null;
                    }
                } else {
                    possibleMoves = selectedPiece.getLegalMovesWithoutCheck(this);
                    capturedPiece = getPiece(row, col);
                }
            } else {
                possibleMoves = selectedPiece.getLegalMovesWithoutCheck(this);
                capturedPiece = getPiece(row, col);
            }

            boolean validMove = possibleMoves.stream().anyMatch(move -> move[0] == row && move[1] == col);

            if (validMove) {
                System.out.println("Mov was valid");
                // Temporarily make the move
                Piece pieceToMove = selectedPiece.copy();

                // Handle en passant
                if (capturedPiece != null) {
                    setPiece(capturedPiece.getRow(), capturedPiece.getCol(), null); // Remove the captured pawn from the board
                }

                setPiece(row, col, pieceToMove);
                setPiece(originalRow, originalCol, null);
                pieceToMove.setPosition(row, col);

               
                if(pieceToMove instanceof King)
                {
                    updateKingPositions();
                }
               
                recordMove(originalRow, originalCol, row, col, capturedPiece, pieceToMove,
                capturedPiece != null ? capturedPiece.getRow() : 1, 
                capturedPiece != null ? capturedPiece.getCol() : 1);
                  if (!validMove) {
                return false;
            }

                // Check if the king is in check
                if (isInCheck(isWhiteTurn())) {
                    SoundManager.playNotifySound();
                    // Revert the move if it puts the king in check
                    setPiece(originalRow, originalCol, pieceToMove);
                    setPiece(row, col, capturedPiece); // Restore the captured piece
                    pieceToMove.setPosition(originalRow, originalCol); // Restore the piece's position
                    if (capturedPiece != null) {
                        capturedPiece.setPosition(row, col);
                    }
                   // popMoveStack();
                    System.out.println("Move puts king in check. Move reverted.");
                } else {
                    if (originalRow != row || originalCol != col) {
                        setWhiteTurn(!isWhiteTurn());
                    }
                }
            }

            if (checkMate(this)) {
                return true;
            }

            if (checkDraw(this)) {
                System.out.println("Draw!");
                return true;
            }
               // Redraw the board only if the move is valid
           
        }

        return true;

    
}
         

public void promotePawn(int toRow, int toCol, String pieceName) {
    // Ensure the provided pieceName is valid
    if (!List.of("queen", "rook", "bishop", "knight").contains(pieceName.toLowerCase())) {
        throw new IllegalArgumentException("Invalid piece name for promotion: " + pieceName);
    }

    // Get the piece currently at the specified location
    Piece pieceAtPosition = getPiece(toRow, toCol);

    // Check if the piece is a pawn
    if (pieceAtPosition instanceof Pawn) {
        // Create the new piece based on the promotion choice
        Piece newPiece = createPiece(pieceName, toRow, toCol);

        // Replace the pawn with the new piece on the board
        setPiece(toRow, toCol, newPiece);
    } else {
        throw new IllegalArgumentException("There is no pawn at the specified position: " + toRow + ", " + toCol);
    }
}

    private Piece createPiece(String pieceType, int row, int col) {
        switch (pieceType.toLowerCase()) {
            case "queen":
                return new Queen(row, col, isWhiteTurn());
            case "rook":
                return new Rook(row, col, isWhiteTurn());
            case "bishop":
                return new Bishop(row, col, isWhiteTurn());
            case "knight":
                return new Knight(row, col, isWhiteTurn());
            case "pawn":
                return new Pawn(row, col, isWhiteTurn());
            case "king":
                return new King(row, col, isWhiteTurn());
            default:
                throw new IllegalArgumentException("Unknown piece type: " + pieceType);
        }
    }

    public boolean isEnPassant(int row, int col) {
        System.out.println("Trying to see if enpassant");
        if (moveStack.isEmpty()) {
            System.out.println("Stack is empty");
            return false;
        }
        if (!(row == 3 || row == 4)) {
            System.out.println("Wrong row");
            return false;
        }
    
        // Get the last move
        Move lastMove = getLastMove();
        int startRow = lastMove.getFromRow();
        int startCol = lastMove.getFromCol();
        int newRow = lastMove.getToRow();
        int newCol = lastMove.getToCol();
    
        // Ensure the piece moved is a pawn
        Piece movedPiece = getPiece(newRow, newCol);
        if (!(movedPiece instanceof Pawn)) {
            return false;
        }
        System.out.println("Moved piece was pawn");
        // Check if the move was a two-square pawn advance
        if (Math.abs(startRow - newRow) == 2 && startCol == newCol ) {
            
            // Determine the direction of the pawn
            if(col == 0 )
            {
                if ( getPiece(row, col + 1) instanceof Pawn) {
                    return true;
                }
            }
            if(col ==7 )
            {
                if ( getPiece(row, col - 1) instanceof Pawn) {
                    return true;
                }
            }
            if (getPiece(row, col + 1) instanceof Pawn || getPiece(row, (col- 1)) instanceof Pawn) {
                return true;
            }
            
        }
   
        return false;
    }

    public void undoLastMove() {
        if (!moveStack.isEmpty()) {
            Move lastMove = moveStack.pop();
            Piece movedPiece = lastMove.getMovedPiece();
    
            Piece capturedPiece = lastMove.getCapturedPiece();
    
            // Move the moved piece back to its original position
            if (movedPiece != null) {
                board.setPiece(lastMove.getFromRow(), lastMove.getFromCol(), movedPiece);
                board.setPiece(lastMove.getToRow(), lastMove.getToCol(), null);
                movedPiece.setPosition(lastMove.getFromRow(), lastMove.getFromCol());
            } else {
                // If the moved piece is null, clear the destination cell
                board.setPiece(lastMove.getFromRow(), lastMove.getFromCol(), null);
            }
    
            // Restore the captured piece to its original position, if any
            //In case enpassant the eating piece position is not updated so it gets drawn in undo two times
            if (capturedPiece != null) {
                board.setPiece(lastMove.getCapturePieceRow(), lastMove.getCapturePieceCol(), capturedPiece);
                capturedPiece.setPosition(lastMove.getCapturePieceRow(), lastMove.getCapturePieceCol());
            } else {
                board.setPiece(lastMove.getToRow(), lastMove.getToCol(), null);
                
            }
    
            SoundManager.playMoveSound();
            setWhiteTurn(!isWhiteTurn());
            
            
        }
    }
    

    public void recordMove(int fromRow, int fromCol, int toRow, int toCol, Piece capturedPiece,Piece movedPiece, int capturePieceRow, int capturePieceCol) {
        moveStack.push(new Move(fromRow, fromCol, toRow, toCol, capturedPiece, movedPiece, capturePieceRow, capturePieceCol));
        System.out.println("MOVE WAS ADDED");
    }

    public boolean isInCheck(boolean isWhite) {
        boolean isInCheck = false;

        int[] kingPosition = isWhite ? getWhiteKingPosition() : getBlackKingPosition();
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
        int[] kingPosition = isWhite ? getWhiteKingPosition() : getBlackKingPosition();
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

    public boolean checkMate(Game game){

       int[] kingPosition = game.isWhiteTurn() ? getWhiteKingPosition() : getBlackKingPosition();

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

