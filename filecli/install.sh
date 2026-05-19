#!/bin/bash
# ══════════════════════════════════════════
#  FileCLI — Script de instalación
# ══════════════════════════════════════════

BOLD="\033[1m"
GREEN="\033[32m"
CYAN="\033[36m"
YELLOW="\033[33m"
RED="\033[31m"
RESET="\033[0m"

echo -e "${BOLD}${CYAN}"
echo "╔══════════════════════════════════════════╗"
echo "║     Instalando FileCLI en tu sistema     ║"
echo "╚══════════════════════════════════════════╝"
echo -e "${RESET}"

# 1. Verificar Java
if ! command -v java &> /dev/null; then
    echo -e "${RED}✘ Java no está instalado.${RESET}"
    echo -e "  Instálalo con: ${YELLOW}sudo apt install openjdk-21-jre-headless${RESET}"
    exit 1
fi
echo -e "${GREEN}✔ Java encontrado: $(java -version 2>&1 | head -1)${RESET}"

# 2. Compilar si no existe el JAR
SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
JAR="$SCRIPT_DIR/filecli.jar"

if [ ! -f "$JAR" ]; then
    echo -e "${YELLOW}⚙  Compilando...${RESET}"
    if ! command -v javac &> /dev/null; then
        echo -e "${RED}✘ javac no encontrado. Instala el JDK:${RESET}"
        echo -e "   ${YELLOW}sudo apt install openjdk-21-jdk-headless${RESET}"
        exit 1
    fi
    mkdir -p "$SCRIPT_DIR/out"
    javac -d "$SCRIPT_DIR/out" "$SCRIPT_DIR/src/com/filecli/FileCLI.java"
    jar cfe "$JAR" com.filecli.FileCLI -C "$SCRIPT_DIR/out" .
    echo -e "${GREEN}✔ Compilado y empaquetado.${RESET}"
fi

# 3. Crear wrapper ejecutable
WRAPPER="/usr/local/bin/filecli"
echo -e "${YELLOW}⚙  Instalando en /usr/local/bin/filecli...${RESET}"

cat > /tmp/filecli_wrapper << EOF
#!/bin/bash
java -jar "$JAR" "\$@"
EOF

sudo mv /tmp/filecli_wrapper "$WRAPPER"
sudo chmod +x "$WRAPPER"

echo -e "${GREEN}✔ Instalado correctamente.${RESET}"
echo ""
echo -e "${BOLD}Prueba con:${RESET}  ${CYAN}filecli help${RESET}"
echo ""
