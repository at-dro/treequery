package at.ac.tuwien.treequery.benchmark;

import java.io.IOException;

/**
 * This is the entry point for benchmarking a query and a given subject
 */
public class BenchmarkApp {

    /**
     * The maximal number of times the query is run
     */
    private static final int MAX_RUNS = 20;

    /**
     * Runs the benchmark
     *
     * @param args The subject path, query path and optional name printed as prefix in the CSV output line (defaults to the query name)
     * @throws IOException Thrown if the subject or query could not be loaded
     */
    public static void main(String[] args) throws IOException {
        if (args.length < 2) {
            System.err.println("Subject and query path required.");
            System.exit(1);
        }

        BenchmarkRunner runner = new BenchmarkRunner(args[0], args[1], args.length >= 3 ? args[2] : args[1]);
        runner.runBenchmark(MAX_RUNS);
        System.out.println(runner.toCsv());
    }
}
