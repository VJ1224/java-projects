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
        setName.setText("-");
    }

    @FXML
    private void quit() {
        Stage stage = (Stage) showButton.getScene().getWindow();
        stage.close();
    }

    @FXML
    private void clearSet() {
        cards.clear();
        questionText.clear();
        answerText.clear();
        current = -1;
        showButton.setText("Show");
        shown = false;
        filename = null;
        setName.setText("-");
    }

    @FXML
    private void openSet() {
        clearSet();
        Stage stage = (Stage) showButton.getScene().getWindow();
        File file = fileChooser.showOpenDialog(stage);

        if (file != null) {
            filename = file.getAbsolutePath();
            setName.setText(removeExt(file.getName()));
        } else {
            return;
        }

        readCards();
    }

    private void readCards() {
        try {
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

        if (filename == null)  {
            Stage stage = (Stage) showButton.getScene().getWindow();
            File file = fileChooser.showSaveDialog(stage);

            if (file != null) {
                filename = file.getAbsolutePath();
                setName.setText(removeExt(file.getName()));
            } else {
                return;
            }
        }

        saveToFile(question, answer);
        cards.add(card);
        int index = cards.indexOf(card);
        setCard(index);
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

    @FXML
    private void newCard() {
        Dialog<Card> dialog = new Dialog<>();
        dialog.setTitle("New Card");
        dialog.setResizable(false);
        dialog.initOwner(showButton.getScene().getWindow());

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
        if (cards.isEmpty()) {
            createInfoAlert("No set loaded");
            return;
        }

        if (cards.size() > current + 1) {
            current++;
            setCard(-1);
        } else {
            createInfoAlert("This is the last card");
        }
    }

    @FXML
    private void prevCard() {
        if (cards.isEmpty()) {
            createInfoAlert("No set loaded");
            return;
        }

        if (current - 1 >= 0) {
            current--;
            setCard(-1);
        } else {
            createInfoAlert("This is the first card");
        }
    }

    private void setCard(int index) {
        if (index == -1)
            index = current;
        else
            current = index;

        Card card = cards.get(index);
        questionText.setText(card.getQuestion());
        answerText.clear();
        showButton.setText("Show");
    }

    private void createInfoAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.NONE, message, ButtonType.CLOSE);
        alert.setTitle("Message");
        alert.initOwner(showButton.getScene().getWindow());
        alert.show();
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

    private String removeExt(String s) {
        return s.substring(0, s.length() - 4);
    }
}
