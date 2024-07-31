package com.example;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javafx.animation.FadeTransition;
import javafx.animation.FillTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.TranslateTransition;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.fxml.FXML;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.util.Duration;

public class PrimaryController {

    @FXML
    private GridPane chessBoard;
    
    @FXML
    private StackPane rootPane;
    @FXML
    private BorderPane borderPane;
   
    
    private Label statusLabel;

    private Game game;
    private Piece selectedPiece;
    private ImageView draggedPiece;
    @FXML
    private Button button;
    Color lightColor = Color.web("#E8EDF9"); // Custom light color (e.g., beige)
    Color darkColor = Color.web("#B7C0D8"); 
    Color moveHelpColor = Color.web("#7B61FF");
    private boolean drawPossibleMoves;
    @FXML
        VBox vbox ; 
    @FXML
    HBox hbox1;
  
    @FXML
    public void initialize() {
        game = new Game();
        drawBoard();
        drawPossibleMoves = true;
        if (statusLabel == null) {
            statusLabel = new Label();
            statusLabel.setTextFill(Color.BLACK); 
            statusLabel.setVisible(false);
            rootPane.getChildren().add(statusLabel);
            StackPane.setAlignment(statusLabel, javafx.geometry.Pos.CENTER);
    
            // Apply CSS class
            statusLabel.getStyleClass().add("status-label");
        }
        // Assuming you already have this

        toggleSwitch toggleSwitch = new toggleSwitch();
    
        hbox1.getChildren().add(toggleSwitch);

        toggleSwitch.swichedOn().addListener((obs, oldState, newState) -> {
            drawPossibleMoves = !drawPossibleMoves;
        });

        
        
        

        // Set preferred size for the board
        chessBoard.setPrefSize(800, 800);

        // Center the chessBoard within the rootPane
        StackPane.setAlignment(chessBoard, javafx.geometry.Pos.CENTER);

        // Optionally: set the rootPane size to be 100% of the Scene
        rootPane.setPrefSize(800, 800);
    }
    @FXML
    private void handleFullScreen() {
        Stage stage = (Stage) borderPane.getScene().getWindow();
        Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();

        if (stage.isFullScreen()) {
            stage.setFullScreen(false);
        } else {
            stage.setFullScreen(true);
        }
    }

    @FXML
    private void handleExit() {
        Stage stage = (Stage) borderPane.getScene().getWindow();
        stage.close();
    }
    @FXML
    private void handleReset() {

        if (game != null) {
            // Example: Remove event handlers from pieces
            chessBoard.getChildren().forEach(node -> {
                if (node instanceof ImageView) {
                    ImageView pieceView = (ImageView) node;
                    pieceView.setOnMousePressed(null);
                    pieceView.setOnMouseDragged(null);
                    pieceView.setOnMouseReleased(null);
                }
            });
        }
        // Reset the game instance
        game = new Game();
        
        // Clear the board and draw the new board
        drawBoard();
        
        // Clear status messages
        if (statusLabel != null) {
            statusLabel.setText("");
            statusLabel.setVisible(false);
        }
    }

