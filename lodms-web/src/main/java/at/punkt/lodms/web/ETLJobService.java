/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package at.punkt.lodms.web;

import at.punkt.lodms.Disableable;
import at.punkt.lodms.ETLPipeline;
import at.punkt.lodms.impl.ETLPipelineImpl;
import at.punkt.lodms.integration.Configurable;
import at.punkt.lodms.spi.extract.Extractor;
import at.punkt.lodms.spi.load.Loader;
import at.punkt.lodms.spi.transform.Transformer;
import at.punkt.lodms.web.persistence.ComponentBlueprint;
import at.punkt.lodms.web.persistence.ETLJobBlueprint;
import com.thoughtworks.xstream.XStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import org.apache.log4j.Logger;
import org.openrdf.repository.Repository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.support.CronTrigger;

/**
 *
 * @author Alex Kreiser
 */
public class ETLJobService {

    private File persistPath = new File(".");
    private Set<ETLJob> jobs = new HashSet<ETLJob>();
    @Autowired(required = true)
    private TaskScheduler scheduler;
    @Autowired
    private ApplicationContext context;
    @Autowired
    private Repository repository;
    private String PERSIST_FILE = "etl_jobs.xml";
    private final Logger logger = Logger.getLogger(ETLJobService.class);
    private final XStream xstream = new XStream();

    @PostConstruct
    public void loadJobs() {
        try {
            logger.info("Loading persisted jobs..");
            List<ETLJobBlueprint> blueprints = (List<ETLJobBlueprint>) xstream.fromXML(new FileInputStream(persistPath.getAbsolutePath() + File.separator + PERSIST_FILE));

            for (ETLJobBlueprint blueprint : blueprints) {
                ETLJob job = new ETLJob(blueprint.getId());
                job.setMetadata(blueprint.getMetadata());
                ETLPipeline pipeline = new ETLPipelineImpl(blueprint.getPipelineId(), context, repository);
                job.setPipeline(pipeline);
                for (ComponentBlueprint<Extractor> comp : blueprint.getExtractors()) {
                    Extractor extractor = comp.getType().newInstance();
                    pipeline.getExtractors().add(extractor);
                    if (extractor instanceof Configurable) {
                        Configurable configurable = (Configurable) extractor;
                        configurable.configure(comp.getConfig());
                    }
                    if (comp.isDisabled()) {
                        ((Disableable)extractor).setDisabled(true);
                    }
                }
                for (ComponentBlueprint<Transformer> comp : blueprint.getTransformers()) {
                    Transformer transformer = comp.getType().newInstance();
                    pipeline.getTransformers().add(transformer);
                    if (transformer instanceof Configurable) {
                        Configurable configurable = (Configurable) transformer;
                        configurable.configure(comp.getConfig());
                    }
                    if (comp.isDisabled()) {
                        ((Disableable)transformer).setDisabled(true);
                    }
                }
                for (ComponentBlueprint<Loader> comp : blueprint.getLoaders()) {
                    Loader loader = comp.getType().newInstance();
                    pipeline.getLoaders().add(loader);
                    if (loader instanceof Configurable) {
                        Configurable configurable = (Configurable) loader;
                        configurable.configure(comp.getConfig());
                    }
                    if (comp.isDisabled()) {
                        ((Disableable)loader).setDisabled(true);
                    }
                }
                logger.debug("Reconfigured job [" + blueprint.getId() + "]");
                if (job.getMetadata().isScheduled()) {
                    job.setFuture(scheduler.schedule(pipeline, new CronTrigger(job.getMetadata().getInterval())));
                    logger.debug("Scheduled job [" + blueprint.getId() + "] with cron interval [" + job.getMetadata().getInterval() + "]");
                }
                jobs.add(job);
            }
            logger.info("Loaded and rescheduled [" + blueprints.size() + "] persisted jobs.");
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        }
    }

    @PreDestroy
    public void persistJobs() throws Exception {
        try {
            logger.info("Persisting [" + jobs.size() + "] jobs to filesystem");
            ArrayList<ETLJobBlueprint> blueprints = new ArrayList<ETLJobBlueprint>();
            for (ETLJob job : jobs) {
                ETLJobBlueprint blueprint = new ETLJobBlueprint(job.getId());
                blueprint.setMetadata(job.getMetadata());
                ETLPipeline pipeline = job.getPipeline();
                blueprint.setPipelineId(pipeline.getId());
                for (Extractor extractor : pipeline.getExtractors()) {
                    Object config = null;
                    if (extractor instanceof Configurable) {
                        config = ((Configurable) extractor).getConfig();
                    }
                    ComponentBlueprint<Extractor> comp = new ComponentBlueprint<Extractor>(extractor.getClass(), config);
                    if (extractor instanceof Disableable) {
                        comp.setDisabled(((Disableable)extractor).isDisabled());
                    }
                    blueprint.getExtractors().add(comp);
                }
                for (Transformer transformer : pipeline.getTransformers()) {
                    Object config = null;
                    if (transformer instanceof Configurable) {
                        config = ((Configurable) transformer).getConfig();
                    }
                    ComponentBlueprint<Transformer> comp = new ComponentBlueprint<Transformer>(transformer.getClass(), config);
                    if (transformer instanceof Disableable) {
                        comp.setDisabled(((Disableable)transformer).isDisabled());
                    }
                    blueprint.getTransformers().add(comp);
                }
                for (Loader loader : pipeline.getLoaders()) {
                    Object config = null;
                    if (loader instanceof Configurable) {
                        config = ((Configurable) loader).getConfig();
                    }
                    ComponentBlueprint<Loader> comp = new ComponentBlueprint<Loader>(loader.getClass(), config);
                    if (loader instanceof Disableable) {
                        comp.setDisabled(((Disableable)loader).isDisabled());
                    }
                    blueprint.getLoaders().add(comp);
                }
                blueprints.add(blueprint);
            }
            xstream.toXML(blueprints, new FileOutputStream(persistPath.getAbsolutePath() + File.separator + PERSIST_FILE));
        } catch (Exception ex) {
            logger.error("Unable to persist jobs", ex);
        }
    }

    public File getPersistPath() {
        return persistPath;
    }

    public void setPersistPath(File persistPath) {
        this.persistPath = persistPath;
    }

    public Set<ETLJob> getJobs() {
        return jobs;
    }

    public void addJob(ETLJob job) {
        jobs.add(job);
        try {
            persistJobs();
        } catch (Exception ex) {
            logger.error("Unable to persist jobs after addJob", ex);
        }
    }

    public void removeJob(ETLJob job) {
        jobs.remove(job);
        try {
            persistJobs();
        } catch (Exception ex) {
            logger.error("Unable to persist jobs after removeJob", ex);
        }
    }
}
