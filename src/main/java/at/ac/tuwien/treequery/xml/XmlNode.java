package at.ac.tuwien.treequery.xml;

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
public class XmlNode {

    private final Node node;

    /**
     * Create a new instance wrapping the given XML element
     *
     * @param node The XML element to wrap
     */
    public XmlNode(Node node) {
        this.node = node;
    }

    /**
     * Get the first child of this element with the given tag name
     *
     * @param name The tag name of the element to return
     * @return An element with the given tag name, if it exists
     */
    public Optional<XmlNode> getChild(String name) {
        return getChildrenByName(name).findFirst();
    }

    /**
     * Get all children of this element
     *
     * @return A stream of XmlNode instances
     */
    public Stream<XmlNode> getChildren() {
        return streamChildren().map(XmlNode::new);
    }

    /**
     * Get all children of this element with a given tag name
     *
     * @param name The tag name of the elements to return
     * @return A stream of XmlNode instances with the given tag name
     */
    public Stream<XmlNode> getChildrenByName(String name) {
        return streamChildren().filter(n -> name.equals(n.getNodeName())).map(XmlNode::new);
    }

    /**
     * Get the tag name of this XML element
     *
     * @return The tag name
     */
    public String getName() {
        return node.getNodeName();
    }

    /**
     * Get the text value of this XML element
     *
     * @return The text value, if this is a non-empty text node
     */
    public Optional<String> getValue() {
        return Optional.ofNullable(node.getFirstChild())
                .map(Node::getNodeValue)
                .filter(n -> !n.isBlank());
    }

    /**
     * Get the text value of this XML element
     *
     * @return The non-empty text value
     * @throws RuntimeException Thrown if this not a text node, or it is empty
     */
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
