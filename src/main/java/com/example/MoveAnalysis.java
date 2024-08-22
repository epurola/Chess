package com.example;

public class MoveAnalysis {
    private String fen;
    private String playerMove;
    private String bestMove;
    private String score;

    public MoveAnalysis(String fen, String playerMove, String bestMove, String score) {
        this.fen = fen;
        this.playerMove = playerMove;
        this.bestMove = bestMove;
        this.score = score;
    }

    public String getFEN() {
        return fen;
    }

    public String getPlayerMove() {
        return playerMove;
    }

    public String getBestMove() {
        return bestMove;
    }

    public String getScore() {
        return score;
    }

    public boolean isBlunder() {
        // Check if the score is a blunder (e.g., a significant drop in centipawns)
        // For simplicity, we'll define a blunder as a drop of more than 200 centipawns
        if (score.contains("centipawns")) {
            int scoreValue = Integer.parseInt(score.split(" ")[0]);
            return Math.abs(scoreValue) >= 200;
        }
        return false;
    }

    @Override
    public String toString() {
        return "FEN: " + fen + ", Move: " + playerMove + ", Best Move: " + bestMove + ", Score: " + score;
    }
}


