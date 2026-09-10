#!/usr/bin/env bash
set -euo pipefail
cd "$(dirname "${BASH_SOURCE[0]}")/.."
docker compose -p cadastro-parte2 exec -T db sh -c 'export MYSQL_PWD="$MYSQL_PASSWORD"; exec mysql -u cadastro cadastro_parte2' <<'SQL'
SELECT VERSION() AS mysql;
SELECT COUNT(*) AS usuarios,
       SUM(senha_hash LIKE 'pbkdf2-sha256$600000$%' AND LENGTH(senha_hash) = 90) AS hashes_no_formato_esperado,
       COUNT(DISTINCT id) AS ids_distintos
FROM usuarios;
SELECT usuario_id, interesse FROM usuario_interesses ORDER BY usuario_id, interesse;
SELECT EXTRA AS geracao_id FROM information_schema.COLUMNS
WHERE TABLE_SCHEMA = 'cadastro_parte2' AND TABLE_NAME = 'usuarios' AND COLUMN_NAME = 'id';
SQL
