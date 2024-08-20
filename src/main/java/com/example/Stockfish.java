package com.example;

import java.io.*;

public class Stockfish {
    private Process stockfishProcess;
    private BufferedReader input;
    private PrintWriter output;
    private Game game;
  

    public Stockfish(Game game) throws IOException {
       this.game = game;
        // Initialize Stockfish process, input/output streams
        ProcessBuilder pb = new ProcessBuilder("C:/Users/eelip/Downloads/stockfish-windows-x86-64-sse41-popcnt/stockfish/stockfish-windows-x86-64-sse41-popcnt.exe");
        stockfishProcess = pb.start();
        input = new BufferedReader(new InputStreamReader(stockfishProcess.getInputStream()));
        output = new PrintWriter(stockfishProcess.getOutputStream());
    }

    public void sendUciCommand(String command) {
        output.println(command);
        output.flush();
    }

    public String getBestMove() {
        //Convert game state to FEN
        //implement this in the game class
        String fen = game.toFen();

        // Send UCI commands to Stockfish
        sendUciCommand("position fen " + fen);
        sendUciCommand("go depth 20"); // Adjust depth as needed

        // Parse Stockfish's output for the best move
        String bestMove = "";
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
        System.err.println(bestMove);
        return bestMove;
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
