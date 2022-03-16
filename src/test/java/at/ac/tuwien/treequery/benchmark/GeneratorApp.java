package at.ac.tuwien.treequery.benchmark;

import java.io.IOException;
import java.nio.file.Path;

/**
 * This is the entry point for generating subjects and queries
 */
public class GeneratorApp {

    /**
     * Generates the subjects and queries
     *
     * @param args The path to the output director
     * @throws IOException Thrown if a subject or query could not be exported
     */
    public static void main(String[] args) throws IOException {
        if (args.length != 1) {
            System.err.println("Output path required.");
            System.exit(1);
        }

        new GeneratorRunner(Path.of(args[0])).run();
    }
}
