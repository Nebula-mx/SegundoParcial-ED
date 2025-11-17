#!/bin/bash

# SCRIPT DE PRUEBA: CONDICIONES INICIALES
# Demuestra cómo resolver ecuaciones con y(0), y'(0), etc.

WORKSPACE="/home/hector_ar/Documentos/SegundoParcial-ED/geogera"
cd "$WORKSPACE" || exit 1

echo "════════════════════════════════════════════════════════════"
echo "  DEMOSTRACIONES CON CONDICIONES INICIALES"
echo "════════════════════════════════════════════════════════════"
echo ""

# Compilar
echo "🔧 Compilando..."
mvn clean compile -q 2>/dev/null
if [ $? -ne 0 ]; then
    echo "❌ Error en compilación"
    exit 1
fi
echo "✅ Compilación exitosa"
echo ""

# Casos de ejemplo
echo "════════════════════════════════════════════════════════════"
echo "📋 CASOS CON CONDICIONES INICIALES"
echo "════════════════════════════════════════════════════════════"
echo ""

# Caso 1: Homogénea orden 1
echo "1️⃣  CASO: y' = y con y(0) = 1"
echo "   Descripción: Exponencial pura"
echo "   Solución esperada: y = e^x"
echo ""

# Caso 2: Homogénea orden 2 - raíces reales distintas
echo "2️⃣  CASO: y'' - 5y' + 6y = 0 con y(0)=1, y'(0)=0"
echo "   Descripción: Raíces reales distintas (2, 3)"
echo "   Solución esperada: y = 3e^(2x) - 2e^(3x)"
echo ""

# Caso 3: Homogénea orden 2 - raíz doble
echo "3️⃣  CASO: y'' - 4y' + 4y = 0 con y(0)=1, y'(0)=0"
echo "   Descripción: Raíz doble (2)"
echo "   Solución esperada: y = (1 - 2x)e^(2x)"
echo ""

# Caso 4: Homogénea orden 2 - raíces complejas
echo "4️⃣  CASO: y'' + 4y = 0 con y(0)=1, y'(0)=0"
echo "   Descripción: Raíces complejas (±2i)"
echo "   Solución esperada: y = cos(2x)"
echo ""

# Caso 5: No-homogénea sin resonancia
echo "5️⃣  CASO: y'' + y = 1 con y(0)=0, y'(0)=0"
echo "   Descripción: Constante, sin resonancia"
echo "   Solución esperada: y = 1 - cos(x)"
echo ""

# Caso 6: No-homogénea con resonancia trigonométrica
echo "6️⃣  CASO: y'' + y = sin(x) con y(0)=0, y'(0)=0"
echo "   Descripción: RESONANCIA trigonométrica"
echo "   Solución esperada: y = -(x/2)cos(x) + (1/2)sin(x)"
echo ""

echo "════════════════════════════════════════════════════════════"
echo "  📊 RESUMEN DE DEMOSTRACIÓN"
echo "════════════════════════════════════════════════════════════"
echo ""
echo "✅ Se presentaron 6 casos con condiciones iniciales:"
echo "   • 4 Casos Homogéneos (órdenes 1 y 2)"
echo "   • 2 Casos No-homogéneos (con y sin resonancia)"
echo ""
echo "El archivo CASOS_CONDICIONES_INICIALES.md contiene:"
echo "   • 31 casos totales con C.I."
echo "   • 8 grupos de ecuaciones"
echo "   • Métodos de resolución explicados"
echo ""
echo "════════════════════════════════════════════════════════════"
echo ""
echo "ℹ️  Para usar en tu amigo:"
echo "   1. Enviar ecuación: 'y'' - 5y' + 6y = 0'"
echo "   2. Método automático detecta solución homogénea"
echo "   3. Frontend aplica C.I. para encontrar C₁ y C₂"
echo "   4. Retorna solución particular"
echo ""
echo "════════════════════════════════════════════════════════════"
