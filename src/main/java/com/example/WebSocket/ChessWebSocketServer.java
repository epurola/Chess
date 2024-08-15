package com.example.WebSocket;

import org.java_websocket.server.WebSocketServer;
import org.json.JSONObject;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChessWebSocketServer extends WebSocketServer {
    private List<WebSocket> clients = new ArrayList<>();
     private Map<WebSocket, Boolean> playerTurnMap; 
     private WebSocket whitePlayer;
     private WebSocket blackPlayer;

    public ChessWebSocketServer(InetSocketAddress address) {
        super(address);
         this.playerTurnMap = new HashMap<>();
    }

    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake) {
        System.out.println("New connection from: " + conn.getRemoteSocketAddress());
        
        // Add the new client to the list
        clients.add(conn);
    
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
        } else {
            // Notify that the game is full or some other handling
            conn.send("Game is full");
            conn.close(); // Optionally close the connection if too many players
        }
    }
    

    @Override
    public void onMessage(WebSocket conn, String message) {
        System.out.println("Received message: " + message);
    
        try {
            JSONObject jsonMessage = new JSONObject(message);
    
            String pieceName = jsonMessage.optString("pieceName");
            Integer fromRow = jsonMessage.optInt("fromRow", -1); // Default to -1 if not present
            Integer fromCol = jsonMessage.optInt("fromCol", -1);
            Integer movedRow = jsonMessage.optInt("movedRow");
            Integer movedCol = jsonMessage.optInt("movedCol");
            Integer toRow = jsonMessage.optInt("toRow", -1);
            Integer toCol = jsonMessage.optInt("toCol", -1);
            boolean isWhiteTurn = jsonMessage.optBoolean("isWhiteTurn");
            String capturedPiece = jsonMessage.optString("capturedPiece");
            String selectedPiece = jsonMessage.optString("selectedPiece");
    
            // Call the broadcastMove method with the extracted data
            broadcastMove(pieceName, fromRow, fromCol,movedRow,movedCol, toRow, toCol, isWhiteTurn, capturedPiece, selectedPiece);
    
        } 
         catch (Exception e) {
            System.err.println("Unexpected error: " + e.getMessage());
            // Handle general exceptions
        }
    }

    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {
        clients.remove(conn);
        System.out.println("Client disconnected: " + conn.getRemoteSocketAddress());
    }

    @Override
    public void onError(WebSocket conn, Exception ex) {
        ex.printStackTrace();
    }

    @Override
    public void onStart() {
        System.out.println("Server started successfully.");
    }

    public void broadcastMove(String pieceName, int fromRow, int fromCol,int movedRow, int movedCol, int toRow, int toCol, boolean isWhiteTurn, String capturedPiece, String selectedPiece) {
        // Create JSON payload
        String jsonPayload = String.format("{\"pieceName\":\"%s\",\"fromRow\":%s,\"fromCol\":%s,\"movedRow\":%s,\"movedCol\":%s,\"toRow\":%s,\"toCol\":%s,\"isWhiteTurn\":%b,\"capturedPiece\":%s}",
                                            pieceName, fromRow, fromCol,movedRow,movedCol, toRow, toCol, isWhiteTurn, capturedPiece, selectedPiece);
        
        // Iterate over all clients and send the JSON payload
        for (WebSocket client : clients) {
            if (client.isOpen()) {
                client.send(jsonPayload);
            }
        }
    }

    public static void main(String[] args) {
        try {
            ChessWebSocketServer server = new ChessWebSocketServer(new InetSocketAddress(8887));
            server.start();
            System.out.println("WebSocket server started successfully on port 8887");
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Failed to start WebSocket server");
        }
    }
}








