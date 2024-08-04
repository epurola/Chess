package com.example.WebSocket;

import com.example.MessageCallBack;
import com.example.Move;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.function.BiConsumer;

public class ChessSocketClient extends WebSocketClient {

    private MessageCallBack messageCallback;
    private BiConsumer<String[][], String> boardStateCallback;

    public ChessSocketClient(URI serverUri) {
        super(serverUri);
    }

    public void setMessageCallback(MessageCallBack messageCallback) {
        this.messageCallback = messageCallback;
    }

    public void setBoardStateCallback(BiConsumer<String[][], String> callback) {
        this.boardStateCallback = callback;
    }

    @Override
    public void onOpen(ServerHandshake handshakedata) {
        System.out.println("Connected to server");
    }

    @Override
    public void onMessage(String message) {
        System.out.println("Received message: " + message);

        if (message.startsWith("COLOR:")) {
            String color = message.substring("COLOR:".length()).trim();
            if (messageCallback != null) {
                messageCallback.onPlayerColorReceived(color);
            }
        } else if (message.startsWith("BOARD_STATE")) {
            processBoardStateMessage(message);
        } 
    }


    @Override
    public void onClose(int code, String reason, boolean remote) {
        System.out.println("Connection closed with code: " + code + " and reason: " + reason);
    }

    @Override
    public void onError(Exception ex) {
        ex.printStackTrace();
    }

    public void sendMove(Move move) {
        int fromRow = move.getFromRow();
        int fromCol = move.getFromCol();
        int toRow = move.getToRow();
        int toCol = move.getToCol();
    
        String movedPiece = move.getMovedPiece() != null ? move.getMovedPiece().getClass().getSimpleName().toLowerCase() : "none";
        String capturedPiece = move.getCapturedPiece() != null ? move.getCapturedPiece().getClass().getSimpleName().toLowerCase() : "none";
    
        String moveMessage = String.format("%d,%d,%d,%d,%s,%s",
            fromRow, fromCol, toRow, toCol, capturedPiece, movedPiece);
    
        send(moveMessage);
    }

    public void sendPromotionRequest(int row, int col, String pieceName) {
        String promotionMessage = String.format("PROMOTE,%d,%d,%s", row, col, pieceName);
        send(promotionMessage);
    }
    public void requestBoardState() {
        // Define the message format for requesting the board state
        String requestMessage = "REQUEST_BOARD_STATE";
        // Send the request message to the server
        send(requestMessage);
    }


    private void processBoardStateMessage(String message) {
        final String PREFIX = "BOARD_STATE,";
        
        if (message.startsWith(PREFIX)) {
            // Remove the prefix
            String boardStateString = message.substring(PREFIX.length());
    
            // Extract the current turn from the message
            String[] parts = boardStateString.split(",", 2);
            String currentTurn = parts[0];  // "w" or "b"
            String boardData = parts.length > 1 ? parts[1] : "";  // Remaining board data
    
            // Parse the board state
            String[][] board = parseBoardState(boardData);
    
            // Convert currentTurn to a human-readable format
            String turnString = "w".equals(currentTurn) ? "white" : "black";
    
            // Trigger the callback with the parsed board and current turn
            if (boardStateCallback != null) {
                boardStateCallback.accept(board, turnString);
            }
        }
    }

    private String[][] parseBoardState(String boardStateString) {
        // Initialize an empty board with "x" for empty squares
        String[][] board = new String[8][8];
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                board[row][col] = "x";  // "x" indicates an empty square
            }
        }
        // Split the board state string by semicolons to get each piece's data
        String[] pieces = boardStateString.split(";");
        System.out.println(boardStateString);
    
        for (String pieceData : pieces) {
            // Split each piece's data into component parts
            String[] parts = pieceData.split(",");
            if (parts.length == 3) {
                String pieceIdentifier = parts[0];
                int row = Integer.parseInt(parts[1]);
                int col = Integer.parseInt(parts[2]);
    
                // Validate pieceIdentifier format
                if (pieceIdentifier.length() >= 3 
                && (pieceIdentifier.charAt(0) == 'w' 
                || pieceIdentifier.charAt(0) == 'b'
                || pieceIdentifier.charAt(0) == 'e')) {
                    // The identifier should be in the format like "w-r" or "b-p"
                    board[row][col] = pieceIdentifier;  // Set the pieceIdentifier at the given position
                } else {
                    System.err.println("Invalid piece identifier format!!!: " + pieceIdentifier);
                }
            } else {
                System.err.println("Invalid piece data format!!: " + pieceData );
            }
        }
    
        return board;
    }

    public static void main(String[] args) {
        try {
            URI serverUri = new URI("ws://localhost:8887");
            ChessSocketClient client = new ChessSocketClient(serverUri);
            client.connectBlocking();

        } catch (URISyntaxException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}



