package at.ac.tuwien.treequery.xml;

import at.ac.tuwien.treequery.annotation.PublicApi;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.IOException;
import java.io.OutputStream;

/**
 * This class is a wrapper around an XML Node and provides some higher level creation access
 */
@PublicApi
public class XmlCreator {

    private final Document document;
    private final Element element;

    /**
     * Construct a creator for the root element of a new XML document
     *
     * @param root The tag name of the root element
     * @throws XmlException Thrown if the XML document could not be created
     */
    @PublicApi
    public XmlCreator(String root) {
        try {
            document = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
            element = document.createElement(root);
            document.appendChild(element);
        } catch (ParserConfigurationException e) {
            throw new XmlException("Failed to create XML document", e);
        }
    }

    private XmlCreator(Document document, Element element) {
        this.document = document;
        this.element = element;
    }

    /**
     * Create a new element in the XML tree
     *
     * @param name The tag name of the element
     * @param parent The XmlCreator associated with the parent, or null to create the root element of a new XML document
     * @return An XmlCreator instance wrapping the new element
     * @throws XmlException Thrown if the XML document could not be created
     */
    @PublicApi
    public static XmlCreator createElement(String name, XmlCreator parent) {
        return parent == null ? new XmlCreator(name) : parent.appendChild(name);
    }

    /**
     * Append a child to this XML element
     *
     * @param name The tag name of the new child
     * @return The XmlCreator instance wrapping the new child element
     */
    @PublicApi
    public XmlCreator appendChild(String name) {
        Element child = document.createElement(name);
        element.appendChild(child);
        return new XmlCreator(document, child);
    }

    /**
     * Set an attribute on this XML element
     *
     * @param key The key of the attribute
     * @param value The value for the attribute
     * @return This XmlCreator instance to allow chaining
     */
    @PublicApi
    public XmlCreator setAttribute(String key, String value) {
        element.setAttribute(key, value);
        return this;
    }

    /**
     * Set the text content this XML element
     *
     * @param text The text to set
     * @return This XmlCreator instance to allow chaining
     */
    @PublicApi
    public XmlCreator setText(String text) {
        element.appendChild(document.createCDATASection(text));
        return this;
    }

    /**
     * Write the formatted XML string starting at this element to an output stream
     *
     * @param out The output stream to use
     * @throws IOException Thrown if writing failed
     */
    @PublicApi
    public void write(OutputStream out) throws IOException {
        write(out, true);
    }

    /**
     * Write an XML string starting at this element to an output stream
     *
     * @param out The output stream to use
     * @param indent Whether the resulting XML should be indented
     * @throws IOException Thrown if writing failed
     */
    @PublicApi
    public void write(OutputStream out, boolean indent) throws IOException {
        if (indent) {
            // CAVE: There is a bug in OpenJDK<14 that causes additional whitespace to be added around text content
            // See: https://bugs.openjdk.java.net/browse/JDK-8223291
            // The wrapper removes this whitespace
            out = CDATAOutputStreamWrapper.wrap(out);
        }

        try {
            Transformer transformer = TransformerFactory.newInstance().newTransformer();

            transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
            if (indent) {
                transformer.setOutputProperty(OutputKeys.INDENT, "yes");
                transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
            }

            transformer.transform(new DOMSource(document), new StreamResult(out));

            // The transformer should call flush anyway, but do it explicitly (there might be differences between JVM versions)
            out.flush();
        } catch (TransformerException e) {
            throw new IOException("Failed to write XML file", e);
        }
    }
}
