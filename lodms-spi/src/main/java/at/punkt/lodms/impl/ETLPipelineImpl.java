package at.punkt.lodms.impl;

import at.punkt.lodms.*;
import at.punkt.lodms.spi.transform.TransformCompletedEvent;
import at.punkt.lodms.spi.load.Loader;
import at.punkt.lodms.spi.load.LoadException;
import at.punkt.lodms.spi.transform.Transformer;
import at.punkt.lodms.spi.transform.TransformException;
import at.punkt.lodms.spi.extract.ExtractCompletedEvent;
import at.punkt.lodms.spi.extract.Extractor;
import at.punkt.lodms.spi.extract.ExtractException;
import at.punkt.lodms.spi.transform.TransformFailedEvent;
import at.punkt.lodms.spi.extract.ExtractFailedEvent;
import at.punkt.lodms.spi.load.LoadFailedEvent;
import at.punkt.lodms.spi.extract.ExtractContext;
import at.punkt.lodms.spi.load.LoadCompletedEvent;
import at.punkt.lodms.spi.load.LoadContext;
import at.punkt.lodms.spi.transform.TransformContext;
import at.punkt.lodms.util.NoStartEndWrapper;
import at.punkt.lodms.util.TripleCountingWrapper;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.apache.log4j.Logger;
import org.openrdf.model.URI;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.util.RDFInserter;
import org.openrdf.rio.RDFHandlerException;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;

/**
 * Represents a fixed workflow composed of one or several {@link Extractor}s,
 * {@link Transformer}s and {@link Loader}s executed in a fixed order.
 *
 * Processing will always take place in the following order: 1. Execute all
 * {@link Extractor}s in the order of the {@link List} If an Extractor throws an
 * error publish an {@link ExtractFailedEvent} - otherwise publish an
 * {@link ExtractCompletedEvent}. If an Extractor requests cancellation of the
 * pipeline through {@link ProcessingContext#cancelPipeline} publish a
 * {@link PipelineAbortedEvent} and exit.
 *
 * 2. Execute all {@link Transformer}s in the order of the {@link List} If a
 * Transformer throws an error publish an {@link TransformFailedEvent} -
 * otherwise publish an {@link TransformCompletedEvent}. If a Transformer
 * requests cancellation of the pipeline through
 * {@link ProcessingContext#cancelPipeline} publish a
 * {@link PipelineAbortedEvent} and exit.
 *
 * 3. Execute all {@link Loader}s in the order of the {@link List} If a Loader
 * throws an error publish an {@link LoadFailedEvent} - otherwise publish an
 * {@link LoadCompletedEvent}. If a Loader requests cancellation of the pipeline
 * through {@link ProcessingContext#cancelPipeline} publish a
 * {@link PipelineAbortedEvent} and exit.
 *
 * 4. Publish a {@link PipelineCompletedEvent}
 *
 * A Spring {@link ApplicationEventPublisher} is required for propagation of
 * important events occurring thoughout the pipeline. Also, a {@link Repository}
 * instance capable of storing named graphs is essential for this pipeline to
 * work. All extracted RDF data will be stored in a dedicated graph in the
 * repository and accessed / manipulated by the {@link Transformer}s before it
 * is exported by the {@link Loader}s.
 *
 * @see Extractor
 * @see Transformer
 * @see Loader
 * @author Alex Kreiser (akreiser@gmail.com)
 */
public class ETLPipelineImpl implements ETLPipeline, ApplicationEventPublisherAware {

    protected List<Extractor> extractors = new ArrayList<Extractor>();
    protected List<Transformer> transformers = new ArrayList<Transformer>();
    protected List<Loader> loaders = new ArrayList<Loader>();
    protected String id;
    protected ApplicationEventPublisher eventPublisher;
    protected Repository repository;
    protected static final Logger logger = Logger.getLogger(ETLPipelineImpl.class);
    protected boolean cancelAllowed = true;

    /**
     * Constructor
     *
     * @param id The identifier of this pipeline, will be used as named graph
     * where all the RDF data of this pipeline will be cached
     * @param eventPublisher Publisher for {@link ETLEvent}s
     * @param repository Repository functioning as RDF cache
     */
    public ETLPipelineImpl(String id, ApplicationEventPublisher eventPublisher, Repository repository) {
        this.id = id;
        this.eventPublisher = eventPublisher;
        this.repository = repository;
    }

    public ETLPipelineImpl() {
    }

    @Override
    public void run() {
        long pipelineStart = System.currentTimeMillis();
        String runId = generateRunId();
        final URI namedGraph = new URIImpl(id);
        try {
            final Map<String, Object> customData = new HashMap<String, Object>();
            eventPublisher.publishEvent(new PipelineStartedEvent(this, runId, this));

            runExtractors(runId, namedGraph, customData);
            runTransformers(runId, namedGraph, customData);
            runLoaders(runId, namedGraph, customData);
        } catch (Exception ex) {
            logger.error("An error occurred executing the pipeline", ex);
        } finally {
            clearGraph(namedGraph);
            eventPublisher.publishEvent(new PipelineCompletedEvent((System.currentTimeMillis() - pipelineStart), this, runId, this));
        }
    }

