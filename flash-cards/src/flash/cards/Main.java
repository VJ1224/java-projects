package flash.cards;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        FXMLLoader loader = new FXMLLoader(getClass().getResource("main.fxml"));
        Parent root = loader.load();

        Controller controller = loader.getController();
        controller.setStage(primaryStage);

        primaryStage.setTitle("Flash Cards");
        primaryStage.setScene(new Scene(root, 480, 360));
        primaryStage.getIcons().add(new Image("file:resources/images/icon.jpg"));
        primaryStage.show();

        primaryStage.addEventHandler(KeyEvent.KEY_PRESSED, (key) -> {
            if (key.getCode() == KeyCode.LEFT) {
                controller.prevCard();
            } else if (key.getCode() == KeyCode.RIGHT) {
                controller.nextCard();
            } else if(key.getCode() == KeyCode.SPACE) {
                controller.showAnswer();
            }

            key.consume();
        });

        primaryStage.setOnCloseRequest(e -> {
            primaryStage.close();
            System.exit(0);
        });
    }

    public static void main(String[] args) {
        launch(args);
    }
}
