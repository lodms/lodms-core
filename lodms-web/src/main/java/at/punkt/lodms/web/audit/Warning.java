/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package at.punkt.lodms.web.audit;

/**
 *
 * @author Alex Kreiser (akreiser@gmail.com)
 */
public class Warning {
    
    private String warning;

    public Warning(String warning) {
        this.warning = warning;
    }
    
    public String getWarning() {
        return warning;
    }

    public void setWarning(String warning) {
        this.warning = warning;
    }
}
