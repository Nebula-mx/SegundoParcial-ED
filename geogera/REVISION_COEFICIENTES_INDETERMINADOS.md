# 🔍 REVISIÓN: LÓGICA DE COEFICIENTES INDETERMINADOS

## 📋 RESUMEN EJECUTIVO

Tras revisar el código de `UndeterminedCoeff.java` y `UndeterminedCoeffResolver.java`, he identificado la estructura y lógica general está **BIEN IMPLEMENTADA**, pero hay **áreas de riesgo** que necesitan validación en casos específicos.

---

## ✅ ASPECTOS CORRECTOS IDENTIFICADOS

### 1. Separación de Términos Base vs. Términos y_p*

**En `UndeterminedCoeff.java` (líneas 35-37):**
```java
private final List<String> baseUCTerms = new ArrayList<>();     // Para FILAS
private final List<String> ypStarTerms = new ArrayList<>();     // Para COLUMNAS
```

**✓ CORRECTO:** Esta separación es fundamental:
- `baseUCTerms`: Términos sin factor de resonancia x^s (usados como FILAS)
- `ypStarTerms`: Términos CON factor de resonancia x^s (usados como COLUMNAS)

### 2. Detección de Resonancia

**En `getFormForSingleTerm()` (línea 114):**
```java
int s = findDuplicityFactor(alpha, beta);
String xResonanceFactor = s == 0 ? "" : (s == 1 ? "x" : getXPower(s));
```

**✓ CORRECTO:** 
- `s = 0`: Sin resonancia
- `s = 1`: Multiplicar por `x`
- `s > 1`: Multiplicar por `x^s` (para multiplicidades mayores)

### 3. Construcción de la Matriz A|b

**En `UndeterminedCoeffResolver.java` (líneas 110-130):**
```java
for (int i = 0; i < numRows; i++) {        // FILAS (términos base)
    String baseTerm_i = baseUCTerms.get(i);
    
    for (int j = 0; j < numCols; j++) {    // COLUMNAS (términos yp*)
        String ypTerm_j = ypStarTerms.get(j);
        
        for (int k = 0; k <= order; k++) {  // Derivadas
            String derived_tj = SymbolicDifferentiator.calculateDerivative(ypTerm_j, k);
            double functionalCoeff = getRobustExtractedCoeff(derived_tj, baseTerm_i);
            totalCoefficientOfTerm_base += a_k * functionalCoeff;
        }
        matrixA[i][j] = totalCoefficientOfTerm_base;
    }
    vectorB[i] = getRobustExtractedCoeff(gX, baseTerm_i);  // Lado derecho
}
```

**✓ CORRECTO:** 
- Forma correcta: A[i,j] = Σ a_k * (d^k/dx^k [yp_j]) evaluado en baseTerm_i
- Vector b correcto: b[i] = coeficiente de baseTerm_i en g(x)

---

## ⚠️ ÁREAS DE RIESGO IDENTIFICADAS

### Riesgo 1: Extracción de Coeficientes (getRobustExtractedCoeff)

**En `UndeterminedCoeffResolver.java` (líneas 56-103):**

```java
private double getRobustExtractedCoeff(String expression, String functionalTerm) {
    String normalizedExpr = SymbolicDifferentiator.simplify(expression);
    String normalizedTerm = SymbolicDifferentiator.simplify(functionalTerm);
    
    // ... lógica de extracción ...
    String patternString = "(^|\\*)" + Pattern.quote(normalizedTerm) + "(\\*|$)";
    Matcher m = Pattern.compile(patternString).matcher(cleanTerm);
}
```

**⚠️ RIESGO:**
- Depende de que `SymbolicDifferentiator.simplify()` produzca formas **CANÓNICAS**
- Si la simplificación no es consistente, pueden no coincidirse términos equivalentes

**Ejemplo problemático:**
```
Término 1: "cos(2*x)"
Término 2: "cos(2x)"
Término 3: "2*cos(x)*cos(x) - sin(2x)"  (Identidad trigonométrica)

Si simplify() no normaliza, podrían no matchear
```

### Riesgo 2: Derivadas y Simplificación

