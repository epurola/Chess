package com.example;

import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.animation.FadeTransition;
import javafx.collections.ObservableList;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

 
    public class SinglePlayerController {
        private SoundManager soundManager;
        private Piece selectedPiece;
        private ImageView draggedPiece;
        private Pawn pawnToPromote;
        private Label statusLabel;
        private boolean drawPossibleMoves;
        private ToggleSwitch toggleSwitch;
        private Game game;
    

        Color lightColor = Color.web("#E8EDF9"); 
        Color darkColor = Color.web("#B7C0D8"); 
        Color moveHelpColor = Color.web("#7B61FF");
        
        // UI Components
        @FXML private StackPane rootPane;
        @FXML private GridPane chessBoard;
        @FXML private BorderPane borderPane;
        @FXML private Button exitButton;
        @FXML private Button exitButton1;
        @FXML private Button fullScreenButton;
        @FXML private Button resetButton;
        @FXML private Button undoButton;
        @FXML private HBox hbox1;
    
        @FXML
        public void initialize()  {
            soundManager = new SoundManager();
            game = new Game();
            drawPossibleMoves = false;
            drawBoard();
    
            // Initialize status label
            statusLabel = new Label();
            statusLabel.setTextFill(Color.BLACK);
            statusLabel.setVisible(false);
            rootPane.getChildren().add(statusLabel);
            StackPane.setAlignment(statusLabel, javafx.geometry.Pos.CENTER);
            statusLabel.getStyleClass().add("status-label");
    
            // Initialize promotionComboBox
        
            // Initialize ToggleSwitch
            toggleSwitch = new ToggleSwitch();
            hbox1.getChildren().add(toggleSwitch);
            toggleSwitch.switchedOn().addListener((obs, oldState, newState) -> drawPossibleMoves = !drawPossibleMoves);
    
            chessBoard.setPrefSize(800, 800);
            StackPane.setAlignment(chessBoard, javafx.geometry.Pos.CENTER);
            rootPane.setPrefSize(800, 800);
        }

        @FXML
        private void handleFullScreen() {
            Stage stage = (Stage) borderPane.getScene().getWindow();
            stage.setFullScreen(!stage.isFullScreen());
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
    
        @FXML
        private void Undo() {
            game.undoLastMove();
            drawBoard();
        }
    
        private void drawBoard() {
            chessBoard.getChildren().clear();
            for (int i = 0; i < 8; i++) {
                for (int j = 0; j < 8; j++) {
                    Rectangle square = new Rectangle(100, 100);
                    square.setFill((i + j) % 2 == 0 ? lightColor : darkColor);
                    chessBoard.add(square, j, i);
                    Piece piece = game.getPiece(i, j);
                    if (piece != null) {
                        Image pieceImage = getPieceImage(piece);
                        ImageView pieceView = new ImageView(pieceImage);
                        pieceView.setFitHeight(100);
                        pieceView.setFitWidth(100);
                        chessBoard.add(pieceView, j, i);
                        pieceView.setMouseTransparent(!game.isWhiteTurn() == piece.isWhite());
                        pieceView.setOnMousePressed(event -> handlePieceDragStart(event, pieceView, piece));
                        pieceView.setOnMouseDragged(this::handlePieceDrag);
                        pieceView.setOnMouseReleased(this::handlePieceDrop);
                    }
                }
            }
        }
    
        private Image getPieceImage(Piece piece) {
            String color = piece.isWhite() ? "white-" : "black-";
            String imagePath = "/images/" + color + piece.getClass().getSimpleName().toLowerCase() + ".png";
            try {
                return new Image(getClass().getResourceAsStream(imagePath));
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
    
        private void handlePieceDragStart(MouseEvent event, ImageView pieceView, Piece piece) {
            selectedPiece = piece;
            draggedPiece = pieceView;
            if (drawPossibleMoves) {
                drawPossibleMoves(selectedPiece);
            }
            pieceView.toFront();
        }
    
        private void handlePieceDrag(MouseEvent event) {
            if (draggedPiece != null) {
                draggedPiece.setTranslateX(event.getSceneX() - 50 - draggedPiece.getLayoutX() - chessBoard.localToScene(0, 0).getX());
                draggedPiece.setTranslateY(event.getSceneY() - 50 - draggedPiece.getLayoutY() - chessBoard.localToScene(0, 0).getY());
            }
        }
    //Captured piece null in enpassant
        private void handlePieceDrop(MouseEvent event) {
            if (draggedPiece != null) {
                int row = (int) ((event.getSceneY() - chessBoard.localToScene(0, 0).getY()) / 100);
                int col = (int) ((event.getSceneX() - chessBoard.localToScene(0, 0).getX()) / 100);
                int pawnStartCol = selectedPiece.getCol();
                if (row >= 0 && row < 8 && col >= 0 && col < 8) 
                {
                Piece capturedPiece = game.getPiece(row, col);
                boolean validMove = game.makeMove(selectedPiece.getRow(), selectedPiece.getCol(), row, col);
                if (!validMove)
                {
                  soundManager.playNotifySound();
                }
               
                if (validMove) {
                    if (selectedPiece instanceof Pawn && (row == 0 || row == 7)) {
                        pawnToPromote = (Pawn) selectedPiece;
                        promotePawn(row, col);
                    }
                    if(capturedPiece != null || pawnStartCol != col )
                    {
                        soundManager.playCaptureSound();
                    }else{
                        soundManager.playMoveSound();
                    }
                    }
        
                } else{
                    soundManager.playNotifySound();
                }
            }
                drawBoard();
                if(game.checkMate(game))
                {
                     displayConfetti(chessBoard);
                     soundManager.playWinSound();
                }
                selectedPiece = null;
                draggedPiece = null;
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
        
                switch (choice) {
                    case "/images/white-rook.png":
                        newPiece = new Rook(row, col, pawnToPromote.isWhite());
                        break;
                    case "/images/white-bishop.png":
                        newPiece = new Bishop(row, col, pawnToPromote.isWhite());
                        break;
                    case "/images/white-knight.png":
                        newPiece = new Knight(row, col, pawnToPromote.isWhite());
                        break;
                    case "/images/white-queen.png":
                      newPiece = new Queen(row, col, pawnToPromote.isWhite());
                        break;
                    case "/images/black-rook.png":
                        newPiece = new Rook(row, col, pawnToPromote.isWhite());
                        break;
                    case "/images/black-bishop.png":
                        newPiece = new Bishop(row, col, pawnToPromote.isWhite());
                        break;
                    case "/images/black-knight.png":
                        newPiece = new Knight(row, col, pawnToPromote.isWhite());
                        break;
                    case "/images/black-queen.png":
                        newPiece = new Queen(row, col, pawnToPromote.isWhite());
                        break;
                    default:
                        newPiece = new Queen(row, col, pawnToPromote.isWhite());
                }
                soundManager.playButtonSound();
                game.setPiece(row, col, newPiece);
                System.out.println("Pawn promoted to " + choice + ".");
                pawnToPromote = null; // Reset pawnToPromote after promotion
                drawBoard();  // Redraw the board to update the piece's position
                exitButton.setMouseTransparent(false); 
                exitButton1.setMouseTransparent(false);
                fullScreenButton.setMouseTransparent(false);
                resetButton.setMouseTransparent(false);
                undoButton.setMouseTransparent(false);
                
            });
            promotionMenu.setLayoutX(row);
            promotionMenu.setLayoutY(col);
            rootPane.getChildren().add(promotionMenu);
            exitButton.setMouseTransparent(true); 
            exitButton1.setMouseTransparent(true);
            fullScreenButton.setMouseTransparent(true);
            resetButton.setMouseTransparent(true);
            undoButton.setMouseTransparent(true); 
        }
        private String getPieceNameFromImagePath(String imagePath) {
            if (imagePath.contains("rook")) return "rook";
            if (imagePath.contains("bishop")) return "bishop";
            if (imagePath.contains("knight")) return "knight";
            if (imagePath.contains("queen")) return "queen";
            return null;
        }
    
    
        @FXML
        private void handleBackButton() {
            try {
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
                FadeTransition fadeOut = new FadeTransition(Duration.millis(300), oldRoot);
                fadeOut.setFromValue(1.0);
                fadeOut.setToValue(0.0);
                FadeTransition fadeIn = new FadeTransition(Duration.millis(300), newRoot);
                fadeIn.setFromValue(0.0);
                fadeIn.setToValue(1.0);
                fadeOut.setOnFinished(event -> {
                    scene.setRoot(newRoot);
                    fadeIn.play();
                });
                fadeOut.play();
            } else {
                scene.setRoot(newRoot);
                newRoot.setOpacity(0.0);
                FadeTransition fadeIn = new FadeTransition(Duration.millis(300), newRoot);
                fadeIn.setFromValue(0.0);
                fadeIn.setToValue(1.0);
                fadeIn.play();
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
        private void displayConfetti(Pane pane) {
            double paneWidth = pane.getWidth();
            double paneHeight = pane.getHeight();
        
            for (int i = 0; i < 100; i++) { // Number of confetti pieces
                Confetti confetti = new Confetti(Color.hsb(Math.random() * 360, 1.0, 1.0), paneWidth, paneHeight);
                pane.getChildren().add(confetti);
                confetti.animate();
            }
        }

       
    }
   
   
    
   
    




















