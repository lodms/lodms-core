/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package at.punkt.lodms.web;

import at.punkt.lodms.ETLPipeline;
import java.util.concurrent.ScheduledFuture;

/**
 *
 * @author Alex Kreiser
 */
public class ETLJob {

    private final String id;
    private ETLJobMetadata metadata;
    private ScheduledFuture future;
    private ETLPipeline pipeline;
    private boolean running;

    public ETLJob(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public ETLPipeline getPipeline() {
        return pipeline;
    }

    public void setPipeline(ETLPipeline pipeline) {
        this.pipeline = pipeline;
    }

    public ETLJobMetadata getMetadata() {
        return metadata;
    }

    public void setMetadata(ETLJobMetadata metadata) {
        this.metadata = metadata;
    }

    public ScheduledFuture getFuture() {
        return future;
    }

    public void setFuture(ScheduledFuture future) {
        this.future = future;
    }

    public boolean isRunning() {
        return running;
    }

    public void setRunning(boolean running) {
        this.running = running;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final ETLJob other = (ETLJob) obj;
        if ((this.id == null) ? (other.id != null) : !this.id.equals(other.id)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 61 * hash + (this.id != null ? this.id.hashCode() : 0);
        return hash;
    }
}
