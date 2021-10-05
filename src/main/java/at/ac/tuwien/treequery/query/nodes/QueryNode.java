package at.ac.tuwien.treequery.query.nodes;

import at.ac.tuwien.treequery.query.state.MatchingState;
import at.ac.tuwien.treequery.tree.TreeNode;

import java.util.stream.Stream;

public interface QueryNode {

    default boolean matches(TreeNode node) {
        return MatchingState.fromTreeNode(node)
                .flatMap(this::matches)
                .findAny().isPresent();
    }

    Stream<MatchingState> matches(MatchingState start);

    boolean hasReferences();
}
