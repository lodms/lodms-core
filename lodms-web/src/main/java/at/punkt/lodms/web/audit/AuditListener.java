/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package at.punkt.lodms.web.audit;

import at.punkt.lodms.ETLEvent;
import at.punkt.lodms.PipelineAbortedEvent;
import at.punkt.lodms.PipelineCompletedEvent;
import at.punkt.lodms.PipelineEvent;
import at.punkt.lodms.spi.extract.ExtractEvent;
import at.punkt.lodms.spi.load.LoadEvent;
import at.punkt.lodms.spi.transform.TransformEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

/**
 *
 * @author Alex Kreiser
 */
@Component
public class AuditListener implements ApplicationListener<ETLEvent> {

    @Autowired
    private AuditService service;

    public void onPipelineEvent(PipelineEvent e) {
        if (e instanceof PipelineCompletedEvent || e instanceof PipelineAbortedEvent) {
            service.addPipelineEvent(e);
        }
    }

    @Override
    public void onApplicationEvent(ETLEvent e) {
        if (e instanceof PipelineEvent) {
            onPipelineEvent((PipelineEvent)e);
        }
        else if (e instanceof ExtractEvent) {
            onExtractEvent((ExtractEvent)e);
        }
        else if (e instanceof TransformEvent) {
            onTransformEvent((TransformEvent)e);
        }
        else if (e instanceof LoadEvent) {
            onLoadEvent((LoadEvent)e);
        }
    }

    private void onExtractEvent(ExtractEvent extractEvent) {
        service.addExtractEvent(extractEvent);
    }

    private void onTransformEvent(TransformEvent transformEvent) {
        service.addTransformEvent(transformEvent);
    }

    private void onLoadEvent(LoadEvent loadEvent) {
        service.addLoadEvent(loadEvent);
    }
}