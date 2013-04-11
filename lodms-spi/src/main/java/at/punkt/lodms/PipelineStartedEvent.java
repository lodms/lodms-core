/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package at.punkt.lodms;

import at.punkt.lodms.impl.ETLPipelineImpl;

/**
 *
 * @author Alex Kreiser
 */
public class PipelineStartedEvent extends PipelineEvent {

    public PipelineStartedEvent(ETLPipelineImpl pipeline, String runId, Object source) {
        super(pipeline, runId, source);
    }
}
