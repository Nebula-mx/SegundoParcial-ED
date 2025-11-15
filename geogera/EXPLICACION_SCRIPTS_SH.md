# 📜 EXPLICACIÓN DE LOS ARCHIVOS `.sh` DE GEOGERA

Los archivos `.sh` son **scripts de terminal (Shell)** que automatizan tareas comunes. Son útiles para no tener que escribir comandos largos una y otra vez.

---

## 📋 RESUMEN RÁPIDO

| Script | Uso | Comando |
|--------|-----|---------|
| `compile.sh` | Compilar el proyecto | `./compile.sh` |
| `run.sh` | Ejecutar la aplicación | `./run.sh` |
| `start_server.sh` | Iniciar servidor REST | `./start_server.sh [PUERTO]` |
| `test_api.sh` | Pruebas de la API | `./test_api.sh` |

---

## 🔧 EXPLICACIÓN DETALLADA DE CADA SCRIPT

### 1️⃣ `compile.sh` - Compilar el Proyecto

**¿QUÉ HACE?**
Compila todo el código Java usando Maven.

**CONTENIDO:**
```bash
#!/bin/bash                                          ← Indica que es un script bash
cd /home/hector_ar/Documentos/.../geogera           ← Entra a la carpeta del proyecto
echo "🔨 Compilando proyecto..."                     ← Imprime mensaje
mvn clean compile                                    ← Limpia y compila
echo "✅ Compilación completada"                     ← Imprime resultado
```

**¿CUÁNDO USARLO?**
- Cuando cambias el código Java
- Cuando haces cambios y quieres verificar que no hay errores
- Antes de ejecutar tests

**EQUIVALENTE SIN SCRIPT:**
```bash
cd /home/hector_ar/Documentos/SegundoParcial-ED/geogera
mvn clean compile
```

**COMANDO:**
```bash
./compile.sh
# O si no tiene permisos de ejecución:
bash compile.sh
```

---

### 2️⃣ `run.sh` - Ejecutar la Aplicación

**¿QUÉ HACE?**
1. Compila el proyecto
2. Ejecuta el programa principal (Main.java)

**CONTENIDO DESGLOSADO:**
```bash
#!/bin/bash

cd /home/hector_ar/Documentos/SegundoParcial-ED/geogera

echo "╔════════════════════════════════════════════════════════════════╗"
echo "║     SOLUCIONADOR DE ECUACIONES DIFERENCIALES DE ORDEN N        ║"
echo "║                   (Coef. Indeterminados + VP)                 ║"
echo "╚════════════════════════════════════════════════════════════════╝"
echo ""
echo "Compilando proyecto..."

# Compila sin mostrar todo el output (-q = quiet)
mvn clean compile -q

# Si la compilación falla, termina con error
if [ $? -ne 0 ]; then
    echo "❌ Error en la compilación"
    exit 1
fi

echo "✅ Compilación exitosa"
echo ""
echo "Ejecutando aplicación..."
echo ""

# Ejecuta la clase Main con todas las librerías en el classpath
java -cp "target/classes:$(find ~/.m2/repository -name '*.jar' | tr '\n' ':' | sed 's/:$//')" \
     com.ecuaciones.diferenciales.Main

echo ""
echo "Gracias por usar el solucionador. ¡Hasta luego!"
```

**DESGLOSE DEL COMANDO `java`:**
```bash
java -cp "target/classes:$(find ~/.m2/repository -name '*.jar' | tr '\n' ':' | sed 's/:$//')" \
     com.ecuaciones.diferenciales.Main

↓
├─ java                                    ← Intérprete de Java
├─ -cp "...:"                              ← Classpath (dónde buscar clases)
│  ├─ target/classes                       ← Clases compiladas del proyecto
│  └─ ~/.m2/repository/.../*.jar           ← Todas las librerías Maven
└─ com.ecuaciones.diferenciales.Main       ← Clase principal a ejecutar
```

**¿CUÁNDO USARLO?**
- Cuando quieres ejecutar la aplicación de terminal
- Para pruebas rápidas sin servidor
- Para desarrollo

**COMANDO:**
```bash
./run.sh
```

---

### 3️⃣ `start_server.sh` - Iniciar Servidor REST

**¿QUÉ HACE?**
Inicia el servidor Spring Boot en el puerto especificado.

