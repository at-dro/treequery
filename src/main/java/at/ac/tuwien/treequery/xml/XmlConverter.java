package at.ac.tuwien.treequery.xml;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Objects;

public abstract class XmlConverter<T> {

    public T parseFile(String name) throws IOException {
        try (InputStream data = new FileInputStream(name)) {
            return parse(data);
        }
    }

    public T parseResource(String name) throws IOException {
        try (InputStream data = getClass().getClassLoader().getResourceAsStream(name)) {
            return parse(data);
        }
    }

    public T parse(InputStream data) throws IOException {
        try {
            Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(data);
            XmlNode root = new XmlNode(document.getDocumentElement());
            return parse(root);
        } catch (SAXException | ParserConfigurationException e) {
            throw new IOException(e);
        }
    }

    public abstract T parse(XmlNode node);

    public void export(T node, OutputStream out) throws IOException {
        createXml(node, null).write(out);
    }

    protected abstract XmlCreator createXml(T node, XmlCreator parent);

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
