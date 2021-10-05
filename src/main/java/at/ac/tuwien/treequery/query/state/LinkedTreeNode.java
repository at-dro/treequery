package at.ac.tuwien.treequery.query.state;

import at.ac.tuwien.treequery.tree.TreeNode;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * This class wraps tree nodes in a linked structure to allow iterating along the tree
 */
public class LinkedTreeNode implements Comparable<LinkedTreeNode> {

    private final TreeNode node;
    private final int[] path;
    private final LinkedTreeNode parent;
    private final List<LinkedTreeNode> children;

    public LinkedTreeNode(TreeNode node) {
        this(node, null, 0);
    }

    private LinkedTreeNode(TreeNode node, LinkedTreeNode parent, int index) {
        this.node = node;
        this.parent = parent;

        // Store the path to this node in an array to allow faster comparison
        if (parent != null) {
            path = Arrays.copyOf(parent.path, parent.path.length + 1);
            path[path.length - 1] = index;
        } else {
            path = new int[0];
        }

        // Recursively create linked nodes for the children
        List<TreeNode> children = node.getChildren().stream()
                .flatMap(TreeNode::getMatchingTargets)
                .collect(Collectors.toList());
        this.children = IntStream.range(0, children.size())
                .mapToObj(i -> new LinkedTreeNode(children.get(i), this, i))
                .collect(Collectors.toList());
    }

    /**
     * Get the actual node for this instance
     *
     * @return The non-null node
     */
    public TreeNode node() {
        return node;
    }

    /**
     * Get a stream of nodes within a given ancestor, starting at this node
     *
     * @param ancestor The ancestor
     * @return A stream of ordered nodes within (but excluding) the ancestor
     */
    public Stream<LinkedTreeNode> within(LinkedTreeNode ancestor) {
        return Stream.iterate(this, Objects::nonNull, e -> e.getNext(ancestor));
    }

    /**
     * Get a stream of neighbors, starting at this node
     *
     * @return A stream of ordered nodes at the same level of this node
     */
    public Stream<LinkedTreeNode> neighbors() {
        if (parent == null) {
            return Stream.of(this);
        }
        return parent.children.stream().skip(path[path.length - 1]);
    }

    /**
     * Get the first child of this node
     *
     * @return The first child, or null if no children exist
     */
    public LinkedTreeNode getFirstChild() {
        return !children.isEmpty() ? children.get(0) : null;
    }

    /**
     * Get a direct child of the given parent, starting at this node
     *
     * @param parent The parent of the result
     * @return A node that is a direct child of parent, or null if none could be found
     */
    public LinkedTreeNode getDirectChildOf(LinkedTreeNode parent) {
        if (this.parent == parent) {
            // This node already is a direct child
            return this;
        }

        // Look for the ancestor that is a direct child and go to its next neighbor
        LinkedTreeNode ancestor = getAncestor(parent);
        return ancestor != null ? ancestor.getDirectNeighbor() : null;
    }

    /**
     * Get a neighbor of this node's level or higher up within the given ancestor
     *
     * @param ancestor The common ancestor of the subtree to check
     * @return A neighbor of this node, or null if none exists
     */
    public LinkedTreeNode getNeighborWithin(LinkedTreeNode ancestor) {
        // Move up the tree until we find a neighbor
        LinkedTreeNode current = this;
        while (current != null && current != ancestor) {
            LinkedTreeNode neighbor = current.getDirectNeighbor();
            if (neighbor != null) {
                // Found a neighbor: Return it
                return neighbor;
            }
            // Move up another level
            current = current.parent;
        }

        // Reached either the root node or the given ancestor
        return null;
    }

    /**
     * Get the next neighbor of this node
     *
     * @return The next node on the same level, or null if none exists
     */
    public LinkedTreeNode getDirectNeighbor() {
        if (parent == null) {
            // Root node has no neighbors
            return null;
        }

        // Find the next child in the parent node
        int index = path[path.length - 1] + 1;
        return parent.children.size() > index ? parent.children.get(index) : null;
    }

    /**
     * Find the next node within the subtree rooted at the given ancestor
     *
     * @param ancestor The root of the subtree
     * @return The next node or null if it does not exist
     */
    private LinkedTreeNode getNext(LinkedTreeNode ancestor) {
        // Use the first child if it exists, or move up the tree to find a neighbor
        return children.isEmpty() ? getNeighborWithin(ancestor) : children.get(0);
    }

    /**
     * Find the ancestor of this node that is a direct child of the given parent
     *
     * @param parent The parent to look for
     * @return The direct child of the parent, or null if this node is not a descendent of the given parent
     */
    private LinkedTreeNode getAncestor(LinkedTreeNode parent) {
        LinkedTreeNode current = this;
        while (current.parent != null) {
            if (current.parent == parent) {
                return current;
            }
            current = current.parent;
        }
        return null;
    }

    @Override
    public int compareTo(LinkedTreeNode that) {
        return this != that ? Arrays.compare(this.path, that.path) : 0;
    }
}