**En `getFormForSingleTerm()` (línea 155):**
```java
String derived_tj = SymbolicDifferentiator.calculateDerivative(ypTerm_j, k);
```

**⚠️ RIESGO:**
- Las derivadas de términos complejos (exponencial × trigonométrico, polinomial × exponencial) pueden producir expresiones complicadas
- Si la simplificación no normaliza correctamente, la extracción de coeficientes puede fallar

**Ejemplo:**
```
yp = x * e^(-x) * cos(2x)

d/dx[yp] = e^(-x) * cos(2x) - x * e^(-x) * cos(2x) - 2 * x * e^(-x) * sin(2x)
         = e^(-x) * (cos(2x) - x*cos(2x) - 2*x*sin(2x))
         = e^(-x) * (cos(2x)*(1-x) - 2*x*sin(2x))

¿Se simplifica correctamente? Depende de SymbolicDifferentiator
```

### Riesgo 3: Casos de Multiplicidades Altas

**En `findDuplicityFactor()` (líneas 122-130):**
```java
private int findDuplicityFactor(double alpha, double beta) {
    int s = 0;
    for (Root r : homogeneousRoots) {
        if (Math.abs(alpha - rAlpha) < TOLERANCE && 
            Math.abs(absBeta - rBeta) < TOLERANCE) {
            s = Math.max(s, r.getMultiplicity());
        }
    }
    return s;
}
```

**⚠️ RIESGO:**
- ¿Qué pasa si hay múltiples raíces con la MISMA parte real e imaginaria?
- El código usa `Math.max(s, r.getMultiplicity())` que es correcto, pero...

**Caso problemático:**
```
Ecuación: (D-1)³ * (D+2) * y = e^x  (resonancia con multiplicidad 3)
Raíces: r = 1 (m=3), r = -2 (m=1)
Forzamiento: e^x (frecuencia α=1, β=0)

Esperado: s = 3, así que y_p debería ser x³ * A * e^x

El código haría: s = max(0, 3) = 3 ✓ CORRECTO
```

### Riesgo 4: Casos Trigonométricos Mixtos

**En `getFormForSingleTerm()` (líneas 161-212):**

Cuando hay forzamiento trigonométrico como `cos(3x) + sin(3x)`:

```
Forzamiento: cos(3x) + sin(3x)
Análisis: 2 términos separados (uno para cos, otro para sin)
Cada uno genera su propio par de términos (cos, sin)
```

**⚠️ RIESGO:**
- Puede haber duplicación de términos en `baseUCTerms` y `ypStarTerms`
- Aunque hay deduplicación final (líneas 266-271), ¿funciona correctamente?

```java
Set<String> uniqueBaseTerms = new LinkedHashSet<>(this.baseUCTerms);
this.baseUCTerms.clear();
this.baseUCTerms.addAll(uniqueBaseTerms);
```

**Ejemplo:**
```
Forzamiento: cos(2x) + sin(2x)

Término cos(2x):
├─ baseUCTerms.add("cos(2x)")
└─ baseUCTerms.add("sin(2x)")

Término sin(2x):
├─ baseUCTerms.add("cos(2x)")  ← DUPLICADO
└─ baseUCTerms.add("sin(2x)")  ← DUPLICADO

Después de deduplicación:
baseUCTerms = {"cos(2x)", "sin(2x)"}  ✓ CORRECTO
```

---

## 🔧 VALIDACIÓN POR CASOS DE USO

### Caso 1: Polinomio Simple (SIN Resonancia)

**Ecuación:** `y'' - y = x² + 1`

```
Raíces: r = ±1
Forzamiento: x² (grado 2, α=0, β=0)
Resonancia: s = 0 (no hay raíz en α=0)

Forma propuesta: A + Bx + Cx²

baseUCTerms = ["1", "x", "x²"]
ypStarTerms = ["1", "x", "x²"]

LÓGICA: ✅ CORRECTA
```

### Caso 2: Exponencial CON Resonancia

**Ecuación:** `y'' - 3y' + 2y = e^x`

