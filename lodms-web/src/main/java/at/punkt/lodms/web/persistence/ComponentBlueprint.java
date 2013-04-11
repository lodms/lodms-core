/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package at.punkt.lodms.web.persistence;

import java.io.Serializable;

/**
 *
 * @author Alex Kreiser
 */
public class ComponentBlueprint<T> implements Serializable {
    
    private Class<? extends T> type;
    private Serializable config;
    private boolean disabled;

    public ComponentBlueprint(Class<? extends T> type, Serializable config) {
        this.type = type;
        this.config = config;
    }
    
    public Serializable getConfig() {
        return config;
    }

    public Class<? extends T> getType() {
        return type;
    }

    public boolean isDisabled() {
        return disabled;
    }

    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }
}
