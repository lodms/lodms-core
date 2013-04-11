package at.punkt.lodms.spi.transform;

/**
 * Published when a {@link Transformer} throws an exception during transformation.
 *
 * @see Transformer
 * @author Alex Kreiser (akreiser@gmail.com)
 */
public class TransformFailedEvent extends TransformEvent {

    private Exception exception;

    public TransformFailedEvent(Exception exception, Transformer transformer, TransformContext transformContext, Object source) {
        super(transformer, transformContext, source);
        this.exception = exception;
    }

    public Exception getException() {
        return exception;
    }
}