package com.example;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import javafx.animation.FadeTransition;
import javafx.animation.FillTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.TranslateTransition;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.util.Duration;

public class PrimaryController {

    @FXML
    private GridPane chessBoard;
    @FXML
    private StackPane rootPane;
    @FXML
    private AnchorPane pane;
    @FXML
    private BorderPane borderPane;
    private Label statusLabel;
    private Game game;
    private Piece selectedPiece;
    private ImageView draggedPiece;
    @FXML
    private Button button;
    Color lightColor = Color.web("#E8EDF9"); 
    Color darkColor = Color.web("#B7C0D8"); 
    Color moveHelpColor = Color.web("#7B61FF");
    private boolean drawPossibleMoves;
    @FXML
    VBox vbox ; 
    @FXML
    HBox hbox1;
    @FXML
    private Button exitButton;
    @FXML
    private Button undoButton;
    @FXML
    private Button fullScreenButton;
    @FXML
    private Button resetButton;
    @FXML
    private Button exitButton1;
    private static SoundManager soundManager;
    @FXML
    private ComboBox<String> promotionComboBox;
    private ObservableList<String> promotionChoise;
    @FXML
    private Pawn pawnToPromote;
    private int promoteRow, promoteCol;
    double sceneY;
    double sceneX;
    String promotionChoice;
  
    @FXML
    public void initialize() {
        game = new Game();
        soundManager= new SoundManager();
        drawBoard();
        

        drawPossibleMoves = true;

        if (statusLabel == null) {
            statusLabel = new Label();
            statusLabel.setTextFill(Color.BLACK); 
            statusLabel.setVisible(false);
            rootPane.getChildren().add(statusLabel);
            StackPane.setAlignment(statusLabel, javafx.geometry.Pos.CENTER);
            statusLabel.getStyleClass().add("status-label");
        }

      
        promotionComboBox.setItems(promotionChoise);
        promotionComboBox.setOnAction(event -> promotePawn(promoteRow,promoteCol));
        promotionComboBox.setVisible(false);
        
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
            chessBoard.getChildren().forEach(node -> {
                if (node instanceof ImageView) {
                    ImageView pieceView = (ImageView) node;
                    pieceView.setOnMousePressed(null);
                    pieceView.setOnMouseDragged(null);
                    pieceView.setOnMouseReleased(null);
                }
            });
        }
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
            int row = (int) ((event.getSceneY() - chessBoard.localToScene(0,0).getY()) / 100);
            int col = (int) ((event.getSceneX() - chessBoard.localToScene(0,0).getX()) / 100);
            int originalRow = selectedPiece.getRow();
            int originalCol = selectedPiece.getCol();
            List<int[]> possibleMoves = new ArrayList<>();
            Piece capturedPiece = null;
    
            if (row >= 0 && row < 8 && col >= 0 && col < 8) {
                // Check if the move is valid
                if (selectedPiece instanceof Pawn) {
                    if (game.isEnPassant(originalRow, originalCol) && col != originalCol) {
                        possibleMoves = ((Pawn) selectedPiece).getPossibleMoveswithEnPassant(game.getBoard(), game);
                        
                        capturedPiece = game.getPiece(game.getLastMove().getToRow() , game.getLastMove().getToCol()); 

                        if(capturedPiece != null && capturedPiece.isWhite() == selectedPiece.isWhite())
                        {
                            capturedPiece = null;
                        }
                    } else {
                        possibleMoves = selectedPiece.getLegalMovesWithoutCheck(game);
                        capturedPiece = game.getPiece(row, col);
                    }
                } else {
                    possibleMoves = selectedPiece.getLegalMovesWithoutCheck(game);
                    capturedPiece = game.getPiece(row, col);
                }
    
                boolean validMove = possibleMoves.stream().anyMatch(move -> move[0] == row && move[1] == col);
    
                if (!validMove) {
                    soundManager.playNotifySound();
                }
    
                if (validMove) {
                    if (capturedPiece != null) {
                        soundManager.playCaptureSound(); // Play capture sound
                    } else {
                        soundManager.playMoveSound(); // Play move sound
                    }
    
                    // Temporarily make the move
                    Piece pieceToMove = selectedPiece.copy();
    
                    // Handle en passant
                    if (capturedPiece != null) {
                        game.setPiece(capturedPiece.getRow(), capturedPiece.getCol(), null); // Remove the captured pawn from the board
                    }
    
                    game.setPiece(row, col, pieceToMove);
                    game.setPiece(originalRow, originalCol, null);
                    pieceToMove.setPosition(row, col);
    
                    if (pieceToMove instanceof Pawn && (row == 0 || row == 7)) {
                        pawnToPromote = (Pawn) pieceToMove;
                        promotePawn(row, col);
                    }
                    if(pieceToMove instanceof King)
                    {
                        game.updateKingPositions();
                    }
                   
                    game.recordMove(originalRow, originalCol, row, col, capturedPiece, pieceToMove,
                    capturedPiece != null ? capturedPiece.getRow() : 1, 
                    capturedPiece != null ? capturedPiece.getCol() : 1);
    
                    // Check if the king is in check
                    if (game.isInCheck(game.isWhiteTurn())) {
                        soundManager.playNotifySound();
                        // Revert the move if it puts the king in check
                        game.setPiece(originalRow, originalCol, pieceToMove);
                        game.setPiece(row, col, capturedPiece); // Restore the captured piece
                        pieceToMove.setPosition(originalRow, originalCol); // Restore the piece's position
                        if (capturedPiece != null) {
                            capturedPiece.setPosition(row, col);
                        }
                        game.popMoveStack();
                        System.out.println("Move puts king in check. Move reverted.");
                    } else {
                        // Valid move; switch turns if move is valid and does not put the king in check
                        if (originalRow != row || originalCol != col) {
                            game.setWhiteTurn(!game.isWhiteTurn());
                        }
                    }
                }
    
                if (game.checkMate(game)) {
                    System.out.println("Checkmate!");
                    statusLabel.setText("Checkmate!");
                    statusLabel.setVisible(true);
                    soundManager.playWinSound();
                    displayConfetti(rootPane);
                }
    
                if (game.checkDraw(game)) {
                    System.out.println("Draw!");
                    statusLabel.setText("Draw");
                    statusLabel.setVisible(true);
                }
                    drawBoard(); // Redraw the board only if the move is valid
               
            }
    
