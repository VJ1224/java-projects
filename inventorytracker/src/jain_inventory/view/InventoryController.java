package jain_inventory.view;

import jain_inventory.Jain_inventory;
import jain_inventory.Product;
import jain_inventory.ProductComparator;
import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.text.NumberFormat;
import java.util.*;
import java.util.regex.Matcher;
import javafx.collections.*;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.*;
import javafx.stage.StageStyle;
import javax.mail.internet.AddressException;

public class InventoryController implements Initializable {

    @FXML private TableView<Product> inventoryTable;
    @FXML private TableColumn<Product, String> idColumn;
    @FXML private TableColumn<Product, String> nameColumn;
    @FXML private TableColumn<Product, Integer> quantityColumn;
    @FXML private TableColumn<Product, Integer> priceColumn;
    
    private ObservableList<Product> inventory = FXCollections.observableArrayList();
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {  
        resetInventoryTable();
        idColumn.setCellValueFactory(new PropertyValueFactory("ID"));
        nameColumn.setCellValueFactory(new PropertyValueFactory("name"));
        quantityColumn.setCellValueFactory(new PropertyValueFactory("quantity"));
        priceColumn.setCellValueFactory(new PropertyValueFactory("price"));
    }
    
    private void loadInventory() {
        inventory.clear();
        try {
            String sqlSelect = "SELECT * FROM APP.INVENTORY";
            Statement statement;
            statement = Jain_inventory.connection.createStatement();
            ResultSet rst = statement.executeQuery(sqlSelect);

            while (rst.next()) {
                inventory.add(new Product(rst.getString("PRODUCT_ID"), rst.getString("NAME"), 
                        rst.getInt("QUANTITY_METRES"), rst.getInt("PRICE_METRES")));
            }
        } catch (SQLException exc) {
            System.out.println(exc);
        }
    }
    
    @FXML
    private void addProducts() {
        resetInventoryTable();
        String ID = "";
        String name = "";
        int quantity = 0;
        int price = 0;
        
        
        boolean cancel = false;        
        boolean idValidated = false;
        boolean nameValidated = false;
        boolean quantityValidated = false;
        boolean priceValidated = false;
        
        while (!idValidated && !cancel) {
            TextInputDialog enterID = new TextInputDialog();
            enterID.initStyle(StageStyle.UTILITY);
            enterID.setTitle("Add Product");
            enterID.setHeaderText(null);
            enterID.setContentText("Enter Product ID");
            Optional<String> optID = enterID.showAndWait();
            
            if (optID.isPresent()) {
                ID = optID.get();
            } else {
                ID = null;
            }
            
            if (ID == null) {
                cancel = true;
            } else if (!ID.isEmpty()) {
                Matcher matcher = Jain_inventory.idPattern.matcher(ID);
                
                if (inventory.isEmpty() && matcher.matches()) {
                    idValidated = true;
                } else if (!matcher.matches()) {
                    Jain_inventory.infoAlert("Add Product", null, "Enter ID correctly.");
                } else {
                    idValidated = true;
                }
                
                for(int i = 0; i < inventory.size(); i++) {
                    if (inventory.get(i).getID().equals(ID)) {
                        Jain_inventory.infoAlert("Add Product", null, "Product ID already exists.");
                        idValidated = false;
                        break;
                    }
                }
                
            } else {
                Jain_inventory.infoAlert("Add Product", null, "Enter ID correctly.");
            }
        }
     
        while (!nameValidated && !cancel) {
            TextInputDialog enterName = new TextInputDialog();
            enterName.initStyle(StageStyle.UTILITY);
            enterName.setTitle("Add Product");
            enterName.setHeaderText(null);
            enterName.setContentText("Enter Product Name");
            Optional<String> optName = enterName.showAndWait();
            
            if (optName.isPresent()) {
                name = optName.get();
            } else {
                name = null;
            }
            
            if (name == null) {
                cancel = true;
            } else if (Jain_inventory.isText(name)) {
                nameValidated = true;
            } else {
                Jain_inventory.infoAlert("Add Product", null, "Enter name correctly.");
            }
        }

        while (!quantityValidated && !cancel) {
            TextInputDialog enterQuantity = new TextInputDialog();
            enterQuantity.initStyle(StageStyle.UTILITY);
            enterQuantity.setTitle("Add Product");
            enterQuantity.setHeaderText(null);
            enterQuantity.setContentText("Enter Product Quantity");
            Optional <String> optQuantity = enterQuantity.showAndWait();
            String strQuantity;
            
            if (optQuantity.isPresent()) {
                strQuantity = optQuantity.get();
            } else {
                strQuantity = null;
            }
            
            if (strQuantity == null){
                cancel = true;
            } else if (Jain_inventory.isInt(strQuantity)) {
                quantity = Integer.valueOf(strQuantity);
                quantityValidated = true;
            } else {
                Jain_inventory.infoAlert("Add Product", null, "Enter quantity correctly.");
            }
        }

        while (!priceValidated && !cancel) {
            TextInputDialog enterPrice = new TextInputDialog();
            enterPrice.initStyle(StageStyle.UTILITY);
            enterPrice.setTitle("Add Product");
            enterPrice.setHeaderText(null);
            enterPrice.setContentText("Enter Product Price");
            Optional<String> optPrice = enterPrice.showAndWait();
            String strPrice;
            
            if (optPrice.isPresent()) {
                strPrice = optPrice.get();
            } else {
                strPrice = null;
            }
            
            if (strPrice == null){
                cancel = true;
            } else if (Jain_inventory.isInt(strPrice)) {
                price = Integer.valueOf(strPrice);
                priceValidated = true;
            } else {
                Jain_inventory.infoAlert("Add Product", null, "Enter price correctly.");
            }
        }        
        
        if (!cancel) {
            inventory.add(new Product(ID, name, quantity, price));
            saveInventoryTable();
        }
    }
    
