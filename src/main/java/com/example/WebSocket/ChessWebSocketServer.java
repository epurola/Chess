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
            Integer fromRow = jsonMessage.optInt("fromRow", -1);
            Integer fromCol = jsonMessage.optInt("fromCol", -1);
            Integer movedRow = jsonMessage.optInt("movedRow");
            Integer movedCol = jsonMessage.optInt("movedCol");
            Integer toRow = jsonMessage.optInt("toRow", -1);
            Integer toCol = jsonMessage.optInt("toCol", -1);
            boolean isWhiteTurn = jsonMessage.optBoolean("isWhiteTurn");
            String capturedPiece = jsonMessage.optString("capturedPiece");
            String selectedPiece = jsonMessage.optString("selectedPiece");
            boolean isCastle = jsonMessage.optBoolean("isCastle");
            System.out.println(isCastle);
    
            // Call the broadcastMove method with the extracted data
            broadcastMove(pieceName, fromRow, fromCol, movedRow, movedCol, toRow, toCol, isWhiteTurn, capturedPiece, selectedPiece, isCastle, conn);
    
        } catch (Exception e) {
            System.err.println("Unexpected error: " + e.getMessage());
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

    public void broadcastMove(String pieceName, int fromRow, int fromCol, int movedRow, int movedCol, int toRow, int toCol, boolean isWhiteTurn, String capturedPiece, String selectedPiece, boolean isCastle, WebSocket sender) {
        // Create JSON payload using JSONObject
        JSONObject jsonPayload = new JSONObject();
    
        try {
            jsonPayload.put("pieceName", pieceName);
            jsonPayload.put("fromRow", fromRow);
            jsonPayload.put("fromCol", fromCol);
            jsonPayload.put("movedRow", movedRow);
            jsonPayload.put("movedCol", movedCol);
            jsonPayload.put("toRow", toRow);
            jsonPayload.put("toCol", toCol);
            jsonPayload.put("isWhiteTurn", isWhiteTurn);
            jsonPayload.put("capturedPiece", capturedPiece);
            jsonPayload.put("selectedPiece", selectedPiece);
            jsonPayload.put("isCastle", isCastle);
    
            // Print the JSON payload for debugging
            System.out.println("Broadcasting: " + jsonPayload.toString());
    
            // Determine which client to send the move to (the opponent)
            WebSocket opponent;
            if (sender.equals(whitePlayer)) {
                opponent = blackPlayer;
            } else {
                opponent = whitePlayer;
            }
    
            // Send the move to the opponent only if their connection is open
            if (opponent != null && opponent.isOpen()) {
                opponent.send(jsonPayload.toString()); // Send the JSON string
            }
    
        } catch (Exception e) {
            e.printStackTrace();
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








