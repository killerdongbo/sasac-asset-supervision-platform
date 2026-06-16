import os, sys
from python_calamine import CalamineWorkbook

file_group_map = {
    'D700976742717815148841400': '湛江市城市发展集团有限公司',
    'D700976742717815148503764': '湛江市海洋与农业投资集团有限公司',
    'D700976742717815149039436': '湛江市旅游投资集团有限公司',
    'D700976742717815149418908': '湛江市公共服务集团有限公司',
    'D700976742717815149605958': '湛江市资产运营集团有限公司',
}

root_id = 100
group_id = 200
sub_id = 500

sql = []
sql.append("-- Clear old orgs (except SASAC root)")
sql.append("DELETE FROM sys_organization WHERE id > 100;")
sql.append("")

for fname_key, group_name in file_group_map.items():
    matched = None
    for f in os.listdir('docs'):
        if fname_key in f and f.endswith('.xls'):
            matched = f
            break
    if not matched:
        print(f"NOT FOUND: {fname_key}", file=sys.stderr)
        continue

    wb = CalamineWorkbook.from_path(f"docs/{matched}")
    sheet = wb.get_sheet_by_index(0)
    rows = sheet.to_python()

    subs = []
    for r in rows[2:]:
        if len(r) >= 4 and r[0] and r[3]:
            sub_name = str(r[3]).strip()
            relation = str(r[1]).strip() if r[1] else ""
            if relation == "对外投资" and sub_name and sub_name != group_name:
                subs.append(sub_name)

    subs = list(dict.fromkeys(subs))  # deduplicate

    safe_name = group_name.replace("'", "''")
    sql.append(f"-- {group_name}")
    sql.append(f"INSERT INTO sys_organization (id, parent_id, name, org_type, tenant_id, status) VALUES ({group_id}, {root_id}, '{safe_name}', 'GROUP', 1, 1);")

    for sub in subs[:25]:  # max 25 per group
        safe_sub = sub.replace("'", "''")
        sql.append(f"INSERT INTO sys_organization (id, parent_id, name, org_type, tenant_id, status) VALUES ({sub_id}, {group_id}, '{safe_sub}', 'ENTERPRISE', 1, 1);")
        sub_id += 1

    print(f"{group_name}: {len(subs)} subs (using {min(25, len(subs))})")
    group_id += 1

# Write output
with open("org_migration.sql", "w", encoding="utf-8") as f:
    f.write("\n".join(sql))

print(f"\nDone. {group_id - 200} groups, {sub_id - 500} subsidiaries")
print("SQL written to org_migration.sql")
