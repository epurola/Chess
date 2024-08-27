package com.example;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import com.example.WebSocket.ChessWebSocketClient;
import com.example.WebSocket.ChessWebSocketServer;

import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.effect.ColorAdjust;
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
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.util.Duration;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.net.InetAddress;
import java.net.UnknownHostException;




 
    public class MultiplayerController  {
        private ChessWebSocketClient socketClient;
        private Piece selectedPiece;
        private ImageView draggedPiece;
        private Pawn pawnToPromote;
        private Label statusLabel;
        private boolean drawPossibleMoves;
        private ToggleSwitch toggleSwitch;
        private Game game;
        private String playerColor;
        private boolean isWhite;
        private Piece capturedPiece;
        private boolean isCastle;
        private boolean isMyTurn ;

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
        @FXML private HBox timer1;
        @FXML private HBox timer2;
        @FXML private Button startServerButton;
        @FXML private Button connectButton;
        @FXML private TextField serverIpField;
      
       private ChessWebSocketServer webSocketServer;
       private boolean isServerRunning = false;

     
        @FXML private VBox vbox2;
     
        @FXML private HBox WhiteHbox;
        @FXML private HBox blackHbox;
        @FXML private AnchorPane pane;
        CountdownClock countdownClock;
        CountdownClock countdownClock2;
        double screenWidth;
        double screenHeight;
        Stockfish stockfish;
        
    
       
        @FXML
        public void initialize() throws IOException {
            

                checkAndDrawBoard();
                
        
                // Initialize the drawPossibleMoves flag
                drawPossibleMoves = false;
        
                // Initialize statusLabel
                statusLabel = new Label();
                statusLabel.setTextFill(Color.BLACK);
                statusLabel.setVisible(false);
                rootPane.getChildren().add(statusLabel);
                StackPane.setAlignment(statusLabel, javafx.geometry.Pos.CENTER);
                statusLabel.getStyleClass().add("status-label");
                countdownClock = new CountdownClock(this); 
               countdownClock2 = new CountdownClock(this); 
        
              timer1.setAlignment(Pos.CENTER);
              timer1.getChildren().addAll(countdownClock);
              timer2.setAlignment(Pos.CENTER);
              timer2.getChildren().addAll(countdownClock2);
        
                // Initialize ToggleSwitch
                toggleSwitch = new ToggleSwitch(moveHelpColor);
                hbox1.getChildren().add(toggleSwitch);
                toggleSwitch.switchedOn().addListener((obs, oldState, newState) -> drawPossibleMoves = !drawPossibleMoves);
                Screen screen = Screen.getPrimary();
               Rectangle2D bounds = screen.getBounds();
               screenWidth = bounds.getWidth();
               screenHeight = bounds.getHeight();

               double chessBoardSize = screenHeight * 0.8 ;
        
                pane.setMaxSize(screenHeight, screenWidth);
                borderPane.setMaxSize(screenWidth, screenHeight);
        
                chessBoard.setPrefSize(chessBoardSize, chessBoardSize);
                rootPane.setMinSize(chessBoardSize+30, chessBoardSize+30);
                double boxWidth = (screenWidth - chessBoardSize) / 2;
                hbox1.setPrefWidth(boxWidth );
                vbox2.setPrefWidth(boxWidth);
                vbox2.setMaxHeight(screenHeight);
                vbox2.setSpacing(screenHeight/2);
                 vbox2.setMaxHeight(screenHeight);
                 serverIpField.setMaxWidth(154);
                 serverIpField.setMinHeight(38);
              
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
    
          
        }
        @FXML
        private void startServer() throws URISyntaxException, InterruptedException {
              // Get the local IP address
              String localIPAddress = getLocalIPAddress();
            if (!isServerRunning) {
                Task<Void> serverTask = new Task<Void>() {
                    @Override
                    protected Void call() throws Exception {
                      
                        // Use the local IP address for the server
                        webSocketServer = new ChessWebSocketServer(new InetSocketAddress(localIPAddress, 8887));
                        webSocketServer.start();
                        isServerRunning = true;
                        return null;
                    }
                };
    
                new Thread(serverTask).start();
            }
            URI serverUri = new URI("ws://" + localIPAddress + ":8887");
            socketClient = new ChessWebSocketClient(serverUri);
            socketClient.connectBlocking();
            socketClient.setController(this);
        }
    
        private String getLocalIPAddress() {
            try {
                InetAddress inetAddress = InetAddress.getLocalHost();
                return inetAddress.getHostAddress();
            } catch (UnknownHostException e) {
                e.printStackTrace();
                return "localhost"; // Fallback to localhost if IP address cannot be determined
            }
        }
    @FXML
    private void connectToServer() {
        String serverIp = serverIpField.getText();
        if (serverIp != null && !serverIp.isEmpty()) {
            try {
                URI serverUri = new URI("ws://" + serverIp + ":8887");
                socketClient = new ChessWebSocketClient(serverUri);
                socketClient.connect();
                socketClient.setController(this);
            } catch (URISyntaxException e) {
                e.printStackTrace(); 
            } catch (Exception e) {
                e.printStackTrace(); 
            }
        }
    }

  
        private void checkAndDrawBoard() throws IOException {
            if (playerColor == null) {
                System.out.println("Player color is not set yet.");
                // Schedule a re-check after a delay
                PauseTransition delay = new PauseTransition(Duration.seconds(1));
                delay.setOnFinished(event -> {
                    try {
                        checkAndDrawBoard();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }); // Recursive call after delay
                delay.play();
            } else {
                if(playerColor.equals("white"))
                {
                    isWhite = true;
                    isMyTurn = true;
                    game = new Game();
                    stockfish = new Stockfish(game);
                }
                else
                {
                    isWhite = false;
                    isCastle = false;
                    game = new Game( isWhite);
                    stockfish = new Stockfish(game);
                }
                drawBoard();
            }
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
            SoundManager.playMoveSound();
            drawBoard();
        }
    
         private void drawBoard() {
            if (playerColor == null) {
                System.out.println("Player color is not set yet.");
                return; // Exit if player color is not available
            }
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
                            handlePieceDrop(event);
                        });
                    }
                }
            }     
        }
    
        private Image getPieceImage(Piece piece) {
            String color = piece.isWhite() ? "white-" : "black-";
           /* if(playerColor.equals("black"))
            {
                color = piece.isWhite() ? "black-" : "white-";
            }*/ 
            String imagePath = "/images/" + color + piece.getClass().getSimpleName().toLowerCase() + ".png";
            try {
                return new Image(getClass().getResourceAsStream(imagePath));
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
    
        private void handlePieceDragStart(MouseEvent event, ImageView pieceView, Piece piece) {

            if(!isMyTurn)
            {
                return;
            }
            selectedPiece = piece;
            draggedPiece = pieceView;
           
            if (drawPossibleMoves) {
                drawPossibleMoves(selectedPiece);
            }
            pieceView.toFront();
        }
    
        private void handlePieceDrag(MouseEvent event) {
            if(!isMyTurn)
            {
                return;
            }
            
            if (draggedPiece != null) {
                draggedPiece.setTranslateX(event.getSceneX() - 50 - draggedPiece.getLayoutX() - chessBoard.localToScene(0, 0).getX());
                draggedPiece.setTranslateY(event.getSceneY() - 50 - draggedPiece.getLayoutY() - chessBoard.localToScene(0, 0).getY());
            }
        }
        private final ExecutorService analysisExecutor = Executors.newSingleThreadExecutor();
        private void startGameAnalysis(int fromRow, int fromCol, int toRow, int toCol, String fen) {
            analysisExecutor.submit(() -> {
                try {
                    String moveString = rowColToAlgebraic(fromRow, fromCol) + rowColToAlgebraic(toRow, toCol);
                    stockfish.getBestMoveFromFEN(fen);
                    String analysis = stockfish.analyzeMove(fen, moveString);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }
        public String rowColToAlgebraic(int row, int col) {
            // Convert column index to column character ('a' to 'h')
            char columnChar = (char) ('a' + col);
            // Convert row index to row character ('1' to '8')
            char rowChar = (char) ('1' + (7 - row)); // Adjust for 0-based indexing
            // Combine column and row characters to form the algebraic notation
            return "" + columnChar + rowChar;
        }
        
        private void triggerBestMoveAnalysis() {
          
            new Thread(() -> {
                try {
                    // Analyze the best move using Stockfish
                  //  int[] bestMove = parseMove(stockfish.getBestMove());
                   /* Platform.runLater(() -> {
                        drawBestMoveIndicators(bestMove[0], bestMove[1], bestMove[2], bestMove[3]);  
                    });*/ 
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }).start();
       
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
  
      
        private void handlePieceDrop(MouseEvent event) {
            if(!isMyTurn)
            {
                return;
            }
            double squareSize = chessBoard.getPrefWidth() / 8;
            String fen = game.toFen();
            if (draggedPiece != null) {
                int row = (int) ((event.getSceneY() - chessBoard.localToScene(0, 0).getY()) / squareSize);
                int col = (int) ((event.getSceneX() - chessBoard.localToScene(0, 0).getX()) / squareSize);
                if (row >= 0 && row < 8 && col >= 0 && col < 8) 
                {
                capturedPiece = game.getPiece(row, col);
                boolean validMove = game.makeMove(selectedPiece.getRow(), selectedPiece.getCol(), row, col);
                if (!validMove)
                {
                  SoundManager.playNotifySound();
                }
               
                if (validMove) {
                    boolean soundPlayed = false;
                    isMyTurn = false;
                    triggerBestMoveAnalysis();
                    startGameAnalysis(selectedPiece.getRow(),selectedPiece.getCol(),row, col,fen);

                    switchPlayer();
                   
                    // Check for castling first
                    if (selectedPiece instanceof King && Math.abs(col - selectedPiece.getCol()) == 2) {
                        SoundManager.playCastleSound();
                        isCastle = true;
                        if(selectedPiece.getCol()> col)
                        {
                            capturedPiece = new Rook(selectedPiece.getRow(), col+1, game.isWhiteTurn());
                        }
                        else{
                            capturedPiece = new Rook(selectedPiece.getRow(), col-1, game.isWhiteTurn());
                        }
                        
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
                 
                      Move lastmove = game.getLastMove();
                      //This causes issue where when two pawn are next to each other both get eaten.
                        if(lastmove.getToRow() != lastmove.getCapturePieceRow() && game.isEnPassant(row, col))
                        {
                           capturedPiece =  new Pawn(selectedPiece.getRow(), col, game.isWhiteTurn());
                        }
                    }
                
                    // Play sound based on capture, check, or move
                    if (!soundPlayed) {
                        if (capturedPiece != null) {
                            SoundManager.playCaptureSound();
                        } else if (game.isInCheck(game.isWhiteTurn())) {
                            SoundManager.playCheckSound();
                        } else {
                            SoundManager.playMoveSound();
                        }
                    }
            
                        socketClient.sendMove(
                            selectedPiece.getClass().getSimpleName(), 
                            inverRow(selectedPiece.getRow()),
                            selectedPiece.getCol(),
                            inverRow(row),
                            col,
                            capturedPiece != null ? inverRow(capturedPiece.getRow()) : inverRow(row), 
                            capturedPiece != null ? capturedPiece.getCol() : col, 
                            isWhite,
                            capturedPiece != null ? capturedPiece.getClass().getSimpleName() : "null",
                            isCastle
                        );
                     
                       
                        
                   
                }
                
            
                } else{
                    SoundManager.playNotifySound();
                }
            }
                drawBoard();
                if (game.checkMate(game)) {
                        SoundManager.playWinSound();
                        // Handle game end in a separate thread
                        ExecutorServiceManager.getExecutorService().submit(() -> {
                        
                                stockfish.handleGameEnd(fen);
                        });
                
                        // Display confetti and update UI on the JavaFX thread
                        Platform.runLater(() -> {
                        
                                displayConfetti(chessBoard);
                                statusLabel.setText("Checkmate!");
                                statusLabel.setVisible(true);
                                countdownClock.stop();
                                countdownClock2.stop();
                        });
                }
                if(game.checkDraw(game))
                {
                    statusLabel.setText("Draw");
                    statusLabel.setVisible(true);
                    SoundManager.playDrawSound();
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
                SoundManager.playButtonSound();
                selectedPiece = newPiece;
                game.setPiece(row, col, newPiece);
                System.out.println("Pawn promoted to " + choice + ".");
                pawnToPromote = null; // Reset pawnToPromote after promotion
                drawBoard();  // Redraw the board to update the piece's position
                exitButton.setMouseTransparent(false); 
                exitButton1.setMouseTransparent(false);
                fullScreenButton.setMouseTransparent(false);
                resetButton.setMouseTransparent(false);
                undoButton.setMouseTransparent(false);
                socketClient.sendMove(
                    selectedPiece.getClass().getSimpleName(), 
                    selectedPiece.getRow(),
                    selectedPiece.getCol(),
                    row,
                    col,
                    capturedPiece != null ? capturedPiece.getRow() : row, 
                    capturedPiece != null ? capturedPiece.getCol() : col, 
                    isWhite,
                    capturedPiece != null ? capturedPiece.getClass().getSimpleName() : "null",
                    isCastle
                );
                
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
        private void displayConfetti(Pane pane) {
            double paneWidth = pane.getWidth();
            double paneHeight = pane.getHeight();
        
            for (int i = 0; i < 800; i++) { // Number of confetti pieces
                Confetti confetti = new Confetti(Color.hsb(Math.random() * 360, 1.0, 1.0), paneWidth*2, paneHeight*2);
                pane.getChildren().add(confetti);
                confetti.animate();
            }
        }

        public void updateGameState(String pieceName, int fromRow, int fromCol, int movedRow,int movedCol, int capturedPieceRow, int capturedPieceCol,
             boolean isWhiteTurn, String capturedPiece, boolean isCastle) {
                
            int row = fromRow;
            int col = fromCol;
            int mRow = movedRow;
            int mCol = movedCol;
            int tRow = capturedPieceRow;
            int tCol = capturedPieceCol;
            boolean isWhite = isWhiteTurn;
            boolean castle = isCastle;
            boolean capture = false;
            game.setPiece(row, col, null);
            if(castle)
            {
                game.setPiece(capturedPieceRow, capturedPieceCol,getPieceFromString(capturedPiece, tRow, tCol, isWhite));
                if(capturedPieceCol == 3 )
                {
                    game.setPiece(movedRow, 0, null);
                }
                else
                {
                    game.setPiece(movedRow, 7, null);
                }
            }
            else{
                game.setPiece(capturedPieceRow, capturedPieceCol,null);
            }
            
            game.setPiece(mRow,mCol, getPieceFromString(pieceName, tRow, tCol, isWhite));

            game.recordMove(row, col, mRow, mCol,
            getPieceFromString(capturedPiece, tRow, tCol, isWhite),
            getPieceFromString(pieceName, mRow, mCol, isWhite),
            tRow,
            tCol,
           game.getBoard().toFEN(isWhite) );//Fix this later
            isMyTurn = true;
            
            game.setWhiteTurn(!game.isWhiteTurn());
            switchPlayer();
            if(!capturedPiece.equals("null"))
            {
                capture = true;
            }
            if (capture) {
                    SoundManager.playCaptureSound();
                } else if (game.isInCheck(game.isWhiteTurn())) {
                    SoundManager.playCheckSound();
                } else if(castle){
                    SoundManager.playCastleSound();
                }
                else{
                    SoundManager.playMoveSound();
                }
                String fen = game.toFen();

             Platform.runLater(() -> drawBoard());
             if (game.checkMate(game)) {
                SoundManager.playWinSound();
                // Handle game end in a separate thread
                ExecutorServiceManager.getExecutorService().submit(() -> {
                
                        stockfish.handleGameEnd(fen);
                });
        
                // Display confetti and update UI on the JavaFX thread
                Platform.runLater(() -> {
                
                        displayConfetti(chessBoard);
                        statusLabel.setText("Checkmate!");
                        statusLabel.setVisible(true);
                        countdownClock.stop();
                        countdownClock2.stop();
                });
        }
        if(game.checkDraw(game))
        {
            statusLabel.setText("Draw");
            statusLabel.setVisible(true);
            SoundManager.playDrawSound();
        }
        }
        //This basically only moves black pieces since they are set in the top row in the game, But the board is
        //drawn  so that both players have their pieces at the bottom. 
        //The rows are inverted when sending the moves
        private Piece getPieceFromString(String piece,int row,int col,boolean isWhite) {
           // isWhite = false;
            if (piece.contains("Rook")) return new Rook(row, col, isWhite);
            if (piece.contains("Bishop")) return new Bishop(row, col, isWhite);
            if (piece.contains("Knight")) return new Knight(row, col, isWhite);
            if (piece.contains("Queen")) return new Queen(row, col, isWhite);
            if (piece.contains("Pawn")) return new Pawn(row, col, isWhite);
            if (piece.contains("King")) return new King(row, col, isWhite);
            return null;
        }
    

        public void setPlayerColor(String color) {
            this.playerColor = color;
        }
        //Used for converting black piece movements
        private int inverRow(int row)
        {
            return Math.abs(row-7);
        }
               // Call this method when a player makes a move
    private void switchPlayer() {
        if(playerColor.equals("black"))
        {
          if (game.isWhiteTurn() ) {
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
             
          } else {
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
          }
        
        }
        else
        {
           if (game.isWhiteTurn() ) {
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
         
      }
      public void onTimeOut() {
          if (game.isWhiteTurn()) {
              statusLabel.setText("Black Wins by Timeout!");
              displayConfetti(chessBoard);
              countdownClock.stop();
              countdownClock2.stop();
          } else {
              statusLabel.setText("White Wins by Timeout!");
              countdownClock.stop();
              countdownClock2.stop();
              displayConfetti(chessBoard);
          }
          statusLabel.setVisible(true);
          SoundManager.playWinSound();
      }
    }
    
