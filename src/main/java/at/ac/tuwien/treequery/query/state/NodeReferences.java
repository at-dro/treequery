package at.ac.tuwien.treequery.query.state;

import at.ac.tuwien.treequery.subject.SubjectNode;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class NodeReferences {

    static final NodeReferences EMPTY = new NodeReferences(Collections.emptyMap());

    private final Map<String, SubjectNode> data;

    private NodeReferences(Map<String, SubjectNode> data) {
        this.data = data;
    }

    public NodeReferences withReference(String reference, SubjectNode node) {
        if (reference == null || data.get(reference) == node) {
            return this;
        }

        Map<String, SubjectNode> newData = new HashMap<>(data);
        newData.put(reference, node);
        return new NodeReferences(newData);
    }

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
