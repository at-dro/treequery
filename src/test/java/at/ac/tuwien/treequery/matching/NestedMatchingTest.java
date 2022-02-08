package at.ac.tuwien.treequery.matching;

import org.junit.jupiter.params.provider.Arguments;

import java.util.stream.Stream;

class NestedMatchingTest extends XmlMatchingTest {

    public static Stream<Arguments> cases() {
        return Stream.of(
                // Simple nesting
                Arguments.of("subject_nested01a", "nested/nested01", true),
                Arguments.of("subject_nested01b", "nested/nested01", true),
                Arguments.of("subject_nested01c", "nested/nested01", false),
                // Nesting within exact
                Arguments.of("subject_nested02", "nested/nested02a_pos", true),
                Arguments.of("subject_nested02", "nested/nested02b_pos", true),
                Arguments.of("subject_nested02", "nested/nested02c_pos", true),
                Arguments.of("subject_nested02", "nested/nested02d_pos", true),
                Arguments.of("subject_nested02", "nested/nested02a_neg", false),
                Arguments.of("subject_nested02", "nested/nested02b_neg", false),
                Arguments.of("subject_nested02", "nested/nested02c_neg", false),
                Arguments.of("subject_nested02", "nested/nested02d_neg", false)
        );
    }
}
