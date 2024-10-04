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
    private String bestMoveCurrent;
    private Database database;
    private String previousScoreValue;
    private StringBuilder bestMovesSequence = new StringBuilder();

    public Stockfish(Game game) throws IOException {
        this.game = game;
        this.moveHistory = new ArrayList<>();
        previousScoreValue = "";
        database = new Database();

        // Extract Stockfish executable
        String stockfishPath = extractStockfishExecutable();

        // Initialize Stockfish process, input/output streams
        ProcessBuilder pb = new ProcessBuilder(stockfishPath);
        stockfishProcess = pb.start();
        input = new BufferedReader(new InputStreamReader(stockfishProcess.getInputStream()));
        output = new PrintWriter(stockfishProcess.getOutputStream(), true);  // autoFlush = true
        System.out.println("Stockfish ready");
    }

    private String extractStockfishExecutable() throws IOException {
        // Get the executable as a stream
        InputStream in = getClass().getResourceAsStream("/com/example/stockfish.exe");
        if (in == null) {
            throw new FileNotFoundException("Stockfish executable not found in the JAR.");
        }

        // Define the temporary file
        File tempFile = File.createTempFile("stockfish", ".exe");
        tempFile.deleteOnExit();

        // Copy the executable to the temporary file
        try (OutputStream out = new FileOutputStream(tempFile)) {
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = in.read(buffer)) != -1) {
                out.write(buffer, 0, bytesRead);
            }
        }

        return tempFile.getAbsolutePath();
    }

    public Stockfish() throws IOException {
        this.moveHistory = new ArrayList<>();
        previousScoreValue = "";

        // Extract Stockfish executable
        String stockfishPath = extractStockfishExecutable();

        // Initialize Stockfish process, input/output streams
        ProcessBuilder pb = new ProcessBuilder(stockfishPath);
        stockfishProcess = pb.start();
        input = new BufferedReader(new InputStreamReader(stockfishProcess.getInputStream()));
        output = new PrintWriter(stockfishProcess.getOutputStream(), true);  // autoFlush = true
        
        // Send UCI command to Stockfish
        output.println("uci"); // Send the UCI command
        String response;
        while ((response = input.readLine()) != null) {
            System.out.println("Stockfish: " + response); // Print response for debugging
            if (response.equals("uciok")) {
                break; // Stockfish is ready to accept commands
            }
        }
        
        System.out.println("Stockfish ready");

        // Start a new game
        output.println("ucinewgame");
    }

    public void sendUciCommand(String command) {
        output.println(command);  // println automatically adds \n
        output.flush();
    }

    public String getBestMove(String fen) {
        sendUciCommand("position fen " + fen );
        sendUciCommand("go depth 15");  // Adjust depth as needed

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

    public String getBestMoveFromFEN(String fen) {
        // Send UCI commands to Stockfish
        System.out.println(fen);
        sendUciCommand("position fen " + fen );
        sendUciCommand("go depth 20"); // Adjust depth as needed

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
        System.out.println("Move FEN: " + fen );
        // Set the position with the provided FEN
        sendUciCommand("position fen " + fen);
        sendUciCommand("go depth 15");
       


        String score = "0";
        String bestMove = "";
        bestMovesSequence.setLength(0);  // Clear previous sequence

        try {
            String line;
            while ((line = input.readLine()) != null) {
                
                if (line.startsWith("bestmove")) {
                    bestMove = line.split(" ")[1];
                    break;  // Stop reading once the best move is found
                }
                if (line.startsWith("info")) {
                    if (line.contains("pv")) {
                        bestMovesSequence.setLength(0);  // Clear the StringBuilder
                        String[] tokens = line.split(" ");
                        for (int i = 0; i < tokens.length; i++) {
                            if (tokens[i].equals("pv")) {
                                for (int j = i + 1; j < tokens.length; j++) {
                                    bestMovesSequence.append(tokens[j]).append(", ");
                                }
                                break;
                            }
                        }
                    }
                    if (line.contains("score")) {
                        String[] tokens = line.split(" ");
                        for (int i = 0; i < tokens.length; i++) {
                            if (tokens[i].equals("score")) {
                                if (tokens[i + 1].equals("cp")) {
                                    score = tokens[i + 2] + " centipawns";
                                } else if (tokens[i + 1].equals("mate")) {
                                    score = tokens[i + 2] + " moves to mate";
                                }
                                break;
                            }
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        moveHistory.add(new MoveAnalysis(fen, move, bestMove, score, previousScoreValue, bestMovesSequence.toString()));
        previousScoreValue = score;

        System.err.println("Move: " + move + ", Best Move: " + bestMove + ", Score: " + score);
        System.out.print(bestMovesSequence);

        return "Move: " + move + ", Best Move: " + bestMove + ", Score: " + score;
    }

    public List<MoveAnalysis> getMoveHistory() {
        return moveHistory;
    }

    public void handleGameEnd(String gameName) {
        int gameId = database.insertGame(gameName);
        int moveNumber = 0;

        if (gameId != -1) {
            for (MoveAnalysis move : moveHistory) {
                moveNumber++;
                database.insertMoveAnalysis(gameId, moveNumber, move);
            }
        } else {
            System.out.println("Failed to create a new game record.");
        }
    }

    public void close() {
        try {
            sendUciCommand("quit");
            stockfishProcess.destroy();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

