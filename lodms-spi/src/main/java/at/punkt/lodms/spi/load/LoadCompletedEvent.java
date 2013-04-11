package at.punkt.lodms.spi.load;

/**
 * Event is published when a {@link Loader} completed successfully.
 *
 * @author Alex Kreiser (akreiser@gmail.com)
 */
public class LoadCompletedEvent extends LoadEvent {

    public LoadCompletedEvent(Loader loader, LoadContext loadContext, Object source) {
        super(loader, loadContext, source);
    }
}