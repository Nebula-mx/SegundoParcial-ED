#!/bin/bash

# 🧪 Script exhaustivo de pruebas para Variación de Parámetros
# ============================================================

cd "$(dirname "$0")"

# Colores
GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
MAGENTA='\033[0;35m'
NC='\033[0m' # No Color

echo -e "${BLUE}╔══════════════════════════════════════════════════════════════╗${NC}"
echo -e "${BLUE}║   🧪 PRUEBAS EXHAUSTIVAS: VARIACIÓN DE PARÁMETROS (VP)      ║${NC}"
echo -e "${BLUE}╚══════════════════════════════════════════════════════════════╝${NC}"
echo ""

# Compilar primero
echo -e "${YELLOW}[*] Compilando proyecto...${NC}"
mvn clean compile -q 2>&1 | tail -1

if [ $? -ne 0 ]; then
    echo -e "${RED}❌ Error de compilación${NC}"
    exit 1
fi

echo -e "${GREEN}✅ Compilación exitosa${NC}"
echo ""

# Contador de pruebas
TOTAL_TESTS=0
PASSED_TESTS=0
FAILED_TESTS=0

# Función para ejecutar una prueba
run_test() {
    local TEST_NUM=$1
    local EQUATION=$2
    local DESCRIPTION=$3
    local METHOD=${4:-1}  # Por defecto UC (1), si es 2 usa VP
    
    ((TOTAL_TESTS++))
    
    echo -e "${MAGENTA}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"
    echo -e "${BLUE}[TEST ${TEST_NUM}] ${DESCRIPTION}${NC}"
    echo -e "${MAGENTA}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"
    echo -e "📝 Ecuación: ${YELLOW}${EQUATION}${NC}"
    echo -e "🎯 Método: $([ "$METHOD" = "2" ] && echo "VP (Variación de Parámetros)" || echo "UC (Coeficientes Indeterminados)")"
    echo ""
    
    # Preparar input
    if [ "$METHOD" = "2" ]; then
        INPUT=$(printf "${EQUATION}\nn\n${METHOD}\n")
    else
        INPUT=$(printf "${EQUATION}\nn\n${METHOD}\n")
    fi
    
    # Ejecutar
    OUTPUT=$(echo "$INPUT" | mvn exec:java -Dexec.mainClass="com.ecuaciones.diferenciales.Main" -q 2>&1)
    RESULT=$?
    
    if [ $RESULT -eq 0 ]; then
        echo -e "${GREEN}✅ PASÓ${NC}"
        ((PASSED_TESTS++))
        
        # Mostrar partes relevantes del output
        echo "$OUTPUT" | grep -E "Solución Particular|Paso|Wronskiano|y_p|u_" | head -10
    else
        echo -e "${RED}❌ FALLÓ${NC}"
        ((FAILED_TESTS++))
        echo "$OUTPUT" | tail -5
    fi
    
    echo ""
    sleep 1
}

# ============================================================
# CASOS DE PRUEBA: VARIACIÓN DE PARÁMETROS
# ============================================================

echo -e "${YELLOW}[*] Iniciando suite de pruebas VP...${NC}"
echo ""

# -------- Grupo 1: No-homogéneas simples (sin resonancia) --------
echo -e "${BLUE}╔════════════════════════════════════════════════════════════╗${NC}"
echo -e "${BLUE}║ GRUPO 1: No-homogéneas con VP (Sin Resonancia)            ║${NC}"
echo -e "${BLUE}╚════════════════════════════════════════════════════════════╝${NC}"
echo ""

# Test 1: Exponencial básico
run_test "1.1" "y'' - 3*y' + 2*y = e^x" \
    "Exponencial: y'' - 3y' + 2y = e^x (Raíces distintas: r=1, r=2)" "1"

# Test 2: Con seno (especial para VP)
run_test "1.2" "y'' + y = sin(x)" \
    "Trigonométrica: y'' + y = sin(x) (Raíces: ±i, g(x)=sin(x))" "1"

# Test 3: Con coseno
run_test "1.3" "y'' + 4*y = cos(2*x)" \
    "Trigonométrica: y'' + 4y = cos(2x) (Raíces: ±2i)" "1"

# -------- Grupo 2: No-homogéneas con raíces repetidas --------
echo ""
echo -e "${BLUE}╔════════════════════════════════════════════════════════════╗${NC}"
echo -e "${BLUE}║ GRUPO 2: No-homogéneas con Raíces Repetidas               ║${NC}"
echo -e "${BLUE}╚════════════════════════════════════════════════════════════╝${NC}"
echo ""

# Test 4: Raíz repetida con exponencial
run_test "2.1" "y'' - 2*y' + y = e^x" \
    "Raíz repetida: y'' - 2y' + y = e^x (Raíz r=1, mult=2, RESONANCIA)" "1"

