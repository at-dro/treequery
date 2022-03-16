package at.ac.tuwien.treequery.benchmark;

import at.ac.tuwien.treequery.subject.SubjectNode;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * This class wraps subject nodes and stores the depth, height of the tree and total size
 */
class SubjectNodeWrapper {

    private final SubjectNode element;
    private final int depth;
    private final List<SubjectNodeWrapper> children;

    final int height;
    private final int size;

    public SubjectNodeWrapper(SubjectNode root) {
        this(root, 0);
    }

    private SubjectNodeWrapper(SubjectNode element, int depth) {
        this.element = element;
        this.depth = depth;

        children = element.getChildren().stream()
                .map(node -> new SubjectNodeWrapper(node, depth + 1))
                .collect(Collectors.toList());
        size = 1 + children.stream().mapToInt(c -> c.size).sum();
        height = 1 + children.stream().mapToInt(c -> c.height).max().orElse(-1);
    }

    /**
     * Obtains a random node at a given depth with a minimum size
     *
     * @param minDepth The minimum depth of the node
     * @param maxDepth The maximum depth of the node
     * @param minSize The minimum ratio of the maximum node size to consider in the output
     * @return A node fulfilling the properties, or null if no such node exists
     */
    public SubjectNode getRandom(int minDepth, int maxDepth, double minSize) {
        List<SubjectNodeWrapper> candidates = getAtDepth(minDepth, maxDepth).collect(Collectors.toList());

        // Obtain maximum size of any subtree
        int maxSize = candidates.stream().mapToInt(n -> n.size).max().orElse(0);

        // Get a random node fulfilling the size bound
        return RandomUtils.item(candidates.stream().filter(n -> n.size >= minSize * maxSize).map(n -> n.element));
    }

    private Stream<SubjectNodeWrapper> getAtDepth(int minDepth, int maxDepth) {
        if (depth > maxDepth) {
            return Stream.empty();
        }

        Stream<SubjectNodeWrapper> matchingDescendents = children.stream().flatMap(child -> child.getAtDepth(minDepth, maxDepth));
        return depth >= minDepth ? Stream.concat(Stream.of(this), matchingDescendents) : matchingDescendents;
    }
}
