package jain_inventory;

import java.util.Comparator;

public class ProductComparator implements Comparator<Product>{
    @Override
    public int compare(Product prod1, Product prod2) {
            return prod2.getID().compareTo(prod1.getID())*-1;
    }
}
