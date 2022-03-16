package at.ac.tuwien.treequery.matching;

import at.ac.tuwien.treequery.annotation.InternalApi;

import java.util.LinkedList;
import java.util.List;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.Consumer;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * This class wraps a stream and allows replaying items without loading them again
 *
 * @param <T>
 */
@InternalApi
public class StreamCache<T> {

    private final CachingIterator<T> iterator;

    /**
     * Wrap a stream
     *
     * @param source The source stream
     */
    public StreamCache(Stream<T> source) {
        iterator = new CachingIterator<>(source.spliterator());
    }

    /**
     * Creates a copy of the stream for all items in the original stream
     *
     * @return A new stream instance
     */
    public Stream<T> get() {
        return Stream.concat(iterator.cache.stream(), StreamSupport.stream(iterator, false));
    }

    private static class CachingIterator<T> extends Spliterators.AbstractSpliterator<T> {

        private final Spliterator<T> iterator;
        private final List<T> cache = new LinkedList<>();

        CachingIterator(Spliterator<T> iterator) {
            super(iterator.estimateSize(), iterator.characteristics());
            this.iterator = iterator;
        }

        @Override
        public boolean tryAdvance(Consumer<? super T> action) {
            return iterator.tryAdvance(item -> {
                // Item handled: Add it to cache, then apply the original action
                cache.add(item);
                action.accept(item);
            });
        }
    }
}
