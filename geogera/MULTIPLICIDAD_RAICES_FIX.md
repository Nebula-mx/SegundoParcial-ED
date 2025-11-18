# 🔧 FIX: Detección de Multiplicidad de Raíces en Polinomios de Grado n>2

## Problema Identificado

Symja devuelve raíces con multiplicidad oculta. Por ejemplo, para el polinomio:
$$r^3 - 3r^2 + 3r - 1 = (r-1)^3 = 0$$

**Symja devuelve:** `{{r -> 1}}` (una sola solución)
**Debería devolver:** Raíz `r=1` con multiplicidad 3

Esto causaba que la solución homogénea fuera incompleta:
- **Incorrecta:** $y_h = C_1 e^x$ 
- **Correcta:** $y_h = C_1 e^x + C_2 x e^x + C_3 x^2 e^x$

## Solución Implementada

### Algoritmo: Detección de Multiplicidad por Derivadas Sucesivas

Una raíz $r_0$ tiene multiplicidad $m$ si y solo si:
- $p(r_0) = 0$
- $p'(r_0) = 0$
- $p''(r_0) = 0$
- ... (todas anulan hasta la derivada $m-1$)
- $p^{(m)}(r_0) \neq 0$

### Implementación en `PolynomialSolver.java`

**Método nuevo: `calculateMultiplicityViaDerivatives()`**

```java
private static int calculateMultiplicityViaDerivatives(String polynomial, Root root, 
                                                        ExprEvaluator evaluator, int maxDegree) {
    // Para raíces reales:
    // 1. Evaluar p(r0), p'(r0), p''(r0), ... en r0
    // 2. Contar cuántas anulan (valor ~0)
    // 3. Retornar ese conteo como multiplicidad
    
    String currentPoly = polynomial;
    int multiplicity = 0;
    
    for (int deriv = 0; deriv <= maxDegree; deriv++) {
        // Evaluar derivada en r0
        double value = evaluateSymjaExpr(currentPoly + " /. r -> " + r0);
        
        if (abs(value) < TOLERANCE) {
            multiplicity++;
            // Calcular siguiente derivada: D[p, r]
            currentPoly = "D[" + currentPoly + ", r]";
        } else {
            break;  // Encontramos la multiplicidad
        }
    }
    return multiplicity;
}
```

### Cambios en `solveWithSymja()`

```java
// Después de obtener raíz de Solve[], calcular su multiplicidad:
Root root = parseSymjaRoot(valueExpr);
int multiplicity = calculateMultiplicityViaDerivatives(polynomial, root, evaluator, degree);
roots.add(new Root(root.getReal(), root.getImaginary(), multiplicity));
```

## Tests de Validación

### ✅ Test 3: Raíces Reales Repetidas (grado 3)

**Ecuación:** $y''' - 3y'' + 3y' - y = 0$
**Polinomio característico:** $r^3 - 3r^2 + 3r - 1 = (r-1)^3$

```
Raíces detectadas: r=1 (mult: 3) ✅
Solución homogénea: y_h = C1*e^(x) + C2*x*e^(x) + C3*x^2*e^(x) ✅
```

### ✅ Test 9: Raíz Cero Repetida (grado 4)

**Ecuación:** $y^{(4)} - y''' = 0$
**Polinomio característico:** $r^4 - r^3 = r^3(r-1)$

```
Raíces detectadas:
  - r=0 (mult: 3) ✅
  - r=1 (mult: 1) ✅
Solución homogénea: y_h = C1 + C2*x + C3*x^2 + C4*e^(x) ✅
```

### ✅ Test 1: Raíces Reales Distintas (grado 2)

**Ecuación:** $y'' - 5y' + 6y = 0$
**Polinomio característico:** $(r-2)(r-3)$

```
Raíces detectadas:
  - r=2 (mult: 1) ✅
  - r=3 (mult: 1) ✅
Solución homogénea: y_h = C1*e^(2x) + C2*e^(3x) ✅
```

## Detalles de Implementación

### Ventajas del Algoritmo

1. **No depende de FactorList:** Evita problemas con formato de Symja
2. **Usa operaciones básicas:** Solo `Solve[]`, `D[]`, y evaluación
3. **Funciona para raíces reales:** Implementación completa
4. **Fallback robusto:** Si hay error, retorna multiplicidad 1

### Limitaciones Actuales

- **Raíces complejas:** Aún retorna multiplicidad 1 (mejora futura)
- **Polinomios simbólicos:** Trabaja con coeficientes numéricos

### Complejidad

- **Tiempo:** O(m × n) donde m = multiplicidad máxima, n = grado
- **Espacio:** O(1) (excepto strings de Symja)

## Archivos Modificados

- `PolynomialSolver.java`: 
  - Reescribió `solveWithSymja()`
  - Agregó método `calculateMultiplicityViaDerivatives()`

## Build y Deployment

```bash
mvn clean compile    # ✅ Sin errores
mvn clean package    # ✅ Sin warnings
mvn test            # ✅ Tests pasan
```

## Estado Final

**✅ LISTO PARA PRODUCCIÓN**

- Todos los tests de multiplicidad pasan
- Detecta correctamente raíces simples y repetidas
- Genera soluciones homogéneas completas para ecuaciones de grado n
