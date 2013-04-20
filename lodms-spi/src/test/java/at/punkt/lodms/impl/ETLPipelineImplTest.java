/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package at.punkt.lodms.impl;

import at.punkt.lodms.Disableable;
import at.punkt.lodms.ETLEvent;
import at.punkt.lodms.PipelineAbortedEvent;
import at.punkt.lodms.PipelineCompletedEvent;
import at.punkt.lodms.PipelineStartedEvent;
import at.punkt.lodms.spi.extract.ExtractCompletedEvent;
import at.punkt.lodms.spi.extract.ExtractContext;
import at.punkt.lodms.spi.extract.ExtractException;
import at.punkt.lodms.spi.extract.ExtractFailedEvent;
import at.punkt.lodms.spi.extract.Extractor;
import at.punkt.lodms.spi.load.LoadCompletedEvent;
import at.punkt.lodms.spi.load.LoadContext;
import at.punkt.lodms.spi.load.LoadException;
import at.punkt.lodms.spi.load.LoadFailedEvent;
import at.punkt.lodms.spi.load.Loader;
import at.punkt.lodms.spi.transform.TransformCompletedEvent;
import at.punkt.lodms.spi.transform.TransformContext;
import at.punkt.lodms.spi.transform.TransformException;
import at.punkt.lodms.spi.transform.TransformFailedEvent;
import at.punkt.lodms.spi.transform.Transformer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import static org.junit.Assert.*;
import org.openrdf.model.Resource;
import org.openrdf.model.URI;
import org.openrdf.model.impl.BNodeImpl;
import org.openrdf.model.impl.StatementImpl;
import org.openrdf.model.vocabulary.RDF;
import org.openrdf.repository.Repository;
import org.openrdf.rio.RDFHandler;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationEventPublisher;

/**
 *
 * @author akreiser@gmail.com
 */
public class ETLPipelineImplTest extends RepositoryBasedTest {

    static String id = "pipeline:uuid";

    @Test
    public void testGetId() {
        ETLPipelineImpl instance = new ETLPipelineImpl(id, new MockApplicationEventPublisher(), repository);
        String result = instance.getId();
        assertEquals(id, result);
    }

    @Test
    public void testSetId() {
        String newId = "new:id";
        ETLPipelineImpl instance = new ETLPipelineImpl();
        instance.setId(newId);
        String result = instance.getId();
        assertEquals(newId, result);
    }

    @Test
    public void testGetRepository() {
        ETLPipelineImpl instance = new ETLPipelineImpl(id, new MockApplicationEventPublisher(), repository);
        Repository expResult = repository;
        Repository result = instance.getRepository();
        assertEquals(expResult, result);
    }

    @Test
    public void testSetRepository() {
        ETLPipelineImpl instance = new ETLPipelineImpl();
        instance.setRepository(repository);
        Repository result = instance.getRepository();
        assertEquals(repository, result);
    }

    @Test
    public void testGetEventPublisher() {
        ApplicationEventPublisher publisher = new MockApplicationEventPublisher();
        ETLPipelineImpl instance = new ETLPipelineImpl(id, publisher, repository);

        ApplicationEventPublisher result = instance.getEventPublisher();
        assertEquals(publisher, result);
    }

    @Test
    public void testSetAndGetExtractors() {
        List<Extractor> extractors = new ArrayList<Extractor>();
        extractors.add(new MockExtractor());
        ETLPipelineImpl instance = new ETLPipelineImpl();
        instance.setExtractors(extractors);

        assertEquals(extractors, instance.getExtractors());
    }

    /**
     * Test of getLoaders method, of class ETLPipelineImpl.
     */
    @Test
    public void testSetAndGetLoaders() {
        List<Loader> loaders = new ArrayList<Loader>();
        loaders.add(new MockLoader());
        ETLPipelineImpl instance = new ETLPipelineImpl();
        instance.setLoaders(loaders);

        assertEquals(loaders, instance.getLoaders());
    }

