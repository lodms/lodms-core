package at.punkt.lodms.util;

import org.openrdf.model.Statement;
import org.openrdf.rio.RDFHandler;
import org.openrdf.rio.RDFHandlerException;
import org.openrdf.rio.helpers.RDFHandlerWrapper;

/**
 * Counts the number of triples that were handled.
 *
 * @author Alex Kreiser (akreiser@gmail.com)
 */
public class TripleCountingWrapper extends RDFHandlerWrapper {

    private long triples = 0;

    public TripleCountingWrapper(RDFHandler rdfHandler) {
        super(rdfHandler);
    }

    @Override
    public void handleStatement(Statement st) throws RDFHandlerException {
        super.handleStatement(st);
        triples++;
    }

    public long getTriples() {
        return triples;
    }
}
