package com.example;

import java.util.List;
import java.util.function.Consumer;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

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
        Rectangle background = new Rectangle(520, 120);
        background.setArcHeight(15);
        background.setArcWidth(15);
        background.setFill(Color.WHITE);
        background.setStroke(Color.LIGHTGRAY);

        // Create an HBox to hold the images
        HBox itemsContainer = new HBox(); // Spacing between images
        itemsContainer.setPrefWidth(background.getWidth());
        itemsContainer.setPrefHeight(background.getHeight());
        itemsContainer.setSpacing(35);
        itemsContainer.setAlignment(Pos.CENTER);


        // Add images to the HBox
        for (String imagePath : imagePaths) {
            // Load the image using the imagePath from the list
            Image image = new Image(getClass().getResourceAsStream(imagePath));

            // Create an ImageView for the loaded image
            ImageView imageView = new ImageView(image);
            imageView.setFitHeight(100); 
            imageView.setFitWidth(100);
            

            // Set up event handlers
            imageView.setOnMouseClicked(e -> handleChoice(imagePath)); // Handle choice when image is clicked
            imageView.setOnMouseEntered(e -> {
                imageView.setStyle("-fx-border-color: #7B61FF; -fx-border-width: 3;");
                imageView.setOpacity(0.9); // Change cursor to hand on hover
                imageView.setFitHeight(110);
                imageView.setFitWidth(110);
            });

            imageView.setOnMouseExited(e -> {
                imageView.setEffect(null); // Remove shadow effect
                imageView.setStyle("-fx-border-color: transparent; -fx-border-width: 3;");
                imageView.setOpacity(1); // Revert cursor on exit
                imageView.setFitHeight(100);
                imageView.setFitWidth(100);
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