**CONTENIDO DESGLOSADO:**
```bash
#!/bin/bash

# Acepta un puerto como parámetro (default 8080)
PORT=${1:-8080}

# Define dónde está el JAR compilado
JAR_PATH="/home/hector_ar/Documentos/SegundoParcial-ED/geogera/target/geogera-0.1.jar"

# Verifica si el JAR existe
if [ ! -f "$JAR_PATH" ]; then
    echo "❌ Error: JAR no encontrado en $JAR_PATH"
    echo "Por favor, ejecuta: mvn clean package -DskipTests"
    exit 1
fi

# Muestra información sobre el servidor
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

# Ejecuta el JAR en el puerto especificado
java -jar "$JAR_PATH" --server.port=$PORT
```

**¿CUÁNDO USARLO?**
- Cuando quieres acceder a GEOGERA mediante API REST
- Para desarrollo frontend
- Para pruebas de endpoints HTTP
- Para despliegue en producción

**COMANDO:**
```bash
# En puerto 8080 (default)
./start_server.sh

# O en puerto diferente
./start_server.sh 9000
```

**QUÉ SUCEDE:**
```
Ejecución:
./start_server.sh 8080

↓
Inicia servidor Spring Boot

↓
Escucha en http://localhost:8080

↓
Endpoints disponibles:
  - POST /api/ode/solve
  - GET /api/ode/health
  - GET /api/ode/examples
  - GET /api/ode/docs

↓
Presiona Ctrl+C para detener
```

---

### 4️⃣ `test_api.sh` - Pruebas de la API

**¿QUÉ HACE?**
Realiza **pruebas HTTP** contra los endpoints del servidor REST.

**CONTENIDO:**
```bash
#!/bin/bash

echo "🧪 PRUEBAS DE LA API - GeoGERA"
echo "================================"
echo

# 1. Esperar a que el servidor esté listo (hasta 30 segundos)
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

# 2. TEST 1: Health Check (verificar que server está vivo)
echo "📋 TEST 1: Health Check"
echo "========================"
curl -s http://localhost:8080/api/ode/health | python3 -m json.tool || echo "❌ Error"
echo

# 3. TEST 2: Obtener ejemplos
echo "📋 TEST 2: Obtener ejemplos disponibles"
curl -s http://localhost:8080/api/ode/examples | python3 -m json.tool | head -20
echo

# 4. TEST 3: Resolver EDO homogénea
echo "📋 TEST 3: Resolver EDO homogénea (y'' + 4y = 0)"
curl -s -X POST http://localhost:8080/api/ode/solve \
  -H "Content-Type: application/json" \
  -d '{
    "equation": "y\u0027\u0027 + 4*y = 0",
    "initialConditions": ["y(0)=1", "y\u0027(0)=0"],
    "variable": "x"
  }' | python3 -m json.tool || echo "❌ Error"
echo

# 5. TEST 4: Resolver EDO orden 1
echo "📋 TEST 4: Resolver EDO orden 1 (y\u0027 + 2*y = 0)"
curl -s -X POST http://localhost:8080/api/ode/solve \
  -H "Content-Type: application/json" \
  -d '{
    "equation": "y\u0027 + 2*y = 0",
    "initialConditions": ["y(0)=1"],
    "variable": "x"
  }' | python3 -m json.tool || echo "❌ Error"
echo

# 6. TEST 5: Manejo de errores
echo "📋 TEST 5: Manejo de errores (ecuación vacía)"
curl -s -X POST http://localhost:8080/api/ode/solve \
  -H "Content-Type: application/json" \
  -d '{
    "equation": "",
    "initialConditions": [],
    "variable": "x"
  }' | python3 -m json.tool || echo "❌ Error"
echo

echo "✅ PRUEBAS COMPLETADAS"
```

**¿CUÁNDO USARLO?**
- Después de hacer cambios en los endpoints
- Para verificar que todo funciona correctamente
- Para documentar el comportamiento de la API
- En CI/CD pipelines (automatización)

**COMANDO:**
```bash
# Primero, en otra terminal, inicia el servidor:
./start_server.sh

# Luego, en otra terminal, ejecuta los tests:
./test_api.sh
```

**OUTPUT ESPERADO:**
```
🧪 PRUEBAS DE LA API - GeoGERA
================================

⏳ Esperando a que el servidor inicie...
✅ Servidor está listo!

📋 TEST 1: Health Check
========================
{
  "status": "UP",
  "version": "1.0.0",
  "service": "Differential Equations Solver API"
}

📋 TEST 2: Obtener ejemplos disponibles
=========================================
{
  "examples": [
    "y'' + 4y = 0",
    "y'' - 3y' + 2y = 0",
    ...
  ]
}

... [más tests] ...

✅ PRUEBAS COMPLETADAS
```

---

## 🎯 FLUJO DE TRABAJO COMÚN

