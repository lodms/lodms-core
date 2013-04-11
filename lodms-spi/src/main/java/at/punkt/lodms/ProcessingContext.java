package at.punkt.lodms;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Base context class for all specific contexts for Extractors, Transformers and Loaders.<br/>
 * Provides a flexible way of passing custom data between components with a {@link Map}.
 *
 * @author Alex Kreiser (akreiser@gmail.com)
 */
public class ProcessingContext {

    protected final String id;
    protected ETLPipeline pipeline;
    protected long duration;
    protected boolean cancelPipeline;
    protected String cancelMessage;
    protected final Map<String, Object> customData;
    protected List<String> warnings = new ArrayList<String>();

    public ProcessingContext(String id, Map<String, Object> customData) {
        this.id = id;
        this.customData = customData;
    }

    /**
     * Returns the id of the current pipeline process.
     * 
     * @return 
     */
    public String getId() {
        return id;
    }
    
    /**
     * Returns a map containing global custom data of components that want to
     * exchange information that is not stored explicitly in the repository.<br/>
     * This information will only be available within the scope of a pipeline execution.
     *
     * @return
     */
    public Map<String, Object> getCustomData() {
        return customData;
    }

    /**
     * Returns the duration (in ms) the component took to execute.
     *
     * @return
     */
    public long getDuration() {
        return duration;
    }

    /**
     * Sets the duration (in ms) the component took to execute.
     *
     * @param duration
     */
    public void setDuration(long duration) {
        this.duration = duration;
    }

    /**
     * Returns if pipeline cancellation is requested by this component.
     *
     * @return
     */
    public boolean isCancelPipeline() {
        return cancelPipeline;
    }

    /**
     * Requests cancellation of the pipeline after this component finished (successfully or not).
     *
     * @param message Informative message why the pipeline should be cancelled
     */
    public void cancelPipeline(String message) {
        this.cancelPipeline = true;
        this.cancelMessage = message;
    }

    /**
     * Returns a message containing the reason for cancelling the pipeline.
     *
     * @return
     */
    public String getCancelMessage() {
        return cancelMessage;
    }

    public ETLPipeline getPipeline() {
        return pipeline;
    }

    public void setPipeline(ETLPipeline pipeline) {
        this.pipeline = pipeline;
    }

    public List<String> getWarnings() {
        return warnings;
    }
}