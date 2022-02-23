package at.ac.tuwien.treequery.subject;

import at.ac.tuwien.treequery.annotation.PublicApi;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;

/**
 * This is just a simple base implementation for subject nodes with default matching implementations for the generic cases.<br>
 * Matching is split into multiple small methods, which allows parts to be overridden for more special subclasses.
 */
@PublicApi
public class BaseSubjectNode implements SubjectNode {

    private final String type;
    private final Map<String, Object> properties;
    private final List<? extends SubjectNode> children;

    /**
     * Construct a new subject node
     *
     * @param type The type of the node, which is separate from the rest of the properties to allow fast filtering
     * @param children An (optional) list of ordered children of this node
     * @param properties An (optional) map of properties of this node
     */
    public BaseSubjectNode(String type, Map<String, Object> properties, List<? extends SubjectNode> children) {
        this.type = type;
        this.properties = Objects.requireNonNullElse(properties, Collections.emptyMap());
        this.children = Objects.requireNonNullElse(children, Collections.emptyList());
    }

    @Override
    public boolean matches(String type, Map<String, Object> properties, Map<String, SubjectNode> references) {
        return this.typeMatches(type) && this.propertiesMatch(properties, references);
    }

    /**
     * Check if the type of this node matches the given one
     *
     * @param type The type the node should have, or null to allow any node type
     * @return True iff the given type is null or matches the node type
     */
    @PublicApi
    protected boolean typeMatches(String type) {
        return type == null || type.equals(this.type);
    }

    /**
     * Check if the properties of this node match the given required properties
     *
     * @param properties The properties the node should have, or null to not check any properties
     * @param references A collection of named node references that can be used for matching
     * @return True iff all given properties match the properties of this node
     */
    @PublicApi
    protected boolean propertiesMatch(Map<String, Object> properties, Map<String, SubjectNode> references) {
        return properties == null || properties.entrySet().stream().allMatch(e -> propertyMatches(e.getKey(), e.getValue(), references));
    }

    /**
     * Check if this node has the requested property
     *
     * @param key The key of the required property
     * @param value The required value of the property
     * @param references A collection of named node references that can be used for matching
     * @return True iff this node fulfills the given property
     */
    @PublicApi
    protected boolean propertyMatches(String key, Object value, Map<String, SubjectNode> references) {
        return propertyMatches(key, value);
    }

    /**
     * Check if this node has the requested property
     *
     * @param key The key of the required property
     * @param value The required value of the property
     * @return True iff this node fulfills the given property
     */
    @PublicApi
    protected boolean propertyMatches(String key, Object value) {
        return Objects.equals(properties.get(key), value);
    }

    @Override
    public Stream<? extends SubjectNode> getMatchingTargets() {
        return Stream.of(this);
    }

    @Override
    public String getType() {
        return type;
    }

    @Override
    public Map<String, Object> getProperties() {
        return properties;
    }

    @Override
    public List<? extends SubjectNode> getChildren() {
        return children;
    }

    @Override
    public String toString() {
        return String.format("%s, %s, children=%d", type, properties, children.size());
    }
}
