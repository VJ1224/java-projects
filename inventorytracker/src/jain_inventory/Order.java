package jain_inventory;

import java.time.LocalDate;

public class Order {
    private String ID;
    private String productID;
    private String personID;
    private int quantity;
    private LocalDate deliveryDate;
    
    public Order(String ID, String productID, String personID, int quantity, int day, int month, int year) {
        this.ID = ID;
        this.productID = productID;
        this.personID = personID;
        this.quantity = quantity;
        this.deliveryDate = LocalDate.of(year, month, day);
    }
    
    public Order(String ID, String productID, String personID, int quantity, LocalDate deliveryDate) {
        this.ID = ID;
        this.productID = productID;
        this.personID = personID;
        this.quantity = quantity;
        this.deliveryDate = deliveryDate;
    }
    
    public String getID() {
        return this.ID;
    }
    
    public String getProductID() {
        return this.productID;
    }
    
    public String getPersonID() {
        return this.personID;
    }
    
    public int getQuantity() {
        return this.quantity;
    }
    
    public LocalDate getDeliveryDate() {
        return this.deliveryDate;
    }
    
    public void setID(String ID) {
        this.ID = ID;
    }
    
    public void setProductID(String productID) {
        this.productID = productID;
    }
    
    public void setPersonID(String personID) {
        this.personID = personID;
    }
    
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
    
    public void setDeliveryDate(int day, int month, int year) {
        this.deliveryDate = LocalDate.of(year, month, day);
    }
    
    public void setDeliveryDate(LocalDate deliveryDate) {
        this.deliveryDate = deliveryDate;
    }
}
