package at.ac.tuwien.treequery.query.nodes;

import at.ac.tuwien.treequery.query.state.MatchingState;
import at.ac.tuwien.treequery.subject.SubjectNode;

import java.util.stream.Stream;

public interface QueryNode {

    default boolean hasMatches(SubjectNode node) {
        return findMatches(node).findAny().isPresent();
    }

    default Stream<MatchingState> findMatches(SubjectNode node) {
        return MatchingState.fromSubjectNode(node).flatMap(this::findMatches);
    }

    Stream<MatchingState> findMatches(MatchingState start);

    boolean hasReferences();
}
