package jain_inventory.view;

import jain_inventory.Account;
import jain_inventory.Jain_inventory;
import java.io.IOException;
import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.ListIterator;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;

public class RegisterController implements Initializable {
    
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField1;
    @FXML private PasswordField passwordField2;
    @FXML private PasswordField masterPassword;
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
    private void registerUser() {
        String e = emailField.getText();
        String p = passwordField1.getText();
        String p1 = passwordField2.getText();
        String mp = masterPassword.getText();
        
        boolean present = false;
        Matcher matcher = Jain_inventory.emailPattern.matcher(e);
        
        for(int i = 0; i < accounts.size(); i++) {
            if (accounts.get(i).getEmail().equals(e)) {
                present = true;
                break;
            } else {
                present = false;
            }
        }
        
        if (!p.equals(p1)) {
            Jain_inventory.infoAlert("Register", null, "Passwords do not match.");
        } else if (e.isEmpty() || !matcher.matches()) {
            Jain_inventory.infoAlert("Register", null, "Enter a valid email address.");
        } else if (present) {
            Jain_inventory.infoAlert("Register", null, "Account with email already exists.");
        } else if (!mp.equals(Jain_inventory.MASTER_PASSWORD)) {
            Jain_inventory.infoAlert("Register", null, "Master password is incorrect.");
        } else {
            addUser(e, p);
            Jain_inventory.infoAlert("Register", null, "New user registered.");
            emailField.setText("");
            passwordField1.setText("");
            passwordField2.setText("");
            masterPassword.setText("");
        }
    }
    
    private void addUser(String email, String password) {
        accounts.add(new Account(email, password));
        
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
