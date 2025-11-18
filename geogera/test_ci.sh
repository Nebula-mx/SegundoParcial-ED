#!/bin/bash

# Script de prueba para verificar CI en punto diferente a 0

cd /home/hector_ar/Documentos/SegundoParcial-ED

# Ejecutar el programa con entrada automática
echo "════════════════════════════════════════"
echo "PRUEBA: y'' + 9y = 0 con CI en x=2"
echo "════════════════════════════════════════"

/usr/bin/env /home/hector_ar/java/jdk-17.0.12/bin/java \
  -cp /home/hector_ar/Documentos/SegundoParcial-ED/geogera/target/classes:/home/hector_ar/.m2/repository/com/mathsyslib/matheclipse-core/0.1.8/matheclipse-core-0.1.8.jar:/home/hector_ar/.m2/repository/org/slf4j/slf4j-api/1.7.36/slf4j-api-1.7.36.jar:/home/hector_ar/.m2/repository/org/slf4j/slf4j-simple/1.7.36/slf4j-simple-1.7.36.jar:/home/hector_ar/.m2/repository/com/fasterxml/jackson/core/jackson-databind/2.15.2/jackson-databind-2.15.2.jar:/home/hector_ar/.m2/repository/com/fasterxml/jackson/core/jackson-annotations/2.15.2/jackson-annotations-2.15.2.jar:/home/hector_ar/.m2/repository/com/fasterxml/jackson/core/jackson-core/2.15.2/jackson-core-2.15.2.jar \
  com.ecuaciones.diferenciales.Main << 'EOF'
s
y'' + 9y = 0
s
y(2)=3
y'(2)=4

n
EOF

echo ""
echo "════════════════════════════════════════"
echo "PRUEBA 2: y'' - 5y' + 6y = 0 con CI en x=0"
echo "════════════════════════════════════════"

/usr/bin/env /home/hector_ar/java/jdk-17.0.12/bin/java \
  -cp /home/hector_ar/Documentos/SegundoParcial-ED/geogera/target/classes:/home/hector_ar/.m2/repository/com/mathsyslib/matheclipse-core/0.1.8/matheclipse-core-0.1.8.jar:/home/hector_ar/.m2/repository/org/slf4j/slf4j-api/1.7.36/slf4j-api-1.7.36.jar:/home/hector_ar/.m2/repository/org/slf4j/slf4j-simple/1.7.36/slf4j-simple-1.7.36.jar:/home/hector_ar/.m2/repository/com/fasterxml/jackson/core/jackson-databind/2.15.2/jackson-databind-2.15.2.jar:/home/hector_ar/.m2/repository/com/fasterxml/jackson/core/jackson-annotations/2.15.2/jackson-annotations-2.15.2.jar:/home/hector_ar/.m2/repository/com/fasterxml/jackson/core/jackson-core/2.15.2/jackson-core-2.15.2.jar \
  com.ecuaciones.diferenciales.Main << 'EOF'
s
y'' - 5*y' + 6*y = 0
s
y(0)=1
y'(0)=2

n
EOF
