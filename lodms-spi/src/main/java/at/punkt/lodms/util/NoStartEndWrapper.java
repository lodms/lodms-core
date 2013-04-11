package at.punkt.lodms.util;

import org.openrdf.rio.RDFHandler;
import org.openrdf.rio.RDFHandlerException;
import org.openrdf.rio.helpers.RDFHandlerWrapper;

/**
 * Prevents {@link #startRDF()} and {@link #endRDF()} from being called in the wrapped handler.
 *
 * @author Alex Kreiser (akreiser@gmail.com)
 */
public class NoStartEndWrapper extends RDFHandlerWrapper {

    public NoStartEndWrapper(RDFHandler rdfHandler) {
        super(rdfHandler);
    }

    @Override
    public void endRDF() throws RDFHandlerException {

    }

    @Override
    public void startRDF() throws RDFHandlerException {

    }
}