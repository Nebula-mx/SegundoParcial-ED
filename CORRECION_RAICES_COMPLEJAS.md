# Corrección de Raíces Complejas y Soporte para y^n

## Problema Identificado

Tu resolvedor de ecuaciones diferenciales presentaba varios problemas críticos:

### 1. **Parseo incorrecto de raíces complejas** ❌
- **Archivo afectado:** `PolynomialSolver.java`
- **Síntoma:** Para `y''' - y = 0`, mostraba raíz `0` con multiplicidad 2, en lugar de las raíces complejas conjugadas `-1/2 ± i√3/2`
- **Causa:** El método `parseSymjaRoot` parseaba frágil la representación en string de números complejos, fallando con fracciones (`-1/2`) y raíces (`√3/2`)

### 2. **Consolidación duplicada de raíces complejas** ❌
- **Archivo afectado:** `RootConsolidator.java`
- **Síntoma:** Los pares conjugados complejos se trataban como raíces distintas, generando términos duplicados en la solución homogénea
- **Causa:** No se consideraba que `α + βi` y `α - βi` son pares que deben consolidarse

### 3. **Almacenamiento de signo incorrecto** ❌
- **Archivo afectado:** `Root.java`
- **Síntoma:** Se normalizaba siempre a magnitud positiva, impidiendo distinguir entre raíces positivas e imaginarias negativas
- **Causa:** `this.imaginary = cleanValue(Math.abs(imaginary))` forzaba a positivos siempre

### 4. **Parser no soportaba `y^n` sin paréntesis** ❌
- **Archivo afectado:** `EcuationParser.java`
- **Síntoma:** `y^4 - 16y = 0` fallaba con "No se pudo extraer el polinomio característico"
- **Causa:** El patrón regex solo capturaba `y^(n)` con paréntesis, no `y^n`

---

## Soluciones Aplicadas

### ✅ 1. Parseo Correcto de Raíces Complejas

**Cambio en `PolynomialSolver.java`:**

```java
// ANTES (incorrecto):
private static Root parseSymjaRoot(IExpr expr) {
    String exprStr = expr.toString().trim();
    // Intentaba parsear frágil strings como "-0.5+I*0.866025"
    // Fallaba con fracciones y raíces
    ...
}

// DESPUÉS (correcto):
private static Root parseSymjaRoot(IExpr expr) {
    ExprEvaluator evaluator = new ExprEvaluator();
    String exprStr = expr.toString().trim();

    // Usar Symja para extraer numéricamente Re e Im
    IExpr re = evaluator.eval("N[Re[" + exprStr + "]]");
    IExpr im = evaluator.eval("N[Im[" + exprStr + "]]");

    double realPart = re.evalDouble();  // Convierte -1/2 → -0.5
    double imagPart = im.evalDouble();  // Convierte √3/2 → 0.866...

    return new Root(realPart, imagPart, 1);
}
```

**Ventajas:**
- ✅ Maneja fracciones automáticamente
- ✅ Maneja raíces (sqrt) automáticamente
- ✅ Usa evaluación numérica en lugar de parseo de strings

---

### ✅ 2. Consolidación Correcta de Pares Conjugados

**Cambio en `RootConsolidator.java`:**

```java
// Raíz compleja: buscar conjugado (α + βi con α - βi)
int conjugateIndex = -1;
for (int i = 0; i < rootsToProcess.size(); i++) {
    Root next = rootsToProcess.get(i);
    if (Math.abs(curReal - next.getReal()) < TOLERANCE && 
        Math.abs(curImag + next.getImaginary()) < TOLERANCE) {
        conjugateIndex = i;
        multiplicity = Math.max(multiplicity, next.getMultiplicity());
        break;
    }
}

// Si encontramos el conjugado, removerlo
if (conjugateIndex >= 0) {
    rootsToProcess.remove(conjugateIndex);
}

// Normalizar a representación canónica (magnitud positiva)
consolidated.add(new Root(curReal, Math.abs(curImag), multiplicity));
```

