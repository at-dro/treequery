package at.ac.tuwien.treequery.matching;

import at.ac.tuwien.treequery.annotation.InternalApi;
import at.ac.tuwien.treequery.subject.SubjectNode;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * This class encapsulates a collection of named references obtained in the matching process.
 * <p>
 * This class should not be used by third-party code directly.
 */
@InternalApi
public class NodeReferences {

    static final NodeReferences EMPTY = new NodeReferences(Collections.emptyMap());

    private final Map<String, SubjectNode> data;

    private NodeReferences(Map<String, SubjectNode> data) {
        this.data = data;
    }

    /**
     * Creates a copy of this instance with the additional reference set.
     * If the reference name is {@code null} or already set to the given node, the unmodified instance is returned.
     *
     * @param reference The reference name, or {@code null} to not store a reference
     * @param node The subject node to store
     * @return A copy of this instance with the additional reference, or this instance if no change occurred
     */
    public NodeReferences withReference(String reference, SubjectNode node) {
        if (reference == null || data.get(reference) == node) {
            return this;
        }

        Map<String, SubjectNode> newData = new HashMap<>(data);
        newData.put(reference, node);
        return new NodeReferences(newData);
    }

    /**
     * Gets the underlying {@code Map} containing the named references.
     * The obtained map must not be modified by external callers.
     *
     * @return The map containing the references
     */
    public Map<String, SubjectNode> getData() {
        return data;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        NodeReferences that = (NodeReferences) o;
        return Objects.equals(data, that.data);
    }

    @Override
    public int hashCode() {
        return Objects.hash(data);
    }
}
