/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package at.punkt.lodms.web.persistence;

/**
 * @author Alex Kreiser
 */
public class ComponentBlueprint<T> {

  private Class<? extends T> type;
  private Object config;
  private boolean disabled;

  public ComponentBlueprint() {

  }

  public ComponentBlueprint(Class<? extends T> type, Object config) {
    this.type = type;
    this.config = config;
  }

  public void setConfig(Object config) {
    this.config = config;
  }

  public Object getConfig() {
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