# Test 5: Raíz repetida con polinomio
run_test "2.2" "y'' + 2*y' + y = x" \
    "Raíz repetida: y'' + 2y' + y = x (Raíz r=-1, mult=2)" "1"

# Test 6: Raíz repetida con e^(-x)
run_test "2.3" "y'' - 4*y' + 4*y = e^(2*x)" \
    "Raíz repetida: y'' - 4y' + 4y = e^(2x) (Raíz r=2, mult=2, RESONANCIA)" "1"

# -------- Grupo 3: Casos especiales para VP --------
echo ""
echo -e "${BLUE}╔════════════════════════════════════════════════════════════╗${NC}"
echo -e "${BLUE}║ GRUPO 3: Casos Especiales (Mejor con VP)                  ║${NC}"
echo -e "${BLUE}╚════════════════════════════════════════════════════════════╝${NC}"
echo ""

# Test 7: sec(x) - ideal para VP
run_test "3.1" "y'' + y = sec(x)" \
    "Secante: y'' + y = sec(x) (Ideal para VP, no UC)" "1"

# Test 8: tan(x) - ideal para VP  
run_test "3.2" "y'' + y = tan(x)" \
    "Tangente: y'' + y = tan(x) (Ideal para VP)" "1"

# Test 9: 1/x - solo con VP
run_test "3.3" "y'' - 2*y' + y = 1/x" \
    "Función especial: y'' - 2y' + y = 1/x (Solo con VP)" "1"

# -------- Grupo 4: Orden superior --------
echo ""
echo -e "${BLUE}╔════════════════════════════════════════════════════════════╗${NC}"
echo -e "${BLUE}║ GRUPO 4: Ecuaciones de Orden Superior                      ║${NC}"
echo -e "${BLUE}╚════════════════════════════════════════════════════════════╝${NC}"
echo ""

# Test 10: Orden 3
run_test "4.1" "y''' - 3*y'' + 2*y' = e^x" \
    "Orden 3: y''' - 3y'' + 2y' = e^x" "1"

# Test 11: Orden 3 con raíces complejas
run_test "4.2" "y''' + y' = sin(x)" \
    "Orden 3 complejo: y''' + y' = sin(x)" "1"

# -------- Grupo 5: Combinaciones complejas --------
echo ""
echo -e "${BLUE}╔════════════════════════════════════════════════════════════╗${NC}"
echo -e "${BLUE}║ GRUPO 5: Combinaciones y Casos Especiales                 ║${NC}"
echo -e "${BLUE}╚════════════════════════════════════════════════════════════╝${NC}"
echo ""

# Test 12: Polinomio de alto grado
run_test "5.1" "y'' + y' + y = x^3" \
    "Polinomio cúbico: y'' + y' + y = x³" "1"

# Test 13: Mezcla exp + trig
run_test "5.2" "y'' - y = e^x + sin(x)" \
    "Mezcla: y'' - y = e^x + sin(x)" "1"

# Test 14: Coeficiente principal ≠ 1
run_test "5.3" "2*y'' + 3*y' + y = x" \
    "Coeficiente principal: 2y'' + 3y' + y = x" "1"

# ============================================================
# RESUMEN FINAL
# ============================================================

echo ""
echo -e "${MAGENTA}════════════════════════════════════════════════════════════${NC}"
echo -e "${MAGENTA}                    📊 RESUMEN FINAL${NC}"
echo -e "${MAGENTA}════════════════════════════════════════════════════════════${NC}"

PASS_PERCENT=$((PASSED_TESTS * 100 / TOTAL_TESTS))

echo -e "Total de pruebas:  ${YELLOW}${TOTAL_TESTS}${NC}"
echo -e "Pruebas pasadas:   ${GREEN}${PASSED_TESTS}${NC} ✅"
echo -e "Pruebas fallidas:  ${RED}${FAILED_TESTS}${NC} ❌"
echo -e "Porcentaje éxito:  ${BLUE}${PASS_PERCENT}%${NC}"

echo ""

if [ $FAILED_TESTS -eq 0 ]; then
    echo -e "${GREEN}╔════════════════════════════════════════════════════════════╗${NC}"
    echo -e "${GREEN}║     🎉 TODAS LAS PRUEBAS PASARON EXITOSAMENTE 🎉         ║${NC}"
    echo -e "${GREEN}╚════════════════════════════════════════════════════════════╝${NC}"
    exit 0
else
    echo -e "${RED}╔════════════════════════════════════════════════════════════╗${NC}"
    echo -e "${RED}║         ⚠️  ALGUNAS PRUEBAS FALLARON ⚠️                  ║${NC}"
    echo -e "${RED}╚════════════════════════════════════════════════════════════╝${NC}"
    exit 1
fi
