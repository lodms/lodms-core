/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package at.punkt.lodms.web.view;

import at.punkt.lodms.PipelineAbortedEvent;
import at.punkt.lodms.PipelineCompletedEvent;
import at.punkt.lodms.PipelineEvent;
import at.punkt.lodms.integration.UIComponent;
import at.punkt.lodms.spi.extract.ExtractCompletedEvent;
import at.punkt.lodms.spi.extract.ExtractEvent;
import at.punkt.lodms.spi.extract.ExtractFailedEvent;
import at.punkt.lodms.spi.load.LoadEvent;
import at.punkt.lodms.spi.load.LoadFailedEvent;
import at.punkt.lodms.spi.transform.TransformEvent;
import at.punkt.lodms.spi.transform.TransformFailedEvent;
import at.punkt.lodms.web.Job;
import at.punkt.lodms.web.JobService;
import at.punkt.lodms.web.audit.AuditService;
import at.punkt.lodms.web.audit.Warning;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.ItemClickEvent.ItemClickListener;
import com.vaadin.terminal.ThemeResource;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.HorizontalSplitPanel;
import com.vaadin.ui.Label;
import com.vaadin.ui.PopupView;
import com.vaadin.ui.Table;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window.Notification;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import javax.annotation.PostConstruct;
import org.apache.commons.lang.time.DurationFormatUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 *
 * @author Alex Kreiser
 */
@Component
@Scope("session")
public class ErrorReportingView extends HorizontalSplitPanel implements View {

    private final Logger logger = Logger.getLogger(ErrorReportingView.class);
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yy - HH:mm:ss");
    private Table jobTable = new Table("Scheduled Jobs");
    private GridLayout jobInfo = new GridLayout(1, 3);
    private final int PIPELINE_INFO = 1;
    private final int COMPONENTS_INFO = 2;
    private BeanItemContainer<Job> beanContainer;
    @Autowired
    private JobService jobService;
    @Autowired
    private AuditService auditService;

    @PostConstruct
    public void init() {
        beanContainer = new BeanItemContainer<Job>(Job.class, jobService.getJobs());
        jobTable.setContainerDataSource(beanContainer);
        jobTable.setVisibleColumns(new String[]{"id"});
        jobTable.setColumnWidth("id", 225);
        jobTable.addGeneratedColumn("Name", new Table.ColumnGenerator() {

            @Override
            public com.vaadin.ui.Component generateCell(Table source, Object itemId, Object columnId) {
                Label label = new Label(((Job) itemId).getMetadata().getName());
                label.setDescription(((Job) itemId).getMetadata().getDescription());
                return label;
            }
        });
        jobTable.setSizeFull();
        jobTable.setSelectable(true);
        jobTable.addListener(new ItemClickListener() {

            @Override
            public void itemClick(ItemClickEvent event) {
                final Job job = (Job) event.getItemId();
                showPipelineInfo(job);
            }
        });
        addComponent(jobTable);
        jobInfo.setSizeFull();
        jobInfo.setRowExpandRatio(1, 3);
        jobInfo.setRowExpandRatio(2, 3);
        addComponent(jobInfo);
        setSizeFull();
        setSplitPosition(35, UNITS_PERCENTAGE);
    }

    @Override
    public void preView() {
        beanContainer.removeAllItems();
        beanContainer.addAll(jobService.getJobs());
        jobInfo.removeAllComponents();
        jobTable.select(jobTable.getNullSelectionItemId());
        setSizeFull();
    }

    @Override
    public void postView() {
    }

