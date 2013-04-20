/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package at.punkt.lodms.web.event;

import at.punkt.lodms.ETLPipeline;
import at.punkt.lodms.PipelineEvent;
import at.punkt.lodms.PipelineStartedEvent;
import at.punkt.lodms.web.ETLJob;
import at.punkt.lodms.web.ETLJobService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

/**
 *
 * @author Alex Kreiser (akreiser@gmail.com)
 */
@Component
public class RunningJobsUpdater implements ApplicationListener<PipelineEvent> {

    @Autowired
    private ETLJobService service;

    @Override
    public void onApplicationEvent(PipelineEvent e) {
        if (e instanceof PipelineStartedEvent) {
            getJob(e.getPipeline()).setRunning(true);
        } else {
            getJob(e.getPipeline()).setRunning(false);
        }
    }

    private ETLJob getJob(ETLPipeline pipeline) {
        for (ETLJob job : service.getJobs()) {
            if (job.getPipeline().equals(pipeline))
                return job;
        }
        return null;
    }
}
