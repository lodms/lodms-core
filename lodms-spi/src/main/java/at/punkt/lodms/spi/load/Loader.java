package at.punkt.lodms.spi.load;

import org.openrdf.model.URI;
import org.openrdf.repository.Repository;

/**
 * Implementations of this SPI are responsible for exporting or loading
 * the RDF data that was extracted and transformed previously.<br/>
 * Loading may include
 * <ul>
 * <li>Importing the data into a remote triple store</li>
 * <li>Storing the data in a local file</li>
 * <li>Calling a web service</li>
 * </ul>
 * Optionally a {@link Loader} can convert the data into a non-RDF format
 * before loading it if need be.
 *
 * @author Alex Kreiser (akreiser@gmail.com)
 */
public interface Loader {

    /**
     * Loads the RDF data of an completed extract and transform cycle to a data sink.<br/>
     * For convenience and flexibility reasons the repository and the graph is provided,
     * so that loaders can query only parts of the RDF data.<br/>
     * <strong>Loaders may only read the RDF data and must not transform it!</strong>
     *
     * @param repository The repository where the RDF data is cached that should be loaded
     * @param graph The graph that contains the RDF data of one perticular ETL cycle
     * @param context The context containing meta information about this load process
     * @throws LoadException If loading fails, this exception has to be thrown
     */
    public void load(Repository repository, URI graph, LoadContext context) throws LoadException;

}