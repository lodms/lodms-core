/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package at.punkt.lodms.web;

import at.punkt.lodms.web.dialog.Dialog;
import at.punkt.lodms.web.dialog.DialogCloseHandler;
import at.punkt.lodms.web.dialog.AboutDialog;
import at.punkt.lodms.web.LodmsApplication;
import at.punkt.lodms.web.view.ErrorReportingView;
import at.punkt.lodms.web.view.ExistingJobsView;
import at.punkt.lodms.web.view.NewJobView;
import at.punkt.lodms.web.view.View;
import com.vaadin.terminal.ClassResource;
import com.vaadin.terminal.ThemeResource;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.MenuBar;
import com.vaadin.ui.Window;
import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 *
 * @author Alex Kreiser (akreiser@gmail.com)
 */
@Component
@Scope("session")
public class LodmsMainWindow extends Window {

    private final static int MAIN_COLUMN = 0;
    private final static int MAIN_ROW = 2;
    private final static int MENU_ROW = 1;
    private GridLayout layout;
    
    @Autowired
    private ExistingJobsView jobsView;
    @Autowired
    private ErrorReportingView errorView;
    @Autowired
    private AboutDialog aboutDialog;
    
    @Autowired
    private ApplicationContext context;
    @Autowired
    private String applicationVersion;

    public void init() {
        setCaption("LOD Management Suite " + applicationVersion);
        setWidth("100%");
        
        initLayout();
        initHeader();
        initMenu();
        setStartView(jobsView);
    }

    private void initLayout() {
        layout = new GridLayout(1, 3);
        layout.setSizeFull();
        layout.setRowExpandRatio(MAIN_ROW, 1.0f);
        setContent(layout);
    }
    
    private void initHeader() {
        CssLayout header = new CssLayout();
        header.setMargin(true);
        header.setHeight("15px");
        header.setCaption("LOD Management Suite");
        header.addStyleName("lodms-header");
        header.setIcon(new ClassResource("/at/punkt/lodms/logo.png", getApplication()));
        addComponent(header);
    }

    private void initMenu() {
        MenuBar menu = new MenuBar();

        menu.setWidth("100%");
        menu.addItem("New Job", new ThemeResource("../runo/icons/16/document.png"), new ShowDialogCommand(NewJobView.class, jobsView));
        menu.addItem("Manage Jobs", new ThemeResource("../runo/icons/16/settings.png"), new SwitchViewCommand(jobsView));
        menu.addItem("Error Reports", new ThemeResource("../runo/icons/16/attention.png"), new SwitchViewCommand(errorView));
        menu.addItem("About", new ThemeResource("../runo/icons/16/help.png"), new MenuBar.Command() {
            @Override
            public void menuSelected(MenuBar.MenuItem selectedItem) {
                ((LodmsApplication)getApplication()).showDialog(aboutDialog);
            }
        });
        layout.addComponent(menu, MAIN_COLUMN, MENU_ROW);
    }

    public void showView(com.vaadin.ui.Component component) {
        com.vaadin.ui.Component currentView = layout.getComponent(MAIN_COLUMN, MAIN_ROW);
        if (currentView != null) {
            layout.removeComponent(MAIN_COLUMN, MAIN_ROW);
        }
        if (currentView instanceof View) {
            ((View) currentView).postView();
        }
        if (component instanceof View) {
            ((View) component).preView();
        }
        layout.addComponent(component, MAIN_COLUMN, MAIN_ROW);
    }

    private void setStartView(com.vaadin.ui.Component startView) {
        showView(startView);
    }

    private class SwitchViewCommand implements MenuBar.Command {

        final com.vaadin.ui.Component component;

        public SwitchViewCommand(com.vaadin.ui.Component component) {
            this.component = component;
        }

        @Override
        public void menuSelected(MenuBar.MenuItem selectedItem) {
            showView(component);
        }
    }

    private class ShowDialogCommand extends SwitchViewCommand {

        final Class<? extends Dialog> dialogClass;

        public ShowDialogCommand(Class dialogClass, com.vaadin.ui.Component component) {
            super(component);
            this.dialogClass = dialogClass;
        }

        @Override
        public void menuSelected(final MenuBar.MenuItem selectedItem) {

            final Dialog dialog = (Dialog) context.getBean(dialogClass);
            dialog.setDialogCloseHandler(new DialogCloseHandler() {
                @Override
                public void close() {
                    showView(component);
                }
            });
            showView(dialog);
        }
    }
}
