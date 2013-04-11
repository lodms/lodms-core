/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package at.punkt.lodms.web.view;

import at.punkt.lodms.web.ETLJob;
import org.apache.log4j.Logger;
import org.springframework.context.annotation.Scope;

/**
 *
 * @author Alex Kreiser
 */
@org.springframework.stereotype.Component
@Scope("prototype")
public class EditJobWizard extends JobWizardBase {

    private final Logger logger = Logger.getLogger(EditJobWizard.class);

    public EditJobWizard() {
    }

    public void setJob(ETLJob job) {
        this.job = job;
    }
}
