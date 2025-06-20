import re

errors = []

# Parse the compilation output
with open('compile_errors.log', 'r') as f:
    content = f.read()
    
    # Extract all error lines
    error_pattern = r'\[ERROR\] (.+?\.java):\[(\d+),(\d+)\] (.+)'
    matches = re.findall(error_pattern, content)
    
    for match in matches:
        file_path, line, col, error_msg = match
        errors.append({
            'file': file_path.split('/')[-1],
            'line': int(line),
            'col': int(col),
            'message': error_msg,
            'full_path': file_path
        })

# Categorize errors
error_categories = {
    'UUID_to_String': [],
    'String_to_UUID': [],
    'UUID_to_Long': [],
    'Long_to_UUID': [],
    'DateTime_conversion': [],
    'Method_not_found': [],
    'Abstract_method': [],
    'Other_type_conversion': [],
    'Other': []
}

for error in errors:
    msg = error['message']
    if 'UUID cannot be converted to java.lang.String' in msg:
        error_categories['UUID_to_String'].append(error)
    elif 'String cannot be converted to java.util.UUID' in msg:
        error_categories['String_to_UUID'].append(error)
    elif 'UUID cannot be converted to java.lang.Long' in msg:
        error_categories['UUID_to_Long'].append(error)
    elif 'Long cannot be converted to java.util.UUID' in msg:
        error_categories['Long_to_UUID'].append(error)
    elif 'LocalDateTime cannot be converted to java.time.LocalDate' in msg:
        error_categories['DateTime_conversion'].append(error)
    elif 'cannot find symbol' in msg and 'method' in msg:
        error_categories['Method_not_found'].append(error)
    elif 'does not override abstract method' in msg:
        error_categories['Abstract_method'].append(error)
    elif 'incompatible types' in msg:
        error_categories['Other_type_conversion'].append(error)
    else:
        error_categories['Other'].append(error)

# Print analysis
print("COMPILATION ERROR ANALYSIS")
print("=" * 80)
print(f"Total errors found: {len(errors)}")
print()

print("ERROR CATEGORIES:")
print("-" * 80)
for category, items in error_categories.items():
    if items:
        print(f"\n{category}: {len(items)} errors")
        # Group by file
        files = {}
        for item in items:
            file_name = item['file']
            if file_name not in files:
                files[file_name] = []
            files[file_name].append(item)
        
        for file_name, file_errors in sorted(files.items()):
            print(f"  {file_name}: {len(file_errors)} errors")
            for err in file_errors[:2]:  # Show first 2 errors per file
                print(f"    Line {err['line']}: {err['message'][:80]}...")

print("\n" + "=" * 80)
print("MOST AFFECTED FILES:")
print("-" * 80)
file_counts = {}
for error in errors:
    file_name = error['file']
    file_counts[file_name] = file_counts.get(file_name, 0) + 1

for file_name, count in sorted(file_counts.items(), key=lambda x: x[1], reverse=True)[:10]:
    print(f"{file_name}: {count} errors")

