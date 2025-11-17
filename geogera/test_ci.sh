#!/bin/bash

cd /home/hector_ar/Documentos/SegundoParcial-ED/geogera

echo "════════════════════════════════════════════════════════"
echo "PRUEBA: Condiciones Iniciales Aplicadas"
echo "════════════════════════════════════════════════════════"
echo ""
echo "Ejecutando: y'' - 5y' + 6y = 0 con y(0)=1, y'(0)=2"
echo ""

mvn -q exec:java -Dexec.mainClass="com.ecuaciones.diferenciales.Main" <<EOF
s
y'' - 5*y' + 6*y = 0
AUTO
s
2
y(0)=1
y'(0)=2
n
n
EOF

echo ""
echo "════════════════════════════════════════════════════════"
