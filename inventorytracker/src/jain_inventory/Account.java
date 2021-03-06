/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jain_inventory;

/**
 *
 * @author Vansh Jain
 */
public class Account {
    private String email;
    private String password;
    
    public Account(String email, String password) {
        this.email = email;
        this.password = password;
    }
    
    public String getEmail() {
        return this.email;
    }
    
    public String getPassword() {
        return this.password;
    }
  
    public void setEmail(String email) {
        this.email = email;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }
}
