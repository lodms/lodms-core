/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package at.punkt.lodms.web;

import at.punkt.lodms.web.dialog.ApplicationConfigDialog;
import at.punkt.lodms.ETLPipeline;
import at.punkt.lodms.impl.ETLPipelineImpl;
import at.punkt.lodms.integration.ConfigBeanProvider;
import at.punkt.lodms.integration.ConfigDialog;
import at.punkt.lodms.integration.ConfigDialogProvider;
import at.punkt.lodms.integration.ConfigUIContext;
import at.punkt.lodms.integration.ConfigUIContextAware;
import at.punkt.lodms.integration.ConfigurationException;
import at.punkt.lodms.integration.UIComponent;
import at.punkt.lodms.web.view.*;
import com.vaadin.Application;
import com.vaadin.data.util.BeanItem;
import com.vaadin.ui.*;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Window.Notification;
import java.text.SimpleDateFormat;
import org.apache.log4j.Logger;
import org.openrdf.repository.Repository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 *
 * @author Alex Kreiser
 */
@Component("lodmsApp")
@Scope("session")
public class LodmsApplication extends Application {

    private final Logger logger = Logger.getLogger(LodmsApplication.class);
    private final String CONFIG_INFO = "When you have finished configuring the component, click the configure button to add this component to the pipeline.";
    public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd.MM.yyyy - HH:mm:ss");
    /**
     * Essential autowired components
     */
    @Autowired
    ServiceListProvider serviceProvider;
    @Autowired
    private ApplicationContext context;
    @Autowired
    private Repository repository;

    @Autowired
    private ExistingJobsView jobsView;
    @Autowired
    private LodmsMainWindow mainWindow;
    private Window configWindow = new Window("Configuration");
    
    @Autowired
    private JobService jobService;
    @Autowired
    private ApplicationConfig applicationConfig;

    @Override
    public void init() {
        setMainWindow(mainWindow);
        mainWindow.init();
        setTheme("lodms");
        
        initConfigWindow();
        
        if (applicationConfig.get("lodms.home").equals(".")) {
            logger.info("Opening application config window");
            ApplicationConfigDialog dialog = new ApplicationConfigDialog(applicationConfig, jobService);
            showDialog(dialog);
        }
    }
    
    public void showDialog(Window window) {
        if (window.getParent() == null) {
            mainWindow.addWindow(window);
        }
    }

    public void initConfigWindow() {
        configWindow.setModal(true);
        configWindow.setResizable(true);
        configWindow.setWidth("850px");
    }

    public void viewComponent(com.vaadin.ui.Component component) {
        mainWindow.showView(component);
    }

    public void displayConfigWindow(final ConfigBeanProvider configBeanProvider, final ConfigSuccessHandler handler, Object config) {
        configWindow.removeAllComponents();
        configWindow.setCaption("Configuration: " + ((UIComponent) configBeanProvider).getName());
        VerticalLayout configWinLayout = new VerticalLayout();
        Form form = new Form(new VerticalLayout());

        if (config == null) {
            config = configBeanProvider.newDefaultConfig();
        }
        final BeanItem beanItem = new BeanItem(config);
        form.setItemDataSource(beanItem);
        form.setImmediate(true);
        configWinLayout.addComponent(form);
        Button configureButton = new Button("Configure");
        configureButton.addListener(new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                try {
                    configBeanProvider.configure(beanItem.getBean());
                    handler.configured();
                    getMainWindow().removeWindow(configWindow);
                } catch (ConfigurationException ex) {
                    getMainWindow().showNotification(ex.getMessage(), Notification.TYPE_ERROR_MESSAGE);
                    logger.error("Unable to configure component", ex);
                }
            }
        });
        Label configExplanation = new Label(CONFIG_INFO, Label.CONTENT_TEXT);
        configExplanation.addStyleName("lodms-config-info");
        configWinLayout.addComponent(configExplanation);
        configWinLayout.addComponent(configureButton);
        configWinLayout.setComponentAlignment(configureButton, Alignment.BOTTOM_CENTER);
        configWindow.addComponent(configWinLayout);
        getMainWindow().addWindow(configWindow);
    }

    public void displayConfigWindow(final ConfigDialogProvider configDialogProvider, final ConfigSuccessHandler handler, Object config) {
        configWindow.removeAllComponents();
        configWindow.setCaption("Configuration: " + ((UIComponent) configDialogProvider).getName());
        VerticalLayout configWinLayout = new VerticalLayout();
        configWinLayout.setSizeFull();
        if (config == null) {
            config = configDialogProvider.newDefaultConfig();
        }
        final ConfigDialog dialog = configDialogProvider.getConfigDialog(config);
        configWinLayout.addComponent(dialog);
        dialog.setSizeFull();
        Button configureButton = new Button("Configure");
        configureButton.addListener(new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                try {
                    Object config = dialog.getConfig();
                    configDialogProvider.configure(config);
                    handler.configured();
                    getMainWindow().removeWindow(configWindow);
                } catch (ConfigurationException ex) {
                    System.out.println(ex);
                }
            }
        });
        if (dialog instanceof ConfigUIContextAware) {
            ConfigUIContext ctx = new ConfigUIContext();
            ctx.setConfigureButton(configureButton);
            ((ConfigUIContextAware) dialog).setConfigUIContext(ctx);
        }
        Label configExplanation = new Label(CONFIG_INFO, Label.CONTENT_TEXT);
        configExplanation.addStyleName("lodms-config-info");
        configWinLayout.addComponent(configExplanation);
        configWinLayout.addComponent(configureButton);
        configWinLayout.setComponentAlignment(configureButton, Alignment.BOTTOM_CENTER);
        configWindow.addComponent(configWinLayout);
        getMainWindow().addWindow(configWindow);
    }

    public ETLPipeline newPipeline() {
        ETLPipeline pipeline = new ETLPipelineImpl();
        if (pipeline instanceof ApplicationEventPublisherAware) {
            ((ApplicationEventPublisherAware) pipeline).setApplicationEventPublisher(context);
        }
        pipeline.setRepository(repository);
        return pipeline;
    }

    public ExistingJobsView getJobsView() {
        return jobsView;
    }
}
