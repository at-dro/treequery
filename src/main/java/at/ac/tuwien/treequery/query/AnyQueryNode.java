package at.ac.tuwien.treequery.query;

import at.ac.tuwien.treequery.annotation.InternalApi;
import at.ac.tuwien.treequery.builder.QueryNodeBuilder;
import at.ac.tuwien.treequery.matching.MatchingState;
import at.ac.tuwien.treequery.xml.QueryXmlConverter;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
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

        Set<MatchingState> states = new HashSet<>();

        for (QueryNode query : children) {
            // Match against previous match and against original state
            Set<MatchingState> subResult = Stream.concat(Stream.of(start), states.stream())
                    // Make sure only distinct states are evaluated
                    .distinct()
                    // Evaluate candidate states
                    .flatMap(query::findMatches)
                    .collect(Collectors.toSet());

            // Add to set of existing solutions
            states.addAll(subResult);
        }

        return optional && states.isEmpty() ? Stream.of(start) : states.stream();
    }

    public boolean isOptional() {
        return optional;
    }
}
