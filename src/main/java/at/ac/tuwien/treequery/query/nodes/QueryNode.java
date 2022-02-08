package at.ac.tuwien.treequery.query.nodes;

import at.ac.tuwien.treequery.query.state.MatchingState;
import at.ac.tuwien.treequery.subject.SubjectNode;

import java.util.stream.Stream;

public interface QueryNode {

    default boolean matches(SubjectNode node) {
        return MatchingState.fromSubjectNode(node)
                .flatMap(this::matches)
                .findAny().isPresent();
    }

    Stream<MatchingState> matches(MatchingState start);

    boolean hasReferences();
}
