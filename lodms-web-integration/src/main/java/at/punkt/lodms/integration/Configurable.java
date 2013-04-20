package at.punkt.lodms.integration;

import java.io.Serializable;

/**
 * Components that need to be configured in the Web GUI have to implement this interface,
 * either through {@link ConfigBeanProvider} or {@link ConfigDialogProvider>}.
 * IMPORTANT:
 * The config object and all nested objects have to implement {@link Serializable}!
 * 
 * Otherwise ETLJobs using this component cannot be persisted between application restarts!
 * 
 * @see ConfigBeanProvider
 * @see ConfigDialogProvider
 * @author Alex Kreiser (akreiser@gmail.com)
 */
public interface Configurable<T extends Serializable> {

    /**
     * Returns the serializable object holding the configured state of the component.
     * The component must be re-configurable calling {@link #configure(java.io.Serializable) }
     * with this object to return to a valid and consistent state.
     * 
     * @return 
     */
    public T getConfig();
    
    /**
     * Configures the component using the passed config object.
     * 
     * @param config Serializable object that holds all necessary data for the component to be configured
     * @throws ConfigurationException If anything goes wrong and the component cannot be configured
     */
    public void configure(T config) throws ConfigurationException;
}
