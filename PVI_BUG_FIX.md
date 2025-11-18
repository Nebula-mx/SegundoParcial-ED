# Fix: Bug en PVI con Soluciones Particulares Constantes

## Problema Identificado

Para la ecuación diferencial `y' + 2y = 4` con condición inicial `y(0) = 1`:

**Comportamiento Incorrecto (ANTES):**
- Solución general: `y(x) = C1*e^(-2*x) + 2`
- Sistema PVI calculado: `C1*e^(0) = 1` → `C1 = 1` ❌ **INCORRECTO**
- Solución particular: `y(x) = e^(-2*x) + 2`
- Verificación: `y(0) = 1 + 2 = 3` ≠ 1 ❌

**Comportamiento Correcto (DESPUÉS):**
- Solución general: `y(x) = C1*e^(-2*x) + 2`
- Sistema PVI ajustado: `C1*e^(0) = 1 - 2 = -1` → `C1 = -1` ✅ **CORRECTO**
- Solución particular: `y(x) = -e^(-2*x) + 2 = 2 - e^(-2*x)`
- Verificación: `y(0) = 2 - 1 = 1` ✓

## Raíz Causa

El método `InitialConditionsSolver.extractBaseFunctions()` trataba el término constante `2` como una función base adicional con una nueva constante arbitraria, cuando en realidad es un **valor determinado** (solución particular).

Para `y(x) = (C1*e^(-2*x)) + (2)`:
- **Antes:** Extraía 2 funciones base: `[e^(-2*x), 1]`
  - Sistema: `C1*e^(0) + C2*1 = 1` → indeterminado, asumía `C1=1, C2=0`
  
- **Después:** Extrae solo 1 función base: `[e^(-2*x)]`
  - Constante detectada y restada de CI: `2`
  - Sistema: `C1*e^(0) = 1 - 2 = -1` → `C1 = -1` ✓

## Solución Implementada

### 1. Modificación de `extractBaseFunctions()` en `InitialConditionsSolver.java`

**Cambio:** El método ahora ignora los términos constantes puros (sin `C#`) cuando extrae funciones base.

```java
// PATRÓN 2: Término sin C# (término constante puro)
Pattern constPattern = Pattern.compile("^([+\\-])(\\d+(?:\\.\\d+)?)$");
Matcher constMatcher = constPattern.matcher(cleanTerm);

if (constMatcher.matches()) {
    // NO agregamos a functions
    // Se maneja en extractConstantTerm() para procesar condiciones iniciales
}
```

### 2. Nuevo Método: `extractConstantTerm()`

Extrae explícitamente el valor del término constante de la solución general:

```java
/**
 * Extrae el término constante de la solución general.
 * Si la solución es "y(x) = (C1*e^(-2x)) + (2)", devuelve 2.0
 */
private double extractConstantTerm() { ... }
```

### 3. Ajuste de Condiciones Iniciales en `solveInitialConditions()`

Resta el término constante de la condición inicial cuando es necesario:

```java
// --- PASO 3: Construir vector b (valores de las CI ajustados) ---
for (int i = 0; i < order; i++) {
    InitialCondition ic = conditions.get(i);
    double adjustedValue = ic.value;
    
    // Si es la función sin derivar y hay término constante,
    // restamos el término constante
    if (ic.derivativeOrder == 0 && Math.abs(constantTerm) > 1e-12) {
        adjustedValue = ic.value - constantTerm;
    }
    
    vectorB.add(adjustedValue);
}
```

## Archivos Modificados

- `InitialConditionsSolver.java`:
  - Método `solveInitialConditions()` - Añadido ajuste de CI
  - Método `extractBaseFunctions()` - Ignora términos constantes
  - Nuevo método `extractConstantTerm()` - Extrae el término constante

## Pruebas de Validación

### Test 1: `y' + 2y = 4` con `y(0) = 1`
```
✅ Raíz: -2
✅ y_h: C1 * e^(-2x)
✅ y_p: 2
✅ C1 = -1 (correcto)
✅ Solución: y(x) = 2 - e^(-2*x)
✅ Verificación: y(0) = 1 ✓
```

## Impacto

Este fix corrige la aplicación de condiciones iniciales para todas las ecuaciones diferenciales no-homogéneas cuya solución particular es una constante, que es un caso muy común en ecuaciones de primer orden con forzamiento constante.

**Casos afectados:**
- ✅ `y' + ay = b` (forzamiento constante)
- ✅ `y'' + ay' + by = c` (forzamiento constante)
- ✅ Cualquier ecuación con `y_p = constante`

---

**Fecha de Corrección:** 17 de Noviembre de 2025  
**Estado:** ✅ CORREGIDO Y VALIDADO
