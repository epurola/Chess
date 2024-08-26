package com.example;

import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Database {

    private static final String DB_FILE_NAME = "move_analysis.db";
    private static final String DB_DIRECTORY = System.getProperty("user.home") + "/Database"; // Uses user's home directory
    private static final String DB_PATH = DB_DIRECTORY + "/" + DB_FILE_NAME;
    private static final String URL = "jdbc:sqlite:" + DB_PATH;

    static {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("SQLite JDBC driver not found.", e);
        }
    }

    public Database() {
        createDatabaseDirectory();
        createTables();
    }

    private void createDatabaseDirectory() {
        File dbDir = new File(DB_DIRECTORY);
        if (!dbDir.exists()) {
            boolean dirCreated = dbDir.mkdirs(); // Create directory and any missing parent directories
            if (!dirCreated) {
                throw new RuntimeException("Failed to create database directory: " + dbDir.getAbsolutePath());
            }
            System.out.println("Database directory created: " + dbDir.getAbsolutePath());
        }
    }

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
                + "previous_score INTEGER, " 
                + "FOREIGN KEY (game_id) REFERENCES games(id)"
                + ");";

        try (Connection conn = DriverManager.getConnection(URL);
             Statement stmt = conn.createStatement()) {
            stmt.execute(createGamesTable);
            stmt.execute(createMoveAnalysisTable);
            System.out.println("Tables created or verified.");
        } catch (SQLException e) {
            System.out.println("Failed to create tables: " + e.getMessage());
        }
    }

    public int getTotalGames() {
        String sql = "SELECT COUNT(*) AS total FROM games";
        try (Connection conn = DriverManager.getConnection(URL);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getInt("total");
            }
        } catch (SQLException e) {
            System.out.println("Failed to get total games: " + e.getMessage());
        }
        return 0;
    }

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
            System.out.println("Failed to insert game: " + e.getMessage());
        }
        return -1;
    }

    public void insertMoveAnalysis(int gameId, int moveNumber, MoveAnalysis moveAnalysis) {
        String sql = "INSERT INTO move_analysis(game_id, move_number, fen, move, best_move, score, previous_score) VALUES(?,?,?,?,?,?,?)";
        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, gameId);
            pstmt.setInt(2, moveNumber);
            pstmt.setString(3, moveAnalysis.getFEN());
            pstmt.setString(4, moveAnalysis.getPlayerMove());
            pstmt.setString(5, moveAnalysis.getBestMove());
            pstmt.setString(6, moveAnalysis.getScore()); 
            if (moveAnalysis.getPreviousscore() != null) {
                pstmt.setString(7, moveAnalysis.getPreviousscore()); 
            } else {
                pstmt.setNull(7, Types.INTEGER);
            }
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Failed to insert move analysis: " + e.getMessage());
        }
    }

    public List<MoveAnalysis> getMoveAnalysis(int gameId) {
        List<MoveAnalysis> moveHistory = new ArrayList<>();
        String sql = "SELECT move_number, fen, move, best_move, score, previous_score FROM move_analysis WHERE game_id = ? ORDER BY move_number DESC;";
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
                    String previousScore = rs.getString("previous_score");

                    MoveAnalysis moveAnalysis = new MoveAnalysis(fen, move, bestMove, score,previousScore);
                    moveHistory.add(moveAnalysis);
                }
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return moveHistory;
    }

    public void clearMoveHistory(int gameId) {
        String sql = "DELETE FROM move_analysis WHERE game_id = ?";
        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, gameId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Failed to clear move history: " + e.getMessage());
        }
    }

    public void clearDatabase() {
        String dropMoveAnalysisTable = "DROP TABLE IF EXISTS move_analysis";
        String dropGamesTable = "DROP TABLE IF EXISTS games";

        try (Connection conn = DriverManager.getConnection(URL);
             Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(dropMoveAnalysisTable);
            stmt.executeUpdate(dropGamesTable);

            createTables();
        } catch (SQLException e) {
            System.out.println("Failed to clear database: " + e.getMessage());
        }
    }
}




