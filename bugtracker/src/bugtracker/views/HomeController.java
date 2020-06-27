/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bugtracker.views;

import bugtracker.Bug;
import static bugtracker.Bugtracker.conn;
import bugtracker.Project;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.List;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.SplitPane;
import javafx.scene.control.SplitPane.Divider;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

/**
 * FXML Controller class
 *
 * @author Vansh Jain
 */
public class HomeController implements Initializable {

    /**
     * Initializes the controller class.
     */
    public static ObservableList<Project> projects = FXCollections.observableArrayList();
    public static ObservableList<Bug> bugs = FXCollections.observableArrayList();
    @FXML Button projectAdd;
    @FXML Button solveBugButton;
    @FXML Button removeProjectButton;
    @FXML TableView<Project> projectsTable;
    @FXML TableColumn<Project, String> projectsColumn;
    @FXML TableView<Bug> bugsTable;
    @FXML TableColumn<Bug, String> bugTitle;
    @FXML TableColumn<Bug, Integer> bugSeverity;
    @FXML Label projectDate;
    @FXML Label bugPriority;
    @FXML Label detailsText;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        projectsColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getTitle()));
        bugTitle.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getTitle()));
        bugSeverity.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getSeverity()).asObject());
        
        try {
            loadProjects();
            loadBugs();
        } catch (Exception exc) {
            System.out.println(exc);
        }
    }  
    
    public void reset() {
        projectsTable.getSelectionModel().clearSelection();
        bugsTable.getSelectionModel().clearSelection();
        projectDate.setText("Date");
        bugPriority.setText("Priority");
        detailsText.setText("");
        removeProjectButton.setDisable(true);
        solveBugButton.setDisable(true);
    }
    
    public void loadProjects()throws Exception{
        projects.clear();
        Statement stmt = conn.createStatement();  
        ResultSet rs = stmt.executeQuery("select * from projects"); 
        while (rs.next()) {
            projects.add(new Project(rs.getString("title"),rs.getDate("date_due")));
        }
        projectsTable.setItems(projects);
        reset();
    }
    
    public void loadBugs()throws Exception{
        bugs.clear();
        Statement stmt = conn.createStatement();  
        ResultSet rs = stmt.executeQuery("select * from bugs"); 
        while (rs.next()) {
            
            bugs.add(new Bug(rs.getString("title"),rs.getInt("severity"),rs.getString("description")));
        }
        bugsTable.setItems(bugs);
        reset();
    }
    
    @FXML
    public void solveBug() {
        Bug selected = bugsTable.getSelectionModel().getSelectedItem();
        try {
            Statement stmt = conn.createStatement();
            stmt.executeUpdate("DELETE FROM bugs WHERE title = '"+selected.getTitle()+"'");
            loadBugs();
        } catch (Exception e) {
            System.out.println(e);
        }
        solveBugButton.setDisable(true);   
    }
    
    @FXML
    public void removeProject() {
        if (!projectsTable.getSelectionModel().isEmpty()) {
            Project selected = projectsTable.getSelectionModel().getSelectedItem();
            try {
                Statement stmt = conn.createStatement();
                stmt.executeUpdate("DELETE FROM projects WHERE title = '"+selected.getTitle()+"'");
                stmt.executeUpdate("DELETE FROM bugs WHERE project ='"+selected.getTitle()+"'");
                loadProjects();
                loadBugs();
            } catch (Exception e) {
                System.out.println(e);
            }
            removeProjectButton.setDisable(true);
        }
        
    }
    
    @FXML
    public void showDetails() throws Exception{
        if (!projectsTable.getSelectionModel().isEmpty()) {
            bugs.clear();
            Project selected = projectsTable.getSelectionModel().getSelectedItem();
            projectDate.setText(selected.dateString());
            Statement stmt = conn.createStatement();  
            ResultSet rs = stmt.executeQuery("SELECT * FROM bugs WHERE project = '"+selected.getTitle()+"'");
            while (rs.next()) {
                bugs.add(new Bug(rs.getString("title"),rs.getInt("severity"),rs.getString("description")));
            }
            bugsTable.setItems(bugs);
            detailsText.setText("");
            bugPriority.setText("Priority");
            removeProjectButton.setDisable(false);
            solveBugButton.setDisable(true);
        }
    }
    
    @FXML
    public void showBugDetails() {
        if (!bugsTable.getSelectionModel().isEmpty()) {
            Bug selected = bugsTable.getSelectionModel().getSelectedItem();
            bugPriority.setText(String.valueOf(selected.getSeverity()));
            detailsText.setText(selected.getDescription());
            solveBugButton.setDisable(false);
        }
    }
    
    @FXML
    public void clearDetails() throws Exception{
        projectsTable.getSelectionModel().clearSelection();
        loadBugs();
    }
    
    @FXML public void clearBugDetails() {
        bugsTable.getSelectionModel().clearSelection();
        detailsText.setText("");
        solveBugButton.setDisable(true);
    }
    
    @FXML
    public void addProject() throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("project.fxml"));
        Stage stage = new Stage();
        stage.setTitle("Add Project");
        stage.setScene(new Scene(root));
        stage.setResizable(false);
        stage.setOnCloseRequest((WindowEvent e) -> {
            try {
                loadProjects();
            } catch (Exception exc) {
                System.out.println(exc);
            }
        });
        stage.show();
    }
    
    @FXML
    public void addBug() throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("bug.fxml"));
        Stage stage = new Stage();
        stage.setTitle("Add Bug");
        stage.setScene(new Scene(root));
        stage.setResizable(false);
        stage.setOnCloseRequest((WindowEvent e) -> {
            try {
               loadBugs();
            } catch (Exception exc) {
                System.out.println(exc);
            }
        });
        stage.show();
    }
}
