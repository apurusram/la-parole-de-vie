import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;



public class ProjectorWindow {
    private Stage stage;
    private Label verseLabel;
    private BibleApp bibleApp;
    private double fontSize;
    private double padding = 40;

    public void increaseFont() {
        fontSize += 4;
        verseLabel.setFont(new Font("Arial", fontSize));
    }

    public void decreaseFont() {
        fontSize = Math.max(12, fontSize - 4);
        verseLabel.setFont(new Font("Arial", fontSize));
    }

    public void increasePadding() {
        padding += 10;
        ((StackPane) stage.getScene().getRoot()).setPadding(new Insets(padding));
    }

    public void decreasePadding() {
        padding = Math.max(0, padding - 10);
        ((StackPane) stage.getScene().getRoot()).setPadding(new Insets(padding));
    }

    public ProjectorWindow(BibleApp app) {
        this.bibleApp = app;
        stage = new Stage();

        // Get screen dimensions (use the second screen if available)
        Screen targetScreen = Screen.getScreens().size() > 1 ? Screen.getScreens().get(1) : Screen.getPrimary();
        double screenWidth = targetScreen.getVisualBounds().getWidth();
        double screenHeight = targetScreen.getVisualBounds().getHeight();

        // Compute dynamic font size based on screen dimensions
        fontSize = Math.min(screenWidth, screenHeight) / 20;



        verseLabel = new Label();
        verseLabel.setTextFill(Color.BLACK);
        verseLabel.setFont(new Font("Arial", fontSize));
        verseLabel.setWrapText(true);
        verseLabel.setAlignment(Pos.CENTER);
        // Set the label's max width to ensure padding on the sides
        verseLabel.setMaxWidth(Double.MAX_VALUE);

        StackPane root = new StackPane();
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(40));
        root.getChildren().add(verseLabel);

        // Load the background image
        try {
            Image bgImage = new Image(new FileInputStream("resources/verse_background.jpg"));
            BackgroundImage backgroundImage = new BackgroundImage(bgImage,
                    BackgroundRepeat.NO_REPEAT,
                    BackgroundRepeat.NO_REPEAT,
                    BackgroundPosition.CENTER,
                    new BackgroundSize(BackgroundSize.AUTO, BackgroundSize.AUTO, false, false, true, true));

            root.setBackground(new Background(backgroundImage));
        } catch (FileNotFoundException e) {
            System.out.println("Background image not found.");
        }

//        Scene scene = new Scene(root, 1280, 720); // fallback size
        Scene scene = new Scene(root);
        scene.setOnKeyPressed(event -> {
            switch (event.getCode()) {
                case RIGHT -> bibleApp.goToNextVerse();
                case LEFT -> bibleApp.goToPreviousVerse();
            }
        });

        stage.setScene(scene);
        stage.setFullScreen(true);

        // If a second screen is available, move it there
        Screen secondScreen = Screen.getScreens().size() > 1 ? Screen.getScreens().get(1) : null;
        if (secondScreen != null) {
            stage.setX(secondScreen.getVisualBounds().getMinX());
            stage.setY(secondScreen.getVisualBounds().getMinY());
        }
    }

    public void show() {
        stage.show();
    }

    public void setVerseText(String text) {
        verseLabel.setText(text);
    }

    public void close() {
        stage.close();
    }
}
