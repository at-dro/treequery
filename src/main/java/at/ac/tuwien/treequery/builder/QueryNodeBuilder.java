package at.ac.tuwien.treequery.builder;

import at.ac.tuwien.treequery.query.nodes.AllQueryNode;
import at.ac.tuwien.treequery.query.nodes.AnyQueryNode;
import at.ac.tuwien.treequery.query.nodes.ExactQueryNode;
import at.ac.tuwien.treequery.query.nodes.QueryNode;
import at.ac.tuwien.treequery.query.nodes.SingleQueryNode;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class QueryNodeBuilder {

    public static SingleQueryNodeBuilder single(String type) {
        return new SingleQueryNodeBuilder(type);
    }

    public static ContainerQueryNodeBuilder container() {
        return new ContainerQueryNodeBuilder();
    }

    public static class SingleQueryNodeBuilder {

        private final String type;
        private final PropertiesBuilder properties = new PropertiesBuilder();
        private QueryNode children;
        private boolean direct;
        private String reference;

        private SingleQueryNodeBuilder(String type) {
            this.type = type;
        }

        public SingleQueryNodeBuilder prop(String key, Object value) {
            properties.set(key, value);
            return this;
        }

        public SingleQueryNodeBuilder children(QueryNode children) {
            this.children = children;
            return this;
        }

        public SingleQueryNodeBuilder direct() {
            direct = true;
            return this;
        }

        public SingleQueryNodeBuilder ref(String reference) {
            this.reference = reference;
            return this;
        }

        public QueryNode build() {
            QueryNode childrenContainer = children == null ? new AllQueryNode(Collections.emptyList(), true) : children;
            return new SingleQueryNode(type, properties.build(), childrenContainer, direct, reference);
        }
    }

    public static class ContainerQueryNodeBuilder {

        private final List<QueryNode> children = new LinkedList<>();

        public ContainerQueryNodeBuilder child(QueryNode child) {
            children.add(child);
            return this;
        }

        public QueryNode exact() {
            return new ExactQueryNode(children);
        }

        public QueryNode ordered() {
            return new AllQueryNode(children, true);
        }

        public QueryNode unordered() {
            return new AllQueryNode(children, false);
        }

        public QueryNode any() {
            return new AnyQueryNode(children, false);
        }

        public QueryNode optional() {
            return new AnyQueryNode(children, true);
        }
    }
}
