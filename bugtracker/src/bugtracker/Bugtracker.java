/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bugtracker;

/**
 *
 * @author Vansh Jain
 */
import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.sql.*;
import javafx.application.Platform;
import javafx.stage.WindowEvent;

public class Bugtracker extends Application {
    
    public static Connection conn;
    
    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("views/home.fxml"));
        primaryStage.setTitle("Bug Tracker");
        primaryStage.setScene(new Scene(root));
        primaryStage.setResizable(false);
        primaryStage.setOnCloseRequest((WindowEvent e) -> {
            Platform.exit();
        });
        primaryStage.show();
    }
    
    public static void main(String[] args) {
        try {
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/bugtracker","root","vansh1234");
        } catch (SQLException e ){
            System.out.println(e);
        }
        launch(args);
    }
}