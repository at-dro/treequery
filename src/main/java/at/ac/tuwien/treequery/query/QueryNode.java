package at.ac.tuwien.treequery.query;

import at.ac.tuwien.treequery.annotation.InternalApi;
import at.ac.tuwien.treequery.annotation.PublicApi;
import at.ac.tuwien.treequery.matching.MatchingState;
import at.ac.tuwien.treequery.subject.SubjectNode;

import java.util.stream.Stream;

/**
 * This interface is the base for all query node types
 */
@PublicApi
public interface QueryNode {

    /**
     * Checks the subject tree rooted in the given subject node for matches of the query tree rooted in this query node
     *
     * @param node The root node of the subject tree
     * @return True iff at least one match exists
     */
    @PublicApi
    default boolean hasMatches(SubjectNode node) {
        return findMatches(node).findAny().isPresent();
    }

    /**
     * Obtains all matches in the subject tree rooted in the given subject node
     *
     * @param node The root node of the subject tree
     * @return A stream of matching states, or an empty stream if no match could be found
     */
    @InternalApi
    default Stream<MatchingState> findMatches(SubjectNode node) {
        return MatchingState.fromSubjectNode(node).flatMap(this::findMatches);
    }

    /**
     * Executes the matching process starting from the given state
     *
     * @param start The matching state before reaching this query node
     * @return A stream of matching states after successfully matching this query node, or an empty stream if no match could be found
     */
    @InternalApi
    Stream<MatchingState> findMatches(MatchingState start);

    /**
     * Checks whether this query node or any of its descendants stores any named references
     *
     * @return True iff named references are used
     */
    @InternalApi
    boolean hasReferences();
}
