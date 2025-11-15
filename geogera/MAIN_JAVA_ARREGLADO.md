# ✅ Main.java ARREGLADO - Guía de Uso

## 🎯 Lo Que Se Arregló

### ✅ ARREGLO #1: Main.java ahora respeta el parámetro "method"
- ✅ Puede elegir entre UC (Undetermined Coefficients) o VP (Variation of Parameters)
- ✅ Funciona desde línea de comandos
- ✅ Funciona de forma interactiva

### ✅ ARREGLO #2: Main.java ahora lee CI (Condiciones Iniciales) correctamente
- ✅ Pregunta si quieres agregar CI
- ✅ Lee la respuesta correctamente (antes no leía)
- ✅ Solicita cada CI hasta que escribas vacío
- ✅ Almacena las CI para futura integración web

---

## 🚀 Cómo Usar Main.java

### Opción A: MODO INTERACTIVO (Sin argumentos)

```bash
cd /home/hector_ar/Documentos/SegundoParcial-ED/geogera
java -cp target/classes com.ecuaciones.diferenciales.Main
```

**Lo que pasa:**
```
╔════════════════════════════════════════════════════════════╗
║     RESOLVEDOR INTERACTIVO DE ECUACIONES DIFERENCIALES     ║
╚════════════════════════════════════════════════════════════╝

📝 INGRESO DE DATOS:
   Ingresa una ecuación (Ej: y'' + 4y = 8cos(2x)): y'' + 4y = 8cos(2x)

❓ ¿Qué método prefieres? (UC/VP) [default=UC]: VP

❓ ¿Deseas agregar condiciones iniciales? (s/n): s

📋 INGRESO DE CONDICIONES INICIALES:
   Formato: y(0)=1, y'(0)=2, etc.
   (Ingresa vacío cuando termines)
   CI: y(0)=1
   CI: y'(0)=0
   CI: 

✅ Condiciones iniciales ingresadas: [y(0)=1, y'(0)=0]

[... RESULTADO ...]
```

---

### Opción B: MODO DIRECTO CON ARGUMENTOS (Línea de comandos)

```bash
# Solo ecuación (usa UC por defecto)
java -cp target/classes com.ecuaciones.diferenciales.Main "y'' + 4y = 8cos(2x)"

# Ecuación + Método
java -cp target/classes com.ecuaciones.diferenciales.Main "y'' + 4y = 8cos(2x)" VP

# Ecuación + Método + Condiciones Iniciales
java -cp target/classes com.ecuaciones.diferenciales.Main "y'' + 4y = 8cos(2x)" VP "y(0)=1" "y'(0)=0"
```

---

### Opción C: USANDO JAR (Después de compilar con Maven)

```bash
cd /home/hector_ar/Documentos/SegundoParcial-ED/geogera

# Compilar y empaquetar
mvn clean package

# Ejecutar (modo interactivo)
java -jar target/ecuaciones-diferenciales-1.0-SNAPSHOT.jar

# O con argumentos
java -jar target/ecuaciones-diferenciales-1.0-SNAPSHOT.jar "y'' + 4y = sin(x)" UC
```

---

## 📊 Ejemplos de Uso

### Ejemplo 1: UC Interactivo
```bash
java -cp target/classes com.ecuaciones.diferenciales.Main

# Entrada interactiva:
# Ecuación: y'' + 4y = 8cos(2x)
# Método: UC
# CI: n
```

### Ejemplo 2: VP Interactivo con CI
```bash
java -cp target/classes com.ecuaciones.diferenciales.Main

# Entrada interactiva:
# Ecuación: y'' - y = e^x
# Método: VP
# CI: s
#   → y(0)=0
#   → y'(0)=1
#   → [vacío]
```

### Ejemplo 3: VP Directo (CLI)
```bash
java -cp target/classes com.ecuaciones.diferenciales.Main "y'' + y = sin(x)" VP
```

### Ejemplo 4: UC con CI Directo (CLI)
```bash
java -cp target/classes com.ecuaciones.diferenciales.Main "y'' + 4y = 8cos(2x)" UC "y(0)=1" "y'(0)=0"
```

---

## 🔄 Cómo Integrar con el Frontend Web

### Para Tu Amigo (Desarrollo Frontend)

El backend ahora es **mucho más limpio y fácil de integrar**:

#### 1. **Para Resolver una Ecuación:**

**Opción A: Via JAR (Recomendado)**
```bash
java -jar backend.jar "y'' + y = sin(x)" VP "y(0)=1" "y'(0)=0"
```

