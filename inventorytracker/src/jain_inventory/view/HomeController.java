package jain_inventory.view;

import jain_inventory.Jain_inventory;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;

public class HomeController implements Initializable {

    @FXML private Button inventory;
    @FXML private Button warehouses;     
    @FXML private Button orders;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    

    @FXML
    private void openInventory() throws IOException{
        Parent root = FXMLLoader.load(getClass().getResource("inventory.fxml"));
        Scene scene = new Scene (root);
        Jain_inventory.mainStage.setScene(scene);
        Jain_inventory.mainStage.show();
    }
    
    @FXML
    private void openOrders() throws IOException{
        Parent root = FXMLLoader.load(getClass().getResource("orders.fxml"));
        Scene scene = new Scene (root);
        Jain_inventory.mainStage.setScene(scene);
        Jain_inventory.mainStage.show();
    }
    
    @FXML
    private void logout() throws IOException{
        Jain_inventory.loggedIn = false;
        Parent root = FXMLLoader.load(getClass().getResource("login.fxml"));
        Scene scene = new Scene (root);
        Jain_inventory.mainStage.setScene(scene);
        Jain_inventory.mainStage.show();
    }
    
    @FXML
    private void exitApplication() {
        Jain_inventory.exitApplication();
    }
}
