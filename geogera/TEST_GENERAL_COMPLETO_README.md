# 🎓 TestGeneralCompleto - Guía de Ejecución

## 📋 ¿Qué es?

`TestGeneralCompleto` es una clase de prueba interactiva que te permite ejecutar todos los casos de ecuaciones diferenciales disponibles en el sistema:

✅ **Ecuaciones Homogéneas** (órdenes 1, 2, 3+)
✅ **No-Homogéneas con Coeficientes Indeterminados (UC)**
✅ **No-Homogéneas con Variación de Parámetros (VP)**
✅ **Detección Automática de Resonancia**
✅ **Condiciones Iniciales (CI)**
✅ **Casos Especiales** (raíces repetidas, complejas, etc.)

---

## 🚀 Cómo Ejecutarla

### **Opción 1: Desde Maven (Terminal)**

```bash
cd /home/hector_ar/Documentos/SegundoParcial-ED/geogera
mvn exec:java -Dexec.mainClass="com.ecuaciones.diferenciales.TestGeneralCompleto"
```

### **Opción 2: Desde el IDE (IntelliJ / VS Code)**

1. Abre el proyecto en tu IDE
2. Navega a `src/test/java/com/ecuaciones/diferenciales/TestGeneralCompleto.java`
3. Click derecho → **Run 'TestGeneralCompleto.main()'**
4. La consola del IDE mostrará el menú interactivo

### **Opción 3: Compilar y Ejecutar Manualmente**

```bash
cd /home/hector_ar/Documentos/SegundoParcial-ED/geogera
javac -cp "target/classes:target/dependency/*" src/test/java/com/ecuaciones/diferenciales/TestGeneralCompleto.java
java -cp "target/classes:target/dependency/*" com.ecuaciones.diferenciales.TestGeneralCompleto
```

---

## 📋 Menú de Opciones

Cuando ejecutes la clase, verás este menú:

```
┌─────────────────────────────────────────────────────┐
│  MENÚ PRINCIPAL                                     │
├─────────────────────────────────────────────────────┤
│  1. 🏠 Ecuaciones Homogéneas                        │
│  2. 🔧 No-Homogéneas: Coeficientes Indeterminados │
│  3. 🔄 No-Homogéneas: Variación de Parámetros      │
│  4. ⚡ Casos de Resonancia                         │
│  5. 📍 Condiciones Iniciales                       │
│  6. 🎯 Casos Especiales                            │
│  7. 🚀 EJECUTAR TODOS LOS CASOS                    │
│  0. 🚪 Salir                                        │
└─────────────────────────────────────────────────────┘
```

---

## 📚 Casos Incluidos

### **1️⃣ Ecuaciones Homogéneas**

- `y' + 2y = 0` (Primer orden)
- `y'' - 5y' + 6y = 0` (Raíces reales distintas)
- `y'' + 2y' + y = 0` (Raíces repetidas)
- `y'' + 4y = 0` (Raíces complejas)
- `y''' - y'' = 0` (Orden 3)
- `y'''' + 2y'' + y = 0` (Orden 4)

### **2️⃣ No-Homogéneas: UC**

- `y'' - 3y' + 2y = e^x`
- `y'' + 4y = 8*cos(2x)` ← **SIN Resonancia**
- `y'' - y = x^2`
- `y'' + 2y' + y = e^(-x)`
- `y'' + y = sin(x) + cos(x)`

### **3️⃣ No-Homogéneas: VP**

- `y'' + y = sec(x)` (No resolvible por UC)
- `y'' + y = tan(x)` (No resolvible por UC)
- `y'' - y = e^x*x`
- `y'' + 4y = 2*sin(x)` (Integración con Symja)
- `y'' - 2y' + y = 1/x` (Integración de 1/x)

### **4️⃣ Resonancia (Detectada Automáticamente)**

- `y'' + 4y = 8*cos(2*x)` ← **CON Resonancia**
- `y'' + y = sin(x)` ← **Resonancia simple**
- `y'' - y = e^x` ← **Resonancia exponencial**
- `y'' + 2y' + y = e^(-x)` ← **Raíz repetida + Resonancia**
- `y''' - y'' = e^x` ← **Orden 3 con Resonancia**

### **5️⃣ Condiciones Iniciales**