    private void showPipelineInfo(final Job job) {
        jobInfo.removeAllComponents();
        if (auditService.getPipelineEvents(job.getPipeline().getId()) == null) {
            getWindow().showNotification("Job has not been executed yet.", Notification.TYPE_WARNING_MESSAGE);
            return;
        }
        Button refreshButton = new Button("Refresh", new Button.ClickListener() {

            @Override
            public void buttonClick(ClickEvent event) {
                showPipelineInfo(job);
            }
        });
        refreshButton.setIcon(new ThemeResource("../runo/icons/16/reload.png"));
        jobInfo.addComponent(refreshButton, 0, 0);
        Table pipelineEvents = new Table("Last Executions");
        pipelineEvents.setSizeFull();

        BeanItemContainer<PipelineEvent> events = new BeanItemContainer<PipelineEvent>(PipelineEvent.class);
        events.addAll(auditService.getPipelineEvents(job.getPipeline().getId()));
        pipelineEvents.setContainerDataSource(events);
        pipelineEvents.setSelectable(true);
        pipelineEvents.setMultiSelect(false);
        pipelineEvents.setVisibleColumns(new String[]{});
        pipelineEvents.setPageLength(15);
        pipelineEvents.addGeneratedColumn("date", new Table.ColumnGenerator() {

            @Override
            public com.vaadin.ui.Component generateCell(Table source, Object itemId, Object columnId) {
                Date date = new Date(((PipelineEvent) itemId).getTimestamp());
                return new Label(dateFormat.format(date));
            }
        });
        pipelineEvents.addGeneratedColumn("duration", new Table.ColumnGenerator() {

            @Override
            public com.vaadin.ui.Component generateCell(Table source, Object itemId, Object columnId) {
                if (itemId instanceof PipelineCompletedEvent) {
                    return new Label(getDuration(((PipelineCompletedEvent) itemId).getDuration()));
                }
                return null;
            }
        });
        pipelineEvents.addGeneratedColumn("component errors", new Table.ColumnGenerator() {

            @Override
            public com.vaadin.ui.Component generateCell(Table source, Object itemId, Object columnId) {
                int errors = auditService.getComponentErrors(((PipelineEvent) itemId).getId());

                Label errorLabel = new Label(String.valueOf(errors));
                if (errors > 0) {
                    errorLabel.addStyleName("lodms-component-error");
                } else {
                    errorLabel.addStyleName("lodms-component-success");
                }
                return errorLabel;
            }
        });
        pipelineEvents.setCellStyleGenerator(new Table.CellStyleGenerator() {

            @Override
            public String getStyle(Object itemId, Object propertyId) {
                if (itemId instanceof PipelineAbortedEvent) {
                    return "lodms-event-error";
                } else {
                    return "";
                }
            }
        });
        pipelineEvents.addListener(new ItemClickListener() {

            @Override
            public void itemClick(ItemClickEvent event) {
                PipelineEvent ev = (PipelineEvent) event.getItemId();
                showComponentInfo(ev.getId());
            }
        });
        jobInfo.addComponent(pipelineEvents, 0, PIPELINE_INFO);
    }

