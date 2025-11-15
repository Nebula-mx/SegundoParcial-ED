# 👋 PARA TU AMIGO (Frontend Developer)

## ¡Hola! Aquí está el Backend Listo

El backend está **100% funcional y listo para integrar** con tu frontend web.

---

## 🚀 Comienza Aquí

### Opción A: Usar API REST (RECOMENDADO)

**Paso 1: Iniciar el servidor**
```bash
cd /home/hector_ar/Documentos/SegundoParcial-ED/geogera
./start_server.sh
```

El servidor estará en: `http://localhost:8080`

**Paso 2: Enviar ecuación desde tu frontend**

```javascript
// Ejemplo en JavaScript
const equation = "y'' + 4y = 8cos(2x)";
const method = "VP"; // "UC" o "VP"
const initialConditions = {
  y_0: 1,        // y(0) = 1
  y_0_prime: 0   // y'(0) = 0
};

fetch('http://localhost:8080/api/solve', {
  method: 'POST',
  headers: { 'Content-Type': 'application/json' },
  body: JSON.stringify({
    equation: equation,
    method: method,
    initialConditions: initialConditions
  })
})
.then(response => response.json())
.then(data => {
  console.log("Solución:", data.solution);
  console.log("Pasos:", data.steps);
  console.log("Raíces:", data.roots);
})
.catch(error => console.error('Error:', error));
```

**Paso 3: Procesar la respuesta**

Recibirás JSON como:
```json
{
  "equation": "y'' + 4y = 8cos(2x)",
  "method": "VP",
  "homogeneousSolution": "C1*cos(2x) + C2*sin(2x)",
  "particularSolution": "2x*sin(2x)",
  "generalSolution": "C1*cos(2x) + C2*sin(2x) + 2x*sin(2x)",
  "roots": [
    { "real": 0, "imaginary": 2, "multiplicity": 1 },
    { "real": 0, "imaginary": -2, "multiplicity": 1 }
  ],
  "steps": [...]
}
```

---

### Opción B: Usar JAR Directamente (Sin servidor)

Si prefieres no tener un servidor corriendo:

```bash
# Compilar (una sola vez)
cd /home/hector_ar/Documentos/SegundoParcial-ED/geogera
mvn clean package

# Ejecutar desde tu aplicación
java -jar target/ecuaciones-diferenciales-1.0-SNAPSHOT.jar \
  "y'' + 4y = 8cos(2x)" \
  "VP" \
  "y(0)=1" \
  "y'(0)=0"
```

---

## 📝 Documentación Importante

Aquí están los archivos que necesitas leer:

1. **[MAIN_JAVA_ARREGLADO.md](MAIN_JAVA_ARREGLADO.md)** ⭐
   - Cómo funciona Main.java
   - Argumentos soportados
   - Ejemplos de uso

2. **[COMIENZA_AQUI.md](COMIENZA_AQUI.md)**
   - Overview del sistema

3. **[RESPUESTA_QUE_FALTA.md](RESPUESTA_QUE_FALTA.md)**
   - Estado actual del proyecto

4. **[QUE_FALTA_CLARO.md](QUE_FALTA_CLARO.md)**
   - Qué falta (nada crítico)

---

## 🔗 API REST Endpoints

### POST /api/solve

**Request:**
```json
{
  "equation": "y'' + 4y = 8cos(2x)",
  "method": "UC",
  "initialConditions": {
    "y_0": 1,
    "y_0_prime": 0,
    "y_0_double_prime": 0
  }
}
```

**Response:**
```json
{
  "equation": "y'' + 4y = 8cos(2x)",
  "method": "UC",
  "isHomogeneous": false,
  "order": 2,
  "homogeneousSolution": "C1*cos(2x) + C2*sin(2x)",
  "particularSolution": "2x*sin(2x)",
  "generalSolution": "C1*cos(2x) + C2*sin(2x) + 2x*sin(2x)",
  "roots": [...],
  "steps": [...]
}
```

