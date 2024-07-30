package com.example;

import java.util.ArrayList;
import java.util.List;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
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
    
    private Label statusLabel;

    private Game game;
    private Piece selectedPiece;
    private ImageView draggedPiece;
    @FXML
    private Button button;
  
    @FXML
    public void initialize() {
        game = new Game();
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

                Piece piece = game.getPiece(i, j);
                if (piece != null) {
                    String color = piece.isWhite() ? "white-" : "black-";
                    String imagePath = "/images/" + color + piece.getClass().getSimpleName().toLowerCase() + ".png";
                    //System.out.println("Attempting to load image: " + imagePath);
                    Image pieceImage = null;
                    try {
                        pieceImage = new Image(getClass().getResourceAsStream(imagePath));
                        if (pieceImage.isError()) {
                           // System.out.println("Error loading image: " + pieceImage.getException());
                        }
                    } catch (Exception e) {
                        //System.out.println("Exception loading image: " + e.getMessage());
                    }

                    if (pieceImage != null) {
                        ImageView pieceView = new ImageView(pieceImage);
                        pieceView.setFitHeight(100);
                        pieceView.setFitWidth(100);
                        chessBoard.add(pieceView, j, i);

                        if (game.isWhiteTurn()) {
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
    @FXML
    private void Undo(){
        game.undoLastMove();
        drawBoard();
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
                List<int[]> possibleMoves = selectedPiece.getPossibleMoves(game.getBoard());
                boolean validMove = possibleMoves.stream().anyMatch(move -> move[0] == row && move[1] == col);
    
                if (validMove) {
                    // Get the piece that will be captured, if any
                    Piece capturedPiece = game.getPiece(row, col);
    
                    // Temporarily make the move
                    Piece pieceToMove = selectedPiece.copy(); // Ensure a copy of the piece is used
                    game.setPiece(row, col, pieceToMove);
                    game.setPiece(originalRow, originalCol, null);
                    pieceToMove.setPosition(row, col);
                    game.recordMove(originalRow, originalCol, row, col, capturedPiece);
    
                    // Check if the king is in check
                    if (game.isInCheck(game.isWhiteTurn())) {
                        // Revert the move if it puts the king in check
                        game.setPiece(originalRow, originalCol, pieceToMove);
                        game.setPiece(row, col, capturedPiece); // Restore the captured piece
                        pieceToMove.setPosition(originalRow, originalCol); // Restore the piece's position
                        System.out.println("Move puts king in check. Move reverted.");
                    } else {
                        // Valid move; switch turns if move is valid and does not put the king in check
                        if (originalRow != row || originalCol != col) {
                            game.setWhiteTurn(!game.isWhiteTurn());
                        }
                    }
                }
                if(game.checkMate(game))
                {
                    System.out.println("Checkmate!");
                    statusLabel.setText("Checkmate!");
                }
                if(game.checkDraw(game))
                {
                    System.out.println("Draw!");
                    statusLabel.setText("Draw");
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
    
        possibleMoves=selectedPiece.getLegalMovesWithoutCheck(game);
        
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


















