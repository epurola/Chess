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
    private String fen;
    private String[] parts;
    private String board ;
    private Piece piece;
    private String playerMove;
    private Board oldState;
    private static final Map<Character, String> pieceNames = new HashMap<>();
  

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
public MoveAdvisor(Game game, String bestMove, String playerMove) {
        this.game = game;
        this.playerMove =playerMove;
        
        int[] bMove = parseMove(playerMove);
        this.fromRow = bMove[0];
        this.fromCol = bMove[1];
        this.toRow = bMove[2];
        this.toCol = bMove[3];
        oldState = game.getBoard();
        game.makeMove(fromRow, fromCol, toRow, toCol);
        piece = game.getBoard().getPiece(toRow, toCol);
        fen = game.toFen();
        parts = fen.split(" ");
        board=parts[0];
    }

  
public String analyzeMove() {
        if (isForkingMove()) {
            return "The move "+ playerMove+ " is good because it forks the opponent.";
        } else if (isBlunder()) {
            return "The move "+ playerMove+ " is a blunder because it leaves a valuable piece hanging.";
        } 
        else if(canCaptureMoreValuablePiece())
        {
          return "The move "+ playerMove + " is a good because it captures a more valuable piece.";
        }
        else {
            return "The move is solid "+ playerMove+ ", but nothing extraordinary.";
        }
    }
   
private boolean isForkingMove() {
        List<int[]> opponentPositions = getOpponentPiecePositions(fen, piece.isWhite());
        System.out.println("Opponent Piece Positions:");
        for (int[] pos : opponentPositions) {
            System.out.println("Row: " + pos[0] + ", Col: " + pos[1]);
        }
        List<int[]> allMoves = piece.getPossibleMoves(game);
        System.out.println("Possible Moves for the Piece:" + piece.getClass().toString()+piece.isWhite());
        for (int[] move : allMoves) {
            System.out.println("Move to Row: " + move[0] + ", Col: " + move[1]);
        }
        System.out.print("Piece Position"+ piece.getRow() +piece.getCol());
        int attackCount = 0;
        for (int[] move : allMoves) {
            System.out.println("Checking move to Row: " + move[0] + ", Col: " + move[1]);
            boolean isAttackingOpponent = false;
            for (int[] opponentPosition : opponentPositions) {
                if (move[0] == opponentPosition[0] && move[1] == opponentPosition[1]) {
                    isAttackingOpponent = true;
                    System.out.println("Move targets an opponent's piece at Row: " + opponentPosition[0] + ", Col: " + opponentPosition[1]);
                    break; 
                }
            }
            if (isAttackingOpponent) {
                attackCount++;
                System.out.println("Total attack count so far: " + attackCount);
            }
            if (attackCount > 1) {
                System.out.println("Forking move detected! Attack count: " + attackCount);
                return true;
            }
        }
        System.out.println("No forking move detected. Final attack count: " + attackCount);
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
        boolean isWhiteTurn = piece.isWhite(); 
        List<int[]> opponentPositions = getOpponentPiecePositions(fen, isWhiteTurn);
        System.out.println("Opponent Piece Positions:");
        for (int[] pos : opponentPositions) {
            System.out.println("Row: " + pos[0] + ", Col: " + pos[1]);
        }
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

private boolean canCaptureMoreValuablePiece() {
       int[]pmove = parseMove(playerMove);
        int toRow= pmove[2];
        int toCol = pmove[3];
        Piece capturedPiece;
        if(game.getPiece(toRow, toCol) != null)
        {
            capturedPiece = oldState.getPiece(toRow, toCol);
        }
        else
        {
            return false;
        }
        return capturedPiece.getValue() > piece.getValue();
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
    
private int getPieceValue(Piece piece) {
      
        switch (piece.getClass().getSimpleName()) {
            case "Pawn":
                return 1;
            case "Knight":
                return 3;
            case "Bishop":
                return 3;
            case "Rook":
                return 5;
            case "Queen":
                return 9;
            case "King":
                return 0; 
            default:
                return 0;
        }
    }
}