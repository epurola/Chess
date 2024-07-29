package com.example;

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
    private StackPane rootPane; // Make sure this is defined in your FXML

    private Board board;
    private Piece selectedPiece;
    private ImageView draggedPiece;

    @FXML
    public void initialize() {
        board = new Board();
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

                        pieceView.setOnMousePressed(event -> handlePieceDragStart(event, pieceView, piece));
                        pieceView.setOnMouseDragged(this::handlePieceDrag);
                        pieceView.setOnMouseReleased(this::handlePieceDrop);
                    }
                }

                square.setOnMouseClicked(this::handleSquareClick);
            }
        }

        if (selectedPiece != null) {
            drawPossibleMoves(selectedPiece);
        }
    }

    private void handlePieceDragStart(MouseEvent event, ImageView pieceView, Piece piece) {
        selectedPiece = piece;
        draggedPiece = pieceView;
        pieceView.toFront();
    }

    private void handlePieceDrag(MouseEvent event) {
        if (draggedPiece != null) {
            draggedPiece.setTranslateX(event.getSceneX() - 50 - draggedPiece.getLayoutX());
            draggedPiece.setTranslateY(event.getSceneY() - 50 - draggedPiece.getLayoutY());
        }
    }

    private void handlePieceDrop(MouseEvent event) {
        if (draggedPiece != null) {
            int row = (int) (event.getSceneY() / 100);
            int col = (int) (event.getSceneX() / 100);

            if (row >= 0 && row < 8 && col >= 0 && col < 8) {
                // Check if the move is valid
                List<int[]> possibleMoves = selectedPiece.getPossibleMoves(board);
                boolean validMove = possibleMoves.stream().anyMatch(move -> move[0] == row && move[1] == col);

                if (validMove) {
                    board.setPiece(row, col, selectedPiece);
                    board.setPiece(selectedPiece.getRow(), selectedPiece.getCol(), null);
                    selectedPiece.setPosition(row, col);
                }

                drawBoard(); // Redraw the board to update the piece's position
            }

            draggedPiece = null;
            selectedPiece = null;
        }
    }

    @FXML
    private void handleSquareClick(MouseEvent event) {
        // No need to handle square clicks separately now since dragging handles the moves
    }

    private void drawPossibleMoves(Piece selectedPiece) {
        List<int[]> possibleMoves = selectedPiece.getPossibleMoves(board);

        // Clear existing move indicators
        chessBoard.getChildren().removeIf(node -> node instanceof Circle);

        double squareSize = 100; // Size of each square on the board
        double indicatorSize = squareSize * 0.3; // Diameter of the indicator, e.g., 30% of square size

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








