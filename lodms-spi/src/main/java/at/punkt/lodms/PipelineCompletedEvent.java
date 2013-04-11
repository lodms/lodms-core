package at.punkt.lodms;

import at.punkt.lodms.impl.ETLPipelineImpl;

/**
 * Event indicating that a {@link ETLPipelineImpl} has completed successfully.
 *
 * @author Alex Kreiser (akreiser@gmail.com)
 */
public class PipelineCompletedEvent extends PipelineEvent {

    protected final long duration;

    public PipelineCompletedEvent(long duration, ETLPipelineImpl pipeline, String id, Object source) {
        super(pipeline, id, source);
        this.duration = duration;
    }

    /**
     * Returns the total duration (in ms) the pipeline took to execute.
     *
     * @return
     */
    public long getDuration() {
        return duration;
    }
}