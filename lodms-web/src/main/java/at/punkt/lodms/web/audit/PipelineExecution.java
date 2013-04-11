/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package at.punkt.lodms.web.audit;

import at.punkt.lodms.ETLPipeline;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 *
 * @author Alex Kreiser
 */
public class PipelineExecution implements Serializable {
    
    private final String id;
    private Date date;
    private boolean aborted;
    private long duration;
    private String errorMessage;
    private ETLPipeline pipeline;
    private List<ExtractExecution> extractExecutions = new ArrayList<ExtractExecution>();

    public PipelineExecution(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }
    
    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public boolean isAborted() {
        return aborted;
    }

    public void setAborted(boolean aborted) {
        this.aborted = aborted;
    }

    public List<ExtractExecution> getExtractExecutions() {
        return extractExecutions;
    }

    public void setExtractExecutions(List<ExtractExecution> extractExecutions) {
        this.extractExecutions = extractExecutions;
    }

    public ETLPipeline getPipeline() {
        return pipeline;
    }

    public void setPipeline(ETLPipeline pipeline) {
        this.pipeline = pipeline;
    }
}