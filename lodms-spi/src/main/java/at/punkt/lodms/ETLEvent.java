package at.punkt.lodms;

import org.springframework.context.ApplicationEvent;

/**
 * Root event class for the event class hierarchy
 *
 * @author Alex Kreiser (akreiser@gmail.com)
 */
public abstract class ETLEvent extends ApplicationEvent {

    public ETLEvent(Object source) {
        super(source);
    }
}
