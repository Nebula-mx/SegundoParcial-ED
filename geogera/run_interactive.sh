#!/bin/bash

# Script simple para ejecutar Main interactivo
# Uso: ./run_interactive.sh

echo "🚀 Iniciando Resolvedor Interactivo de Ecuaciones Diferenciales..."
echo ""

cd "$(dirname "$0")" || exit

# Compilar si es necesario
echo "📦 Compilando proyecto..."
mvn clean compile -q 2>/dev/null

# Ejecutar
echo "✅ Iniciando interfaz interactiva..."
echo ""

mvn exec:java -Dexec.mainClass="com.ecuaciones.diferenciales.Main" -q