    private void drawBoard() {
        chessBoard.getChildren().clear(); // Clear existing children to reset the board

        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                Rectangle square = new Rectangle(100, 100);
                if ((i + j) % 2 == 0) {
                    square.setFill(lightColor);
                } else {
                    square.setFill(darkColor);
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
        if(drawPossibleMoves)
        {
            drawPossibleMoves(selectedPiece);
        }
        
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
                        game.recordMove(originalRow, originalCol, row, col, capturedPiece);
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
                    statusLabel.setVisible(true);
                }
                if(game.checkDraw(game))
                {
                    System.out.println("Draw!");
                    statusLabel.setText("Draw");
                    statusLabel.setVisible(true);
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
        chessBoard.getChildren().removeIf(node -> node instanceof StackPane);
    
        double squareSize = 100; // Size of each square on the board
        double indicatorSize = squareSize * 0.3; // Diameter of the indicator, e.g., 30% of square size
        
        possibleMoves = selectedPiece.getLegalMovesWithoutCheck(game);
       
        
        for (int[] move : possibleMoves) {
            int row = move[0];
            int col = move[1];
    
            // Check if the square is occupied
            boolean isOccupied = game.getPiece(row, col) != null; // Use your board's method to check for occupation
    
            // Create and configure the move indicator
            Circle moveIndicator = new Circle(indicatorSize / 2);
            if (isOccupied) {
                moveIndicator.setFill(null); // No fill for occupied squares
                moveIndicator.setStroke(moveHelpColor.deriveColor(0, 1, 1, 0.5)); // Set stroke color with transparency (50% opacity)
                moveIndicator.setStrokeWidth(4); // Adjust the stroke width if needed
                moveIndicator.setRadius(40);
            } else {
                moveIndicator.setFill(moveHelpColor.deriveColor(0, 1, 1, 0.5)); // Set fill color with transparency (50% opacity)
            }
    
            // Use a StackPane to center the Circle
            StackPane moveIndicatorContainer = new StackPane();
            moveIndicatorContainer.setPickOnBounds(false);
            moveIndicatorContainer.getChildren().add(moveIndicator);
            moveIndicatorContainer.setPrefSize(squareSize, squareSize); // Ensure the container matches the square size
    
            moveIndicatorContainer.setMouseTransparent(true);
    
            chessBoard.add(moveIndicatorContainer, col, row); // Add the container to the grid
        }
    }
    
    @FXML
    private void handleBackButton() {
        try {
            // Call the method to switch to the secondary screen with animation
            setRootWithTransition("secondary");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setRootWithTransition(String fxml) throws IOException {
        Parent newRoot = App.loadFXML(fxml);
        Scene scene = borderPane.getScene();
    
        if (scene != null) {
            Parent oldRoot = scene.getRoot();
    
            // Apply fade-out transition to the old root
            FadeTransition fadeOut = new FadeTransition(Duration.millis(300), oldRoot);
            fadeOut.setFromValue(1.0);
            fadeOut.setToValue(0.0);
    
            // Apply fade-in transition to the new root
            FadeTransition fadeIn = new FadeTransition(Duration.millis(300), newRoot);
            fadeIn.setFromValue(0.0);
            fadeIn.setToValue(1.0);
    
            // Chain the transitions
            fadeOut.setOnFinished(event -> {
                scene.setRoot(newRoot);
                fadeIn.play();
            });
    
            fadeOut.play();
        } else {
            // If the scene is not yet initialized, just set the root directly
            scene.setRoot(newRoot);
    
            // Apply fade-in transition to the initial scene
            newRoot.setOpacity(0.0);
            FadeTransition fadeIn = new FadeTransition(Duration.millis(300), newRoot);
            fadeIn.setFromValue(0.0);
            fadeIn.setToValue(1.0);
            fadeIn.play();
        }
    }


    private static class toggleSwitch extends Parent{
        private BooleanProperty switchedOn = new SimpleBooleanProperty(false);
        private TranslateTransition transition = new TranslateTransition(Duration.seconds(0.25));
        private FillTransition FillBackground = new FillTransition(Duration.seconds(0.25));
        Color moveHelpColor = Color.web("#7B61FF");

        private ParallelTransition animation = new ParallelTransition(transition,FillBackground);

        public BooleanProperty swichedOn(){
            return switchedOn;
        }
        public toggleSwitch(){
            Rectangle background = new Rectangle(50,25);
            background.setArcHeight(25);
            background.setArcWidth(25);
            background.setFill(Color.WHITE);
            background.setStroke(Color.LIGHTGRAY);

            Circle toggle = new Circle(12.5);
            toggle.setCenterX(12.5);
            toggle.setCenterY(12.5);
            toggle.setFill(Color.WHITE);
            toggle.setStroke(Color.LIGHTGRAY);

            transition.setNode(toggle);
            FillBackground.setShape(background);

            getChildren().addAll(background,toggle);
            switchedOn.addListener((obs,oldState, newState) ->{
                boolean isOn = newState.booleanValue();
                transition.setToX(isOn ? 50 -25 : 0);
                FillBackground.setFromValue(isOn ? Color.WHITE : moveHelpColor);
                FillBackground.setToValue(isOn ? moveHelpColor : Color.WHITE);
                animation.play();

            });

            setOnMouseClicked(event ->{
                switchedOn.set(!switchedOn.get());
            });


        }
    }
    
    
}


