    @Test
    public void testSetAndGetTransformers() {
        List<Transformer> transformers = new ArrayList<Transformer>();
        transformers.add(new MockTransformer());
        ETLPipelineImpl instance = new ETLPipelineImpl();
        instance.setTransformers(transformers);

        assertEquals(transformers, instance.getTransformers());
    }

    @Test
    public void testIsCancelAllowed() {
        ETLPipelineImpl instance = new ETLPipelineImpl();
        boolean expResult = true;
        boolean result = instance.isCancelAllowed();
        assertEquals(expResult, result);
    }

    @Test
    public void testSetCancelAllowed() {
        boolean cancelAllowed = false;
        ETLPipelineImpl instance = new ETLPipelineImpl();
        instance.setCancelAllowed(cancelAllowed);
        assertEquals(false, instance.isCancelAllowed());
    }

    @Test
    public void testRunStartedEventFired() {
        MockApplicationEventPublisher eventPublisher = new MockApplicationEventPublisher();
        ETLPipelineImpl instance = new ETLPipelineImpl(id, eventPublisher, repository);
        instance.run();

        List<PipelineStartedEvent> events = eventPublisher.getPublishedEventsOfType(PipelineStartedEvent.class);
        Assert.assertEquals(1, events.size());
    }

    @Test
    public void testRunStartedEventProperties() {
        MockApplicationEventPublisher eventPublisher = new MockApplicationEventPublisher();
        ETLPipelineImpl instance = new ETLPipelineImpl(id, eventPublisher, repository);
        instance.run();

        PipelineStartedEvent startedEvent = eventPublisher.getPublishedEventsOfType(PipelineStartedEvent.class).get(0);
        Assert.assertEquals(instance, startedEvent.getPipeline());
        Assert.assertNotNull(startedEvent.getId());
    }

    @Test
    public void testRunCompletedEventFired() {
        MockApplicationEventPublisher eventPublisher = new MockApplicationEventPublisher();
        ETLPipelineImpl instance = new ETLPipelineImpl(id, eventPublisher, repository);
        instance.run();

        List<PipelineCompletedEvent> events = eventPublisher.getPublishedEventsOfType(PipelineCompletedEvent.class);
        Assert.assertEquals(1, events.size());
    }

    @Test
    public void testRunCompletedEventProperties() throws InterruptedException {
        MockApplicationEventPublisher eventPublisher = new MockApplicationEventPublisher();
        ETLPipelineImpl instance = new ETLPipelineImpl(id, eventPublisher, repository);

        Extractor delayingExtractor = new Extractor() {
            @Override
            public void extract(RDFHandler handler, ExtractContext context) throws ExtractException {
                try {
                    Thread.sleep(10);
                } catch (InterruptedException ex) {
                    throw new ExtractException(ex);
                }
            }
        };
        instance.setExtractors(Arrays.asList(delayingExtractor));

        instance.run();
        PipelineCompletedEvent completedEvent = eventPublisher.getPublishedEventsOfType(PipelineCompletedEvent.class).get(0);
        Assert.assertEquals(instance, completedEvent.getPipeline());
        Assert.assertNotNull(completedEvent.getId());
        Assert.assertTrue(completedEvent.getDuration() > 0);
    }

    @Test
    public void testRunExecuteExtractors() {
        ETLPipelineImpl instance = new ETLPipelineImpl(id, new MockApplicationEventPublisher(), repository);

        instance.setExtractors(getMockExtractors(2));
        instance.run();
        for (Extractor extractor : instance.getExtractors()) {
            assertTrue(((MockExtractor) extractor).executed);
        }
    }

    @Test
    public void testRunExtractCompletedEventFired() {
        MockApplicationEventPublisher eventPublisher = new MockApplicationEventPublisher();
        ETLPipelineImpl instance = new ETLPipelineImpl(id, eventPublisher, repository);

        Extractor extractor = new MockExtractor();
        instance.setExtractors(Arrays.asList(extractor));
        instance.run();

        ExtractCompletedEvent completedEvent = eventPublisher.getPublishedEventsOfType(ExtractCompletedEvent.class).get(0);
        Assert.assertEquals(extractor, completedEvent.getExtractor());
    }

