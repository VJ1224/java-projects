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

public class SuppliersController implements Initializable {

    @FXML private TableView<Person> supplierTable;
    @FXML private TableColumn<Person, String> idColumn;
    @FXML private TableColumn<Person, String> nameColumn;
    @FXML private TableColumn<Person, String> addressColumn;
    @FXML private TableColumn<Person, String> phoneColumn;

    private ObservableList<Person> suppliers = FXCollections.observableArrayList();
       
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        resetSupplierTable();
        idColumn.setCellValueFactory(new PropertyValueFactory("ID"));
        nameColumn.setCellValueFactory(new PropertyValueFactory("name"));
        addressColumn.setCellValueFactory(new PropertyValueFactory("address"));
        phoneColumn.setCellValueFactory(new PropertyValueFactory("phoneNumber"));
    }
    
    private void loadSuppliers() {
        suppliers.clear();
        try {
            String sqlSelect = "SELECT * FROM APP.SUPPLIERS";
            Statement statement;
            statement = Jain_inventory.connection.createStatement();
            ResultSet rst = statement.executeQuery(sqlSelect);

            while (rst.next()) {
                suppliers.add(new Person(rst.getString("PERSON_ID"), rst.getString("NAME"), rst.getString("ADDRESS"), rst.getString("PHONE_NUMBER")));
            }
        } catch (SQLException exc) {
            System.out.println(exc);
        }
    }
    
    @FXML
    private void resetSupplierTable() {
        loadSuppliers();
        Collections.sort(suppliers,new PersonComparator());
        supplierTable.setItems(suppliers);
    }
    
    @FXML
    private void addSupplier() {
        resetSupplierTable();
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
            enterID.setTitle("Add Supplier");
            enterID.setHeaderText(null);
            enterID.setContentText("Enter Supplier ID");
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
                
                if (suppliers.isEmpty() && matcher.matches()) {
                    idValidated = true;
                } else if (!matcher.matches()) {
                    Jain_inventory.infoAlert("Add Supplier", null, "Enter ID correctly.");
                    break;
                } else {
                    idValidated = true;
                }
                
                for(int i = 0; i < suppliers.size(); i++) {
                    if (suppliers.get(i).getID().equals(ID)) {
                        Jain_inventory.infoAlert("Add Supplier", null, "Supplier ID already exists.");
                        idValidated = false;
                        break;
                    }
                }
            } else {
                Jain_inventory.infoAlert("Add Supplier", null, "Enter ID correctly.");
            }
        }
        
        while (!nameValidated && !cancel) {
            TextInputDialog enterName = new TextInputDialog();
            enterName.initStyle(StageStyle.UTILITY);
            enterName.setTitle("Add Supplier");
            enterName.setHeaderText(null);
            enterName.setContentText("Enter Supplier Name");
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
                Jain_inventory.infoAlert("Add Supplier", null, "Enter name correctly.");
            }
        }
     
        while (!addressValidated && !cancel) {
            TextInputDialog enterAddress = new TextInputDialog();
            enterAddress.initStyle(StageStyle.UTILITY);
            enterAddress.setTitle("Add Supplier");
            enterAddress.setHeaderText(null);
            enterAddress.setContentText("Enter Supplier Address");
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
                Jain_inventory.infoAlert("Add Supplier", null, "Enter address correctly.");
            }
        }
        
        while (!phoneValidated && !cancel) {
            TextInputDialog enterPhone = new TextInputDialog();
            enterPhone.initStyle(StageStyle.UTILITY);
            enterPhone.setTitle("Add Supplier");
            enterPhone.setHeaderText(null);
            enterPhone.setContentText("Enter Supplier Phone Number");
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
                Jain_inventory.infoAlert("Add Supplier", null, "Enter phone number correctly.");
            }
        }
        
        if (!cancel) {
            suppliers.add(new Person(ID, name, address, phoneNumber));
            saveSupplierTable();
        }
    }
    
    @FXML 
    private void editSupplier() {    
        resetSupplierTable();
        TextInputDialog enterID = new TextInputDialog();
        enterID.initStyle(StageStyle.UTILITY);
        enterID.setTitle("Edit Supplier");
        enterID.setHeaderText(null);
        enterID.setContentText("Enter Supplier ID");
        String ID = enterID.showAndWait().get();
        
        int index = -1;
        
        String name = "";
        String address = "";
        String phoneNumber = "";
        
        for(int i = 0; i < suppliers.size(); i++) {
            if (suppliers.get(i).getID().equals(ID)) {
                index = i;
                
                List<String> columnChoices = new ArrayList<>();
                columnChoices.add("Name");
                columnChoices.add("Address");
                columnChoices.add("Phone Number");
                ChoiceDialog<String> chooseOption = new ChoiceDialog<>("Name", columnChoices);
                chooseOption.initStyle(StageStyle.UTILITY);
                chooseOption.setTitle("Edit Supplier");
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
                                enterName.setTitle("Edit Supplier");
                                enterName.setHeaderText(null);
                                enterName.setContentText("Enter Supplier Name:");
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
                                    Jain_inventory.infoAlert("Edit Supplier", null, "Enter name correctly.");
                                }
                            }

                            if (!cancel) {
                                Person p = suppliers.get(index);
                                p.setName(name);
                                suppliers.set(index, p);
                            }

                            break;

                        case "Address":
                            boolean addressValidated = false;

                            while (!addressValidated && !cancel) {
                                TextInputDialog enterAddress = new TextInputDialog();
                                enterAddress.initStyle(StageStyle.UTILITY);
                                enterAddress.setTitle("Edit Supplier");
                                enterAddress.setHeaderText(null);
                                enterAddress.setContentText("Enter Supplier Address:");
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
                                    Jain_inventory.infoAlert("Edit Supplier", null, "Enter address correctly.");
                                }
                            }

                            if (!cancel) {
                                Person p = suppliers.get(index);
                                p.setAddress(address);
                                suppliers.set(index, p);
                            }

                            break;

                        case "Phone Number":
                            boolean phoneValidated = false;

                            while (!phoneValidated && !cancel) {
                                TextInputDialog enterPhone = new TextInputDialog();
                                enterPhone.initStyle(StageStyle.UTILITY);
                                enterPhone.setTitle("Edit Supplier");
                                enterPhone.setHeaderText(null);
                                enterPhone.setContentText("Enter Supplier Phone Number:");
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
                                    Jain_inventory.infoAlert("Edit Supplier", null, "Enter phone number correctly.");
                                }
                            }

                            if (!cancel) {
                                Person p = suppliers.get(index);
                                p.setPhoneNumber(phoneNumber);
                                suppliers.set(index, p);
                            }

                            break;
                    }
                }
            }
        }
        
        saveSupplierTable();
        
        if (index == -1) {
            Jain_inventory.infoAlert("Edit Supplier", null, "Supplier ID does not exist.");
        }
    }
    
    @FXML
    private void removeSupplier() {
        resetSupplierTable();
        TextInputDialog enterID = new TextInputDialog();
        enterID.initStyle(StageStyle.UTILITY);
        enterID.setTitle("Remove Supplier");
        enterID.setHeaderText(null);
        enterID.setContentText("Enter Supplier ID");
        Optional<String> optID = enterID.showAndWait();
        String ID;
        
        if (optID.isPresent()) {
            ID = optID.get();
        } else {
            ID = null;
        }
        
        if (ID != null) {
            int index = -1;
            for(int i = 0; i < suppliers.size(); i++) {
                if (suppliers.get(i).getID().equals(ID)) {
                    index = i;
                    break;
                }
            }

            if (index >= 0) {
                if (Jain_inventory.confirmAlert("Remove Supplier", null, "Are you sure you want to remove Supplier " + ID + "?"))
                    suppliers.remove(index);
            } else {
                Jain_inventory.infoAlert("Remove Supplier", null, "Supplier ID does not exist.");
            }
        }
        
        saveSupplierTable();
    }
    
    @FXML
    private void searchSuppliers() {
        resetSupplierTable();
        List<String> columnChoices = new ArrayList<>();
        columnChoices.add("ID");
        columnChoices.add("Name");
        ChoiceDialog<String> chooseOption = new ChoiceDialog<>("ID", columnChoices);
        chooseOption.initStyle(StageStyle.UTILITY);
        chooseOption.setTitle("Search Suppliers");
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
            enterValue.setTitle("Search Suppliers");
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
                
                for(int i = 0; i < suppliers.size(); i++) {
                    if (suppliers.get(i).getID().equals(value) && columnResult.equals("ID")) {
                        searchQuery.add(suppliers.get(i));
                    } else if (suppliers.get(i).getName().equals(value) && columnResult.equals("Name")) {
                        searchQuery.add(suppliers.get(i));
                    }
                }
                
                suppliers.clear();
                suppliers = searchQuery;
                supplierTable.setItems(suppliers);
                
                if (searchQuery.isEmpty()) {
                    Jain_inventory.infoAlert("Search Suppliers", null, "Supplier does not exist.");
                }
            }
        }
    }
    
    private void saveSupplierTable() {
        try {
            String sqlDelete = "DELETE FROM APP.SUPPLIERS";
            String sqlInsert = "INSERT INTO APP.SUPPLIERS(PERSON_ID, NAME, ADDRESS, PHONE_NUMBER) VALUES(?,?,?,?)";
            PreparedStatement prepStatement = Jain_inventory.connection.prepareStatement(sqlDelete);
            prepStatement.executeUpdate();
            prepStatement = Jain_inventory.connection.prepareStatement(sqlInsert);
            
            ListIterator<Person> iter = suppliers.listIterator();
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
        
        resetSupplierTable();
    }
    
    @FXML
    private void emailSuppliersReport() throws AddressException {
        String[][] tableSuppliers = new String[suppliers.size() + 1][4];
        tableSuppliers[0][0] = "ID";
        tableSuppliers[0][1] = "Name";
        tableSuppliers[0][2] = "Address";
        tableSuppliers[0][3] = "Phone Number";
        
        for (int i = 1; i < suppliers.size() + 1; i++) {
            tableSuppliers[i][0] = suppliers.get(i-1).getID();
            tableSuppliers[i][1] = suppliers.get(i-1).getName();
            tableSuppliers[i][2] = suppliers.get(i-1).getAddress();
            tableSuppliers[i][3] = suppliers.get(i-1).getPhoneNumber();
        }
        
        String message = "";
        int maxLength = 0;
        for(String records[] : tableSuppliers){
          for(String current : records){
            int length = current.length(); 
            if (length > maxLength) maxLength = length;
          }
        }
        
        for(String records[] : tableSuppliers){
          for(String current : records){
            String value = current;
            message += String.format("%-" + maxLength + "s" + "\t", value);
          }
          message += "\n";
        }
        
        System.out.println(message);
        Jain_inventory.sendEmail("Jain Textiles Suppliers", message);
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
    private void openCustomers() throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("customers.fxml"));
        Scene scene = new Scene(root);
        Jain_inventory.mainStage.setScene(scene);
        Jain_inventory.mainStage.show();
    }
}
