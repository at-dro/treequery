package at.ac.tuwien.treequery.xml;

import at.ac.tuwien.treequery.subject.BaseSubjectNode;
import at.ac.tuwien.treequery.subject.SubjectNode;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class XmlSubjectParser {

    public List<SubjectNode> parse(InputStream data) throws IOException {
        try {
            Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(data);
            XmlNode root = new XmlNode(document.getDocumentElement());
            return root.getChildren().map(this::parse).collect(Collectors.toList());
        } catch (SAXException | ParserConfigurationException e) {
            throw new IOException(e);
        }
    }

    public SubjectNode parse(XmlNode node) {
        // Load children and properties
        List<SubjectNode> children = node.getChildren().map(this::parse).collect(Collectors.toList());
        Map<String, String> properties = node.getAttributes();

        // Add the value of text elements to the properties
        if (children.isEmpty()) {
            node.getValue().ifPresent(v -> properties.put("value", v));
        }

        return new BaseSubjectNode(node.getName(), Collections.unmodifiableMap(properties), children);
    }
}