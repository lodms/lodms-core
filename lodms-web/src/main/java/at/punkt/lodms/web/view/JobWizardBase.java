/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package at.punkt.lodms.web.view;

import at.punkt.lodms.integration.ConfigBeanProvider;
import at.punkt.lodms.integration.ConfigDialogProvider;
import at.punkt.lodms.integration.UIComponent;
import at.punkt.lodms.spi.extract.Extractor;
import at.punkt.lodms.spi.load.Loader;
import at.punkt.lodms.spi.transform.Transformer;
import at.punkt.lodms.util.BeanItemContainerSorter;
import at.punkt.lodms.web.*;
import com.vaadin.data.Container.Sortable;
import com.vaadin.data.Item;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.data.util.ItemSorter;
import com.vaadin.terminal.Resource;
import com.vaadin.ui.*;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.support.CronTrigger;

/**
 *
 * @author Alex Kreiser (akreiser@gmail.com)
 */
public abstract class JobWizardBase extends VerticalLayout implements Dialog {

    private final Logger logger = Logger.getLogger(JobWizardBase.class);
    @Autowired(required = true)
    protected ETLJobService jobService;
    @Autowired(required = true)
    protected LodmsApplication application;
    @Autowired(required = true)
    protected ServiceListProvider provider;
    @Autowired(required = true)
    protected TaskScheduler scheduler;
    protected HorizontalSplitPanel extractorDialog = new HorizontalSplitPanel();
    protected HorizontalSplitPanel transformerDialog = new HorizontalSplitPanel();
    protected HorizontalSplitPanel loaderDialog = new HorizontalSplitPanel();
    protected VerticalLayout jobDialog = new VerticalLayout();
    protected DialogCloseHandler closeHandler;
    protected BeanItemContainer<Extractor> selectedExtractors;
    protected BeanItemContainer<Transformer> selectedTransformers;
    protected BeanItemContainer<Loader> selectedLoaders;
    protected ETLJob job;

    public void init() {
        TabSheet tabsheet = new TabSheet();
        tabsheet.setWidth("100%");

        selectedExtractors = new BeanItemContainer<Extractor>(Extractor.class, job.getPipeline().getExtractors());
        selectedTransformers = new BeanItemContainer<Transformer>(Transformer.class, job.getPipeline().getTransformers());
        selectedLoaders = new BeanItemContainer<Loader>(Loader.class, job.getPipeline().getLoaders());
        initGenericDialog(Extractor.class, extractorDialog, provider.getAvailableExtractors(), job.getPipeline().getExtractors(), selectedExtractors);
        initGenericDialog(Loader.class, loaderDialog, provider.getAvailableLoaders(), job.getPipeline().getLoaders(), selectedLoaders);
        initGenericDialog(Transformer.class, transformerDialog, provider.getAvailableTransformers(), job.getPipeline().getTransformers(), selectedTransformers);

        initJobDialog(job);
        addComponent(jobDialog);
        tabsheet.addTab(extractorDialog, "Extractors", null, 0);
        tabsheet.addTab(transformerDialog, "Transformers", null, 1);
        tabsheet.addTab(loaderDialog, "Loaders", null, 2);
        tabsheet.setSizeFull();
        addComponent(tabsheet);
        setExpandRatio(tabsheet, 0.75f);
        setSizeFull();
    }
    
    private <T> void initGenericDialog(Class<T> type, HorizontalSplitPanel dialog, List<T> components, final List<T> jobComponents, final BeanItemContainer<T> selected) {
        dialog.setSizeFull();
        final VerticalLayout availableComponents = new VerticalLayout();
        availableComponents.setMargin(true);
        availableComponents.setSpacing(true);
        selected.setItemSorter(new BeanItemContainerSorter(jobComponents));
        for (final T component : components) {
            if (!(component instanceof UIComponent)) {
                continue;
            }
            UIComponent uiComponent = (UIComponent) component;

            final Panel panel = new Panel(" " + uiComponent.getName());
            panel.setWidth(300, UNITS_PIXELS);
            Resource icon = uiComponent.getIcon(application);
            if (icon != null) {
                panel.setIcon(icon);
            }

            VerticalLayout compLayout = new VerticalLayout();
            compLayout.setMargin(true);
            compLayout.setSpacing(true);
            panel.setContent(compLayout);
            compLayout.addComponent(new Label(uiComponent.getDescription()));
            Button addButton = new Button("Add");
            compLayout.addComponent(addButton);
            addButton.addListener(new Button.ClickListener() {

                @Override
                public void buttonClick(Button.ClickEvent event) {
                    try {
                        final T instance = (T) component.getClass().newInstance();
                        if (instance instanceof ConfigDialogProvider) {
                            application.displayConfigWindow((ConfigDialogProvider) instance, new ConfigSuccessHandler() {

                                @Override
                                public void configured() {
                                    jobComponents.add(instance);
                                    selected.addBean(instance);
                                }
                            }, null);
                        } else if (instance instanceof ConfigBeanProvider) {
                            application.displayConfigWindow((ConfigBeanProvider) instance, new ConfigSuccessHandler() {

                                @Override
                                public void configured() {
                                    jobComponents.add(instance);
                                    selected.addBean(instance);
                                }
                            }, null);
                        } else {
                            jobComponents.add(instance);
                            selected.addBean(instance);
                        }
                    } catch (InstantiationException ex) {
                        logger.error(ex.getMessage(), ex);
                    } catch (IllegalAccessException ex) {
                        logger.error(ex.getMessage(), ex);
                    }
                }
            });
            availableComponents.addComponent(panel);
        }
        dialog.addComponent(availableComponents);
        dialog.addComponent(new ConfiguredComponentTable(application, type, jobComponents, selected, true, true));
    }

