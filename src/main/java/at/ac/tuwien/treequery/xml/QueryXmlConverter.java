package at.ac.tuwien.treequery.xml;

import at.ac.tuwien.treequery.query.nodes.AllQueryNode;
import at.ac.tuwien.treequery.query.nodes.AnyQueryNode;
import at.ac.tuwien.treequery.query.nodes.ExactQueryNode;
import at.ac.tuwien.treequery.query.nodes.QueryNode;
import at.ac.tuwien.treequery.query.nodes.SingleQueryNode;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class QueryXmlConverter extends XmlConverter<QueryNode> {

    private final Set<String> containerTags;

    /**
     * Create a new XML converter for queries
     *
     * @param containerTags Optional additional tag names to be used for containers
     */
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
        // We do not currently support this
        throw new UnsupportedOperationException();
    }
}