    @FXML
    private void removeProducts() {
        resetInventoryTable();
        TextInputDialog enterID = new TextInputDialog();
        enterID.initStyle(StageStyle.UTILITY);
        enterID.setTitle("Remove Product");
        enterID.setHeaderText(null);
        enterID.setContentText("Enter Product ID");
        Optional<String> optID = enterID.showAndWait();
        String ID;
        
        if (optID.isPresent()) {
            ID = optID.get();
        } else {
            ID = null;
        }
        
        if (ID != null) {
            int index = -1;
            for(int i = 0; i < inventory.size(); i++) {
                if (inventory.get(i).getID().equals(ID)) {
                    index = i;
                    break;
                }
            }

            if (index >= 0) {
                if (Jain_inventory.confirmAlert("Remove Product", null, "Are you sure you want to remove Product " + ID + "?"))
                    inventory.remove(index);
            } else {
                Jain_inventory.infoAlert("Remove Product", null, "Product ID does not exist.");
            }
        }
        
        saveInventoryTable();
    }
    
    @FXML
    private void editProducts() {
        resetInventoryTable();
        TextInputDialog enterID = new TextInputDialog();
        enterID.initStyle(StageStyle.UTILITY);
        enterID.setTitle("Edit Product");
        enterID.setHeaderText(null);
        enterID.setContentText("Enter Product ID");
        Optional<String> optID = enterID.showAndWait();
        String ID;
        
        if (optID.isPresent()) {
            ID = optID.get();
        } else {
            ID = null;
        }
        
        int index = -1;
        
        String name = "";
        int quantity = 0;
        int price = 0;
        
        if (ID != null) {
            for(int i = 0; i < inventory.size(); i++) {
                if (inventory.get(i).getID().equals(ID)) {
                    index = i;

                    List<String> columnChoices = new ArrayList<>();
                    columnChoices.add("Name");
                    columnChoices.add("Quantity");
                    columnChoices.add("Price");
                    ChoiceDialog<String> chooseOption = new ChoiceDialog<>("Name", columnChoices);
                    chooseOption.initStyle(StageStyle.UTILITY);
                    chooseOption.setTitle("Edit Product");
                    chooseOption.setHeaderText(null);
                    chooseOption.setContentText("Choose field:");
                    Optional<String> optColumnResult = chooseOption.showAndWait();
                    String columnResult;

                    if(optColumnResult.isPresent()) {
                        columnResult = optColumnResult.get();
                    } else {
                        columnResult = null;
                    }

                    boolean cancel = false;

                    if (columnResult != null) {
                        switch (columnResult) {
                            case "Name":
                                boolean nameValidated = false;
                                while (!nameValidated && !cancel) {
                                    TextInputDialog enterName = new TextInputDialog();
                                    enterName.initStyle(StageStyle.UTILITY);
                                    enterName.setTitle("Edit Product");
                                    enterName.setHeaderText(null);
                                    enterName.setContentText("Enter Product Name");
                                    Optional<String> optName = enterName.showAndWait();

                                    if (optName.isPresent()) {
                                        name = optName.get();
                                    } else {
                                        name = null;
                                    }

                                    if (name == null) {
                                        cancel = true;
                                    } else if (Jain_inventory.isText(name)) {
                                        nameValidated = true;
                                    } else {
                                        Jain_inventory.infoAlert("Edit Product", null, "Enter name correctly.");
                                    }
                                }

                                if (!cancel) {
                                    Product prod = inventory.get(index);
                                    prod.setName(name);
                                    inventory.set(index, prod);
                                    saveInventoryTable();
                                }

                                break;

                            case "Quantity":
                               boolean quantityValidated = false;
                               while (!quantityValidated && !cancel) {
                                    TextInputDialog enterQuantity = new TextInputDialog();
                                    enterQuantity.initStyle(StageStyle.UTILITY);
                                    enterQuantity.setTitle("Edit Product");
                                    enterQuantity.setHeaderText(null);
                                    enterQuantity.setContentText("Enter Product Quantity");
                                    Optional <String> optQuantity = enterQuantity.showAndWait();
                                    String strQuantity;

                                    if (optQuantity.isPresent()) {
                                        strQuantity = optQuantity.get();
                                    } else {
                                        strQuantity = null;
                                    }

                                    if (strQuantity == null){
                                        cancel = true;
                                    } else if (Jain_inventory.isInt(strQuantity)) {
                                        quantity = Integer.valueOf(strQuantity);
                                        quantityValidated = true;
                                    } else {
                                        Jain_inventory.infoAlert("Edit Product", null, "Enter quantity correctly.");
                                    }
                                }

                                if (!cancel) {
                                    Product prod = inventory.get(index);
                                    prod.setQuantity(quantity);
                                    inventory.set(index, prod);
                                    saveInventoryTable();
                                }

                                break;

                            case "Price":
                                boolean priceValidated = false;
                                while (!priceValidated && !cancel) {
                                    TextInputDialog enterPrice = new TextInputDialog();
                                    enterPrice.initStyle(StageStyle.UTILITY);
                                    enterPrice.setTitle("Edit Product");
                                    enterPrice.setHeaderText(null);
                                    enterPrice.setContentText("Enter Product Price");
                                    Optional<String> optPrice = enterPrice.showAndWait();
                                    String strPrice;

                                    if (optPrice.isPresent()) {
                                        strPrice = optPrice.get();
                                    } else {
                                        strPrice = null;
                                    }

                                    if (strPrice == null){
                                        cancel = true;
                                    } else if (Jain_inventory.isInt(strPrice)) {
                                        price = Integer.valueOf(strPrice);
                                        priceValidated = true;
                                    } else {
                                        Jain_inventory.infoAlert("Edit Product", null, "Enter price correctly.");
                                    }
                                }

                                if (!cancel) {
                                    Product prod = inventory.get(index);
                                    prod.setPrice(price);
                                    inventory.set(index, prod);
                                    saveInventoryTable();
                                }

                                break;
                        }
                    }
                }
            }
        }
        
        if (index == -1 && optID.isPresent()) {
            Jain_inventory.infoAlert("Edit Product", null, "Product ID does not exist.");
        }
    }
    
