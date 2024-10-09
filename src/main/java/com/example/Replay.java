package com.example;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.ImageCursor;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextArea;
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
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import java.util.Map;
import java.util.HashMap;
import java.util.Arrays;

public class Replay {
    @FXML
    private StackPane rootPane;
    @FXML
    private GridPane chessBoard;
    @FXML
    private BorderPane borderPane;
    @FXML
    private Button exitButton;
    @FXML
    private Button exitButton1;
    @FXML
    private Button fullScreenButton;
    @FXML
    private Button resetButton;
    @FXML
    private Button undoButton;
    @FXML
    private HBox hbox1;
    @FXML
    private HBox hbox11;
    @FXML
    private VBox vbox2;
    @FXML
    private VBox vbox;
    @FXML
    private HBox timer1;
    @FXML
    private HBox timer2;
    @FXML
    private HBox WhiteHbox;
    @FXML
    private HBox blackHbox;
    @FXML
    private AnchorPane pane;
    @FXML
    private ComboBox<ComboBoxItem> gameSelector;
    @FXML
    private Button backButton;
    @FXML
    private Button delete;
    @FXML
    private TextFlow textFlow;
    @FXML
    private ProgressBar progressBar;
    @FXML
    private Label progressLabel;
    @FXML
    private Label openingLabel;
    @FXML
    private Button replayButton;

    Color lightColor = Color.web("#E8EDF9");
    Color darkColor = Color.web("#B7C0D8");
    Color moveHelpColor = Color.web("#7B61FF");

    private Board board;
    private List<MoveAnalysis> moveHistory;
    private int currentMoveIndex = 0;
    private Database database;
    private double screenWidth;
    private double screenHeight;
    private int currentScore;
    private double previousScore;
    private double scoreChange;
    private Color colour;
    private Stockfish stockfish;
    private Game game;
    private String moveCategory;
    private double evaluation;
    private String score;
    private int mateMoves;
    private double normalizedValue;
    private List<String> whiteMoveCategories;
    private List<String> blackMoveCategories;
    private Map<String, Integer> whiteMoveCount;
    private Map<String, Integer> blackMoveCount;
    private Label loadingBar;
    private String fen;
    @FXML
    private TextArea summaryTextArea;
    private int previousMate;
    private List<String> moves;
    private ChessOpeningMap openning;

    @FXML
    public void initialize() throws IOException, InterruptedException {
        moveHistory = new ArrayList<>();
        moves = new ArrayList<>();
        openning = new ChessOpeningMap();
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
        openingLabel.setText("");

        openingLabel.setVisible(false);
        loadingBar = new Label();
        loadingBar.setStyle(
                "-fx-font-family: 'Arial Black', Gadget, sans-serif; -fx-font-size: 25px; -fx-font-weight: bold; -fx-text-fill: black;");
        loadingBar.setVisible(false);
        loadingBar.setText("Analyzing...");
        rootPane.getChildren().add(loadingBar);
        pane.setCursor(customCursor);
        Screen screen = Screen.getPrimary();
        Rectangle2D bounds = screen.getBounds();
        screenWidth = bounds.getWidth();
        screenHeight = bounds.getHeight();
        double chessBoardSize = screenHeight * 0.75;
        double hboxHeight = (screenHeight - chessBoardSize - 60) / 2;
        chessBoard.setPrefSize(chessBoardSize, chessBoardSize);
        rootPane.setMinSize(chessBoardSize + 30, chessBoardSize + 30);
        rootPane.setMaxSize(chessBoardSize + 30, chessBoardSize + 30);
        borderPane.setMaxSize(screenWidth, screenHeight);
        double boxWidth = (screenWidth - chessBoardSize) / 2;
        vbox.setMaxWidth(boxWidth);
        vbox.setMinWidth(boxWidth);
        vbox.setMaxHeight(screenHeight);
        textFlow.setMinWidth(boxWidth * 0.8);
        textFlow.setMaxWidth(boxWidth * 0.8);
        textFlow.setMaxHeight(chessBoardSize);
        vbox2.setMaxWidth(boxWidth);
        vbox2.setMaxHeight(screenHeight - (hboxHeight * 2));
        vbox2.setSpacing(20);
        // Set height as 10% of the screen height
        WhiteHbox.setMinHeight(hboxHeight);
        WhiteHbox.setPrefHeight(hboxHeight);
        WhiteHbox.setMaxHeight(hboxHeight);

        blackHbox.setMinHeight(hboxHeight);
        blackHbox.setPrefHeight(hboxHeight);
        blackHbox.setMaxHeight(hboxHeight);

        progressBar.setProgress(0.5);
        progressBar.setPrefWidth(chessBoardSize * 0.8);
        progressBar.setMaxWidth(chessBoardSize);

        progressBar.setStyle(
                "-fx-background-color: transparent;" +
                        "-fx-accent: #7B61FF;" +
                        "-fx-pref-height: 20px;" +
                        "-fx-background-radius: 10;" +
                        "-fx-bar-background-radius: 10;" +
                        "-fx-border-color: transparent;"

        );

        progressLabel.setPrefWidth(45);
        currentScore = 0;
        score = "";
        board = new Board();
        board.clearBoard();
        if (!moveHistory.isEmpty()) {
            game.getBoard().setFEN(moveHistory.get(0).getFEN()); // Set the board to the initial position
            initializeGameComboBox();
            loadGame(totalGames);
        }
    }

