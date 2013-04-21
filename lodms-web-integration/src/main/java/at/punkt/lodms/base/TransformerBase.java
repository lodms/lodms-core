/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package at.punkt.lodms.base;

import at.punkt.lodms.integration.ConfigurableBase;
import at.punkt.lodms.integration.UIComponent;
import at.punkt.lodms.spi.transform.Transformer;

/**
 *
 * @author kreisera
 */
public abstract class TransformerBase<T> extends ConfigurableBase<T> implements Transformer, UIComponent {

}
