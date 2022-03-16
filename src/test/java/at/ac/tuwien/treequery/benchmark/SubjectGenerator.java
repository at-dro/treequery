package at.ac.tuwien.treequery.benchmark;

import at.ac.tuwien.treequery.builder.SubjectNodeBuilder;
import at.ac.tuwien.treequery.subject.SubjectNode;

import java.util.List;
import java.util.function.ToIntFunction;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * This class is used for generating subject trees with given characteristics
 */
class SubjectGenerator {

    private final int typeSize;
    private final int maxProperties;
    private final double valueChance;

    /**
     * Creates a new subject generator instance
     *
     * @param typeSize The number of digits of the numeric value in the type string
     * @param maxProperties The maximal number of properties in a node
     * @param valueChance The chance of adding a value string to a node
     */
    public SubjectGenerator(int typeSize, int maxProperties, double valueChance) {
        this.typeSize = typeSize;
        this.maxProperties = maxProperties;
        this.valueChance = valueChance;
    }

    /**
     * Generate a subject tree with a constant number of nodes on each level
     *
     * @param size The total number of nodes
     * @param ratio The ratio height:width
     * @return The root node of a tree with approximately {@code size} nodes
     */
    public SubjectNode rectangle(int size, double ratio) {
        double height = Math.sqrt(size * ratio);
        int width = (int) Math.round(height / ratio);
        return generate((int) Math.round(height), level -> width);
    }

    /**
     * Generate a subject tree with a linear number of nodes on each level
     *
     * @param size The total number of nodes
     * @param factor The multiplier for each level
     * @return The root node of a triangular tree with approximately {@code size} nodes
     */
    public SubjectNode triangle(int size, int factor) {
        int height = (int) Math.round(Math.sqrt(0.25 + 2.0 * size / factor) - 0.5);
        double corr = 2.0 * size / (factor * height * (height + 1));
        return generate(height, level -> (int) Math.round(corr * level * factor));
    }

    /**
     * Generate a subject tree where each node has the same average number of children
     *
     * @param size The total number of nodes
     * @param perNode The number of children per node
     * @return The root node of an exponentially growing tree with approximately {@code size} nodes
     */
    public SubjectNode exponential(int size, int perNode) {
        int height = (int) Math.round(Math.log(size + 1 - (double) size / perNode) / Math.log(perNode));
        double corr = size * (perNode - 1) / (perNode * (Math.pow(perNode, height) - 1));
        return generate(height, level -> (int) Math.round(corr * Math.pow(perNode, level)));
    }

    /**
     * Generate a subject tree
     *
     * @param height The height of the tree
     * @param countForLevel A function for calculating the total number of nodes on a given level
     * @return The root node of the resulting tree
     */
    private SubjectNode generate(int height, ToIntFunction<Integer> countForLevel) {
        Stream<SubjectNode> children = Stream.empty();
        for (int level = height; level > 0; level--) {
            // Determine number of nodes on the current level
            int count = countForLevel.applyAsInt(level);

            // Generate builders for all nodes on the current level
            List<SubjectNodeBuilder> nodes = Stream.generate(this::builder)
                    .limit(count)
                    .collect(Collectors.toList());

            // Randomly distribute the children among the nodes of the current level
            children.forEach(node -> nodes.get(RandomUtils.integer(count)).child(node));

            // Build the nodes and set them as children for the next level
            children = nodes.stream().map(SubjectNodeBuilder::build);
        }

        SubjectNodeBuilder root = SubjectNodeBuilder.type("root");
        children.forEach(root::child);
        return root.build();
    }

    /**
     * Create a builder instance of a node with a type and some properties
     *
     * @return A builder instance
     */
    private SubjectNodeBuilder builder() {
        SubjectNodeBuilder builder = SubjectNodeBuilder.type(RandomUtils.string("node", typeSize));

        int propCount = RandomUtils.integer(maxProperties + 1);
        for (int i = 0; i < propCount; i++) {
            builder.prop(RandomUtils.string("prop", 2), RandomUtils.string("val", 4));
        }
        if (RandomUtils.chance(valueChance)) {
            builder.prop("value", RandomUtils.string("val", 4));
        }

        return builder;
    }
}
