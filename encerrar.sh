#!/usr/bin/env bash
set -euo pipefail
PROJECT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "$PROJECT_DIR"
[[ "$(git branch --show-current)" == parte2-customizada ]] || { echo 'Use a branch parte2-customizada.' >&2; exit 1; }
docker compose -p cadastro-parte2 --env-file .env -f compose.yaml stop
echo 'Parte 2 encerrada. Contêineres e volume preservados.'
