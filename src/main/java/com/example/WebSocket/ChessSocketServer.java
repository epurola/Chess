package com.example.WebSocket;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;
import com.example.Game;
import com.example.Knight;
import com.example.Piece;

public class ChessSocketServer extends WebSocketServer {
    private Game game;
    private Map<WebSocket, Boolean> playerTurnMap; // true for White, false for Black
    private WebSocket whitePlayer;
    private WebSocket blackPlayer;
    private boolean isWhiteTurn; // true if it's White's turn
    

    public ChessSocketServer(InetSocketAddress address) {
        super(address);
        this.game = new Game();
        this.playerTurnMap = new HashMap<>();
        this.isWhiteTurn = true; // Start with White's turn
        System.out.print("TURN SET TO WHITE");
    }

    @Override
    public void onStart() {
        System.out.println("WebSocket server started successfully");
    }

    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake) {
        System.out.println("New connection from: " + conn.getRemoteSocketAddress());

        // Assign color based on the number of connected players
        if (playerTurnMap.size() == 0) {
            // First player is White
            whitePlayer = conn;
            playerTurnMap.put(conn, true);
            conn.send("COLOR: white");
        } else if (playerTurnMap.size() == 1) {
            // Second player is Black
            blackPlayer = conn;
            playerTurnMap.put(conn, false);
            conn.send("COLOR: black");
            broadcast("Game starts now. White's turn.");
        } else {
            // Reject additional connections
            conn.send("Game already full.");
            conn.close();
        }
    }

    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {
        System.out.println("Connection closed: " + conn.getRemoteSocketAddress() + " with exit code: " + code + " and reason: " + reason);
        if (playerTurnMap.containsKey(conn)) {
            boolean wasWhite = playerTurnMap.get(conn);
            playerTurnMap.remove(conn);
            if (wasWhite) {
                whitePlayer = null;
            } else {
                blackPlayer = null;
            }
            broadcast("A player has disconnected.");
        }
    }
   // Changes turn on invalid move also;
    @Override
    public void onMessage(WebSocket conn, String message) {
        System.out.println("Received message: " + message);
        String[] parts = message.split(",");
        
        boolean isWhitePlayer = playerTurnMap.getOrDefault(conn, false);
        
        if (parts.length == 6) {
            // Handle moves with 6 components
            int fromRow = Integer.parseInt(parts[0]);
            int fromCol = Integer.parseInt(parts[1]);
            int toRow = Integer.parseInt(parts[2]);
            int toCol = Integer.parseInt(parts[3]);
            String capturedPiece = parts[4];
            String movedPiece = parts[5];
            
            // Check if the move is attempted by the player whose turn it is
            if ((isWhitePlayer && isWhiteTurn) || (!isWhitePlayer && !isWhiteTurn)) {
                boolean moveSuccessful = game.makeMove(fromRow, fromCol, toRow, toCol);
                if (moveSuccessful) {
                    broadcast(getBoardState());
                    // Check game status
                    if (game.checkMate(game)) {
                        broadcast("Checkmate! " + (isWhiteTurn ? "Black" : "White") + " wins.");
                    } else if (game.checkDraw(game)) {
                        broadcast("Draw! No legal moves left.");
                    } else {
                        // Switch turn
                        isWhiteTurn = !isWhiteTurn;
                        System.out.print("TURN SET TO " + isWhiteTurn);
                        broadcast(isWhiteTurn ? "White's turn." : "Black's turn.");
                    }
                } else {
                    conn.send("Invalid move.");
                }
            } else {
                conn.send("It's not your turn.");
            }
        } else if (parts.length == 4 && "PROMOTE".equalsIgnoreCase(parts[0])) {
            // Handle promotion
            int row = Integer.parseInt(parts[1]);
            int col = Integer.parseInt(parts[2]);
            String pieceName = parts[3];
            game.promotePawn(row, col, pieceName);
            broadcast(getBoardState());
        } else if ("REQUEST_BOARD_STATE".equalsIgnoreCase(message)) {
            broadcast(getBoardState());
            conn.send(getBoardState());
        } else {
            conn.send("Invalid message format.");
        }
    }

    private String getBoardState() {
        // Create a StringBuilder to build the board state string
        StringBuilder sb = new StringBuilder("BOARD_STATE,");
        String turnIndicator = isWhiteTurn ? "w" : "b";
        sb.append(turnIndicator).append(",");
        // Iterate through the board to gather piece information
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                Piece piece = game.getPiece(row, col);
                if (piece != null) {
                    // Append piece details followed by a semicolon
                    String pieceName = piece.getClass().getSimpleName().toLowerCase().substring(0, 1);
                    if (piece instanceof Knight) {
                        pieceName = "n";
                    }
                    String colorPrefix = piece.isWhite() ? "w-" : "b-";
                    sb.append(colorPrefix).append(pieceName).append(",").append(row).append(",").append(col).append(";");
                } else {
                    // Append "e-x" for an empty square
                    sb.append("e-x,").append(row).append(",").append(col).append(";");
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
    public void onError(WebSocket conn, Exception ex) {
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






