#!/usr/bin/env python3
import csv
import sys
from operator import itemgetter

import matplotlib.pyplot as plt

sum_fields = ['init time', 'warmup time', 'total query time', 'run count', 'average time']


def aggregate_runtimes(filename: str, key_fields: list[str]) -> list[dict[str, int]]:
    aggregated = {}
    with open(filename) as csvfile:
        csvreader = csv.DictReader(csvfile, delimiter=',')
        for row in csvreader:
            # Build key from corresponding fields
            row_key = "_".join(row[field] for field in key_fields)

            if row_key not in aggregated:
                # First result for that key: Initialize the row values
                aggregated[row_key] = {}
                for field in key_fields:
                    try:
                        aggregated[row_key][field] = int(row[field])
                    except ValueError:
                        aggregated[row_key][field] = row[field]

                aggregated[row_key]['count'] = 0
                for field in sum_fields:
                    aggregated[row_key]['sum ' + field] = 0
                    aggregated[row_key][field] = 0

            # Increase count and add all sum values
            aggregated[row_key]['count'] += 1
            for field in sum_fields:
                if row[field]:
                    aggregated[row_key]['sum ' + field] += int(row[field])

    # Throw away keys and sort by key fields
    result = list(aggregated.values())
    result.sort(key=itemgetter(*key_fields))

    # Calculate averages
    for row in result:
        for field in sum_fields:
            # Add each sum and average value
            row[field] = round(row['sum ' + field] / row['count'])

    return result


def create_plot(plot_key_fields: list[str], line_key_fields: list[str], x_field: str, y_field: str, data: list[dict[str, int]],
                output_dir: str):
    # Extract all lines from the data
    lines = {}
    for row in data:
        if row['success'] != 'OK':
            # Ignore any failed results
            continue

        # Group into subplots and lines
        plot_key = ", ".join(field + " " + str(row[field]) for field in plot_key_fields)
        line_key = ", ".join(field + " " + str(row[field]) for field in line_key_fields)
        if plot_key not in lines:
            lines[plot_key] = {}
        if line_key not in lines[plot_key]:
            lines[plot_key][line_key] = ([], [])

        # Append data point to correct line
        lines[plot_key][line_key][0].append(row[x_field])
        lines[plot_key][line_key][1].append(row[y_field] / 1000)

    # Create common plot
    plt.style.use('seaborn-whitegrid')
    fig, axs = plt.subplots(1, len(lines), sharey='all')
    if len(lines) <= 1:
        axs = [axs]

    # Hack for adding common label to axis
    # see https://stackoverflow.com/questions/6963035/pyplot-common-axes-labels-for-subplots/36542971#36542971
    fig.add_subplot(111, frameon=False)
    plt.tick_params(labelcolor='none', top=False, bottom=False, left=False, right=False)
    plt.grid(False)
    plt.xlabel(x_field)
    plt.ylabel(y_field + ' [ms]')

    # Create subplot for each group
    ax_index = 0
    for title, data in lines.items():
        ax = axs[ax_index]

        ax.set_title(title)
        for label, values in data.items():
            ax.plot(values[0], values[1], '-o', label=label)

        if x_field == 'subj size':
            ax.set_xscale('log')
        ax.set_yscale('log')

        ax_index += 1

    # Add common legend
    handles, labels = axs[0].get_legend_handles_labels()
    fig.legend(handles, labels, loc='upper center', ncol=len(handles))

    # Set figure size
    fig.set_size_inches(10, 5)

    # Save to file
    filename = output_dir + '/'
    for field in plot_key_fields:
        filename += field.replace(' ', '') + '_'
    filename += x_field.replace(' ', '') + '_' + y_field.replace(' ', '') + '.svg'
    plt.savefig(filename)


def get_header(key_fields: list[str]) -> str:
    # Build the CSV header line for given key fields
    cols = []
    cols.extend(key_fields)
    cols.append('count')
    for field in sum_fields:
        # Add column for each sum and the average value
        cols.append('sum ' + field)
        cols.append(field)

    return ','.join(cols)


def get_row_csv(data: dict[str, int]) -> str:
    # Build the CSV row for the given data row
    return ','.join(str(val) for val in data.values())


def write_aggregated(key_fields: list[str], data: list[dict[str, int]], filename: str):
    # Write the aggregated data to a CSV file
    with open(filename, 'w') as f:
        f.write(get_header(key_fields) + '\n')
        for row in data:
            f.write(get_row_csv(row) + '\n')


def main(input_file: str, output_dir: str):
    for key in None, 'subj type', 'query start', 'direct set', 'query type', 'container mode':
        key_fields = ['subj size', 'query size', 'success']
        plot_key_fields = []
        output_csv = output_dir + '/'

        # Build key field arrays and output name for the given key
        if key is not None:
            key_fields.append(key)
            plot_key_fields.append(key)
            output_csv += key.replace(' ', '') + '_'
        output_csv += 'aggregated.csv'

        # Aggregate the input data
        aggregated = aggregate_runtimes(input_file, key_fields)

        # Write the aggregated data to the CSV file
        write_aggregated(key_fields, aggregated, output_csv)

        # Create plots based on query size and subject size
        create_plot(plot_key_fields, ['query size'], 'subj size', 'average time', aggregated, output_dir)
        create_plot(plot_key_fields, ['subj size'], 'query size', 'average time', aggregated, output_dir)


if __name__ == '__main__':
    if len(sys.argv) <= 2:
        print('Usage: analyze.py input-file.csv output-dir')
        exit(0)

    main(sys.argv[1], sys.argv[2])
