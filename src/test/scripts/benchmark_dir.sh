#!/bin/bash

# This script executes all queries in a specified directory
if [[ -z "$1" ]] || [[ -z "$2" ]]; then
  echo "Usage: benchmark_dir.sh input-dir output-csv"
  exit 1
fi

# Read the arguments
subj_dir=$1/subj
query_dir=$1/query
out_csv=$2

# Find the script for executing a single query
script=$(readlink -f "$(dirname $0)/benchmark_run.sh")

# Print the CSV header
echo -n 'subj size,subj type,subj param,query start,query size,direct set,query type,container mode,' > "$out_csv"
echo 'init time,warmup time,total query time,run count,average time,success' >> "$out_csv"

# Make sure Ctrl+C kills the whole loop, not just a single query
trap trap_int SIGINT
function trap_int {
    exit 2
}

# Find all queries in the directory
shopt -s nullglob
for query in $query_dir/*.xml; do
  # Find the corresponding subject file
  subj="$subj_dir/$(cut -d'_' -f1-3 <<< $(basename "$query")).xml"

  # Execute the benchmark script
  "$script" "$subj" "$query" >>  "$out_csv"
done
