package at.ac.tuwien.treequery.query.state;

import at.ac.tuwien.treequery.tree.TreeNode;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class NodeReferences {

    static final NodeReferences EMPTY = new NodeReferences(Collections.emptyMap());

    private final Map<String, TreeNode> data;

    private NodeReferences(Map<String, TreeNode> data) {
        this.data = data;
    }

    public NodeReferences withReference(String reference, TreeNode node) {
        if (reference == null || data.get(reference) == node) {
            return this;
        }

        Map<String, TreeNode> newData = new HashMap<>(data);
        newData.put(reference, node);
        return new NodeReferences(newData);
    }

    public Map<String, TreeNode> getData() {
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
