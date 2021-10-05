package at.ac.tuwien.treequery.query.nodes;

import java.util.List;

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
}
