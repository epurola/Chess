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
    private boolean whiteCanCastleKingSide;
    private boolean whiteCanCastleQueenSide;
    private boolean blackCanCastleKingSide;
    private boolean blackCanCastleQueenSide;
    private boolean isOnline;
    private boolean isInCheck;
    List<int[]> possibleMoves;
    private boolean whiteHasCastled;
    private boolean blackHasCastled;

    public Game() {
        board = new Board();
        whiteTurn = true; // White always starts first
        moveStack = new Stack<>(); // Initialize the move stack
        whiteKingPosition = new int[2];
        blackKingPosition = new int[2];
        whiteCanCastleKingSide = true;
        whiteCanCastleQueenSide = true;
        blackCanCastleKingSide = true;
        blackCanCastleQueenSide = true;

        updateKingPositions();
    }

    public Game(boolean isBlack) {
        board = new Board(isBlack);
        whiteTurn = true; // White always starts first
        moveStack = new Stack<>(); // Initialize the move stack
        whiteKingPosition = new int[2];
        blackKingPosition = new int[2];
        whiteCanCastleKingSide = true;
        whiteCanCastleQueenSide = true;
        blackCanCastleKingSide = true;
        blackCanCastleQueenSide = true;
        isOnline = true;
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

    public boolean getIsOnline() {
        return isOnline;
    }

    // Board constructor initialises pieces in starting position....
    private Board parseBoardState(String[][] board2) {
        Board newBoard = new Board();

        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                String pieceIdentifier = board2[row][col];
                // Create a Piece object from the identifier or null if empty
                Piece piece = createPieceFromIdentifier(pieceIdentifier, row, col);
                // Set the piece on the board
                // newBoard.setPiece(row, col, piece);
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
        // Check white kings
        long whiteKingBitboard = board.getWhiteKings();
        for (int i = 0; i < 64; i++) {
            if ((whiteKingBitboard & (1L << i)) != 0) {
                int row = i / 8;
                int col = i % 8;
                setWhiteKingPosition(row, col);
                break;
            }
        }
        // Check black kings
        long blackKingBitboard = board.getBlackKings();
        for (int i = 0; i < 64; i++) {
            if ((blackKingBitboard & (1L << i)) != 0) {
                int row = i / 8;
                int col = i % 8;
                setBlackKingPosition(row, col);
                break;
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

    public boolean canCastle(boolean isWhite, boolean kingSide) {
        if (!isInCheck) {
            return isWhite ? (kingSide ? whiteCanCastleKingSide : whiteCanCastleQueenSide)
                    : (kingSide ? blackCanCastleKingSide : blackCanCastleQueenSide);
        } else {
            return false;
        }

    }

    public void updateCastlingRightsAfterMove(Piece piece) {
        if (piece instanceof King) {
            if (piece.isWhite()) {
                whiteCanCastleKingSide = false;
                whiteCanCastleQueenSide = false;
            } else {
                blackCanCastleKingSide = false;
                blackCanCastleQueenSide = false;
            }
        } else if (piece instanceof Rook) {
            Rook rook = (Rook) piece;
            if (rook.isWhite()) {
                if (rook.getCol() == 0) { // Queen-side rook
                    whiteCanCastleQueenSide = false;
                } else if (rook.getCol() == 7) { // King-side rook
                    whiteCanCastleKingSide = false;
                }
            } else {
                if (rook.getCol() == 0) { // Queen-side rook
                    blackCanCastleQueenSide = false;
                } else if (rook.getCol() == 7) { // King-side rook
                    blackCanCastleKingSide = false;
                }
            }
        }
    }

    public boolean makeMove(int fromRow, int fromCol, int toRow, int toCol) {
        canEnpassant = false;

        int row = toRow;
        int col = toCol;
        Piece selectedPiece = getPiece(fromRow, fromCol);
        int originalRow = selectedPiece.getRow();
        int originalCol = selectedPiece.getCol();
        List<int[]> possibleMoves = new ArrayList<>();
        Piece capturedPiece = null;

        if (row >= 0 && row < 8 && col >= 0 && col < 8) {

            possibleMoves = selectedPiece.getLegalMovesWithoutCheck(this);
            capturedPiece = getPiece(row, col);
            if (selectedPiece instanceof Pawn && isEnPassant(originalRow, originalCol)) {
                capturedPiece = getPiece(originalRow, toCol);
                if (capturedPiece != null && capturedPiece.isWhite() == isWhiteTurn()) {
                    capturedPiece = null;
                }
            }

            boolean validMove = possibleMoves.stream().anyMatch(move -> move[0] == row && move[1] == col);

            if (validMove) {
                // Temporarily make the move
                Piece pieceToMove = selectedPiece.copy();
                String fen = toFen();

                // Handle en passant
                if (capturedPiece != null) {
                    setPiece(capturedPiece.getRow(), capturedPiece.getCol(), null); // Remove the captured pawn from the
                                                                                    // board
                }
                if (capturedPiece instanceof Rook) {
                    updateCastlingRightsAfterMove(capturedPiece);
                }
                if (pieceToMove instanceof King || pieceToMove instanceof Rook) {
                    updateCastlingRightsAfterMove(selectedPiece);
                }
                // Castling check
                if (pieceToMove instanceof King && Math.abs(fromCol - toCol) == 2) {
                    // King-side or Queen-side castling
                    int[] capturedPiecePosition = new int[2];
                    capturedPiecePosition = performCastling((King) pieceToMove, toRow, toCol);

                    capturedPiece = new Rook(capturedPiecePosition[0], capturedPiecePosition[1], isWhiteTurn());
                    if (pieceToMove.isWhite()) {
                        whiteHasCastled = true;
                    }
                    if (!pieceToMove.isWhite()) {
                        blackHasCastled = true;
                    }

                }

                setPiece(row, col, pieceToMove);
                setPiece(originalRow, originalCol, null);
                pieceToMove.setPosition(row, col);

                updateKingPositions();

                if (!validMove) {
                    return false;
                }

                // Check if the king is in check
                if (isInCheck(isWhiteTurn())) {

                    // Revert the move if it puts the king in check
                    setPiece(originalRow, originalCol, pieceToMove);
                    setPiece(row, col, capturedPiece); // Restore the captured piece
                    pieceToMove.setPosition(originalRow, originalCol); // Restore the piece's position
                    if (capturedPiece != null) {
                        capturedPiece.setPosition(row, col);
                    }
                    popMoveStack();
                    System.out.println("Move puts king in check. Move reverted.");
                } else {
                    if (originalRow != row || originalCol != col) {
                        setWhiteTurn(!isWhiteTurn());
                    }
                }
                if (checkMate(this)) {
                    moveString = generateSanNotation(selectedPiece, fromRow, fromCol, toRow, toCol, capturedPiece);
                    return true;
                }

                if (checkDraw(this)) {
                    System.out.println("Draw!");
                    moveString = generateSanNotation(selectedPiece, fromRow, fromCol, toRow, toCol, capturedPiece);
                    return true;
                }

                recordMove(originalRow, originalCol, row, col, capturedPiece, pieceToMove,
                        capturedPiece != null ? capturedPiece.getRow() : 1,
                        capturedPiece != null ? capturedPiece.getCol() : 1,
                        fen);
                moveString = generateSanNotation(selectedPiece, fromRow, fromCol, toRow, toCol, capturedPiece);
                return true;

            }

        }

        return false;

    }

    public String generateSanNotation(Piece piece, int fromRow, int fromCol, int toRow, int toCol,
            Piece capturedPiece) {
        StringBuilder san = new StringBuilder();

        // Determine the piece type for the SAN
        if (piece instanceof Pawn) {
            // For pawns, if a capture occurs, append the source column and an "x"
            if (capturedPiece != null) {
                san.append(rowColToAlgebraic(fromRow, fromCol).charAt(0)).append("x");
            }
        } else if (piece instanceof King) {
            san.append("K");
            if (capturedPiece != null) {
                san.append("x");
            }
        } else if (piece instanceof Queen) {
            san.append("Q");
            if (capturedPiece != null) {
                san.append("x");
            }
        } else if (piece instanceof Rook) {
            san.append("R");
            if (capturedPiece != null) {
                san.append("x");
            }
        } else if (piece instanceof Bishop) {
            san.append("B");
            if (capturedPiece != null) {
                san.append("x");
            }
        } else if (piece instanceof Knight) {
            san.append("N");
            if (capturedPiece != null) {
                san.append("x");
            }
        }

        // Add the destination square
        san.append(rowColToAlgebraic(toRow, toCol));

        // Temporarily make the move to check if it puts the opponent's king in check
        // Remove the piece from its original position

        // Check if the opponent's king is in check after the move
        boolean isInCheckAfterMove = false;

        // Check for the opponent's king based on the color of the piece being moved
        if (piece.isWhite()) {
            isInCheckAfterMove = isKingInCheck(getBlackKingPosition()[0], getBlackKingPosition()[1]);
        } else {
            isInCheckAfterMove = isKingInCheck(getWhiteKingPosition()[0], getWhiteKingPosition()[1]);
        }

        // Add check or checkmate notation
        if (isInCheckAfterMove && !gameOver) {
            san.append("+"); // Append '+' to indicate check
        }

        // Check for checkmate after the move
        if (gameOver) {
            san.append("#"); // Append '#' to indicate checkmate
        }

        return san.toString();
    }

    private String moveString;

    public String getMoveString() {
        return moveString;
    }

    public String rowColToAlgebraic(int row, int col) {
        // Convert column index to column character ('a' to 'h')
        char columnChar = (char) ('a' + col);
        // Convert row index to row character ('1' to '8')
        char rowChar = (char) ('1' + (7 - row)); // Adjust for 0-based indexing
        // Combine column and row characters to form the algebraic notation
        return "" + columnChar + rowChar;
    }

    public Piece getCapturedPiece() {
        return capturedPiece;
    }

    Piece capturedPiece = null;

    public boolean makeMoveReplay(int fromRow, int fromCol, int toRow, int toCol) {

        int row = toRow;
        int col = toCol;
        Piece selectedPiece = getPiece(fromRow, fromCol);
        int originalRow = selectedPiece.getRow();
        int originalCol = selectedPiece.getCol();
        List<int[]> possibleMoves = new ArrayList<>();
        capturedPiece = null;

        if (row >= 0 && row < 8 && col >= 0 && col < 8) {

            possibleMoves = selectedPiece.getLegalMovesWithoutCheck(this);
            capturedPiece = getPiece(row, col);
            if (selectedPiece instanceof Pawn && isEnPassant(originalRow, originalCol)) {
                capturedPiece = getPiece(originalRow, toCol);
                if (capturedPiece != null && capturedPiece.isWhite() == isWhiteTurn()) {
                    capturedPiece = null;
                }
            }

            boolean validMove = possibleMoves.stream().anyMatch(move -> move[0] == row && move[1] == col);

            if (validMove) {
                // Temporarily make the move
                Piece pieceToMove = selectedPiece.copy();
                String fen = toFen();

                // Handle en passant
                if (capturedPiece != null) {
                    setPiece(capturedPiece.getRow(), capturedPiece.getCol(), null); // Remove the captured pawn from the
                                                                                    // board
                }
                if (capturedPiece instanceof Rook) {
                    updateCastlingRightsAfterMove(capturedPiece);
                }
                if (pieceToMove instanceof King || pieceToMove instanceof Rook) {
                    updateCastlingRightsAfterMove(selectedPiece);
                }
                // Castling check
                if (pieceToMove instanceof King && Math.abs(fromCol - toCol) == 2) {
                    // King-side or Queen-side castling
                    int[] capturedPiecePosition = new int[2];
                    capturedPiecePosition = performCastling((King) pieceToMove, toRow, toCol);

                    capturedPiece = new Rook(capturedPiecePosition[0], capturedPiecePosition[1], isWhiteTurn());
                    if (pieceToMove.isWhite()) {
                        whiteHasCastled = true;
                    }
                    if (!pieceToMove.isWhite()) {
                        blackHasCastled = true;
                    }

                }

                setPiece(row, col, pieceToMove);
                setPiece(originalRow, originalCol, null);
                pieceToMove.setPosition(row, col);

                updateKingPositions();

                if (!validMove) {
                    return false;
                }

                // Check if the king is in check

                if (originalRow != row || originalCol != col) {
                    setWhiteTurn(!isWhiteTurn());
                }

                if (checkMate(this)) {
                    return true;
                }

                if (checkDraw(this)) {
                    System.out.println("Draw!");
                    return true;
                }

                recordMove(originalRow, originalCol, row, col, capturedPiece, pieceToMove,
                        capturedPiece != null ? capturedPiece.getRow() : 1,
                        capturedPiece != null ? capturedPiece.getCol() : 1,
                        fen);
                return true;
            }

        }

        return false;

    }

    public String makeMoveReplay1(int fromRow, int fromCol, int toRow, int toCol) {
        int row = toRow;
        int col = toCol;
        Piece selectedPiece = getPiece(fromRow, fromCol);
        int originalRow = selectedPiece.getRow();
        int originalCol = selectedPiece.getCol();
        List<int[]> possibleMoves = new ArrayList<>();
        capturedPiece = null;

        // Prepare data for algebraic notation
        String moveAlgebraic = ""; // This will store the final algebraic notation

        if (row >= 0 && row < 8 && col >= 0 && col < 8) {

            possibleMoves = selectedPiece.getLegalMovesWithoutCheck(this);
            capturedPiece = getPiece(row, col);

            // Handle en passant
            if (selectedPiece instanceof Pawn && isEnPassant(originalRow, originalCol)) {
                capturedPiece = getPiece(originalRow, toCol);
                if (capturedPiece != null && capturedPiece.isWhite() == isWhiteTurn()) {
                    capturedPiece = null;
                }
            }

            boolean validMove = possibleMoves.stream().anyMatch(move -> move[0] == row && move[1] == col);

            if (validMove) {
                Piece pieceToMove = selectedPiece.copy();
                String fen = toFen(); // Store the board position before making the move

                // Castling check

                // Promotion check
                if (pieceToMove instanceof Pawn && (toRow == 0 || toRow == 7)) {
                    // Assume promotion to Queen for simplicity; you can expand this
                    setPiece(row, col, new Queen(row, col, pieceToMove.isWhite()));
                    moveAlgebraic = getChessCoordinate(fromRow, fromCol) + getChessCoordinate(toRow, toCol) + "=Q";
                    return moveAlgebraic; // Return promotion notation
                }

                // Handle regular move or capture
                if (capturedPiece != null) {
                    if (pieceToMove instanceof Pawn) {
                        // Pawns require file of origin on captures (e.g., exd5)
                        moveAlgebraic = (char) ('a' + originalCol) + "x" + getChessCoordinate(toRow, toCol);
                    } else {
                        // Other pieces just have 'x' between the piece and destination (e.g., Nxf3)
                        moveAlgebraic = getNotationSymbol(pieceToMove) + "x" + getChessCoordinate(toRow, toCol);
                    }
                } else {
                    if (pieceToMove instanceof Pawn) {
                        // Pawn move without capture is just the destination (e.g., e4)
                        moveAlgebraic = getChessCoordinate(toRow, toCol);
                    } else {
                        // Piece move without capture (e.g., Nf3)
                        moveAlgebraic = getNotationSymbol(pieceToMove) + getChessCoordinate(toRow, toCol);
                    }
                }
                if (pieceToMove instanceof King && Math.abs(fromCol - toCol) == 2) {
                    // King-side or Queen-side castling
                    performCastling((King) pieceToMove, toRow, toCol);

                    if (fromCol < toCol) {
                        moveAlgebraic = "O-O"; // King-side castling
                    } else {
                        moveAlgebraic = "O-O-O"; // Queen-side castling
                    }
                    // Return castling notation immediately
                }

                // Apply the move on the board
                setPiece(row, col, pieceToMove);
                setPiece(originalRow, originalCol, null);
                pieceToMove.setPosition(row, col);

                updateKingPositions();

                if (!validMove) {
                    return null;
                }

                // Change the turn
                setWhiteTurn(!isWhiteTurn());

                return moveAlgebraic + " " + (pieceToMove.isWhite() ? "(White)" : "(Black)"); // Return the algebraic
                                                                                              // notation of the move
            }
        }

        return null;
    }

    private String getChessCoordinate(int row, int col) {
        char file = (char) ('a' + col); // 'a' to 'h' for columns
        int rank = 8 - row; // '8' to '1' for rows
        return "" + file + rank;
    }

    public String getNotationSymbol(Piece piece) {
        if (piece == null) {
            return ""; // No piece means no symbol
        }

        // Get the class name of the piece (e.g., "Knight", "Pawn", etc.)
        String className = piece.getClass().getSimpleName();

        switch (className) {
            case "Pawn":
                return ""; // Pawns don't use a symbol in algebraic notation
            case "Knight":
                return "N"; // Knight is represented by 'N'
            case "Bishop":
                return "B"; // Bishop is represented by 'B'
            case "Rook":
                return "R"; // Rook is represented by 'R'
            case "Queen":
                return "Q"; // Queen is represented by 'Q'
            case "King":
                return "K"; // King is represented by 'K'
            default:
                return ""; // Fallback case if a new piece type is introduced
        }
    }

    private int[] performCastling(King king, int kingRow, int kingCol) {
        boolean kingSide = (kingCol == 6);
        int rookCol = kingCol < 4 ? 0 : 7; // Determine if king-side or queen-side
        int newRookCol = kingCol < 4 ? 3 : 5; // New rook position after castling
        int rookRow = kingRow;
        int newKingCol = kingSide ? 6 : 2;

        int[] capturedPiecePosition = new int[2];

        // Assign values to the array
        capturedPiecePosition[0] = rookRow;
        capturedPiecePosition[1] = newRookCol;

        Piece rook = getPiece(rookRow, rookCol);
        if (rook instanceof Rook) {
            // Move the rook to its new position
            setPiece(rookRow, newRookCol, rook);
            setPiece(rookRow, rookCol, null);
            rook.setPosition(rookRow, newRookCol);

            // Move the king to its new position
            setPiece(kingRow, kingCol, king);
            king.setPosition(kingRow, newKingCol);

            // Update castling rights
            updateCastlingRightsAfterMove(king);
            updateCastlingRightsAfterMove(rook);
        }
        return capturedPiecePosition;
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

    private boolean canEnpassant;

    public boolean isEnPassant(int row, int col) {
        System.out.println("Trying to see if enpassant");
        if (moveStack.isEmpty()) {
            System.out.println("Stack is empty");
            return false;
        }
        if (!(row == 3 || row == 4)) {
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
        // Check if the move was a two-square pawn advance
        if (Math.abs(startRow - newRow) == 2 && startCol == newCol) {

            // Determine the direction of the pawn
            if (col == 0) {
                if (getPiece(row, col + 1) instanceof Pawn) {
                    System.out.println("Enpassant true");
                    canEnpassant = true;
                    return true;
                }
            }
            if (col == 7) {
                if (getPiece(row, col - 1) instanceof Pawn) {
                    System.out.println("Enpassant true");
                    canEnpassant = true;
                    return true;
                }
            }
            if (getPiece(row, col + 1) instanceof Pawn || getPiece(row, (col - 1)) instanceof Pawn) {
                System.out.println("Enpassant true");
                canEnpassant = true;
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

            if (capturedPiece != null) {
                board.setPiece(lastMove.getCapturePieceRow(), lastMove.getCapturePieceCol(), capturedPiece);
                capturedPiece.setPosition(lastMove.getCapturePieceRow(), lastMove.getCapturePieceCol());
                if (movedPiece.isWhite() == capturedPiece.isWhite()) {
                    board.setPiece(capturedPiece.getRow(), capturedPiece.getCol(), null);
                }
                // In castling the rook is market as captured and its moved position is in
                // coordinates
                if (movedPiece.isWhite() == capturedPiece.isWhite()) {
                    System.out.println("Captured piece was white");
                    if (capturedPiece.getCol() == 3 && isWhiteTurn()) {
                        board.setPiece(0, 0, capturedPiece);
                    }
                    if (capturedPiece.getCol() == 5 && isWhiteTurn()) {
                        board.setPiece(0, 7, capturedPiece);

                    }
                    if (capturedPiece.getCol() == 3 && !isWhiteTurn()) {
                        board.setPiece(7, 0, capturedPiece);

                    }
                    if (capturedPiece.getCol() == 5 && !isWhiteTurn()) {
                        board.setPiece(7, 7, capturedPiece);

                    }
                }
            } else {
                board.setPiece(lastMove.getToRow(), lastMove.getToCol(), null);
            }

            updateKingPositions();
            setWhiteTurn(!isWhiteTurn());
            if (!whiteCanCastleKingSide && isWhiteTurn()) {

                whiteCanCastleKingSide = true;
            }
            if (!whiteCanCastleQueenSide && isWhiteTurn()) {

                whiteCanCastleQueenSide = true;
            }
            if (!blackCanCastleKingSide && !isWhiteTurn()) {

                blackCanCastleKingSide = true;
            }
            if (!blackCanCastleQueenSide && !isWhiteTurn()) {
                blackCanCastleQueenSide = true;
            }

        }

    }
    // fix the fen to use castling and enpassant aswell.

    public void recordMove(int fromRow, int fromCol, int toRow, int toCol, Piece capturedPiece, Piece movedPiece,
            int capturePieceRow, int capturePieceCol, String fen) {
        moveStack.push(new Move(fromRow, fromCol, toRow, toCol, capturedPiece, movedPiece, capturePieceRow,
                capturePieceCol, fen));
    }

    public boolean isInCheck() {
        boolean isInCheck = false; // Local variable for check status

        // Get positions of both kings
        int[] whiteKingPosition = getWhiteKingPosition();
        int[] blackKingPosition = getBlackKingPosition();

        // Check if white king is in check
        if (whiteKingPosition != null) {
            int kingRow = whiteKingPosition[0];
            int kingCol = whiteKingPosition[1];
            isInCheck = isKingInCheck(kingRow, kingCol);
        }

        // Check if black king is in check
        if (blackKingPosition != null) {
            int kingRow = blackKingPosition[0];
            int kingCol = blackKingPosition[1];
            isInCheck |= isKingInCheck(kingRow, kingCol);
        }

        return isInCheck;
    }

    // Helper method to determine if a specific king is in check
    private boolean isKingInCheck(int kingRow, int kingCol) {
        // Check all opponent's pieces
        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                Piece piece = board.getPiece(r, c);
                if (piece != null && piece.isWhite() != board.getPiece(kingRow, kingCol).isWhite()) {
                    List<int[]> possibleMoves = piece.getPossibleMoves(this);
                    for (int[] move : possibleMoves) {
                        // Check if the king is in check
                        if (move[0] == kingRow && move[1] == kingCol) {
                            return true; // King is in check
                        }
                    }
                }
            }
        }
        return false; // King is not in check
    }

    public boolean isInCheck(boolean isWhite) {
        isInCheck = false;
        if (!whiteHasCastled) {
            whiteCanCastleKingSide = true;
            whiteCanCastleQueenSide = true;
        }
        if (!blackHasCastled) {
            blackCanCastleKingSide = true;
            blackCanCastleQueenSide = true;
        }

        int[] kingPosition = isWhite ? getWhiteKingPosition() : getBlackKingPosition();
        if (kingPosition == null) {
            System.out.println("King not found");
            return false; // King not found
        }

        int kingRow = kingPosition[0];
        int kingCol = kingPosition[1];

        // Check all opponent's pieces
        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                Piece piece = board.getPiece(r, c);
                if (piece != null && piece.isWhite() != isWhite) {
                    possibleMoves = piece.getPossibleMoves(this);
                    for (int[] move : possibleMoves) {
                        // Check if the king is in check
                        if (move[0] == kingRow && move[1] == kingCol) {
                            isInCheck = true;
                        }

                        // Check castling squares
                        if (isWhite) {
                            if ((move[0] == 7 && move[1] == 5) || (move[0] == 7 && move[1] == 6)) {
                                whiteCanCastleKingSide = false; // f1 or g1 under attack
                            }
                            if ((move[0] == 7 && move[1] == 3) || (move[0] == 7 && move[1] == 2)) {
                                whiteCanCastleQueenSide = false; // d1 or c1 under attack
                            }
                        } else {
                            if ((move[0] == 0 && move[1] == 5) || (move[0] == 0 && move[1] == 6)) {
                                blackCanCastleKingSide = false; // f8 or g8 under attack
                            }
                            if ((move[0] == 0 && move[1] == 3) || (move[0] == 0 && move[1] == 2)) {
                                blackCanCastleQueenSide = false; // d8 or c8 under attack
                            }
                        }
                    }
                }
            }
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

    private boolean gameOver;

    public boolean checkMate(Game game) {
        // List to hold all legal moves for the current player's pieces
        List<int[]> allLegalMoves = new ArrayList<>();

        // Retrieve all pieces of the current player
        List<Piece> pieces = game.getAllPieces(whiteTurn);

        // Iterate over each piece and collect its legal moves
        for (Piece piece : pieces) {
            List<int[]> legalMovesForPiece = piece.getLegalMovesWithoutCheck(game);
            allLegalMoves.addAll(legalMovesForPiece);
        }

        if (isInCheck(whiteTurn) && allLegalMoves.isEmpty()) {
            gameOver = true;
            return true;
        }

        return false;
    }

    public boolean checkDraw(Game game) {
        // List to hold all legal moves for the current player's pieces
        List<int[]> allLegalMoves = new ArrayList<>();
        // Retrieve all pieces of the current player
        List<Piece> pieces = game.getAllPieces(whiteTurn);
        List<Piece> pieces1 = game.getAllPieces(!whiteTurn);
        if (pieces.size() == 1 && pieces1.size() == 1) {
            return true;
        }

        for (Piece piece : pieces) {
            List<int[]> legalMovesForPiece = piece.getLegalMovesWithoutCheck(game);
            allLegalMoves.addAll(legalMovesForPiece);
        }

        if (!isInCheck(whiteTurn) && allLegalMoves.isEmpty()) {
            return true;
        }
        return false;
    }

    public Game copyGame() {
        // Create a new Game object with a deep copy of the board
        return new Game(this.board.copyBoard());
    }

    public String toFen() {
        String fen = getBoard().toFEN(isWhiteTurn(), whiteCanCastleKingSide, whiteCanCastleQueenSide,
                blackCanCastleKingSide, blackCanCastleQueenSide, canEnpassant);
        return fen;
    }

}
