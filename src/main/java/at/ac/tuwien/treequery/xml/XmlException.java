package at.ac.tuwien.treequery.xml;

import at.ac.tuwien.treequery.annotation.PublicApi;

/**
 * This exception indicates some error on serializing data as XML
 */
@PublicApi
public class XmlException extends RuntimeException {

    public XmlException(String message, Throwable cause) {
        super(message, cause);
    }
}
