/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package at.punkt.lodms.web.view;

import at.punkt.lodms.web.ApplicationConfig;
import at.punkt.lodms.web.ETLJobService;
import com.vaadin.data.Item;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.validator.AbstractStringValidator;
import com.vaadin.ui.*;
import com.vaadin.ui.Button.ClickEvent;
import java.io.File;
import java.io.FileOutputStream;
import java.util.Properties;
import org.apache.log4j.Logger;

/**
 *
 * @author kreisera
 */
public class ApplicationConfigDialog extends Window {

    private final Logger logger = Logger.getLogger(ApplicationConfigDialog.class);
    private LodmsConfig config = new LodmsConfig();
    
    public class LodmsConfig {
        
        private String homeDirectory = "";

        public String getHomeDirectory() {
            return homeDirectory;
        }

        public void setHomeDirectory(String homeDirectory) {
            this.homeDirectory = homeDirectory;
        }
    }

    public ApplicationConfigDialog(final ApplicationConfig appConfig, final ETLJobService jobService) {
        super("LOD Management Suite Configuration");
        setModal(true);
        VerticalLayout layout = new VerticalLayout();
        addComponent(layout);
        setWidth("600px");
        Label label = new Label("The data directory of your LOD Management Suite installation is not set yet. "
                + "Please set a valid path to a writable directory outside of the web application.");
        label.addStyleName("lodms-config-info");
        layout.addComponent(label);
        final Form form = new Form();
        form.setFormFieldFactory(new DefaultFieldFactory() {

            @Override
            public Field createField(Item item, Object propertyId, Component uiContext) {
                if (propertyId.equals("homeDirectory")) {
                    TextField textField = new TextField("Data Directory");
                    textField.setWidth("350px");
                    textField.setRequired(true);
                    textField.addValidator(new AbstractStringValidator("Invalid or non-writable file path") {

                        @Override
                        protected boolean isValidString(String value) {
                            try {
                                File file = new File(value);
                                if (!file.exists()) {
                                    setErrorMessage("Path "+file.getAbsolutePath()+" does not exist.");
                                    return false;
                                } else if (!file.isDirectory()) {
                                    setErrorMessage(file.getAbsolutePath()+" is not a directory.");
                                    return false;
                                } else if (!file.canRead()) {
                                    setErrorMessage("Folder "+file.getAbsolutePath()+" cannot be read by LODMS.");
                                    return false;
                                } else if (!file.canWrite()) {
                                    setErrorMessage("Folder "+file.getAbsolutePath()+" is not writable by LODMS.");
                                    return false;
                                }
                                return true;
                            } catch (Exception ex) {
                                setErrorMessage(ex.getMessage());
                                return false;
                            }
                        }
                    });
                    return textField;
                }
                return super.createField(item, propertyId, uiContext);
            }
            
        });
        form.setItemDataSource(new BeanItem<LodmsConfig>(config));
        layout.addComponent(form);
        final Window thisWindow = this;
        Button save = new Button("Save", new Button.ClickListener() {

            @Override
            public void buttonClick(ClickEvent event) {
                form.commit();
                appConfig.set("lodms.home", config.getHomeDirectory());
                try {
                    jobService.setPersistPath(new File(config.getHomeDirectory()));
                    jobService.loadJobs();
                    appConfig.save();
                } catch (Exception ex) {
                    logger.error("Unable to persist properties", ex);
                }
                getApplication().getMainWindow().removeWindow(thisWindow);
            }
        });
        layout.addComponent(save);
    }
}
