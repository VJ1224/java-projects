package jain_inventory;

public class Person {
    private String ID;
    private String name;
    private String address;
    private String phoneNumber;
    
    public Person (String ID, String name, String address, String phoneNumber) {
        this.ID = ID;
        this.name = name;
        this.address = address;
        this.phoneNumber = phoneNumber;
    }
    
    public String getID() {
        return this.ID;
    }
    
    public String getName() {
        return this.name;
    }
    
    public String getAddress() {
        return this.address;
    }
    
    public String getPhoneNumber() {
        return this.phoneNumber;
    }
    
    public void setID(String ID) {
        this.ID = ID;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public void setAddress(String address) {
        this.address = address;
    }
    
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}