---

## 🛠️ Stack Técnico Backend

```
Java 17 + Spring Boot 3.1.5
├─ Solver ODE
│  ├─ Undetermined Coefficients (UC)
│  └─ Variation of Parameters (VP)
├─ Database
│  └─ 50+ Integral Table
└─ API REST
   └─ JSON Response
```

---

## ⚙️ Configuración

### Puerto (Default: 8080)

Para cambiar puerto, editar `application.properties`:
```properties
server.port=9000
```

### CORS (Para integración frontend)

Si necesitas CORS habilitado, agregar:
```java
@Configuration
public class CorsConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
            .allowedOrigins("*")
            .allowedMethods("GET", "POST", "PUT", "DELETE");
    }
}
```

---

## ✅ Verificación

Antes de integrar, verifica que todo está bien:

```bash
# 1. Compilar
mvn clean compile
# Expected: BUILD SUCCESS ✅

# 2. Tests
mvn test
# Expected: Tests run: 129, Failures: 0, Errors: 0 ✅

# 3. Iniciar servidor
./start_server.sh
# Expected: Server started on port 8080 ✅

# 4. Probar API
curl -X POST http://localhost:8080/api/solve \
  -H "Content-Type: application/json" \
  -d '{
    "equation": "y'\'' + y = 0",
    "method": "UC"
  }'
# Expected: JSON response with solution ✅
```

---

## 📊 Ejemplos de Ecuaciones Soportadas

```
Homogéneas:
✅ y' + 2y = 0
✅ y'' + 4y = 0
✅ y''' + 3y'' + 2y' = 0

No-homogéneas con UC:
✅ y'' + 4y = 8cos(2x)
✅ y'' + 2y' + y = e^x
✅ y'' - 4y = 5sin(x)

No-homogéneas con VP:
✅ y'' + y = sec(x)
✅ y'' - y = 1/(1+e^x)
✅ y'' + 4y = cot(2x)

Con Notación Leibniz:
✅ d²y/dx² + 4y = 8cos(2x)
✅ dy/dx + 2y = sin(x)
```

---

## 🐛 Troubleshooting

### Problema: "Port 8080 already in use"
```bash
# Cambiar puerto
./start_server.sh --server.port=9000
```

### Problema: "Maven not found"
```bash
# Instalar Maven
sudo apt-get install maven

# O usar Maven wrapper
./mvnw clean package
```

### Problema: "Java 17 not found"
```bash
# Instalar Java 17
sudo apt-get install openjdk-17-jdk

# Verificar
java -version
```

### Problema: JSON parsing error
```
Verifica que la ecuación esté bien escapada en JSON
Ejemplo incorrecto: "y'' + 4y"
Ejemplo correcto:   "y'\\' + 4y"
o usar raw string en JavaScript
```

---

## 🎯 Quick Links

- **Backend Code:** `/src/main/java/com/ecuaciones/diferenciales/`
- **Tests:** `/src/test/java/com/ecuaciones/diferenciales/`
- **API Controller:** `./api/controller/ODEController.java`
- **Solver Core:** `./model/solver/`
- **Documentation:** `./MAIN_JAVA_ARREGLADO.md`

---

## 💬 Contacto / Preguntas

Si tienes preguntas sobre la API:
- Lee `MAIN_JAVA_ARREGLADO.md`
- Revisa `SOLVER_TECHNICAL_GUIDE.md`
- Chequea los tests en `src/test/java/`

---

## ✨ Estado Final

```
✅ Backend LISTO
✅ API REST FUNCIONAL
✅ 129/129 tests PASANDO
✅ Documentación COMPLETA
✅ Listo para INTEGRACIÓN

Tu turno: Hacer el FRONTEND 🚀
```

---

**Fecha:** 15 Noviembre 2025  
**Status:** ✅ BACKEND COMPLETADO Y DOCUMENTADO

¡Adelante con el frontend! 🎉

