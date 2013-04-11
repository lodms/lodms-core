/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package at.punkt.lodms.web.audit;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import org.openrdf.model.URI;

/**
 *
 * @author Alex Kreiser
 */
public class ETLJobStatistics implements Serializable {
    
    private final String id;
    private long executionCount;
    private long successCount;
    private long failCount;
    
    private long extractCount;
    private long extractFailCount;
    private long extractSuccessCount;
    
    private long transformCount;
    private long transfromFailCount;
    private long transformSuccessCount;
    
    private long loadCount;
    private long loadFailCount;
    private long loadSuccessCount;
    
    private List<PipelineExecution> executions = new ArrayList<PipelineExecution>();

    public ETLJobStatistics(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public long getExecutionCount() {
        return executionCount;
    }

    public long getFailCount() {
        return failCount;
    }

    public long getSuccessCount() {
        return successCount;
    }

    public long getExtractCount() {
        return extractCount;
    }

    public long getExtractFailCount() {
        return extractFailCount;
    }

    public long getExtractSuccessCount() {
        return extractSuccessCount;
    }

    public long getLoadCount() {
        return loadCount;
    }

    public long getLoadFailCount() {
        return loadFailCount;
    }

    public long getLoadSuccessCount() {
        return loadSuccessCount;
    }

    public long getTransformCount() {
        return transformCount;
    }

    public long getTransformSuccessCount() {
        return transformSuccessCount;
    }

    public long getTransfromFailCount() {
        return transfromFailCount;
    }

    public List<PipelineExecution> getExecutions() {
        return executions;
    }
    
    public void addExecution(PipelineExecution execution) {
        executionCount++;
        executions.add(execution);
        if (execution.isAborted())
            failCount++;
        else
            successCount++;
    }
    
    public void addExtractExecution(ExtractExecution execution) {
        extractCount++;
    }
}
