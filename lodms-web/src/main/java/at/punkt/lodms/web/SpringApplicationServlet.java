/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package at.punkt.lodms.web;

import com.vaadin.Application;
import com.vaadin.terminal.gwt.server.AbstractApplicationServlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

/**
 *
 * @author Alex Kreiser
 */
public class SpringApplicationServlet extends AbstractApplicationServlet {

    private WebApplicationContext applicationContext;
    private Class<? extends Application> applicationClass;
    private String applicationBean = "lodmsApp";

    @Override
    public void init(ServletConfig servletConfig) throws ServletException {
        super.init(servletConfig);
        applicationContext = WebApplicationContextUtils.getWebApplicationContext(servletConfig.getServletContext());
//        applicationBean = servletConfig.getInitParameter("applicationName");
//        System.out.println(applicationBean);
        applicationClass = (Class<? extends Application>) applicationContext.getType(applicationBean);
    }

    @Override
    protected Class<? extends Application> getApplicationClass() throws
            ClassNotFoundException {
        return applicationClass;
    }

    @Override
    protected Application getNewApplication(HttpServletRequest request) {
        return (Application) applicationContext.getBean(applicationBean);
    }
}