    public int calculateCurrentScore(List<MoveAnalysis> moveHistory, int currentMove) {
        int currentScore = 0;
        // Ensure the index is within bounds
        if (currentMove < 0 || currentMove >= moveHistory.size()) {
            System.err.println("Index out of bounds: " + currentMove);
            return currentScore;
        }
        // Get the current move analymsis
        MoveAnalysis move = moveHistory.get(currentMove);
        score = move.getScore();
        System.out.println("Score: " + score);
        try {
            if (score.contains("mate")) {
                // If it's a mate sequence, get the number of moves to mate
                String[] parts = score.split(" ");
                mateMoves = Integer.parseInt(parts[0]); // Number of moves to mate

            } else if (score.contains("centipawns")) {
                // If it's a centipawn evaluation, extract the centipawn value
                String[] parts = score.split(" ");
                currentScore = Integer.parseInt(parts[0]); // Convert centipawns to integer
                mateMoves = 0;

            }
        } catch (NumberFormatException e) {
            System.err.println("Error parsing score: " + e.getMessage());
        }

        return currentScore;
    }

    private void loadGame(int gameId) throws IOException, InterruptedException {
        // Retrieve move history for the selected game
        moveHistory = database.getMoveAnalysis(gameId);
        whiteMoveCategories = new ArrayList<>(); // Track White's move categories
        blackMoveCategories = new ArrayList<>();
        whiteMoveCount = new HashMap<>(); // Initialize map for White
        blackMoveCount = new HashMap<>();

        if (!moveHistory.isEmpty()) {
            currentMoveIndex = moveHistory.size() - 1;
            board.clearBoard();
            fen = moveHistory.get(currentMoveIndex).getFEN();
            game.getBoard().setFEN(moveHistory.get(currentMoveIndex).getFEN()); // Set the board to the initial position
            drawBoard();
        }
    }

    private void analyzeboard(Game game, String bestMove, String playerMove) throws IOException, InterruptedException {
        textFlow.getChildren().clear();
        String scoreString = String.valueOf(evaluation);
        if (score.contains("mate")) {
            scoreString = "M" + mateMoves;
            if (mateMoves == 0) {
                scoreString = "#";
            }
            if (fen.contains("b")) {
                normalizedValue = 100;
            } else {
                normalizedValue = 0;
            }

        }
        progressLabel.setText(scoreString);
        // Set the progress
        progressBar.setProgress(normalizedValue);
        String line = moveHistory.get(currentMoveIndex).getBestLine();
        MoveAdvisor coach = new MoveAdvisor(game, bestMove, playerMove, stockfish, moveCategory, scoreString, line,
                fen);
        String name = openning.search(formatFen(fen));
        if (!name.equals("Unknown Opening")) {
            openingLabel.setText(name);
            openingLabel.setVisible(true);
        } else {
            openingLabel.setVisible(false);
        }

        Text responseText = new Text(coach.analyzeMove());
        responseText.setStyle("-fx-font-size: 16; -fx-font-weight: bold; -fx-fill:  black; -fx-font-style: italic; ");
        // Load the image using the correct path
        Image iconImage = loadImageForMoveCategory(moveCategory);
        // Replace with your icon path
        ImageView iconView = new ImageView(iconImage);
        iconView.setFitWidth(25); // Set desired width
        iconView.setFitHeight(25); // Set desired height
        iconView.setPreserveRatio(true); // Preserve aspect ratio
        iconView.setStyle("-fx-padding: 5;");
        textFlow.getChildren().addAll(iconView, responseText);
    }

