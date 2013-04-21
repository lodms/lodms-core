package at.punkt.lodms.integration;

/**
 * Components implementing this interface provide a {@link ConfigDialog} through
 * which they can be configured.
 * This is far more flexible than implementing {@link ConfigBeanProvider} since the dialog
 * can be customized.
 * 
 * @author Alex Kreiser (akreiser@gmail.com)
 */
public interface ConfigDialogProvider<T> extends ConfigBeanProvider<T> {
    
    /**
     * Returns a new {@link ConfigDialog} instance that will be embedded in the 
     * dialog window on configuration of this component.
     * 
     * @param config An already existing configuration object<br/>
     * {@code null} if this is the first configuration of the component
     * @return 
     */
    public ConfigDialog getConfigDialog(T config);
}
