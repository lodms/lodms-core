/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package at.punkt.lodms.spi.extract;

import org.openrdf.rio.RDFHandler;

/**
 * Implementations of this SPI are responsible for extracting data from a
 * data source and converting the data to RDF. <br/>
 * RDF data is produced through the {@link RDFHandler} interface of the openRDF Sesame API.
 *
 * @author Alex Kreiser (akreiser@gmail.com)
 */
public interface Extractor {

    /**
     * Extracts data from a data source and converts it to RDF.<br/>
     *
     * @param handler This handler has to be used to store the produced RDF statements.<br/>
     * @param context Context for one extraction cycle containing meta information about the extraction.
     * @throws ExtractException If any error occurs troughout the extraction cycle.
     */
    public void extract(RDFHandler handler, ExtractContext context) throws ExtractException;

}