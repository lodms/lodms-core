package at.punkt.lodms.integration;

import com.vaadin.ui.Component;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;

/**
 * A custom configuration dialog that used to configure a component through the web GUI.<br/>
 * Instances have to be a vaadin {@link Component} - e.g. {@link VerticalLayout} 
 * or {@link Panel} can be extended.
 * 
 * @author Alex Kreiser (akreiser@gmail.com)
 */
public interface ConfigDialog extends Component {
    
    /**
     * Returns the config object that has been configured using this dialog.<br/>
     * This method will be called when the user hits the "configure" button.
     * 
     * @return 
     */
    public Object getConfig();
}
