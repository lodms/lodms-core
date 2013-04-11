/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package at.punkt.lodms.web.view;

import at.punkt.lodms.web.ETLJob;
import java.util.UUID;
import javax.annotation.PostConstruct;
import org.apache.log4j.Logger;
import org.springframework.context.annotation.Scope;

/**
 *
 * @author Alex Kreiser
 */
@org.springframework.stereotype.Component
@Scope("prototype")
public class NewJobWizard extends JobWizardBase {

    private final Logger logger = Logger.getLogger(NewJobWizard.class);

    @PostConstruct
    @Override
    public void init() {
        job = new ETLJob(UUID.randomUUID().toString());
        job.setPipeline(application.newPipeline());
        job.getPipeline().setId("lodms:" + job.getId());
        super.init();
    }
}
