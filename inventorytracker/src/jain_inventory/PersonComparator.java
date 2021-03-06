package jain_inventory;

import java.util.Comparator;

public class PersonComparator implements Comparator<Person>{
    @Override
    public int compare(Person p1, Person p2) {
            return p2.getID().compareTo(p1.getID())*-1;
    }
}
