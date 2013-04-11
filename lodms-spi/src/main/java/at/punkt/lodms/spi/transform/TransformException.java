package at.punkt.lodms.spi.transform;

/**
 * Exception thrown by a transformer if something goes wrong throughout the
 * transformation process.
 *
 * @see Transformer
 * @author Alex Kreiser (akreiser@gmail.com)
 */
public class TransformException extends Exception {

    public TransformException(Throwable cause) {
        super(cause);
    }

    public TransformException(String message, Throwable cause) {
        super(message, cause);
    }

    public TransformException(String message) {
        super(message);
    }
}