```
Raíces: r = 1, 2
Forzamiento: e^x (α=1, β=0)
Resonancia: s = 1 (r=1 es raíz)

Forma propuesta: x * A * e^x

findDuplicityFactor(1, 0):
  ├─ Revisa r=1: Math.abs(1-1)=0 < TOLERANCE ✓
  ├─ Math.abs(0-0)=0 < TOLERANCE ✓
  ├─ Multiplicidad de r=1 es 1
  └─ s = max(0, 1) = 1

xResonanceFactor = "x"

baseUCTerms = ["e^x"]
ypStarTerms = ["x * e^x"]

LÓGICA: ✅ CORRECTA
```

### Caso 3: Trigonométrico CON Resonancia

**Ecuación:** `y'' + y = sin(x)`

```
Raíces: r = ±i (α=0, β=1)
Forzamiento: sin(x) (α=0, β=1)
Resonancia: s = 1 (raíces ±i son raíces)

Forma propuesta: x * (A * cos(x) + B * sin(x))

findDuplicityFactor(0, 1):
  ├─ Revisa r=i: Math.abs(0-0)=0 < TOLERANCE ✓
  ├─ Math.abs(1-1)=0 < TOLERANCE ✓
  ├─ Multiplicidad es 1
  └─ s = max(0, 1) = 1

xResonanceFactor = "x"

baseUCTerms = ["cos(x)", "sin(x)"]
ypStarTerms = ["x*cos(x)", "x*sin(x)"]

LÓGICA: ✅ CORRECTA
```

### Caso 4: Mixto Complejo (Exponencial × Polinomial CON Resonancia)

**Ecuación:** `y'' - 2y' + y = x * e^x`

```
Raíces: r = 1 (m=2)
Forzamiento: x * e^x (exponencial e^x con polinomio x)
Análisis del forzamiento:
├─ Tipo: EXPONENCIAL
├─ α = 1, β = 0
├─ Polinomio multiplicador: grado 1
└─ Resonancia: s = 2 (multiplicidad de r=1)

Forma propuesta: x² * (A + Bx) * e^x
               = x² * (A * e^x + B * x * e^x)
               = x² * A * e^x + x³ * B * e^x

baseUCTerms = ["e^x", "x*e^x"]
ypStarTerms = ["x²*e^x", "x³*e^x"]

¿Es correcto?
- Término 1: e^x tiene resonancia s=2, se multiplica por x²  ✓
- Término 2: x*e^x tiene resonancia s=2, se multiplica por x² → x³*e^x ✓

LÓGICA: ✅ CORRECTA
```

---

## 🔬 VERIFICACIÓN CRÍTICA: EXTRACCIÓN DE COEFICIENTES

**La función `getRobustExtractedCoeff()` es CRÍTICA. Veamos su robustez:**

### Ejemplo 1: Extracción Simple

```
expression = "2*cos(2x) + 3*sin(2x)"
functionalTerm = "cos(2x)"

Normalizado:
├─ expression → "2*cos(2x)+3*sin(2x)"
└─ functionalTerm → "cos(2x)"

Procesamiento:
├─ Divide: ["2*cos(2x)", "3*sin(2x)"]
├─ Primer término "2*cos(2x)" contiene "cos(2x)"
│  └─ Coeficiente: 2.0 ✓
├─ Segundo término "3*sin(2x)" NO contiene "cos(2x)"
└─ RESULTADO: 2.0

CORRECTO ✓
```

### Ejemplo 2: Casos Complicados

```
expression = "3*x*e^(-x)*cos(2x) - 2*x*e^(-x)*sin(2x)"
functionalTerm = "cos(2x)"

Normalizado:
├─ expression → "3*x*e^(-x)*cos(2x)-2*x*e^(-x)*sin(2x)"
└─ functionalTerm → "cos(2x)"

Procesamiento:
├─ Divide: ["3*x*e^(-x)*cos(2x)", "-2*x*e^(-x)*sin(2x)"]
├─ Primer término contiene "cos(2x)"
│  ├─ Reemplazar: "3*x*e^(-x)*@@@"
│  ├─ Extraer coef: "3*x*e^(-x)"
│  └─ ¿Es un número? NO → currentCoeff = 0.0 ❌
└─ RESULTADO: 0.0

PROBLEMA: El coeficiente es "3*x*e^(-x)" que NO es un número puro
```

