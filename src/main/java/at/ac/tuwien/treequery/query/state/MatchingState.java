package at.ac.tuwien.treequery.query.state;

import at.ac.tuwien.treequery.subject.SubjectNode;

import java.util.Objects;
import java.util.stream.Stream;

/**
 * This class contains the current matching state and provides methods to find candidates for the next matching steps
 */
public class MatchingState {

    private final NodeReferences references;
    private final LinkedSubjectNode root;
    private final LinkedSubjectNode element;

    /**
     * Build a stream of (empty) matching states for a root node
     *
     * @param node The root node
     * @return A stream of empty matching states
     */
    public static Stream<MatchingState> fromSubjectNode(SubjectNode node) {
        return node.getMatchingTargets()
                .map(LinkedSubjectNode::new)
                .map(target -> new MatchingState(NodeReferences.EMPTY, target, target.getFirstChild()));
    }

    private MatchingState(NodeReferences references, LinkedSubjectNode root, LinkedSubjectNode element) {
        this.references = references;
        this.root = root;
        this.element = element;
    }

    /**
     * Get the references of the current matching state
     *
     * @return An instance containing a map references
     */
    public NodeReferences getReferences() {
        return references;
    }

    /**
     * Get the next element to be considered for matching
     *
     * @return A nullable element
     */
    public LinkedSubjectNode getElement() {
        return element;
    }

    /**
     * Get a stream of candidate elements starting at the current element
     *
     * @return A (possibly empty) stream of candidate elements
     */
    public Stream<LinkedSubjectNode> streamWithin() {
        return element != null ? element.within(root) : Stream.empty();
    }

    /**
     * Get a stream of candidate elements that are direct children of the parent
     *
     * @return A (possibly empty) stream of candidate elements
     */
    public Stream<LinkedSubjectNode> streamDirectChildren() {
        LinkedSubjectNode start = element != null ? element.getDirectChildOf(root) : null;
        return start != null ? start.neighbors() : Stream.empty();
    }

    /**
     * Derive a new state set to the next neighbor of the given element
     *
     * @param element A non-null element
     * @return A new state instance
     */
    public MatchingState neighborOf(LinkedSubjectNode element) {
        return withElement(element.getNeighborWithin(root));
    }

    /**
     * Derive a new state set to the next direct child of the parent
     *
     * @return A new state instance
     */
    public MatchingState withDirectChild() {
        return element != null ? withElement(element.getDirectChildOf(root)) : this;
    }

    /**
     * Derive a new state instance set to the same element as the given state
     *
     * @param start A state instance from which the element is copied
     * @return A new state instance
     */
    public MatchingState withStart(MatchingState start) {
        return withElement(start.element);
    }

    /**
     * Derive a new state instance set to the maximum of the current element and the given one
     *
     * @param that A state instance with another element
     * @return A new state instance
     */
    public MatchingState withMaxElement(MatchingState that) {
        if (this.element == null || that.element == null) {
            return withElement(null);
        }
        return withElement(this.element.compareTo(that.element) > 0 ? this.element : that.element);
    }

    /**
     * Derive a new state instance with updated references
     *
     * @param references The new references
     * @return A new state instance
     */
    public MatchingState withReferences(NodeReferences references) {
        return !this.references.equals(references) ? new MatchingState(references, root, element) : this;
    }

    /**
     * Derive a new state instance with updated references and root
     *
     * @param reference The key for the new reference
     * @param element The element stored as reference and used as new root
     * @return A new state instance
     */
    public MatchingState buildChildState(String reference, LinkedSubjectNode element) {
        return new MatchingState(references.withReference(reference, element.node()), element, element.getFirstChild());
    }

    /**
     * Derive a new state instance with a new element
     *
     * @param element The new element
     * @return A new state instance if the element changed
     */
    private MatchingState withElement(LinkedSubjectNode element) {
        return this.element != element ? new MatchingState(references, root, element) : this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        MatchingState that = (MatchingState) o;
        return Objects.equals(root, that.root)
                && Objects.equals(element, that.element)
                && Objects.equals(references, that.references);
    }

    @Override
    public int hashCode() {
        return Objects.hash(root, element, references);
    }

    @Override
    public String toString() {
        return String.format("State[%s]", element != null ? element.node() : null);
    }
}
