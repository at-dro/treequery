package at.ac.tuwien.treequery.builder;

import at.ac.tuwien.treequery.subject.BaseSubjectNode;
import at.ac.tuwien.treequery.subject.SubjectNode;

import java.util.LinkedList;
import java.util.List;

public class SubjectNodeBuilder {

    private final String type;
    private final PropertiesBuilder properties = PropertiesBuilder.props();
    private final List<SubjectNode> children = new LinkedList<>();

    private SubjectNodeBuilder(String type) {
        this.type = type;
    }

    public static SubjectNodeBuilder type(String type) {
        return new SubjectNodeBuilder(type);
    }

    public SubjectNodeBuilder prop(String key, Object value) {
        properties.set(key, value);
        return this;
    }

    public SubjectNodeBuilder child(SubjectNode child) {
        children.add(child);
        return this;
    }

    public SubjectNode build() {
        return new BaseSubjectNode(type, properties.build(), children);
    }
}