    private void showComponentInfo(String eventId) {
        jobInfo.removeComponent(0, COMPONENTS_INFO);
        HorizontalLayout components = new HorizontalLayout();
        components.setSizeFull();
        // ExtractEvents
        Table extractEventTable = new Table("Extractors");
        BeanItemContainer<ExtractEvent> extractEvents = new BeanItemContainer<ExtractEvent>(ExtractEvent.class);
        if (auditService.getExtractEventsFor(eventId) != null) {
            extractEvents.addAll(auditService.getExtractEventsFor(eventId));
        }
        extractEventTable.setContainerDataSource(extractEvents);
        extractEventTable.setVisibleColumns(new String[]{});
        extractEventTable.addGeneratedColumn("extractor", new Table.ColumnGenerator() {

            @Override
            public com.vaadin.ui.Component generateCell(Table source, Object itemId, Object columnId) {
                Label comp = new Label(((UIComponent) ((ExtractEvent) itemId).getExtractor()).asString());
                comp.setDescription(((UIComponent) ((ExtractEvent) itemId).getExtractor()).asString());
                return comp;
            }
        });
        extractEventTable.addGeneratedColumn("result", new Table.ColumnGenerator() {

            @Override
            public com.vaadin.ui.Component generateCell(Table source, Object itemId, Object columnId) {
                if (itemId instanceof ExtractFailedEvent) {
                    Label error = new Label("ERROR");
                    error.addStyleName("lodms-component-error");
                    //((ExtractFailedEvent) itemId).getException().printStackTrace(new PrintWriter(writer));
                    Exception e = ((ExtractFailedEvent) itemId).getException();
                    if (e.getMessage() != null)
                        error.setDescription(e.getMessage());
                    else if (e.getCause() != null && e.getCause().getMessage() != null)
                        error.setDescription(e.getCause().getMessage());
                    return error;
                } else {
                    Label success = new Label("OK");
                    success.addStyleName("lodms-component-success");
                    success.setDescription("Extracted " + ((ExtractCompletedEvent) itemId).getExtractContext().getTriplesExtracted() + " triples in " + getDuration(((ExtractCompletedEvent) itemId).getExtractContext().getDuration()));
                    return success;
                }
            }
        });
        extractEventTable.addGeneratedColumn("warnings", new Table.ColumnGenerator() {

            @Override
            public com.vaadin.ui.Component generateCell(Table source, Object itemId, Object columnId) {
                List<String> warnings = ((ExtractEvent)itemId).getExtractContext().getWarnings();
                if (warnings.isEmpty()) {
                    Label success = new Label("0");
                    success.addStyleName("lodms-component-success");
                    return success;
                } else {
                    VerticalLayout warnContent = new VerticalLayout();
                    warnContent.setWidth("800px");
                    warnContent.setMargin(true);
                    warnContent.setSpacing(true);
                    BeanItemContainer<Warning> warnContainer = new BeanItemContainer<Warning>(Warning.class);
                    for (String warning : warnings) {
                        warnContainer.addBean(new Warning(warning));
                    }
                    Table warnTable = new Table();
                    warnTable.setContainerDataSource(warnContainer);
                    warnTable.setWidth("750px");
                    warnContent.addComponent(warnTable);
                    PopupView warnPopup = new PopupView(String.valueOf(warnings.size()), warnContent);
                    warnPopup.setHideOnMouseOut(false);
                    warnPopup.addStyleName("lodms-component-warn");
                    return warnPopup;
                }
            }
        });
        extractEventTable.setColumnWidth("warnings", 55);
        extractEventTable.setColumnWidth("result", 50);
        extractEventTable.setSizeFull();
        components.addComponent(extractEventTable);

        // TransformEvents
        Table transformEventTable = new Table("Transformers");
        BeanItemContainer<TransformEvent> transformEvents = new BeanItemContainer<TransformEvent>(TransformEvent.class);
        if (auditService.getTransformEventsFor(eventId) != null) {
            transformEvents.addAll(auditService.getTransformEventsFor(eventId));
        }
        transformEventTable.setContainerDataSource(transformEvents);
        transformEventTable.setVisibleColumns(new String[]{});
        transformEventTable.addGeneratedColumn("transformer", new Table.ColumnGenerator() {

            @Override
            public com.vaadin.ui.Component generateCell(Table source, Object itemId, Object columnId) {
                Label comp = new Label(((UIComponent) ((TransformEvent) itemId).getTransformer()).asString());
                comp.setDescription(((UIComponent) ((TransformEvent) itemId).getTransformer()).asString());
                return comp;
            }
        });
        transformEventTable.addGeneratedColumn("result", new Table.ColumnGenerator() {

            @Override
            public com.vaadin.ui.Component generateCell(Table source, Object itemId, Object columnId) {
                if (itemId instanceof TransformFailedEvent) {
                    Label error = new Label("ERROR");
                    error.addStyleName("lodms-component-error");
                    Exception e = ((TransformFailedEvent) itemId).getException();
                    if (e.getMessage() != null)
                        error.setDescription(e.getMessage());
                    else if (e.getCause() != null && e.getCause().getMessage() != null)
                        error.setDescription(e.getCause().getMessage());
                    return error;
                } else {
                    Label success = new Label("OK");
                    success.addStyleName("lodms-component-success");
                    success.setDescription("Transformed in " + getDuration(((TransformEvent) itemId).getTransformContext().getDuration()));
                    return success;
                }
            }
        });

        transformEventTable.addGeneratedColumn("warnings", new Table.ColumnGenerator() {

            @Override
            public com.vaadin.ui.Component generateCell(Table source, Object itemId, Object columnId) {
                List<String> warnings = ((TransformEvent)itemId).getTransformContext().getWarnings();
                if (warnings.isEmpty()) {
                    Label success = new Label("0");
                    success.addStyleName("lodms-component-success");
                    return success;
                } else {
                    VerticalLayout warnContent = new VerticalLayout();
                    warnContent.setWidth("800px");
                    warnContent.setMargin(true);
                    warnContent.setSpacing(true);
                    BeanItemContainer<Warning> warnContainer = new BeanItemContainer<Warning>(Warning.class);
                    for (String warning : warnings) {
                        warnContainer.addBean(new Warning(warning));
                    }
                    Table warnTable = new Table();
                    warnTable.setContainerDataSource(warnContainer);
                    warnTable.setWidth("750px");
                    warnContent.addComponent(warnTable);
                    PopupView warnPopup = new PopupView(String.valueOf(warnings.size()), warnContent);
                    warnPopup.addStyleName("lodms-component-warn");
                    warnPopup.setHideOnMouseOut(false);
                    return warnPopup;
                }
            }
        });
        transformEventTable.setColumnWidth("warnings", 55);
        transformEventTable.setColumnWidth("result", 50);
        transformEventTable.setSizeFull();
        components.addComponent(transformEventTable);
        // LoadEvents
        Table loadEventTable = new Table("Loaders");
        BeanItemContainer<LoadEvent> loadEvents = new BeanItemContainer<LoadEvent>(LoadEvent.class);
        if (auditService.getLoadEventsFor(eventId) != null) {
            loadEvents.addAll(auditService.getLoadEventsFor(eventId));
        }
        loadEventTable.setContainerDataSource(loadEvents);
        loadEventTable.setVisibleColumns(new String[]{});
        loadEventTable.addGeneratedColumn("loader", new Table.ColumnGenerator() {

            @Override
            public com.vaadin.ui.Component generateCell(Table source, Object itemId, Object columnId) {
                Label comp = new Label(((UIComponent) ((LoadEvent) itemId).getLoader()).asString());
                comp.setDescription(((UIComponent) ((LoadEvent) itemId).getLoader()).asString());
                return comp;
            }
        });
        loadEventTable.addGeneratedColumn("result", new Table.ColumnGenerator() {

            @Override
            public com.vaadin.ui.Component generateCell(Table source, Object itemId, Object columnId) {
                if (itemId instanceof LoadFailedEvent) {
                    Label error = new Label("ERROR");
                    error.addStyleName("lodms-component-error");
                    Exception e = ((LoadFailedEvent) itemId).getException();
                    if (e.getMessage() != null)
                        error.setDescription(e.getMessage());
                    else if (e.getCause() != null && e.getCause().getMessage() != null)
                        error.setDescription(e.getCause().getMessage());
                    return error;
                } else {
                    List<String> warnings = ((LoadEvent) itemId).getLoadContext().getWarnings();
                    Label success = new Label("OK");
                    success.addStyleName("lodms-component-success");
                    success.setDescription("Loaded in " + getDuration(((LoadEvent) itemId).getLoadContext().getDuration()));
                    return success;
                }
            }
        });
        loadEventTable.addGeneratedColumn("warnings", new Table.ColumnGenerator() {

            @Override
            public com.vaadin.ui.Component generateCell(Table source, Object itemId, Object columnId) {
                List<String> warnings = ((LoadEvent)itemId).getLoadContext().getWarnings();
                if (warnings.isEmpty()) {
                    Label success = new Label("0");
                    success.addStyleName("lodms-component-success");
                    return success;
                } else {
                    VerticalLayout warnContent = new VerticalLayout();
                    warnContent.setWidth("800px");
                    warnContent.setMargin(true);
                    warnContent.setSpacing(true);
                    BeanItemContainer<Warning> warnContainer = new BeanItemContainer<Warning>(Warning.class);
                    for (String warning : warnings) {
                        warnContainer.addBean(new Warning(warning));
                    }
                    Table warnTable = new Table();
                    warnTable.setWidth("750px");
                    warnTable.setContainerDataSource(warnContainer);
                    warnContent.addComponent(warnTable);
                    PopupView warnPopup = new PopupView(String.valueOf(warnings.size()), warnContent);
                    warnPopup.addStyleName("lodms-component-warn");
                    warnPopup.setHideOnMouseOut(false);
                    return warnPopup;
                }
            }
        });
        loadEventTable.setColumnWidth("warnings", 55);
        loadEventTable.setColumnWidth("result", 50);
        loadEventTable.setSizeFull();
        components.addComponent(loadEventTable);
        components.setSizeFull();
        jobInfo.addComponent(components, 0, COMPONENTS_INFO);
    }

    private String getDuration(long milliseconds) {
        return DurationFormatUtils.formatDurationWords(milliseconds, true, true);
    }
}
