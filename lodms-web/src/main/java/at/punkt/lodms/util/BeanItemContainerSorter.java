/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package at.punkt.lodms.util;

import com.vaadin.data.Container.Sortable;
import com.vaadin.data.util.ItemSorter;
import java.util.List;

/**
 *
 * @author kreisera
 */
public class BeanItemContainerSorter implements ItemSorter {

    private final List<?> backingList;

    public BeanItemContainerSorter(List<?> backingList) {
        this.backingList = backingList;
    }

    @Override
    public void setSortProperties(Sortable container, Object[] propertyId, boolean[] ascending) {
    }

    @Override
    public int compare(Object itemId1, Object itemId2) {
        if (backingList.indexOf(itemId1) > backingList.indexOf(itemId2)) {
            return 1;
        } else if (backingList.indexOf(itemId1) < backingList.indexOf(itemId2)) {
            return -1;
        } else {
            return 0;
        }
    }
}
