/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package at.punkt.lodms.web.event;

import at.punkt.lodms.spi.load.LoadCompletedEvent;
import at.punkt.lodms.spi.load.LoadEvent;
import at.punkt.lodms.spi.load.LoadFailedEvent;
import org.apache.log4j.Logger;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

/**
 *
 * @author kreisera
 */
@Component
public class Log4jLoadListener implements ApplicationListener<LoadEvent> {

    final Logger logger = Logger.getLogger(Log4jLoadListener.class);

    @Override
    public void onApplicationEvent(LoadEvent e) {
        if (e instanceof LoadFailedEvent) {
            logger.error("Loading failed", ((LoadFailedEvent)e).getException());
        } else if (e instanceof LoadCompletedEvent) {
            logger.info("Loading completed successfully for "+e.getLoader().getClass().getSimpleName()+" in "+((LoadCompletedEvent)e).getLoadContext().getDuration()+" ms");
        }
    }
}