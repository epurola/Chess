package com.example;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;

/*This class is designed to build a prompt for a llm that could give advice on the move and categorize
 * it. ChatGPT is able to give pretty accurate analysis if prompted with this string and the move made. 
 * also giving it the best move will help. For cost related reason this might not be developed further...
 * Local models like llama 3 are ass and will give inaccurate information
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

    public static String fenToNaturalLanguage(String fen, Game game ) {
        String[] parts = fen.split(" ");
        String board = parts[0];
        String turn = parts[1];
        StringBuilder description = new StringBuilder();

        String[] ranks = board.split("/");
        int rankNumber = 1; 

        for (String rank : ranks) {
           
            int fileNumber = 1; // Start from file 'a' to 'h'
            for (char symbol : rank.toCharArray()) {
                if (Character.isDigit(symbol)) {
                    // Skip the number of empty squares
                    fileNumber += Character.getNumericValue(symbol);
                } else {
                    String piece = pieceNames.get(symbol);
              
                    String position = getPosition(fileNumber - 1, rankNumber - 1); 
                    // Get the current piece from the board at this position
                    Piece currentPiece = game.getBoard().getPiece(rankNumber-1 , fileNumber-1);
                    List<int[]>  possibleMoves = currentPiece.getLegalMovesWithoutCheck(game);

                    List<String> movesForPiece = getMovesForPiece(rankNumber-1, fileNumber-1,possibleMoves);

                    description.append(piece).append(" on ").append(position);
                    boolean isWhiteTurn = turn.equals("w");
                    boolean isPieceWhite = currentPiece.isWhite();

                    if (!movesForPiece.isEmpty() && isWhiteTurn == isPieceWhite) {
                        description.append(" with possible moves ").append(String.join(", ", movesForPiece));
                    }
                    
                    description.append(". ");
                       
                     fileNumber++;
                }
                
            }
            rankNumber++;
        }
        

        return description.toString().trim();
    }

    private static String getPosition(int fileNumber, int rankNumber) {
        // Convert zero-based fileNumber (0-7) to chess file letters (a-h)
        char file = (char) ('a' + fileNumber); // 0 -> 'a', 7 -> 'h'

        // Convert zero-based rankNumber (0-7) to chess ranks (8-1)
        int rank = rankNumber + 1; 

        // Return chess notation
        return String.valueOf(file) + rank;
    }

    private static List<String> getMovesForPiece(int fileNumber, int rankNumber,List<int[]> possibleMoves) {
        List<String> moves = new ArrayList<>();

        for (int[] move : possibleMoves) {
            int targetRow = move[0];
            int targetCol = move[1];

            // Convert row/column to chess notation
            String movePosition = getPosition(targetCol, targetRow); // Translate to chess notation
            moves.add(movePosition);
        }
        return moves;
    }

    public static void main(String[] args) {
        String fen = "r3k2r/p4ppp/n1pQ4/q3p3/4n3/P3BN2/2P2PPP/1R3RK1 w";
       Game game = new Game();
        game.getBoard().setFEN(fen);
        String description = fenToNaturalLanguage(fen, game);
        System.out.println(description);
    }
}