**Opción B: Via API REST (si está corriendo)**
```bash
curl -X POST http://localhost:8080/api/solve \
  -H "Content-Type: application/json" \
  -d '{
    "equation": "y'\'\'  + y = sin(x)",
    "method": "VP",
    "initialConditions": {
      "y_0": 1,
      "y_0_prime": 0
    }
  }'
```

#### 2. **Argumentos Aceptados**

| Argumento | Tipo | Valores | Obligatorio |
|-----------|------|---------|-------------|
| **Ecuación** | String | Cualquier EDO válida | ✅ Sí (arg 0) |
| **Método** | String | "UC" o "VP" | ❌ No (default=UC) |
| **CI 1** | String | "y(0)=valor" | ❌ No |
| **CI 2** | String | "y'(0)=valor" | ❌ No |
| **CI 3+** | String | "y''(0)=valor", etc. | ❌ No |

#### 3. **Salida Esperada**

El programa imprime:
1. Información de la ecuación
2. Raíces características
3. Solución homogénea
4. Solución particular
5. **Solución general final** (lo más importante)
6. Si hay CI, nota sobre integración web

**Tu amigo puede:**
- Parsear la salida (buscar "y(x) = ")
- Usar API REST (más fácil, estructura JSON)
- O integrar el JAR como subprocess

---

## 💻 Código Modificado

### Cambios en Main.java

**ANTES (Problemas):**
```java
// No respetaba parámetro method
System.out.print("   Selecciona (1 o 2): ");
String opcion = scanner.nextLine();
if ("1".equals(opcion)) { /* UC */ }
else if ("2".equals(opcion)) { /* VP */ }

// No leía CI correctamente
System.out.print("❓ ¿Deseas agregar condiciones iniciales? (s/n): ");
String respuestCI = scanner.nextLine(); // Leía pero no usaba bien
```

**AHORA (Arreglado):**
```java
// Parsea argumentos CLI
String metodoSeleccionado = "UC"; // default
if (args.length > 1) {
    metodoSeleccionado = args[1].toUpperCase(); // UC o VP
}

// Lee CI correctamente
if ("s".equals(respuestCI) || "si".equals(respuestCI)) {
    while (true) {
        System.out.print("   CI: ");
        String ci = scanner.nextLine().trim();
        if (ci.isEmpty()) {
            break; // Sale del loop cuando presionas Enter vacío
        }
        condicionesIniciales.add(ci);
    }
}

// Usa el método seleccionado
if ("UC".equals(metodoSeleccionado)) {
    /* Coeficientes Indeterminados */
} else if ("VP".equals(metodoSeleccionado)) {
    /* Variación de Parámetros */
}
```

---

## ✅ Verificación

### Tests Status
```
Tests: 129/129 ✅ (Todos pasando)
Build: SUCCESS ✅
Errores: 0
```

### Compilación
```bash
mvn clean compile
# Result: BUILD SUCCESS ✅
```

### Tests
```bash
mvn test
# Result: Tests run: 129, Failures: 0, Errors: 0 ✅
```

---

## 🎯 Recomendación para Tu Amigo (Frontend)

### Opción A: Usar API REST (Recomendado)
```
✅ Más fácil de integrar
✅ Respuestas en JSON
✅ No necesita procesar salida de texto
✅ Simultáneo (múltiples requests)

Comando: ./start_server.sh
Endpoint: POST http://localhost:8080/api/solve
```

### Opción B: Usar JAR como Subprocess
```
✅ No necesita servidor corriendo
✅ Ejecución sincrónica
✅ Resultado inmediato
❌ Necesita parsear salida de texto
❌ Ejecutable llamadas al JAR

Comando: java -jar backend.jar [args]
```

---

## 📝 Resumen de Cambios

| Item | Antes | Después |
|------|-------|---------|
| **Respeta método CLI** | ❌ No | ✅ Sí |
| **Lee CI interactivo** | ⚠️ Preguntaba pero no leía | ✅ Lee correctamente |
| **Argumentos CLI** | ❌ No | ✅ Soporta args |
| **Default method** | ❌ UC hardcodeado | ✅ UC (configurable) |
| **Tests** | 129/129 ✅ | 129/129 ✅ |
| **Compilación** | SUCCESS ✅ | SUCCESS ✅ |

---

## 🚀 Status Final

```
✅ Main.java está LISTO
✅ Código COMPILADO
✅ Tests PASANDO
✅ Fácil de INTEGRAR
✅ Listo para tu amigo (Frontend)
```

**Próximo Paso:** Tu amigo puede empezar a integrar el backend con su frontend web.

---

**Fecha:** 15 Noviembre 2025  
**Status:** ✅ COMPLETADO  

