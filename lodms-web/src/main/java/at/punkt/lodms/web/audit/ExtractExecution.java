/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package at.punkt.lodms.web.audit;

import at.punkt.lodms.spi.extract.ExtractContext;
import at.punkt.lodms.spi.extract.Extractor;
import java.io.Serializable;

/**
 *
 * @author Alex Kreiser
 */
public class ExtractExecution implements Serializable {
    
    private boolean failed;
    private long duration;
    private Exception exception;
    private Extractor extractor;
    private ExtractContext context;

    public ExtractContext getContext() {
        return context;
    }

    public void setContext(ExtractContext context) {
        this.context = context;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public Exception getException() {
        return exception;
    }

    public void setException(Exception exception) {
        this.exception = exception;
    }

    public Extractor getExtractor() {
        return extractor;
    }

    public void setExtractor(Extractor extractor) {
        this.extractor = extractor;
    }

    public boolean isFailed() {
        return failed;
    }

    public void setFailed(boolean failed) {
        this.failed = failed;
    }
    
}
