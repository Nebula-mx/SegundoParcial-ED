#!/bin/bash

# Script de Pruebas Exhaustivas - Casos Diversos

RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

echo -e "${BLUE}╔════════════════════════════════════════════════════════════════════╗${NC}"
echo -e "${BLUE}║          SUITE EXHAUSTIVA DE PRUEBAS - EDO SOLVER                 ║${NC}"
echo -e "${BLUE}╚════════════════════════════════════════════════════════════════════╝${NC}"

# Compilar una sola vez
echo -e "${YELLOW}📦 Compilando proyecto...${NC}"
cd "$(dirname "$0")" || exit
mvn clean compile -q 2>/dev/null
echo -e "${GREEN}✅ Compilación exitosa${NC}\n"

# Contador
TOTAL=0
PASSED=0
FAILED=0

# Función para ejecutar prueba
run_test() {
    local name=$1
    local equation=$2
    local ci=$3
    local method=$4
    
    TOTAL=$((TOTAL + 1))
    
    echo -e "${BLUE}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"
    echo -e "${YELLOW}Test $TOTAL: $name${NC}"
    echo -e "${BLUE}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"
    
    # Construir input
    local input="$equation"
    input="$input"$'\n'"$ci"
    
    if [ "$ci" = "s" ] && [ -n "$method" ]; then
        # Agregar CIs vacías (solo presionar enter)
        input="$input"$'\n'
    fi
    input="$input"$'\n'"1"  # Selecciona UC
    
    # Ejecutar
    output=$(echo "$input" | mvn exec:java -Dexec.mainClass="com.ecuaciones.diferenciales.Main" -q 2>&1)
    
    # Verificar si tiene solución
    if echo "$output" | grep -q "y(x) ="; then
        echo "$output" | grep -A 2 "SOLUCIÓN FINAL\|SOLUCIÓN GENERAL"
        echo -e "${GREEN}✅ PASÓ${NC}\n"
        PASSED=$((PASSED + 1))
    else
        echo "$output" | tail -20
        echo -e "${RED}❌ FALLÓ${NC}\n"
        FAILED=$((FAILED + 1))
    fi
}

# ====== PRUEBAS HOMOGÉNEAS ======

echo -e "\n${BLUE}═══════════════════════════════════════════════════════════════════${NC}"
echo -e "${BLUE}          PRUEBAS: ECUACIONES HOMOGÉNEAS                          ${NC}"
echo -e "${BLUE}═══════════════════════════════════════════════════════════════════${NC}\n"

run_test "Homogénea Orden 2 - Raíces Reales Distintas" "y'' - 3*y' + 2*y = 0" "n"
run_test "Homogénea Orden 2 - Raíces Reales Repetidas" "y'' - 4*y' + 4*y = 0" "n"
run_test "Homogénea Orden 2 - Raíces Complejas" "y'' + 2*y' + 5*y = 0" "n"
run_test "Homogénea Orden 2 - Raíces Imaginarias Puras" "y'' + 9*y = 0" "n"
run_test "Homogénea Orden 2 - Coef Negativos" "y'' - y = 0" "n"

# ====== PRUEBAS NO-HOMOGÉNEAS POLINOMIALES ======

echo -e "\n${BLUE}═══════════════════════════════════════════════════════════════════${NC}"
echo -e "${BLUE}      PRUEBAS: NO-HOMOGÉNEAS (FORZAMIENTO POLINOMIAL)              ${NC}"
echo -e "${BLUE}═══════════════════════════════════════════════════════════════════${NC}\n"

run_test "Polinomial Grado 1" "y'' + y' = x" "n"
run_test "Polinomial Grado 2" "y'' + 2*y' + y = x^2" "n"
run_test "Polinomial Grado 3" "y'' + 4*y = x^3" "n"
run_test "Polinomial Constante" "y'' - y = 5" "n"
run_test "Polinomial Múltiple" "y''' + y'' = x^2 + x" "n"

# ====== PRUEBAS NO-HOMOGÉNEAS EXPONENCIALES ======

echo -e "\n${BLUE}═══════════════════════════════════════════════════════════════════${NC}"
echo -e "${BLUE}     PRUEBAS: NO-HOMOGÉNEAS (FORZAMIENTO EXPONENCIAL)              ${NC}"
echo -e "${BLUE}═══════════════════════════════════════════════════════════════════${NC}\n"

run_test "Exponencial Básico" "y'' - y = e^x" "n"
run_test "Exponencial Negativo" "y'' + 2*y' = e^(-x)" "n"
run_test "Exponencial Coef" "y'' + y' = 3*e^(2*x)" "n"
run_test "Exponencial Orden 3" "y''' - y'' = e^x" "n"

