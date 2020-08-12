package cards;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.*;
import java.util.ArrayList;

public class Controller {
    @FXML private Button showButton;

    @FXML private TextArea questionText;
    @FXML private TextArea answerText;

    FileChooser fileChooser = new FileChooser();
    FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("TXT files (*.txt)", "*.txt");

    private final ArrayList<Card> cards = new ArrayList<>();
    String filename = null;
    int current = -1;
    boolean shown = false;

    @FXML
    private void initialize() {
        fileChooser.getExtensionFilters().add(extFilter);
    }

    @FXML
    private void quit() {
        Stage stage = (Stage) showButton.getScene().getWindow();
        stage.close();
    }

    @FXML
    private void newSet() {
        cards.clear();
        filename = null;
        clear();
    }

    @FXML
    private void openSet() {
        newSet();
        Stage stage = (Stage) showButton.getScene().getWindow();
        File file = fileChooser.showOpenDialog(stage);

        if (file != null) {
            filename = file.getAbsolutePath();
        }

        try {
            cards.clear();
            String line;
            BufferedReader read = new BufferedReader(new FileReader(filename));

            while ((line = read.readLine()) != null) {
                String[] words = line.split("~");
                Card card = new Card(words[0], words[1]);
                cards.add(card);
            }

            read.close();
            nextCard();
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    @FXML
    private void saveCard() {
        String question = questionText.getText();
        String answer = answerText.getText();

        if (question.isEmpty() || answer.isEmpty())
            return;

        if (filename != null) {
            saveToFile(question, answer);
        } else {
            Stage stage = (Stage) showButton.getScene().getWindow();
            File file = fileChooser.showSaveDialog(stage);

            if (file != null) {
                filename = file.getAbsolutePath();
                saveToFile(question, answer);
            }
        }
    }

    private void saveToFile(String question, String answer) {
        Card card = new Card(question, answer);
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(filename, true));

            writer.append(card.getQuestion());
            writer.append("~");
            writer.append(card.getAnswer());
            writer.newLine();
            writer.close();

            cards.add(card);
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    @FXML
    private void nextCard() {
        if (cards.size() > current + 1) {
            current++;
            setCard();
        }
    }

    @FXML
    private void prevCard() {
        if (current - 1 >= 0) {
            current--;
            setCard();
        }
    }

    private void setCard() {
        Card card = cards.get(current);
        questionText.setText(card.getQuestion());
        answerText.clear();
        showButton.setText("Show");
    }

    @FXML
    private void showAnswer() {
        if (questionText.getText().isEmpty())
            return;

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
    private void clear() {
        questionText.clear();
        answerText.clear();
        current = -1;
        showButton.setText("Show");
        shown = false;
    }
}
