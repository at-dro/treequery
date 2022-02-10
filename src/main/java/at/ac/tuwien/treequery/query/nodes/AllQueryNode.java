package at.ac.tuwien.treequery.query.nodes;

import at.ac.tuwien.treequery.query.state.MatchingState;

import java.util.List;
import java.util.stream.Stream;

public class AllQueryNode extends ContainerQueryNode {

    private final boolean ordered;

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