    public static String formatFen(String fullFen) {
        // Split the full FEN string by space to get the first two parts
        String[] parts = fullFen.split(" ");

        // Return only the piece placement (parts[0]) and active color (parts[1])
        return parts[0] + " " + parts[1];
    }

    public String unflipFEN(String fen) {
        // Split the FEN string into its components
        String[] fenParts = fen.split(" ");

        // Get the piece placement part (first part)
        String[] rows = fenParts[0].split("/");

        // Reverse the rows
        StringBuilder flippedPosition = new StringBuilder();
        for (int i = rows.length - 1; i >= 0; i--) {
            flippedPosition.append(rows[i]);
            if (i != 0) {
                flippedPosition.append("/");
            }
        }

        // Flip the side to move
        String sideToMove = fenParts[1].equals("w") ? "b" : "w";

        // Construct the new flipped FEN string
        String flippedFEN = flippedPosition.toString() + " " + sideToMove + " ";

        return flippedFEN;
    }

    public Image loadImageForMoveCategory(String moveCategory) {
        // Determine the image path based on the move category
        String imagePath;
        switch (moveCategory) {
            case "Brilliant":
                imagePath = "/images/brilliant.png"; // Path to the Brilliant image
                break;
            case "Good":
                imagePath = "/images/good.png"; // Path to the Good image
                break;
            case "Slight Improvement":
                imagePath = "/images/slightImprovement.png"; // Path to the Slight Improvement image
                break;
            case "Inaccuracy":
                imagePath = "/images/inaccuracy.png"; // Path to the Inaccuracy image
                break;
            case "Mistake":
                imagePath = "/images/mistake.png"; // Path to the Mistake image
                break;
            case "Blunder":
                imagePath = "/images/blunder.png"; // Path to the Blunder image
                break;
            case "Best":
                imagePath = "/images/best7.png"; // Path to the Best image
                break;
            case "Even":
                imagePath = "/images/book.png";
                break;

            default:
                imagePath = "/images/book.png"; // Default image path if category is not found
                break;
        }

        // Load and return the image from the resources
        Image iconImage = new Image(getClass().getResourceAsStream(imagePath));
        return iconImage;

    }

    private Color updateScoreAndColor() {
        // Calculate current score based on move history and current move index
        previousScore = calculateCurrentScore(moveHistory, currentMoveIndex + 1);
        currentScore = calculateCurrentScore(moveHistory, currentMoveIndex);

        String previousfen = "";
        int z;
        System.out.println(fen);
        System.out.println(previousfen);

        fen = moveHistory.get(currentMoveIndex).getFEN();
        if (currentMoveIndex < moveHistory.size() - 1) {
            previousfen = moveHistory.get(currentMoveIndex + 1).getFEN();
            z = previousfen.contains("w") ? 1 : -1;
        } else {
            previousfen = "";
            z = 0;
        }

        // Castling loses the white king and when black castles the white rook disappers
        // also
        // Determine the evaluation for the current player
        int n = fen.contains("w") ? 1 : -1;
        System.out.print(fen);
        z = previousfen.contains("w") ? 1 : -1;
        double eval = (double) currentScore / 100 * n;
        // Normalize evaluation for the current move
        previousScore = (double) previousScore / 100 * z;
        evaluation = eval;

        scoreChange = (double) Math.abs(eval - previousScore) * z;
        normalizedValue = 1 - (eval + 10) / (2 * 10);
        // previousScore = eval;

        // Log for debugging
        System.out.println("Current Eval: " + eval);
        System.out.println("Previous Eval: " + previousScore);
        System.out.println("Scorchange " + scoreChange);
        System.out.println("N: " + n);

        moveCategory = categorizeMove(scoreChange); // Categorize based on the actual change

        // Get the color based on the score change
        Color color = getColorForScoreChange(scoreChange); // Use scoreChange for color determination

        if (!fen.contains("w")) {
            whiteMoveCategories.add(moveCategory);
            whiteMoveCount.put(moveCategory, whiteMoveCount.getOrDefault(moveCategory, 0) + 1); // Count moves by
                                                                                                // category for White
        } else {
            blackMoveCategories.add(moveCategory);
            blackMoveCount.put(moveCategory, blackMoveCount.getOrDefault(moveCategory, 0) + 1); // Count moves by
                                                                                                // category for Black
        }
        // previousScore = eval;
        previousMate = mateMoves;

        return color;
    }

