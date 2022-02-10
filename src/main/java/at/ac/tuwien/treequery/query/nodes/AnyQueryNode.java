package at.ac.tuwien.treequery.query.nodes;

import at.ac.tuwien.treequery.query.state.MatchingState;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class AnyQueryNode extends ContainerQueryNode {

    private final boolean optional;

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
