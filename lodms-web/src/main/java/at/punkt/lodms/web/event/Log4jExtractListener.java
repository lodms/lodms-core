/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package at.punkt.lodms.web.event;

import at.punkt.lodms.spi.extract.ExtractCompletedEvent;
import at.punkt.lodms.spi.extract.ExtractEvent;
import at.punkt.lodms.spi.extract.ExtractFailedEvent;
import org.apache.log4j.Logger;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

/**
 *
 * @author kreisera
 */
@Component
public class Log4jExtractListener implements ApplicationListener<ExtractEvent> {

    final Logger logger = Logger.getLogger(Log4jExtractListener.class);

    @Override
    public void onApplicationEvent(ExtractEvent e) {
        if (e instanceof ExtractFailedEvent) {
            logger.error("Extraction failed", ((ExtractFailedEvent)e).getException());
        } else if (e instanceof ExtractCompletedEvent) {
            logger.info("Extraction completed successfully for "+e.getExtractor().getClass().getSimpleName()+" in "+((ExtractCompletedEvent)e).getExtractContext().getDuration()+" ms");
        }
    }
}
