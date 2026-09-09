#!/usr/bin/env bash
set -u

PROJECT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
TOMCAT_DIR="$HOME/Ferramentas/apache-tomcat-9.0.121"
COMPOSE_FILE="$PROJECT_DIR/compose.yaml"

TOTAL_ERROS=0

registrar_erro() {
    echo "[ERRO] $1" >&2
    TOTAL_ERROS=$((TOTAL_ERROS + 1))
}

echo "Encerrando o Tomcat..."

if "$TOMCAT_DIR/bin/shutdown.sh"; then
    echo "[OK] Tomcat encerrado."
else
    registrar_erro "Não foi possível encerrar o Tomcat ou ele já estava parado."
fi

echo "Encerrando o MySQL Docker..."

if docker compose -f "$COMPOSE_FILE" down; then
    echo "[OK] MySQL Docker encerrado."
else
    registrar_erro "Não foi possível encerrar o MySQL Docker."
fi

echo "Restaurando o MySQL local..."

if sudo systemctl start mysql; then
    echo "[OK] MySQL local iniciado."
else
    registrar_erro "Não foi possível iniciar o MySQL local."
fi

if [[ "$TOTAL_ERROS" -eq 0 ]]; then
    echo "[OK] Projeto encerrado corretamente."
    exit 0
fi

echo "[AVISO] O encerramento terminou com $TOTAL_ERROS erro(s)."
exit 1