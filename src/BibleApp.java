import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BibleApp extends Application {

    private Bible bible;

    private TextField searchField = new TextField();

    private ComboBox<Book> bookComboBox = new ComboBox<>();
    private ComboBox<Chapter> chapterComboBox = new ComboBox<>();
    private ComboBox<Verse> verseComboBox = new ComboBox<>();
    private TextArea verseTextArea = new TextArea();
    private ProjectorWindow projectorWindow;
    private int currentVerseIndex = -1;
    private Chapter currentChapter;
    private HBox rootLayout;




    public void goToNextVerse() {
        if (currentChapter != null && currentVerseIndex < currentChapter.getVerses().size() - 1) {
            currentVerseIndex++;
            Verse verse = currentChapter.getVerses().get(currentVerseIndex);
            verseComboBox.setValue(verse);
            verseTextArea.setText(verse.getText());

            String reference = String.format("%s %d.%d - LSG",
                    bookComboBox.getValue().getName(),
                    currentChapter.getNumber(),
                    verse.getNumber());
            projectorWindow.setVerseText(reference + "\n\n" + verse.getText());
        }
    }


    public void goToPreviousVerse() {
        if (currentChapter != null && currentVerseIndex > 0) {
            currentVerseIndex--;
            Verse verse = currentChapter.getVerses().get(currentVerseIndex);
            verseComboBox.setValue(verse);
            verseTextArea.setText(verse.getText());

            String reference = String.format("%s %d.%d - LSG",
                    bookComboBox.getValue().getName(),
                    currentChapter.getNumber(),
                    verse.getNumber());
            projectorWindow.setVerseText(reference + "\n\n" + verse.getText());
        }
    }

    private void performSearch(String query) {
        if (query == null || query.trim().isEmpty()) return;

        // Try to extract parts: "Gen 1.1", "Genese 1:1", etc.
        Pattern pattern = Pattern.compile("([\\d]?[a-zA-ZéÉûÛêÊèÈçÇäÄôÔîÎâÂ]+)\\s*(\\d+)[.:]?(\\d+)?");
        Matcher matcher = pattern.matcher(query.trim());

        if (matcher.matches()) {
            String bookInput = matcher.group(1).toLowerCase();
            int chapterNum = Integer.parseInt(matcher.group(2));
            int verseNum = matcher.group(3) != null ? Integer.parseInt(matcher.group(3)) : 1;

            // Match the book (by name or abbreviation)
            for (Book book : bible.getBooks()) {
                String name = book.getName().toLowerCase();
                String shortName = book.getShortName().toLowerCase();
                if (name.startsWith(bookInput) || shortName.startsWith(bookInput)) {
                    // Select the book
                    bookComboBox.setValue(book);
                    chapterComboBox.getSelectionModel().select(chapterNum - 1);

                    currentChapter = book.getChapters().get(chapterNum - 1);

                    Verse verse = currentChapter.getVerses().get(verseNum - 1);
                    verseComboBox.getSelectionModel().select(verse);

                    currentVerseIndex = verseNum - 1;
                    verseTextArea.setText(verse.getText());

                    String reference = String.format("%s %d.%d - LSG", book.getName(), chapterNum, verseNum);
                    projectorWindow.setVerseText(reference + "\n\n" + verse.getText());
                    return;
                }
            }
        } else {
            // Optional: show error if parsing fails
            verseTextArea.setText("Référence invalide. Essayez : Genèse 1.1 ou Gn 1:1");
        }
    }



    @Override
    public void start(Stage primaryStage) {
        // Load Bible
        File xmlFile = new File("resources/french_bible.xml");
        bible = BibleLoader.loadBible(xmlFile);

        TableView<Book> bookTable = new TableView<>();
        TableColumn<Book, String> nameCol = new TableColumn<>("Book");
        TableColumn<Book, String> shortCol = new TableColumn<>("Code");

        nameCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getName()));
        shortCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getShortName()));

        bookTable.getColumns().addAll(nameCol, shortCol);
        bookTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        bookTable.getItems().addAll(bible.getBooks());
        bookTable.setPrefWidth(200); // adjust as needed





        projectorWindow = new ProjectorWindow(this);
