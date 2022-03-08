package at.ac.tuwien.treequery.xml;

import at.ac.tuwien.treequery.annotation.PublicApi;
import at.ac.tuwien.treequery.subject.BaseSubjectNode;
import at.ac.tuwien.treequery.subject.SubjectNode;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * This converter can be used to parse and write subject trees as XML
 */
@PublicApi
public class SubjectXmlConverter extends XmlConverter<SubjectNode> {

    @Override
    public SubjectNode parse(XmlNode node) {
        // Load children and properties
        List<SubjectNode> children = node.getChildren().map(this::parse).collect(Collectors.toList());
        Map<String, String> properties = node.getAttributes();

        // Add the value of text elements to the properties
        node.getValue().ifPresent(v -> properties.put("value", v));

        return new BaseSubjectNode(node.getName(), Collections.unmodifiableMap(properties), children);
    }

    @Override
    protected XmlCreator createXml(SubjectNode node, XmlCreator parent) {
        XmlCreator xml = XmlCreator.createElement(node.getType(), parent);
        node.getProperties().forEach((key, value) -> setXmlAttribute(xml, key, value));
        node.getChildren().forEach(child -> createXml(child, xml));
        return xml;
    }
}