package at.punkt.lodms.integration;

import com.vaadin.ui.Form;
import java.io.Serializable;

/**
 * Components implementing this interface can be configured with a serializable JavaBean.<br/>
 * A vaadin {@link Form} will be created automatically from the JavaBean that
 * the user can fill in the web frontend.<br/>
 * The filled JavaBean will then be used to call {@link Configurable#configure(java.io.Serializable))
 * 
 * @author Alex Kreiser (akreiser@gmail.com)
 */
public interface ConfigBeanProvider<T extends Serializable> extends Configurable<T> {
    
    /**
     * Returns a new (blank) JavaBean instance with its default values set.
     * 
     * @return 
     */
    public T newDefaultConfig();
    
}
