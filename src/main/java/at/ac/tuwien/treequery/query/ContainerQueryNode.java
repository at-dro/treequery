package at.ac.tuwien.treequery.query;

import at.ac.tuwien.treequery.annotation.InternalApi;

import java.util.List;

/**
 * This class is the base for all container query nodes
 * <p>
 * Third-party code should not use this class directly, but the {@link QueryNode} interface.
 */
@InternalApi
public abstract class ContainerQueryNode implements QueryNode {

    protected final List<QueryNode> children;
    private final boolean hasReferences;

    protected ContainerQueryNode(List<QueryNode> children) {
        this.children = children;
        this.hasReferences = children.stream().anyMatch(QueryNode::hasReferences);
    }

    @Override
    public boolean hasReferences() {
        return hasReferences;
    }

    public List<QueryNode> getChildren() {
        return children;
    }
}
