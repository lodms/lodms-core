package at.punkt.lodms.integration;

/**
 * Thrown when something goes wrong during configuration and the component can not be configured.
 * 
 * @author Alex Kreiser (akreiser@gmail.com)
 */
public class ConfigurationException extends Exception {

    public ConfigurationException(Throwable cause) {
        super(cause);
    }

    public ConfigurationException(String message, Throwable cause) {
        super(message, cause);
    }

    public ConfigurationException(String message) {
        super(message);
    }
    
}
