package flash.cards;

import com.opencsv.CSVWriter;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.*;
import java.util.*;

public class Controller {
    @FXML private Label deckLabel;
    @FXML private Label timerLabel;
    @FXML private Label currentLabel;

    @FXML private Button showButton;
    @FXML private Button prevButton;
    @FXML private Button nextButton;

    @FXML private TextArea questionText;
    @FXML private TextArea answerText;

    @FXML private MenuItem editMenuItem;
    @FXML private MenuItem saveMenuItem;
    @FXML private MenuItem timerMenuItem;

    Stage primaryStage;
    Timer timer;

    final FileChooser fileChooser = new FileChooser();
    final FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("TXT files (*.txt)", "*.txt");

    private final List<Card> cards = new ArrayList<>();
    private final List<String> timerLog = new ArrayList<>();
    String filename = null;
    int current = -1;
    boolean shown = false;
    int time = -1;


    @FXML
    private void initialize() {
        fileChooser.getExtensionFilters().add(extFilter);
    }

    @FXML
    private void aboutApp() {
        createInfoAlert("About", "This app was created by Vansh Jain.\nGitHub: github.com/VJ1224");
    }

    @FXML
    private void quit() {
        primaryStage.close();
        System.exit(0);
    }

    @FXML
    private void clearDeck() {
        clearBox();
        cards.clear();
        filename = null;
        deckLabel.setText("-");
    }

    @FXML
    private void openDeck() {
        File file = fileChooser.showOpenDialog(primaryStage);

        if (file == null) return;

        clearDeck();
        filename = file.getAbsolutePath();
        deckLabel.setText(removeExt(file.getName()));

        Thread thread = new Thread(this::readCards);
        thread.start();

        try {
            thread.join();
            nextCard();
        } catch (InterruptedException exception) {
            exception.printStackTrace();
        }
    }

    @FXML
    private void newCard() {
        Dialog<Card> dialog = createCardDialog("New Card");

        Optional<Card> result = dialog.showAndWait();

        result.ifPresent(this::saveCard);
    }

    @FXML
    private void editCard() {
        if (current == -1) {
            createInfoAlert("No Card", "No card selected.");
            return;
        }

        setCardEditable(true);
    }

    @FXML
    private void saveCard() {
        Card card = cards.get(current);
        String oldString = card.toString();
        card.setQuestion(questionText.getText());
        card.setAnswer(answerText.getText());
        setCard(current);

        setCardEditable(false);

        new Thread(() -> replaceLine(oldString, card.toString())).start();
    }

    @FXML
    private void deleteCard() {
        if (current == -1) {
            createInfoAlert("No Card", "No card selected.");
            return;
        }

        Card card = cards.get(current);
        String lineToRemove = card.toString();

        new Thread(() -> deleteLine(lineToRemove)).start();

        cards.remove(current);

        if (!cards.isEmpty()) setCard(current - 1);
        else clearBox();
    }

    @FXML
    private void shuffleDeck() {
        if (cards.isEmpty()) {
            createInfoAlert("Empty Deck", "No deck loaded.");
            return;
        }

        Collections.shuffle(cards);
        setCard(0);
    }

    @FXML
    void nextCard() {
        if (cards.isEmpty()) {
            createInfoAlert("Empty Deck", "No deck loaded.");
            return;
        }

        if (cards.size() > current + 1) {
            setCard(current + 1);
        } else {
            setCard(0);
        }
    }

    @FXML
    void prevCard() {
        if (cards.isEmpty()) {
            createInfoAlert("Empty Deck", "No deck loaded.");
            return;
        }

        if (current - 1 >= 0) {
            setCard(current - 1);
        } else {
            setCard(cards.size() - 1);
        }
    }

    @FXML
    private void goToCard() {
        Dialog<Card> dialog = new Dialog<>();
        dialog.setTitle("Go to Card");
        dialog.setResizable(false);
        dialog.initOwner(primaryStage);

        Label numberLabel = new Label("Card No: ");

        TextField numberInput = new TextField();

        GridPane grid = new GridPane();
        grid.add(numberLabel, 1, 1);
        grid.add(numberInput, 2, 1);
        dialog.getDialogPane().setContent(grid);

        ButtonType goButton = new ButtonType("Go");
        dialog.getDialogPane().getButtonTypes().add(goButton);

        dialog.setResultConverter(button -> {
            if (button == goButton) {
                int index;
                try {
                    index = Integer.parseInt(numberInput.getText()) - 1;
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                    return null;
                }

                if (index >= 0 && index < cards.size()) {
                    setCard(index);
                    return null;
                }

                createInfoAlert("Out of Bounds", "Index out of bounds");
            }

            return null;
        });

        dialog.showAndWait();
    }

    private void setCard(int index) {
        if (index == -1) index = current;
        else current = index;

        Card card = cards.get(index);
        questionText.setText(card.getQuestion());
        answerText.clear();
        showButton.setText("Show");
        currentLabel.setText((current + 1) + " / " + cards.size());
    }

    @FXML
    void showAnswer() {
        if (questionText.getText().isEmpty()) return;

        if (shown) {
            answerText.clear();
            showButton.setText("Show");
        } else {
            Card card = cards.get(current);
            answerText.setText(card.getAnswer());
            showButton.setText("Hide");
        }

        shown = !shown;
    }

