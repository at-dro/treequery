package at.ac.tuwien.treequery.benchmark;

import at.ac.tuwien.treequery.subject.SubjectNode;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

/**
 * This class is used to store a selection of nodes from a subtree
 */
class SubjectNodeSelection {

    public static final Predicate<SubjectNodeSelection> FILTER_ALL = node -> true;
    public static final Predicate<SubjectNodeSelection> FILTER_SELECTED = node -> node.selected && node.representative;
    public static final Predicate<SubjectNodeSelection> FILTER_REPRESENTATIVE = node -> node.representative;

    public final SubjectNode element;

    public final boolean direct;
    private final boolean selected;
    private final boolean representative;

    public final List<SubjectNodeSelection> children;

    private final Map<Predicate<SubjectNodeSelection>, Integer> counts = new HashMap<>();

    public SubjectNodeSelection(SubjectNode element, boolean direct, boolean selected, boolean representative,
            List<SubjectNodeSelection> children) {
        this.element = element;
        this.direct = direct;
        this.selected = selected;
        this.representative = representative;
        this.children = children;
    }

    /**
     * Calculates the total subtree size for a given filter
     *
     * @param filter The filter determining which nodes to include
     * @return The count, which is zero if this node itself does not match the filter
     */
    public int count(Predicate<SubjectNodeSelection> filter) {
        if (!filter.test(this)) {
            return 0;
        }

        if (!counts.containsKey(filter)) {
            counts.put(filter, 1 + children.stream().mapToInt(child -> child.count(filter)).sum());
        }

        return counts.get(filter);
    }
}
