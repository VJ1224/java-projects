package jain_inventory.view;

import jain_inventory.Jain_inventory;
import jain_inventory.Person;
import jain_inventory.PersonComparator;
import java.io.IOException;
import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ListIterator;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.StageStyle;
import javax.mail.internet.AddressException;

public class CustomersController implements Initializable {
    
    @FXML private TableView<Person> customerTable;
    @FXML private TableColumn<Person, String> idColumn;
    @FXML private TableColumn<Person, String> nameColumn;
    @FXML private TableColumn<Person, String> addressColumn;
    @FXML private TableColumn<Person, String> phoneColumn;

    private ObservableList<Person> customers = FXCollections.observableArrayList();
       
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        resetCustomerTable();
        idColumn.setCellValueFactory(new PropertyValueFactory("ID"));
        nameColumn.setCellValueFactory(new PropertyValueFactory("name"));
        addressColumn.setCellValueFactory(new PropertyValueFactory("address"));
        phoneColumn.setCellValueFactory(new PropertyValueFactory("phoneNumber"));
    }
    
    private void loadCustomers() {
        customers.clear();
        try {
            String sqlSelect = "SELECT * FROM APP.CUSTOMERS";
            Statement statement;
            statement = Jain_inventory.connection.createStatement();
            ResultSet rst = statement.executeQuery(sqlSelect);

            while (rst.next()) {
                customers.add(new Person(rst.getString("PERSON_ID"), rst.getString("NAME"), rst.getString("ADDRESS"), rst.getString("PHONE_NUMBER")));
            }
        } catch (SQLException exc) {
            System.out.println(exc);
        }
    }
    
    @FXML
    private void resetCustomerTable() {
        loadCustomers();
        Collections.sort(customers,new PersonComparator());
        customerTable.setItems(customers);
    }
    
    @FXML
    private void addCustomer() {
        resetCustomerTable();
        String ID = "";
        String name = "";
        String address = "";
        String phoneNumber = "";
        
        boolean cancel = false;        
        boolean idValidated = false;
        boolean nameValidated = false;
        boolean addressValidated = false;
        boolean phoneValidated = false;
        
        while (!idValidated && !cancel) {
            TextInputDialog enterID = new TextInputDialog();
            enterID.initStyle(StageStyle.UTILITY);
            enterID.setTitle("Add Customer");
            enterID.setHeaderText(null);
            enterID.setContentText("Enter Customer ID");
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
                
                if (customers.isEmpty() && matcher.matches()) {
                    idValidated = true;
                } else if (!matcher.matches()) {
                    Jain_inventory.infoAlert("Add Customer", null, "Enter ID correctly.");
                    break;
                } else {
                    idValidated = true;
                }
                
                for(int i = 0; i < customers.size(); i++) {
                    if (customers.get(i).getID().equals(ID)) {
                        Jain_inventory.infoAlert("Add Customer", null, "Customer ID already exists.");
                        idValidated = false;
                        break;
                    }
                }
            } else if (!idValidated) {
                Jain_inventory.infoAlert("Add Customer", null, "Enter ID correctly.");
            }
        }
        
        while (!nameValidated && !cancel) {
            TextInputDialog enterName = new TextInputDialog();
            enterName.initStyle(StageStyle.UTILITY);
            enterName.setTitle("Add Customer");
            enterName.setHeaderText(null);
            enterName.setContentText("Enter Customer Name");
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
                Jain_inventory.infoAlert("Add Customer", null, "Enter name correctly.");
            }
        }
     
        while (!addressValidated && !cancel) {
            TextInputDialog enterAddress = new TextInputDialog();
            enterAddress.initStyle(StageStyle.UTILITY);
            enterAddress.setTitle("Add Customer");
            enterAddress.setHeaderText(null);
            enterAddress.setContentText("Enter Customer Address");
            Optional<String> optAddress = enterAddress.showAndWait();
            
            if (optAddress.isPresent()) {
                address = optAddress.get();
            } else {
                address = null;
            }
            
            if (address == null) {
                cancel = true;
            } else if (Jain_inventory.isText(address)) {
                addressValidated = true;
            } else {
                Jain_inventory.infoAlert("Add Customer", null, "Enter address correctly.");
            }
        }
        
        while (!phoneValidated && !cancel) {
            TextInputDialog enterPhone = new TextInputDialog();
            enterPhone.initStyle(StageStyle.UTILITY);
            enterPhone.setTitle("Add Customer");
            enterPhone.setHeaderText(null);
            enterPhone.setContentText("Enter Customer Phone Number");
            Optional<String> optPhone = enterPhone.showAndWait();
            
            if (optPhone.isPresent()) {
                phoneNumber = optPhone.get();
            } else {
                phoneNumber = null;
            }
            
            Matcher matcher = Jain_inventory.phonePattern.matcher(phoneNumber);
            
            if (phoneNumber == null) {
                cancel = true;
            } else if (matcher.matches()) {
                phoneValidated = true;
            } else {
                Jain_inventory.infoAlert("Add Customer", null, "Enter phone number correctly.");
            }
        }
        
        if (!cancel) {
            customers.add(new Person(ID, name, address, phoneNumber));
            saveCustomerTable();
        }
    }
    
    @FXML 
    private void editCustomer() {    
        resetCustomerTable();
        TextInputDialog enterID = new TextInputDialog();
        enterID.initStyle(StageStyle.UTILITY);
        enterID.setTitle("Edit Customer");
        enterID.setHeaderText(null);
        enterID.setContentText("Enter Customer ID");
        Optional<String> optID = enterID.showAndWait();
        String ID;
        
        if (optID.isPresent()) {
            ID = optID.get();
        } else {
            ID = null;
        }
        
        int index = -1;
        
        String name = "";
        String address = "";
        String phoneNumber = "";
        
        if (ID != null) {
            for(int i = 0; i < customers.size(); i++) {
                if (customers.get(i).getID().equals(ID)) {
                    index = i;

                    List<String> columnChoices = new ArrayList<>();
                    columnChoices.add("Name");
                    columnChoices.add("Address");
                    columnChoices.add("Phone Number");
                    ChoiceDialog<String> chooseOption = new ChoiceDialog<>("Name", columnChoices);
                    chooseOption.initStyle(StageStyle.UTILITY);
                    chooseOption.setTitle("Edit Customer");
                    chooseOption.setHeaderText(null);
                    chooseOption.setContentText("Choose column:");
                    Optional<String> optColumnResult = chooseOption.showAndWait();
                    String columnResult;

                    if(optColumnResult.isPresent()) {
                        columnResult = optColumnResult.get();
                    } else {
                        columnResult = null;
                    }

                    boolean cancel = false;

                    if (columnResult != null) {    
                        switch(columnResult) {
                            case "Name":
                                boolean nameValidated = false;

                                while (!nameValidated && !cancel) {
                                    TextInputDialog enterName = new TextInputDialog();
                                    enterName.initStyle(StageStyle.UTILITY);
                                    enterName.setTitle("Edit Customer");
                                    enterName.setHeaderText(null);
                                    enterName.setContentText("Enter Customer Name:");
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
                                        Jain_inventory.infoAlert("Edit Customer", null, "Enter name correctly.");
                                    }
                                }

                                if (!cancel) {
                                    Person p = customers.get(index);
                                    p.setName(name);
                                    customers.set(index, p);
                                }

                                break;

                            case "Address":
                                boolean addressValidated = false;

                                while (!addressValidated && !cancel) {
                                    TextInputDialog enterAddress = new TextInputDialog();
                                    enterAddress.initStyle(StageStyle.UTILITY);
                                    enterAddress.setTitle("Edit Customer");
                                    enterAddress.setHeaderText(null);
                                    enterAddress.setContentText("Enter Customer Address:");
                                    Optional<String> optAddress = enterAddress.showAndWait();

                                    if (optAddress.isPresent()) {
                                        address = optAddress.get();
                                    } else {
                                        address = null;
                                    }

                                    if (address == null) {
                                        cancel = true;
                                    } else if (Jain_inventory.isText(address)) {
                                        addressValidated = true;
                                    } else {
                                        Jain_inventory.infoAlert("Edit Customer", null, "Enter address correctly.");
                                    }
                                }

                                if (!cancel) {
                                    Person p = customers.get(index);
                                    p.setAddress(address);
                                    customers.set(index, p);
                                }

                                break;

                            case "Phone Number":
                                boolean phoneValidated = false;

                                while (!phoneValidated && !cancel) {
                                    TextInputDialog enterPhone = new TextInputDialog();
                                    enterPhone.initStyle(StageStyle.UTILITY);
                                    enterPhone.setTitle("Edit Customer");
                                    enterPhone.setHeaderText(null);
                                    enterPhone.setContentText("Enter Customer Phone Number:");
                                    Optional<String> optPhone = enterPhone.showAndWait();

                                    if (optPhone.isPresent()) {
                                        phoneNumber = optPhone.get();
                                    } else {
                                        phoneNumber = null;
                                    }

                                    Matcher matcher = Jain_inventory.phonePattern.matcher(phoneNumber);

                                    if (phoneNumber == null) {
                                        cancel = true;
                                    } else if (matcher.matches()) {
                                        phoneValidated = true;
                                    } else {
                                        Jain_inventory.infoAlert("Edit Customer", null, "Enter phone number correctly.");
                                    }
                                }

                                if (!cancel) {
                                    Person p = customers.get(index);
                                    p.setPhoneNumber(phoneNumber);
                                    customers.set(index, p);
                                    saveCustomerTable();
                                }

                                break;
                        }
                    }
                }
            }
        }
        
        
        if (index == -1 && optID.isPresent()) {
            Jain_inventory.infoAlert("Edit Customer", null, "Customer ID does not exist.");
        }
    }
    
    @FXML
    private void removeCustomer() {
        resetCustomerTable();
        TextInputDialog enterID = new TextInputDialog();
        enterID.initStyle(StageStyle.UTILITY);
        enterID.setTitle("Remove Customer");
        enterID.setHeaderText(null);
        enterID.setContentText("Enter Customer ID");
        Optional<String> optID = enterID.showAndWait();
        String ID;
        
        if (optID.isPresent()) {
            ID = optID.get();
        } else {
            ID = null;
        }
        
        if (ID != null) {
            int index = -1;
            for(int i = 0; i < customers.size(); i++) {
                if (customers.get(i).getID().equals(ID)) {
                    index = i;
                    break;
                }
            }

            if (index >= 0) {
                if (Jain_inventory.confirmAlert("Remove Customer", null, "Are you sure you want to remove Customer " + ID + "?"))    
                    customers.remove(index);
            } else {
                Jain_inventory.infoAlert("Remove Customer", null, "Customer ID does not exist.");
            }
        }
        
        saveCustomerTable();
    }
    
    @FXML
    private void searchCustomers() {
        resetCustomerTable();
        List<String> columnChoices = new ArrayList<>();
        columnChoices.add("ID");
        columnChoices.add("Name");
        ChoiceDialog<String> chooseOption = new ChoiceDialog<>("ID", columnChoices);
        chooseOption.initStyle(StageStyle.UTILITY);
        chooseOption.setTitle("Search Customers");
        chooseOption.setHeaderText(null);
        chooseOption.setContentText("Choose column:");
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
            enterValue.setTitle("Search Customers");
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
                ObservableList<Person> searchQuery = FXCollections.observableArrayList();
                
                for(int i = 0; i < customers.size(); i++) {
                    if (customers.get(i).getID().equals(value) && columnResult.equals("ID")) {
                        searchQuery.add(customers.get(i));
                    } else if (customers.get(i).getName().equals(value) && columnResult.equals("Name")) {
                        searchQuery.add(customers.get(i));
                    }
                }
                
                customers.clear();
                customers = searchQuery;
                customerTable.setItems(customers);
                
                if (searchQuery.isEmpty()) {
                    Jain_inventory.infoAlert("Search Customers", null, "Customer does not exist.");
                }
            }
        }
    }
    
    private void saveCustomerTable() {
        try {
            String sqlDelete = "DELETE FROM APP.CUSTOMERS";
            String sqlInsert = "INSERT INTO APP.CUSTOMERS(PERSON_ID, NAME, ADDRESS, PHONE_NUMBER) VALUES(?,?,?,?)";
            PreparedStatement prepStatement = Jain_inventory.connection.prepareStatement(sqlDelete);
            prepStatement.executeUpdate();
            prepStatement = Jain_inventory.connection.prepareStatement(sqlInsert);
            
            ListIterator<Person> iter = customers.listIterator();
            while (iter.hasNext()) {
                Person p = iter.next();
                prepStatement.setString(1,p.getID());
                prepStatement.setString(2,p.getName());
                prepStatement.setString(3,p.getAddress());
                prepStatement.setString(4,p.getPhoneNumber());
                prepStatement.addBatch();
            }
            
            prepStatement.executeBatch();
        } catch (SQLException exc) {
            System.out.println(exc);
        }
        
        resetCustomerTable();
    }
    
    @FXML
    private void emailCustomerReport() throws AddressException {
        String[][] tableCustomers = new String[customers.size() + 1][4];
        tableCustomers[0][0] = "ID";
        tableCustomers[0][1] = "Name";
        tableCustomers[0][2] = "Address";
        tableCustomers[0][3] = "Phone Number";
        
        for (int i = 1; i < customers.size() + 1; i++) {
            tableCustomers[i][0] = customers.get(i-1).getID();
            tableCustomers[i][1] = customers.get(i-1).getName();
            tableCustomers[i][2] = customers.get(i-1).getAddress();
            tableCustomers[i][3] = customers.get(i-1).getPhoneNumber();
        }
        
        String message = "";
        int maxLength = 0;
        for(String records[] : tableCustomers){
          for(String current : records){
            int length = current.length(); 
            if (length > maxLength) maxLength = length;
          }
        }
        
        for(String records[] : tableCustomers){
          for(String current : records){
            String value = current;
            message += String.format("%-" + maxLength + "s" + "\t", value);
          }
          message += "\n";
        }
        
        System.out.println(message);
        Jain_inventory.sendEmail("Jain Textiles Customers", message);
    }
    
    @FXML
    private void back() throws IOException{
        Parent root = FXMLLoader.load(getClass().getResource("home.fxml"));
        Scene scene = new Scene (root);
        Jain_inventory.mainStage.setScene(scene);
        Jain_inventory.mainStage.show();
    } 
    
    @FXML
    private void openPurchaseOrders() throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("purchaseOrders.fxml"));
        Scene scene = new Scene(root);
        Jain_inventory.mainStage.setScene(scene);
        Jain_inventory.mainStage.show();
    }
    
    @FXML
    private void openSalesOrders() throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("salesOrders.fxml"));
        Scene scene = new Scene(root);
        Jain_inventory.mainStage.setScene(scene);
        Jain_inventory.mainStage.show();
    }
    
    @FXML
    private void openSuppliers() throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("suppliers.fxml"));
        Scene scene = new Scene(root);
        Jain_inventory.mainStage.setScene(scene);
        Jain_inventory.mainStage.show();
    }
}