//        projectorWindow.show();


        // Fill books dropdown
        bookComboBox.getItems().addAll(bible.getBooks());
        bookComboBox.setPromptText("Select Book");

        bookComboBox.setOnAction(e -> {
            Book selectedBook = bookComboBox.getValue();
            if (selectedBook != null) {
                chapterComboBox.getItems().clear();
                chapterComboBox.getItems().addAll(selectedBook.getChapters());
                chapterComboBox.setDisable(false);
                verseComboBox.getItems().clear();
                verseComboBox.setDisable(true);
                verseTextArea.clear();
            }
        });

        chapterComboBox.setPromptText("Select Chapter");
        chapterComboBox.setDisable(true);
        chapterComboBox.setOnAction(e -> {
            currentChapter = chapterComboBox.getValue();
            if (currentChapter != null) {
                verseComboBox.getItems().clear();
                verseComboBox.getItems().addAll(currentChapter.getVerses());
                verseComboBox.setDisable(false);
                verseTextArea.clear();
                currentVerseIndex = -1; // reset index
            }
        });


        verseComboBox.setPromptText("Select Verse");
        verseComboBox.setDisable(true);
        verseComboBox.setOnAction(e -> {
            Verse selectedVerse = verseComboBox.getValue();
            if (selectedVerse != null) {
                verseTextArea.setText(selectedVerse.getText());

                if (currentChapter != null) {
                    currentVerseIndex = currentChapter.getVerses().indexOf(selectedVerse);
                    String reference = String.format("%s %d.%d - LSG",
                    bookComboBox.getValue().getName(),
                    chapterComboBox.getValue().getNumber(),
                    selectedVerse.getNumber());

                    String fullText = reference + "\n\n" + selectedVerse.getText();
                    projectorWindow.setVerseText(fullText);

                }
            }
        });

        Button startProjectionButton = new Button("Start Projection");
        Button stopProjectionButton = new Button("Stop Projection");

        startProjectionButton.setOnAction(e -> projectorWindow.show());
        stopProjectionButton.setOnAction(e -> projectorWindow.close());

        Button exitButton = new Button("Close");
        exitButton.setOnAction(e -> {
            projectorWindow.close();  // Close projector if it's open
            primaryStage.close();     // Close main app window
        });

        startProjectionButton.setPrefWidth(150);
        stopProjectionButton.setPrefWidth(150);
        exitButton.setPrefWidth(150);

        startProjectionButton.setStyle("-fx-font-size: 14px; -fx-background-color: #008000; -fx-text-fill: white;");
        stopProjectionButton.setStyle("-fx-font-size: 14px; -fx-background-color: #e74c3c; -fx-text-fill: white;");
        exitButton.setStyle("-fx-font-size: 14px; -fx-background-color: #000; -fx-text-fill: white;");

        searchField.setPromptText("Enter book chapter.verse (e.g., Genèse 1.1)");
        searchField.setOnAction(e -> performSearch(searchField.getText()));



        Button increaseFontButton = new Button("+ FontSize");
        Button decreaseFontButton = new Button("- FontSize");
        Button increasePaddingButton = new Button("+ Padding");
        Button decreasePaddingButton = new Button("- Padding");

        increaseFontButton.setOnAction(e -> projectorWindow.increaseFont());
        decreaseFontButton.setOnAction(e -> projectorWindow.decreaseFont());
        increasePaddingButton.setOnAction(e -> projectorWindow.increasePadding());
        decreasePaddingButton.setOnAction(e -> projectorWindow.decreasePadding());

        bookComboBox.setPrefWidth(150);
        chapterComboBox.setPrefWidth(150);
        verseComboBox.setPrefWidth(150);

        HBox dropdownBox = new HBox(10, bookComboBox, chapterComboBox, verseComboBox);
        dropdownBox.setAlignment(Pos.CENTER);

        HBox controlsBox = new HBox(10, increaseFontButton, decreaseFontButton, increasePaddingButton, decreasePaddingButton);
        controlsBox.setStyle("-fx-padding: 10; -fx-alignment: center;");

        HBox actionButtons = new HBox(10, startProjectionButton, stopProjectionButton, exitButton);
        actionButtons.setAlignment(Pos.CENTER);








        // Make verses read-only
        verseTextArea.setWrapText(true);
        verseTextArea.setEditable(false);

        VBox rightPane = new VBox(10, new Label("Books & Codes"), bookTable);
        rightPane.setStyle("-fx-padding: 10; -fx-background-color: #f4f4f4;");
        rightPane.setPrefWidth(220);

        ToggleButton toggleBookPaneBtn = new ToggleButton("Show Book Codes");
        toggleBookPaneBtn.setOnAction(e -> {
            if (toggleBookPaneBtn.isSelected()) {
                toggleBookPaneBtn.setText("Hide Book Codes");
                if (!rootLayout.getChildren().contains(rightPane)) {
                    rootLayout.getChildren().add(rightPane);
                }
            } else {
                toggleBookPaneBtn.setText("Show Book Codes");
                rootLayout.getChildren().remove(rightPane);
            }
        });

        VBox leftPane = new VBox(10,
                toggleBookPaneBtn,
                searchField,
                dropdownBox,
                verseTextArea,
                controlsBox,
                actionButtons
        );
        leftPane.setStyle("-fx-padding: 20;");

        rootLayout = new HBox(10, leftPane);
        Scene scene = new Scene(rootLayout, 750, 500);

        scene.addEventFilter(javafx.scene.input.KeyEvent.KEY_PRESSED, event -> {
            if (currentChapter != null) {
                switch (event.getCode()) {
                    case RIGHT:
                        goToNextVerse();
                        event.consume();
                        break;
                    case LEFT:
                        goToPreviousVerse();
                        event.consume();
                        break;
                }
            }
        });







        primaryStage.setTitle("Parole de Vie");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
