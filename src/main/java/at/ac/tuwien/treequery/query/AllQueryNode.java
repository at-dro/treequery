package at.ac.tuwien.treequery.query;

import at.ac.tuwien.treequery.annotation.InternalApi;
import at.ac.tuwien.treequery.builder.QueryNodeBuilder;
import at.ac.tuwien.treequery.matching.MatchingState;
import at.ac.tuwien.treequery.matching.StreamCache;
import at.ac.tuwien.treequery.xml.QueryXmlConverter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

/**
 * This class represents an ordered or unordered container query node.
 * <p>
 * Third-party code should not use this class directly, but use the builder or XML converter and the {@link QueryNode} interface.
 */
@InternalApi
public class AllQueryNode extends ContainerQueryNode {

    private final boolean ordered;

    /**
     * Creates a new "ordered" or "unordered" container query node instance.
     * <p>
     * Third-party code should not call this constructor directly, but use the provided builder or XML converter
     *
     * @param children The list of child query nodes in this container
     * @param ordered Flag indicating whether the matching should be ordered
     * @see QueryNodeBuilder#container
     * @see QueryXmlConverter
     */
    public AllQueryNode(List<QueryNode> children, boolean ordered) {
        super(children);
        this.ordered = ordered;
    }

    @Override
    public Stream<MatchingState> findMatches(MatchingState start) {
        Stream<MatchingState> states = Stream.of(start);
        for (QueryNode query : children) {
            // Cache results, so they do not need to be loaded multiple times
            Map<MatchingState, StreamCache<MatchingState>> resultCache = new HashMap<>();
            states = states.flatMap(
                    candidate -> {
                        if (!ordered) {
                            // Unordered case: Start looking from the start state, but then put the later node in the result
                            return runCached(candidate.withStart(start), query, resultCache).map(s -> s.withMaxElement(candidate));
                        } else if (query instanceof ContainerQueryNode) {
                            // Ordered case, but another container: This might return the candidate again, so the filter approach won't work
                            return runCached(candidate, query, resultCache);
                        } else {
                            // Ordered case: Start looking from the start state, but then only keep those after the candidate state
                            return runCached(candidate.withStart(start), query, resultCache).filter(s -> s.isLaterThan(candidate));
                        }
                    }
            ).distinct();
        }
        return states;
    }

    private Stream<MatchingState> runCached(MatchingState state, QueryNode query, Map<MatchingState, StreamCache<MatchingState>> cache) {
        if (!cache.containsKey(state)) {
            // Not yet in cache: Build the result
            cache.put(state, new StreamCache<>(query.findMatches(state)));
        }
        return cache.get(state).get();
    }

    public boolean isOrdered() {
        return ordered;
    }
}