    private void initJobDialog(final ETLJob job) {
        jobDialog.setSpacing(true);
        jobDialog.setMargin(true);
        final Form form = new Form();
        form.setFormFieldFactory(new DefaultFieldFactory() {

            @Override
            public Field createField(Item item, Object propertyId, Component uiContext) {
                if (propertyId.equals("id")) {
                    TextField field = new TextField("ID");
                    field.setEnabled(false);
                    return field;
                } else if (propertyId.equals("name")) {
                    TextField field = new TextField("Name");
                    field.setDescription("A short, human-readable name or label that identifies the job.");
                    field.setRequired(true);
                    field.setRequiredError("Please provide a name!");
                    field.setWidth(200, UNITS_PIXELS);
                    return field;
                } else if (propertyId.equals("description")) {
                    TextArea field = new TextArea("Description");
                    field.setColumns(30);
                    field.setRows(3);
                    field.setDescription("A more detailed description of the job.");
                    return field;
                } else if (propertyId.equals("interval")) {
                    TextField field = new TextField("Execution Interval");
                    field.setDescription("A cron expression that is used to schedule and execute the job.");
                    field.setRequiredError("Must be a valid cron expression!");
                    return field;
                }
                return super.createField(item, propertyId, uiContext);
            }
        });
        if (job.getMetadata() == null)
            job.setMetadata(new ETLJobMetadata());
        final ETLJobMetadata metadata = job.getMetadata();
        BeanItem<ETLJobMetadata> beanItem = new BeanItem<ETLJobMetadata>(metadata, Arrays.asList("name", "description", "scheduled", "interval"));
        form.setItemDataSource(beanItem);
        jobDialog.addComponent(form);
        Button saveJob = new Button("Save");
        HorizontalLayout buttons = new HorizontalLayout();
        buttons.setSpacing(true);
        buttons.addComponent(saveJob);
        saveJob.addListener(new Button.ClickListener() {

            @Override
            public void buttonClick(Button.ClickEvent event) {
                if (((CheckBox)form.getField("scheduled")).booleanValue()) {
                    ((TextField)form.getField("interval")).setRequired(true);
                } else {
                    ((TextField)form.getField("interval")).setRequired(false);
                }
                if (selectedExtractors.size() == 0) {
                    getWindow().showNotification("Please configure at least 1 extractor!", Window.Notification.TYPE_ERROR_MESSAGE);
                    return;
                } else if (selectedLoaders.size() == 0) {
                    getWindow().showNotification("No loader has been configured", Window.Notification.TYPE_WARNING_MESSAGE);
                }
                form.commit();
                job.getMetadata().setCreated(new Date());
                if (job.getFuture() != null)
                    job.getFuture().cancel(true);
                if (job.getMetadata().isScheduled()) {
                    job.setFuture(scheduler.schedule(job.getPipeline(), new CronTrigger(job.getMetadata().getInterval())));
                }
                try {
                    if (!jobService.getJobs().contains(job)) {
                        jobService.addJob(job);
                    }
                    jobService.persistJobs();
                } catch (Exception ex) {
                    logger.error("Unable to persist jobs after saving a changed job.", ex);
                } finally {
                    closeHandler.close();
                }
            }
        });
        Button cancel = new Button("Cancel");
        cancel.addListener(new Button.ClickListener() {

            @Override
            public void buttonClick(Button.ClickEvent event) {
                closeHandler.close();
            }
        });
        buttons.addComponent(cancel);
        jobDialog.addComponent(buttons);
    }
    
    @Override
    public void setDialogCloseHandler(DialogCloseHandler handler) {
        this.closeHandler = handler;
    }
}