    @Test
    public void testRunExtractContext() {
        MockApplicationEventPublisher eventPublisher = new MockApplicationEventPublisher();
        ETLPipelineImpl instance = new ETLPipelineImpl(id, eventPublisher, repository);

        instance.setExtractors(getMockExtractors(1));
        instance.run();

        ExtractCompletedEvent completedEvent = eventPublisher.getPublishedEventsOfType(ExtractCompletedEvent.class).get(0);
        ExtractContext context = completedEvent.getExtractContext();
        Assert.assertEquals(instance, context.getPipeline());
        Assert.assertNotNull(context.getId());
        Assert.assertTrue(context.getDuration() >= 0);
    }

    @Test
    public void testRunExtractContextTriplesExtracted() {
        MockApplicationEventPublisher eventPublisher = new MockApplicationEventPublisher();
        ETLPipelineImpl instance = new ETLPipelineImpl(id, eventPublisher, repository);

        Extractor extractor = new Extractor() {
            @Override
            public void extract(RDFHandler handler, ExtractContext context) throws ExtractException {
                try {
                    Resource id = new BNodeImpl("id:1");
                    handler.handleStatement(new StatementImpl(id, RDF.TYPE, RDF.STATEMENT));
                    handler.handleStatement(new StatementImpl(id, RDF.SUBJECT, id));
                    handler.handleStatement(new StatementImpl(id, RDF.PREDICATE, id));
                    handler.handleStatement(new StatementImpl(id, RDF.OBJECT, id));
                } catch (Exception ex) {
                    throw new ExtractException(ex);
                }
            }
        };
        instance.setExtractors(Arrays.asList(extractor));
        instance.run();

        ExtractCompletedEvent completedEvent = eventPublisher.getPublishedEventsOfType(ExtractCompletedEvent.class).get(0);
        ExtractContext context = completedEvent.getExtractContext();
        Assert.assertEquals(4, context.getTriplesExtracted());
    }

    @Test
    public void testRunExtractContextWarnings() {
        MockApplicationEventPublisher eventPublisher = new MockApplicationEventPublisher();
        ETLPipelineImpl instance = new ETLPipelineImpl(id, eventPublisher, repository);

        Extractor extractor = new MockExtractor() {
            @Override
            public void extract(RDFHandler handler, ExtractContext context) throws ExtractException {
                context.getWarnings().add("Test Warning 1");
                context.getWarnings().add("Test Warning 2");
            }
        };
        instance.setExtractors(Arrays.asList(extractor));
        instance.run();

        ExtractCompletedEvent completedEvent = eventPublisher.getPublishedEventsOfType(ExtractCompletedEvent.class).get(0);
        ExtractContext context = completedEvent.getExtractContext();
        Assert.assertEquals(2, context.getWarnings().size());
        Assert.assertEquals("Test Warning 1", context.getWarnings().get(0));
        Assert.assertEquals("Test Warning 2", context.getWarnings().get(1));
    }

    @Test
    public void testRunExtractContextCustomData() {
        MockApplicationEventPublisher eventPublisher = new MockApplicationEventPublisher();
        ETLPipelineImpl instance = new ETLPipelineImpl(id, eventPublisher, repository);

        Extractor extractor = new MockExtractor() {
            @Override
            public void extract(RDFHandler handler, ExtractContext context) throws ExtractException {
                context.getCustomData().put("key", "Test");
            }
        };
        instance.setExtractors(Arrays.asList(extractor));
        instance.run();

        ExtractCompletedEvent completedEvent = eventPublisher.getPublishedEventsOfType(ExtractCompletedEvent.class).get(0);
        ExtractContext context = completedEvent.getExtractContext();
        Assert.assertEquals("Test", context.getCustomData().get("key"));
    }

