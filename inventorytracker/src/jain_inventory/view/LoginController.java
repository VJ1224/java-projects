package jain_inventory.view;

import jain_inventory.Jain_inventory;
import jain_inventory.Account;
import java.io.IOException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.ArrayList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.StageStyle;
import javax.mail.internet.AddressException;

public class LoginController implements Initializable {
    
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
    private void login() throws IOException{
        String e = emailField.getText();
        String p = passwordField.getText();
        
        boolean emailPresent = false;
        int index = -1;
        
        for(int i = 0; i < accounts.size(); i++) {
            if (accounts.get(i).getEmail().equals(e)) {
                emailPresent = true;
                index = i;
                break;
            } else {
                emailPresent = false;
            }
        }
        if (emailPresent) {
            if (accounts.get(index).getPassword().equals(p)) {
                Jain_inventory.loggedIn = true;
                Jain_inventory.infoAlert("Login", null, "Login successful.");
                Parent root = FXMLLoader.load(getClass().getResource("home.fxml"));
                Scene scene = new Scene (root);
                Jain_inventory.mainStage.setScene(scene);
                Jain_inventory.mainStage.show();
            } else {
                Jain_inventory.infoAlert("Login", null, "Login unsuccessful. Password is incorrect.");
            }
        } else {
            Jain_inventory.infoAlert("Login", null, "Login unsuccessful. User does not exist.");
        }
    }
    
    @FXML
    private void forgotPassword() throws AddressException{
        TextInputDialog enterEmail = new TextInputDialog();
        enterEmail.initStyle(StageStyle.UTILITY);
        enterEmail.setTitle("Forgot Password");
        enterEmail.setHeaderText(null);
        enterEmail.setContentText("Enter a registered email address:");
        Optional<String> optEmail = enterEmail.showAndWait();
        String email;
        if (optEmail.isPresent()) {
            email = optEmail.get();
        } else {
            email = null;
        }
        
        boolean emailPresent = false;
        int index = -1;
        
        if (email != null) {
            for(int i = 0; i < accounts.size(); i++) {
                if (accounts.get(i).getEmail().equals(email)) {
                    emailPresent = true;
                    index = i;
                    break;
                } else {
                    emailPresent = false;
                }
            }
        } else {
            emailPresent = false;
        }
        
        if (emailPresent) {
            String password = accounts.get(index).getPassword();
            String oldEmail = Jain_inventory.RECEIVER;
            Jain_inventory.RECEIVER = email;
            Jain_inventory.sendEmail("Jain Textiles Forgot Password", "Your password is " + password);
            Jain_inventory.RECEIVER = oldEmail;
        } else if (email != null) {
            Jain_inventory.infoAlert("Forgot Password", null, "User does not exist.");
        }
    }
    
    @FXML
    private void registerUser() throws IOException{
        Parent root = FXMLLoader.load(getClass().getResource("register.fxml"));
        Scene scene = new Scene (root);
        Jain_inventory.mainStage.setScene(scene);
        Jain_inventory.mainStage.show();
    }
    
    @FXML
    private void removeUser() throws IOException{
        Parent root = FXMLLoader.load(getClass().getResource("unregister.fxml"));
        Scene scene = new Scene (root);
        Jain_inventory.mainStage.setScene(scene);
        Jain_inventory.mainStage.show();
    }
    
    @FXML
    private void exitApplication() {
        Jain_inventory.exitApplication();
    }
}