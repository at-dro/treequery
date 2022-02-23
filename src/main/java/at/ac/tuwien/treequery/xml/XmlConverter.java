package at.ac.tuwien.treequery.xml;

import at.ac.tuwien.treequery.annotation.PublicApi;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Objects;

/**
 * This is the base class for parsing and writing trees as XML
 *
 * @param <T>
 */
@PublicApi
public abstract class XmlConverter<T> {

    /**
     * Parses XML data from the file specified through a path
     *
     * @param name The filename, including the absolute or relative path
     * @return The root node of the parsed tree
     * @throws IOException Thrown if the file could not be read or XML parsing failed
     */
    @PublicApi
    public T parseFile(String name) throws IOException {
        try (InputStream data = new FileInputStream(name)) {
            return parse(data);
        }
    }

    /**
     * Parses XML data from the specified classpath resource
     *
     * @param name The name of the classpath resource
     * @return The root node of the parsed tree
     * @throws IOException Thrown if the resource could not be read or XML parsing failed
     */
    @PublicApi
    public T parseResource(String name) throws IOException {
        try (InputStream data = getClass().getClassLoader().getResourceAsStream(name)) {
            return parse(data);
        }
    }

    /**
     * Parses XML data from the specified {@code InputStream}
     *
     * @param data The stream containing the XML data
     * @return The root node of the parsed tree
     * @throws IOException Thrown if the stream could not be read or XML parsing failed
     */
    @PublicApi
    public T parse(InputStream data) throws IOException {
        try {
            Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(data);
            XmlNode root = new XmlNode(document.getDocumentElement());
            return parse(root);
        } catch (SAXException | ParserConfigurationException e) {
            throw new IOException(e);
        }
    }

    /**
     * Parses the tree from the XML in the specified XML wrapper.
     *
     * @param node The {@link XmlNode} wrapper for the XML element for the root node
     * @return The root node of the parsed tree
     */
    @PublicApi
    public abstract T parse(XmlNode node);

    /**
     * Writes a tree to the specified {@code OutputStream}
     *
     * @param node The root node of the tree to write
     * @param out The stream where the XML string is written to
     * @throws IOException Thrown if the data could not be written
     */
    @PublicApi
    public void export(T node, OutputStream out) throws IOException {
        createXml(node, null).write(out);
    }

    /**
     * Creates an XML wrapper for the given tree node
     *
     * @param node The tree node to write
     * @param parent The nullable XML parent to which the element should be appended
     * @return A new {@link XmlCreator} wrapper containing the XML representation of the tree node and its descendents
     */
    protected abstract XmlCreator createXml(T node, XmlCreator parent);

    /**
     * Sets an XML attribute, or the text content for a non-empty {@code value} property
     *
     * @param xml The XML wrapper to set the attribute on
     * @param key The key of the property
     * @param value The value of the property, whose String representation is used for the XML
     */
    protected void setXmlAttribute(XmlCreator xml, String key, Object value) {
        if (key.equals("value")) {
            String strValue = Objects.toString(value);
            if (strValue.isEmpty()) {
                // XML export does not keep empty CDATA blocks, use attribute in that case
                xml.setAttribute("value", "");
            } else {
                // Otherwise, store it as text content
                xml.setText(strValue);
            }
        } else {
            xml.setAttribute(key, Objects.toString(value, ""));
        }
    }
}
