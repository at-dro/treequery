package at.ac.tuwien.treequery.query;

import at.ac.tuwien.treequery.annotation.InternalApi;
import at.ac.tuwien.treequery.builder.QueryNodeBuilder;
import at.ac.tuwien.treequery.matching.MatchingState;
import at.ac.tuwien.treequery.xml.QueryXmlConverter;

import java.util.List;
import java.util.stream.Stream;

/**
 * This class represents an ordered or unordered container query node.
 * <p>
 * Third-party code should not use this class directly, but use the builder or XML converter and the {@link QueryNode} interface.
 */
@InternalApi
public class AllQueryNode extends ContainerQueryNode {

    private final boolean ordered;

    /**
     * Creates a new "ordered" or "unordered" container query node instance.
     * <p>
     * Third-party code should not call this constructor directly, but use the provided builder or XML converter
     *
     * @param children The list of child query nodes in this container
     * @param ordered Flag indicating whether the matching should be ordered
     * @see QueryNodeBuilder#container
     * @see QueryXmlConverter
     */
    public AllQueryNode(List<QueryNode> children, boolean ordered) {
        super(children);
        this.ordered = ordered;
    }

    @Override
    public Stream<MatchingState> findMatches(MatchingState start) {
        Stream<MatchingState> states = Stream.of(start);
        for (QueryNode query : children) {
            states = states.flatMap(candidate -> handleCandidate(start, candidate, query)).distinct();
        }
        return states;
    }

    private Stream<MatchingState> handleCandidate(MatchingState start, MatchingState candidate, QueryNode query) {
        // If we are not looking for an ordered solution start at the original start node, but with the candidates references set
        MatchingState state = ordered ? candidate : candidate.withStart(start);
        Stream<MatchingState> result = query.findMatches(state);

        if (!ordered) {
            // If we are not looking for an ordered solution just put the later node in the result
            result = result.map(s -> s.withMaxElement(candidate));
        }

        return result;
    }

    public boolean isOrdered() {
        return ordered;
    }
}
