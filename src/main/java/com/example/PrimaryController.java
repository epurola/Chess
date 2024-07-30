package com.example;

import java.util.ArrayList;
import java.util.List;

import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;

public class PrimaryController {

    @FXML
    private GridPane chessBoard;
    
    @FXML
    private StackPane rootPane; 

    private Game board;
    private Piece selectedPiece;
    private ImageView draggedPiece;
    private double mouseOffsetX;
    private double mouseOffsetY;
    @FXML
    public void initialize() {
        board = new Game();
        drawBoard();

        // Set preferred size for the board
        chessBoard.setPrefSize(800, 800);

        // Center the chessBoard within the rootPane
        StackPane.setAlignment(chessBoard, javafx.geometry.Pos.CENTER);

        // Optionally: set the rootPane size to be 100% of the Scene
        rootPane.setPrefSize(800, 800);
    }

    private void drawBoard() {
        chessBoard.getChildren().clear(); // Clear existing children to reset the board

        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                Rectangle square = new Rectangle(100, 100);
                if ((i + j) % 2 == 0) {
                    square.setFill(Color.BEIGE);
                } else {
                    square.setFill(Color.DARKSEAGREEN);
                }

                chessBoard.add(square, j, i); // Add the square to the grid

                Piece piece = board.getPiece(i, j);
                if (piece != null) {
                    String color = piece.isWhite() ? "white-" : "black-";
                    String imagePath = "/images/" + color + piece.getClass().getSimpleName().toLowerCase() + ".png";
                    System.out.println("Attempting to load image: " + imagePath);
                    Image pieceImage = null;
                    try {
                        pieceImage = new Image(getClass().getResourceAsStream(imagePath));
                        if (pieceImage.isError()) {
                            System.out.println("Error loading image: " + pieceImage.getException());
                        }
                    } catch (Exception e) {
                        System.out.println("Exception loading image: " + e.getMessage());
                    }

                    if (pieceImage != null) {
                        ImageView pieceView = new ImageView(pieceImage);
                        pieceView.setFitHeight(100);
                        pieceView.setFitWidth(100);
                        chessBoard.add(pieceView, j, i);

                        if (board.isWhiteTurn()) {
                            // If it's white's turn, make black pieces transparent
                            pieceView.setMouseTransparent(!piece.isWhite());
                        } else {
                            // If it's black's turn, make white pieces transparent
                            pieceView.setMouseTransparent(piece.isWhite());
                        }

                        pieceView.setOnMousePressed(event -> handlePieceDragStart(event, pieceView, piece));
                        pieceView.setOnMouseDragged(this::handlePieceDrag);
                        pieceView.setOnMouseReleased(this::handlePieceDrop);
                    }
                }
            }
        }
    }

    private void handlePieceDragStart(MouseEvent event, ImageView pieceView, Piece piece) {
        selectedPiece = piece;
        draggedPiece = pieceView;
        drawPossibleMoves(selectedPiece);
        pieceView.toFront();
    }

    private void handlePieceDrag(MouseEvent event) {
        if (draggedPiece != null) {
           
            draggedPiece.setTranslateX(event.getSceneX() -50 - draggedPiece.getLayoutX()- chessBoard.localToScene(0,0).getX());
            draggedPiece.setTranslateY(event.getSceneY() -50 - draggedPiece.getLayoutY()- chessBoard.localToScene(0,0).getY());
        }
    }

    private void handlePieceDrop(MouseEvent event) {
        if (draggedPiece != null) {
            int row = (int) ((event.getSceneY() - chessBoard.localToScene(0,0).getY())/ 100);
            int col = (int) ((event.getSceneX()  - chessBoard.localToScene(0,0).getX())/ 100);
            int originalRow = selectedPiece.getRow();
            int originalCol = selectedPiece.getCol();
            
    
            if (row >= 0 && row < 8 || col >= 0 && col < 8) {
                // Check if the move is valid
                List<int[]> possibleMoves = selectedPiece.getPossibleMoves(board.getBoard());
                boolean validMove = possibleMoves.stream().anyMatch(move -> move[0] == row && move[1] == col);
    
                if (validMove) {
                    // Get the piece that will be captured, if any
                    Piece capturedPiece = board.getPiece(row, col);
    
                    // Temporarily make the move
                    Piece pieceToMove = selectedPiece.copy(); // Ensure a copy of the piece is used
                    board.setPiece(row, col, pieceToMove);
                    board.setPiece(originalRow, originalCol, null);
                    pieceToMove.setPosition(row, col);
    
                    // Check if the king is in check
                    if (board.isInCheck(board.isWhiteTurn())) {
                        // Revert the move if it puts the king in check
                        board.setPiece(originalRow, originalCol, pieceToMove);
                        board.setPiece(row, col, capturedPiece); // Restore the captured piece
                        pieceToMove.setPosition(originalRow, originalCol); // Restore the piece's position
                        System.out.println("Move puts king in check. Move reverted.");
                    } else {
                        // Valid move; switch turns if move is valid and does not put the king in check
                        if (originalRow != row || originalCol != col) {
                            board.setWhiteTurn(!board.isWhiteTurn());
                        }
                    }
                }
    
                drawBoard(); // Redraw the board to update the piece's position
            }
            selectedPiece = null;
            draggedPiece = null;
        }
    }
    
    

    private void drawPossibleMoves(Piece selectedPiece) {
        List<int[]> possibleMoves = new ArrayList<>();

        // Clear existing move indicators
        chessBoard.getChildren().removeIf(node -> node instanceof Circle);

        double squareSize = 100; // Size of each square on the board
        double indicatorSize = squareSize * 0.3; // Diameter of the indicator, e.g., 30% of square s
    
        possibleMoves=selectedPiece.getLegalMovesWithoutCheck(board);
        
        for (int[] move : possibleMoves) {
            int row = move[0];
            int col = move[1];

            // Create and configure the move indicator
            Circle moveIndicator = new Circle(indicatorSize / 2);
            moveIndicator.setFill(Color.GRAY.deriveColor(0, 1, 1, 0.3)); // Set fill color with transparency (30% opacity)

            // Use a StackPane to center the Circle
            StackPane moveIndicatorContainer = new StackPane();
            moveIndicatorContainer.setPickOnBounds(false);
            moveIndicatorContainer.getChildren().add(moveIndicator);
            moveIndicatorContainer.setPrefSize(squareSize, squareSize); // Ensure the container matches the square size

            moveIndicatorContainer.setMouseTransparent(true);

            chessBoard.add(moveIndicatorContainer, col, row); // Add the container to the grid
        }
    }
    
    
}


