    @Test
    public void testRunExtractFailedEventFired() {
        MockApplicationEventPublisher eventPublisher = new MockApplicationEventPublisher();
        ETLPipelineImpl instance = new ETLPipelineImpl(id, eventPublisher, repository);

        Extractor extractor = new MockExtractor() {
            @Override
            public void extract(RDFHandler handler, ExtractContext context) throws ExtractException {
                throw new ExtractException("Test");
            }
        };
        instance.setExtractors(Arrays.asList(extractor));
        instance.run();

        ExtractFailedEvent failedEvent = eventPublisher.getPublishedEventsOfType(ExtractFailedEvent.class).get(0);
        Assert.assertEquals(extractor, failedEvent.getExtractor());
        Assert.assertNotNull(failedEvent.getException());
    }

    @Test
    public void testRunExtractorAbortedEventFired() {
        MockApplicationEventPublisher eventPublisher = new MockApplicationEventPublisher();
        ETLPipelineImpl instance = new ETLPipelineImpl(id, eventPublisher, repository);

        Extractor extractor = new MockExtractor() {
            @Override
            public void extract(RDFHandler handler, ExtractContext context) throws ExtractException {
                context.cancelPipeline("Test");
            }
        };
        instance.setExtractors(Arrays.asList(extractor));
        instance.run();

        PipelineAbortedEvent abortedEvent = eventPublisher.getPublishedEventsOfType(PipelineAbortedEvent.class).get(0);
        Assert.assertEquals(instance, abortedEvent.getPipeline());
        Assert.assertEquals("Test", abortedEvent.getMessage());
    }

    @Test
    public void testRunExtractorSkippedAfterAbortedEvent() {
        MockApplicationEventPublisher eventPublisher = new MockApplicationEventPublisher();
        ETLPipelineImpl instance = new ETLPipelineImpl(id, eventPublisher, repository);

        Extractor extractor = new MockExtractor() {
            @Override
            public void extract(RDFHandler handler, ExtractContext context) throws ExtractException {
                context.cancelPipeline("Test");
            }
        };
        MockExtractor skippedExtractor = new MockExtractor();
        instance.setExtractors(Arrays.asList(extractor, skippedExtractor));
        instance.run();

        Assert.assertEquals(false, skippedExtractor.executed);
    }

    @Test
    public void testRunExecuteTransformers() {
        ETLPipelineImpl instance = new ETLPipelineImpl(id, new MockApplicationEventPublisher(), repository);

        instance.setTransformers(getMockTransformers(2));
        instance.run();
        for (Transformer transformer : instance.getTransformers()) {
            assertTrue(((MockTransformer) transformer).executed);
        }
    }

    @Test
    public void testRunTransformContext() {
        MockApplicationEventPublisher eventPublisher = new MockApplicationEventPublisher();
        ETLPipelineImpl instance = new ETLPipelineImpl(id, eventPublisher, repository);

        instance.setTransformers(getMockTransformers(1));
        instance.run();

        TransformCompletedEvent completedEvent = eventPublisher.getPublishedEventsOfType(TransformCompletedEvent.class).get(0);
        TransformContext context = completedEvent.getTransformContext();
        Assert.assertEquals(instance, context.getPipeline());
        Assert.assertNotNull(context.getId());
        Assert.assertTrue(context.getDuration() >= 0);
    }

    @Test
    public void testRunTransformContextWarnings() {
        MockApplicationEventPublisher eventPublisher = new MockApplicationEventPublisher();
        ETLPipelineImpl instance = new ETLPipelineImpl(id, eventPublisher, repository);

        Transformer transformer = new Transformer() {
            @Override
            public void transform(Repository repository, URI graph, TransformContext context) throws TransformException {
                context.getWarnings().add("Test Warning 1");
                context.getWarnings().add("Test Warning 2");
            }
        };
        instance.setTransformers(Arrays.asList(transformer));
        instance.run();

        TransformCompletedEvent completedEvent = eventPublisher.getPublishedEventsOfType(TransformCompletedEvent.class).get(0);
        TransformContext context = completedEvent.getTransformContext();
        Assert.assertEquals(2, context.getWarnings().size());
        Assert.assertEquals("Test Warning 1", context.getWarnings().get(0));
        Assert.assertEquals("Test Warning 2", context.getWarnings().get(1));
    }

