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
    private String board ;
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
public MoveAdvisor(Game game, String bestmove,String playerMove, Stockfish stockfish, String moveGategory, String scoreString) {
        this.game = game;
        this.playerMove =playerMove;
        this.stockfish = stockfish;
        this.moveCategory = moveGategory;
        this.scoreString = scoreString;
        
        int[] pMove = parseMove(playerMove);
        this.fromRow = pMove[0];
        this.fromCol = pMove[1];
        this.toRow = pMove[2];
        this.toCol = pMove[3];
        this.bestMove = bestmove;
      
       // int[] bMove = parseMove(bestmove);
        //this.bfromRow = bMove[0];
       // this.bfromCol = bMove[1];
       // this.btoRow = bMove[2];
       // this.btoCol = bMove[3];
        oldState = game.getBoard();
        
        piece = game.getBoard().getPiece(toRow, toCol);
        game.makeMoveReplay(bfromRow, bfromCol, btoRow, btoCol);
        bestPiece = game.getPiece(toRow, toCol);
        
        fen = game.toFen();
        
        parts = fen.split(" ");
        board=parts[0];
    }

  
    public String analyzeMove() {
        if (scoreString.contains("mate")) {
            return "The move " + playerMove + " This will move lead in to checkmate";
        } else if (isForkingMove() && isCheck() && isGood()) {
            return "The move " + playerMove + "is great! You put the opponent in check while also forking material\n" ;
        } else if ( isGood() && isCheck()) {
            return "The move " + playerMove + " puts the king in check. "  ;
        } else if (isForkingMove() && isGood()) {
            return "The move " + playerMove + " is good because it forks the opponent.";
        } else if (isPin() && isGood()) {
            return "The move " + playerMove + " is strong because it pins a piece." ;
        } else if (isSkewer() && isGood()) {
            return "The move " + playerMove + " is powerful as it skews the opponent's piece." ;
        } else if (moveCategory.equals("Blunder")) {
            return "The move " + playerMove + " is a blunder." ;
        
        } else if (!bestMove.equals(playerMove)) {
            return analyzeMissedBestMove();
        } else {
            return "The move " + playerMove + " is solid, but nothing extraordinary.\n" ;
        }
    }
    
    private String analyzeMissedBestMove() {
        StringBuilder analysis = new StringBuilder();
        if (moveCategory.equals("Good") || 
        moveCategory.equals("Brilliant") || 
        moveCategory.equals("Slight Improvement") || 
        moveCategory.equals("Best")) {
        
        analysis.append("The move " + playerMove + " is a " + moveCategory + " move.\n");
    } else {
        analysis.append("The move " + playerMove + " is a " + moveCategory + " move. \n");
        analysis.append("The best move would have been " + bestMove + ". ");
        analysis.append("This would lead to a better position for you.");
    }

        if (isForkingMove()  ) {
            analysis.append(" The best move also creates a fork, attacking two of your opponent's pieces at once.\n");
        } else if (isPin() ) {
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
    if(piece == null)
    {
        return false;
    }
    List<int[]> opponentPositions = getOpponentPiecePositions(fen, piece.isWhite());
    List<int[]> ownPositions = getOpponentPiecePositions(fen, !piece.isWhite());
    int[] kingPosition = piece.isWhite() ? game.getBlackKingPosition() : game.getWhiteKingPosition();
    if (!(bestPiece instanceof Rook || bestPiece instanceof Bishop || bestPiece instanceof Queen)) {
        return false; 
    }
    if(!isAlignedWithKing(kingPosition))
    {
        return false; 
    }
    // Check for opponent pieces between the piece and the king
    int piecesBetween = countOpponentPiecesBetween(opponentPositions ,ownPositions, kingPosition);
    
    return piecesBetween == 1; 

}

private int countOpponentPiecesBetween(List<int[]> opponentPositions, List<int[]> ownPositions, int[] kingPosition) {
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

private boolean isAlignedWithKing( int[] kingPosition) {
    if(bestPiece instanceof Queen || bestPiece instanceof Rook)
    {
    // Check for alignment on the same row (horizontal)
    if (bestPiece .getRow()== kingPosition[0]) {
        return true; // Same row
    }
    // Check for alignment on the same column (vertical)
    if (bestPiece .getCol()== kingPosition[1]) {
        return true; // Same column
    }
}

    if(bestPiece  instanceof Queen || bestPiece  instanceof Bishop)
    {
      // Check for alignment on the same diagonal
       int rowDiff = Math.abs(bestPiece .getRow() - kingPosition[0]);
       int colDiff = Math.abs(bestPiece .getCol()- kingPosition[1]);
        if (rowDiff == colDiff) {
            return true; // Same diagonal
        }
    }
   
    return false; // Not aligned
}


private boolean isCheck() {
    if(piece!=null)
    {
        return game.isInCheck(!piece.isWhite());
    }
    else
    {
        return false;
    }
      
    }


private boolean isCheckmate() {
        return false;
    }

//FIX here the is sometimes null
private boolean isForkingMove() {
    if(piece == null)
    {
        return false;
    }
        List<int[]> opponentPositions = getOpponentPiecePositions(fen, piece.isWhite());
      
        List<int[]> allMoves = piece.getPossibleMoves(game);
       
        int attackCount = 0;
        for (int[] move : allMoves) {
        
            boolean isAttackingOpponent = false;
            for (int[] opponentPosition : opponentPositions) {
                if (move[0] == opponentPosition[0] && move[1] == opponentPosition[1] ) {
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
                        opponentPositions.add(new int[]{rankNumber, fileNumber});
                    }
                    fileNumber++;
                }
            }
            rankNumber++;
        }
    
        return opponentPositions;
    }
    

private boolean isBlunder() {
    if (bestMove.equals(playerMove)) {
        return false; 
    }
        boolean isWhiteTurn = piece.isWhite(); 
        List<int[]> opponentPositions = getOpponentPiecePositions(fen, isWhiteTurn);
        List<int[]> opponentMoves = new ArrayList<>();
        for (int[] opponentPosition : opponentPositions) {
            Piece opponentPiece = game.getBoard().getPiece(opponentPosition[0], opponentPosition[1]);
            if(opponentPiece != null)
            {
                 opponentMoves = opponentPiece.getLegalMovesWithoutCheck(game);
            }
            for (int[] movePosition : opponentMoves) {
                Piece targetPiece = game.getBoard().getPiece(movePosition[0], movePosition[1]);
                
                if (isValuablePiece(targetPiece)) {
                    return true;  
                }
            }
        }
        return false;
    }
   


    
private boolean isValuablePiece(Piece piece) {
        if (piece == null) return false;
        return piece.getValue() >= 5;  
    }
 
public static int[] parseMove(String bestMove) {
        String from = bestMove.substring(0, 2); 
        String to = bestMove.substring(2, 4);  
        int fromCol = from.charAt(0) - 'a';   
        int fromRow = '8' - from.charAt(1);     
        int toCol = to.charAt(0) - 'a';        
        int toRow = '8' - to.charAt(1);        
        return new int[] {fromRow, fromCol, toRow, toCol};
    }
    
}