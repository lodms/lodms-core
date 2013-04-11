/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package at.punkt.lodms.web.view;

import at.punkt.lodms.spi.extract.Extractor;
import at.punkt.lodms.spi.load.Loader;
import at.punkt.lodms.spi.transform.Transformer;
import at.punkt.lodms.web.ETLJob;
import at.punkt.lodms.web.ETLJobMetadata;
import at.punkt.lodms.web.ETLJobService;
import at.punkt.lodms.web.LodmsApplication;
import at.punkt.lodms.web.event.RunningJobsUpdater;
import com.vaadin.data.Item;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.ItemClickEvent.ItemClickListener;
import com.vaadin.terminal.ThemeResource;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.DefaultFieldFactory;
import com.vaadin.ui.Embedded;
import com.vaadin.ui.Field;
import com.vaadin.ui.Form;
import com.vaadin.ui.HorizontalSplitPanel;
import com.vaadin.ui.Label;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window.Notification;
import com.vaadin.ui.themes.Reindeer;
import java.util.Date;
import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.scheduling.support.SimpleTriggerContext;
import org.springframework.stereotype.Component;
import org.vaadin.kim.countdownclock.CountdownClock;

/**
 *
 * @author Alex Kreiser
 */
@Component
@Scope("session")
public class ETLJobsView extends HorizontalSplitPanel implements View {

    private Table jobTable = new Table("Scheduled Jobs");
    private VerticalLayout jobInfo = new VerticalLayout();
    @Autowired(required = true)
    private TaskScheduler scheduler;
    private BeanItemContainer<ETLJob> beanContainer;
    @Autowired
    private ETLJobService jobService;
    @Autowired
    private ApplicationContext context;
    @Autowired
    private RunningJobsUpdater updater;

    private class ReadOnlyFieldFactory extends DefaultFieldFactory {

        @Override
        public Field createField(Item item, Object propertyId, com.vaadin.ui.Component uiContext) {
            Field f = super.createField(item, propertyId, uiContext);
            if (propertyId.equals("created")) {
                f = new TextField("Created");
            } else if (propertyId.equals("description")) {
                f = new TextArea("Description");
            }
            f.setReadOnly(true);
            f.setWidth(90, UNITS_PERCENTAGE);

            return f;
        }
    }

    @PostConstruct
    public void init() {
        beanContainer = new BeanItemContainer<ETLJob>(ETLJob.class, jobService.getJobs());
        jobTable.setContainerDataSource(beanContainer);
        jobTable.setVisibleColumns(new String[]{});
        jobTable.addGeneratedColumn("Name", new Table.ColumnGenerator() {

            @Override
            public com.vaadin.ui.Component generateCell(Table source, Object itemId, Object columnId) {
                return new Label(((ETLJob) itemId).getMetadata().getName());
            }
        });
        jobTable.addGeneratedColumn("Interval", new Table.ColumnGenerator() {

            @Override
            public com.vaadin.ui.Component generateCell(Table source, Object itemId, Object columnId) {
                return new Label(((ETLJob) itemId).getMetadata().getInterval());
            }
        });
        jobTable.addGeneratedColumn("Status", new Table.ColumnGenerator() {

            @Override
            public com.vaadin.ui.Component generateCell(Table source, Object itemId, Object columnId) {
                final ETLJob job = (ETLJob) itemId;
                if (job.isRunning()) {
                    Embedded embedded = new Embedded("", new ThemeResource("ajax-loader.gif"));
                    embedded.setDescription("Running...");
                    return embedded;
                } else if (job.getMetadata().isScheduled()) {
                    Embedded embedded = new Embedded("", new ThemeResource("../runo/icons/32/calendar.png"));
                    embedded.setDescription("Scheduled");
                    return embedded;
                } else {
                    Embedded embedded = new Embedded("", null);
                    embedded.setDescription("Hold");
                    return embedded;
                }
            }
        });
        jobTable.addGeneratedColumn("Schedule", new Table.ColumnGenerator() {

            @Override
            public com.vaadin.ui.Component generateCell(Table source, final Object itemId, Object columnId) {
                final ETLJob job = (ETLJob) itemId;
                if (job.getMetadata().isScheduled()) {
                    Button run = new Button("Cancel");
                    run.addListener(new Button.ClickListener() {

                        @Override
                        public void buttonClick(Button.ClickEvent event) {
                            job.getFuture().cancel(false);
                            job.getMetadata().setScheduled(false);
                            refreshJobTable();
                        }
                    });
                    return run;
                } else {
                    Button run = new Button("Schedule");
                    run.addListener(new Button.ClickListener() {

                        @Override
                        public void buttonClick(Button.ClickEvent event) {
                            job.setFuture(scheduler.schedule(job.getPipeline(), new CronTrigger(job.getMetadata().getInterval())));
                            job.getMetadata().setScheduled(true);
                            refreshJobTable();
                        }
                    });
                    return run;
                }
            }
        });
        jobTable.addGeneratedColumn("Run", new Table.ColumnGenerator() {

            @Override
            public com.vaadin.ui.Component generateCell(Table source, final Object itemId, Object columnId) {
                Button run = new Button("Run");
                if (((ETLJob) itemId).isRunning()) {
                    run.setEnabled(false);
                }
                run.addListener(new Button.ClickListener() {

                    @Override
                    public void buttonClick(Button.ClickEvent event) {
                        ((ETLJob) itemId).getPipeline().run();
                        refreshJobTable();
                    }
                });
                return run;
            }
        });

        jobTable.addGeneratedColumn("Delete", new Table.ColumnGenerator() {

            @Override
            public com.vaadin.ui.Component generateCell(final Table source, final Object itemId, final Object columnId) {
                Button delete = new Button("");
                if (((ETLJob) itemId).isRunning()) {
                    delete.setEnabled(false);
                }
                delete.addListener(new Button.ClickListener() {

                    @Override
                    public void buttonClick(Button.ClickEvent event) {
                        if (((ETLJob) itemId).getFuture() != null)
                            ((ETLJob) itemId).getFuture().cancel(true);
                        removeJob((ETLJob) itemId);
                        refreshJobTable();
                    }
                });
                delete.setStyleName(Reindeer.BUTTON_LINK);
                delete.setIcon(new ThemeResource("../runo/icons/32/cancel.png"));
                return delete;
            }
        });

        setSizeFull();
        jobTable.setSizeFull();
        jobTable.setColumnWidth("Interval", 100);
        jobTable.setColumnWidth("Run", 60);
        jobTable.setColumnWidth("Delete", 40);
        jobTable.setColumnWidth("Schedule", 85);
        jobTable.setColumnWidth("Status", 40);
        jobTable.setSelectable(true);
        jobTable.addListener(new ItemClickListener() {

            @Override
            public void itemClick(ItemClickEvent event) {
                ETLJob job = (ETLJob) event.getItemId();
                displayInfo(job);
            }
        });
        addComponent(jobTable);
        jobInfo.setSizeFull();
        jobInfo.setSpacing(true);
        addComponent(jobInfo);
    }

