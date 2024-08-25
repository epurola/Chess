package com.example;

import javafx.scene.paint.Color;

public class MoveAnalysis {
    private String fen;
    private String playerMove;
    private String bestMove;
    private String score;
    private String previousScoreValue;
    private Color scoreColor; 

    public MoveAnalysis(String fen, String playerMove, String bestMove, String score, String previousScoreValue) {
        this.fen = fen;
        this.playerMove = playerMove;
        this.bestMove = bestMove;
        this.score = score;
        this.previousScoreValue = previousScoreValue;
    }
    

   
    public Color getScoreColor() {
        return scoreColor;
    }
    public void setScoreColor(Color scoreColor) {
        this.scoreColor = scoreColor;
    }

    public String getFEN() {
        return fen;
    }
    public String getPreviousscore() {
        return previousScoreValue;
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

    // Check if the move is a blunder based on the score change compared to the previous score
    public boolean isBlunder(String previousScoreValue) {
       int previousScore = extractScoreValue(previousScoreValue);
        int currentScoreValue = extractScoreValue(score);
        // Define a blunder as a drop of more than 200 centipawns
        return (previousScore - currentScoreValue) >= 200;
    }

    // Check if the move is a great move based on the score change compared to the previous score
    public boolean isGreatMove(String previousScoreValue) {
        int previousScore = extractScoreValue(previousScoreValue);
        int currentScoreValue = extractScoreValue(score);
        // Define a great move as an increase of more than 200 centipawns
        return (currentScoreValue - previousScore) >= 200;
    }
    private int extractScoreValue(String score) {
        try {
            return Integer.parseInt(score.split(" ")[0]);
        } catch (NumberFormatException e) {
            return 0; // Return a default value in case of parsing error
        }
    }

    @Override
    public String toString() {
        return "FEN: " + fen + ", Move: " + playerMove + ", Best Move: " + bestMove + ", Score: " + score;
    }
}


