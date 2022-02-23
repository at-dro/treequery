package at.ac.tuwien.treequery.xml;

import at.ac.tuwien.treequery.annotation.PublicApi;
import at.ac.tuwien.treequery.query.AllQueryNode;
import at.ac.tuwien.treequery.query.AnyQueryNode;
import at.ac.tuwien.treequery.query.ContainerQueryNode;
import at.ac.tuwien.treequery.query.ExactQueryNode;
import at.ac.tuwien.treequery.query.QueryNode;
import at.ac.tuwien.treequery.query.SingleQueryNode;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * This converter can be used to parse and write query trees as XML
 */
@PublicApi
public class QueryXmlConverter extends XmlConverter<QueryNode> {

    private final Set<String> containerTags;

    /**
     * Create a new XML converter for queries
     *
     * @param containerTags Optional additional tag names to be used for containers
     */
    @PublicApi
    public QueryXmlConverter(String... containerTags) {
        this.containerTags = new HashSet<>();
        this.containerTags.add("container");
        this.containerTags.addAll(Arrays.asList(containerTags));
    }

    @Override
    public QueryNode parse(XmlNode node) {
        Map<String, String> properties = node.getAttributes();

        // Read the child mode from the attribute and create a container for the children accordingly
        String mode = properties.getOrDefault("mode", "ordered");
        properties.remove("mode");

        List<QueryNode> children = node.getChildren().map(this::parse).collect(Collectors.toList());
        QueryNode childrenContainer = getChildrenContainer(mode, children);

        if (containerTags.contains(node.getName())) {
            // Structural query node, return the children container directly
            return childrenContainer;
        }

        // Determine if child queries must match direct children of the matched node
        boolean direct = false;
        if (properties.containsKey("direct")) {
            direct = Boolean.parseBoolean(properties.get("direct"));
            properties.remove("direct");
        }

        // Assign a reference name to the matched node
        String reference = null;
        if (properties.containsKey("ref")) {
            reference = properties.get("ref");
            properties.remove("ref");
        }

        // Add the value of text elements to the properties
        if (children.isEmpty()) {
            node.getValue().ifPresent(v -> properties.put("value", v));
        }

        return new SingleQueryNode(node.getName(), Collections.unmodifiableMap(properties), childrenContainer, direct, reference);
    }

    private QueryNode getChildrenContainer(String mode, List<QueryNode> children) {
        switch (mode) {
            case "any":
                return new AnyQueryNode(children, false);
            case "optional":
                return new AnyQueryNode(children, true);
            case "unordered":
                return new AllQueryNode(children, false);
            case "ordered":
                return new AllQueryNode(children, true);
            case "exact":
                return new ExactQueryNode(children);
            default:
                throw new IllegalArgumentException("Unknown child mode " + mode);
        }
    }

    @Override
    protected XmlCreator createXml(QueryNode node, XmlCreator parent) {
        if (node instanceof ContainerQueryNode) {
            return createXml((ContainerQueryNode) node, parent);
        }

        if (node instanceof SingleQueryNode) {
            return createXml((SingleQueryNode) node, parent);
        }

        throw new IllegalArgumentException("Unknown query node " + node.getClass());
    }

    private XmlCreator createXml(ContainerQueryNode node, XmlCreator parent) {
        XmlCreator xml = XmlCreator.createElement("container", parent);
        applyContainerXml(node, xml);
        return xml;
    }

    private void applyContainerXml(ContainerQueryNode node, XmlCreator xml) {
        String mode = getContainerMode(node);
        if (mode != null) {
            setXmlAttribute(xml, "mode", mode);
        }

        node.getChildren().forEach(child -> createXml(child, xml));
    }

    private XmlCreator createXml(SingleQueryNode node, XmlCreator parent) {
        XmlCreator xml = XmlCreator.createElement(node.getType(), parent);

        node.getProperties().forEach((key, value) -> setXmlAttribute(xml, key, value));
        if (node.isDirect()) {
            setXmlAttribute(xml, "direct", "true");
        }
        if (node.getReference() != null) {
            setXmlAttribute(xml, "ref", node.getReference());
        }

        if (node.getChildren() instanceof ContainerQueryNode) {
            // Unwrap the children container
            applyContainerXml((ContainerQueryNode) node.getChildren(), xml);
        } else {
            // Otherwise, just call the XML logic recursively
            createXml(node.getChildren(), xml);
        }

        return xml;
    }

    private String getContainerMode(ContainerQueryNode container) {
        if (container instanceof ExactQueryNode) {
            return "exact";
        }
        if (container instanceof AllQueryNode) {
            // "ordered" is the default mode
            return ((AllQueryNode) container).isOrdered() ? null : "unordered";
        }
        if (container instanceof AnyQueryNode) {
            return ((AnyQueryNode) container).isOptional() ? "optional" : "any";
        }
        throw new IllegalArgumentException("Unknown query container " + container.getClass());
    }
}