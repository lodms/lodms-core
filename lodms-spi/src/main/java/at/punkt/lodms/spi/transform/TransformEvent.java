package at.punkt.lodms.spi.transform;

import at.punkt.lodms.ETLEvent;

/**
 * Base class for {@link Transformer} events
 *
 * @see Transformer
 * @author Alex Kreiser (akreiser@gmail.com)
 */
public abstract class TransformEvent extends ETLEvent {

    protected final Transformer transformer;
    protected final TransformContext transformContext;

    public TransformEvent(Transformer transformer, TransformContext transformContext, Object source) {
        super(source);
        this.transformer = transformer;
        this.transformContext = transformContext;
    }

    /**
     * Returns the {@link Transformer} associated with this event.
     *
     * @return
     */
    public Transformer getTransformer() {
        return transformer;
    }

    /**
     * Returns the {@link TransformContext} of this execution.
     *
     * @return
     */
    public TransformContext getTransformContext() {
        return transformContext;
    }
}
