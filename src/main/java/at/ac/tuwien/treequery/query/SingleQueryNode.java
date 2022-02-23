package at.ac.tuwien.treequery.query;

import at.ac.tuwien.treequery.annotation.InternalApi;
import at.ac.tuwien.treequery.builder.QueryNodeBuilder;
import at.ac.tuwien.treequery.matching.LinkedSubjectNode;
import at.ac.tuwien.treequery.matching.MatchingState;
import at.ac.tuwien.treequery.matching.NodeReferences;
import at.ac.tuwien.treequery.xml.QueryXmlConverter;

import java.util.Map;
import java.util.stream.Stream;

/**
 * This class represents a single container query node
 * <p>
 * Third-party code should not use this class directly, but use the builder or XML converter and the {@link QueryNode} interface.
 */
@InternalApi
public class SingleQueryNode implements QueryNode {

    private final String type;
    private final Map<String, Object> properties;
    private final QueryNode children;
    private final boolean direct;
    private final String reference;
    private final boolean hasReferences;

    /**
     * Creates a new exact container query node instance.
     * <p>
     * Third-party code should not call this constructor directly, but use the provided builder or XML converter
     *
     * @param type The type to match
     * @param properties A collection of optional properties to match
     * @param children The query node wrapping the child query
     * @param direct Flag indicating whether only direct children should be matched
     * @param reference The reference name used to store the matching subject node
     * @see QueryNodeBuilder#single
     * @see QueryXmlConverter
     */
    public SingleQueryNode(String type, Map<String, Object> properties, QueryNode children, boolean direct, String reference) {
        this.type = type;
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
        return element.node().matches(type, properties, references.getData());
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

    public String getType() {
        return type;
    }

    public Map<String, Object> getProperties() {
        return properties;
    }

    public QueryNode getChildren() {
        return children;
    }

    public boolean isDirect() {
        return direct;
    }

    public String getReference() {
        return reference;
    }

    @Override
    public String toString() {
        return String.format("Single[%s, %s, direct=%s, reference=%s]", type, properties, direct, reference);
    }
}
