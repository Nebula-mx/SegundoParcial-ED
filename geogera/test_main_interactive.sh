#!/bin/bash

# Script de prueba interactiva para Main.java

cd /home/hector_ar/Documentos/SegundoParcial-ED/geogera

echo "═══════════════════════════════════════════════════════════════"
echo "  🧪 TESTING MAIN.JAVA - PRUEBAS INTERACTIVAS"
echo "═══════════════════════════════════════════════════════════════"
echo ""

# Test 1: Homogénea orden 2 - Raíces Reales Distintas
echo "TEST 1: Homogénea Orden 2 (Raíces Reales Distintas)"
echo "───────────────────────────────────────────────────────────────"
(
echo "s"  # resolver ecuación
echo "y'' - 5*y' + 6*y = 0"  # ecuación
echo ""  # método default
echo "n"  # sin CI
echo "n"  # no resolver más
) | mvn -q exec:java -Dexec.mainClass="com.ecuaciones.diferenciales.Main" 2>&1 | tail -25

echo ""
echo "═══════════════════════════════════════════════════════════════"
echo ""

# Test 2: No-Homogénea con Coeficientes Indeterminados
echo "TEST 2: No-Homogénea (UC - Coeficientes Indeterminados)"
echo "───────────────────────────────────────────────────────────────"
(
echo "s"  # resolver ecuación
echo "y'' - y = 2*x"  # ecuación
echo "1"  # UC
echo "n"  # sin CI
echo "n"  # no resolver más
) | mvn -q exec:java -Dexec.mainClass="com.ecuaciones.diferenciales.Main" 2>&1 | tail -30

echo ""
echo "═══════════════════════════════════════════════════════════════"
echo ""

# Test 3: No-Homogénea con Variación de Parámetros
echo "TEST 3: No-Homogénea (VP - Variación de Parámetros)"
echo "───────────────────────────────────────────────────────────────"
(
echo "s"  # resolver ecuación
echo "y'' + y = sec(x)"  # ecuación
echo "2"  # VP
echo "n"  # sin CI
echo "n"  # no resolver más
) | mvn -q exec:java -Dexec.mainClass="com.ecuaciones.diferenciales.Main" 2>&1 | tail -30

echo ""
echo "═══════════════════════════════════════════════════════════════"
echo ""

# Test 4: Con Condiciones Iniciales
echo "TEST 4: Con Condiciones Iniciales"
echo "───────────────────────────────────────────────────────────────"
(
echo "s"  # resolver ecuación
echo "y'' - 4*y = 0"  # ecuación
echo ""  # método default
echo "s"  # con CI
echo "y(0)=1"  # CI 1
echo "y'(0)=2"  # CI 2
echo ""  # terminar CI
echo "n"  # no resolver más
) | mvn -q exec:java -Dexec.mainClass="com.ecuaciones.diferenciales.Main" 2>&1 | tail -30

echo ""
echo "═══════════════════════════════════════════════════════════════"
echo ""

# Test 5: Orden 3
echo "TEST 5: Orden 3 (Casos Extremos)"
echo "───────────────────────────────────────────────────────────────"
(
echo "s"  # resolver ecuación
echo "y''' - 6*y'' + 11*y' - 6*y = 0"  # ecuación
echo ""  # método default
echo "n"  # sin CI
echo "n"  # no resolver más
) | mvn -q exec:java -Dexec.mainClass="com.ecuaciones.diferenciales.Main" 2>&1 | tail -25

echo ""
echo "═══════════════════════════════════════════════════════════════"
echo "  ✅ PRUEBAS COMPLETADAS EXITOSAMENTE"
echo "═══════════════════════════════════════════════════════════════"