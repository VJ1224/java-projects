package cards;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

import java.io.*;
import java.util.ArrayList;

public class Controller {
    @FXML private Button quitButton;
    @FXML private Button showButton;

    @FXML private TextArea questionText;
    @FXML private TextArea answerText;

    private ArrayList<Card> cards = new ArrayList<>();
    int current = -1;
    boolean shown = false;

    @FXML
    private void initialize() {
        try {
            String line;
            BufferedReader read = new BufferedReader(new FileReader("cards.txt"));

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
    private void quit() {
        Stage stage = (Stage) quitButton.getScene().getWindow();
        stage.close();
    }

    @FXML
    private void saveCard() {
        Card card = new Card(questionText.getText(), answerText.getText());
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter("cards.txt", true));
            writer.append(card.getQuestion() + "~" + card.getAnswer() + "\n");
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
        } else {
            current = 0;
        }

        Card card = cards.get(current);
        questionText.setText(card.getQuestion());
        answerText.clear();
    }

    @FXML
    private void showAnswer() {
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
}
