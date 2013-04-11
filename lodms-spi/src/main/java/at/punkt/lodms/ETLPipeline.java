package at.punkt.lodms;

import at.punkt.lodms.spi.extract.Extractor;
import at.punkt.lodms.spi.load.Loader;
import at.punkt.lodms.spi.transform.Transformer;
import java.util.List;
import org.openrdf.model.URI;
import org.openrdf.repository.Repository;

/**
 * Represents a fixed workflow composed of one or several {@link Extractor}s,
 * {@link Transformer}s and {@link Loader}s executed in a fixed order.<br/>
 * Implementations of this class are supposed to execute the components in the following order:
 * <ol>
 * <li>Execute all {@link Extractor}s in the order of the {@link List}</li>
 * <li>Execute all {@link Transformer}s in the order of the {@link List}</li>
 * <li>Execute all {@link Loader}s in the order of the {@link List}</li>
 * </ol>
 *
 * @author Alex Kreiser (akreiser@gmail.com)
 */
public interface ETLPipeline extends Runnable {

    /**
     * Returns the identifier of this pipeline, will be used as named graph
     * where all the RDF data of this pipeline is cached.
     *
     * @return
     */
    public String getId();

    /**
     * Sets the identifier of this pipeline, will be used as named graph
     * where all the RDF data of this pipeline is cached.<br/>
     * <strong>The ID has to be unique for all pipelines that share an {@link Repository}</strong>
     *
     * @param id
     */
    public void setId(String id);

    /**
     * Returns the list of extractors defined for this pipeline
     *
     * @return
     */
    public List<Extractor> getExtractors();

    /**
     * Sets the list of extractors defined for this pipeline
     *
     * @param extractors
     */
    public void setExtractors(List<Extractor> extractors);

    /**
     * Returns the list of loaders defined for this pipeline
     *
     * @return
     */
    public List<Loader> getLoaders();

    /**
     * Sets the list of loaders defined for this pipeline
     *
     * @param loaders
     */
    public void setLoaders(List<Loader> loaders);

    /**
     * Returns the list of transformers defined for this pipeline
     *
     * @return
     */
    public List<Transformer> getTransformers();

    /**
     * Sets the list of extractors defined for this pipeline
     *
     * @param transformers
     */
    public void setTransformers(List<Transformer> transformers);

    /**
     * Returns the repository instance that will be used by the pipeline for caching RDF data.
     *
     * @return
     */
    public Repository getRepository();

    /**
     * Sets the repository instance that will be used by the pipeline for caching RDF data.<br/>
     * <strong>Has to be capable to store named graphs!</strong>
     *
     * @param repository
     */
    public void setRepository(Repository repository);
}
