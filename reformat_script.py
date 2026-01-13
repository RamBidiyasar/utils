import json
import os

source_file = '/Users/B0296099/Documents/Learning/Secret Manager/src/main/java/in/wynk/secret/manager/script/failed_records.csv'

if not os.path.exists(source_file):
    print(f"Error: {source_file} not found")
    exit(1)

with open(source_file, 'r', encoding='utf-8') as f:
    lines = f.readlines()

processed_rows = []
target_header = "si,reference_id,uid,cohort,created_at,valid_till"
processed_rows.append(target_header)

for line in lines:
    line = line.strip()
    if not line or line == '_col0' or line == target_header:
        continue
    
    # Handle wrapping quotes and escaped quotes
    if line.startswith('"') and line.endswith('"'):
        line = line[1:-1].replace('""', '"')
    
    try:
        data = json.loads(line)
        # Convert null to empty string and join with comma
        csv_row = ",".join(["" if x is None else str(x) for x in data])
        processed_rows.append(csv_row)
    except Exception as e:
        # Fallback cleaning if not valid JSON
        cleaned = line.replace('[', '').replace(']', '').replace('"', '').replace(' ', '')
        if cleaned:
            processed_rows.append(cleaned.replace('null', ''))

# Write with CRLF and no trailing newline
final_content = '\r\n'.join(processed_rows)
with open(source_file, 'wb') as f:
    f.write(final_content.encode('utf-8'))

print(f"Reformatted {len(processed_rows)-1} records in {source_file}")