            selectedPiece = null;
            draggedPiece = null;
        }
    }
    
    private void promotePawn(int row, int col) {
        
        List<String> promotionImagePaths = List.of(
            "/images/white-rook.png",
            "/images/white-bishop.png",
            "/images/white-knight.png",
            "/images/white-queen.png"
        );
    
    
        if (selectedPiece.isWhite()) {
            promotionImagePaths = List.of(
                "/images/white-rook.png",
                "/images/white-bishop.png",
                "/images/white-knight.png",
                "/images/white-queen.png"
            );
        } else {
            promotionImagePaths = List.of(
                "/images/black-rook.png",
                "/images/black-bishop.png",
                "/images/black-knight.png",
                "/images/black-queen.png"
            );
        }
        


        
        // Instantiate the ChoiseMenu with the promotion options and a callback
        ChoiseMenu promotionMenu = new ChoiseMenu("Choose Promotion", promotionImagePaths, choice -> {
            // Handle the user's choice
            Piece newPiece;
            promoteRow = row;
            promoteCol = col;
    
            switch (choice) {
                case "/images/white-rook.png":
                    newPiece = new Rook(promoteRow, promoteCol, pawnToPromote.isWhite());
                    break;
                case "/images/white-bishop.png":
                    newPiece = new Bishop(promoteRow, promoteCol, pawnToPromote.isWhite());
                    break;
                case "/images/white-knight.png":
                    newPiece = new Knight(promoteRow, promoteCol, pawnToPromote.isWhite());
                    break;
                case "/images/white-queen.png":
                  newPiece = new Queen(promoteRow, promoteCol, pawnToPromote.isWhite());
                    break;
                case "/images/black-rook.png":
                    newPiece = new Rook(promoteRow, promoteCol, pawnToPromote.isWhite());
                    break;
                case "/images/black-bishop.png":
                    newPiece = new Bishop(promoteRow, promoteCol, pawnToPromote.isWhite());
                    break;
                case "/images/black-knight.png":
                    newPiece = new Knight(promoteRow, promoteCol, pawnToPromote.isWhite());
                    break;
                case "/images/black-queen.png":
                    newPiece = new Queen(promoteRow, promoteCol, pawnToPromote.isWhite());
                    break;
                default:
                    newPiece = new Queen(promoteRow, promoteCol, pawnToPromote.isWhite());
            }
            soundManager.playButtonSound();
            game.setPiece(promoteRow, promoteCol, newPiece);
            System.out.println("Pawn promoted to " + choice + ".");
            pawnToPromote = null; // Reset pawnToPromote after promotion
            drawBoard(); // Redraw the board to update the piece's position
            exitButton.setMouseTransparent(false); 
            exitButton1.setMouseTransparent(false);
            fullScreenButton.setMouseTransparent(false);
            resetButton.setMouseTransparent(false);
            undoButton.setMouseTransparent(false);
            
        });
    
        promotionMenu.setLayoutX(calculateXForRow(row));
        promotionMenu.setLayoutY(calculateYForCol(col));
        rootPane.getChildren().add(promotionMenu);
        exitButton.setMouseTransparent(true); 
        exitButton1.setMouseTransparent(true);
        fullScreenButton.setMouseTransparent(true);
        resetButton.setMouseTransparent(true);
        undoButton.setMouseTransparent(true);
        
        
    }
   

    // Helper methods to calculate positions
    private double calculateXForRow(int row) {
        // Implement your logic to convert the row to X position
        return row  ; // Placeholder
    }
    
    private double calculateYForCol(int col) {
        // Implement your logic to convert the column to Y position
        return col ; // Placeholder
    }
    

    private void displayConfetti(Pane pane) {
        double paneWidth = pane.getWidth();
        double paneHeight = pane.getHeight();
    
        for (int i = 0; i < 100; i++) { // Number of confetti pieces
            Confetti confetti = new Confetti(Color.hsb(Math.random() * 360, 1.0, 1.0), paneWidth, paneHeight);
            pane.getChildren().add(confetti);
            confetti.animate();
        }
    }
    
    private void drawPossibleMoves(Piece selectedPiece) {
        List<int[]> possibleMoves = new ArrayList<>();
    
        // Clear existing move indicators
        chessBoard.getChildren().removeIf(node -> node instanceof StackPane);
    
        double squareSize = 100; // Size of each square on the board
        double indicatorSize = squareSize * 0.3; // Diameter of the indicator, e.g., 30% of square size
      
        if (selectedPiece instanceof Pawn) {
            // Cast to Pawn and get possible moves including en passant
            possibleMoves = ((Pawn) selectedPiece).getPossibleMoveswithEnPassant(game.getBoard(), game);
        } else {
            // For other pieces, use the standard method
            possibleMoves = selectedPiece.getLegalMovesWithoutCheck(game);
        }
    
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
                soundManager.playButtonSound();
            });


        }
    }
    public class ChoiseMenu extends Parent {
        private BooleanProperty visible = new SimpleBooleanProperty(false);
        private Consumer<String> onChoiceSelected; // Callback for when a choice is selected
        private Color purple = Color.web("#7B61FF");
    
        @SuppressWarnings("exports")
        public BooleanProperty VisibleProperty() {
            return visible;
        }
    
        public boolean IsVisible() {
            return visible.get();
        }
    
        public void SetVisible(boolean visible) {
            this.visible.set(visible);
        }
    
        // Constructor with a callback for choice selection
        public ChoiseMenu(String header, List<String> imagePaths, Consumer<String> onChoiceSelected) {
            this.onChoiceSelected = onChoiceSelected; // Set the callback
    
            // Create the background rectangle
            Rectangle background = new Rectangle(400, 100);
            background.setArcHeight(15);
            background.setArcWidth(15);
            background.setFill(Color.WHITE);
            background.setStroke(Color.LIGHTGRAY);
    
            // Create an HBox to hold the images
            HBox itemsContainer = new HBox(); // Spacing between images
            itemsContainer.setPrefWidth(background.getWidth());
            itemsContainer.setPrefHeight(background.getHeight());

    
            // Add images to the HBox
            for (String imagePath : imagePaths) {
                // Load the image using the imagePath from the list
                Image image = new Image(getClass().getResourceAsStream(imagePath));
    
                // Create an ImageView for the loaded image
                ImageView imageView = new ImageView(image);
                imageView.setFitHeight(100); 
                imageView.setFitWidth(100);
                
                
                // Create a DropShadow effect
                
    
                // Set up event handlers
                imageView.setOnMouseClicked(e -> handleChoice(imagePath)); // Handle choice when image is clicked
                imageView.setOnMouseEntered(e -> {
                    imageView.setStyle("-fx-border-color: #7B61FF; -fx-border-width: 3;");
                    imageView.setOpacity(0.5); // Change cursor to hand on hover
                });
    
                imageView.setOnMouseExited(e -> {
                    imageView.setEffect(null); // Remove shadow effect
                    imageView.setStyle("-fx-border-color: transparent; -fx-border-width: 3;");
                    imageView.setOpacity(1); // Revert cursor on exit
                });
    
                // Add the ImageView to the HBox
                itemsContainer.getChildren().add(imageView);
            }
    
            // Add background and items container to the parent
            getChildren().addAll(background, itemsContainer);
    
            // Set initial visibility
            updateVisibility();
    
            // Add listener to the visibility property
            visible.addListener((obs, oldState, newState) -> updateVisibility());
        }
    
        // Method to handle the choice selection
        private void handleChoice(String imagePath) {
            if (onChoiceSelected != null) {
                onChoiceSelected.accept(imagePath); // Invoke the callback with the selected image path
            }
            setVisible(false); // Hide the menu after a choice is made
        }
    
        // Method to update visibility based on the visibility property
        private void updateVisibility() {
            setManaged(isVisible()); // Ensure layout is managed based on visibility
            setVisible(isVisible()); // Update actual visibility
            // You can also use other visibility management logic if needed
        }
    }
    
}


















