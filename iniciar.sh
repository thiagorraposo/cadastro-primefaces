#!/usr/bin/env bash
set -euo pipefail
PROJECT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "$PROJECT_DIR"
[[ "$(git branch --show-current)" == parte2-customizada ]] || { echo 'Use a branch parte2-customizada.' >&2; exit 1; }
[[ -f .env ]] || { echo 'Copie .env.example para .env e preencha as senhas.' >&2; exit 1; }
export JAVA_HOME="${JAVA_HOME:-/usr/lib/jvm/jdk-25.0.3-oracle-x64}"
export PATH="$JAVA_HOME/bin:$PATH"
java -version
mvn -B verify
docker compose -p cadastro-parte2 --env-file .env -f compose.yaml config --quiet
docker compose -p cadastro-parte2 --env-file .env -f compose.yaml up -d --build --wait --wait-timeout 300
curl --fail --silent --show-error http://localhost:8082/cadastro/ > /dev/null
echo 'Parte 2 iniciada: http://localhost:8082/cadastro/'
