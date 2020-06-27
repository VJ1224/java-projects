package jain_inventory;

import java.util.Comparator;

public class OrderComparator implements Comparator<Order>{
    @Override
    public int compare(Order o1, Order o2) {
            return o2.getID().compareTo(o1.getID())*-1;
    }
}
