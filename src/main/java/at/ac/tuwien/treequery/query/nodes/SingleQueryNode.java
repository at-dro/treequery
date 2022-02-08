package at.ac.tuwien.treequery.query.nodes;

import at.ac.tuwien.treequery.query.state.LinkedSubjectNode;
import at.ac.tuwien.treequery.query.state.MatchingState;
import at.ac.tuwien.treequery.query.state.NodeReferences;

import java.util.Map;
import java.util.stream.Stream;

public class SingleQueryNode implements QueryNode {

    private final String name;
    private final Map<String, Object> properties;
    private final QueryNode children;
    private final boolean direct;
    private final String reference;
    private final boolean hasReferences;

    public SingleQueryNode(String name, Map<String, Object> properties, QueryNode children, boolean direct, String reference) {
        this.name = name;
        this.properties = properties;
        this.children = children;
        this.direct = direct;
        this.reference = reference;
        this.hasReferences = reference != null || children.hasReferences();
    }

    @Override
    public Stream<MatchingState> findMatches(MatchingState start) {
        Stream<LinkedSubjectNode> candidates = direct ? start.streamDirectChildren() : start.streamWithin();
        return candidates
                .filter(e -> matches(start.getReferences(), e))
                .flatMap(e -> handleCandidate(start, e));
    }

    private boolean matches(NodeReferences references, LinkedSubjectNode element) {
        return element.node().matches(name, properties, references.getData());
    }

    private Stream<MatchingState> handleCandidate(MatchingState state, LinkedSubjectNode element) {
        // Try to find children using the current element as parent
        MatchingState childState = state.buildChildState(reference, element);
        Stream<NodeReferences> result = children.findMatches(childState).map(MatchingState::getReferences);

        // If there are no named refs in this query subtree we do not actually need to calculate every result, they will all be the same
        result = hasReferences ? result.distinct() : result.limit(1);

        MatchingState nextState = state.neighborOf(element);
        return result.map(nextState::withReferences);
    }

    @Override
    public boolean hasReferences() {
        return hasReferences;
    }
}
