package at.ac.tuwien.treequery.subject;

import at.ac.tuwien.treequery.annotation.InternalApi;
import at.ac.tuwien.treequery.annotation.PublicApi;

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

/**
 * This interface represents nodes in the subject tree.
 * <p>
 * Users of the library may implement the interface themselves, or use the provided base implementation.
 *
 * @see BaseSubjectNode
 */
@PublicApi
public interface SubjectNode {

    /**
     * Check if this node matches the given type and properties
     *
     * @param type The type the node should have, or null to allow any node type
     * @param properties The properties the node should have, or null to not check any properties
     * @param references A collection of named node references that can be used for matching
     * @return True iff the type and properties are either null or match the given node
     */
    @PublicApi
    boolean matches(String type, Map<String, Object> properties, Map<String, SubjectNode> references);

    /**
     * Get the targets against which the matching is performed.<br>
     * Normally, that is just this node itself, but subclasses can override it and, for instance, match against the children
     *
     * @return A stream of nodes to match against
     */
    @InternalApi
    Stream<? extends SubjectNode> getMatchingTargets();

    /**
     * Get the type of this node
     *
     * @return The string identifying the type of this node
     */
    @InternalApi
    String getType();

    /**
     * Get the properties of this node
     *
     * @return A (possibly empty) map of properties of this node
     */
    @InternalApi
    Map<String, Object> getProperties();

    /**
     * Get the children of this node
     *
     * @return A (possibly empty) list of children of this node
     */
    @InternalApi
    List<? extends SubjectNode> getChildren();
}
