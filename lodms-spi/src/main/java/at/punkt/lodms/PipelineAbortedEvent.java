package at.punkt.lodms;

import at.punkt.lodms.impl.ETLPipelineImpl;

/**
 * Published when a component {@link Extractor}, {@link Transformer},
 * {@link Loader} requests cancellation of the pipeline through
 * {@link ProcessingContext#cancelPipeline(java.lang.String)}.<br/>
 * The pipeline exits directly after this event is published.
 *
 * @see ProcessingContext#cancelPipeline(java.lang.String)
 * @author Alex Kreiser (akreiser@gmail.com)
 */
public class PipelineAbortedEvent extends PipelineEvent {

    private final String message;

    public PipelineAbortedEvent(String message, ETLPipelineImpl pipeline, String id, Object source) {
        super(pipeline, id, source);
        this.message = message;
    }

    /**
     * Returns the cancellation message
     *
     * @return
     */
    public String getMessage() {
        return message;
    }
}