### Escenario 1: Desarrollo local

```bash
# Terminal 1: Compilar cambios
./compile.sh

# Terminal 1: Ejecutar la app
./run.sh

# (Resuelve ecuaciones en terminal interactiva)
```

### Escenario 2: Desarrollo con API

```bash
# Terminal 1: Iniciar servidor REST
./start_server.sh 8080

# Terminal 2: Verificar que funciona
./test_api.sh

# Terminal 2: Hacer peticiones manualmente
curl -X POST http://localhost:8080/api/ode/solve \
  -H "Content-Type: application/json" \
  -d '{"equation": "y'' + y = sin(x)"}'
```

### Escenario 3: Testing continuo

```bash
# Terminal 1: Iniciar servidor
./start_server.sh

# Terminal 2: Ejecutar tests cada vez que cambias código
while true; do
    ./test_api.sh
    sleep 5
done
```

---

## 🔑 COMANDOS DENTRO DE LOS SCRIPTS

### `#!/bin/bash`
```bash
#!/bin/bash
```
**Shebang** - Indica que el script debe ejecutarse con bash (shell).

---

### `echo`
```bash
echo "Esto es un mensaje"
```
**Imprime texto** en la terminal.

---

### `cd`
```bash
cd /ruta/del/directorio
```
**Cambia de directorio**.

---

### `mvn clean compile`
```bash
mvn clean compile
```
- `clean` ← Elimina compilaciones anteriores
- `compile` ← Compila el código Java

---

### `[ $? -ne 0 ]`
```bash
if [ $? -ne 0 ]; then
    echo "Error"
    exit 1
fi
```
**Verifica si el comando anterior falló.**
- `$?` ← Código de salida del último comando
- `-ne 0` ← "no es igual a 0"
- `exit 1` ← Termina con código de error

---

### `${1:-8080}`
```bash
PORT=${1:-8080}
```
**Parámetro con valor por defecto.**
- `${1}` ← Primer parámetro
- `:-8080` ← Si no existe, usa 8080

---

### `curl`
```bash
curl -s -X POST http://localhost:8080/api/ode/solve \
  -H "Content-Type: application/json" \
  -d '{"equation": "..."}'
```
**Hace peticiones HTTP.**
- `-s` ← Silencioso (sin barra de progreso)
- `-X POST` ← Método HTTP POST
- `-H` ← Header (encabezado)
- `-d` ← Data (cuerpo de la petición)

---

### `python3 -m json.tool`
```bash
curl ... | python3 -m json.tool
```
**Formatea JSON** para que sea legible.

---

## ✅ CÓMO DAR PERMISOS DE EJECUCIÓN

Si los scripts no son ejecutables:

```bash
# Dar permisos a un script
chmod +x compile.sh
chmod +x run.sh
chmod +x start_server.sh
chmod +x test_api.sh

# O todos de una vez
chmod +x *.sh

# Verificar permisos
ls -la *.sh
```

**Output esperado:**
```
-rwxr-xr-x  1 user group  ...  compile.sh        ← El 'x' indica que es ejecutable
-rwxr-xr-x  1 user group  ...  run.sh
-rwxr-xr-x  1 user group  ...  start_server.sh
-rwxr-xr-x  1 user group  ...  test_api.sh
```

---

## 📊 TABLA COMPARATIVA: FORMAS DE EJECUTAR

| Acción | Script | Manual | Ventaja del Script |
|--------|--------|--------|-------------------|
| Compilar | `./compile.sh` | `mvn clean compile` | Rápido, recordar comando |
| Ejecutar | `./run.sh` | `mvn clean compile && java -cp ...` | Automatiza pasos |
| Server | `./start_server.sh` | `java -jar target/geogera-0.1.jar` | Parámetros configurables |
| Tests | `./test_api.sh` | Escribir 5 `curl` manuales | Automatización, repitabilidad |

---

## 🎓 CONCLUSIÓN

Los scripts `.sh` son **atajos para tareas repetitivas**:

1. **`compile.sh`** → Compila rápidamente
2. **`run.sh`** → Ejecuta la aplicación completa
3. **`start_server.sh`** → Inicia el servidor REST
4. **`test_api.sh`** → Verifica que todo funciona

**Uso típico:**
```bash
# Desarrollo
./compile.sh          # Compila cambios
./run.sh              # Prueba rápida

# API + Frontend
./start_server.sh     # Inicia servidor
./test_api.sh         # Verifica endpoints

# Producción
mvn clean package     # Build final
./start_server.sh 80  # Inicia en puerto 80
```

