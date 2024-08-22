package com.example;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Rectangle2D;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ComboBoxBase;
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
import javafx.stage.Screen;
import javafx.stage.Stage;



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
   

    // Constructor that initializes the board with move history
    @FXML
    
    public void  initialize() {
        moveHistory = new ArrayList<>();
        database = new Database("jdbc:sqlite:move_analysis.db");
        int totalGames = database.getTotalGames();
        moveHistory = database.getMoveAnalysis(totalGames);
       
        Screen screen = Screen.getPrimary();
            Rectangle2D bounds = screen.getBounds();
             screenWidth = bounds.getWidth();
             screenHeight = bounds.getHeight();

            double chessBoardSize = screenHeight * 0.8 ;
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
          
            // Enable fill height for HBoxes to stretch vertically if needed
            WhiteHbox.setFillHeight(true);
            blackHbox.setFillHeight(true);
      
        board = new Board();
        board.clearBoard();
        if (!moveHistory.isEmpty()) {
           
            board.setFEN(moveHistory.get(0).getFEN());  // Set the board to the initial position

            initializeGameComboBox();
           
            loadGame(totalGames);
        }
    }
    @FXML
    private void handleGameSelection() {
        ComboBoxItem selectedGame = gameSelector.getValue();
       
        int id = selectedGame.getValue();
        loadGame(id);
    }

    private void loadGame(int gameId) {
        // Retrieve move history for the selected game
        moveHistory = database.getMoveAnalysis(gameId);

        if (!moveHistory.isEmpty()) {
            currentMoveIndex = 0;
            board.clearBoard();
            board.setFEN(moveHistory.get(0).getFEN());  // Set the board to the initial position
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
   

    private void drawBoard() {
        chessBoard.getChildren().clear();
        double squareSize = chessBoard.getPrefWidth() / 8;
        int[] playerMove={0,0,0,0};
    
        // Coordinates of the best move and the opponent's move
        int[] bestMove = parseMove(moveHistory.get(currentMoveIndex).getBestMove());
        if(currentMoveIndex > 0)
        {
             playerMove = parseMove(moveHistory.get(currentMoveIndex-1).getPlayerMove());
        }
       
    
        int bestFromRow = bestMove[0];
        int bestFromCol = bestMove[1];
        int bestToRow = bestMove[2];
        int bestToCol = bestMove[3];
    
        int playerFromRow = playerMove[0];
        int playerFromCol = playerMove[1];
        int playerToRow = playerMove[2];
        int playerToCol = playerMove[3];
    
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
                    square.setFill(Color.RED.darker()); // From square for player's move
                } else if (i == playerToRow && j == playerToCol && currentMoveIndex > 0) {
                    square.setFill(Color.RED); // To square for player's move
                }
                // Regular board colors
                else {
                    square.setFill((i + j) % 2 == 0 ? lightColor : darkColor);
                }
    
                chessBoard.add(square, j, i);
    
                Piece piece = board.getPiece(i, j);
                if (piece != null) {
                    Image pieceImage = getPieceImage(piece);
                    ImageView pieceView = new ImageView(pieceImage);
                    pieceView.setFitHeight(squareSize);
                    pieceView.setFitWidth(squareSize);
                    chessBoard.add(pieceView, j, i);
    
                    pieceView.setOnMousePressed(event -> handlePieceDragStart(event, pieceView, piece));
                    pieceView.setOnMouseDragged(this::handlePieceDrag);
                    pieceView.setOnMouseReleased(this::handlePieceDrop);
                }
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

    private void handlePieceDragStart(MouseEvent event, ImageView pieceView, Piece piece) {
        selectedPiece = piece;
        draggedPiece = pieceView;
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
                board.setPiece(row, col, selectedPiece);
                board.setPiece(selectedPiece.getRow(), selectedPiece.getCol(), null);
                drawBoard();  // Redraw the board after the move
            }
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
            board.clearBoard();
            board.setFEN(moveAnalysis.getFEN());
          
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
}

