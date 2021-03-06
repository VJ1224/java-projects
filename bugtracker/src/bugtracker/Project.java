/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bugtracker;

import java.util.Date;

/**
 *
 * @author Vansh Jain
 */
public class Project {
    private String title;
    private Date due;

    public Project(String title, Date due) {
        this.title = title;
        this.due = due;
    }
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
    
    public Date getDue() {
        return due;
    }

    public void setDue(Date due) {
        this.due = due;
    }
   
    public String dateString() {
        return due.toString();
    }
}