    @FXML
    private void setTimer() {
        if (time != -1) {
            time = -1;

            timerLog.add(deckLabel.getText() + ": " + timerLabel.getText());
            timerLabel.setText("00:00:00");
            timerMenuItem.setText("Start Timer");

            timer.cancel();
            timer.purge();
        } else {
            timerMenuItem.setText("Stop Timer");

            timer = new Timer();
            timer.scheduleAtFixedRate(new TimerTask() {
                public void run() {
                    Platform.runLater(() -> {
                        time++;
                        timerLabel.setText(formatSeconds(time));
                    });
                }
            }, 0, 1000);
        }
    }

    @FXML
    private void showTimerLog() {
        StringBuilder message = new StringBuilder();
        for (String timestamp: timerLog) {
            message.append(timestamp);
            message.append("\n");
        }
        createInfoAlert("History", message.toString());
    }

    @FXML
    private void exportCSV() {
        if (cards.isEmpty()) {
            createInfoAlert("Empty Deck", "No deck loaded.");
            return;
        }

        new Thread(() -> {
            String[] header = {"question", "answer"};
            List<String[]> data = new ArrayList<>();

            for (Card card: cards) {
                data.add(new String[] {card.getQuestion(), card.getAnswer()});
            }

            try {
                File file = new File (removeExt(filename) + ".csv");
                FileWriter outputFile = new FileWriter(file);
                CSVWriter writer = new CSVWriter(outputFile);
                writer.writeNext(header);
                writer.writeAll(data);
                writer.close();
            } catch (IOException exception) {
                exception.printStackTrace();
            }
        }).start();

        createInfoAlert("Export CSV", "Export complete.");
    }

    private void readCards() {
        try {
            String line;
            BufferedReader read = new BufferedReader(new FileReader(filename));

            while ((line = read.readLine()) != null) {
                String[] words = line.split("~");
                cards.add(new Card(words[0], words[1]));
            }

            read.close();
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    private void saveCard(Card card) {
        String question = card.getQuestion();
        String answer = card.getAnswer();

        if (filename == null)  {
            File file = fileChooser.showSaveDialog(primaryStage);

            if (file == null) return;

            filename = file.getAbsolutePath();
            deckLabel.setText(removeExt(file.getName()));
        }

        new Thread(() -> saveToFile(question, answer)).start();

        cards.add(card);
        setCard(cards.indexOf(card));
    }

    private void saveToFile(String question, String answer) {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(filename, true));

            writer.append(question);
            writer.append("~");
            writer.append(answer);
            writer.newLine();
            writer.close();
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    public void deleteLine(String lineToRemove) {
        try {
            File file = new File(filename);
            File tempFile = new File("temp.txt");

            BufferedReader reader = new BufferedReader(new FileReader(file));
            BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile));

            String currentLine;
            while((currentLine = reader.readLine()) != null) {
                String trimmedLine = currentLine.trim();
                if(trimmedLine.equals(lineToRemove)) continue;
                writer.write(currentLine);
                writer.newLine();
            }

            writer.close();
            reader.close();
            if (file.delete()) //noinspection ResultOfMethodCallIgnored
                tempFile.renameTo(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void replaceLine(String oldLine, String newLine) {
        try {
            File file = new File(filename);
            File tempFile = new File("temp.txt");

            BufferedReader reader = new BufferedReader(new FileReader(file));
            BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile));

            String currentLine;
            while((currentLine = reader.readLine()) != null) {
                String trimmedLine = currentLine.trim();
                if(trimmedLine.equals(oldLine)) {
                    writer.write(newLine);
                    writer.newLine();
                    continue;
                }
                writer.write(currentLine);
                writer.newLine();
            }

            writer.close();
            reader.close();

            if (file.delete()) //noinspection ResultOfMethodCallIgnored
                tempFile.renameTo(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("SameParameterValue")
    private Dialog<Card> createCardDialog(String title) {
        Dialog<Card> dialog = new Dialog<>();
        dialog.setTitle(title);
        dialog.setResizable(false);
        dialog.initOwner(primaryStage);

        Label question = new Label("Question: ");
        Label answer = new Label("Answer: ");

        TextField questionInput = new TextField();
        TextField answerInput = new TextField();

        GridPane grid = new GridPane();
        grid.add(question, 1, 1);
        grid.add(questionInput, 2, 1);
        grid.add(new Label(""), 1, 2);
        grid.add(new Label(""), 2, 2);
        grid.add(answer, 1, 3);
        grid.add(answerInput, 2, 3);
        dialog.getDialogPane().setContent(grid);

        ButtonType saveButton = new ButtonType("Save");
        dialog.getDialogPane().getButtonTypes().add(saveButton);

        dialog.setResultConverter(button -> {
            if (button == saveButton) {
                return new Card(questionInput.getText(), answerInput.getText());
            }

            return null;
        });

        return dialog;
    }

    private void createInfoAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.NONE, message, ButtonType.CLOSE);
        alert.setTitle(title);
        alert.initOwner(primaryStage);
        alert.show();
    }

    private String removeExt(String file) {
        return file.substring(0, file.length() - 4);
    }

    private void clearBox() {
        questionText.clear();
        answerText.clear();
        current = -1;
        currentLabel.setText("0 / 0");
        showButton.setText("Show");
        shown = false;
    }

    private void setCardEditable(boolean value) {
        showAnswer();
        questionText.setEditable(value);
        answerText.setEditable(value);
        showButton.setDisable(value);
        prevButton.setDisable(value);
        nextButton.setDisable(value);
        saveMenuItem.setDisable(!value);
        editMenuItem.setDisable(value);
    }

    private String formatSeconds(int seconds) {
        int hours = seconds / 3600;
        seconds %= 3600;
        int minutes = seconds / 60 ;
        seconds %= 60;
        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }

    public void setStage(Stage stage) {
        primaryStage = stage;
    }
}
