/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package at.punkt.lodms.web.view;

/**
 *
 * @author Alex Kreiser
 */
public interface View {
    
    /**
     * Executed before a view is made visible and put in the main layout.
     * 
     */
    public void preView();
    
    /**
     * Executed after this view was made invisible and switched for another view.
     * 
     */
    public void postView();
}
