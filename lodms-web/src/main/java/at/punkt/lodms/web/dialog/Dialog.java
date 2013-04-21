/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package at.punkt.lodms.web.dialog;

import com.vaadin.ui.Component;

/**
 *
 * @author Alex Kreiser (akreiser@gmail.com)
 */
public interface Dialog extends Component {
    
    public void setDialogCloseHandler(DialogCloseHandler handler);
}
