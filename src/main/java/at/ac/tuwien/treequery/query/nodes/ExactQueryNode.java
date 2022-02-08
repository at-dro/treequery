package at.ac.tuwien.treequery.query.nodes;

import at.ac.tuwien.treequery.query.state.MatchingState;
import at.ac.tuwien.treequery.query.state.LinkedSubjectNode;

import java.util.List;
import java.util.stream.Stream;

public class ExactQueryNode extends ContainerQueryNode {

    public ExactQueryNode(List<QueryNode> children) {
        super(children);
    }

    @Override
    public Stream<MatchingState> findMatches(MatchingState start) {
        Stream<MatchingState> states = Stream.of(start);
        boolean exactEnd = true;
        for (QueryNode query : children) {
            boolean isSingle = query instanceof SingleQueryNode;
            boolean matchOne = exactEnd && isSingle;

            states = states
                    .flatMap(candidate -> handleCandidate(candidate, query, matchOne))
                    .distinct();

            exactEnd = isSingle || query instanceof ExactQueryNode;
        }

        if (exactEnd) {
            // The last query needs to match the last child of the subject node, i.e. no node may be left at the end
            states = states.filter(state -> state.getElement() == null);
        }

        return states;
    }

    private Stream<MatchingState> handleCandidate(MatchingState candidate, QueryNode query, boolean matchOne) {
        if (matchOne && candidate.getElement() == null) {
            // Need to match exactly one element, but no elements left: Fail directly
            return Stream.empty();
        }

        Stream<MatchingState> result = query.findMatches(candidate).map(MatchingState::withDirectChild);
        if (matchOne) {
            // If matching exactly one element the resulting element must be the direct neighbor of the candidate
            LinkedSubjectNode expected = candidate.getElement().getDirectNeighbor();
            result = result.filter(s -> s.getElement() == expected);
        }

        return result;
    }
}
