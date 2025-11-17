# 📚 Documentación - Solver de Ecuaciones Diferenciales Ordinarias (EDO)

## 🎯 Descripción General

Este proyecto es un **solver completo de ecuaciones diferenciales ordinarias (EDO)** implementado en Java que resuelve:

- **Ecuaciones homogéneas** de cualquier orden
- **Ecuaciones no-homogéneas** mediante:
  - Método de Coeficientes Indeterminados (UC)
  - Método de Variación de Parámetros (VP)
- **Problemas de Valor Inicial (PVI)** con aplicación automática de condiciones iniciales

---

## 🏗️ Arquitectura del Proyecto

```
src/main/java/com/ecuaciones/diferenciales/
├── Main.java                          # Interfaz interactiva CLI
├── model/
│   ├── EcuationParser.java           # Parseo de ecuaciones
│   ├── ODEParser.java                # Análisis de ODEs
│   └── solver/
│       ├── homogeneous/              # Solucionadores homogéneos
│       ├── nonhomogeneous/           # Solucionadores no-homogéneos
│       ├── InitialConditionsSolver.java
│       └── SymbolicDifferentiator.java
├── service/
│   └── EquationSolverService.java   # Orquestador principal
├── evaluator/
│   └── EquationEvaluator.java       # Evaluación de expresiones
└── utils/
    ├── SymjaEngine.java              # CAS (Computer Algebra System)
    ├── MatrixSolver.java             # Resolución de sistemas lineales
    └── LinearSystemSolver.java
```

---

## 🚀 Uso del Sistema

### 1. **Interfaz Interactiva (CLI)**

```bash
cd geogera
mvn clean compile
mvn exec:java -Dexec.mainClass="com.ecuaciones.diferenciales.Main"
```

**Ejemplo de uso:**
```
Ingrese la ecuación diferencial (ej: y'' - 5y' + 6y = x^2):
> y'' - 5y' + 6y = 0

¿Es problema de valor inicial? (s/n):
> s

Ingrese condiciones iniciales (ej: y(0)=1):
> y(0)=1

Ingrese y'(0):
> 0

[Sistema resuelve y muestra la solución con constantes calculadas]
```

### 2. **Uso Programático (API)**

```java
EquationSolverService solver = new EquationSolverService();
DifferentialEquationResponse response = solver.solve(
    "y'' - 5y' + 6y = 0",
    true,  // tiene PVI
    "y(0)=1, y'(0)=0"
);
System.out.println(response.getSolution());
```

---

## 📋 Métodos Principales

### `EquationSolverService.solve()`
Método principal que orquesta todo el proceso:
1. Parsea la ecuación
2. Extrae raíces características
3. Genera solución homogénea
4. Genera solución particular (si es no-homogénea)
5. Aplica condiciones iniciales (si existen)
6. Retorna solución en formato JSON

**Parámetros:**
- `equationStr`: Ecuación en formato texto
- `hasInitialConditions`: Boolean indicando si hay PVI
- `conditions`: String con condiciones (ej: "y(0)=1,y'(0)=0")

**Retorna:** `DifferentialEquationResponse` con:
- Solución general
- Solución particular (si aplica)
- Constantes resueltas (si hay PVI)
- Metadata

---

## 🔧 Solucionadores Disponibles

### Homogéneos
- **PolynomialSolver**: Calcula raíces de polinomio característico
- **HomogeneousSolver**: Genera $y_h(x) = \sum C_i e^{r_i x}$

### No-Homogéneos
- **UndeterminedCoeff**: Método UC para términos polinomiales/trigonométricos
- **VariationOfParametersSolverV2**: Método VP general (fallback)

### PVI
- **InitialConditionsSolver**: Resuelve sistema lineal para constantes

---

## 💡 Ejemplos de Ecuaciones Soportadas

| Ecuación | Tipo | Método |
|----------|------|--------|
| $y'' - 5y' + 6y = 0$ | Homogénea orden 2 | Polinomial |
| $y'' + 4y = 8\cos(2x)$ | No-homogénea | UC |
| $y''' - y' = x^2$ | No-homogénea orden 3 | UC |
| $y'''' - 5y'' + 4y = 0$ | Homogénea orden 4 | Polinomial |
| Con $y(0)=1, y'(0)=0$ | PVI | Sistema Lineal |

---

## 📊 Estructura de Respuesta JSON

```json
{
  "homogeneousSolution": "C1*e^(3x) + C2*e^(2x)",
  "particularSolution": "- 0.3333 * x^3",
  "generalSolution": "(C1*e^(3x) + C2*e^(2x)) + (- 0.3333 * x^3)",
  "solvedConstants": {
    "C1": 1.0,
    "C2": -0.5
  },
  "finalSolution": "1.0*e^(3x) - 0.5*e^(2x) - 0.3333*x^3",
  "order": 2,
  "isHomogeneous": false
}
```

---

## 🔍 Características Principales

✅ **Resolución automática** de ecuaciones diferenciales  
✅ **Aplicación de PVI** con resolución numérica de constantes  
✅ **Formato limpio** de expresiones simplificadas  
✅ **Manejo de errores** robusto con fallback automático  
✅ **Soporte de ecuaciones** de orden arbitrario  
✅ **Integración CAS** mediante Symja/Matheclipse  
✅ **Salida JSON** compatible con APIs REST

---

## 📦 Dependencias

- **Java 17**
- **Maven 3.9+**
- **Symja/Matheclipse** (CAS)
- **Jackson** (JSON)

---

## 🛠️ Compilación y Testing

```bash
# Compilación
mvn clean compile

# Ejecución
mvn exec:java -Dexec.mainClass="com.ecuaciones.diferenciales.Main"

# JAR ejecutable
mvn clean package
java -jar target/geogera-1.0.jar
```

---

## 📝 Notas Técnicas

- El sistema utiliza **análisis simbólico** mediante Symja para factorizar polinomios
- Las expresiones se **limpian automáticamente** eliminando términos innecesarios
- El **fallback automático** UC→VP maneja casos de resonancia
- Las **constantes se calculan numéricamente** resolviendo sistemas lineales

---

**Versión:** 1.0  
**Última actualización:** 17 de noviembre de 2025
