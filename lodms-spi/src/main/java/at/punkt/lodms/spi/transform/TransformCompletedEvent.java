package at.punkt.lodms.spi.transform;


/**
 * Event is published when a {@link Transformer} completed successfully.
 *
 * @author Alex Kreiser (akreiser@gmail.com)
 */
public class TransformCompletedEvent extends TransformEvent {

    public TransformCompletedEvent(Transformer transformer, TransformContext transformContext, Object source) {
        super(transformer, transformContext, source);
    }
}
