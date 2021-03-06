package jain_inventory;

public class Product {
    private String ID;
    private String name;
    private int quantity;
    private int price;
    
    public Product(String id, String name, int quantity, int price) {
        this.ID = id;
        this.name = name;
        this.quantity = quantity;
        this.price = price;
    }
    
    public String getID() {
        return this.ID;
    }
    
    public String getName() {
        return this.name;
    }
    
    public int getQuantity() {
        return this.quantity;
    }
    
    public int getPrice() {
        return this.price;
    }
    
    public void setID(String id) {
        this.ID = id;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
    
    public void setPrice(int price) {
        this.price = price;
    }
}

