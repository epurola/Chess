package com.example;



import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.text.Text;
import javafx.stage.Popup;
import javafx.stage.Stage;

public class PopupController {
   @FXML
    private Text whiteMovesText;
    @FXML
    private Text whiteAccuracyText;
    @FXML
    private Button closeButton;
    
    @FXML
    private Label whiteBrilliantCount;
    @FXML
    private Label whiteGoodCount;
    @FXML
    private Label whiteBestCount;
    @FXML
    private Label whiteSlightImprovementCount;
    @FXML
    private Label whiteInaccuracyCount;
    @FXML
    private Label whiteMistakeCount;
    @FXML
    private Label whiteBlunderCount;
    @FXML
    private Text blackMovesText;
    @FXML
    private Text blackAccuracyText;
    @FXML
    private Label blackBrilliantCount;
    @FXML
    private Label blackGoodCount;
    @FXML
    private Label blackBestCount;
    @FXML
    private Label blackSlightImprovementCount;
    @FXML
    private Label blackInaccuracyCount;
    @FXML
    private Label blackMistakeCount;
    @FXML
    private Label blackBlunderCount;
    
    private Popup popup; // Reference to the Popup

    // Method to set the Popup reference
    public void setPopup(Popup popup) {
        this.popup = popup;
    }

    // Method to update the popup content
    public void updateMoveCounts(int whiteTotalMoves, double whiteAccuracy,
                                  int whiteBrilliant, int whiteGood, int whiteBest, int whiteSlightImprovement,
                                  int whiteInaccuracy, int whiteMistake, int whiteBlunder,
                                  int blackTotalMoves, double blackAccuracy,
                                  int blackBrilliant, int blackGood, int blackBest, int blackSlightImprovement,
                                  int blackInaccuracy, int blackMistake, int blackBlunder) {

        // Update White's total moves and accuracy
        whiteMovesText.setText("White's Total Moves: " + whiteTotalMoves);
        whiteAccuracyText.setText(String.format("Accuracy: %.2f%%", whiteAccuracy));
        whiteBrilliantCount.setText(String.valueOf(whiteBrilliant));
        whiteGoodCount.setText(String.valueOf(whiteGood));
        whiteBestCount.setText(String.valueOf(whiteBest));
        whiteSlightImprovementCount.setText(String.valueOf(whiteSlightImprovement));
        whiteInaccuracyCount.setText(String.valueOf(whiteInaccuracy));
        whiteMistakeCount.setText(String.valueOf(whiteMistake));
        whiteBlunderCount.setText(String.valueOf(whiteBlunder));

        // Update Black's total moves and accuracy
        blackMovesText.setText("Black's Total Moves: " + blackTotalMoves);
        blackAccuracyText.setText(String.format("Accuracy: %.2f%%", blackAccuracy));
        blackBrilliantCount.setText(String.valueOf(blackBrilliant));
        blackGoodCount.setText(String.valueOf(blackGood));
        blackBestCount.setText(String.valueOf(blackBest));
        blackSlightImprovementCount.setText(String.valueOf(blackSlightImprovement));
        blackInaccuracyCount.setText(String.valueOf(blackInaccuracy));
        blackMistakeCount.setText(String.valueOf(blackMistake));
        blackBlunderCount.setText(String.valueOf(blackBlunder));
    }

    // Handle the close button action
    @FXML
private void handleCloseButton() {
    // Close the popup stage
    Stage stage = (Stage) closeButton.getScene().getWindow(); // Get the current window (popup)
    stage.close(); // Close the popup
}
}