    @Test
    public void testRunTransformContextCustomData() {
        MockApplicationEventPublisher eventPublisher = new MockApplicationEventPublisher();
        ETLPipelineImpl instance = new ETLPipelineImpl(id, eventPublisher, repository);

        Transformer transformer = new Transformer() {
            @Override
            public void transform(Repository repository, URI graph, TransformContext context) throws TransformException {
                context.getCustomData().put("key", "Test");
            }
        };
        instance.setTransformers(Arrays.asList(transformer));
        instance.run();

        TransformCompletedEvent completedEvent = eventPublisher.getPublishedEventsOfType(TransformCompletedEvent.class).get(0);
        TransformContext context = completedEvent.getTransformContext();
        Assert.assertEquals("Test", context.getCustomData().get("key"));
    }

    @Test
    public void testRunTransformCompletedEventFired() {
        MockApplicationEventPublisher eventPublisher = new MockApplicationEventPublisher();
        ETLPipelineImpl instance = new ETLPipelineImpl(id, eventPublisher, repository);

        Transformer transformer = new MockTransformer();
        instance.setTransformers(Arrays.asList(transformer));
        instance.run();

        TransformCompletedEvent completedEvent = eventPublisher.getPublishedEventsOfType(TransformCompletedEvent.class).get(0);
        Assert.assertEquals(transformer, completedEvent.getTransformer());
    }

    @Test
    public void testRunTransformFailedEventFired() {
        MockApplicationEventPublisher eventPublisher = new MockApplicationEventPublisher();
        ETLPipelineImpl instance = new ETLPipelineImpl(id, eventPublisher, repository);

        Transformer transformer = new MockTransformer() {
            @Override
            public void transform(Repository repository, URI graph, TransformContext context) throws TransformException {
                throw new TransformException("Test");
            }
        };
        instance.setTransformers(Arrays.asList(transformer));
        instance.run();

        TransformFailedEvent failedEvent = eventPublisher.getPublishedEventsOfType(TransformFailedEvent.class).get(0);
        Assert.assertEquals(transformer, failedEvent.getTransformer());
        Assert.assertNotNull(failedEvent.getException());
    }

    @Test
    public void testRunTransformerAbortedEventFired() {
        MockApplicationEventPublisher eventPublisher = new MockApplicationEventPublisher();
        ETLPipelineImpl instance = new ETLPipelineImpl(id, eventPublisher, repository);

        Transformer transformer = new MockTransformer() {
            @Override
            public void transform(Repository repository, URI graph, TransformContext context) throws TransformException {
                context.cancelPipeline("Test");
            }
        };
        instance.setTransformers(Arrays.asList(transformer));
        instance.run();

        PipelineAbortedEvent abortedEvent = eventPublisher.getPublishedEventsOfType(PipelineAbortedEvent.class).get(0);
        Assert.assertEquals(instance, abortedEvent.getPipeline());
        Assert.assertEquals("Test", abortedEvent.getMessage());
    }

    @Test
    public void testRunExecuteLoaders() {
        ETLPipelineImpl instance = new ETLPipelineImpl(id, new MockApplicationEventPublisher(), repository);

        instance.setLoaders(getMockLoaders(2));
        instance.run();
        for (Loader loader : instance.getLoaders()) {
            assertTrue(((MockLoader) loader).executed);
        }
    }

    @Test
    public void testRunLoadContext() {
        MockApplicationEventPublisher eventPublisher = new MockApplicationEventPublisher();
        ETLPipelineImpl instance = new ETLPipelineImpl(id, eventPublisher, repository);

        instance.setLoaders(getMockLoaders(1));
        instance.run();

        LoadCompletedEvent completedEvent = eventPublisher.getPublishedEventsOfType(LoadCompletedEvent.class).get(0);
        LoadContext context = completedEvent.getLoadContext();
        Assert.assertEquals(instance, context.getPipeline());
        Assert.assertNotNull(context.getId());
        Assert.assertTrue(context.getDuration() >= 0);
    }

