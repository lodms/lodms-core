package at.punkt.lodms.spi.load;

/**
 * Exception thrown by a loader if something goes wrong throughout the
 * loading process.
 *
 * @see Loader
 * @author Alex Kreiser (akreiser@gmail.com)
 */
public class LoadException extends Exception {

    public LoadException(Throwable cause) {
        super(cause);
    }

    public LoadException(String message, Throwable cause) {
        super(message, cause);
    }

    public LoadException(String message) {
        super(message);
    }
}
