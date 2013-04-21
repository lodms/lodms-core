/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package at.punkt.lodms.web.audit;

import at.punkt.lodms.PipelineEvent;
import at.punkt.lodms.spi.extract.ExtractEvent;
import at.punkt.lodms.spi.extract.ExtractFailedEvent;
import at.punkt.lodms.spi.load.LoadEvent;
import at.punkt.lodms.spi.load.LoadFailedEvent;
import at.punkt.lodms.spi.transform.TransformEvent;
import at.punkt.lodms.spi.transform.TransformFailedEvent;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.openrdf.model.URI;
import org.springframework.stereotype.Service;

/**
 *
 * @author Alex Kreiser
 */
@Service
public class AuditService {

    Map<String, JobStatistics> jobStatistics = new HashMap<String, JobStatistics>();
    Map<String, Deque<PipelineEvent>> pipelineEvents = new HashMap<String, Deque<PipelineEvent>>();
    HashMap<String, List<ExtractEvent>> extractMap = new HashMap<String, List<ExtractEvent>>();
    HashMap<String, List<TransformEvent>> transformMap = new HashMap<String, List<TransformEvent>>();
    HashMap<String, List<LoadEvent>> loadMap = new HashMap<String, List<LoadEvent>>();
    private final int MAX_QUEUE_SIZE = 15;

    public JobStatistics getStatistics(String pipelineId) {
        JobStatistics stats = jobStatistics.get(pipelineId);
        if (stats == null) {
            stats = new JobStatistics(pipelineId);
            jobStatistics.put(pipelineId, stats);
        }
        return stats;
    }

    public void addPipelineEvent(PipelineEvent event) {
        Deque<PipelineEvent> events = pipelineEvents.get(event.getPipeline().getId());
        if (events == null) {
            events = new ArrayDeque<PipelineEvent>();
            pipelineEvents.put(event.getPipeline().getId(), events);
        }
        events.addFirst(event);
        if (events.size() > MAX_QUEUE_SIZE) {
            String id = events.pollLast().getId();
            extractMap.remove(id);
            transformMap.remove(id);
            loadMap.remove(id);
        }
    }

    public Collection<PipelineEvent> getPipelineEvents(String id) {
        return pipelineEvents.get(id);
    }

    public void addExtractEvent(ExtractEvent event) {
        List<ExtractEvent> events = extractMap.get(event.getExtractContext().getId());
        if (events == null) {
            events = new ArrayList<ExtractEvent>();
            extractMap.put(event.getExtractContext().getId(), events);
        }
        events.add(event);
    }

    public Collection<ExtractEvent> getExtractEventsFor(String id) {
        return extractMap.get(id);
    }

    public int getComponentErrors(String id) {
        int errors = 0;
        if (extractMap.containsKey(id)) {
            for (ExtractEvent e : extractMap.get(id)) {
                if (e instanceof ExtractFailedEvent) {
                    errors++;
                }
            }
        }
        if (transformMap.containsKey(id)) {
            for (TransformEvent e : transformMap.get(id)) {
                if (e instanceof TransformFailedEvent) {
                    errors++;
                }
            }
        }
        if (loadMap.containsKey(id)) {
            for (LoadEvent e : loadMap.get(id)) {
                if (e instanceof LoadFailedEvent) {
                    errors++;
                }
            }
        }
        return errors;
    }

    public void addTransformEvent(TransformEvent event) {
        List<TransformEvent> events = transformMap.get(event.getTransformContext().getId());
        if (events == null) {
            events = new ArrayList<TransformEvent>();
            transformMap.put(event.getTransformContext().getId(), events);
        }
        events.add(event);
    }

    public Collection<TransformEvent> getTransformEventsFor(String eventId) {
        return transformMap.get(eventId);
    }

    public void addLoadEvent(LoadEvent event) {
        List<LoadEvent> events = loadMap.get(event.getLoadContext().getId());
        if (events == null) {
            events = new ArrayList<LoadEvent>();
            loadMap.put(event.getLoadContext().getId(), events);
        }
        events.add(event);
    }

    public Collection<LoadEvent> getLoadEventsFor(String eventId) {
        return loadMap.get(eventId);
    }
}