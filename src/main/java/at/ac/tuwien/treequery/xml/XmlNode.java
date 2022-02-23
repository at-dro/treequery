package at.ac.tuwien.treequery.xml;

import at.ac.tuwien.treequery.annotation.PublicApi;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;
import java.util.stream.Stream.Builder;

/**
 * This class is a wrapper around an XML Node and provides some higher level read access
 */
@PublicApi
public class XmlNode {

    private final Node node;

    /**
     * Create a new instance wrapping the given XML element
     *
     * @param node The XML element to wrap
     */
    @PublicApi
    public XmlNode(Node node) {
        this.node = node;
    }

    /**
     * Get the first child of this element with the given tag name
     *
     * @param name The tag name of the element to return
     * @return An element with the given tag name, if it exists
     */
    @PublicApi
    public Optional<XmlNode> getChild(String name) {
        return getChildrenByName(name).findFirst();
    }

    /**
     * Get all children of this element
     *
     * @return A stream of XmlNode instances
     */
    @PublicApi
    public Stream<XmlNode> getChildren() {
        return streamChildren().map(XmlNode::new);
    }

    /**
     * Get all children of this element with a given tag name
     *
     * @param name The tag name of the elements to return
     * @return A stream of XmlNode instances with the given tag name
     */
    @PublicApi
    public Stream<XmlNode> getChildrenByName(String name) {
        return streamChildren().filter(n -> name.equals(n.getNodeName())).map(XmlNode::new);
    }

    /**
     * Get the tag name of this XML element
     *
     * @return The tag name
     */
    @PublicApi
    public String getName() {
        return node.getNodeName();
    }

    /**
     * Get the text value of this XML element
     *
     * @return The text value, if this is a non-empty text node
     */
    @PublicApi
    public Optional<String> getValue() {
        StringBuilder result = new StringBuilder();
        boolean hasContent = false;

        for (Node child = node.getFirstChild(); child != null; child = child.getNextSibling()) {
            // Check all immediate children for text (do not collect text recursively)
            String content = child.getNodeValue();
            switch (child.getNodeType()) {
                case Node.TEXT_NODE:
                    // Regular text node: Ignore surrounding whitespace
                    if (!content.isBlank()) {
                        // Only add if not empty or blank
                        hasContent = true;
                        result.append(content.trim());
                    }
                    break;
                case Node.CDATA_SECTION_NODE:
                    // CDATA node: Keep it as is
                    hasContent = true;
                    result.append(content);
                    break;
            }
        }

        return hasContent ? Optional.of(result.toString()) : Optional.empty();
    }

    /**
     * Get the text value of this XML element
     *
     * @return The non-empty text value
     * @throws RuntimeException Thrown if this not a text node, or it is empty
     */
    @PublicApi
    public String getNonNullValue() {
        return getValue().orElseThrow(() -> new RuntimeException(
                String.format("Missing value in node '%s' > '%s'.", node.getParentNode().getNodeName(), node.getNodeName())
        ));
    }

    /**
     * Get all attributes of this XML element
     *
     * @return A map of attribute values
     */
    @PublicApi
    public Map<String, String> getAttributes() {
        NamedNodeMap attr = node.getAttributes();
        Map<String, String> result = new LinkedHashMap<>();
        for (int i = 0; i < attr.getLength(); i++) {
            Node item = attr.item(i);
            result.put(item.getNodeName(), item.getNodeValue());
        }
        return result;
    }

    /**
     * Get the value of a specific attribute
     *
     * @param attribute The requested attribute
     * @return The value of the attribute, if it is set
     */
    @PublicApi
    public Optional<String> getAttribute(String attribute) {
        return Optional.ofNullable(node.getAttributes().getNamedItem(attribute))
                .map(Node::getNodeValue)
                .filter(n -> !n.isBlank());
    }

    /**
     * Get the value of a specific attribute
     *
     * @param attribute The requested attribute
     * @return The non-empty value of the attribute
     * @throws RuntimeException Thrown if the attribute is not set
     */
    @PublicApi
    public String getNonNullAttribute(String attribute) {
        return getAttribute(attribute).orElseThrow(() -> new RuntimeException(
                String.format("Missing '%s' attribute @ '%s' > '%s'.", attribute, node.getParentNode().getNodeName(),
                        node.getNodeName())
        ));
    }

    /**
     * Get the integer value of a specific attribute
     *
     * @param attribute The requested attribute
     * @return The integer value of the attribute
     * @throws NumberFormatException Thrown if the attribute does not contain a valid integer
     */
    @PublicApi
    public int getIntAttribute(String attribute) {
        return Integer.parseInt(getNonNullAttribute(attribute));
    }

    private Stream<Node> streamChildren() {
        NodeList children = node.getChildNodes();

        Builder<Node> builder = Stream.builder();
        for (int i = 0; i < children.getLength(); i++) {
            Node item = children.item(i);
            if (item.getNodeType() == Node.ELEMENT_NODE) {
                builder.accept(item);
            }
        }
        return builder.build();
    }
}
