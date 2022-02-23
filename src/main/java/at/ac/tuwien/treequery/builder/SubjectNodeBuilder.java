package at.ac.tuwien.treequery.builder;

import at.ac.tuwien.treequery.annotation.PublicApi;
import at.ac.tuwien.treequery.subject.BaseSubjectNode;
import at.ac.tuwien.treequery.subject.SubjectNode;

import java.util.LinkedList;
import java.util.List;

/**
 * This is a utility class for building a {@link SubjectNode} instance.
 * This builder uses the provided default implementation for subject nodes {@link BaseSubjectNode}.
 */
@PublicApi
public class SubjectNodeBuilder {

    private final String type;
    private final PropertiesBuilder properties = PropertiesBuilder.props();
    private final List<SubjectNode> children = new LinkedList<>();

    private SubjectNodeBuilder(String type) {
        this.type = type;
    }

    /**
     * Creates a builder for a subject node
     *
     * @param type The type of the subject node
     * @return A new builder instance
     */
    @PublicApi
    public static SubjectNodeBuilder type(String type) {
        return new SubjectNodeBuilder(type);
    }

    /**
     * Adds a property to the subject node.
     * If the property already exists the old value is overwritten.
     *
     * @param key The key of the property.
     * @param value The new value for the property.
     * @return This builder instance
     */
    @PublicApi
    public SubjectNodeBuilder prop(String key, Object value) {
        properties.set(key, value);
        return this;
    }

    /**
     * Appends a child subject node to the node
     *
     * @param child The child subject node, which itself may be the root of a whole subtree
     * @return This builder instance
     */
    @PublicApi
    public SubjectNodeBuilder child(SubjectNode child) {
        children.add(child);
        return this;
    }

    /**
     * Obtains the subject node.
     * This builder instance should not be used anymore after this method was called.
     *
     * @return The finalized subject node
     */
    @PublicApi
    public SubjectNode build() {
        return new BaseSubjectNode(type, properties.build(), children);
    }
}
