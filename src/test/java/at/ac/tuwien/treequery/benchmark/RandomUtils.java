package at.ac.tuwien.treequery.benchmark;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * This class contains static helpers for generating random data
 */
class RandomUtils {

    /**
     * Use a Random instance with a fixed seed to make results reproducible
     */
    private static final Random RANDOM = new Random(0);

    /**
     * Generates a random integer
     *
     * @param bound The exclusive upper bound
     * @return An integer in [0, {@code bound}[
     */
    public static int integer(int bound) {
        return RANDOM.nextInt(bound);
    }

    /**
     * Generates a random boolean with a given probability
     *
     * @param probability The probability for {@code true}
     * @return A random boolean
     */
    public static boolean chance(double probability) {
        return RANDOM.nextDouble() < probability;
    }

    /**
     * Generates a random numeric string
     *
     * @param prefix A prefix for the generated string
     * @param length The length of the random suffix
     * @return A string consisting of {@code prefix} and {@code length} digits
     */
    public static String string(String prefix, int length) {
        return prefix + string(length);
    }

    /**
     * Generates a random numeric string
     *
     * @param length The length of the random string
     * @return A string consisting of {@code length} digits
     */
    public static String string(int length) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < length; i++) {
            result.append(RANDOM.nextInt(10));
        }
        return result.toString();
    }

    /**
     * Selects a random item from the given list
     *
     * @param elements The elements to choose from
     * @return A random list item, or {@code null} if the list is empty
     */
    public static <T> T item(List<T> elements) {
        return elements.isEmpty() ? null : elements.get(integer(elements.size()));
    }

    /**
     * Selects a random item from the given stream
     * <br>
     * This is a terminal stream operation.
     *
     * @param elements A stream of elements to choose from
     * @return A random item, or {@code null} if the stream is empty
     */
    public static <T> T item(Stream<T> elements) {
        return item(elements.collect(Collectors.toList()));
    }

    /**
     * Creates a collector for obtaining list from a stream
     *
     * @param shuffle Whether the result list should be shuffled
     * @return A collector for obtaining a list of stream elements
     */
    public static <T> Collector<T, ?, List<T>> toList(boolean shuffle) {
        return shuffle ? Collectors.collectingAndThen(
                Collectors.toCollection(ArrayList::new),
                list -> {
                    Collections.shuffle(list, RANDOM);
                    return list;
                }
        ) : Collectors.toList();
    }
}
