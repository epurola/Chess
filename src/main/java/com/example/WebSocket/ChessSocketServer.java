package com.example.WebSocket;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;

import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import com.example.Game;
import com.example.Move;
import com.example.Piece;

public class ChessSocketServer extends WebSocketServer {
    private Game game;
    private Map<org.java_websocket.WebSocket, Boolean> playerTurnMap;

    public ChessSocketServer(InetSocketAddress address) {
        super(address);
        this.game = new Game(); // Initialize the game instance
        this.playerTurnMap = new HashMap<>(); // Track player turns
    }

    @Override
    public void onStart() {
        System.out.println("WebSocket server started successfully");
    }

    @Override
    public void onOpen(org.java_websocket.WebSocket conn, ClientHandshake handshake) {
        System.out.println("New connection from: " + conn.getRemoteSocketAddress());
        playerTurnMap.put(conn, null); // Initialize connection state
        if (playerTurnMap.size() == 2) {
            // Notify both players that the game can start
            for (org.java_websocket.WebSocket client : playerTurnMap.keySet()) {
                client.send("Game starts now. White's turn.");
            }
        }
    }

    @Override
    public void onClose(org.java_websocket.WebSocket conn, int code, String reason, boolean remote) {
        System.out.println("Connection closed: " + conn.getRemoteSocketAddress() + " with exit code: " + code + " and reason: " + reason);
        playerTurnMap.remove(conn);
    }

    @Override
    public void onMessage(org.java_websocket.WebSocket conn, String message) {
        System.out.println("Received message: " + message);
    
        String[] parts = message.split(",");
        
        if (parts.length == 4) {
            // Handle moves
            int fromRow = Integer.parseInt(parts[0]);
            int fromCol = Integer.parseInt(parts[1]);
            int toRow = Integer.parseInt(parts[2]);
            int toCol = Integer.parseInt(parts[3]);
            boolean moveSuccessful = game.makeMove(fromRow, fromCol, toRow, toCol);
    
            boolean isWhitePlayer = playerTurnMap.get(conn); // Determine player color
            boolean isWhiteTurn = game.isWhiteTurn();
    
            if (isWhitePlayer) {
                isWhitePlayer = playerTurnMap.size() % 2 == 0;
                playerTurnMap.put(conn, isWhitePlayer);
                conn.send(isWhitePlayer ? "You are playing as White" : "You are playing as Black");
            }
    
            if ((isWhitePlayer && isWhiteTurn) || (!isWhitePlayer && !isWhiteTurn)) {
                System.out.println("You made a move");
                moveSuccessful = game.makeMove(fromRow, fromCol, toRow, toCol);
    
                if (moveSuccessful) {
                    broadcast(getBoardState());
                    if (game.checkMate(game)) {
                        broadcast("Checkmate! " + (isWhiteTurn ? "Black" : "White") + " wins.");
                    } else if (game.checkDraw(game)) {
                        broadcast("Draw! No legal moves left.");
                    } else {
                        broadcast(isWhiteTurn ? "White's turn." : "Black's turn.");
                    }
                } else {
                    conn.send("Invalid move or not your turn.");
                }
            } else {
                conn.send("It's not your turn.");
            }
        } else if (parts.length == 3 && "PROMOTE".equalsIgnoreCase(parts[0])) {
            // Handle promotion
            int row = Integer.parseInt(parts[1]);
            int col = Integer.parseInt(parts[2]);
            String pieceName = parts[3];
            game.promotePawn(row, col, pieceName);
        } else if ("REQUEST_BOARD_STATE".equalsIgnoreCase(message)) {
            // Handle board state request
            conn.send(getBoardState());
        } else {
            conn.send("Invalid message format.");
        }
    }
    private String getBoardState() {
        // Create a StringBuilder to build the board state string
        StringBuilder sb = new StringBuilder("BOARD_STATE,");
        // Iterate through the board to gather piece information
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                Piece piece = game.getPiece(row, col);
                if (piece != null) {
                    // Append piece details followed by a semicolon
                    String pieceName = piece.getClass().getSimpleName().toLowerCase().substring(0, 1); // Get the first letter of the piece type
                    String colorPrefix = piece.isWhite() ? "w-" : "b-"; 
                    sb.append(colorPrefix).append(pieceName).append(",").append(row).append(",").append(col).append(";");
                } else {
                    // Append "x" for an empty square
                    String emptyPreFix = "e-";
                    sb.append(emptyPreFix).append("x,").append(row).append(",").append(col).append(";");
                }
            }
        }
        
        // Remove the trailing semicolon if present
        if (sb.length() > "BOARD_STATE,".length()) {
            sb.setLength(sb.length() - 1);
        }
        return sb.toString();
    }
    
    @Override
    public void onError(org.java_websocket.WebSocket conn, Exception ex) {
        ex.printStackTrace();
    }
    public static void main(String[] args) {
        try {
            ChessSocketServer server = new ChessSocketServer(new InetSocketAddress(8887));
            server.start();
            System.out.println("WebSocket server started successfully on port 8887");
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Failed to start WebSocket server");
        }
    }
}





