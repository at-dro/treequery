package at.ac.tuwien.treequery.xml;

import at.ac.tuwien.treequery.subject.SubjectNode;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Objects;

public class XmlSubjectExporter {

    private final String root;

    public XmlSubjectExporter() {
        this.root = "root";
    }

    public XmlSubjectExporter(String root) {
        this.root = root;
    }

    public XmlCreator export(SubjectNode node) throws IOException {
        XmlCreator xml = new XmlCreator(root);
        appendXml(xml, node);
        return xml;
    }

    public void export(SubjectNode node, OutputStream out) throws IOException {
        export(node).write(out);
    }

    private void appendXml(XmlCreator parent, SubjectNode node) {
        XmlCreator xml = parent.appendChild(node.getType());
        if (xml != null) {
            node.getProperties().forEach((key, value) -> setXmlAttribute(xml, key, value));
            node.getChildren().forEach(child -> appendXml(xml, child));
        }
    }

    private void setXmlAttribute(XmlCreator xml, String key, Object value) {
        if (key.equals("value")) {
            xml.setText(Objects.toString(value));
        } else {
            xml.setAttribute(key, Objects.toString(value, ""));
        }
    }
}
