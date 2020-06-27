/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bugtracker.views;

import static bugtracker.Bugtracker.conn;
import java.net.URL;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.stage.WindowEvent;

/**
 * FXML Controller class
 *
 * @author Vansh Jain
 */
public class ProjectController implements Initializable {

    /**
     * Initializes the controller class.
     */
    @FXML Button add;
    @FXML TextField title;
    @FXML DatePicker date;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
    }    
    
    @FXML
    public void addProject() {
        try {
            Statement stmt = conn.createStatement();  
            String t = title.getText();
            LocalDate d = date.getValue();
            stmt.executeUpdate("INSERT INTO projects (title,date_due) " + "VALUES ('"+t+"','"+d+"')");
            Stage stage = (Stage) add.getScene().getWindow();
            System.gc();
            stage.fireEvent(new WindowEvent(stage, WindowEvent.WINDOW_CLOSE_REQUEST));
        } catch (SQLException e ){
            System.out.println(e);
        }
    }
}