    public void printMoveCountsAndAccuracy() {
        // Count total moves for both players
        int totalWhiteMoves = whiteMoveCategories.size();
        int totalBlackMoves = blackMoveCategories.size();

        // Calculate accuracy based on categories
        double whiteAccuracy = calculateAccuracy(whiteMoveCategories);
        double blackAccuracy = calculateAccuracy(blackMoveCategories);

        // Prepare counts for each category for White
        int whiteBrilliantCount = this.whiteMoveCount.getOrDefault("Brilliant", 0);
        int whiteGoodCount = this.whiteMoveCount.getOrDefault("Good", 0);
        int whiteBestCount = this.whiteMoveCount.getOrDefault("Best", 0);
        int whiteSlightImprovementCount = this.whiteMoveCount.getOrDefault("Slight Improvement", 0);
        int whiteEven = this.whiteMoveCount.getOrDefault("Even", 0);
        int whiteInaccuracyCount = this.whiteMoveCount.getOrDefault("Inaccuracy", 0);
        int whiteMistakeCount = this.whiteMoveCount.getOrDefault("Mistake", 0);
        int whiteBlunderCount = this.whiteMoveCount.getOrDefault("Blunder", 0);

        // Prepare counts for each category for Black
        int blackBrilliantCount = this.blackMoveCount.getOrDefault("Brilliant", 0);
        int blackGoodCount = this.blackMoveCount.getOrDefault("Good", 0);
        int blackBestCount = this.blackMoveCount.getOrDefault("Best", 0);
        int blackSlightImprovementCount = this.blackMoveCount.getOrDefault("Slight Improvement", 0);
        int blackInaccuracyCount = this.blackMoveCount.getOrDefault("Inaccuracy", 0);
        int blackMistakeCount = this.blackMoveCount.getOrDefault("Mistake", 0);
        int blackBlunderCount = this.blackMoveCount.getOrDefault("Blunder", 0);
        int blackEven = this.blackMoveCount.getOrDefault("Even", 0);

        // Create and show the popup
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/popup.fxml")); // Update the path to
                                                                                                   // your FXML
            Parent popupRoot = loader.load();

            // Get the controller and update it
            PopupController controller = loader.getController();
            controller.updateMoveCounts(totalWhiteMoves, whiteAccuracy,
                    whiteBrilliantCount, whiteGoodCount, whiteBestCount,
                    whiteSlightImprovementCount, whiteInaccuracyCount,
                    whiteMistakeCount, whiteBlunderCount, whiteEven,
                    totalBlackMoves, blackAccuracy,
                    blackBrilliantCount, blackGoodCount, blackBestCount,
                    blackSlightImprovementCount, blackInaccuracyCount,
                    blackMistakeCount, blackBlunderCount, blackEven);

            // Create a new Stage for the popup
            Stage popupStage = new Stage();
            popupStage.initModality(Modality.NONE); // Allow interaction with the main window

            popupStage.setScene(new Scene(popupRoot));
            // Add an event filter for mouse clicks outside the popup

            // Get the primary stage (main window)
            Stage primaryStage = (Stage) borderPane.getScene().getWindow();
            popupStage.initOwner(primaryStage); // Set the owner to the main window

            popupStage.initStyle(StageStyle.UNDECORATED);

            popupStage.show(); // Show the popup without waiting for it to close
            primaryStage.getScene().addEventFilter(MouseEvent.MOUSE_PRESSED, event -> {
                Bounds popupBoundsInScene = popupRoot.localToScene(popupRoot.getBoundsInLocal());

                if (!popupBoundsInScene.contains(event.getSceneX(), event.getSceneY())
                        && popupStage.isShowing()) {
                    event.consume();
                    popupStage.close(); // Close the popup
                    System.out.println("Closed popup on outside click");
                }
            });

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    // Calculate accuracy based on the move categories
    private double calculateAccuracy(List<String> moveCategories) {
        if (moveCategories.isEmpty())
            return 0.0; // Avoid division by zero
        int correctMoves = 0;

        for (String category : moveCategories) {
            if (category.equals("Good") || category.equals("Brilliant") || category.equals("Best")
                    || category.equals("Even") || category.equals("Slight Improvement")) {
                correctMoves++;
            }
        }

        return (double) correctMoves / moveCategories.size() * 100; // Calculate accuracy as a percentage
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
        String best = "";
        if (currentMoveIndex < moveHistory.size() - 1) {
            if (currentMoveIndex < moveHistory.size() - 1) {
                best = moveHistory.get(currentMoveIndex + 1).getBestMove();
            }

            String move = moveHistory.get(currentMoveIndex).getPlayerMove();
            if (move.equals(best)) {
                return "Best";
            }
        }

        if (fen.contains("w")) {
            if (scoreChange <= -3) {
                return "Blunder"; // Large negative score change (bad for White)
            } else if (scoreChange <= -1) {
                return "Mistake"; // Moderate negative score change
            } else if (scoreChange < -0.5) {
                return "Inaccuracy"; // Small negative score change
            } else if (scoreChange >= 3) {
                return "Brilliant"; // Large positive score change (great for White)
            } else if (scoreChange >= 1) {
                return "Good"; // Moderate positive score change
            } else if (scoreChange > 0.3) {
                return "Slight Improvement"; // Small positive score change
            } else {
                return "Even"; // No significant change
            }
        } else { // For Black pieces, reverse the logic for positive/negative changes
            if (scoreChange >= 3) {
                return "Blunder"; // Large positive score change (bad for Black)
            } else if (scoreChange >= 1) {
                return "Mistake"; // Moderate positive score change
            } else if (scoreChange > 0.5) {
                return "Inaccuracy"; // Small positive score change
            } else if (scoreChange <= -3) {
                return "Brilliant"; // Large negative score change (good for Black)
            } else if (scoreChange <= -1) {
                return "Good"; // Moderate negative score change
            } else if (scoreChange < -0.3) {
                return "Slight Improvement"; // Small negative score change (good for Black)
            } else {
                return "Even"; // No significant change
            }
        }
    }

    public static int[] parseMove(String bestMove) {
        // Parse the move string like "e2e4"
        String from = bestMove.substring(0, 2); // e2
        String to = bestMove.substring(2, 4); // e4

        // Convert from algebraic notation to row and column indices
        int fromCol = from.charAt(0) - 'a'; // 'e' - 'a' = 4
        int fromRow = '8' - from.charAt(1); // '8' - '2' = 6

        int toCol = to.charAt(0) - 'a'; // 'e' - 'a' = 4
        int toRow = '8' - to.charAt(1); // '8' - '4' = 4

        // Return the row and column positions in an array [fromRow, fromCol, toRow,
        // toCol]
        return new int[] { fromRow, fromCol, toRow, toCol };
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

    // This few fucntions here are a fucking mess The thing starts from the big
    // number and goes down so technically back goes forward
    @FXML
    private void handleRewindBack() {
        currentMoveIndex--;
        if (currentMoveIndex >= 0) {

            System.out.print("INDEX" + currentMoveIndex);
            replayMoves();
        }
    }

    @FXML
    private void handleRewindForward() {
        currentMoveIndex++;
        if (currentMoveIndex <= moveHistory.size() - 1) {

            System.out.print("INDEX" + currentMoveIndex);
            replayMoves();
        }
    }

    @FXML
    private void showBestLine() {
        Game copyGame = new Game();
        String line = "";

        // Ensure there's a next move to display
        if (currentMoveIndex < moveHistory.size() - 1 && currentMoveIndex >= 0) {
            line = moveHistory.get(currentMoveIndex).getBestLine();
            copyGame.getBoard().setFEN(moveHistory.get(currentMoveIndex).getFEN());
        }

        // Split the line into individual moves
        String[] parts = line.split(", ");

        // Clear previous children from TextFlow
        textFlow.getChildren().clear(); // Assuming you have a TextFlow defined as textFlow

        // Create a HBox for each pair of moves
        HBox movesRow = new HBox(10); // Spacing between columns
        movesRow.setAlignment(Pos.CENTER_LEFT); // Align items in HBox

        // Iterate over moves to create TextFlow
        for (int i = 0; i < parts.length; i++) {
            String move = parts[i];

            try {
                // Parse the move and get its representation
                System.out.println("Parsing move: " + move);
                int[] position = parseMove(move);
                String moveRepresentation = copyGame.makeMoveReplay1(position[0], position[1], position[2],
                        position[3]);
                System.err.println(moveRepresentation);

                // Determine piece color and type
                boolean isWhite = moveRepresentation.contains("(White)");
                String algebMove = moveRepresentation.replaceAll(" \\(White\\)", "").replaceAll(" \\(Black\\)", "");
                char pieceLetter = moveRepresentation.charAt(0);
                String pieceName = getPieceName(pieceLetter);
                String color = isWhite ? "white-" : "black-";

                // Load image for the piece
                String imagePath = "/images/" + color + pieceName + ".png";
                System.out.print(imagePath);
                Image pieceImage = new Image(getClass().getResourceAsStream(imagePath)); // Load image
                ImageView pieceImageView = new ImageView(pieceImage); // Create ImageView
                pieceImageView.setFitHeight(30); // Set desired height
                pieceImageView.setFitWidth(30); // Set desired width
                pieceImageView.setPreserveRatio(true); // Maintain aspect ratio

                // Create Text for the move
                Text moveText = new Text(algebMove);
                moveText.setStyle("-fx-font-size: 16; -fx-font-weight: bold; -fx-fill: black; -fx-font-style: italic;");
                moveText.setWrappingWidth(100); // Set wrapping width if necessary

                // Create HBox to hold both the image and text
                HBox moveContainer = new HBox(5); // Spacing between elements
                moveContainer.setAlignment(Pos.CENTER_LEFT); // Align items in HBox
                moveContainer.getChildren().addAll(pieceImageView, moveText);

                // Add the moveContainer to the current row of moves
                movesRow.getChildren().add(moveContainer);

                // Every two moves, add the movesRow to the TextFlow and create a new HBox
                if ((i + 1) % 2 == 0) {
                    textFlow.getChildren().add(movesRow);
                    movesRow = new HBox(10); // Reset HBox for the next row
                    movesRow.setAlignment(Pos.CENTER_LEFT); // Align items in the new HBox
                }
            } catch (IndexOutOfBoundsException e) {
                System.err.println("Invalid move: " + move);
            } catch (NullPointerException e) {
                System.err.println("An error occurred while processing the move: " + move);
                e.printStackTrace(); // Print stack trace for debugging
            }
        }

        // If there are any remaining moves in the row that didn't fill a complete row
        if (!movesRow.getChildren().isEmpty()) {
            textFlow.getChildren().add(movesRow);
        }
    }

    private String getPieceName(char pieceLetter) {
        switch (pieceLetter) {
            case 'K':
                return "king";
            case 'O':
                return "king";
            case 'Q':
                return "queen";
            case 'R':
                return "rook";
            case 'B':
                return "bishop";
            case 'N':
                return "knight"; // 'N' is used for knight in chess notation
            case 'P':
                return "pawn"; // 'P' is used for pawn
            default:
                return "pawn"; // or throw an exception for an invalid piece letter
        }
    }

    private String getChessCoordinate(int row, int col) {
        char file = (char) ('a' + col); // 'a' to 'h' for columns
        int rank = 8 - row; // '8' to '1' for rows
        return "" + file + rank;
    }

    @FXML
    private void handleReplayMoves() {
        loadingBar.setVisible(true);
        currentMoveIndex = moveHistory.size() - 1;
        whiteMoveCount = new HashMap<>(); // Initialize map for White
        blackMoveCount = new HashMap<>();
        whiteMoveCategories = new ArrayList<>();
        blackMoveCategories = new ArrayList<>();

        game.setWhiteTurn(true);
        loadingBar.setVisible(true);

        // Käytetään Task-luokkaa ja Platform.runLater() -metodia
        Task<Void> replayTask = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                while (currentMoveIndex >= 0) {
                    Platform.runLater(() -> {
                        try {
                            handleRewindBack();
                        } catch (Exception e) {
                            // Käsittele mahdolliset poikkeukset
                            e.printStackTrace();
                        }
                    });
                    Thread.sleep(50); // Lisätään pieni viive

                }
                return null;
            }
        };

        replayTask.setOnSucceeded(event -> {
            loadingBar.setVisible(false);
            printMoveCountsAndAccuracy();
        });

        Thread thread = new Thread(replayTask);
        thread.setDaemon(true);
        thread.start();
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

    @FXML
    private void handleGameSelection() {
        ComboBoxItem selectedGame = gameSelector.getValue();
        int id = 0;
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

    private void drawBoard() throws IOException, InterruptedException {
        getColorForScoreChange(scoreChange);
        chessBoard.getChildren().clear();
        double squareSize = chessBoard.getPrefWidth() / 8;
        String best = "";
        String move = "";
        int[] playerMove = { -1, -1, -1, -1 };
        if (currentMoveIndex < moveHistory.size() - 1) {
            best = moveHistory.get(currentMoveIndex + 1).getBestMove();
            move = moveHistory.get(currentMoveIndex).getPlayerMove();
            playerMove = parseMove(move);
        }
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
                } else if (i == playerToRow && j == playerToCol && currentMoveIndex > 0) {
                    square.setFill(colour);
                } else {
                    square.setFill((i + j) % 2 == 0 ? lightColor : darkColor);
                }
                chessBoard.add(square, j, i);

                Piece piece = game.getBoard().getPiece(i, j);
                if (piece != null) {
                    Image pieceImage = getPieceImage(piece);
                    ImageView pieceView = new ImageView(pieceImage);
                    pieceView.setFitHeight(squareSize);
                    pieceView.setFitWidth(squareSize);
                    if (!piece.isWhite()) {
                        ColorAdjust colorAdjust = new ColorAdjust();
                        colorAdjust.setContrast(0.3);
                        // Adjust this value between -1.0 and 1.
                        pieceView.setEffect(colorAdjust);
                    } else {
                        ColorAdjust colorAdjust = new ColorAdjust();
                        colorAdjust.setContrast(0.3); // Increase contrast (adjust as needed)
                        colorAdjust.setBrightness(-0.2); // Slightly decrease brightness (adjust as needed)
                        colorAdjust.setSaturation(0); // Optional: set to 0 for grayscale effect

                        pieceView.setEffect(colorAdjust);
                    }
                    chessBoard.add(pieceView, j, i);
                }
            }
        }
        if (currentMoveIndex < moveHistory.size() - 1) {
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

    private void initializeGameComboBox() {
        int totalGames = database.getTotalGames();
        ObservableList<ComboBoxItem> gameItems = FXCollections.observableArrayList();
        for (int i = 1; i <= totalGames; i++) {
            gameItems.add(new ComboBoxItem("GAME", i));
        }
        gameSelector.setItems(gameItems);
        gameSelector.getSelectionModel().selectFirst();
    }

    @FXML
    private void deleteGames() {
        database.clearDatabase();
        initializeGameComboBox();
    }
}
