package at.ac.tuwien.treequery.builder;

import at.ac.tuwien.treequery.annotation.PublicApi;
import at.ac.tuwien.treequery.query.AllQueryNode;
import at.ac.tuwien.treequery.query.AnyQueryNode;
import at.ac.tuwien.treequery.query.ExactQueryNode;
import at.ac.tuwien.treequery.query.QueryNode;
import at.ac.tuwien.treequery.query.SingleQueryNode;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * This is a utility class for building a {@link QueryNode} instance.
 */
@PublicApi
public class QueryNodeBuilder {

    /**
     * Creates a builder for a single query node
     *
     * @param type The queried type, which may be null
     * @return A new builder instance
     */
    @PublicApi
    public static SingleQueryNodeBuilder single(String type) {
        return new SingleQueryNodeBuilder(type);
    }

    /**
     * Creates a builder for a container of query nodes
     *
     * @return A new builder instance
     */
    @PublicApi
    public static ContainerQueryNodeBuilder container() {
        return new ContainerQueryNodeBuilder();
    }

    /**
     * A builder for single query nodes.
     */
    @PublicApi
    public static class SingleQueryNodeBuilder {

        private final String type;
        private final PropertiesBuilder properties = PropertiesBuilder.props();
        private QueryNode children;
        private boolean direct;
        private String reference;

        private SingleQueryNodeBuilder(String type) {
            this.type = type;
        }

        /**
         * Adds a property to the query node.
         * If the property already exists the old value is overwritten.
         *
         * @param key The key of the property.
         * @param value The new value for the property.
         * @return This builder instance
         */
        @PublicApi
        public SingleQueryNodeBuilder prop(String key, Object value) {
            properties.set(key, value);
            return this;
        }

        /**
         * Adds the children for the query node.
         * This is normally a container query node wrapping the children.
         *
         * @param children A query node containing the children subtree
         * @return This builder instance
         */
        @PublicApi
        public SingleQueryNodeBuilder children(QueryNode children) {
            this.children = children;
            return this;
        }

        /**
         * Sets the query mode to {@code direct}.
         * Such a query node will only match immediate children of the query parent's match.
         *
         * @return This builder instance
         */
        @PublicApi
        public SingleQueryNodeBuilder direct() {
            direct = true;
            return this;
        }

        /**
         * Associates a named reference with this query node
         *
         * @param reference The reference name
         * @return This builder instance
         */
        @PublicApi
        public SingleQueryNodeBuilder ref(String reference) {
            this.reference = reference;
            return this;
        }

        /**
         * Obtains the query node.
         * This builder instance should not be used anymore after this method was called.
         *
         * @return The finalized single query node
         */
        @PublicApi
        public QueryNode build() {
            QueryNode childrenContainer = children == null ? new AllQueryNode(Collections.emptyList(), true) : children;
            return new SingleQueryNode(type, properties.build(), childrenContainer, direct, reference);
        }
    }

    /**
     * A builder for container query nodes.
     */
    @PublicApi
    public static class ContainerQueryNodeBuilder {

        private final List<QueryNode> children = new LinkedList<>();

        /**
         * Appends a child query node to the container
         *
         * @param child The child query node, which can be a single node or a container
         * @return This builder instance
         */
        @PublicApi
        public ContainerQueryNodeBuilder child(QueryNode child) {
            children.add(child);
            return this;
        }

        /**
         * Obtains an exact container node with the given children.
         * This builder instance should not be used anymore after this method was called.
         *
         * @return The finalized container query node with exact matching
         */
        @PublicApi
        public QueryNode exact() {
            return new ExactQueryNode(children);
        }

        /**
         * Obtains an ordered container node with the given children.
         * This builder instance should not be used anymore after this method was called.
         *
         * @return The finalized container query node with ordered matching
         */
        @PublicApi
        public QueryNode ordered() {
            return new AllQueryNode(children, true);
        }

        /**
         * Obtains an unordered container node with the given children.
         * This builder instance should not be used anymore after this method was called.
         *
         * @return The finalized container query node with unordered matching
         */
        @PublicApi
        public QueryNode unordered() {
            return new AllQueryNode(children, false);
        }

        /**
         * Obtains an "any" container node with the given children.
         * This builder instance should not be used anymore after this method was called.
         *
         * @return The finalized container query node with "any" matching
         */
        @PublicApi
        public QueryNode any() {
            return new AnyQueryNode(children, false);
        }

        /**
         * Obtains an "optional" container node with the given children.
         * This builder instance should not be used anymore after this method was called.
         *
         * @return The finalized container query node with "optional" matching
         */
        @PublicApi
        public QueryNode optional() {
            return new AnyQueryNode(children, true);
        }
    }
}
