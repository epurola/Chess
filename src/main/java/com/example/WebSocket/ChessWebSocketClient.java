package com.example.WebSocket;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import com.example.MultiplayerController;

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
        if (message.startsWith("COLOR:")) {
            String color = message.substring(6).trim();
            if (controller != null) {
                controller.setColor(color);
            }
        } else {
            JSONObject jsonObject = new JSONObject(message);

            String pieceName = jsonObject.optString("pieceName");
            int fromRow = jsonObject.optInt("fromRow", -1);
            int fromCol = jsonObject.optInt("fromCol", -1);
            Integer movedRow = jsonObject.optInt("movedRow");
            Integer movedCol = jsonObject.optInt("movedCol");
            int toRow = jsonObject.optInt("toRow", -1);
            int toCol = jsonObject.optInt("toCol", -1);
            boolean isWhiteTurn = jsonObject.optBoolean("isWhiteTurn");
            String capturedPiece = jsonObject.optString("capturedPiece");

            if (controller != null) {
                controller.updateGameState(pieceName, fromRow, fromCol,movedRow,movedCol, toRow, toCol, isWhiteTurn, capturedPiece);
            }
        }
    } catch (JSONException e) {
        System.err.println("Error parsing JSON: " + e.getMessage());
        // Handle JSON parsing error
    } catch (NumberFormatException e) {
        System.err.println("Error parsing numbers: " + e.getMessage());
        // Handle number parsing error
    }
}


    public void sendMove(String pieceName, Integer fromRow, Integer fromCol, Integer movedRow, Integer movedCol,Integer toRow, Integer toCol, boolean isWhiteTurn, String capturedPiece) {
        // Change int to Integer to allow null values
        String jsonPayload = String.format("{\"pieceName\":\"%s\",\"fromRow\":%s,\"fromCol\":%s,\"movedRow\":%s,\"movedCol\":%s,\"toRow\":%s,\"toCol\":%s,\"isWhiteTurn\":%b,\"capturedPiece\":%s}",
                pieceName,
                fromRow != null ? fromRow : "null",
                fromCol != null ? fromCol : "null",
                movedRow,
                movedCol,
                toRow != null ? toRow : "null",
                toCol != null ? toCol : "null",
                isWhiteTurn,
                capturedPiece != null ? "\"" + capturedPiece + "\"" : "null");
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