    private void removeJob(ETLJob job) {
        jobService.removeJob(job);
        beanContainer.removeItem(job);
    }

    @Override
    public void preView() {
        refreshJobTable();
    }

    private void refreshJobTable() {
        beanContainer.removeAllItems();
        beanContainer.addAll(jobService.getJobs());
        jobTable.select(jobTable.getNullSelectionItemId());
        jobInfo.removeAllComponents();
    }

    @Override
    public void postView() {
    }

    private void displayInfo(final ETLJob job) {
        jobInfo.removeAllComponents();
        jobInfo.setSizeFull();
        Form jobDetails = new Form();
        BeanItem<ETLJobMetadata> item = new BeanItem<ETLJobMetadata>(job.getMetadata());
        jobDetails.setFormFieldFactory(new ReadOnlyFieldFactory());
        jobDetails.setItemDataSource(item);
        jobDetails.setWidth(100, UNITS_PERCENTAGE);
        jobInfo.addComponent(jobDetails);

        final ETLJobsView view = this;
        final LodmsApplication application = ((LodmsApplication) getApplication());
        Button edit = new Button("Edit Job", new Button.ClickListener() {

            @Override
            public void buttonClick(ClickEvent event) {
                EditJobWizard editWizard = context.getBean(EditJobWizard.class);
                editWizard.setJob(job);
                editWizard.init();
                editWizard.setDialogCloseHandler(new DialogCloseHandler() {

                    @Override
                    public void close() {
                        application.viewComponent(view);
                    }
                });
                application.viewComponent(editWizard);
            }
        });
        if (job.getMetadata().isScheduled()) {
            edit.setEnabled(false);
            edit.setDescription("Please cancel the job before editing.");
        }
        if (job.getMetadata().isScheduled()) {
            final CountdownClock clock = new CountdownClock();
            Date nextExec = new CronTrigger(job.getMetadata().getInterval()).nextExecutionTime(new SimpleTriggerContext());
            clock.setDate(nextExec);
            clock.setFormat("Next run in %d days, %h hours, %m minutes and %s seconds");
            clock.addListener(new CountdownClock.EndEventListener() {

                @Override
                public void countDownEnded(CountdownClock clock) {
                    getWindow().showNotification("Job is being executed - please refresh the detail view.", Notification.TYPE_TRAY_NOTIFICATION);
                }
            });
            jobInfo.addComponent(clock);
            jobInfo.setComponentAlignment(clock, Alignment.MIDDLE_CENTER);
        }
        jobInfo.addComponent(edit);
        jobInfo.setComponentAlignment(edit, Alignment.BOTTOM_CENTER);

        TabSheet components = new TabSheet();
        components.setSizeFull();

        components.addTab(new ConfiguredComponentTable<Extractor>((LodmsApplication) getApplication(), Extractor.class, job.getPipeline().getExtractors(), new BeanItemContainer<Extractor>(Extractor.class, job.getPipeline().getExtractors()), false, false), "Extractors", null);
        components.addTab(new ConfiguredComponentTable<Transformer>((LodmsApplication) getApplication(), Transformer.class, job.getPipeline().getTransformers(), new BeanItemContainer<Transformer>(Transformer.class, job.getPipeline().getTransformers()), false, false), "Transformers", null);
        components.addTab(new ConfiguredComponentTable<Loader>((LodmsApplication) getApplication(), Loader.class, job.getPipeline().getLoaders(), new BeanItemContainer<Loader>(Loader.class, job.getPipeline().getLoaders()), false, false), "Loaders", null);
        jobInfo.addComponent(components);
        jobInfo.setExpandRatio(components, 0.65f);
    }
}
