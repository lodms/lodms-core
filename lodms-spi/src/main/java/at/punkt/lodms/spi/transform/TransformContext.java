package at.punkt.lodms.spi.transform;

import at.punkt.lodms.ProcessingContext;
import java.util.Map;

/**
 * Context used by {@link Transformer}s for the transformation process.
 *
 * @see Transformer
 * @author Alex Kreiser (akreiser@gmail.com)
 */
public class TransformContext extends ProcessingContext {

    public TransformContext(String id, Map<String, Object> customData) {
        super(id, customData);
    }
}