    @FXML
    private void searchProducts() {
        resetInventoryTable();
        List<String> columnChoices = new ArrayList<>();
        columnChoices.add("ID");
        columnChoices.add("Name");
        ChoiceDialog<String> chooseOption = new ChoiceDialog<>("ID", columnChoices);
        chooseOption.initStyle(StageStyle.UTILITY);
        chooseOption.setTitle("Search Products");
        chooseOption.setHeaderText(null);
        chooseOption.setContentText("Choose field:");
        Optional<String> optColumnResult = chooseOption.showAndWait();
        String columnResult;
        
        if(optColumnResult.isPresent()) {
            columnResult = optColumnResult.get();
        } else {
            columnResult = null;
        }
        
        if (columnResult != null) {
            TextInputDialog enterValue = new TextInputDialog();
            enterValue.initStyle(StageStyle.UTILITY);
            enterValue.setTitle("Search Products");
            enterValue.setHeaderText(null);
            enterValue.setContentText("Enter Value");
            Optional<String> optValue = enterValue.showAndWait();
            String value;
            if (optValue.isPresent()) {
                value = optValue.get();
            } else {
                value = null;
            }

            if (value != null) {
                ObservableList<Product> searchQuery = FXCollections.observableArrayList();
                
                for(int i = 0; i < inventory.size(); i++) {
                    if (inventory.get(i).getID().equals(value) && columnResult.equals("ID")) {
                        searchQuery.add(inventory.get(i));
                    } else if (inventory.get(i).getName().equals(value) && columnResult.equals("Name")) {
                        searchQuery.add(inventory.get(i));
                    }
                }
                
                inventory.clear();
                inventory = searchQuery;
                inventoryTable.setItems(inventory);
                
                if (searchQuery.isEmpty()) {
                    Jain_inventory.infoAlert("Search Product", null, "Product does not exist.");
                }
            }
        }
    }
    
