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
public class Job {

    private final String id;
    private JobMetadata metadata;
    private ScheduledFuture future;
    private ETLPipeline pipeline;
    private boolean running;

    public Job(String id) {
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

    public JobMetadata getMetadata() {
        return metadata;
    }

    public void setMetadata(JobMetadata metadata) {
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
        final Job other = (Job) obj;
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
