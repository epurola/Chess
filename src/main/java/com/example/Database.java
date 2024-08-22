package com.example;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import com.example.MoveAnalysis;

public class Database {

    private static final String URL = "jdbc:sqlite:move_analysis.db";
    private Connection connection;


    public Database() {
        createTables();
    }
    public Database(String url) {
        try {
            this.connection = DriverManager.getConnection(url);
            createTables();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to connect to the database.", e);
        }
    }
    // Method to get the total number of games
public int getTotalGames() {
    String sql = "SELECT COUNT(*) AS total FROM games";
    try (Connection conn = DriverManager.getConnection(URL);
         Statement stmt = conn.createStatement();
         ResultSet rs = stmt.executeQuery(sql)) {
        if (rs.next()) {
            return rs.getInt("total");
        }
    } catch (SQLException e) {
        System.out.println(e.getMessage());
    }
    return 0; // Return 0 if the query fails
}


    // Create the tables if they don't exist
    private void createTables() {
        String createGamesTable = "CREATE TABLE IF NOT EXISTS games ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "game_name TEXT NOT NULL "
                + ");";

        String createMoveAnalysisTable = "CREATE TABLE IF NOT EXISTS move_analysis ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "game_id INTEGER NOT NULL, "
                + "move_number INTEGER NOT NULL, "
                + "fen TEXT NOT NULL, "
                + "move TEXT NOT NULL, "
                + "best_move TEXT NOT NULL, "
                + "score INTEGER NOT NULL, "
                + "FOREIGN KEY (game_id) REFERENCES games(id)"
                + ");";

        try (Connection conn = DriverManager.getConnection(URL);
             Statement stmt = conn.createStatement()) {
            stmt.execute(createGamesTable);
            stmt.execute(createMoveAnalysisTable);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    // Insert a new game
    public int insertGame(String gameName) {
        String sql = "INSERT INTO games(game_name) VALUES(?)";
        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, gameName);
            pstmt.executeUpdate();
            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                }
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return -1; // Indicates failure
    }

    // Insert move analysis data into the database
    public void insertMoveAnalysis(int gameId, int moveNumber, MoveAnalysis moveAnalysis) {
        String sql = "INSERT INTO move_analysis(game_id, move_number, fen, move, best_move, score) VALUES(?,?,?,?,?,?)";
        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, gameId);
            pstmt.setInt(2, moveNumber);
            pstmt.setString(3, moveAnalysis.getFEN());
            pstmt.setString(4, moveAnalysis.getPlayerMove());
            pstmt.setString(5, moveAnalysis.getBestMove());
            pstmt.setString(6, moveAnalysis.getScore());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    // Retrieve move analysis data for a specific game
    public List<MoveAnalysis> getMoveAnalysis(int gameId) {
        List<MoveAnalysis> moveHistory = new ArrayList<>();
        String sql = "SELECT move_number, fen, move, best_move, score FROM move_analysis WHERE game_id = ? ORDER BY move_number DESC;";
        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, gameId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    int moveNumber = rs.getInt("move_number");
                    String fen = rs.getString("fen");
                    String move = rs.getString("move");
                    String bestMove = rs.getString("best_move");
                    String score = rs.getString("score");

                    MoveAnalysis moveAnalysis = new MoveAnalysis(fen, move, bestMove, score);
                    moveHistory.add(moveAnalysis);
                }
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return moveHistory;
    }

    // Clear move history for a specific game
    public void clearMoveHistory(int gameId) {
        String sql = "DELETE FROM move_analysis WHERE game_id = ?";
        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, gameId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
}