    @FXML
    private void filterProducts() {
        resetInventoryTable();
        List<String> columnChoices = new ArrayList<>();
        columnChoices.add("Quantity");
        columnChoices.add("Price");
        ChoiceDialog<String> chooseOption = new ChoiceDialog<>("Quantity", columnChoices);
        chooseOption.initStyle(StageStyle.UTILITY);
        chooseOption.setTitle("Filter Products");
        chooseOption.setHeaderText(null);
        chooseOption.setContentText("Choose field:");
        Optional<String> optColumnResult = chooseOption.showAndWait();
        String columnResult;
        
        if(optColumnResult.isPresent()) {
            columnResult = optColumnResult.get();
        } else {
            columnResult = null;
        }
        
        if (columnResult != null) {
            List<String> filterChoices = new ArrayList<>();
            filterChoices.add("Lesser than");
            filterChoices.add("Greater than");
            filterChoices.add("Lesser than equal to");
            filterChoices.add("Greater than equal to");
            filterChoices.add("Equal to");
            ChoiceDialog<String> chooseFilterOption = new ChoiceDialog<>("Equal to", filterChoices);
            chooseFilterOption.initStyle(StageStyle.UTILITY);
            chooseFilterOption.setTitle("Filter Products");
            chooseFilterOption.setHeaderText(null);
            chooseFilterOption.setContentText("Choose parameter:");
            Optional<String> optFilterResult = chooseFilterOption.showAndWait();
            String filterResult;

            if(optFilterResult.isPresent()) {
                filterResult = optFilterResult.get();
            } else {
                filterResult = null;
            }

            if (filterResult != null) {
                TextInputDialog valueDialog = new TextInputDialog();
                valueDialog.initStyle(StageStyle.UTILITY);
                valueDialog.setTitle("Filter Products");
                valueDialog.setHeaderText(null);
                valueDialog.setContentText("Enter Value:");
                Optional<String> optValue = valueDialog.showAndWait();
                String value;

                if (optValue.isPresent()) {
                    value = optValue.get();
                } else {
                    value = null;
                }

                if (value != null) {
                    ObservableList<Product> filtered = FXCollections.observableArrayList();
                    
                    switch (filterResult) {
                        case "Equal to" :
                            for (int i = 0; i < inventory.size(); i++) {
                            if (columnResult.equals("Quantity")) {
                                if (inventory.get(i).getQuantity() == Integer.valueOf(value)) {
                                    filtered.add(inventory.get(i));
                                }
                            } else if (columnResult.equals("Price")) {
                                if (inventory.get(i).getPrice() == Integer.valueOf(value)) {
                                    filtered.add(inventory.get(i));
                                }
                            }
                            }
                            break;
                        
                        case "Greater than": 
                            for (int i = 0; i < inventory.size(); i++) {
                            if (columnResult.equals("Quantity")) {
                                if (inventory.get(i).getQuantity() > Integer.valueOf(value)) {
                                    filtered.add(inventory.get(i));
                                }
                            } else if (columnResult.equals("Price")) {
                                if (inventory.get(i).getPrice() > Integer.valueOf(value)) {
                                    filtered.add(inventory.get(i));
                                }
                            }
                            }
                            break;
                        
                        case "Lesser than":
                            for (int i = 0; i < inventory.size(); i++) {
                            if (columnResult.equals("Quantity")) {
                                if (inventory.get(i).getQuantity() < Integer.valueOf(value)) {
                                    filtered.add(inventory.get(i));
                                }
                            } else if (columnResult.equals("Price")) {
                                if (inventory.get(i).getPrice() < Integer.valueOf(value)) {
                                    filtered.add(inventory.get(i));
                                }
                            }
                            }
                            break;
                            
                        case "Greater than equal to":
                            for (int i = 0; i < inventory.size(); i++) {
                            if (columnResult.equals("Quantity")) {
                                if (inventory.get(i).getQuantity() >= Integer.valueOf(value)) {
                                    filtered.add(inventory.get(i));
                                }
                            } else if (columnResult.equals("Price")) {
                                if (inventory.get(i).getPrice() >= Integer.valueOf(value)) {
                                    filtered.add(inventory.get(i));
                                }
                            }
                            }
                            break;
                            
                        case "Lesser than equal to":
                            for (int i = 0; i < inventory.size(); i++) {
                            if (columnResult.equals("Quantity")) {
                                if (inventory.get(i).getQuantity() <= Integer.valueOf(value)) {
                                    filtered.add(inventory.get(i));
                                }
                            } else if (columnResult.equals("Price")) {
                                if (inventory.get(i).getPrice() <= Integer.valueOf(value)) {
                                    filtered.add(inventory.get(i));
                                }
                            }
                            }
                            break;
                    }
                    
                    inventory.clear();
                    inventory = filtered;
                    inventoryTable.setItems(inventory);
                }
            }
        }
    }
    
