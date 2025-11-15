#!/bin/bash
# Script para probar VP con Main.java

mvn compile -q

echo "======================================"
echo "PROBANDO VP CON y'' + 4y = 8cos(2x)"
echo "======================================"
echo ""

# Usamos echo para simular entrada interactiva
mvn exec:java -Dexec.mainClass="com.ecuaciones.diferenciales.Main" -DskipTests -q 2>&1 << 'INPUT'
y'' + 4*y = 8*cos(2x)
y(0)=1
y'(0)=0
VP
INPUT
