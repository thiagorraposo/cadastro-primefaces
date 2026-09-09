#!/usr/bin/env bash
set -u

PROJECT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
TOMCAT_DIR="$HOME/Ferramentas/apache-tomcat-9.0.121"
COMPOSE_FILE="$PROJECT_DIR/compose.yaml"
APP_URL="http://localhost:8081/primefaces-0.0.1/"

erro() {
    echo "[ERRO] $1" >&2
    exit 1
}

if [[ ! -f "$COMPOSE_FILE" ]]; then
    erro "compose.yaml não encontrado."
fi

if [[ ! -x "$TOMCAT_DIR/bin/startup.sh" ]]; then
    erro "startup.sh do Tomcat não encontrado ou sem permissão."
fi

if ! command -v docker >/dev/null 2>&1; then
    erro "Docker não está disponível."
fi

if ! command -v curl >/dev/null 2>&1; then
    erro "curl não está disponível."
fi

echo "Parando o MySQL local..."

if ! sudo systemctl stop mysql; then
    erro "Não foi possível parar o MySQL local."
fi

echo "Iniciando o MySQL Docker..."

if ! docker compose \
    -f "$COMPOSE_FILE" \
    up -d --wait --wait-timeout 60 db; then
    erro "Não foi possível iniciar o MySQL Docker."
fi

echo "Iniciando o Tomcat..."

if curl --silent --fail --max-time 2 "$APP_URL" >/dev/null; then
    echo "[AVISO] A aplicação já estava em execução."
else
    if ! "$TOMCAT_DIR/bin/startup.sh"; then
        erro "Não foi possível iniciar o Tomcat."
    fi
fi

echo "Aguardando a aplicação responder..."

APLICACAO_OK=0

for ((TENTATIVA = 1; TENTATIVA <= 20; TENTATIVA++)); do
    if curl --silent --fail --max-time 2 "$APP_URL" >/dev/null; then
        APLICACAO_OK=1
        break
    fi

    sleep 1
done

if [[ "$APLICACAO_OK" -ne 1 ]]; then
    erro "A aplicação não respondeu. Verifique: $TOMCAT_DIR/logs/catalina.out"
fi

echo "[OK] Projeto iniciado."
echo "Cadastro: $APP_URL"
echo "Listagem: ${APP_URL}listagem.xhtml"