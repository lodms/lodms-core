/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package at.punkt.lodms.web.view;

import at.punkt.lodms.web.Job;
import org.apache.log4j.Logger;
import org.springframework.context.annotation.Scope;

/**
 *
 * @author Alex Kreiser
 */
@org.springframework.stereotype.Component
@Scope("prototype")
public class EditJobView extends JobWizardBase {

    private final Logger logger = Logger.getLogger(EditJobView.class);

    public EditJobView() {
    }

    public void setJob(Job job) {
        this.job = job;
    }
}
