/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bugtracker.views;

import static bugtracker.Bugtracker.conn;
import bugtracker.Project;
import java.net.URL;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.stage.WindowEvent;

/**
 * FXML Controller class
 *
 * @author Vansh Jain
 */
public class BugController implements Initializable {

    /**
     * Initializes the controller class.
     */
    @FXML Button add;
    @FXML TextField title;
    @FXML TextArea description;
    @FXML ChoiceBox severity;
    @FXML ChoiceBox project;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        ObservableList<String> s = FXCollections.observableArrayList("1", "2","3","4","5");
        severity.setItems(s);
        ObservableList<Project> p = bugtracker.views.HomeController.projects;
        ObservableList<String> pt = FXCollections.observableArrayList();
        for (int i = 0; i < p.size(); i++) {
            pt.add(p.get(i).getTitle());
        }
        project.setItems(pt);
    }    
    
    @FXML
    public void addBug() {
        try {
            Statement stmt = conn.createStatement();  
            String t = title.getText();
            int s = Integer.parseInt(severity.getValue().toString());
            String d = description.getText();
            String p = project.getValue().toString();
            stmt.executeUpdate("INSERT INTO bugs (title,severity,description,project) " + "VALUES ('"+t+"','"+s+"','"+d+"','"+p+"')");
            Stage stage = (Stage) add.getScene().getWindow();
            stage.fireEvent(new WindowEvent(stage, WindowEvent.WINDOW_CLOSE_REQUEST));
        } catch (SQLException e ){
            System.out.println(e);
        }
    }
}
