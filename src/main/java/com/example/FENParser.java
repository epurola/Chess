package com.example;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/*This class is designed to build a prompt for a llm that could give advice on the move and categorize
 * it. ChatGPT is able to give pretty accurate analysis if prompted with this string and the move made. 
 * also giving it the best move will help. For cost related reason this might not be developed further...
 * Local models like llama 3 are ass and will give inaccurate information.
 * The api test are done with gemini which was ok at times but made mistakes.
 */
public class FENParser {
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

    public static String fenToNaturalLanguage(String fen, Game game, String bestMove, String playerMove) {
        // Split FEN string into its components
        String[] parts = fen.split(" ");
        String board = parts[0];
        String turn = parts[1];
        
        // Initialize the description builder
        StringBuilder description = new StringBuilder();
        description.append("Write two sentece analysis Why thiswas the best move and why potetially the players move was good or bad");
        description.append("The Best move was: ").append(bestMove);
        description.append("The player move move was: ").append(playerMove);
    
        // Split the board into ranks (rows)
        String[] ranks = board.split("/");
        int rankNumber = 1; 
    
        // Iterate over each rank
        for (String rank : ranks) {
            int fileNumber = 1; 
    
            // Process each character in the rank
            for (char symbol : rank.toCharArray()) {
                if (Character.isDigit(symbol)) {
                    fileNumber += Character.getNumericValue(symbol);
                } else {
                    // Determine piece and position
                    String piece = pieceNames.get(symbol);
                    String position = getPosition(fileNumber - 1, rankNumber - 1);
                    Piece currentPiece = game.getBoard().getPiece(rankNumber - 1, fileNumber - 1);
                    
                    // Get possible moves for the current piece
                    List<int[]> possibleMoves = currentPiece.getLegalMovesWithoutCheck(game);
                    List<String> movesForPiece = getMovesForPiece(rankNumber - 1, fileNumber - 1, possibleMoves);
    
                    // Build description for the current piece
                    description.append(piece)
                               .append(" is at ")
                               .append(position);
    
                    boolean isWhiteTurn = turn.equals("w");
                    boolean isPieceWhite = currentPiece.isWhite();
    
                    if (!movesForPiece.isEmpty() && isWhiteTurn == isPieceWhite) {
                        description.append(" with possible moves: ")
                                   .append(String.join(", ", movesForPiece));
                    }
    
                    description.append(". ");
                    fileNumber++;
                }
            }
            rankNumber++;
            description.append("\n"); // Add a newline to separate ranks
        }
    
        // Finalize the description
        description.append("It is ")
                   .append(turn.equals("w") ? "White's" : "Black's")
                   .append(" turn to move.");
                   
    
        return description.toString().trim();
    }
    
    

    private static String getPosition(int fileNumber, int rankNumber) {
        char file = (char) ('a' + fileNumber); 
    
        // Calculate the flipped rank number if needed
        int rank =  8 - rankNumber ;
    
        return String.valueOf(file) + rank;
    }
    

    private static List<String> getMovesForPiece(int fileNumber, int rankNumber,List<int[]> possibleMoves) {
        List<String> moves = new ArrayList<>();
        for (int[] move : possibleMoves) {
            int targetRow = move[0];
            int targetCol = move[1];
            String movePosition = getPosition(targetCol, targetRow); 
            moves.add(movePosition);
        }
        return moves;
    }

    /*public static void main(String[] args) {
        String fen = "r3k2r/p4ppp/n1pQ4/q3p3/4n3/P3BN2/2P2PPP/1R3RK1 w";
       Game game = new Game();
        game.getBoard().setFEN(fen);
        String description = fenToNaturalLanguage(fen, game);
        System.out.println(description);
    }*/

    
}




