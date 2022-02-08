package at.ac.tuwien.treequery.xml;

import at.ac.tuwien.treequery.subject.BaseSubjectNode;
import at.ac.tuwien.treequery.subject.SubjectNode;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class SubjectXmlConverter extends XmlConverter<SubjectNode> {

    @Override
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

    @Override
    protected XmlCreator createXml(SubjectNode node, XmlCreator parent) {
        XmlCreator xml = XmlCreator.createElement(node.getType(), parent);
        node.getProperties().forEach((key, value) -> setXmlAttribute(xml, key, value));
        node.getChildren().forEach(child -> createXml(child, xml));
        return xml;
    }

    private void setXmlAttribute(XmlCreator xml, String key, Object value) {
        if (key.equals("value")) {
            xml.setText(Objects.toString(value));
        } else {
            xml.setAttribute(key, Objects.toString(value, ""));
        }
    }
}