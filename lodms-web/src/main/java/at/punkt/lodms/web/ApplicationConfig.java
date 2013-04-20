/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package at.punkt.lodms.web;

import at.punkt.lodms.web.view.ApplicationConfigDialog;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import org.apache.log4j.Logger;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 *
 * @author Alex Kreiser (akreiser@gmail.com)
 */
@Component
@Scope("singleton")
public class ApplicationConfig {

    private final Logger logger = Logger.getLogger(ApplicationConfig.class);
    private Properties properties = new Properties();

    @PostConstruct
    public void load() throws IOException {
        logger.info("Loading properties..");
        properties.load(LodmsApplication.class.getResourceAsStream("/application.properties"));
        logger.info(properties);
    }
    
    @PreDestroy
    public void save() throws FileNotFoundException, IOException {
        logger.info("Persisting properties..");
        properties.store(new FileOutputStream(ApplicationConfig.class.getResource("/application.properties").getFile()), "");
    }
    
    public void set(String property, String value) {
        properties.put(property, value);
    }
    
    public String get(String property) {
        return (String) properties.get(property);
    }
}
