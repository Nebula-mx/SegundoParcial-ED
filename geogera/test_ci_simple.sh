#!/bin/bash

cd /home/hector_ar/Documentos/SegundoParcial-ED/geogera

echo "════════════════════════════════════════════════════════"
echo "PRUEBA: Condiciones Iniciales Aplicadas"
echo "════════════════════════════════════════════════════════"
echo ""

(
# Primera ecuación
echo "s"                           # ¿Deseas resolver una ecuación?
echo "y'' - 5*y' + 6*y = 0"        # Ecuación
echo "AUTO"                         # Seleccionar método
echo "s"                            # ¿Deseas agregar CI?
echo "2"                            # Número de CI
echo "y(0)=1"                       # CI 1
echo "y'(0)=2"                      # CI 2
echo "n"                            # ¿Otra ecuación?
echo "n"                            # Salir

) | mvn -q exec:java -Dexec.mainClass="com.ecuaciones.diferenciales.Main" 2>&1 | grep -A 50 "PASO 3:"

echo ""
echo "════════════════════════════════════════════════════════"
