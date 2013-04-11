package at.punkt.lodms.spi.load;

import at.punkt.lodms.ProcessingContext;
import java.util.Map;

/**
 * Context used by {@link Loader}s for the loading process.
 *
 * @see Loader
 * @author Alex Kreiser (akreiser@gmail.com)
 */
public class LoadContext extends ProcessingContext {

    public LoadContext(String id, Map<String, Object> customData) {
        super(id, customData);
    }
}
