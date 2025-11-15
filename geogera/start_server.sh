#!/bin/bash

# Script para iniciar el servidor REST de Geogera

PORT=${1:-8080}
JAR_PATH="/home/hector_ar/Documentos/SegundoParcial-ED/geogera/target/geogera-0.1.jar"

if [ ! -f "$JAR_PATH" ]; then
    echo "❌ Error: JAR no encontrado en $JAR_PATH"
    echo "Por favor, ejecuta: mvn clean package -DskipTests"
    exit 1
fi

echo "🚀 Iniciando servidor Geogera REST API..."
echo "📍 Puerto: $PORT"
echo "🌐 URL base: http://localhost:$PORT"
echo ""
echo "Endpoints disponibles:"
echo "  - POST   http://localhost:$PORT/api/differential/evaluate"
echo "  - POST   http://localhost:$PORT/api/differential/derivative"
echo "  - POST   http://localhost:$PORT/api/differential/integral"
echo "  - POST   http://localhost:$PORT/api/differential/simplify"
echo "  - GET    http://localhost:$PORT/api/differential/health"
echo ""
echo "Presiona Ctrl+C para detener el servidor"
echo ""

java -jar "$JAR_PATH" --server.port=$PORT
