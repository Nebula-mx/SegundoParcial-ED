# 📋 Plan de Limpieza y Reorganización de src/

## ✅ DEBE MANTENERSE

### Controllers API (Keep: `api/controller/`)
- ✅ `api/controller/ODEController.java` - REST principal endpoint
- ✅ `api/controller/WebViewController.java` - Vista web
- ❌ `api/controllers/DifferentialController.java` - **ELIMINAR** (duplicate typo: "controllers" en lugar de "controller")

### Services
- ✅ `api/service/ODESolver.java`
- ✅ `api/service/StepBuilder.java`

### DTOs (Data Transfer Objects)
- ✅ `api/dto/ExpressionData.java`
- ✅ `api/dto/SolutionResponse.java`
- ✅ `api/dto/Step.java`

### Core Solvers
- ✅ `model/solver/homogeneous/HomogeneousSolver.java`
- ✅ `model/solver/homogeneous/PolynomialSolver.java`
- ✅ `model/solver/homogeneous/RootConsolidator.java`
- ✅ `model/solver/nonhomogeneous/VariationOfParametersSolver.java`
- ✅ `model/solver/nonhomogeneous/UndeterminedCoeff.java`
- ✅ `model/solver/nonhomogeneous/UndeterminedCoeffResolver.java`
- ✅ `model/solver/nonhomogeneous/FunctionAnalyzer.java`
- ✅ `model/solver/InitialConditionsSolver.java`

### Model & Parsing
- ✅ `model/App.java`
- ✅ `model/Expression.java`
- ✅ `model/EcuationParser.java`
- ✅ `model/ODEParser.java`
- ✅ `model/SolveCharacteristicFunction.java`
- ✅ `model/roots/Root.java`

### Templates & Variation
- ✅ `model/templates/ExpressionData.java`
- ✅ `model/templates/FunctionType.java`
- ✅ `model/variation/WronskianCalculator.java`

### Utilities
- ✅ `utils/SymjaEngine.java`
- ✅ `utils/MatrixSolver.java`
- ✅ `utils/LinearSystemSolver.java`
- ✅ `utils/IntegrationUtils.java`
- ✅ `utils/SymbolicDifferentiator.java`

### Configuration
- ✅ `config/WebConfig.java`

### Entry Points
- ✅ `Main.java`
- ✅ `GeogeraApplication.java`

### Tests (TODOS LOS 69)
- ✅ `api/controller/HigherOrderTest.java`
- ✅ `api/controller/InitialConditionsTest.java`
- ✅ `api/controller/LeibnizNotationTest.java`
- ✅ `api/controller/ODEControllerTest.java`
- ✅ `api/controller/VariationOfParametersTest.java`
- ✅ `api/controller/VeryHighOrderTest.java`

---

## ❌ DEBE ELIMINARSE

### 1. **Servlet Obsoleto** (Spring Boot no usa Servlets)
```
src/main/java/com/ecuaciones/diferenciales/example/servlet/ResolvedorEDOServlet.java
```
**Razón**: Proyecto usa Spring Boot REST, no Servlets. Mantener sería confuso.

### 2. **Duplicate Controller Folder**
```
src/main/java/com/ecuaciones/diferenciales/api/controllers/
```
**Razón**: Carpeta con typo ("controllers" vs "controller"). El único controlador aquí es:
- `DifferentialController.java` - Parece ser duplicado/antiguo de `ODEController.java`

### 3. **SolverServlet.java** (Si existe)
```
src/main/java/com/ecuaciones/diferenciales/web/SolverServlet.java
```
**Razón**: Otra implementación Servlet. Spring Boot REST es el estándar.

---

## 📊 Resumen de Cambios

| Categoría | Acción | Cantidad |
|-----------|--------|----------|
| Eliminar archivos | 3 | Servlets + controllers duplicate |
| Eliminar carpetas vacías | 2-3 | example/servlet, api/controllers/ |
| Mantener clases | 32 | En src/main/java |
| Mantener tests | 6 | En src/test/java (69 tests) |
| **Total limpio** | **✅** | **Estructura Maven correcta** |

---

## 🎯 Objetivo Final

```
src/main/java/com/ecuaciones/diferenciales/
├── api/
│   ├── controller/          ✅ (sin el s)
│   ├── service/
│   └── dto/
├── config/
├── model/
│   ├── roots/
│   ├── solver/
│   │   ├── homogeneous/
│   │   └── nonhomogeneous/
│   ├── templates/
│   └── variation/
├── utils/
├── Main.java
└── GeogeraApplication.java

src/test/java/com/ecuaciones/diferenciales/
└── api/controller/          ✅ 6 test files (69 tests)
```

---

## ⚡ Ejecución de Limpieza

```bash
# 1. Eliminar Servlets
rm -rf src/main/java/com/ecuaciones/diferenciales/example/servlet/
rm -rf src/main/java/com/ecuaciones/diferenciales/web/

# 2. Eliminar controllers duplicado
rm -rf src/main/java/com/ecuaciones/diferenciales/api/controllers/

# 3. Limpiar carpeta padre vacía
rmdir src/main/java/com/ecuaciones/diferenciales/example/ 2>/dev/null || true

# 4. Verificar
find src/main/java -name "*.java" | wc -l  # Debería ser ~32

# 5. Compilar para verificar
./compile.sh

# 6. Correr tests
mvn test
```

**Esperado después de limpieza:**
- ✅ 32 clases Java
- ✅ 69 tests pasando
- ✅ 0 Errores de compilación
- ✅ Estructura Maven estándar