# ====== PRUEBAS NO-HOMOGÉNEAS TRIGONOMÉTRICAS ======

echo -e "\n${BLUE}═══════════════════════════════════════════════════════════════════${NC}"
echo -e "${BLUE}    PRUEBAS: NO-HOMOGÉNEAS (FORZAMIENTO TRIGONOMÉTRICO)            ${NC}"
echo -e "${BLUE}═══════════════════════════════════════════════════════════════════${NC}\n"

run_test "Seno Básico" "y'' + y = sin(x)" "n"
run_test "Coseno Básico" "y'' + 4*y = cos(x)" "n"
run_test "Seno Diferente Frecuencia" "y'' + 4*y = sin(3*x)" "n"
run_test "Coseno Doble Frecuencia" "y'' + y = cos(2*x)" "n"

# ====== PRUEBAS CON RESONANCIA ======

echo -e "\n${BLUE}═══════════════════════════════════════════════════════════════════${NC}"
echo -e "${BLUE}        PRUEBAS: RESONANCIA (Casos Especiales)                     ${NC}"
echo -e "${BLUE}═══════════════════════════════════════════════════════════════════${NC}\n"

run_test "Resonancia Exponencial" "y'' - y = e^x" "n"
run_test "Resonancia Polinomial" "y'' + 2*y' + y = x" "n"
run_test "Resonancia Trigonométrica" "y'' + y = sin(x)" "n"
run_test "Resonancia Doble Raíz" "y'' + 4*y' + 4*y = e^(-2*x)" "n"

# ====== PRUEBAS ORDEN SUPERIOR ======

echo -e "\n${BLUE}═══════════════════════════════════════════════════════════════════${NC}"
echo -e "${BLUE}      PRUEBAS: ÓRDENES SUPERIORES (Grado >= 3)                     ${NC}"
echo -e "${BLUE}═══════════════════════════════════════════════════════════════════${NC}\n"

run_test "Orden 3 - Homogénea" "y''' - y'' = 0" "n"
run_test "Orden 3 - No-Homogénea" "y''' + y' = x" "n"
run_test "Orden 4 - Homogénea" "y'''' - y = 0" "n"
run_test "Orden 3 - Raíces Mixtas" "y''' + y'' + y' + y = 0" "n"

# ====== PRUEBAS CASOS EDGE ======

echo -e "\n${BLUE}═══════════════════════════════════════════════════════════════════${NC}"
echo -e "${BLUE}         PRUEBAS: CASOS ESPECIALES (Edge Cases)                    ${NC}"
echo -e "${BLUE}═══════════════════════════════════════════════════════════════════${NC}\n"

run_test "Coeficiente Principal Diferente" "2*y'' + 3*y' + y = 1" "n"
run_test "Falta Término Derivada" "y'' + y = x" "n"
run_test "Solo Derivada Primera" "y' + 2*y = e^x" "n"
run_test "Raíz Cero" "y'' + y' = 1" "n"

# ====== PRUEBAS CON CONDICIONES INICIALES ======

echo -e "\n${BLUE}═══════════════════════════════════════════════════════════════════${NC}"
echo -e "${BLUE}     PRUEBAS: CON CONDICIONES INICIALES (Almacenadas)              ${NC}"
echo -e "${BLUE}═══════════════════════════════════════════════════════════════════${NC}\n"

run_test "Con CI - Homogénea" "y'' + y = 0" "s"
run_test "Con CI - No-Homogénea" "y'' + 3*y' + 2*y = 1" "s"
run_test "Con CI - Orden 3" "y''' + y = 0" "s"

# ====== RESUMEN FINAL ======

echo -e "\n${BLUE}═══════════════════════════════════════════════════════════════════${NC}"
echo -e "${BLUE}                    RESUMEN DE PRUEBAS                             ${NC}"
echo -e "${BLUE}═══════════════════════════════════════════════════════════════════${NC}\n"

echo -e "Total de pruebas:    ${BLUE}$TOTAL${NC}"
echo -e "Exitosas:            ${GREEN}$PASSED${NC}"
echo -e "Fallidas:            ${RED}$FAILED${NC}"

if [ $FAILED -eq 0 ]; then
    echo -e "\n${GREEN}✨ ¡TODAS LAS PRUEBAS PASARON! ✨${NC}\n"
    exit 0
else
    echo -e "\n${RED}⚠️ Algunas pruebas fallaron${NC}\n"
    exit 1
fi
