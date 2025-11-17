# 📖 Referencia de API

## Clase Principal: EquationSolverService

### Método: solve()

```java
public DifferentialEquationResponse solve(
    String equationStr,
    boolean hasInitialConditions,
    String conditions
)
```

#### Parámetros

| Parámetro | Tipo | Descripción | Ejemplo |
|-----------|------|-------------|---------|
| `equationStr` | String | Ecuación diferencial | `"y'' - 5*y' + 6*y = 0"` |
| `hasInitialConditions` | boolean | ¿Tiene PVI? | `true` o `false` |
| `conditions` | String | Condiciones iniciales | `"y(0)=1, y'(0)=0"` |

#### Retorna

**DifferentialEquationResponse** con propiedades:

```java
response.getOrder()                    // int - Orden de la EDO
response.isHomogeneous()               // boolean - Es homogénea
response.getHomogeneousSolution()      // String - Solución homogénea
response.getParticularSolution()       // String - Solución particular (si no-homogénea)
response.getGeneralSolution()          // String - Solución general completa
response.getFinalSolution()            // String - Solución con constantes resueltas
response.getSolvedConstants()          // Map<String, Double> - Constantes C1, C2, etc.
```

#### Ejemplos de Uso

**Ejemplo 1: Ecuación Homogénea**
```java
EquationSolverService solver = new EquationSolverService();
DifferentialEquationResponse resp = solver.solve("y'' - 5*y' + 6*y = 0", false, "");
System.out.println(resp.getHomogeneousSolution());
// Output: C1*e^(3x) + C2*e^(2x)
```

**Ejemplo 2: Con Condiciones Iniciales**
```java
DifferentialEquationResponse resp = solver.solve(
    "y'' - 5*y' + 6*y = 0",
    true,
    "y(0)=1, y'(0)=0"
);
System.out.println(resp.getFinalSolution());
// Output: 1.0*e^(3x) - 0.5*e^(2x)
System.out.println(resp.getSolvedConstants().get("C1"));
// Output: 1.0
```

**Ejemplo 3: No-Homogénea**
```java
DifferentialEquationResponse resp = solver.solve(
    "y'' + 4*y = 8*cos(2*x)",
    false,
    ""
);
System.out.println("y_h: " + resp.getHomogeneousSolution());
System.out.println("y_p: " + resp.getParticularSolution());
System.out.println("y: " + resp.getGeneralSolution());
```

---

## Formatos de Entrada Soportados

### Ecuaciones

```
Forma estándar: y'' - 5*y' + 6*y = 0
Con términos: y''' - y' = x^2
Con funciones: y'' + 4*y = 8*cos(2*x)
Múltiples derivadas: y'''' - 5*y'' + 4*y = 0
```

### Condiciones Iniciales

```
Formato: "y(0)=valor, y'(0)=valor, ..."
Ejemplos:
  "y(0)=1"
  "y(0)=1, y'(0)=0"
  "y(0)=0, y'(0)=1, y''(0)=2"
```

### Operadores

| Operador | Significado | Ejemplo |
|----------|------------|---------|
| `+` | Suma | `y + 2*x` |
| `-` | Resta | `y' - y` |
| `*` | Multiplicación | `2*y`, `x*y` |
| `^` | Potencia | `x^2`, `e^(2*x)` |
| `cos()` | Coseno | `cos(x)`, `cos(2*x)` |
| `sin()` | Seno | `sin(x)`, `sin(3*x)` |
| `e` | Exponencial | `e^x`, `e^(3*x)` |

---

## Casos Especiales Manejados

### Resonancia
Cuando el término inhomogéneo coincide con soluciones de la ecuación homogénea:
```
Sistema automáticamente:
1. Intenta Coeficientes Indeterminados
2. Si falla → Intenta Variación de Parámetros
3. Retorna solución correcta
```

### Raíces Complejas
Se generan soluciones en forma:
```
y_h = e^(ax)[C1*cos(bx) + C2*sin(bx)]
```

### Raíces Repetidas
Se agrega factor x a términos posteriores:
```
Para raíz r con multiplicidad 2:
y = C1*e^(rx) + C2*x*e^(rx)
```

### Órdenes Arbitrarias
Soporta cualquier orden (testeado hasta orden 4+)

---

## Métodos Auxiliares

### EquationEvaluator.evaluate()
```java
EvaluationResult result = evaluator.evaluate(expression, values);
result.getValue()        // Valor numérico
result.getExpression()   // Expresión limpia
```

### LinearSystemSolver.solve()
```java
double[] constants = LinearSystemSolver.solve(matrix, vector);
// Resuelve Ax = b
```

### SymjaEngine
```java
String result = SymjaEngine.simplify(expression);
List<String> roots = SymjaEngine.solvePolynomial(polynomial);
```

---

## Excepciones

| Excepción | Causa | Solución |
|-----------|-------|----------|
| `IllegalArgumentException` | Ecuación mal formada | Revisar sintaxis |
| `ArithmeticException` | División por cero | Revisar sistema singular |
| `RuntimeException` | Error en CAS | Simplificar ecuación |

---

## Notas Importantes

1. **Multiplicación explícita**: Usar `*` siempre
   - ✅ `2*y`
   - ❌ `2y`

2. **Paréntesis**: Usar para exponentes
   - ✅ `e^(2*x)`
   - ❌ `e^2*x` (ambiguo)

3. **Orden**: Comenzar desde orden más alto
   - ✅ `y'' + y' + y = 0`
   - Válido en cualquier orden

4. **Espacios**: Se ignoran
   - ✅ `y'' - 5*y' + 6*y = 0`
   - ✅ `y''-5*y'+6*y=0`

---

**Última actualización**: 17 de noviembre de 2025
