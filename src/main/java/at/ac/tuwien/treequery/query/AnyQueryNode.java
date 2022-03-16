package at.ac.tuwien.treequery.query;

import at.ac.tuwien.treequery.annotation.InternalApi;
import at.ac.tuwien.treequery.builder.QueryNodeBuilder;
import at.ac.tuwien.treequery.matching.MatchingState;
import at.ac.tuwien.treequery.matching.StreamCache;
import at.ac.tuwien.treequery.xml.QueryXmlConverter;

import java.util.List;
import java.util.stream.Stream;

/**
 * This class represents an "any" or "optional" container query node
 * <p>
 * Third-party code should not use this class directly, but use the builder or XML converter and the {@link QueryNode} interface.
 */
@InternalApi
public class AnyQueryNode extends ContainerQueryNode {

    private final boolean optional;

    /**
     * Creates a new "any" or "optional" container query node instance.
     * <p>
     * Third-party code should not call this constructor directly, but use the provided builder or XML converter
     *
     * @param children The list of child query nodes in this container
     * @param optional Flag indicating whether the matching should be optional
     * @see QueryNodeBuilder#container
     * @see QueryXmlConverter
     */
    public AnyQueryNode(List<QueryNode> children, boolean optional) {
        super(children);
        this.optional = optional;
    }

    @Override
    public Stream<MatchingState> findMatches(MatchingState start) {
        if (children.isEmpty()) {
            // Not looking for anything: This trivially matches
            return Stream.of(start);
        }

        StreamCache<MatchingState> states = new StreamCache<>(Stream.empty());

        for (QueryNode query : children) {
            // Match against previous match and against original state
            Stream<MatchingState> subResult = Stream.concat(Stream.of(start), states.get())
                    // Always start looking at the start state (with potentially different references set)
                    .map(state -> state.withStart(start))
                    // Make sure only distinct states are evaluated
                    .distinct()
                    // Evaluate candidate states
                    .flatMap(query::findMatches);

            // Add new results to existing solutions
            states = new StreamCache<>(Stream.concat(states.get(), subResult).distinct());
        }

        return optional && states.get().findAny().isEmpty() ? Stream.of(start) : states.get();
    }

    public boolean isOptional() {
        return optional;
    }
}
