package at.punkt.lodms.integration;

import com.vaadin.Application;
import com.vaadin.terminal.Resource;

/**
 * Represents a component that can be rendered in the LODMS Web Application.<br/>
 * All components that should be available for use in the Web GUI have to implement
 * this interface.
 * 
 * @author Alex Kreiser (akreiser@gmail.com)
 */
public interface UIComponent {
    
    /**
     * Returns a short, self-descriptive name of the component.
     * 
     * @return 
     */
    public String getName();
    
    /**
     * Returns a description of what functionality this component provides.
     * 
     * @return 
     */
    public String getDescription();
    
    /**
     * Returns an icon as vaadin {@link Resource}, {@code null} if no icon is available.
     * 
     * @param application
     * @return 
     */
    public Resource getIcon(Application application);
    
    /**
     * Returns a string representing the configured internal state of this component.<br/>
     * This will be used to display this component after having been configured.
     * 
     * @return 
     */
    public String asString();
}
