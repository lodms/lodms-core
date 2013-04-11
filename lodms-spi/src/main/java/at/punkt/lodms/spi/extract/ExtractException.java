/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package at.punkt.lodms.spi.extract;

/**
 * Exception thrown by an extractor if something goes wrong throughout the
 * extraction process.
 *
 * @see Extractor
 * @author Alex Kreiser (akreiser@gmail.com)
 */
public class ExtractException extends Exception {

    public ExtractException(Throwable cause) {
        super(cause);
    }

    public ExtractException(String message, Throwable cause) {
        super(message, cause);
    }

    public ExtractException(String message) {
        super(message);
    }
}
