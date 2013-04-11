/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package at.punkt.lodms.web.event;

import at.punkt.lodms.spi.extract.ExtractCompletedEvent;
import at.punkt.lodms.spi.transform.TransformCompletedEvent;
import at.punkt.lodms.spi.transform.TransformEvent;
import at.punkt.lodms.spi.transform.TransformFailedEvent;
import org.apache.log4j.Logger;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

/**
 *
 * @author kreisera
 */
@Component
public class Log4jTransformListener implements ApplicationListener<TransformEvent> {

    final Logger logger = Logger.getLogger(Log4jTransformListener.class);

    @Override
    public void onApplicationEvent(TransformEvent e) {
        if (e instanceof TransformFailedEvent) {
            logger.error("Extraction failed", ((TransformFailedEvent)e).getException());
        } else if (e instanceof TransformCompletedEvent) {
            logger.info("Extraction completed successfully for "+e.getTransformer().getClass().getSimpleName()+" in "+((TransformCompletedEvent)e).getTransformContext().getDuration()+" ms");
        }
    }
}
