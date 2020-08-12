package cards;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.*;
import java.util.ArrayList;
import java.util.Optional;

public class Controller {
    @FXML private Label setName;

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
            setName.setText(removeExt(file.getName()));
        }

        readCards();
    }

    private void readCards() {
        try {
            clear();
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

    private void saveSet(Card card) {
        String question = card.getQuestion();
        String answer = card.getAnswer();

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
                setName.setText(removeExt(file.getName()));
                readCards();
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
            int index = cards.indexOf(card);
            current = index;
            setCard(index);

        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    @FXML
    private void newCard() {
        Dialog<Card> dialog = new Dialog<>();
        dialog.setTitle("New Card");
        dialog.setResizable(false);

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

        Optional<Card> result = dialog.showAndWait();

        result.ifPresent(this::saveSet);
    }

    @FXML
    private void nextCard() {
        if (cards.size() > current + 1) {
            current++;
            setCard(-1);
        }
    }

    @FXML
    private void prevCard() {
        if (current - 1 >= 0) {
            current--;
            setCard(-1);
        }
    }

    private void setCard(int index) {
        if (index == -1)
            index = current;

        Card card = cards.get(index);
        questionText.setText(card.getQuestion());
        answerText.clear();
        showButton.setText("Show");
        shown = false;
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

    private void clear() {
        cards.clear();
        questionText.clear();
        answerText.clear();
        current = -1;
        showButton.setText("Show");
        shown = false;
    }

    private String removeExt(String s) {
        return s.substring(0, s.length() - 4);
    }
}
