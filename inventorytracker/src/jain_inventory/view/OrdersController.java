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

public class OrdersController implements Initializable {

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
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
    
    @FXML
    private void openSuppliers() throws IOException{
        Parent root = FXMLLoader.load(getClass().getResource("suppliers.fxml"));
        Scene scene = new Scene (root);
        Jain_inventory.mainStage.setScene(scene);
        Jain_inventory.mainStage.show();
    }
    
    @FXML
    private void openCustomers() throws IOException{
        Parent root = FXMLLoader.load(getClass().getResource("customers.fxml"));
        Scene scene = new Scene (root);
        Jain_inventory.mainStage.setScene(scene);
        Jain_inventory.mainStage.show();
    }
    
    @FXML
    private void openPurchaseOrders() throws IOException{
        Parent root = FXMLLoader.load(getClass().getResource("purchaseOrders.fxml"));
        Scene scene = new Scene (root);
        Jain_inventory.mainStage.setScene(scene);
        Jain_inventory.mainStage.show();
    }
    
    @FXML
    private void openSalesOrders() throws IOException{
        Parent root = FXMLLoader.load(getClass().getResource("salesOrders.fxml"));
        Scene scene = new Scene (root);
        Jain_inventory.mainStage.setScene(scene);
        Jain_inventory.mainStage.show();
    }
}
