#!/bin/bash

echo "🧪 PRUEBAS DE LA API - GeoGERA"
echo "================================"
echo

# Esperar a que el servidor esté listo
echo "⏳ Esperando a que el servidor inicie..."
for i in {1..30}; do
    if curl -s http://localhost:8080/api/ode/health > /dev/null 2>&1; then
        echo "✅ Servidor está listo!"
        break
    fi
    echo -n "."
    sleep 1
done
echo

# Test 1: Health Check
echo "📋 TEST 1: Health Check"
echo "========================"
curl -s http://localhost:8080/api/ode/health | python3 -m json.tool || echo "❌ Error"
echo
echo

# Test 2: Obtener ejemplos
echo "📋 TEST 2: Obtener ejemplos disponibles"
echo "========================================="
curl -s http://localhost:8080/api/ode/examples | python3 -m json.tool | head -20
echo
echo

# Test 3: Resolver EDO homogénea simple (y'' + 4y = 0)
echo "📋 TEST 3: Resolver EDO homogénea (y'' + 4y = 0)"
echo "==================================================="
curl -s -X POST http://localhost:8080/api/ode/solve \
  -H "Content-Type: application/json" \
  -d '{
    "equation": "y\u0027\u0027 + 4*y = 0",
    "initialConditions": ["y(0)=1", "y\u0027(0)=0"],
    "variable": "x"
  }' | python3 -m json.tool || echo "❌ Error"
echo
echo

# Test 4: Resolver EDO orden 1
echo "📋 TEST 4: Resolver EDO orden 1 (y\u0027 + 2*y = 0)"
echo "========================================================="
curl -s -X POST http://localhost:8080/api/ode/solve \
  -H "Content-Type: application/json" \
  -d '{
    "equation": "y\u0027 + 2*y = 0",
    "initialConditions": ["y(0)=1"],
    "variable": "x"
  }' | python3 -m json.tool || echo "❌ Error"
echo
echo

# Test 5: Error handling - ecuación inválida
echo "📋 TEST 5: Manejo de errores (ecuación vacía)"
echo "=============================================="
curl -s -X POST http://localhost:8080/api/ode/solve \
  -H "Content-Type: application/json" \
  -d '{
    "equation": "",
    "initialConditions": [],
    "variable": "x"
  }' | python3 -m json.tool || echo "❌ Error"
echo
echo

echo "✅ PRUEBAS COMPLETADAS"
