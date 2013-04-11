package at.punkt.lodms.spi.extract;

import at.punkt.lodms.ProcessingContext;
import java.util.Map;

/**
 * Context used by {@link Extractor}s for the extraction process.
 *
 * @see Extractor
 * @author Alex Kreiser (akreiser@gmail.com)
 */
public class ExtractContext extends ProcessingContext {

    private long triplesExtracted;

    public ExtractContext(String id, Map<String, Object> customData) {
        super(id, customData);
    }

    /**
     * Returns the number of triples that were extracted or produced.
     *
     * @return
     */
    public long getTriplesExtracted() {
        return triplesExtracted;
    }

    /**
     * Sets the number of triples that were extracted or produced.
     *
     * @param triplesExtracted
     */
    public void setTriplesExtracted(long triplesExtracted) {
        this.triplesExtracted = triplesExtracted;
    }
}