    private String generateRunId() {
        return UUID.randomUUID().toString();
    }

    private void runExtractors(String runId, URI namedGraph, Map<String, Object> customData) throws RepositoryException, RDFHandlerException {
        RepositoryConnection con = repository.getConnection();
        con.setAutoCommit(false);
        RDFInserter inserter = new RDFInserter(con);
        inserter.enforceContext(namedGraph);
        NoStartEndWrapper wrapper = new NoStartEndWrapper(inserter);
        try {
            for (Extractor extractor : extractors) {
                if (extractor instanceof Disableable && ((Disableable) extractor).isDisabled()) {
                    continue;
                }
                ExtractContext context = new ExtractContext(runId, customData);
                context.setPipeline(this);
                TripleCountingWrapper tripleCounter = new TripleCountingWrapper(wrapper);
                try {
                    long start = System.currentTimeMillis();
                    extractor.extract(tripleCounter, context);
                    con.commit();
                    context.setDuration(System.currentTimeMillis() - start);
                    context.setTriplesExtracted(tripleCounter.getTriples());
                    eventPublisher.publishEvent(new ExtractCompletedEvent(extractor, context, this));
                } catch (ExtractException ex) {
                    con.rollback();
                    eventPublisher.publishEvent(new ExtractFailedEvent(ex, extractor, context, this));
                }
                if (cancelAllowed && context.isCancelPipeline()) {
                    eventPublisher.publishEvent(new PipelineAbortedEvent(context.getCancelMessage(), this, runId, extractor));
                    return;
                }
            }
            inserter.endRDF();
        } finally {
            con.commit();
            con.close();
        }
    }

    private void runTransformers(String runId, URI pipelineId, Map<String, Object> customData) {
        for (Transformer transformer : transformers) {
            if (transformer instanceof Disableable && ((Disableable) transformer).isDisabled()) {
                continue;
            }
            TransformContext context = new TransformContext(runId, customData);
            context.setPipeline(this);
            try {
                long start = System.currentTimeMillis();
                transformer.transform(repository, pipelineId, context);
                context.setDuration(System.currentTimeMillis() - start);
                eventPublisher.publishEvent(new TransformCompletedEvent(transformer, context, this));
            } catch (TransformException ex) {
                eventPublisher.publishEvent(new TransformFailedEvent(ex, transformer, context, this));
            }
            if (cancelAllowed && context.isCancelPipeline()) {
                eventPublisher.publishEvent(new PipelineAbortedEvent(context.getCancelMessage(), this, runId, transformer));
                return;
            }
        }
    }

    private void runLoaders(String runId, URI pipelineId, Map<String, Object> customData) {
        for (Loader loader : loaders) {
            if (loader instanceof Disableable && ((Disableable) loader).isDisabled()) {
                continue;
            }
            LoadContext context = new LoadContext(runId, customData);
            context.setPipeline(this);
            try {
                long start = System.currentTimeMillis();
                loader.load(repository, pipelineId, context);
                context.setDuration(System.currentTimeMillis() - start);
                eventPublisher.publishEvent(new LoadCompletedEvent(loader, context, this));
            } catch (LoadException ex) {
                eventPublisher.publishEvent(new LoadFailedEvent(ex, loader, context, this));
            }
            if (cancelAllowed && context.isCancelPipeline()) {
                eventPublisher.publishEvent(new PipelineAbortedEvent(context.getCancelMessage(), this, runId, loader));
                return;
            }
        }
    }

    private void clearGraph(URI pipelineId) {
        try {
            RepositoryConnection con = repository.getConnection();
            try {
                con.clear(pipelineId);
                con.commit();
            } finally {
                con.close();
            }
        } catch (Exception ex) {
            logger.fatal("Unable to clean graph [" + id + "]", ex);
        }
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }

    @Override
    public Repository getRepository() {
        return repository;
    }

    @Override
    public void setRepository(Repository repository) {
        this.repository = repository;
    }

    public ApplicationEventPublisher getEventPublisher() {
        return eventPublisher;
    }

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    @Override
    public List<Extractor> getExtractors() {
        return extractors;
    }

    @Override
    public void setExtractors(List<Extractor> extractors) {
        this.extractors = extractors;
    }

    @Override
    public List<Loader> getLoaders() {
        return loaders;
    }

    @Override
    public void setLoaders(List<Loader> loaders) {
        this.loaders = loaders;
    }

    @Override
    public List<Transformer> getTransformers() {
        return transformers;
    }

    @Override
    public void setTransformers(List<Transformer> transformers) {
        this.transformers = transformers;
    }

    /**
     * Returns if single components are allowed to cancel the entire pipeline
     * using {@link ProcessingContext#cancelPipeline(java.lang.String)}.
     *
     * @return
     */
    public boolean isCancelAllowed() {
        return cancelAllowed;
    }

    /**
     * Sets if single components are allowed to cancel the entire pipeline using
     * {@link ProcessingContext#cancelPipeline(java.lang.String)}.
     *
     * @param cancelAllowed
     */
    public void setCancelAllowed(boolean cancelAllowed) {
        this.cancelAllowed = cancelAllowed;
    }
}