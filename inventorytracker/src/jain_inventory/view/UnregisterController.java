package jain_inventory.view;

import jain_inventory.Account;
import jain_inventory.Jain_inventory;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.ListIterator;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class UnregisterController implements Initializable {
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    private static ArrayList<Account> accounts = new ArrayList<Account>();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {
            String sqlSelect = "SELECT * FROM APP.ACCOUNTS";
            Statement statement;
            statement = Jain_inventory.connection.createStatement();
            ResultSet rst = statement.executeQuery(sqlSelect);

            while (rst.next()) {
                accounts.add(new Account(rst.getString("EMAIL"), rst.getString("PASSWORD")));
            }
        } catch (SQLException exc) {
            System.out.println(exc);
        }
    }    
    
    @FXML
    private void removeUser() {
        String e = emailField.getText();
        String p = passwordField.getText();
        
        boolean present = false;
        int index = -1;
        for(int i = 0; i < accounts.size(); i++) {
            if (accounts.get(i).getEmail().equals(e)) {
                present = true;
                index = i;
                break;
            } else {
                present = false;
            }
        }
        
        if (present) {
            if (accounts.get(index).getPassword().equals(p)) {
                if (Jain_inventory.confirmAlert("Remove Product", null, "Are you sure you want to remove User " + e + "?")) {
                    remove(index);
                    Jain_inventory.infoAlert("Remove User", null, "User removed.");
                    emailField.setText("");
                    passwordField.setText("");
                }
            } else {
                Jain_inventory.infoAlert("Remove User", null, "Unsuccessful. Password is incorrect.");
            }
        } else {
            Jain_inventory.infoAlert("Remove User", null, "Unsuccessful. User does not exist.");
        }
    }
    
    private void remove(int index) {
        accounts.remove(index);
        
        try {
            String sqlDelete = "DELETE FROM APP.ACCOUNTS";
            String sqlInsert = "INSERT INTO APP.ACCOUNTS(EMAIL, PASSWORD) VALUES(?,?)";
            PreparedStatement prepStatement = Jain_inventory.connection.prepareStatement(sqlDelete);
            prepStatement.executeUpdate();
            prepStatement = Jain_inventory.connection.prepareStatement(sqlInsert);
            
            ListIterator<Account> iter = accounts.listIterator();
            while (iter.hasNext()) {
                Account acc = iter.next();
                prepStatement.setString(1,acc.getEmail());
                prepStatement.setString(2,acc.getPassword());
                prepStatement.addBatch();
            }
            
            prepStatement.executeBatch();
        } catch (SQLException exc) {
            System.out.println(exc);
        }
    }
    
    @FXML
    private void back() throws IOException{
        String source;
        if (Jain_inventory.loggedIn) {
            source = "home.fxml";
        } else {
            source = "login.fxml";
        }
        Parent root = FXMLLoader.load(getClass().getResource(source));
        Scene scene = new Scene (root);
        Jain_inventory.mainStage.setScene(scene);
        Jain_inventory.mainStage.show();
    }
}
