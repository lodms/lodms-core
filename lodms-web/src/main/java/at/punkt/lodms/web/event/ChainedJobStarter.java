package at.punkt.lodms.web.event;

import at.punkt.lodms.ETLPipeline;
import at.punkt.lodms.PipelineCompletedEvent;
import at.punkt.lodms.web.Job;
import at.punkt.lodms.web.JobChain;
import at.punkt.lodms.web.JobService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ChainedJobStarter implements ApplicationListener<PipelineCompletedEvent> {

  @Autowired
  private JobService service;

  @Override
  public void onApplicationEvent(PipelineCompletedEvent e) {
    String finishedJobId = getJob(e.getPipeline()).getId();

    JobChain jobChain = generateJobChain();
    List<String> chainedJobs = jobChain.getChildren(finishedJobId);
    if (chainedJobs != null && chainedJobs.size() > 0) {
      for (String chainedJob : chainedJobs) {
        Job nextJob = service.getJobById(chainedJob);
        if (nextJob != null) {
          nextJob.getPipeline().run();
        }
      }
    }
  }

  private Job getJob(ETLPipeline pipeline) {
    for (Job job : service.getJobs()) {
      if (job.getPipeline().equals(pipeline))
        return job;
    }
    return null;
  }

  private JobChain generateJobChain() {
    JobChain jobChain = new JobChain();

    for (Job j : service.getJobs()) {
      if (!j.getMetadata().getPreviousJobId().isEmpty())
        jobChain.add(j.getMetadata().getPreviousJobId(), j.getId());
    }
    return jobChain;
  }

}
