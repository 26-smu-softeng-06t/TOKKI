"""Initialize TOKKI database schema on DigitalOcean MySQL."""
import os
from pathlib import Path

import pymysql

DB_HOST = os.environ.get("MYSQL_HOST", "localhost")
DB_PORT = int(os.environ.get("MYSQL_PORT", "3306"))
DB_USER = os.environ.get("MYSQL_USERNAME", "tokki_user")
DB_PASS = os.environ["MYSQL_PASSWORD"]
DB_NAME = os.environ.get("MYSQL_DATABASE", "tokki")
SSL = {"ssl": {"ssl": True}}

SCHEMA_PATH = Path(__file__).resolve().parents[1] / "backend" / "src" / "main" / "resources" / "schema.sql"

with open(SCHEMA_PATH, "r", encoding="utf-8") as f:
    sql_content = f.read()

# Split by semicolon, filter empty statements
statements = [s.strip() for s in sql_content.split(";") if s.strip()]

conn = pymysql.connect(
    host=DB_HOST,
    port=DB_PORT,
    user=DB_USER,
    password=DB_PASS,
    database=DB_NAME,
    charset="utf8mb4",
    **SSL,
)

try:
    with conn.cursor() as cur:
        for i, stmt in enumerate(statements, 1):
            print(f"[{i}/{len(statements)}] Executing: {stmt[:60]}...")
            cur.execute(stmt)
    conn.commit()
    print(f"\nAll {len(statements)} statements executed successfully.")
except Exception as e:
    print(f"\nError: {e}")
    conn.rollback()
finally:
    conn.close()
