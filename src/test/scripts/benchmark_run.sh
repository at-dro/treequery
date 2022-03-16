#!/bin/bash

# This script executes a single query
if [[ -z "$1" ]] || [[ -z "$2" ]]; then
  echo "Usage: benchmark_run.sh subj_path query_path"
  exit 1
fi

# This needs to contain the compiled test classes, i.e., run `mvn test-compile` or a later phase first
target=$(readlink -f "$(dirname $0)/../../../target")

# Read the filenames
subj_file=$1
query_file=$2

# Read the query parameters from the filename
query=$(basename "$query_file")
IFS='_' read -r -a params <<< "${query%.xml}"
csv_prefix="$(printf '%.f' "${params[0]}"),${params[1]},${params[2]},${params[3]:5},${params[4]:4},${params[5]:3},${params[6]},${params[7]}"

# Execute the benchmark Java application (with increased stack size)
java -Xss2m -cp "${target}/classes:${target}/test-classes" at/ac/tuwien/treequery/benchmark/BenchmarkApp \
        "$subj_file" "$query_file" "$csv_prefix"

# Output a CSV line if the benchmark failed
status=$?
if [[ $status -ne 0 ]]; then
  echo "$csv_prefix,,,,,,Failed (${status})"
fi
