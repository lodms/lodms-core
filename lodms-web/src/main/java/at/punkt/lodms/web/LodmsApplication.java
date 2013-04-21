/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package at.punkt.lodms.web;

import at.punkt.lodms.web.view.NewJobWizard;
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
import com.vaadin.terminal.ClassResource;
import com.vaadin.terminal.ThemeResource;
import com.vaadin.ui.*;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.MenuBar.MenuItem;
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
    private final String CONFIG_INFO = "When you are finished configuring the component, click the configure button to add this component to the pipeline.";
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
    /**
     * Main layout and views
     */
    private final GridLayout mainLayout = new GridLayout(1, 3);
    private final static int MAIN_COLUMN = 0;
    private final static int MAIN_ROW = 2;
    private final static int MENU_ROW = 1;
    @Autowired
    private ETLJobsView jobsView;
    @Autowired
    private ErrorReportingView errorView;
    private Window configWindow = new Window("Configuration");
    @Autowired
    private String applicationVersion;
    @Autowired
    private ETLJobService jobService;
    @Autowired
    private ApplicationConfig applicationConfig;
    @Autowired
    private AboutWindow aboutView;

    /**
     * Custom shortcuts for menubar commands
     */
    private class SwitchViewCommand implements MenuBar.Command {

        final com.vaadin.ui.Component component;

        public SwitchViewCommand(com.vaadin.ui.Component component) {
            this.component = component;
        }

        @Override
        public void menuSelected(MenuItem selectedItem) {
            viewComponent(component);
        }
    }

    public ETLJobsView getJobsView() {
        return jobsView;
    }

    private class ShowDialogCommand extends SwitchViewCommand {

        final Class<? extends Dialog> dialogClass;

        public ShowDialogCommand(Class dialogClass, com.vaadin.ui.Component component) {
            super(component);
            this.dialogClass = dialogClass;
        }

        @Override
        public void menuSelected(final MenuItem selectedItem) {

            final Dialog dialog = (Dialog) context.getBean(dialogClass);
            dialog.setDialogCloseHandler(new DialogCloseHandler() {

                @Override
                public void close() {
                    viewComponent(component);
                }
            });
            viewComponent(dialog);
        }
    }

    public void viewComponent(com.vaadin.ui.Component component) {
        com.vaadin.ui.Component currentView = mainLayout.getComponent(MAIN_COLUMN, MAIN_ROW);
        mainLayout.removeComponent(MAIN_COLUMN, MAIN_ROW);
        if (currentView instanceof View) {
            ((View) currentView).postView();
        }
        if (component instanceof View) {
            ((View) component).preView();
        }
        mainLayout.addComponent(component, MAIN_COLUMN, MAIN_ROW);
    }

    @Override
    public void init() {
        Window mainWindow = new Window("LOD Management Suite " + applicationVersion);

        mainWindow.setWidth("100%");
        mainLayout.setSizeFull();
        mainLayout.setRowExpandRatio(MAIN_ROW, 1.0f);
        mainWindow.setContent(mainLayout);

        CssLayout header = new CssLayout();
        header.setMargin(true);
        header.setHeight("15px");
        header.setCaption("LOD Management Suite");
        header.addStyleName("lodms-header");
        header.setIcon(new ClassResource("/at/punkt/lodms/logo.png", this));
        mainWindow.addComponent(header);
        initMenu();
        initConfigWindow();
        setTheme("lodms");
        com.vaadin.ui.Component startView = jobsView;
        if (startView instanceof View) {
            ((View) startView).preView();
        }
        mainLayout.addComponent(startView, MAIN_COLUMN, MAIN_ROW);
        setMainWindow(mainWindow);
        if (applicationConfig.get("lodms.home").equals(".")) {
            logger.info("Opening application config window");
            ApplicationConfigDialog dialog = new ApplicationConfigDialog(applicationConfig, jobService);
            mainWindow.addWindow(dialog);
        }
    }

    public void initMenu() {
        MenuBar menu = new MenuBar();
        menu.setWidth("100%");
        menu.addItem("New Job", new ThemeResource("../runo/icons/16/document.png"), new ShowDialogCommand(NewJobWizard.class, jobsView));
        menu.addItem("Manage Jobs", new ThemeResource("../runo/icons/16/settings.png"), new SwitchViewCommand(jobsView));
        menu.addItem("Error Reports", new ThemeResource("../runo/icons/16/attention.png"), new SwitchViewCommand(errorView));
        menu.addItem("About", new ThemeResource("../runo/icons/16/help.png"), new MenuBar.Command() {

            @Override
            public void menuSelected(MenuItem selectedItem) {
                if (aboutView.getParent() == null) {
                    getMainWindow().addWindow(aboutView);
                }
            }
        });
        mainLayout.addComponent(menu, MAIN_COLUMN, MENU_ROW);
    }

    public void initConfigWindow() {
        configWindow.setModal(true);
        configWindow.setResizable(true);
        configWindow.setWidth("850px");
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
}