**⚠️ ESTO ES UN BUG POTENCIAL EN EL CÓDIGO:**

```java
try {
    currentCoeff = Double.parseDouble(coeffStr);  // "3*x*e^(-x)" → Exception
} catch (NumberFormatException e) {
    currentCoeff = 0.0;  // ← Se ignora el coeficiente
}
```

---

## 🚨 PROBLEMAS ENCONTRADOS

### Problema 1: Coeficientes No-Numéricos

**Ubicación:** `UndeterminedCoeffResolver.java`, línea 88-90

**Descripción:**
Si el coeficiente tiene variables (x, e^..., etc.), se retorna 0.0 erróneamente.

**Ejemplo que falla:**
```
Derivada: "3*x*e^(-x)*cos(2x)"
Funcional: "cos(2x)"
Coef esperado: 3*x*e^(-x)
Coef obtenido: 0.0 ❌
```

**Impacto:**
- Bajo en ecuaciones simples (polinomios, exponenciales puros)
- ALTO en ecuaciones complejas (mezclas de tipos)

### Problema 2: Simplificación No-Canónica

**Ubicación:** Depende de `SymbolicDifferentiator.simplify()`

**Riesgo:**
Si dos formas algebraicamente equivalentes se escriben diferente después de simplificar:
```
Forma A: "cos(2x) + sin(2x)"
Forma B: "sqrt(2)*sin(2x + pi/4)"

Si simplify() no las iguala, hay mismatch en extracción
```

### Problema 3: Derivadas Complejas

**Ubicación:** `SymbolicDifferentiator.calculateDerivative()`

**Riesgo:**
Las derivadas de funciones complejas pueden no simplificarse correctamente

---

## ✅ RECOMENDACIONES

### Recomendación 1: Validar `SymbolicDifferentiator`

```java
// Agregar logs para cada derivada calculada
System.out.println("Derivada " + k + " de " + ypTerm_j + " = " + derived_tj);
```

### Recomendación 2: Mejorar Extracción de Coeficientes

Cambiar el manejo de excepciones:

```java
try {
    currentCoeff = Double.parseDouble(coeffStr);
} catch (NumberFormatException e) {
    // En lugar de retornar 0, loguear el error
    System.err.println("ADVERTENCIA: No se pudo parsear coeficiente: " + coeffStr);
    System.err.println("  Expresión: " + cleanTerm);
    System.err.println("  Término: " + normalizedTerm);
    currentCoeff = 0.0;  // Fallback
}
```

### Recomendación 3: Validación Post-Resolución

Verificar que la solución y_p satisface la ecuación:

```java
// Después de resolver y_p, substituir en la ecuación
// y verificar que se obtiene g(x)
```

---

## 📊 MATRIZ DE VALIDACIÓN

| Caso | Tipo | Resonancia | Estado | Riesgo |
|------|------|-----------|--------|--------|
| Polinomial simple | P(x) | NO | ✅ | Bajo |
| Exponencial simple | e^(αx) | Sí/No | ✅ | Bajo |
| Trigonométrico | sin/cos(βx) | Sí/No | ✅ | Medio |
| Exponencial×Polinomial | P(x)e^(αx) | Sí/No | ⚠️ | Alto |
| Exponencial×Trigon. | e^(αx)sin/cos | Sí/No | ⚠️ | Alto |
| Multiplicidades altas | Cualquiera | Sí | ⚠️ | Medio |

---

## 🎯 CONCLUSIÓN

**Estado General:** LÓGICA BIEN IMPLEMENTADA, pero con RIESGOS en:
1. ✅ Estructura base: CORRECTA
2. ✅ Resonancia: CORRECTA
3. ⚠️ Extracción de coeficientes: RIESGOSA en casos complejos
4. ⚠️ Simplificación: DEPENDENCIA EXTERNA

**Recomendación:**
- Los tests 126/126 PASANDO sugieren que los casos comunes funcionan bien
- Verificar especialmente con ecuaciones que mezclen exponencial×trigonométrico
- Agregar logs de depuración en `getRobustExtractedCoeff()` y `calculateDerivative()`