    @Test
    public void testRunLoadContextWarnings() {
        MockApplicationEventPublisher eventPublisher = new MockApplicationEventPublisher();
        ETLPipelineImpl instance = new ETLPipelineImpl(id, eventPublisher, repository);

        Loader loader = new Loader() {
            @Override
            public void load(Repository repository, URI graph, LoadContext context) throws LoadException {
                context.getWarnings().add("Test Warning 1");
                context.getWarnings().add("Test Warning 2");
            }
        };
        instance.setLoaders(Arrays.asList(loader));
        instance.run();

        LoadCompletedEvent completedEvent = eventPublisher.getPublishedEventsOfType(LoadCompletedEvent.class).get(0);
        LoadContext context = completedEvent.getLoadContext();
        Assert.assertEquals(2, context.getWarnings().size());
        Assert.assertEquals("Test Warning 1", context.getWarnings().get(0));
        Assert.assertEquals("Test Warning 2", context.getWarnings().get(1));
    }

    @Test
    public void testRunLoadContextCustomData() {
        MockApplicationEventPublisher eventPublisher = new MockApplicationEventPublisher();
        ETLPipelineImpl instance = new ETLPipelineImpl(id, eventPublisher, repository);

        Loader loader = new Loader() {
            @Override
            public void load(Repository repository, URI graph, LoadContext context) throws LoadException {
                context.getCustomData().put("key", "Test");
            }
        };
        instance.setLoaders(Arrays.asList(loader));
        instance.run();

        LoadCompletedEvent completedEvent = eventPublisher.getPublishedEventsOfType(LoadCompletedEvent.class).get(0);
        LoadContext context = completedEvent.getLoadContext();
        Assert.assertEquals("Test", context.getCustomData().get("key"));
    }

    @Test
    public void testRunLoadCompletedEventFired() {
        MockApplicationEventPublisher eventPublisher = new MockApplicationEventPublisher();
        ETLPipelineImpl instance = new ETLPipelineImpl(id, eventPublisher, repository);

        Loader loader = new MockLoader();
        instance.setLoaders(Arrays.asList(loader));
        instance.run();

        LoadCompletedEvent completedEvent = eventPublisher.getPublishedEventsOfType(LoadCompletedEvent.class).get(0);
        Assert.assertEquals(loader, completedEvent.getLoader());
    }

    @Test
    public void testRunLoadFailedEventFired() {
        MockApplicationEventPublisher eventPublisher = new MockApplicationEventPublisher();
        ETLPipelineImpl instance = new ETLPipelineImpl(id, eventPublisher, repository);

        Loader loader = new MockLoader() {
            @Override
            public void load(Repository repository, URI graph, LoadContext context) throws LoadException {
                throw new LoadException("Test");
            }
        };
        instance.setLoaders(Arrays.asList(loader));
        instance.run();

        LoadFailedEvent failedEvent = eventPublisher.getPublishedEventsOfType(LoadFailedEvent.class).get(0);
        Assert.assertEquals(loader, failedEvent.getLoader());
        Assert.assertNotNull(failedEvent.getException());
    }

    @Test
    public void testRunLoaderAbortedEventFired() {
        MockApplicationEventPublisher eventPublisher = new MockApplicationEventPublisher();
        ETLPipelineImpl instance = new ETLPipelineImpl(id, eventPublisher, repository);

        Loader loader = new MockLoader() {
            @Override
            public void load(Repository repository, URI graph, LoadContext context) throws LoadException {
                context.cancelPipeline("Test");
            }
        };
        instance.setLoaders(Arrays.asList(loader));
        instance.run();

        PipelineAbortedEvent abortedEvent = eventPublisher.getPublishedEventsOfType(PipelineAbortedEvent.class).get(0);
        Assert.assertEquals(instance, abortedEvent.getPipeline());
        Assert.assertEquals("Test", abortedEvent.getMessage());
    }

    @Test
    public void testRunSkipDisabledExtractor() {
        ETLPipelineImpl instance = new ETLPipelineImpl(id, new MockApplicationEventPublisher(), repository);

        instance.setExtractors(getMockExtractors(2));
        MockExtractor disabledExtractor = (MockExtractor) instance.getExtractors().get(1);
        disabledExtractor.setDisabled(true);
        instance.run();

        assertTrue(((MockExtractor) instance.getExtractors().get(0)).executed);
        assertFalse(disabledExtractor.executed);
    }

