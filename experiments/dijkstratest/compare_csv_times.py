import csv
import sys

# Usage: python compare_csv_times.py file1.csv file2.csv


# Compare standard with bidirectional:
# python3 experiments/dijkstratest/compare_csv_times.py  ./experiments/dijkstratest/results/standard_1000_results.csv ./experiments/dijkstratest/results/bidirectional_1000_results.csv



import csv
import sys

# Usage: python compare_csv_times.py file1.csv file2.csv

# if len(sys.argv) != 3:
#     print("Usage: python compare_csv_times.py <file1.csv> <file2.csv>")
#     sys.exit(1)

# file1, file2 = sys.argv[1], sys.argv[2]
file1 = "experiments/dijkstratest/results/standard_1000_results.csv"
file2 = "experiments/dijkstratest/results/bidirectional_1000_results.csv"
# Read both CSV files
with open(file1, newline='') as f1, open(file2, newline='') as f2:
    reader1 = list(csv.DictReader(f1))
    reader2 = list(csv.DictReader(f2))

# Check if same number of rows
if len(reader1) != len(reader2):
    print(f"Warning: Different number of rows ({len(reader1)} vs {len(reader2)})")
    min_len = min(len(reader1), len(reader2))
else:
    min_len = len(reader1)

count_less = 0

for i in range(min_len):
    t1 = int(reader1[i]['time_ns'])
    t2 = int(reader2[i]['time_ns'])
    if t1 < t2:
        count_less += 1

print(f"{count_less} out of {min_len} rows have time_ns in {file1} less than in {file2}.")
