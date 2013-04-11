package at.punkt.lodms.spi.transform;

import at.punkt.lodms.spi.extract.Extractor;
import org.openrdf.model.URI;
import org.openrdf.repository.Repository;

/**
 * Implementations of this SPI are providing routines to clean or enrich RDF data
 * that was previously produced by {@link Extractor}s.<br/>
 * Transformations may include e.g.
 * <ul>
 * <li>Cleaning data</li>
 * <li>Converting to another RDF schema</li>
 * <li>Linking resources to external datasets</li>
 * </ul>
 *
 * @author Alex Kreiser (akreiser@gmail.com)
 */
public interface Transformer {

    /**
     * Transforms the cached RDF data in the repository.
     *
     * @param repository The repository where the RDF data is cached that should be transformed
     * @param graph The graph that contains the RDF data which was extracted
     * @param context The context containing meta information about this transformation process
     * @throws TransformException If the transformation fails, this exception has to be thrown
     */
    public void transform(Repository repository, URI graph, TransformContext context) throws TransformException;

}
