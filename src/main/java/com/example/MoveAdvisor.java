package com.example;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MoveAdvisor {

    private Game game;
    private int toRow;
    private int toCol;
    private int fromRow;
    private int fromCol;
    private int btoRow;
    private int btoCol;
    private int bfromRow;
    private int bfromCol;
    private String fen;
    private String[] parts;
    private String board;
    private Piece piece;
    private String playerMove;
    private Board oldState;
    private Stockfish stockfish;
    private String bestMove;
    private Piece bestPiece;
    private double scoreChange;
    private String moveCategory;
    private static final Map<Character, String> pieceNames = new HashMap<>();
    private String scoreString;
    private String bestLine;
    private String fenM;
    private String mateInfo;

    static {
        pieceNames.put('K', "White King");
        pieceNames.put('Q', "White Queen");
        pieceNames.put('R', "White Rook");
        pieceNames.put('B', "White Bishop");
        pieceNames.put('N', "White Knight");
        pieceNames.put('P', "White Pawn");
        pieceNames.put('k', "Black King");
        pieceNames.put('q', "Black Queen");
        pieceNames.put('r', "Black Rook");
        pieceNames.put('b', "Black Bishop");
        pieceNames.put('n', "Black Knight");
        pieceNames.put('p', "Black Pawn");
    }

    public MoveAdvisor(Game game, String bestmove, String playerMove, Stockfish stockfish, String moveGategory,
            String scoreString, String bestLine, String fen, String mateInfo) {
        this.game = game;
        this.playerMove = playerMove;
        this.stockfish = stockfish;
        this.moveCategory = moveGategory;
        this.scoreString = scoreString;
        this.bestLine = bestLine;
        this.fenM = fen;
        this.mateInfo = mateInfo;

        int[] pMove = parseMove(playerMove);
        this.fromRow = pMove[0];
        this.fromCol = pMove[1];
        this.toRow = pMove[2];
        this.toCol = pMove[3];
        this.bestMove = bestmove;

        // int[] bMove = parseMove(bestmove);
        // this.bfromRow = bMove[0];
        // this.bfromCol = bMove[1];
        // this.btoRow = bMove[2];
        // this.btoCol = bMove[3];
        oldState = game.getBoard();

        piece = game.getBoard().getPiece(toRow, toCol);
        // game.makeMoveReplay(bfromRow, bfromCol, btoRow, btoCol);
        bestPiece = game.getPiece(toRow, toCol);

        fen = game.toFen();

        parts = fen.split(" ");
        board = parts[0];
    }

    public String analyzeMove() {

        // Check for moves involving checkmates
        if (scoreString.contains("M")) {
            if (mateInfo.contains("Allowed opponent to checkmate")) {
                return "The move " + playerMove + " allows your opponent to checkmate you.";
            } else if (mateInfo.contains("force a win")) {
                return "The move " + playerMove + " allows you to force a checkmate. Brilliant!";
            } else if (mateInfo.contains("Missed a checkmate opportunity")) {
                return "The move " + playerMove + " missed a checkmate opportunity. This is a mistake.";
            }
        }

        // Check for specific categories of moves
        if (moveCategory.equals("Mistake")) {
            return "The move " + playerMove + " is a mistake.";
        } else if (moveCategory.equals("Inaccuracy")) {
            return "The move " + playerMove + " is an inaccuracy.";
        } else if (moveCategory.equals("Blunder")) {
            return "The move " + playerMove + " is a blunder. " + playBestLine(bestLine);
        }

        // Specific tactical checks
        if (isGood() && isCheck() && !scoreString.contains("#")) {
            return "The move " + playerMove + " puts the king in check.";
        } else if (isPin() && isGood()) {
            return "The move " + playerMove + " is strong because it pins a piece.";
        } else if (scoreString.contains("#")) {
            return "Nice! You found a checkmate!";
        }

        if (bestMove.equals(playerMove)) {
            return analyzeMissedBestMove();
        }
        // Default response for solid but unremarkable moves
        return "The move " + playerMove + " is solid, but nothing extraordinary.";
    }

    private String analyzeMissedBestMove() {
        StringBuilder analysis = new StringBuilder();
        if (moveCategory.equals("Good") ||
                moveCategory.equals("Brilliant") ||
                moveCategory.equals("Slight Improvement") ||
                moveCategory.equals("Best")) {

            analysis.append("The move " + playerMove + " is The " + moveCategory + "\n");
        } else {
            analysis.append("The move " + playerMove + " is a " + moveCategory + "  \n");
            analysis.append("The best move would have been " + bestMove + ". ");
            analysis.append("This would lead to a better position for you.");
        }

        if (isForkingMove()) {
            analysis.append(" The best move also creates a fork, attacking two of your opponent's pieces at once.\n");
        } else if (isPin()) {
            analysis.append(" The best move pins one of your opponent's pieces, limiting their options.\n");
        } else if (isSkewer()) {
            analysis.append(" The best move skews an opponent's piece, forcing a more favorable exchange.\n");
        }

        return analysis.toString();
    }

    private boolean isGood() {
        return bestMove.equals(playerMove);
    }

    private boolean isSkewer() {
        return false;
    }

    private boolean isPin() {
        if (piece == null) {
            return false;
        }
        List<int[]> opponentPositions = getOpponentPiecePositions(fen, piece.isWhite());
        List<int[]> ownPositions = getOpponentPiecePositions(fen, !piece.isWhite());
        int[] kingPosition = piece.isWhite() ? game.getBlackKingPosition() : game.getWhiteKingPosition();
        if (!(bestPiece instanceof Rook || bestPiece instanceof Bishop || bestPiece instanceof Queen)) {
            return false;
        }
        if (!isAlignedWithKing(kingPosition)) {
            return false;
        }
        // Check for opponent pieces between the piece and the king
        int piecesBetween = countOpponentPiecesBetween(opponentPositions, ownPositions, kingPosition);

        return piecesBetween == 1;

    }

    public String playBestLine(String bestLine) {
        Game gameCopy = new Game();
        gameCopy.getBoard().setFEN(fenM); // Start from the given FEN position

        String[] parts = bestLine.split(", ");
        System.out.println("BestLine"+bestLine);

        // Calculate the initial material for both players
        int whiteMaterialBefore = calculateTotalMaterial(gameCopy.getBoard(), true);
        int blackMaterialBefore = calculateTotalMaterial(gameCopy.getBoard(), false);
        if(!bestLine.isBlank())
        {
        for (String move : parts) {
            
                int[] moveParsed = parseMove(move);
            
           
            System.out.println(bestLine);

            System.out.println("row"+moveParsed[0]+ "col"+moveParsed[1]+ "row"+moveParsed[2]+ "col"+moveParsed[3]);
            System.out.print("Move"+move);
            
            gameCopy.makeMoveReplay(moveParsed[0], moveParsed[1], moveParsed[2], moveParsed[3]);

            // Calculate the material after the move
            int whiteMaterialAfter = calculateTotalMaterial(gameCopy.getBoard(), true);
            int blackMaterialAfter = calculateTotalMaterial(gameCopy.getBoard(), false);

            // Update material for the next move
            whiteMaterialBefore = whiteMaterialAfter;
            blackMaterialBefore = blackMaterialAfter;
            
        }
    }
        // Determine who came out on top
        String finalAnalysis = analyzeFinalMaterial(whiteMaterialBefore, blackMaterialBefore);

        return finalAnalysis;
    }

    private String analyzeFinalMaterial(int whiteMaterial, int blackMaterial) {
        if (whiteMaterial > blackMaterial) {
            return "White is ahead with " + (whiteMaterial - blackMaterial)
                    + " points of material after best line is played.\n";
        } else if (blackMaterial > whiteMaterial) {
            return "Black is ahead with " + (blackMaterial - whiteMaterial)
                    + " points of material after best line is played.\n";
        } else {
            return "This move missed an opportunit!\n";
        }
    }

    public int calculateTotalMaterial(Board board, boolean isWhite) {
        int totalMaterial = 0;

        // Loop through all the squares on the board
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                Piece piece = board.getPiece(row, col); // Get the piece at the current position
                if (piece != null && piece.isWhite() == isWhite) {
                    totalMaterial += getPieceValue(piece); // Add the value of the piece
                }
            }
        }

        return totalMaterial;
    }

    // Method to get the value of a piece
    public int getPieceValue(Piece piece) {
        // Logic to determine the piece value
        if (piece instanceof Pawn) {
            return 1;
        } else if (piece instanceof Rook) {
            return 5;
        } else if (piece instanceof Knight) {
            return 3;
        } else if (piece instanceof Bishop) {
            return 3;
        } else if (piece instanceof Queen) {
            return 9;
        } else if (piece instanceof King) {
            return Integer.MAX_VALUE; // or some other high value
        }
        return 0; // default for unrecognized piece types
    }

    private int countOpponentPiecesBetween(List<int[]> opponentPositions, List<int[]> ownPositions,
            int[] kingPosition) {
        int count = 0;
        int rowDirection = Integer.compare(kingPosition[0], bestPiece.getRow()); // Direction towards the king
        int colDirection = Integer.compare(kingPosition[1], bestPiece.getCol());

        // Start checking from the piece's position, one step towards the king
        int row = bestPiece.getRow() + rowDirection;
        int col = bestPiece.getCol() + colDirection;

        // Traverse the path towards the king until we reach the king's position
        while (row != kingPosition[0] || col != kingPosition[1]) {
            // Check if there is an opponent piece at the current position
            boolean foundOpponent = false;
            boolean foundOwnPiece = false;

            for (int[] opponentPos : opponentPositions) {
                if (opponentPos[0] == row && opponentPos[1] == col) {
                    foundOpponent = true; // Found an opponent piece
                    break; // No need to continue checking other positions
                }
            }

            for (int[] ownPos : ownPositions) {
                if (ownPos[0] == row && ownPos[1] == col) {
                    foundOwnPiece = true; // Found an own piece
                    break; // No need to continue checking other positions
                }
            }

            if (foundOwnPiece) {
                return 0; // If there’s an own piece, cannot be a pin
            }

            if (foundOpponent) {
                count++; // Increment the count for opponent pieces found
            }

            // Move one step closer to the king
            row += rowDirection;
            col += colDirection;
        }

        return count; // Return the total count of opponent pieces between
    }

    private boolean isAlignedWithKing(int[] kingPosition) {
        if (bestPiece instanceof Queen || bestPiece instanceof Rook) {
            // Check for alignment on the same row (horizontal)
            if (bestPiece.getRow() == kingPosition[0]) {
                return true; // Same row
            }
            // Check for alignment on the same column (vertical)
            if (bestPiece.getCol() == kingPosition[1]) {
                return true; // Same column
            }
        }

        if (bestPiece instanceof Queen || bestPiece instanceof Bishop) {
            // Check for alignment on the same diagonal
            int rowDiff = Math.abs(bestPiece.getRow() - kingPosition[0]);
            int colDiff = Math.abs(bestPiece.getCol() - kingPosition[1]);
            if (rowDiff == colDiff) {
                return true; // Same diagonal
            }
        }

        return false; // Not aligned
    }

    private boolean isCheck() {
        if (piece != null) {
            return game.isInCheck(!piece.isWhite());
        } else {
            return false;
        }

    }

    // FIX here the is sometimes null
    private boolean isForkingMove() {
        if (piece == null) {
            return false;
        }
        List<int[]> opponentPositions = getOpponentPiecePositions(fen, piece.isWhite());

        List<int[]> allMoves = piece.getPossibleMoves(game);

        int attackCount = 0;
        for (int[] move : allMoves) {

            boolean isAttackingOpponent = false;
            for (int[] opponentPosition : opponentPositions) {
                if (move[0] == opponentPosition[0] && move[1] == opponentPosition[1]) {
                    isAttackingOpponent = true;
                    break;
                }
            }
            if (isAttackingOpponent) {
                attackCount++;
            }
            if (attackCount > 1) {
                return true;
            }
        }
        return false;
    }

    public List<int[]> getOpponentPiecePositions(String fen, boolean isWhite) {
        List<int[]> opponentPositions = new ArrayList<>();

        String[] ranks = board.split("/");
        int rankNumber = 0;

        for (String rank : ranks) {
            int fileNumber = 0;

            for (char symbol : rank.toCharArray()) {
                if (Character.isDigit(symbol)) {
                    fileNumber += Character.getNumericValue(symbol);
                } else {

                    if ((isWhite && Character.isLowerCase(symbol)) || (!isWhite && Character.isUpperCase(symbol))) {
                        opponentPositions.add(new int[] { rankNumber, fileNumber });
                    }
                    fileNumber++;
                }
            }
            rankNumber++;
        }

        return opponentPositions;
    }


    public static int[] parseMove(String bestMove) {
   
        

            String from = bestMove.substring(0, 2); // e2
            String to = bestMove.substring(2, 4); // e4
            int fromCol = from.charAt(0) - 'a'; // 'e' - 'a' = 4
            int fromRow = '8' - from.charAt(1); // '8' - '2' = 6

            int toCol = to.charAt(0) - 'a'; // 'e' - 'a' = 4
            int toRow = '8' - to.charAt(1); // '8' - '4' = 4
            return new int[] { fromRow, fromCol, toRow, toCol };
        
       
        

    }

}