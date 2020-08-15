package flash.cards;

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
        clearBox();
        cards.clear();
        filename = null;
        setName.setText("-");
    }

    @FXML
    private void openSet() {
        Stage stage = (Stage) showButton.getScene().getWindow();
        File file = fileChooser.showOpenDialog(stage);

        if (file != null) {
            clearSet();
            filename = file.getAbsolutePath();
            setName.setText(removeExt(file.getName()));
        } else {
            return;
        }

        readCards();
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

        result.ifPresent(this::saveCard);
    }

    @FXML
    private void deleteCard() {
        Card card = cards.get(current);
        String lineToRemove = card.toString();

        new Thread(() -> deleteLine(lineToRemove)).start();

        cards.remove(current);

        if (!cards.isEmpty()) setCard(0);
        else clearBox();
    }

    @FXML
    private void nextCard() {
        if (cards.isEmpty()) {
            createInfoAlert("Empty set");
            return;
        }

        if (cards.size() > current + 1) {
            current++;
            setCard(-1);
        } else createInfoAlert("Last card");
    }

    @FXML
    private void prevCard() {
        if (cards.isEmpty()) {
            createInfoAlert("Empty set");
            return;
        }

        if (current - 1 >= 0) {
            current--;
            setCard(-1);
        } else createInfoAlert("First card");
    }

    @FXML
    private void showAnswer() {
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

    private void saveCard(Card card) {
        String question = card.getQuestion();
        String answer = card.getAnswer();

        if (filename == null)  {
            Stage stage = (Stage) showButton.getScene().getWindow();
            File file = fileChooser.showSaveDialog(stage);

            if (file != null) {
                filename = file.getAbsolutePath();
                setName.setText(removeExt(file.getName()));
            } else return;
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

    private void setCard(int index) {
        if (index == -1) index = current;
        else current = index;

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

    private String removeExt(String file) {
        return file.substring(0, file.length() - 4);
    }

    private void clearBox() {
        questionText.clear();
        answerText.clear();
        current = -1;
        showButton.setText("Show");
        shown = false;
    }
}