    @Test
    public void testRunSkipDisabledTransformer() {
        ETLPipelineImpl instance = new ETLPipelineImpl(id, new MockApplicationEventPublisher(), repository);

        instance.setTransformers(getMockTransformers(2));
        MockTransformer disabledTransformer = (MockTransformer) instance.getTransformers().get(1);
        disabledTransformer.setDisabled(true);
        instance.run();

        assertTrue(((MockTransformer) instance.getTransformers().get(0)).executed);
        assertFalse(disabledTransformer.executed);
    }

    @Test
    public void testRunSkipDisabledLoader() {
        ETLPipelineImpl instance = new ETLPipelineImpl(id, new MockApplicationEventPublisher(), repository);

        instance.setLoaders(getMockLoaders(2));
        MockLoader disabledLoader = (MockLoader) instance.getLoaders().get(1);
        disabledLoader.setDisabled(true);
        instance.run();

        assertTrue(((MockLoader) instance.getLoaders().get(0)).executed);
        assertFalse(disabledLoader.executed);
    }

    @Test
    public void testSetApplicationEventPublisher() {
        ApplicationEventPublisher publisher = new MockApplicationEventPublisher();
        ETLPipelineImpl instance = new ETLPipelineImpl();
        instance.setApplicationEventPublisher(publisher);

        ApplicationEventPublisher result = instance.getEventPublisher();
        assertEquals(publisher, result);
    }

    private List<Extractor> getMockExtractors(int i) {
        List<Extractor> extractors = new ArrayList<Extractor>(i);
        for (int j = 0; j < i; j++) {
            extractors.add(new MockExtractor());
        }
        return extractors;
    }

    private List<Transformer> getMockTransformers(int i) {
        List<Transformer> transformers = new ArrayList<Transformer>(i);
        for (int j = 0; j < i; j++) {
            transformers.add(new MockTransformer());
        }
        return transformers;
    }

    private List<Loader> getMockLoaders(int i) {
        List<Loader> loaders = new ArrayList<Loader>(i);
        for (int j = 0; j < i; j++) {
            loaders.add(new MockLoader());
        }
        return loaders;
    }

    abstract class DisableableModule implements Disableable {

        protected boolean disabled;

        @Override
        public void setDisabled(boolean disabled) {
            this.disabled = disabled;
        }

        @Override
        public boolean isDisabled() {
            return disabled;
        }
    }

    class MockExtractor extends DisableableModule implements Extractor {

        boolean executed;

        @Override
        public void extract(RDFHandler handler, ExtractContext context) throws ExtractException {
            executed = true;
        }
    }

    class MockTransformer extends DisableableModule implements Transformer {

        boolean executed;

        @Override
        public void transform(Repository repository, URI graph, TransformContext context) throws TransformException {
            executed = true;
        }
    }

    class MockLoader extends DisableableModule implements Loader {

        boolean executed;

        @Override
        public void load(Repository repository, URI graph, LoadContext context) throws LoadException {
            executed = true;
        }
    }

    class MockApplicationEventPublisher implements ApplicationEventPublisher {

        List<ETLEvent> publishedEvents = new ArrayList<ETLEvent>();

        @Override
        public void publishEvent(ApplicationEvent ae) {
            if (ae instanceof ETLEvent) {
                publishedEvents.add((ETLEvent) ae);
            }
        }

        public boolean eventTypeWasPublished(Class<? extends ETLEvent> eventType) {
            for (ETLEvent event : publishedEvents) {
                if (eventType.equals(event.getClass())) {
                    return true;
                }
            }
            return false;
        }

        public <T extends ETLEvent> List<T> getPublishedEventsOfType(Class<T> eventType) {
            List<T> events = new ArrayList<T>();
            for (ETLEvent event : publishedEvents) {
                if (eventType.equals(event.getClass())) {
                    events.add((T) event);
                }
            }
            return events;
        }
    }
}
