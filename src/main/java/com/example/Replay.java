package com.example;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;

import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Rectangle2D;
import javafx.scene.ImageCursor;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.util.Duration;





public class Replay {
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
    @FXML private ComboBox<ComboBoxItem> gameSelector;
    @FXML private Button blunders;
    @FXML private Button GreatMoves;
    @FXML private Button backButton;
    @FXML private Button delete;
    @FXML private TextFlow textFlow;
    
    
    Color lightColor = Color.web("#E8EDF9"); 
    Color darkColor = Color.web("#B7C0D8"); 
    Color moveHelpColor = Color.web("#7B61FF");

    private Board board;
    private ImageView draggedPiece;
    private Piece selectedPiece;
    private List<MoveAnalysis> moveHistory;
    private int currentMoveIndex = 0;
    private Database database;
    double screenWidth;
    double screenHeight;
    private Piece piece;
    private int currentScore;
    private int previousScore;

    private List<Color> moveColors;
    private Color colour;
   private Stockfish stockfish;
    private Piece pawnToPromote;
    private Game game;
  
    
  
    
   

    // Constructor that initializes the board with move history
    @FXML
    
    public void  initialize() throws IOException {
        moveColors = new ArrayList<>();
        moveHistory = new ArrayList<>();
        game = new Game();
        

        database = new Database();
        int totalGames = database.getTotalGames();
        moveHistory = database.getMoveAnalysis(totalGames);
        Image cursorImage = new Image(getClass().getResource("/com/example/pointer.png").toExternalForm());
        new Thread(() -> {
            try {
                stockfish = new Stockfish();
            } catch (IOException e) {
               
                e.printStackTrace();
            }
        }).start();
        

    
            ImageCursor customCursor = new ImageCursor(cursorImage);
            pane.setCursor(customCursor);
       
        Screen screen = Screen.getPrimary();
            Rectangle2D bounds = screen.getBounds();
             screenWidth = bounds.getWidth();
             screenHeight = bounds.getHeight();

            double chessBoardSize = screenHeight * 0.75 ;
            double hboxHeight = (screenHeight -chessBoardSize-60) /2; 
            chessBoard.setPrefSize(chessBoardSize, chessBoardSize);
            rootPane.setMinSize(chessBoardSize+30, chessBoardSize+30);
            rootPane.setMaxSize(chessBoardSize+30, chessBoardSize+30);
            borderPane.setMaxSize(screenWidth, screenHeight);
            double boxWidth = (screenWidth - chessBoardSize) / 2;
            vbox.setMaxWidth(boxWidth);
            vbox.setMinWidth(boxWidth);
            vbox.setMaxHeight(screenHeight);
            textFlow.setMinWidth(boxWidth*0.8);
            textFlow.setMaxWidth(boxWidth *0.8);
            textFlow.setMaxHeight(chessBoardSize);
            vbox2.setMaxWidth(boxWidth);
            vbox2.setMaxHeight(screenHeight -(hboxHeight*2));
            vbox2.setSpacing(screenHeight/2);
             // Set height as 10% of the screen height
            WhiteHbox.setMinHeight(hboxHeight);
            WhiteHbox.setPrefHeight(hboxHeight);
            WhiteHbox.setMaxHeight(hboxHeight);
            
            blackHbox.setMinHeight(hboxHeight);
            blackHbox.setPrefHeight(hboxHeight);
            blackHbox.setMaxHeight(hboxHeight);
          
           
      
        board = new Board();
        board.clearBoard();
        if (!moveHistory.isEmpty()) {
           
            game.getBoard().setFEN(moveHistory.get(0).getFEN());  // Set the board to the initial position

            initializeGameComboBox();
            loadGame(totalGames);
        }
    }
    public static int calculateCurrentScore(List<MoveAnalysis> moveHistory, int currentMove) {
        int currentScore = 0;

        // Ensure the index is within bounds
        if (currentMove < 0 || currentMove >= moveHistory.size()) {
            System.err.println("Index out of bounds!" + currentMove);
            return currentScore;
        }
        // Loop through moves up to the specified index
        for (int i = 0; i <= currentMove; i++) {
            MoveAnalysis move = moveHistory.get(i);
            String score = move.getScore();
            try {
                if (score.contains("centipawns")) {
                    // Extract numerical value for centipawns
                    String[] parts = score.split(" ");
                    int scoreValue = Integer.parseInt(parts[0]);
                    currentScore += scoreValue;
                } else if (score.contains("moves to mate")) {
                    // Extract numerical value for mate moves
                    String[] parts = score.split(" ");
                    int mateMoves = Integer.parseInt(parts[0]);
                    if(mateMoves <= 2)
                    {
                        currentScore += mateMoves * 200; 
                    }
                    else if (mateMoves >= 2 && mateMoves <= 5){
                        currentScore += mateMoves * 100; 
                    }
                    else
                    {
                        currentScore += mateMoves * 50; 
                    }
                    
                }
            } catch (NumberFormatException e) {
                System.err.println("Error parsing score: " + e.getMessage());
            }
        }

        return currentScore;
    }


    
private void findNextBlunder() {
    // Start searching from the next index
    int startIndex = (currentMoveIndex + 1) % moveHistory.size();
    
    // Loop through the move history in a cyclic manner
    for (int i = startIndex; i != currentMoveIndex; i = (i + 1) % moveHistory.size()) {
        if (moveHistory.get(i).isBlunder(moveHistory.get(i).getPreviousscore())) {
            currentMoveIndex = i;
            System.out.println("Next blunder found at move: " + currentMoveIndex);
            return;  // Exit the method once the next blunder is found
        }
    }
    // If no blunder is found after completing the cycle
    System.out.println("No more blunders found.");
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
    bestMoveIndicator.setFill(!selectedPiece.isWhite()?Color.WHITE.deriveColor(0, 1, 1, 0.8):Color.BLACK.deriveColor(0, 1, 1, 0.7)); // No fill
    StackPane bestMoveContainer = new StackPane();
    bestMoveIndicator.toFront();
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


private void startGameAnalysis(int fromRow, int fromCol, int toRow, int toCol, String fen) {
    ExecutorService executor = ExecutorServiceManager.getExecutorService();
        executor.submit(() -> {
       
         
            int[] bestMove = parseMove(stockfish.getBestMove(fen));
            Platform.runLater(() -> {
                drawBestMoveIndicators(bestMove[0], bestMove[1], bestMove[2], bestMove[3]);  
            });    
       
    });
}

private void findNextGreatMove() {
    // Start searching from the next index
    int startIndex = (currentMoveIndex ) % moveHistory.size();
    
    // Loop through the move history in a cyclic manner
    for (int i = startIndex; i != currentMoveIndex; i = (i + 1) % moveHistory.size()) {
        if (moveHistory.get(i).isGreatMove(moveHistory.get(i).getPreviousscore())) {
            currentMoveIndex = i;
            System.out.println("Next great move found at move: " + currentMoveIndex);
            return;  // Exit the method once the next great move is found
        }
    }
    // If no great move is found after completing the cycle
    System.out.println("No more great moves found.");
}

    

    private void loadGame(int gameId) {
        // Retrieve move history for the selected game
        moveHistory = database.getMoveAnalysis(gameId);

        if (!moveHistory.isEmpty()) {
            currentMoveIndex = moveHistory.size()-1;
            board.clearBoard();
            updateScoreAndColor();
            game.getBoard().setFEN(moveHistory.get(currentMoveIndex).getFEN());  // Set the board to the initial position
            drawBoard();
        }
    }
    private void initializeGameComboBox() {
        int totalGames = database.getTotalGames();
        ObservableList<ComboBoxItem> gameItems = FXCollections.observableArrayList();

        // Populate ComboBox with ComboBoxItem objects (1 to totalGames)
        for (int i = 1; i <= totalGames; i++) {
            gameItems.add(new ComboBoxItem("GAME", i));
        }
        gameSelector.setItems(gameItems);
        gameSelector.getSelectionModel().selectFirst(); // Select the first game by default
    }

    private void analyzeboard(String fen, Game game)
    {
        textFlow.getChildren().clear();
        String description = FENParser.fenToNaturalLanguage(fen, game);
       Text responseText = new Text(description);
       textFlow.getChildren().add(responseText);
    }
   

    private void drawBoard() {
        chessBoard.getChildren().clear();
        double squareSize = chessBoard.getPrefWidth() / 8;
       
       
        int[] playerMove = {0, 0, 0, 0};
        int[] bestMove ={0,0,0,0};
    
        if (currentMoveIndex > 0) {
             bestMove = parseMove(moveHistory.get(currentMoveIndex).getBestMove());
            playerMove = parseMove(moveHistory.get(currentMoveIndex ).getPlayerMove());
        }
    
        int bestFromRow = bestMove[0];
        int bestFromCol = bestMove[1];
        int bestToRow = bestMove[2];
        int bestToCol = bestMove[3];
    
        int playerFromRow = playerMove[0];
        int playerFromCol = playerMove[1];
        int playerToRow = playerMove[2];
        int playerToCol = playerMove[3];
        colour = updateScoreAndColor();
        
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                Rectangle square = new Rectangle(squareSize, squareSize);
                
                // Different shades for the best move
                if (i == bestFromRow && j == bestFromCol) {
                    square.setFill(Color.GOLD.darker()); // From square for best move
                } else if (i == bestToRow && j == bestToCol) {
                    square.setFill(Color.GOLD); // To square for best move
                }
                // Different shades for the player's move
                else if (i == playerFromRow && j == playerFromCol && currentMoveIndex > 0) {
                  
                        square.setFill(colour.darker()); 
                  
                   // Color based on score
                } else if (i == playerToRow && j == playerToCol && currentMoveIndex > 0) {
                  
                  
                        square.setFill(colour); 
                 
                }
                // Regular board colors
                else {
                    square.setFill((i + j) % 2 == 0 ? lightColor : darkColor);
                }
    
                chessBoard.add(square, j, i);
    
                Piece piece = game.getBoard().getPiece(i, j);
                if (piece != null) {
                    Image pieceImage = getPieceImage(piece);
                    ImageView pieceView = new ImageView(pieceImage);
                    pieceView.setFitHeight(squareSize);
                    pieceView.setFitWidth(squareSize);
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
                    chessBoard.add(pieceView, j, i);
    
                    pieceView.setOnMousePressed(event -> handlePieceDragStart(event, pieceView, piece));
                    pieceView.setOnMouseDragged(this::handlePieceDrag);
                    pieceView.setOnMouseReleased(this::handlePieceDrop);
                }
            }
        }
        if(currentMoveIndex < moveHistory.size()-1)
        {
            analyzeboard(moveHistory.get(currentMoveIndex).getFEN(),game);
        }
      
       
       
    }
 
    
    private Color getColorForScore(int score) {
        // Define thresholds
        double lowThreshold = 0.4;  // Below this threshold
        double highThreshold = 0.7; // Above this threshold
        double neutralThresholdLow = 0.5;  // Lower bound of neutral range
        double neutralThresholdHigh = 0.6; // Upper bound of neutral range
        
        // Normalize the score
        double normalizedScore = normalizeScore(score);
        
        // Assign colors based on thresholds
        if (normalizedScore < lowThreshold) {
            return Color.RED; // Color for low scores (bad moves)
        } else if (normalizedScore >= lowThreshold && normalizedScore < neutralThresholdLow) {
            return Color.color(1.0, 0.6, 0.6);// Color for scores between low and neutral
        } else if (normalizedScore >= neutralThresholdLow && normalizedScore <= neutralThresholdHigh) {
            return Color.GRAY; // Color for neutral scores
        } else if (normalizedScore > neutralThresholdHigh && normalizedScore < highThreshold) {
            return Color.GREEN; // Color for scores between neutral and high
        } else {
            return Color.GREEN; // Color for high scores (good moves)
        }
    }
   
  
    private Color updateScoreAndColor() {
     
        currentScore = calculateCurrentScore(moveHistory, currentMoveIndex);
      
        // Get color based on score change
        Color color = getColorForScoreChange(currentScore, previousScore);
        // Update previous score
       // previousScore = currentScore;
      
        // Use the color for visualization as needed
         moveColors.add(color);
         return color;
       
    }
    private Color getColorForScoreChange(int currentScore, int previousScore) {
        // Calculate the change in score
        int scoreChange;
       if(currentMoveIndex + 1 < moveHistory.size())
       {
        scoreChange = (calculateCurrentScore(moveHistory, currentMoveIndex + 1 )- currentScore) * -1  ;
       }
       else
       {
        scoreChange = 0;
       }
        
    
        // Define thresholds for color mapping
        int highChangeThreshold = 200;
        int mediumChangeThreshold = 100;
        int lowChangeThreshold = 50;
    
        // Determine color based on the direction and magnitude of the score change
        if (scoreChange >= highChangeThreshold) {
            return Color.GREEN; // Significant improvement
        } else if (scoreChange >= mediumChangeThreshold) {
            return Color.LIGHTGREEN; // Moderate improvement
        } else if (scoreChange >= lowChangeThreshold) {
            return Color.GRAY; // Minor improvement
        } else if (scoreChange <= -highChangeThreshold) {
            return Color.RED; // Significant worsening
        } else if (scoreChange <= -mediumChangeThreshold) {
            return Color.PINK; // Moderate worsening
        } else if (scoreChange <= -lowChangeThreshold) {
            return Color.GRAY; // Minor worsening
        } else {
            return Color.GRAY; // Little to no change
        }
    }
    
    
    
    // Normalize the score to a range [0, 1] based on the expected score range
    private double normalizeScore(int score) {
        int minScore = -600; // Example minimum bad score
        int maxScore = 600;  // Example maximum good score
        return Math.min(1, Math.max(0, (double)(score - minScore) / (maxScore - minScore)));
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
        drawPossibleMoves(selectedPiece);
        pieceView.toFront();
    }

    private void handlePieceDrag(MouseEvent event) {
        if (draggedPiece != null) {
            draggedPiece.setTranslateX(event.getSceneX() - 50 - draggedPiece.getLayoutX() - chessBoard.localToScene(0, 0).getX());
            draggedPiece.setTranslateY(event.getSceneY() - 50 - draggedPiece.getLayoutY() - chessBoard.localToScene(0, 0).getY());
        }
    }

    private void handlePieceDrop(MouseEvent event) {
        double squareSize = chessBoard.getPrefWidth() / 8;
       
        if (draggedPiece != null) {
            int row = (int) ((event.getSceneY() - chessBoard.localToScene(0, 0).getY()) / squareSize);
            int col = (int) ((event.getSceneX() - chessBoard.localToScene(0, 0).getX()) / squareSize);
            if (row >= 0 && row < 8 && col >= 0 && col < 8) {
                
                game.setPiece(row, col, selectedPiece);
                if(row != selectedPiece.getRow() || col != selectedPiece.getCol())
                {
                    game.setPiece(selectedPiece.getRow(), selectedPiece.getCol(), null);
                }
                if (selectedPiece instanceof Pawn && (row == 0 || row == 7)) {
                    pawnToPromote = (Pawn) selectedPiece;
                    promotePawn(row, col);
                }
                
                 SoundManager.playMoveSound();
                 startGameAnalysis(row, col, row, col,  board.toFEN(!selectedPiece.isWhite()));
            }
            else
            {
                game.setPiece(selectedPiece.getRow(), selectedPiece.getCol(), selectedPiece);
                SoundManager.playNotifySound();
            }
        }
        
        drawBoard();
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
            boolean isOccupied = game.getPiece(row, col) != null; 
    
                           
            StackPane moveIndicatorContainer = new StackPane();
           
            if (isOccupied) {
                Rectangle moveIndicatorR = new Rectangle(squareSize-4 , squareSize-4); 
                moveIndicatorR.setFill(null); 
                moveIndicatorR.setStroke(moveHelpColor); 
                moveIndicatorR.setStrokeWidth(4); 
                moveIndicatorContainer.getChildren().add(moveIndicatorR);
                
            } else {
                Circle moveIndicator = new Circle(indicatorSize / 2);
                moveIndicator.setFill(moveHelpColor.deriveColor(0, 1, 1, 0.7)); 
                moveIndicatorContainer.getChildren().add(moveIndicator);
            }

            moveIndicatorContainer.setPickOnBounds(false);
            moveIndicatorContainer.setPrefSize(squareSize, squareSize); // Ensure the container matches the square size
            moveIndicatorContainer.setMouseTransparent(true);
            chessBoard.add(moveIndicatorContainer, col, row); // Add the container to the grid
        }

    }

    @FXML
    private void handleRewindBack() {
        if (currentMoveIndex >= 0) {
            currentMoveIndex--;
            replayMoves();
        }
    }

    @FXML
    private void handleRewindForward() {
        if (currentMoveIndex <= moveHistory.size() - 1) {
            currentMoveIndex++;
            replayMoves();
        }
    }

    private void replayMoves() {
        if (currentMoveIndex >= 0 && currentMoveIndex < moveHistory.size()) {
            MoveAnalysis moveAnalysis = moveHistory.get(currentMoveIndex);
            game.getBoard().clearBoard();
            game.getBoard().setFEN(moveAnalysis.getFEN());
          
          
            drawBoard();
        }
    }

    // Method to undo the last move
    public void undoMove() {
        if (currentMoveIndex > 0) {
            currentMoveIndex--;
            MoveAnalysis moveAnalysis = moveHistory.get(currentMoveIndex);
            board.clearBoard();
            board.setFEN(moveAnalysis.getFEN());
            drawBoard();
        }
    }
    @FXML
    private void handleGameSelection() {
        ComboBoxItem selectedGame = gameSelector.getValue();
        int id =0;
        if (selectedGame != null) {
             id = selectedGame.getValue();
            // Continue processing with the value
        } else {
            // Handle the case where selectedGame is null
            System.out.println("No game is selected.");
        }
        loadGame(id);
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
                board.setPiece(row, col, newPiece);
                System.out.println("Pawn promoted to " + choice + ".");
                pawnToPromote = null; // Reset pawnToPromote after promotion
                drawBoard();  // Redraw the board to update the piece's position
               
                
            });
            promotionMenu.setLayoutX(row);
            promotionMenu.setLayoutY(col);
            rootPane.getChildren().add(promotionMenu);
          
           
        }
    

   
    @FXML
private void showBlunders() {
    findNextBlunder();
    replayMoves();  // Update the board to reflect the move at the new currentMoveIndex
}
@FXML
private void showGreatMoves() {
    findNextGreatMove();
    replayMoves();  // Update the board to reflect the move at the new currentMoveIndex
}
@FXML
private void deleteGames()
{
    database.clearDatabase();
    initializeGameComboBox();
}
}

