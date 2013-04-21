/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package at.punkt.lodms.base;

import at.punkt.lodms.integration.ConfigurableBase;
import at.punkt.lodms.integration.UIComponent;
import at.punkt.lodms.spi.load.Loader;

/**
 *
 * @author kreisera
 */
public abstract class LoaderBase<T> extends ConfigurableBase<T> implements UIComponent, Loader {

}
