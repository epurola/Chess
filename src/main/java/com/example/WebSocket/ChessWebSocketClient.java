package com.example.WebSocket;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import com.example.MultiplayerController;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import org.json.JSONObject;
import org.json.JSONException;

public class ChessWebSocketClient extends WebSocketClient {

    private MultiplayerController controller;
    private Boolean isWhite; // Stores whether the client is playing as white or black

    public ChessWebSocketClient(URI serverUri) {
        super(serverUri);
    }

    public boolean getIsWhite()
    {
        return isWhite;
    }

    public void setController(MultiplayerController controller) {
        this.controller = controller;
    }

    @Override
    public void onOpen(ServerHandshake handshakedata) {
        System.out.println("Connected to server");
    }

    @Override
    public void onMessage(String message) {
        System.out.println("Received message: " + message);
    
        try {
            // Check if the message is a color assignment
            if (message.startsWith("COLOR:")) {
                String color = message.substring(6).trim();
                if (controller != null) {
                    controller.setPlayerColor(color);
                }
            } else {
                // Parse the message as JSON
                JSONObject jsonObject = new JSONObject(message);
                String messageType = jsonObject.optString("type");
    
                switch (messageType) {
                    case "move":
                        // Handle move messages
                        String pieceName = jsonObject.optString("pieceName");
                        int fromRow = jsonObject.optInt("fromRow", -1);
                        int fromCol = jsonObject.optInt("fromCol", -1);
                        Integer movedRow = jsonObject.optInt("movedRow");
                        Integer movedCol = jsonObject.optInt("movedCol");
                        int toRow = jsonObject.optInt("toRow", -1);
                        int toCol = jsonObject.optInt("toCol", -1);
                        boolean isWhiteTurn = jsonObject.optBoolean("isWhiteTurn");
                        String capturedPiece = jsonObject.optString("capturedPiece");
                        boolean isCastle = jsonObject.optBoolean("isCastle");
    
                        if (controller != null) {
                            controller.updateGameState(pieceName, fromRow, fromCol, movedRow, movedCol, toRow, toCol, isWhiteTurn, capturedPiece, isCastle);
                        }
                        break;
    
                    case "playAgain":
                        // Handle play again messages
                        if (controller != null) {
                            // Call startNewGame on the controller to restart the game
                            controller.initialize();
                        }
                        break;
    
                    default:
                        System.out.println("Unknown message type: " + messageType);
                        break;
                }
            }
        } catch (JSONException e) {
            System.err.println("Error parsing JSON: " + e.getMessage());
            // Handle JSON parsing error
        } catch (NumberFormatException e) {
            System.err.println("Error parsing numbers: " + e.getMessage());
            // Handle number parsing error
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void sendMove(String pieceName, Integer fromRow, Integer fromCol, Integer movedRow, Integer movedCol, Integer toRow, Integer toCol, boolean isWhiteTurn, String capturedPiece, boolean isCastle) {
        // Create a JSON object to represent the move
        JSONObject moveMessage = new JSONObject();
        
        // Add data to the JSON object
        moveMessage.put("type", "move");
        moveMessage.put("pieceName", pieceName);
        moveMessage.put("fromRow", fromRow != null ? fromRow : JSONObject.NULL);
        moveMessage.put("fromCol", fromCol != null ? fromCol : JSONObject.NULL);
        moveMessage.put("movedRow", movedRow != null ? movedRow : JSONObject.NULL);
        moveMessage.put("movedCol", movedCol != null ? movedCol : JSONObject.NULL);
        moveMessage.put("toRow", toRow != null ? toRow : JSONObject.NULL);
        moveMessage.put("toCol", toCol != null ? toCol : JSONObject.NULL);
        moveMessage.put("isWhiteTurn", isWhiteTurn);
        moveMessage.put("capturedPiece", capturedPiece != null ? capturedPiece : JSONObject.NULL);
        moveMessage.put("isCastle", isCastle);
        
        // Convert the JSON object to a string
        String jsonPayload = moveMessage.toString();
        
        // Send the JSON payload
        send(jsonPayload);
    }
    

    @Override
    public void onClose(int code, String reason, boolean remote) {
        System.out.println("Disconnected from server");
    }

    @Override
    public void onError(Exception ex) {
        ex.printStackTrace();
    }

    public static void main(String[] args) {
        try {
            URI serverUri = new URI("ws://localhost:8887");
            ChessWebSocketClient client = new ChessWebSocketClient(serverUri);
            client.connectBlocking();

        } catch (URISyntaxException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}





