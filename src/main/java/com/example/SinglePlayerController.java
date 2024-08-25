package com.example;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.util.Duration;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;


    public class SinglePlayerController {
       
        private Piece selectedPiece;
        private ImageView draggedPiece;
        private Pawn pawnToPromote;
        private Label statusLabel;
        private boolean drawPossibleMoves;
        private ToggleSwitch toggleSwitch;
        private Game game;
        private Label whiteScore;
        private Label blackScore;
        private int bScore;
        private int wScore;
        private String fen;
    

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
        @FXML private HBox hbox11;
        @FXML private VBox vbox2;
        @FXML private VBox vbox;
        @FXML private HBox timer1;
        @FXML private HBox timer2;
        @FXML private HBox WhiteHbox;
        @FXML private HBox blackHbox;
        @FXML private AnchorPane pane;

        CountdownClock countdownClock;
        CountdownClock countdownClock2;
        double screenWidth;
        double screenHeight;
        Stockfish stockfish;
        boolean drawBestMove;
        boolean analysis;
      
    
        @FXML
        public void initialize() throws IOException  {
           
            game = new Game();
            stockfish = new Stockfish(game);
            drawPossibleMoves = false;
            whiteScore = new Label();
            blackScore = new Label();
            whiteScore.setText("0");
            blackScore.setText("0");
            whiteScore.setVisible(false);
            blackScore.setVisible(false);
            blackScore.setStyle("-fx-font-size: 13px;");
            whiteScore.setStyle("-fx-font-size: 13px;");
        
        
            // Initialize status label
            statusLabel = new Label();
            statusLabel.setTextFill(Color.BLACK);
            statusLabel.setVisible(false);
            rootPane.getChildren().add(statusLabel);
            StackPane.setAlignment(statusLabel, javafx.geometry.Pos.CENTER);
            statusLabel.getStyleClass().add("status-label");

            // Initialize ToggleSwitch
            toggleSwitch = new ToggleSwitch();
            hbox1.getChildren().add(toggleSwitch);
            toggleSwitch.switchedOn().addListener((obs, oldState, newState) -> drawPossibleMoves = !drawPossibleMoves);
            toggleSwitch = new ToggleSwitch();
            hbox11.getChildren().add(toggleSwitch);
            toggleSwitch.switchedOn().addListener((obs, oldState, newState) -> drawBestMove = !drawBestMove);
          
            countdownClock = new CountdownClock(this); 
            countdownClock2 = new CountdownClock(this); 
            Screen screen = Screen.getPrimary();
            Rectangle2D bounds = screen.getBounds();
             screenWidth = bounds.getWidth();
             screenHeight = bounds.getHeight();

            double chessBoardSize = screenHeight * 0.8 ;
            
            StackPane.setAlignment(chessBoard, Pos.CENTER);
            
            timer1.setAlignment(Pos.CENTER);
            timer1.getChildren().addAll(countdownClock);
         
            timer2.setAlignment(Pos.CENTER);
            timer2.getChildren().addAll(countdownClock2);
        
            pane.setMaxSize(screenHeight, screenWidth);
            borderPane.setMaxSize(screenWidth, screenHeight);
    
            chessBoard.setPrefSize(chessBoardSize, chessBoardSize);
            rootPane.setMinSize(chessBoardSize+30, chessBoardSize+30);
            double boxWidth = (screenWidth - chessBoardSize) / 2;
            hbox1.setPrefWidth(boxWidth );
            vbox2.setPrefWidth(boxWidth);
            vbox2.setMaxHeight(screenHeight);
            vbox2.setSpacing(screenHeight/2);
            double hboxHeight = (screenHeight -chessBoardSize-30) /2;  // Set height as 10% of the screen height
            WhiteHbox.setMinHeight(hboxHeight);
            WhiteHbox.setPrefHeight(hboxHeight);
            WhiteHbox.setMaxHeight(hboxHeight);
            
            blackHbox.setMinHeight(hboxHeight);
            blackHbox.setPrefHeight(hboxHeight);
            blackHbox.setMaxHeight(hboxHeight);
            blackHbox.getChildren().add(whiteScore);
            WhiteHbox.getChildren().addAll(blackScore);
            // Enable fill height for HBoxes to stretch vertically if needed
            WhiteHbox.setFillHeight(true);
            blackHbox.setFillHeight(true);
          /*Image backgroundImage = new Image("file:C:/Users/eelip/Chess/src/main/resources/images/300.png");


        // Create a BackgroundImage
              BackgroundImage background = new BackgroundImage(
                backgroundImage,
                BackgroundRepeat.NO_REPEAT,    // Don't repeat the image
                BackgroundRepeat.NO_REPEAT,    // Don't repeat the image
                BackgroundPosition.CENTER,     // Position the image in the center
                new BackgroundSize(
                    chessBoardSize,
                    chessBoardSize,
                    true, true, true, false
                ) // Use the original size of the image, scale if necessary
        ); chessBoard.setBackground(new Background(background)); */  

        // Set the backgroun
            drawBoard();
        }
    
        @FXML
        private void handleFullScreen() {
            Stage stage = (Stage) borderPane.getScene().getWindow();
            stage.setFullScreen(!stage.isFullScreen());
        }
    
        @FXML
        private void handleExit() {
            Stage stage = (Stage) borderPane.getScene().getWindow();
            countdownClock.stop();
            countdownClock2.stop();
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
                    countdownClock.stop();
                    countdownClock2.stop();
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
            SoundManager.playMoveSound();
            drawBoard();
        }
    
        private void drawBoard() {
            chessBoard.getChildren().clear();
            double squareSize = chessBoard.getPrefWidth() / 8;
            for (int i = 0; i < 8; i++) {
                for (int j = 0; j < 8; j++) {
                    Rectangle square = new Rectangle(squareSize, squareSize);
                  // square.setFill(Color.TRANSPARENT);
                  square.setFill((i + j) % 2 == 0 ? lightColor : darkColor);
                   chessBoard.add(square, j, i);
                    Piece piece = game.getPiece(i, j);
                    if (piece != null) {
                        Image pieceImage = getPieceImage(piece);
                        ImageView pieceView = new ImageView(pieceImage);
                        if(!piece.isWhite())
                        {
                            ColorAdjust colorAdjust = new ColorAdjust();
                            colorAdjust.setContrast(0.3); 
                             // Adjust this value between -1.0 and 1.
                        pieceView.setEffect(colorAdjust);
                        }
                        else
                        {
                            ColorAdjust colorAdjust = new ColorAdjust();
                            colorAdjust.setContrast(0.3);  // Increase contrast (adjust as needed)
                            colorAdjust.setBrightness(-0.2);  // Slightly decrease brightness (adjust as needed)
                            colorAdjust.setSaturation(0);  // Optional: set to 0 for grayscale effect
                            
                        pieceView.setEffect(colorAdjust);
                        }
                    
                        pieceView.setFitHeight(squareSize);
                        pieceView.setFitWidth(squareSize);
                        chessBoard.add(pieceView, j, i);
                        pieceView.setMouseTransparent(!game.isWhiteTurn() == piece.isWhite());
                        pieceView.setOnMousePressed(event -> handlePieceDragStart(event, pieceView, piece));
                        pieceView.setOnMouseDragged(this::handlePieceDrag);
                        pieceView.setOnMouseReleased(event -> {
                            try {
                                handlePieceDrop(event);
                            } catch (InterruptedException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }
                        });
                    }
                }
            }     
        }

 private void drawBestMoveIndicators(int fromRow, int fromCol, int toRow, int toCol) {
    double squareSize = chessBoard.getPrefWidth() / 8;
    double indicatorSize = squareSize * 0.3; // Diameter of the indicator, e.g., 30% of square size
    // Clear existing move indicators
    chessBoard.getChildren().removeIf(node -> node instanceof StackPane);
    // Highlight the best move in gold color
    Circle bestMoveIndicator = new Circle(indicatorSize / 2);
    bestMoveIndicator.setStroke(Color.GOLD.deriveColor(0, 1, 1, 0.9)); // Set stroke color to gold
    bestMoveIndicator.setStrokeWidth(3); // Adjust the stroke width if needed
    bestMoveIndicator.setFill(Color.GOLD.deriveColor(0, 1, 1, 0.9)); // No fill
    StackPane bestMoveContainer = new StackPane();
    bestMoveContainer.setPickOnBounds(false);
    bestMoveContainer.getChildren().add(bestMoveIndicator);
    bestMoveContainer.setPrefSize(squareSize, squareSize); // Ensure the container matches the square size
    bestMoveContainer.setMouseTransparent(true);
        if (toRow>= 0 && toRow < 8 && toCol >= 0 && toCol < 8) {
        chessBoard.add(bestMoveContainer, toCol, toRow); // Add the container to the grid at the destination square
        }
    // Highlight the piece that can make the best move in gold
    Rectangle bestPieceIndicator = new Rectangle(squareSize-4 , squareSize-4); // Full square size
    bestPieceIndicator.setStroke(Color.GOLD.deriveColor(0, 1, 1, 0.9)); // Set stroke color to gold
    bestPieceIndicator.setStrokeWidth(4); // Adjust the stroke width if needed
    bestPieceIndicator.setFill(null); // No fill
    StackPane bestPieceContainer = new StackPane();
    bestPieceContainer.setPickOnBounds(false);
    bestPieceContainer.getChildren().add(bestPieceIndicator);
    bestPieceContainer.setPrefSize(squareSize, squareSize); // Ensure the container matches the square size
    bestPieceContainer.setMouseTransparent(true);
        if (fromRow>= 0 && fromRow < 8 && fromCol >= 0 && fromCol < 8) {
        chessBoard.add(bestPieceContainer, fromCol, fromRow); // Add the container to the grid at the origin square
        }
}
public String rowColToAlgebraic(int row, int col) {
    // Convert column index to column character ('a' to 'h')
    char columnChar = (char) ('a' + col);
    // Convert row index to row character ('1' to '8')
    char rowChar = (char) ('1' + (7 - row)); // Adjust for 0-based indexing
    // Combine column and row characters to form the algebraic notation
    return "" + columnChar + rowChar;
}
private final ExecutorService analysisExecutor = Executors.newSingleThreadExecutor();

private void startGameAnalysis(int fromRow, int fromCol, int toRow, int toCol, String fen) {
    analysisExecutor.submit(() -> {
        try {
            if(drawBestMove)
            {
            int[] bestMove = parseMove(stockfish.getBestMove(game.toFen()));
            Platform.runLater(() -> {
                drawBestMoveIndicators(bestMove[0], bestMove[1], bestMove[2], bestMove[3]);  
            });
        }
            String moveString = rowColToAlgebraic(fromRow, fromCol) + rowColToAlgebraic(toRow, toCol);
            stockfish.analyzeMove(fen, moveString);
           
        } catch (Exception e) {
            e.printStackTrace();
        }
    });
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
            analysis = false;
            if (drawPossibleMoves) {
                drawPossibleMoves(selectedPiece);
            }
            
            pieceView.toFront();
        }
    
        private void handlePieceDrag(MouseEvent event) {
            if (draggedPiece != null) {
                draggedPiece.setTranslateX(event.getSceneX() - 50- draggedPiece.getLayoutX() - chessBoard.localToScene(0, 0).getX());
                draggedPiece.setTranslateY(event.getSceneY() - 50 - draggedPiece.getLayoutY() - chessBoard.localToScene(0, 0).getY());
            }
        }
  
        
        private void handlePieceDrop(MouseEvent event) throws InterruptedException {
            fen = game.toFen();
            
            double squareSize = chessBoard.getPrefWidth() / 8;
            if (draggedPiece != null) {
                int row = (int) ((event.getSceneY() - chessBoard.localToScene(0, 0).getY()) / squareSize);
               int  col = (int) ((event.getSceneX() - chessBoard.localToScene(0, 0).getX()) / squareSize);
                if (row >= 0 && row < 8 && col >= 0 && col < 8) 
                {
                Piece capturedPiece = game.getPiece(row, col);
                boolean validMove = game.makeMove(selectedPiece.getRow(), selectedPiece.getCol(), row, col);
                if (!validMove)
                {
                  SoundManager.playNotifySound();
                }
               //Sometime there is race conditions here fix them...
                if (validMove) {
                    boolean soundPlayed = false;
                    //If you move too fast this will be left behind...
                  
                    startGameAnalysis(selectedPiece.getRow(),selectedPiece.getCol(),row, col,fen);
                
                    switchPlayer(); // Switch the active timer after a valid move
                    
                    // Check for castling first
                    if (selectedPiece instanceof King && Math.abs(col - selectedPiece.getCol()) == 2) {
                        SoundManager.playCastleSound();
                        soundPlayed = true;
                    }
                
                    // Check for pawn promotion
                    if (selectedPiece instanceof Pawn && (row == 0 || row == 7)) {
                        pawnToPromote = (Pawn) selectedPiece;
                        promotePawn(row, col);
                    }
                
                    // Check for captures
                    if (selectedPiece instanceof Pawn && selectedPiece.getCol() != col) {
                        capturedPiece = new Pawn(row, col, game.isWhiteTurn());
                    }
                
                    // Play sound based on capture, check, or move
                    if (!soundPlayed) {
                        if (capturedPiece != null) {
                            SoundManager.playCaptureSound();
                            addTograve( capturedPiece);
                        } else if (game.isInCheck(game.isWhiteTurn())) {
                            SoundManager.playCheckSound();
                        } else {
                            SoundManager.playMoveSound();
                        }
                    }
                }
                } else{
                    SoundManager.playNotifySound();
                }
            }
                drawBoard();
                if(game.checkMate(game))
                {
                    SoundManager.playWinSound();
                    new Thread(() -> {
                        try {
                    analysisExecutor.awaitTermination(1,  TimeUnit.SECONDS);
                    analysisExecutor.shutdown();
                    if(analysisExecutor.isShutdown())
                    {
                       System.err.println("Executor shutdown");
                       stockfish.handleGameEnd(fen);
                       System.err.println(stockfish.getMoveHistory().size()); 
                    } 
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }).start();
                    displayConfetti(chessBoard);
                    
                    statusLabel.setText("Victory");
                    statusLabel.setVisible(true);
                    countdownClock.stop();
                    countdownClock2.stop(); 
                    
                     
                }
                if(game.checkDraw(game))
                {
                    statusLabel.setText("Draw");
                    statusLabel.setVisible(true);
                    SoundManager.playDrawSound();
                    countdownClock.stop();
                    countdownClock2.stop();
                }
                selectedPiece = null;
                draggedPiece = null;
            }
            
            
        
    //Fix this mess wtf is this
            private void addTograve(Piece capturedPiece) {
                boolean isWhite = capturedPiece.isWhite();
                Image pieceImage = getPieceImage(capturedPiece);
                ImageView piece = new ImageView(pieceImage);
                piece.setFitHeight(30);
                piece.setFitWidth(30);
            
                // Assuming Piece has a getValue() method that returns the piece's value
                int pieceValue = capturedPiece.getValue();
            
                if (isWhite) {
                    blackHbox.getChildren().add(0,piece);
                    wScore += pieceValue;
                    bScore -= pieceValue;
                    String btext = String.valueOf(bScore);
                    String wtext = String.valueOf(wScore);
                    blackScore.setText("+" + btext);
                    whiteScore.setText("+"+wtext);
                    if(wScore > 0 && wScore > bScore)
                    {
                       String text = String.valueOf(wScore);
                       whiteScore.setText("+" + text);
                       whiteScore.setVisible(true);
                       blackScore.setVisible(false);
                    }
                  
                    if(wScore == bScore){
                        whiteScore.setVisible(false);
                        blackScore.setVisible(false);
                    }
                  
                   
                } else {
                    WhiteHbox.getChildren().add(0,piece);
                    
                    wScore -= pieceValue;
                    bScore += pieceValue;
                    String btext = String.valueOf(bScore);
                    String wtext = String.valueOf(wScore);
                    blackScore.setText("+" + btext);
                    whiteScore.setText("+"+wtext);
                    if (bScore > 0 && bScore > wScore)
                      {
                       String text = String.valueOf(bScore);
                       blackScore.setText("+"+ text); 
                       blackScore.setVisible(true);
                       whiteScore.setVisible(false);
                    }
                    
                    if(wScore == bScore){
                        whiteScore.setVisible(false);
                        blackScore.setVisible(false);
                    }
                      
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
                SoundManager.playButtonSound();
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
      
    
    
        @FXML
        private void handleBackButton() {
            try {
                setRootWithTransition("secondary");
                countdownClock.stop();
                countdownClock2.stop();
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
        
            double squareSize = chessBoard.getPrefWidth() / 8;
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
        
            // Highlight the best move in gold color
           // Add the container to the grid at the origin square
    
        }
        

        public static int[] parseMove(String bestMove) {
            // Parse the move string like "e2e4"
            String from = bestMove.substring(0, 2); // e2
            String to = bestMove.substring(2, 4);   // e4
    
            // Convert from algebraic notation to row and column indices
            int fromCol = from.charAt(0) - 'a';     // 'e' - 'a' = 4
            int fromRow = '8' - from.charAt(1);     // '8' - '2' = 6
    
            int toCol = to.charAt(0) - 'a';         // 'e' - 'a' = 4
            int toRow = '8' - to.charAt(1);         // '8' - '4' = 4
    
            // Return the row and column positions in an array [fromRow, fromCol, toRow, toCol]
            return new int[] {fromRow, fromCol, toRow, toCol};
        }

        private void displayConfetti(Pane pane) {
            double paneWidth = pane.getWidth();
            double paneHeight = pane.getHeight();
        
            for (int i = 0; i < 800; i++) { // Number of confetti pieces
                Confetti confetti = new Confetti(Color.hsb(Math.random() * 360, 1.0, 1.0), paneWidth*2, paneHeight*2);
                pane.getChildren().add(confetti);
                confetti.animate();
            }
        }
        //This is for the timers
    private void switchPlayer() {
        if (game.isWhiteTurn()) {
            countdownClock.pause();
            countdownClock2.start();
            
            timer2.setStyle(
                "-fx-font-size: 16px;"+
                "-fx-text-fill: #000000;"+
               " -fx-background-color: #ffffff;"+
               "-fx-background-radius: 5; "+
               "-fx-border-color: #7B61FF; -fx-border-width: 2px; -fx-border-radius: 5px;"
            );
            timer2.setOpacity(1);
            timer1.setOpacity(0.5);
            timer1.setStyle(
                "-fx-font-size: 16px;"+
                "-fx-text-fill: #000000;"+
               " -fx-background-color: #6f6f6f;"+
               "-fx-background-radius: 5; "
            );
        } else {
            countdownClock2.pause();
            countdownClock.start();
            timer1.setStyle(
                "-fx-font-size: 16px;"+
                "-fx-text-fill: #000000;"+
               " -fx-background-color: #ffffff;"+
               "-fx-background-radius: 5; "+
               "-fx-border-color: #7B61FF; -fx-border-width: 2px; -fx-border-radius: 5px;"
            );
            timer1.setOpacity(1);
            timer2.setOpacity(0.5);
            timer2.setStyle(
                "-fx-font-size: 16px;"+
                "-fx-text-fill: #000000;"+
               " -fx-background-color: #6f6f6f;"+
               "-fx-background-radius: 5; "
            );
        }
    }
    public void onTimeOut() {
        if (game.isWhiteTurn()) {
            statusLabel.setText("Black Wins by Timeout!");
            displayConfetti(chessBoard);
            countdownClock.stop();
            countdownClock2.stop();
            stockfish.handleGameEnd("Win");
            SoundManager.playWinSound();
        } else {
            statusLabel.setText("White Wins by Timeout!");
            countdownClock.stop();
            countdownClock2.stop();
            displayConfetti(chessBoard);
            SoundManager.playWinSound();
        }
        statusLabel.setVisible(true);
        
    } 
       
    }