**Ventajas:**
- ✅ Consolidar α + βi con α - βi en UNA sola entrada
- ✅ Evita duplicación de términos en `y_h(x)`
- ✅ Usa máximo de multiplicidad (no suma)

---

### ✅ 3. Almacenamiento Correcto de Signo

**Cambio en `Root.java`:**

```java
// ANTES (incorrecto):
this.imaginary = cleanValue(Math.abs(imaginary));  // Siempre positivo

// DESPUÉS (correcto):
this.imaginary = cleanValue(imaginary);  // Permite signo
```

Luego en `toString()` y `HomogeneousSolver`, se usa `Math.abs(imaginary)` para presentación pero se mantiene el signo internamente para consolidación.

---

### ✅ 4. Soporte para `y^n` sin Paréntesis

**Cambio en `EcuationParser.java`:**

```java
// ANTES (incorrecto):
private static final Pattern TERM_PATTERN = Pattern.compile(
    "([+-]\\s*\\d*\\.?\\d+\\s*)" + 
    "\\*?\\s*" +
    "(y'''|y''|y'|y\\^\\(\\d+\\)|y)"  // NO soporta y^n
);

// DESPUÉS (correcto):
private static final Pattern TERM_PATTERN = Pattern.compile(
    "([+-]\\s*\\d*\\.?\\d+\\s*)" + 
    "\\*?\\s*" +
    "(y'''|y''|y'|y\\^\\(\\d+\\)|y\\^\\d+|y)"  // Soporta y^n
);
```

Y agregar lógica para parsear `y^n`:

```java
} else if (derivativeStr.matches("y\\^\\d+")) {
    Pattern p = Pattern.compile("y\\^(\\d+)");
    Matcher m = p.matcher(derivativeStr);
    if (m.find()) {
        order = Integer.parseInt(m.group(1));
    }
}
```

---

## Resultados Comprobados

### Test 1: `y''' - y = 0`
**Raíces esperadas:** 1, -1/2 + i√3/2, -1/2 - i√3/2  
**Raíces obtenidas:** ✅ 1, -0.5 + 0.866i, -0.5 - 0.866i  
**Solución:**
```
y(x) = C₁·e^x + e^(-0.5x)·(C₂·cos(0.866x) + C₃·sin(0.866x))
```

### Test 2: `y^4 - 16y = 0`
**Raíces esperadas:** -2, 2i, -2i, 2  
**Raíces obtenidas:** ✅ -2, 2i, -2i, 2  
**Solución:**
```
y(x) = C₁·e^(-2x) + C₂·cos(2x) + C₃·sin(2x) + C₄·e^(2x)
```

### Test 3: `y'' - 5y' + 6y = 0` (casos reales distintos)
**Raíces esperadas:** 3, 2  
**Raíces obtenidas:** ✅ 3, 2  
**Solución:**
```
y(x) = C₁·e^(3x) + C₂·e^(2x)
```

---

## Archivos Modificados

| Archivo | Cambios |
|---------|---------|
| `PolynomialSolver.java` | Cambio en `parseSymjaRoot` + remoción de debug prints |
| `Root.java` | Permitir signo en parte imaginaria |
| `RootConsolidator.java` | Consolidación correcta de conjugados |
| `EcuationParser.java` | Soporte para `y^n` sin paréntesis |

---

## Validación

✅ Todas las pruebas pasan correctamente  
✅ El programa ahora soporta ambas notaciones: `y^(4)` y `y^4`  
✅ Las raíces complejas se parsean y consolidan correctamente  
✅ La salida es limpia (sin mensajes de debug)  

---

## Notas para Futuros Desarrollos

1. **Precisión numérica:** Considera ajustar `TOLERANCE` si necesitas mayor/menor precisión
2. **Multiplicidad:** El método actual usa `Math.max()` para pares conjugados. Si necesitas suma, revisar `RootConsolidator`
3. **Formatos adicionales:** El parser puede extenderse para soportar más formatos de entrada