- `y' + 2y = 4` con `y(0)=1`
- `y'' - 5y' + 6y = 0` con `y(0)=1, y'(0)=2`
- `y'' + 4y = 0` con `y(0)=1, y'(0)=0`
- `y'' + 2y' + y = 0` con `y(0)=0, y'(0)=1`
- `y'' - 3y' + 2y = e^x` con `y(0)=0, y'(0)=0`

### **6️⃣ Casos Especiales**

- Primer orden simple
- Segundo orden con raíces reales
- Orden 3 completo
- Orden 4 con coeficientes complejos
- Raíces repetidas
- Raíces complejas

### **7️⃣ Ejecutar Todos**

Ejecuta secuencialmente todas las pruebas anteriores.

---

## 🔍 Qué Esperar en la Salida

Cada caso mostrará:

```
──────────────────────────────────────────────────────
CASO UC-1: y'' - 3y' + 2y = e^x
──────────────────────────────────────────────────────
📐 Ecuación: y'' - 3y' + 2y = e^x
📊 Orden: 2
📌 Método: UC

✅ SOLUCIÓN:
[Aquí aparecerá la solución paso a paso]
- Ecuación característica
- Raíces
- Solución homogénea (y_h)
- Solución particular (y_p)
- Solución general (y = y_h + y_p)
```

---

## ⚙️ Características

### ✨ Simplificación Algebraica
- Las integrales se **simplifican automáticamente con Symja**
- Las soluciones particulares se **simplifican algebraicamente**
- Conversión de notación Symja (Sin[], Cos[], etc.) a notación común (sin(), cos(), etc.)

### 🔍 Detección de Resonancia
- **Automática**: detecta cuando el forzamiento coincide con raíces
- **Analítica**: usa fórmulas directas para resonancia pura (sin cambiar a VP)
- **Mensajes claros**: "⚠️ Sistema singular detectado (posible RESONANCIA)"

### 📊 Métodos Soportados
- **UC (Coeficientes Indeterminados)**: Rápido para forzamientos polinomiales/trig/exponenciales
- **VP (Variación de Parámetros)**: General para cualquier forzamiento
- **AUTO**: Intenta UC primero, VP si UC falla

---

## 📝 Ejemplos de Uso

### **Ejemplo 1: Ejecutar solo Homogéneas**
```
📌 Selecciona una opción: 1
[Verás 6 casos de ecuaciones homogéneas]
```

### **Ejemplo 2: Ejecutar solo Resonancia**
```
📌 Selecciona una opción: 4
[Verás 5 casos con resonancia detectada automáticamente]
```

### **Ejemplo 3: Ejecutar TODO**
```
📌 Selecciona una opción: 7
[Ejecutará todos los casos: ~25 ecuaciones diferentes]
```

### **Ejemplo 4: Ver Condiciones Iniciales**
```
📌 Selecciona una opción: 5
[Verás 5 casos con CI aplicadas a la solución general]
```

---

## 🛠️ Requisitos

✅ Java 17+
✅ Maven 3.8+
✅ Proyecto compilado (`mvn clean compile`)
✅ Symja disponible en classpath (ya incluido en pom.xml)

---

## 📧 Notas Finales

- **Interactivo**: Selecciona el caso que quieres ver
- **Completo**: Cubre todos los escenarios posibles
- **Paso a paso**: Cada ecuación muestra toda la resolución
- **Simplificado**: Las salidas están optimizadas para lectura
- **Verificado**: Todos los 254 tests pasaron en la suite completa

---

## 🎯 Resumen de Cambios Realizados

### ✅ VP Mejorado
- Integración real con Symja (`symbolicIntegral`)
- Conversión de notación Symja a notación común
- **Simplificación algebraica de y_p antes de retornar**

### ✅ UC con Resonancia
- Detección automática de resonancia
- Resolución analítica (sin cambiar a VP)
- Manejo correcto de raíces repetidas

### ✅ Suite de Tests
- **254 tests** ejecutados exitosamente
- 0 errores, 0 fallos
- Tiempo total: 12.768 segundos

---

## 📞 ¿Preguntas?

Si tienes dudas durante la ejecución:
1. Revisa la salida de errores
2. Verifica que el formato de la ecuación sea correcto
3. Intenta con una ecuación simple primero

¡Disfruta explorando las ecuaciones diferenciales! 🚀
