package jain_inventory.view;

import jain_inventory.Jain_inventory;
import jain_inventory.Order;
import jain_inventory.OrderComparator;
import jain_inventory.Person;
import jain_inventory.Product;
import java.io.IOException;
import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.NumberFormat;
import java.time.LocalDate;
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
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.stage.StageStyle;
import javax.mail.internet.AddressException;

public class PurchaseOrdersController implements Initializable {

    @FXML
    private TableView<Order> ordersTable;
    @FXML
    private TableColumn<Order, String> idColumn;
    @FXML
    private TableColumn<Order, String> productIDColumn;
    @FXML
    private TableColumn<Order, String> personIDColumn;
    @FXML
    private TableColumn<Order, Integer> quantityColumn;
    @FXML
    private TableColumn<Order, LocalDate> dateColumn;

    private ObservableList<Order> orders = FXCollections.observableArrayList();
    private ObservableList<Person> suppliers = FXCollections.observableArrayList();
    private ObservableList<Product> inventory = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        resetOrderTable();
        loadInventory();
        loadSuppliers();
        idColumn.setCellValueFactory(new PropertyValueFactory("ID"));
        productIDColumn.setCellValueFactory(new PropertyValueFactory("productID"));
        personIDColumn.setCellValueFactory(new PropertyValueFactory("personID"));
        quantityColumn.setCellValueFactory(new PropertyValueFactory("quantity"));
        dateColumn.setCellValueFactory(new PropertyValueFactory("deliveryDate"));
    }

    @FXML
    private void addOrder() {
        resetOrderTable();
        String ID = "";
        String productID = "";
        String personID = "";
        int quantity = 0;
        LocalDate deliveryDate = null;

        boolean cancel = false;
        boolean idValidated = false;
        boolean productIDValidated = false;
        boolean personIDValidated = false;
        boolean quantityValidated = false;
        boolean dateValidated = false;

        while (!idValidated && !cancel) {
            TextInputDialog enterID = new TextInputDialog();
            enterID.initStyle(StageStyle.UTILITY);
            enterID.setTitle("Add Order");
            enterID.setHeaderText(null);
            enterID.setContentText("Enter Order ID");
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

                if (orders.isEmpty() && matcher.matches()) {
                    idValidated = true;
                } else if (!matcher.matches()) {
                    Jain_inventory.infoAlert("Add Order", null, "Enter ID correctly.");
                    break;
                } else {
                    idValidated = true;
                }

                for (int i = 0; i < orders.size(); i++) {
                    if (orders.get(i).getID().equals(ID)) {
                        Jain_inventory.infoAlert("Add Order", null, "Order ID already exists.");
                        idValidated = false;
                        break;
                    }
                }
            } else if (!idValidated) {
                Jain_inventory.infoAlert("Add Order", null, "Enter ID correctly.");
            }
        }

        while (!productIDValidated && !cancel) {
            TextInputDialog enterProductID = new TextInputDialog();
            enterProductID.initStyle(StageStyle.UTILITY);
            enterProductID.setTitle("Add Order");
            enterProductID.setHeaderText(null);
            enterProductID.setContentText("Enter Product ID");
            Optional<String> optProductID = enterProductID.showAndWait();

            if (optProductID.isPresent()) {
                productID = optProductID.get();
            } else {
                productID = null;
            }

            if (productID == null) {
                cancel = true;
            } else if (checkProductID(productID)) {
                productIDValidated = true;
            } else {
                Jain_inventory.infoAlert("Add Order", null, "Enter Product ID correctly.");
            }
        }

        while (!personIDValidated && !cancel) {
            TextInputDialog enterPersonID = new TextInputDialog();
            enterPersonID.initStyle(StageStyle.UTILITY);
            enterPersonID.setTitle("Add Order");
            enterPersonID.setHeaderText(null);
            enterPersonID.setContentText("Enter Supplier ID");
            Optional<String> optPersonID = enterPersonID.showAndWait();

            if (optPersonID.isPresent()) {
                personID = optPersonID.get();
            } else {
                personID = null;
            }

            if (personID == null) {
                cancel = true;
            } else if (checkPersonID(personID)) {
                personIDValidated = true;
            } else {
                Jain_inventory.infoAlert("Add Order", null, "Enter Supplier ID correctly.");
            }
        }

        while (!quantityValidated && !cancel) {
            TextInputDialog enterQuantity = new TextInputDialog();
            enterQuantity.initStyle(StageStyle.UTILITY);
            enterQuantity.setTitle("Add Order");
            enterQuantity.setHeaderText(null);
            enterQuantity.setContentText("Enter Order Quantity");
            Optional<String> optQuantity = enterQuantity.showAndWait();
            String strQuantity;

            if (optQuantity.isPresent()) {
                strQuantity = optQuantity.get();
            } else {
                strQuantity = null;
            }

            if (strQuantity == null) {
                cancel = true;
            } else if (Jain_inventory.isInt(strQuantity)) {
                quantity = Integer.valueOf(strQuantity);
                quantityValidated = true;
            } else {
                Jain_inventory.infoAlert("Add Order", null, "Enter quantity correctly.");
            }
        }

        while (!dateValidated && !cancel) {
            Dialog<LocalDate> dateDialog = new Dialog<>();
            dateDialog.initStyle(StageStyle.UTILITY);
            dateDialog.setTitle("Enter Delivery Date");
            dateDialog.setHeaderText(null);
            DialogPane dialogPane = dateDialog.getDialogPane();
            dialogPane.getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
            DatePicker datePicker = new DatePicker();
            datePicker.setShowWeekNumbers(false);
            dialogPane.setContent(new VBox(8, datePicker));
            dateDialog.setResultConverter((ButtonType button) -> {
                if (button == ButtonType.OK) {
                    return datePicker.getValue();
                }
                return null;
            });

            Optional<LocalDate> optDate = dateDialog.showAndWait();

            if (optDate.isPresent()) {
                deliveryDate = optDate.get();
            } else {
                deliveryDate = null;
            }

            if (deliveryDate == null) {
                cancel = true;
            } else if (deliveryDate.isAfter(LocalDate.now())) {
                dateValidated = true;
            } else {
                Jain_inventory.infoAlert("Add Order", null, "Enter delivery date correctly.");
            }
        }

        if (!cancel) {
            orders.add(new Order(ID, productID, personID, quantity, deliveryDate));
            saveOrdersTable();
        }
    }

    @FXML
    private void removeOrder() {
        resetOrderTable();
        TextInputDialog enterID = new TextInputDialog();
        enterID.initStyle(StageStyle.UTILITY);
        enterID.setTitle("Remove Order");
        enterID.setHeaderText(null);
        enterID.setContentText("Enter Order ID");
        Optional<String> optID = enterID.showAndWait();
        String ID;

        if (optID.isPresent()) {
            ID = optID.get();
        } else {
            ID = null;
        }

        if (ID != null) {
            int index = -1;
            for (int i = 0; i < orders.size(); i++) {
                if (orders.get(i).getID().equals(ID)) {
                    index = i;
                    break;
                }
            }

            if (index >= 0) {
                if (Jain_inventory.confirmAlert("Remove Order", null, "Are you sure you want to remove Order " + ID + "?"))
                    orders.remove(index);
            } else {
                Jain_inventory.infoAlert("Remove Order", null, "Order ID does not exist.");
            }
        }

        saveOrdersTable();
    }

    @FXML
    private void editOrder() {
        resetOrderTable();
        TextInputDialog enterID = new TextInputDialog();
        enterID.initStyle(StageStyle.UTILITY);
        enterID.setTitle("Edit Order");
        enterID.setHeaderText(null);
        enterID.setContentText("Enter Ordert ID");
        Optional<String> optID = enterID.showAndWait();
        String ID;
        
        if (optID.isPresent()) {
            ID = optID.get();
        } else {
            ID = null;
        }
        
        int index = -1;

        String productID = "";
        String personID = "";
        int quantity = 0;
        LocalDate deliveryDate = LocalDate.now();

        if (ID != null) {
            for (int i = 0; i < orders.size(); i++) {
                if (orders.get(i).getID().equals(ID)) {
                    index = i;

                    List<String> columnChoices = new ArrayList<>();
                    columnChoices.add("Product ID");
                    columnChoices.add("Supplier ID");
                    columnChoices.add("Quantity");
                    columnChoices.add("Delivery Date");
                    ChoiceDialog<String> chooseOption = new ChoiceDialog<>("Product ID", columnChoices);
                    chooseOption.initStyle(StageStyle.UTILITY);
                    chooseOption.setTitle("Edit Order");
                    chooseOption.setHeaderText(null);
                    chooseOption.setContentText("Choose column:");
                    Optional<String> optColumnResult = chooseOption.showAndWait();
                    String columnResult;

                    if (optColumnResult.isPresent()) {
                        columnResult = optColumnResult.get();
                    } else {
                        columnResult = null;
                    }

                    boolean cancel = false;

                    if (columnResult != null) {
                        switch (columnResult) {
                            case "Product ID":
                                boolean productIDValidated = false;
                                while (!productIDValidated && !cancel) {
                                    TextInputDialog enterProductID = new TextInputDialog();
                                    enterProductID.initStyle(StageStyle.UTILITY);
                                    enterProductID.setTitle("Edit Order");
                                    enterProductID.setHeaderText(null);
                                    enterProductID.setContentText("Enter Product ID");
                                    Optional<String> optProductID = enterProductID.showAndWait();

                                    if (optProductID.isPresent()) {
                                        productID = optProductID.get();
                                    } else {
                                        productID = null;
                                    }

                                    if (productID == null) {
                                        cancel = true;
                                    } else if (checkProductID(productID)) {
                                        productIDValidated = true;
                                    } else {
                                        Jain_inventory.infoAlert("Edit Order", null, "Enter Product ID correctly.");
                                    }
                                }

                                if (!cancel) {
                                    Order ord = orders.get(index);
                                    ord.setProductID(productID);
                                    orders.set(index, ord);
                                    ordersTable.setItems(orders);
                                    saveOrdersTable();
                                }

                                break;

                            case "Supplier ID":
                                boolean personIDValidated = false;
                                while (!personIDValidated && !cancel) {
                                    TextInputDialog enterPersonID = new TextInputDialog();
                                    enterPersonID.initStyle(StageStyle.UTILITY);
                                    enterPersonID.setTitle("Edit Order");
                                    enterPersonID.setHeaderText(null);
                                    enterPersonID.setContentText("Enter Supplier ID");
                                    Optional<String> optPersonID = enterPersonID.showAndWait();

                                    if (optPersonID.isPresent()) {
                                        personID = optPersonID.get();
                                    } else {
                                        personID = null;
                                    }

                                    if (personID == null) {
                                        cancel = true;
                                    } else if (checkPersonID(personID)) {
                                        personIDValidated = true;
                                    } else {
                                        Jain_inventory.infoAlert("Edit Order", null, "Enter Supplier ID correctly.");
                                    }
                                }

                                if (!cancel) {
                                    Order ord = orders.get(index);
                                    ord.setPersonID(personID);
                                    orders.set(index, ord);
                                    ordersTable.setItems(orders);
                                    saveOrdersTable();
                                }

                                break;

                            case "Quantity":
                                boolean quantityValidated = false;
                                while (!quantityValidated && !cancel) {
                                    TextInputDialog enterQuantity = new TextInputDialog();
                                    enterQuantity.initStyle(StageStyle.UTILITY);
                                    enterQuantity.setTitle("Edit Order");
                                    enterQuantity.setHeaderText(null);
                                    enterQuantity.setContentText("Enter Order Quantity");
                                    Optional<String> optQuantity = enterQuantity.showAndWait();
                                    String strQuantity;

                                    if (optQuantity.isPresent()) {
                                        strQuantity = optQuantity.get();
                                    } else {
                                        strQuantity = null;
                                    }

                                    if (strQuantity == null) {
                                        cancel = true;
                                    } else if (Jain_inventory.isInt(strQuantity)) {
                                        quantity = Integer.valueOf(strQuantity);
                                        quantityValidated = true;
                                    } else {
                                        Jain_inventory.infoAlert("Edit Order", null, "Enter quantity correctly.");
                                    }
                                }

                                if (!cancel) {
                                    Order ord = orders.get(index);
                                    ord.setQuantity(quantity);
                                    orders.set(index, ord);
                                    ordersTable.setItems(orders);
                                    saveOrdersTable();
                                }

                                break;

                            case "Delivery Date":
                                boolean dateValidated = false;
                                while (!dateValidated && !cancel) {
                                    Dialog<LocalDate> dateDialog = new Dialog<>();
                                    dateDialog.initStyle(StageStyle.UTILITY);
                                    dateDialog.setTitle("Enter Delivery Date");
                                    dateDialog.setHeaderText(null);
                                    DialogPane dialogPane = dateDialog.getDialogPane();
                                    dialogPane.getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
                                    DatePicker datePicker = new DatePicker();
                                    datePicker.setShowWeekNumbers(false);
                                    dialogPane.setContent(new VBox(8, datePicker));
                                    dateDialog.setResultConverter((ButtonType button) -> {
                                        if (button == ButtonType.OK) {
                                            return datePicker.getValue();
                                        }
                                        return null;
                                    });

                                    Optional<LocalDate> optDate = dateDialog.showAndWait();

                                    if (optDate.isPresent()) {
                                        deliveryDate = optDate.get();
                                    } else {
                                        deliveryDate = null;
                                    }

                                    if (deliveryDate == null) {
                                        cancel = true;
                                    } else if (deliveryDate.isAfter(LocalDate.now())) {
                                        dateValidated = true;
                                    } else {
                                        Jain_inventory.infoAlert("Edit Order", null, "Enter delivery date correctly.");
                                    }
                                }

                                if (!cancel) {
                                    Order ord = orders.get(index);
                                    ord.setDeliveryDate(deliveryDate);
                                    orders.set(index, ord);
                                    saveOrdersTable();
                                }

                                break;
                        }
                    }
                }
            }
        }

        if (index == -1 && optID.isPresent()) {
            Jain_inventory.infoAlert("Edit Order", null, "Order ID does not exist.");
        }
    }

    @FXML
    private void searchOrders() {
        resetOrderTable();
        List<String> columnChoices = new ArrayList<>();
        columnChoices.add("Order ID");
        columnChoices.add("Product ID");
        columnChoices.add("Supplier ID");
        ChoiceDialog<String> chooseOption = new ChoiceDialog<>("Order ID", columnChoices);
        chooseOption.initStyle(StageStyle.UTILITY);
        chooseOption.setTitle("Search Orders");
        chooseOption.setHeaderText(null);
        chooseOption.setContentText("Choose column:");
        Optional<String> optColumnResult = chooseOption.showAndWait();
        String columnResult;

        if (optColumnResult.isPresent()) {
            columnResult = optColumnResult.get();
        } else {
            columnResult = null;
        }

        if (columnResult != null) {
            TextInputDialog enterValue = new TextInputDialog();
            enterValue.initStyle(StageStyle.UTILITY);
            enterValue.setTitle("Search Orders");
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
                ObservableList<Order> searchQuery = FXCollections.observableArrayList();

                for (int i = 0; i < orders.size(); i++) {
                    if (orders.get(i).getID().equals(value) && columnResult.equals("Order ID")) {
                        searchQuery.add(orders.get(i));
                    } else if (orders.get(i).getProductID().equals(value) && columnResult.equals("Product ID")) {
                        searchQuery.add(orders.get(i));
                    } else if (orders.get(i).getPersonID().equals(value) && columnResult.equals("Supplier ID")) {
                        searchQuery.add(orders.get(i));
                    }
                }

                orders.clear();
                orders = searchQuery;
                ordersTable.setItems(orders);

                if (searchQuery.isEmpty()) {
                    Jain_inventory.infoAlert("Search Orders", null, "Order does not exist.");
                }
            }
        }
    }

    @FXML
    private void filterOrders() {
        resetOrderTable();
        List<String> columnChoices = new ArrayList<>();
        columnChoices.add("Quantity");
        columnChoices.add("Delivery Date");
        ChoiceDialog<String> chooseOption = new ChoiceDialog<>("Quantity", columnChoices);
        chooseOption.initStyle(StageStyle.UTILITY);
        chooseOption.setTitle("Filter Orders");
        chooseOption.setHeaderText(null);
        chooseOption.setContentText("Choose column:");
        Optional<String> optColumnResult = chooseOption.showAndWait();
        String columnResult;

        if (optColumnResult.isPresent()) {
            columnResult = optColumnResult.get();
        } else {
            columnResult = null;
        }

        if (columnResult != null) {
            if (columnResult.equals("Quantity")) {
                List<String> filterChoices = new ArrayList<>();
                filterChoices.add("Lesser than");
                filterChoices.add("Greater than");
                filterChoices.add("Lesser than equal to");
                filterChoices.add("Greater than equal to");
                filterChoices.add("Equal to");
                ChoiceDialog<String> chooseFilterOption = new ChoiceDialog<>("Equal to", filterChoices);
                chooseFilterOption.initStyle(StageStyle.UTILITY);
                chooseFilterOption.setTitle("Filter Orders");
                chooseFilterOption.setHeaderText(null);
                chooseFilterOption.setContentText("Choose parameter:");
                Optional<String> optFilterResult = chooseFilterOption.showAndWait();
                String filterResult;

                if (optFilterResult.isPresent()) {
                    filterResult = optFilterResult.get();
                } else {
                    filterResult = null;
                }

                if (filterResult != null) {
                    TextInputDialog valueDialog = new TextInputDialog();
                    valueDialog.initStyle(StageStyle.UTILITY);
                    valueDialog.setTitle("Enter Value");
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
                        ObservableList<Order> filtered = FXCollections.observableArrayList();

                        switch (filterResult) {
                            case "Equal to":
                                for (int i = 0; i < orders.size(); i++) {
                                    if (orders.get(i).getQuantity() == Integer.valueOf(value)) {
                                        filtered.add(orders.get(i));
                                    }
                                }
                                break;

                            case "Greater than":
                                for (int i = 0; i < orders.size(); i++) {
                                    if (orders.get(i).getQuantity() > Integer.valueOf(value)) {
                                        filtered.add(orders.get(i));
                                    }
                                }
                                break;

                            case "Lesser than":
                                for (int i = 0; i < orders.size(); i++) {
                                    if (orders.get(i).getQuantity() < Integer.valueOf(value)) {
                                        filtered.add(orders.get(i));
                                    }
                                }
                                break;

                            case "Greater than equal to":
                                for (int i = 0; i < orders.size(); i++) {
                                    if (orders.get(i).getQuantity() >= Integer.valueOf(value)) {
                                        filtered.add(orders.get(i));
                                    }
                                }
                                break;

                            case "Lesser than equal to":
                                for (int i = 0; i < orders.size(); i++) {
                                    if (orders.get(i).getQuantity() <= Integer.valueOf(value)) {
                                        filtered.add(orders.get(i));
                                    }
                                }
                                break;
                        }

                        orders.clear();
                        orders = filtered;
                        ordersTable.setItems(orders);
                    }
                }
            } else if (columnResult.equals("Delivery Date")) {
                List<String> filterChoices = new ArrayList<>();
                filterChoices.add("On");
                filterChoices.add("Before");
                filterChoices.add("After");
                ChoiceDialog<String> chooseFilterOption = new ChoiceDialog<>("On", filterChoices);
                chooseFilterOption.initStyle(StageStyle.UTILITY);
                chooseFilterOption.setTitle("Filter Orders");
                chooseFilterOption.setHeaderText(null);
                chooseFilterOption.setContentText("Choose parameter:");
                Optional<String> optFilterResult = chooseFilterOption.showAndWait();
                String filterResult;

                if (optFilterResult.isPresent()) {
                    filterResult = optFilterResult.get();
                } else {
                    filterResult = null;
                }

                if (filterResult != null) {
                    Dialog<LocalDate> dateDialog = new Dialog<>();
                    dateDialog.initStyle(StageStyle.UTILITY);
                    dateDialog.setTitle("Enter Delivery Date");
                    dateDialog.setHeaderText(null);
                    DialogPane dialogPane = dateDialog.getDialogPane();
                    dialogPane.getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
                    DatePicker datePicker = new DatePicker();
                    datePicker.setShowWeekNumbers(false);
                    dialogPane.setContent(new VBox(8, datePicker));
                    dateDialog.setResultConverter((ButtonType button) -> {
                        if (button == ButtonType.OK) {
                            return datePicker.getValue();
                        }
                        return null;
                    });

                    Optional<LocalDate> optValue = dateDialog.showAndWait();
                    LocalDate value;

                    if (optValue.isPresent()) {
                        value = optValue.get();
                    } else {
                        value = null;
                    }

                    if (value != null) {
                        ObservableList<Order> filtered = FXCollections.observableArrayList();

                        switch (filterResult) {
                            case "On":
                                for (int i = 0; i < orders.size(); i++) {
                                    if (orders.get(i).getDeliveryDate().equals(value)) {
                                        filtered.add(orders.get(i));
                                    }
                                }
                                break;

                            case "After":
                                for (int i = 0; i < orders.size(); i++) {
                                    if (orders.get(i).getDeliveryDate().isAfter(value)) {
                                        filtered.add(orders.get(i));
                                    }
                                }
                                break;

                            case "Before":
                                for (int i = 0; i < orders.size(); i++) {
                                    if (orders.get(i).getDeliveryDate().isBefore(value)) {
                                        filtered.add(orders.get(i));
                                    }
                                }
                                break;
                        }

                        orders.clear();
                        orders = filtered;
                        ordersTable.setItems(orders);
                    }
                }
            }
        }
    }

    @FXML
    private void calculateOrderValue() {
        TextInputDialog enterID = new TextInputDialog();
        enterID.initStyle(StageStyle.UTILITY);
        enterID.setTitle("Calculate Order Value");
        enterID.setHeaderText(null);
        enterID.setContentText("Enter Order ID");
        Optional<String> optID = enterID.showAndWait();
        String ID;
        String productID = "";
        int index = -1;
        int quantity = 0;
        int price = 0;

        if (optID.isPresent()) {
            ID = optID.get();
        } else {
            ID = null;
        }

        if (ID != null) {
            for (int i = 0; i < orders.size(); i++) {
                if (orders.get(i).getID().equals(ID)) {
                    index = i;
                    productID = orders.get(i).getProductID();
                    quantity = orders.get(i).getQuantity();
                    break;
                }
            }

            if (index > -1) {
                for (int i = 0; i < inventory.size(); i++) {
                    if (inventory.get(i).getID().equals(productID)) {
                        price = inventory.get(i).getPrice();
                        break;
                    }
                }

                String value = NumberFormat.getIntegerInstance().format(price * quantity);
                Jain_inventory.infoAlert("Order Value", null, "Value of Order " + ID + " is \u20B9" + value + ".");
            } else {
                Jain_inventory.infoAlert("Calculate Order Value", null, "Order ID does not exist.");
            }
        }
    }

    private boolean checkProductID(String productID) {
        boolean exists = false;

        for (int i = 0; i < inventory.size(); i++) {
            if (inventory.get(i).getID().equals(productID)) {
                exists = true;
            }
        }

        return exists;
    }

    private int indexOfProductID(String productID) {
        int index = -1;

        for (int i = 0; i < inventory.size(); i++) {
            if (inventory.get(i).getID().equals(productID)) {
                index = i;
            }
        }

        return index;
    }

    private boolean checkPersonID(String personID) {
        boolean exists = false;

        for (int i = 0; i < suppliers.size(); i++) {
            if (suppliers.get(i).getID().equals(personID)) {
                exists = true;
            }
        }

        return exists;
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

    private void loadInventory() {
        inventory.clear();
        try {
            String sqlSelect = "SELECT * FROM APP.INVENTORY";
            Statement statement;
            statement = Jain_inventory.connection.createStatement();
            ResultSet rst = statement.executeQuery(sqlSelect);

            while (rst.next()) {
                inventory.add(new Product(rst.getString("PRODUCT_ID"), rst.getString("NAME"), rst.getInt("QUANTITY_METRES"), rst.getInt("PRICE_METRES")));
            }
        } catch (SQLException exc) {
            System.out.println(exc);
        }
    }

    private void loadOrders() {
        orders.clear();
        try {
            String sqlSelect = "SELECT * FROM APP.PURCHASE_ORDERS";
            Statement statement;
            statement = Jain_inventory.connection.createStatement();
            ResultSet rst = statement.executeQuery(sqlSelect);

            while (rst.next()) {
                LocalDate date = Jain_inventory.fromStringDate(rst.getString("DELIVERY_DATE"));
                orders.add(new Order(rst.getString("ORDER_ID"), rst.getString("PRODUCT_ID"), rst.getString("PERSON_ID"), rst.getInt("QUANTITY_METRES"), date));
            }
        } catch (SQLException exc) {
            System.out.println(exc);
        }
    }

    @FXML
    private void resetOrderTable() {
        loadOrders();
        Collections.sort(orders, new OrderComparator());
        ordersTable.setItems(orders);
    }

    private void saveOrdersTable() {
        try {
            String sqlDelete = "DELETE FROM APP.PURCHASE_ORDERS";
            String sqlInsert = "INSERT INTO APP.PURCHASE_ORDERS(ORDER_ID, PRODUCT_ID, PERSON_ID, QUANTITY_METRES, DELIVERY_DATE) VALUES(?,?,?,?,?)";
            PreparedStatement prepStatement = Jain_inventory.connection.prepareStatement(sqlDelete);
            prepStatement.executeUpdate();
            prepStatement = Jain_inventory.connection.prepareStatement(sqlInsert);

            ListIterator<Order> iter = orders.listIterator();
            while (iter.hasNext()) {
                Order o = iter.next();
                prepStatement.setString(1, o.getID());
                prepStatement.setString(2, o.getProductID());
                prepStatement.setString(3, o.getPersonID());
                prepStatement.setInt(4, o.getQuantity());
                String date = Jain_inventory.toStringDate(o.getDeliveryDate());
                prepStatement.setString(5, date);
                prepStatement.addBatch();
            }

            prepStatement.executeBatch();
        } catch (SQLException exc) {
            System.out.println(exc);
        }

        resetOrderTable();
    }

    @FXML
    private void emailOrdersReport() throws AddressException {
        String tableOrders[][] = new String[orders.size() + 1][5];
        tableOrders[0][0] = "ID";
        tableOrders[0][1] = "Product ID";
        tableOrders[0][2] = "Supplier ID";
        tableOrders[0][3] = "Quantity";
        tableOrders[0][4] = "Delivery Date";

        for (int i = 1; i < orders.size() + 1; i++) {
            tableOrders[i][0] = orders.get(i - 1).getID();
            tableOrders[i][1] = orders.get(i - 1).getProductID();
            tableOrders[i][2] = orders.get(i - 1).getPersonID();
            String quantity = String.valueOf(orders.get(i - 1).getQuantity());
            tableOrders[i][3] = quantity;
            tableOrders[i][4] = Jain_inventory.toStringDate(orders.get(i - 1).getDeliveryDate());
        }

        String message = "";
        int maxLength = 0;
        for (String records[] : tableOrders) {
            for (String current : records) {
                int length = current.length();
                if (length > maxLength) {
                    maxLength = length;
                }
            }
        }

        for (String records[] : tableOrders) {
            for (String current : records) {
                String value = current;
                message += String.format("%-" + maxLength + "s" + "\t", value);
            }
            message += "\n";
        }

        System.out.println(message);
        Jain_inventory.sendEmail("Jain Textiles Purchase Orders", message);
    }

    @FXML
    private void back() throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("home.fxml"));
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
    
    @FXML
    private void openSuppliers() throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("suppliers.fxml"));
        Scene scene = new Scene(root);
        Jain_inventory.mainStage.setScene(scene);
        Jain_inventory.mainStage.show();
    }
}
