#!/bin/bash

# Script para pruebas interactivas del Main

echo "╔═══════════════════════════════════════════════════════════════════╗"
echo "║        SCRIPT DE PRUEBAS - MAIN INTERACTIVO                      ║"
echo "╚═══════════════════════════════════════════════════════════════════╝"
echo ""

# Función para ejecutar un test
test_equation() {
    local equation=$1
    local answer_ci=$2
    local ci_values=$3
    
    echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
    echo "🧪 Test: $equation"
    echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
    
    # Crear input
    local input="$equation"
    input="$input"$'\n'"$answer_ci"
    
    if [ "$answer_ci" = "s" ] && [ -n "$ci_values" ]; then
        while IFS= read -r ci_line; do
            input="$input"$'\n'"$ci_line"
        done < <(echo "$ci_values")
        input="$input"$'\n'  # Línea vacía para terminar CIs
    fi
    input="$input"$'\n'"1"  # Selecciona UC para y_p
    
    # Compilar si es necesario
    mvn -q compile 2>/dev/null
    
    # Ejecutar
    echo "$input" | mvn exec:java -Dexec.mainClass="com.ecuaciones.diferenciales.Main" -q 2>/dev/null
    
    echo ""
}

# ===== PRUEBAS =====

echo ""
echo "📌 PRUEBA 1: Ecuación Homogénea Simple (sin CI)"
test_equation "y'' + y = 0" "n"

echo ""
echo "📌 PRUEBA 2: Ecuación No-Homogénea (con CI)"
test_equation "y'' + 4y = 8" "s" "y(0)=1
y'(0)=0"

echo ""
echo "📌 PRUEBA 3: Ecuación de Orden 3"
test_equation "y''' + 2*y'' + y = 0" "n"

echo ""
echo "📌 PRUEBA 4: Ecuación No-Homogénea Exponencial"
test_equation "y'' - y = e^x" "s" "y(0)=0"

echo ""
echo "✨ ¡Todas las pruebas completadas!"
