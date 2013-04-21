/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package at.punkt.lodms.web.view;

import at.punkt.lodms.web.Job;
import java.util.UUID;
import javax.annotation.PostConstruct;
import org.apache.log4j.Logger;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 *
 * @author Alex Kreiser
 */
@Component
@Scope("prototype")
public class NewJobView extends JobWizardBase {

    private final Logger logger = Logger.getLogger(NewJobView.class);

    @PostConstruct
    @Override
    public void init() {
        job = new Job(UUID.randomUUID().toString());
        job.setPipeline(application.newPipeline());
        job.getPipeline().setId("lodms:" + job.getId());
        super.init();
    }
}
