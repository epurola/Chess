package com.example;

public class MoveAnalysis {
    private String fen;
    private String playerMove;
    private String bestMove;
    private String score;
    private String previousScoreValue;
    private String bestMoveLine;

    public MoveAnalysis(String fen, String playerMove, String bestMove, String score, String previousScoreValue, String bestMoveLine) {
        this.fen = fen;
        this.playerMove = playerMove;
        this.bestMove = bestMove;
        this.score = score;
        this.previousScoreValue = previousScoreValue;
        this.bestMoveLine = bestMoveLine;
    }
    public String getBestLine()
    {
       return bestMoveLine ;
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
    @Override
    public String toString() {
        return "FEN: " + fen + ", Move: " + playerMove + ", Best Move: " + bestMove + ", Score: " + score;
    }
}


