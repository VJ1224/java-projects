/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bugtracker;


/**
 *
 * @author Vansh Jain
 */
public class Bug {
    private String title;
    private int severity;
    private String description;

    public Bug(String title, int severity, String status) {
        this.title = title;
        this.severity = severity;
        this.description = status;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getSeverity() {
        return severity;
    }

    public void setSeverity(int severity) {
        this.severity = severity;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
