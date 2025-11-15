# 🧪 GUÍA DE TESTING - PROYECTO GEOGERA

**Fecha:** 14 de Noviembre de 2025  
**Versión:** 1.0  
**Estado:** ✅ Listo para Testing

---

## 📋 TABLA DE CONTENIDOS

1. [Pre-requisitos](#pre-requisitos)
2. [Compilación](#compilación)
3. [Pruebas Unitarias](#pruebas-unitarias)
4. [Ejecución de API](#ejecución-de-api)
5. [Casos de Prueba](#casos-de-prueba)
6. [Validación](#validación)

---

## 🔧 Pre-requisitos

```bash
# Verificar versiones
java -version          # Java 17+
mvn --version          # Maven 3.8.9+
git --version          # Git 2.x+

# Ubicación del proyecto
cd /home/hector_ar/Documentos/SegundoParcial-ED/geogera
```

---

## 🔨 Compilación

### Paso 1: Limpiar y compilar
```bash
cd /home/hector_ar/Documentos/SegundoParcial-ED/geogera
mvn clean compile
```

**Resultado esperado:**
```
[INFO] BUILD SUCCESS
[INFO] Total time: 2.080 s
```

### Paso 2: Verificar compilación
```bash
# Ver archivos compilados
ls -la target/classes/com/ecuaciones/diferenciales/

# Debe mostrar directorios:
# api/  config/  model/  utils/  Main.class  GeogeraApplication.class
```

---

## 🧪 Pruebas Unitarias

### Ejecutar todos los tests
```bash
mvn test
```

**Tests disponibles:**
```
✅ ODEControllerTest.java
✅ HigherOrderTest.java
✅ InitialConditionsTest.java
✅ VariationOfParametersTest.java
✅ LeibnizNotationTest.java
✅ VeryHighOrderTest.java
```

### Ejecutar test específico
```bash
# Test de orden superior
mvn test -Dtest=HigherOrderTest

# Test de variación de parámetros
mvn test -Dtest=VariationOfParametersTest

# Test de condiciones iniciales
mvn test -Dtest=InitialConditionsTest
```

### Ejecución verbose
```bash
mvn test -X    # Debug detallado
mvn test -e    # Stack trace completo
```

---

## 🚀 Ejecución de API

### Opción 1: Ejecutar con Maven
```bash
mvn spring-boot:run
```

**Salida esperada:**
```
2025-11-14 22:50:00 - Started GeogeraApplication in 3.5 seconds
  Tomcat started on port(s): 8080 (http)
```

### Opción 2: Ejecutar JAR
```bash
mvn package -DskipTests
java -jar target/geogera-0.1.jar
```

### Opción 3: Desde IDE
```
Main.java → Click derecho → Run As → Java Application
```

---

## 📝 Casos de Prueba

### A. Ecuaciones Homogéneas Grado 1

#### Prueba 1.1: y' + 2y = 0
```bash
curl -X POST "http://localhost:8080/api/solve" \
  -H "Content-Type: application/json" \
  -d '{
    "equation": "y'"'"' + 2*y = 0",
    "variable": "x"
  }'
```

**Respuesta esperada:**
```json
{
  "status": "success",
  "finalSolution": "C*e^(-2*x)",
  "steps": [...]
}
```

#### Prueba 1.2: y' - 3y = 0
```bash
curl -X POST "http://localhost:8080/api/solve" \
  -H "Content-Type: application/json" \
  -d '{
    "equation": "y'"'"' - 3*y = 0",
    "variable": "x"
  }'
```

---

### B. Ecuaciones Homogéneas Grado 2

#### Prueba 2.1: Raíces Reales Distintas
```bash
# y'' - 5y' + 6y = 0
# Raíces: r₁ = 2, r₂ = 3
curl -X POST "http://localhost:8080/api/solve" \
  -H "Content-Type: application/json" \
  -d '{
    "equation": "y'"'"''"'"' - 5*y'"'"' + 6*y = 0",
    "variable": "x"
  }'
```

**Respuesta esperada:**
```json
{
  "status": "success",
  "finalSolution": "C1*e^(2*x) + C2*e^(3*x)"
}
```

#### Prueba 2.2: Raíces Reales Repetidas
```bash
# y'' - 4y' + 4y = 0
# Raíces: r = 2 (multiplicidad 2)
curl -X POST "http://localhost:8080/api/solve" \
  -H "Content-Type: application/json" \
  -d '{
    "equation": "y'"'"''"'"' - 4*y'"'"' + 4*y = 0",
    "variable": "x"
  }'
```

**Respuesta esperada:**
```json
{
  "status": "success",
  "finalSolution": "(C1 + C2*x)*e^(2*x)"
}
```

#### Prueba 2.3: Raíces Complejas Conjugadas
```bash
# y'' + 4y = 0
# Raíces: r = ±2i
curl -X POST "http://localhost:8080/api/solve" \
  -H "Content-Type: application/json" \
  -d '{
    "equation": "y'"'"''"'"' + 4*y = 0",
    "variable": "x"
  }'
```

**Respuesta esperada:**
```json
{
  "status": "success",
  "finalSolution": "C1*cos(2*x) + C2*sin(2*x)"
}
```

#### Prueba 2.4: Raíces Complejas con Parte Real
```bash
# y'' + 2y' + 5y = 0
# Raíces: r = -1 ± 2i
curl -X POST "http://localhost:8080/api/solve" \
  -H "Content-Type: application/json" \
  -d '{
    "equation": "y'"'"''"'"' + 2*y'"'"' + 5*y = 0",
    "variable": "x"
  }'
```

**Respuesta esperada:**
```json
{
  "status": "success",
  "finalSolution": "e^(-x)*(C1*cos(2*x) + C2*sin(2*x))"
}
```

---

### C. Ecuaciones Homogéneas Grado 3+

#### Prueba 3.1: Tres Raíces Reales Distintas
```bash
# y''' - 6y'' + 11y' - 6y = 0
# Raíces: r₁ = 1, r₂ = 2, r₃ = 3
curl -X POST "http://localhost:8080/api/solve" \
  -H "Content-Type: application/json" \
  -d '{
    "equation": "y'"'"''"'"''"'"' - 6*y'"'"''"'"' + 11*y'"'"' - 6*y = 0",
    "variable": "x"
  }'
```

**Respuesta esperada:**
```json
{
  "status": "success",
  "finalSolution": "C1*e^x + C2*e^(2*x) + C3*e^(3*x)"
}
```

#### Prueba 3.2: Raíz Triple
```bash
# y''' - 3y'' + 3y' - y = 0
# Raíces: r = 1 (multiplicidad 3)
curl -X POST "http://localhost:8080/api/solve" \
  -H "Content-Type: application/json" \
  -d '{
    "equation": "y'"'"''"'"''"'"' - 3*y'"'"''"'"' + 3*y'"'"' - y = 0",
    "variable": "x"
  }'
```

**Respuesta esperada:**
```json
{
  "status": "success",
  "finalSolution": "e^x*(C1 + C2*x + C3*x^2)"
}
```

---

### D. Ecuaciones Homogéneas Grado 4

#### Prueba 4.1: Raíces Mixtas
```bash
# y'''' - 1 = 0
# Raíces: r = 1, r = -1, r = i, r = -i
curl -X POST "http://localhost:8080/api/solve" \
  -H "Content-Type: application/json" \
  -d '{
    "equation": "y'"'"''"'"''"'"''"'"' - 1 = 0",
    "variable": "x"
  }'
```

---

### E. Ecuaciones con Condiciones Iniciales

#### Prueba 5.1: CI de Grado 2
```bash
curl -X POST "http://localhost:8080/api/solve" \
  -H "Content-Type: application/json" \
  -d '{
    "equation": "y'"'"''"'"' - 5*y'"'"' + 6*y = 0",
    "variable": "x",
    "initialConditions": ["y(0) = 1", "y'"'"'(0) = 0"]
  }'
```

**Respuesta esperada:**
```json
{
  "status": "success",
  "finalSolution": "y = 2*e^(2*x) - e^(3*x)",
  "constants": {"C1": 2, "C2": -1}
}
```

---

### F. Pruebas de Derivadas Simbólicas

```java
// Prueba en código Java
@Test
void testDerivatives() {
    // Derivada primera
    String d1 = SymbolicDifferentiator.firstDerivative("x^2");
    assertEquals("2*x", d1);
    
    // Derivada segunda
    String d2 = SymbolicDifferentiator.secondDerivative("x^3");
    assertEquals("6*x", d2);
    
    // Simplificación
    String simp = SymbolicDifferentiator.simplify("x^2 + 2*x + 1");
    assertTrue(simp.contains("(x+1)") || simp.contains("(x+1)^2"));
}
```

---

## ✅ Validación

### 1. Validación de Compilación
```bash
mvn clean compile
# Debe terminar con: [INFO] BUILD SUCCESS
# 0 errores, 0 warnings
```

### 2. Validación de Estructura
```bash
# Verificar clases clave
test -f target/classes/com/ecuaciones/diferenciales/api/service/ODESolver.class && echo "✅ ODESolver"
test -f target/classes/com/ecuaciones/diferenciales/model/solver/homogeneous/PolynomialSolver.class && echo "✅ PolynomialSolver"
test -f target/classes/com/ecuaciones/diferenciales/model/solver/nonhomogeneous/UndeterminedCoeff.class && echo "✅ UndeterminedCoeff"
test -f target/classes/com/ecuaciones/diferenciales/model/solver/nonhomogeneous/VariationOfParametersSolver.class && echo "✅ VariationOfParametersSolver"
test -f target/classes/com/ecuaciones/diferenciales/utils/SymbolicDifferentiator.class && echo "✅ SymbolicDifferentiator"
```

### 3. Validación de API
```bash
# Health check
curl -s http://localhost:8080/api/health | jq .

# Debe devolver:
# {
#   "status": "UP"
# }
```

### 4. Validación de Respuesta JSON
```bash
# Ejemplo de respuesta
curl -s -X POST "http://localhost:8080/api/solve" \
  -H "Content-Type: application/json" \
  -d '{"equation": "y'"'"' + y = 0", "variable": "x"}' | jq .

# Debe tener estructura:
# {
#   "status": "success",
#   "finalSolution": "...",
#   "steps": [...],
#   "executionTimeMs": ...
# }
```

---

## 📊 Matriz de Pruebas

| # | Tipo | Ecuación | Raíces | Estado |
|---|------|----------|--------|--------|
| 1.1 | ODE-1 | y' + 2y = 0 | r=-2 | ⏳ |
| 1.2 | ODE-1 | y' - 3y = 0 | r=3 | ⏳ |
| 2.1 | ODE-2 | y'' - 5y' + 6y = 0 | r=2,3 | ⏳ |
| 2.2 | ODE-2 | y'' - 4y' + 4y = 0 | r=2(2) | ⏳ |
| 2.3 | ODE-2 | y'' + 4y = 0 | r=±2i | ⏳ |
| 2.4 | ODE-2 | y'' + 2y' + 5y = 0 | r=-1±2i | ⏳ |
| 3.1 | ODE-3 | y''' - 6y'' + 11y' - 6y = 0 | r=1,2,3 | ⏳ |
| 3.2 | ODE-3 | y''' - 3y'' + 3y' - y = 0 | r=1(3) | ⏳ |
| 4.1 | ODE-4 | y'''' - 1 = 0 | Mixtas | ⏳ |
| 5.1 | CI | Con condiciones iniciales | Mixtas | ⏳ |

**Leyenda:** ⏳ = Pendiente de ejecutar

---

## 🐛 Debugging

### Habilitar logs detallados
```bash
# En application.properties
logging.level.root=DEBUG
logging.level.com.ecuaciones.diferenciales=DEBUG
```

### Ver stack trace completo
```bash
mvn test -e
mvn spring-boot:run -e
```

### Verificar dependencias descargadas
```bash
ls -la ~/.m2/repository/org/matheclipse/matheclipse-core/
ls -la ~/.m2/repository/org/springframework/boot/
```

---

## 📞 Soporte

**Documentos relacionados:**
- `ANALISIS_TECNICO_COMPLETO.md`
- `RESUMEN_FINAL_2025.md`
- `PLAN_INTEGRACION_COMPLETO.md`

---

**Última Actualización:** 14/11/2025  
**Estado:** ✅ Listo para Testing
