#!/bin/bash
# Test VP con CLI args en lugar de entrada interactiva

cd /home/hector_ar/Documentos/SegundoParcial-ED/geogera

echo "======================================"
echo "PROBANDO VP CON CLI ARGS"
echo "======================================"
echo ""

mvn compile -q

# Ejecutar con argumentos CLI
java -cp "target/classes:$(mvn dependency:build-classpath -q -Dmdep.outputFile=/dev/stdout)" \
  com.ecuaciones.diferenciales.Main \
  "y'' + 4*y = 8*cos(2x)" \
  "VP" \
  "y(0)=1" \
  "y'(0)=0" \
  2>&1 | head -200