    @FXML
    private void resetInventoryTable() {
        loadInventory();
        Collections.sort(inventory,new ProductComparator());
        inventoryTable.setItems(inventory);
    }
    
    private void saveInventoryTable() {
        try {
            String sqlDelete = "DELETE FROM APP.INVENTORY";
            String sqlInsert = "INSERT INTO APP.INVENTORY(PRODUCT_ID, NAME, QUANTITY_METRES, PRICE_METRES) VALUES(?,?,?,?)";
            PreparedStatement prepStatement = Jain_inventory.connection.prepareStatement(sqlDelete);
            prepStatement.executeUpdate();
            prepStatement = Jain_inventory.connection.prepareStatement(sqlInsert);
            
            ListIterator<Product> iter = inventory.listIterator();
            while (iter.hasNext()) {
                Product prod = iter.next();
                prepStatement.setString(1,prod.getID());
                prepStatement.setString(2,prod.getName());
                prepStatement.setInt(3,prod.getQuantity());
                prepStatement.setInt(4,prod.getPrice());
                prepStatement.addBatch();
            }
            
            prepStatement.executeBatch();
        } catch (SQLException exc) {
            System.out.println(exc);
        }
        
        resetInventoryTable();
    }
    
    @FXML
    private void calculateInventoryValue() {
        Iterator<Product> iter = inventory.iterator();
        int inventoryValue = 0;
        while (iter.hasNext()) {
            Product prod = iter.next();
            int prodValue = prod.getQuantity() * prod.getPrice();
            inventoryValue += prodValue;
        }
        String value = NumberFormat.getIntegerInstance().format(inventoryValue);
        Jain_inventory.infoAlert("Inventory Value", null, "Inventory value is \u20B9" + value + ".");
    }
    
    @FXML
    private void back() throws IOException{
        String source = "home.fxml";
        Parent root = FXMLLoader.load(getClass().getResource(source));
        Scene scene = new Scene (root);
        Jain_inventory.mainStage.setScene(scene);
        Jain_inventory.mainStage.show();
    }
    
    @FXML
    private void emailInventoryReport() throws AddressException {
        String tableInventory[][] = new String[inventory.size() + 1][4];
        tableInventory[0][0] = "ID";
        tableInventory[0][1] = "Name";
        tableInventory[0][2] = "Quantity";
        tableInventory[0][3] = "Price";
        
        for (int i = 1; i < inventory.size() + 1; i++) {
            tableInventory[i][0] = inventory.get(i-1).getID();
            tableInventory[i][1] = inventory.get(i-1).getName();
            String quantity = String.valueOf(inventory.get(i-1).getQuantity());
            String price = String.valueOf(inventory.get(i-1).getPrice());
            tableInventory[i][2] = quantity;
            tableInventory[i][3] = price;
        }
        
        String message = "";
        int maxLength = 0;
        for(String records[] : tableInventory){
          for(String current : records){
            int length = current.length(); 
            if (length > maxLength) maxLength = length;
          }
        }
        
        for(String records[] : tableInventory){
          for(String current : records){
            String value = current;
            message += String.format("%-" + maxLength + "s" + "\t", value);
          }
          message += "\n";
        }
        
        System.out.println(message);
        Jain_inventory.sendEmail("Jain Textiles Inventory", message);
    }
}