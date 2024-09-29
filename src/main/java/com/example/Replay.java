package com.example;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javafx.animation.FadeTransition;
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
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
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
    private double previousScore;
    private double scoreChange;
    private  int scoreValue;

    private List<Color> moveColors;
    private Color colour;
   private Stockfish stockfish;
    private Piece pawnToPromote;
    private Game game;
    private  PythonScriptRunner python ;
    private String moveCategory;
    private double evaluation;
 
  
    
  
    
   

    // Constructor that initializes the board with move history
    @FXML
    
    public void  initialize() throws IOException, InterruptedException {
        moveColors = new ArrayList<>();
        moveHistory = new ArrayList<>();
        game = new Game();
       
    
        python = new PythonScriptRunner("C:\\Users\\eelip\\Gemini API.py");

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
            currentScore =0;
            score ="";
          
           
      
        board = new Board();
        board.clearBoard();
        if (!moveHistory.isEmpty()) {
           
            game.getBoard().setFEN(moveHistory.get(0).getFEN());  // Set the board to the initial position

            initializeGameComboBox();
            loadGame(totalGames);
        }
    }
    private String score;
    private int mateMoves;
    public int calculateCurrentScore(List<MoveAnalysis> moveHistory, int currentMove) {
        int currentScore = 0;
    
        // Ensure the index is within bounds
        if (currentMove < 0 || currentMove >= moveHistory.size()) {
            System.err.println("Index out of bounds: " + currentMove);
            return currentScore;
        }
    
        // Get the current move analysis
        MoveAnalysis move = moveHistory.get(currentMove);
        score = move.getScore();
        System.out.println("Score: " + score);
    
        try {
            if (score.contains("mate")) {
                // If it's a mate sequence, get the number of moves to mate
                String[] parts = score.split(" ");
                mateMoves = Integer.parseInt(parts[0]); // Number of moves to mate
    
                // Set the currentScore based on how many moves to mate
                if (mateMoves <= 3) {
                    currentScore = 1000; // Indicating a critical situation
                } else if (mateMoves <= 5) {
                    currentScore = 500; // Indicating a mistake scenario
                } else {
                    currentScore = 100; // Indicating an inaccuracy scenario
                }
            } else if (score.contains("centipawns")) {
                // If it's a centipawn evaluation, extract the centipawn value
                String[] parts = score.split(" ");
                currentScore = Integer.parseInt(parts[0]); // Convert centipawns to integer
            }
        } catch (NumberFormatException e) {
            System.err.println("Error parsing score: " + e.getMessage());
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

    

    private void loadGame(int gameId) throws IOException, InterruptedException {
        // Retrieve move history for the selected game
        moveHistory = database.getMoveAnalysis(gameId);

        if (!moveHistory.isEmpty()) {
            currentMoveIndex = moveHistory.size()-1;
            board.clearBoard();
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


    private void analyzeboard(Game game, String bestMove, String playerMove)  throws IOException, InterruptedException
    {
       textFlow.getChildren().clear();
       

       String scoreString = String.valueOf(evaluation);
       if(Math.abs(currentScore )== 1000)
       {
        scoreString = score;
       }
       System.out.println("THIS IS IT"+moveCategory);
       MoveAdvisor coach = new MoveAdvisor(game,bestMove,playerMove, stockfish, moveCategory, scoreString);
       Text responseText = new Text(  coach.analyzeMove() + "\n"+ scoreString+ "\n"+ moveCategory);  
       textFlow.getChildren().add(responseText);
    }
   
  
    private Color updateScoreAndColor() {
        // Calculate current score based on move history and current move index
        currentScore = calculateCurrentScore(moveHistory, currentMoveIndex);
    
        // Determine the evaluation for the current player
        int n = game.isWhiteTurn() ? -1 : 1; // Positive for White, negative for Black
        double eval = (double) currentScore / 100 * n;  // Normalize evaluation for the current move
        evaluation = eval;
    
        // Calculate the difference between the current and previous evaluations
        double scoreChange = eval - previousScore;  // This captures the change in position
    
        // Log for debugging
        System.out.println("Current Eval: " + eval);
        System.out.println("Previous Eval: " + previousScore);
        System.out.println("Score Change: " + scoreChange);

        // Categorize the move based on the actual change in score
        if(eval > 5 )
        {
            scoreChange = scoreChange * 0.7;
        }
        if( eval < -5)
        {
            scoreChange = scoreChange * 0.7;
        }
         moveCategory = categorizeMove(scoreChange);  // Categorize based on the actual change
    
        // Get the color based on the score change
        Color color = getColorForScoreChange(scoreChange);  // Use scoreChange for color determination
    
        // Store the color for future reference
        moveColors.add(color);
    
        // Update previous score for the next calculation
        previousScore = eval;  // Update after processing
    
        return color;
    }
    
    
   

    private Color getColorForScoreChange(double scoreChange) {
        // Define thresholds for color mapping based on move category
        moveCategory = categorizeMove(scoreChange); 
        System.out.println("Move Category: " + moveCategory);
        

    
        // Color mapping based on move category
        switch (moveCategory) {
            case "Brilliant":
                return Color.GREEN; // Large positive change, excellent move
            case "Good":
                return Color.LIGHTGREEN; // Moderate positive change, good move
            case "Slight Improvement":
                return Color.GRAY; // Small positive change, slight improvement
            case "Inaccuracy":
                return Color.ORANGE; // Small negative change, inaccuracy
            case "Mistake":
                return Color.PINK; // Moderate negative change, mistake
            case "Blunder":
                return Color.RED; // Large negative change, blunder
            case "Best":
                return Color.GOLD; // Exceptional move
            default:
                return Color.GRAY; // Even or no significant change
        }
    }
    

public String categorizeMove(double scoreChange) {
    // Determine if the piece is White or Black and categorize move accordingly
    if (currentMoveIndex < moveHistory.size()-1 ) {
    String best = moveHistory.get(currentMoveIndex+1).getBestMove();
     String   move =moveHistory.get(currentMoveIndex+1).getPlayerMove();
     if (move.equals(best))
     {
        return "Best";
     }
    }
    
    if (game.isWhiteTurn()) {
        if (scoreChange <= -3) {
            return "Blunder";  // Large negative score change (bad for White)
        } else if (scoreChange <= -1) {
            return "Mistake";  // Moderate negative score change
        } else if (scoreChange < -0.3) {
            return "Inaccuracy";  // Small negative score change
        } else if (scoreChange >= 3) {
            return "Brilliant";  // Large positive score change (great for White)
        } else if (scoreChange >= 1) {
            return "Good";  // Moderate positive score change
        } else if (scoreChange > 0.3) {
            return "Slight Improvement";  // Small positive score change
        } else {
            return "Even";  // No significant change
        }
    } else {  // For Black pieces, reverse the logic for positive/negative changes
        if (scoreChange >= 3) {
            return "Blunder";  // Large positive score change (bad for Black)
        } else if (scoreChange >= 1) {
            return "Mistake";  // Moderate positive score change
        } else if (scoreChange > 0.3) {
            return "Inaccuracy";  // Small positive score change
        } else if (scoreChange <= -3) {
            return "Brilliant";  // Large negative score change (good for Black)
        } else if (scoreChange <= -1) {
            return "Good";  // Moderate negative score change
        } else if (scoreChange < -0.3) {
            return "Slight Improvement";  // Small negative score change (good for Black)
        } else {
            return "Even";  // No significant change
        }
    }
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
            try {
                MoveAnalysis moveAnalysis = moveHistory.get(currentMoveIndex);
                game.getBoard().clearBoard();
                game.getBoard().setFEN(moveAnalysis.getFEN());
               
                
                drawBoard();
            } catch (IOException ex) {
            } catch (InterruptedException ex) {
            }
        }
    }

    // Method to undo the last move
    public void undoMove() {
        if (currentMoveIndex > 0) {
            currentMoveIndex--;
            MoveAnalysis moveAnalysis = moveHistory.get(currentMoveIndex);
            board.clearBoard();
            board.setFEN(moveAnalysis.getFEN());
            try {
                drawBoard();
            } catch (IOException ex) {
            } catch (InterruptedException ex) {
            }
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
        try {
            loadGame(id);
        } catch (IOException ex) {
        } catch (InterruptedException ex) {
        }
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
private void drawBoard() throws IOException, InterruptedException {
    getColorForScoreChange(scoreChange);
    chessBoard.getChildren().clear();
    double squareSize = chessBoard.getPrefWidth() / 8;
    String best ="" ;
    String move= "";
   
   
    int[] playerMove = {0, 0, 0, 0};
    int[] bestMove ={0,0,0,0};

    if (currentMoveIndex < moveHistory.size()-1 ) {
        best = moveHistory.get(currentMoveIndex).getBestMove();
        move =moveHistory.get(currentMoveIndex+1).getPlayerMove();
        bestMove = parseMove(best);
        playerMove = parseMove(move);
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
            
           
            if (i == playerFromRow && j == playerFromCol && currentMoveIndex > 0) {
              
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
            }
        }
    }

    if(currentMoveIndex < moveHistory.size()-1)
    {
        analyzeboard(game, best, move);
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
}

