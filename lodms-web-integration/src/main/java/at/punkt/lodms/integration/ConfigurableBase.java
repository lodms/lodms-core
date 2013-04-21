package at.punkt.lodms.integration;

import at.punkt.lodms.Disableable;

/**
 * Convenience base class for {@link Configurable}s, storing the config object.
 * 
 * @author Alex Kreiser (akreiser@gmail.com)
 */
public abstract class ConfigurableBase<T> implements Configurable<T>, Disableable {

    protected T config;
    protected boolean disabled;
    
    @Override
    public T getConfig() {
        return config;
    }

    @Override
    public void configure(T config) throws ConfigurationException {
        this.config = config;
        configureInternal(config);
    }

    @Override
    public boolean isDisabled() {
        return disabled;
    }

    @Override
    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }

    protected abstract void configureInternal(T config) throws ConfigurationException;
    
}
