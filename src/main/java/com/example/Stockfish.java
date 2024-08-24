package com.example;

import java.io.*;

import java.util.ArrayList;
import java.util.List;

public class Stockfish {
    private Process stockfishProcess;
    private BufferedReader input;
    private PrintWriter output;
    private Game game;
    private List<MoveAnalysis> moveHistory;
    private String bestMove;
    private String bestMoveCurrent;
    private Database database;
    private String previousScoreValue;
 

    public Stockfish(Game game) throws IOException {
        this.game = game;
        this.moveHistory = new ArrayList<>();
        String bestMove = "";
        String bestMoveCurrent = "";
        previousScoreValue = "";
        database = new Database("jdbc:sqlite:move_analysis.db");
        // Initialize Stockfish process, input/output streams
        ProcessBuilder pb = new ProcessBuilder("stockfish.exe");
        stockfishProcess = pb.start();
        input = new BufferedReader(new InputStreamReader(stockfishProcess.getInputStream()));
        output = new PrintWriter(stockfishProcess.getOutputStream());
    }

    public Stockfish() throws IOException {
        this.moveHistory = new ArrayList<>();
        String bestMove = "";
        String bestMoveCurrent = "";

        // Initialize Stockfish process, input/output streams
        ProcessBuilder pb = new ProcessBuilder("stockfish.exe");
        stockfishProcess = pb.start();
        input = new BufferedReader(new InputStreamReader(stockfishProcess.getInputStream()));
        output = new PrintWriter(stockfishProcess.getOutputStream());
    }

    public void sendUciCommand(String command) {
        output.println(command);
        output.flush();
    }

    public String getBestMove() {
        String fen = game.toFen();
        sendUciCommand("position fen " + fen);
        sendUciCommand("go depth 20"); // Adjust depth as needed
        // Parse Stockfish's output for the best move
       
        try {
            String line;
            while ((line = input.readLine()) != null) {
                if (line.startsWith("bestmove")) {
                    bestMove = line.split(" ")[1];
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bestMove;
    }
//This is called from analyzegame and will get the best move for current player so it c
//can be stored. If get AI help is on we cant use the best move so that is why.
    public String getBestMoveFromFEN(String fen) {
        // Send UCI commands to Stockfish
        sendUciCommand("position fen " + fen);
        sendUciCommand("go depth 20"); // Adjust depth as needed
        // Parse Stockfish's output for the best move
       
        try {
            String line;
            while ((line = input.readLine()) != null) {
                if (line.startsWith("bestmove")) {
                    bestMoveCurrent = line.split(" ")[1];
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bestMoveCurrent;
    }

    public String analyzeMove(String fen, String move) {
        // Apply the move and request analysis
       System.out.println(fen);
        sendUciCommand("position fen " + fen + " moves " + move);
        sendUciCommand("go depth 20"); // Adjust depth as needed

       
        String score = "";

        try {
            String line;
            while ((line = input.readLine()) != null) {
                if (line.startsWith("info") && line.contains("score")) {
                    // Example parsing score from info lines
                    String[] tokens = line.split(" ");
                    for (int i = 0; i < tokens.length; i++) {
                        if (tokens[i].equals("score")) {
                            score = tokens[i + 1];
                            if (score.equals("cp")) {
                                score = tokens[i + 2] + " centipawns";
                            } else if (score.equals("mate")) {
                                score = tokens[i + 2] + " moves to mate";
                            }
                        }
                    }
                }
                if (line.startsWith("bestmove")) {
                    break;  // Stop reading once the best move is found
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
       

        // Save the move analysis to the history
        moveHistory.add(new MoveAnalysis(fen, move, bestMoveCurrent, score,previousScoreValue));
        previousScoreValue = score;
        System.err.println("Move: " + move + ", Best Move: " + bestMoveCurrent + ", Score: " + score);

        return "Move: " + move + ", Best Move: " + bestMove + ", Score: " + score;
    }

    // Method to get move history
    public List<MoveAnalysis> getMoveHistory() {
        return moveHistory;
    }

    // Method to identify blunders in the game
  
    public void handleGameEnd(String gameName ) {
        // Insert the game record and retrieve the game ID
        int gameId = database.insertGame(gameName);
         int moveNumber = 0;
        // Check if the game was created successfully
        if (gameId != -1) {
            // Iterate through the moveHistory list
            for (MoveAnalysis move : moveHistory) {
                moveNumber++;
                // Insert each move analysis record into the database
                database.insertMoveAnalysis(gameId,moveNumber ,move);
              
            }
        } else {
            System.out.println("Failed to create a new game record.");
        }
    }
    

    public void close() {
        try {
            // Clean up resources and destroy the Stockfish process
            sendUciCommand("quit");
            stockfishProcess.destroy();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
   

