#!/bin/bash

# Script para ejecutar el solucionador de ecuaciones diferenciales

cd /home/hector_ar/Documentos/SegundoParcial-ED/geogera

echo "╔════════════════════════════════════════════════════════════════╗"
echo "║     SOLUCIONADOR DE ECUACIONES DIFERENCIALES DE ORDEN N        ║"
echo "║                   (Coef. Indeterminados + VP)                 ║"
echo "╚════════════════════════════════════════════════════════════════╝"
echo ""
echo "Compilando proyecto..."
mvn clean compile -q

if [ $? -ne 0 ]; then
    echo "❌ Error en la compilación"
    exit 1
fi

echo "✅ Compilación exitosa"
echo ""
echo "Ejecutando aplicación..."
echo ""

java -cp "target/classes:$(find ~/.m2/repository -name '*.jar' | tr '\n' ':' | sed 's/:$//')" \
     com.ecuaciones.diferenciales.Main

echo ""
echo "Gracias por usar el solucionador. ¡Hasta luego!"
