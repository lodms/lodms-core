/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package at.punkt.lodms.base;

import at.punkt.lodms.integration.ConfigurableBase;
import at.punkt.lodms.integration.UIComponent;
import at.punkt.lodms.spi.extract.Extractor;
import java.io.Serializable;

/**
 *
 * @author kreisera
 */
public abstract class ExtractorBase<T extends Serializable> extends ConfigurableBase<T> implements UIComponent, Extractor {

}
