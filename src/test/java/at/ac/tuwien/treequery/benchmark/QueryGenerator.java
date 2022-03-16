package at.ac.tuwien.treequery.benchmark;

import static at.ac.tuwien.treequery.benchmark.SubjectNodeSelection.FILTER_ALL;
import static at.ac.tuwien.treequery.benchmark.SubjectNodeSelection.FILTER_REPRESENTATIVE;
import static at.ac.tuwien.treequery.benchmark.SubjectNodeSelection.FILTER_SELECTED;

import at.ac.tuwien.treequery.builder.QueryNodeBuilder;
import at.ac.tuwien.treequery.builder.QueryNodeBuilder.ContainerQueryNodeBuilder;
import at.ac.tuwien.treequery.builder.QueryNodeBuilder.SingleQueryNodeBuilder;
import at.ac.tuwien.treequery.query.QueryNode;
import at.ac.tuwien.treequery.subject.SubjectNode;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * This class is used for generating queries for subject trees
 */
class QueryGenerator {

    private final double probSubtree;
    private final double probNode;
    private final double probProperty;

    /**
     * Creates a new query generator
     *
     * @param probSubtree The probability that some node in a subtree of the subject is included in the query
     * @param probNode The probability that an individual node in the subject tree is included in the query
     * @param probProperty The probability that a property of a selected node is added to the query
     */
    public QueryGenerator(double probSubtree, double probNode, double probProperty) {
        this.probSubtree = probSubtree;
        this.probNode = probNode;
        this.probProperty = probProperty;
    }

    /**
     * Generates a selection of nodes to include in the query
     *
     * @param start The node in the subject tree where the query starts
     * @return A tree of node selections
     */
    public SubjectNodeSelection generateSelection(SubjectNode start) {
        return new SubjectNodeSelection(start, false, true, true, selectChildren(start));
    }

    private List<SubjectNodeSelection> selectChildren(SubjectNode node) {
        List<SubjectNodeSelection> children = new LinkedList<>();

        for (SubjectNode child : node.getChildren()) {
            List<SubjectNode> selected = selectFromSubtree(child).collect(Collectors.toList());

            boolean subtreeSelected;
            if (selected.isEmpty()) {
                // Add the child itself, but set subtree as "not chosen"
                subtreeSelected = false;
                selected = Collections.singletonList(child);
            } else {
                subtreeSelected = RandomUtils.chance(probSubtree);
            }

            SubjectNode representative = RandomUtils.item(selected);

            for (SubjectNode item : selected) {
                children.add(new SubjectNodeSelection(item, item == child, subtreeSelected, item == representative, selectChildren(item)));
            }
        }

        return children;
    }

    private Stream<SubjectNode> selectFromSubtree(SubjectNode node) {
        return RandomUtils.chance(probNode)
                ? Stream.of(node)
                : node.getChildren().stream().flatMap(this::selectFromSubtree);
    }

    /**
     * Creates a query where exactly one node from each subtree is chosen
     *
     * @param node The root node of the node selection
     * @param mode The container mode to set on the children
     * @param setDirect Whether the "direct" property should be set on nodes
     * @param size The size of the generated query
     * @return The root of the query, or null if the selection is not large enough
     */
    public QueryNode buildAllSubtrees(SubjectNodeSelection node, Mode mode, boolean setDirect, int size) {
        return build(node, mode, setDirect, FILTER_REPRESENTATIVE, false, size);
    }

    /**
     * Creates a query where only nodes from selected subtrees are added
     *
     * @param node The root node of the node selection
     * @param mode The container mode to set on the children
     * @param setDirect Whether the "direct" property should be set on nodes
     * @param size The size of the generated query
     * @return The root of the query, or null if the selection is not large enough
     */
    public QueryNode buildSelectedSubtrees(SubjectNodeSelection node, Mode mode, boolean setDirect, int size) {
        return build(node, mode, setDirect, FILTER_SELECTED, false, size);
    }

    /**
     * Creates a query where all subtrees are added in a random order
     *
     * @param node The root node of the node selection
     * @param mode The container mode to set on the children
     * @param setDirect Whether the "direct" property should be set on nodes
     * @param size The size of the generated query
     * @return The root of the query, or null if the selection is not large enough
     */
    public QueryNode buildShuffled(SubjectNodeSelection node, Mode mode, boolean setDirect, int size) {
        return build(node, mode, setDirect, FILTER_ALL, true, size);
    }

    private QueryNode build(SubjectNodeSelection node, Mode mode, boolean setDirect,
            Predicate<SubjectNodeSelection> filter, boolean shuffle, int size) {
        if (node.count(filter) < size) {
            // Selection not large enough, ignore
            return null;
        }

        ContainerQueryNodeBuilder childrenBuilder = QueryNodeBuilder.container();
        List<SubjectNodeSelection> children = node.children.stream()
                .filter(filter)
                .collect(RandomUtils.toList(shuffle));

        if (size > children.size()) {
            // Budget left to include all children
            // Calculate ratio of each subtree that is included
            int actualSize = node.count(filter) - children.size() - 1;
            int allowedSize = size - children.size() - 1;
            double includeRatio = (double) allowedSize / actualSize;

            // Use "cascade rounding": good enough for these purposes and efficient to calculate
            double fractionalAgg = 0;
            int integralAgg = 0;

            for (SubjectNodeSelection child : children) {
                // Calculate the limit for the subtree
                fractionalAgg += includeRatio * (child.count(filter) - 1);
                int childLimit = (int) Math.round(fractionalAgg) - integralAgg;
                integralAgg += childLimit;

                // Build the child node
                childrenBuilder.child(build(child, mode, setDirect, filter, shuffle, childLimit + 1));
            }
        } else if (size > 1) {
            // Budget left to include some children
            for (int i = 0; i < size - 1; i++) {
                childrenBuilder.child(build(children.get(i), mode, setDirect, filter, shuffle, 1));
            }

            if (mode == Mode.EXACT) {
                // Append a dummy container for the missing children if using exact matching
                childrenBuilder.child(QueryNodeBuilder.container().ordered());
            }
        } else {
            // No budget left to include children: Just use an empty ordered container
            mode = Mode.ORDERED;
        }

        // Build the query for the node itself
        return builder(node.element, node.direct && setDirect)
                .children(mode.build.apply(childrenBuilder))
                .build();
    }

    private SingleQueryNodeBuilder builder(SubjectNode node, boolean direct) {
        SingleQueryNodeBuilder singleBuilder = QueryNodeBuilder.single(node.getType());
        if (direct) {
            singleBuilder.direct();
        }
        for (Entry<String, Object> entry : node.getProperties().entrySet()) {
            if (RandomUtils.chance(probProperty)) {
                singleBuilder.prop(entry.getKey(), entry.getValue());
            }
        }
        return singleBuilder;
    }

    public enum Mode {
        EXACT(ContainerQueryNodeBuilder::exact),
        ORDERED(ContainerQueryNodeBuilder::ordered),
        UNORDERED(ContainerQueryNodeBuilder::unordered),
        ANY(ContainerQueryNodeBuilder::any),
        OPTIONAL(ContainerQueryNodeBuilder::optional);

        private final Function<ContainerQueryNodeBuilder, QueryNode> build;

        Mode(Function<ContainerQueryNodeBuilder, QueryNode> build) {
            this.build = build;
        }
    }
}
