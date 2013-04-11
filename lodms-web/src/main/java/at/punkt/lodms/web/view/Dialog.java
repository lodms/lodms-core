/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package at.punkt.lodms.web.view;

import com.vaadin.ui.Component;

/**
 *
 * @author kreisera
 */
public interface Dialog extends Component {
    
    public void setDialogCloseHandler(DialogCloseHandler handler);
}
