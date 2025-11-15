# 🔧 PLAN DE CORRECCIÓN - ENLAZAMIENTO NO-HOMOGÉNEAS

**Generado:** 14 de Noviembre de 2025, 23:11 UTC  
**Estado:** Revisión de enlaces completada  
**Problemas Identificados:** 2  
**Prioridad:** MEDIA-BAJA (Sistema 95% funcional)

---

## 📋 PROBLEMAS IDENTIFICADOS

### Problema #1: Resonancia Trigonométrica No Detectada ⚠️

**Severidad:** MEDIA  
**Test Afectado:** Test 4 (`testResonanceSinusoidalTerm`)  
**Síntoma:** `y'' + y = sin(x)` no genera factor `x*` en la solución particular

#### Caso de Prueba
```
Entrada: y'' + y = sin(x)
Raíces de la característica: r = ±i (números complejos puros)
g(x) = sin(x) (frecuencia = 1)
Condición de resonancia: ω (frecuencia de g) = |im(raíz)| ✅ DETECTADA

PERO: La forma y_p propuesta es SIN factor x
Esperado: y_p = x*(A*cos(x) + B*sin(x))
Obtenido: y_p = A*cos(x) + B*sin(x)
```

#### Ubicación del Código
- **Archivo:** `/src/main/java/com/ecuaciones/diferenciales/model/solver/nonhomogeneous/UndeterminedCoeff.java`
- **Métodos Involucrados:**
  - `getParticularSolutionForm(String gX)`
  - `detectResonance()` (si existe)
  - `getYpStarTerms()` (genera términos con resonancia)

#### Análisis del Debug
```
DEBUG OUTPUT:
--- DEBUG: Sistema Lineal A|b ---
Coeficientes (Columnas/Nombres): [A, B]
Términos Yp* (Columnas): [x*cos(1x), x*sin(1x)]  ← ✅ Se generan correctamente
Términos Base (Filas): [cos(1x), sin(1x)]
Fila 0 (Termino: cos(1x)) [0.0000       0.0000  ] | b = 0.0000
Fila 1 (Termino: sin(1x)) [0.0000       0.0000  ] | b = 1.0000

⚠️ Error: Sistema singular o indeterminado
```

**Interpretación:**
- Los términos `x*cos(1x)` y `x*sin(1x)` se generan ✅
- PERO la matriz A queda singular (filas de ceros)
- Esto ocurre porque no se está sustituyendo correctamente en las derivadas

#### Solución Propuesta

**Opción A:** Validar cálculo de derivadas en resonancia
```java
// En UndeterminedCoeffResolver.java
// Verificar que se derivan correctamente los términos x*sin(x) y x*cos(x)

// d/dx[x*sin(x)] = sin(x) + x*cos(x)
// d²/dx²[x*sin(x)] = cos(x) + cos(x) - x*sin(x) = 2*cos(x) - x*sin(x)

// Estas derivadas deben sustituirse correctamente en la ecuación
```

**Opción B:** Usar VariationOfParametersSolver en su lugar
```java
// Para casos de resonancia, usar VdP en lugar de UC
if (detectResonance(g(x), roots)) {
    return useVariationOfParameters(roots, gX);
}
```

**Opción Recomendada:** Opción A (más completo, mantiene UC)

---

### Problema #2: Step sin Descripción (NullPointer) 🔴

**Severidad:** BAJA  
**Test Afectado:** Test 10 (`testCompleteFlowValidation`)  
**Síntoma:** `NullPointerException` en `Step.getDescription()`

#### Caso de Prueba
```java
response.getSteps().stream()
    .anyMatch(step -> step.getDescription().toLowerCase().contains("general"))
    // NullPointerException aquí ↑
```

#### Ubicación del Código
- **Archivo:** `StepBuilder.java` o `ODESolver.java`
- **Problema:** Algunos Steps se crean sin descripción
  ```java
  new Step(StepType.XXX, title, null)  // ← description = null
  ```

#### Solución Propuesta
```java
// Opción 1: Validar en el test (rápida)
response.getSteps().stream()
    .anyMatch(step -> step.getDescription() != null && 
              step.getDescription().toLowerCase().contains("general"))

// Opción 2: Garantizar descripción en todos los Steps (correcta)
// En StepBuilder.addCustomStep() y otros métodos:
if (explanation == null || explanation.isEmpty()) {
    explanation = "Paso " + (step número);
}
```

**Recomendación:** Opción 2 (asegurar invariante en la clase)

---

## 🛠️ PLAN DE CORRECCIÓN DETALLADO

### Corrección #1: Resonancia Trigonométrica

#### Paso 1: Diagnóstico Adicional
```bash
# Ejecutar test con debug
mvn test -Dtest=NonhomogeneousIntegrationTest#testResonanceSinusoidalTerm -X 2>&1 | grep -A 50 "DEBUG"
```

#### Paso 2: Revisar Cálculo de Derivadas
**Archivo:** `UndeterminedCoeffResolver.java`  
**Método:** `buildSystemMatrix()`

```java
// Verificar que esto funciona correctamente:
// Para y_p = x*sin(x):
// y_p' = sin(x) + x*cos(x)
// y_p'' = 2*cos(x) - x*sin(x)

// Luego sustituir en y'' + y = sin(x):
// (2*cos(x) - x*sin(x)) + x*sin(x) = sin(x)
// 2*cos(x) = sin(x)  ← Esto debería derivar los coeficientes

// Validar que SymbolicDifferentiator.calculateDerivative() 
// está siendo usado correctamente
```

#### Paso 3: Prueba de Corrección
```bash
# Después de corregir, ejecutar:
mvn test -Dtest=NonhomogeneousIntegrationTest#testResonanceSinusoidalTerm
# Debe pasar: ✅
```

---

### Corrección #2: Validar Descripciones

#### Paso 1: Localizar todos los addStep()
```bash
grep -r "addStep\|withDescription\|new Step" src/main/java --include="*.java" \
  | grep -v "addStep\(" | head -20
```

#### Paso 2: Modificar StepBuilder
**Archivo:** `StepBuilder.java`

```java
// Método: asegurar descripción en todos los pasos
private void validateStep(Step step) {
    if (step.getDescription() == null || step.getDescription().isEmpty()) {
        step.setDescription("Paso " + (steps.size() + 1) + ": " + step.getType());
    }
}

// O en constructor de Step:
public Step(..., String explanation) {
    this.explanation = explanation != null ? explanation : "(Sin descripción)";
}
```

#### Paso 3: Prueba
```bash
mvn test -Dtest=NonhomogeneousIntegrationTest#testCompleteFlowValidation
# Debe pasar: ✅
```

---

## 📊 VALIDACIÓN POST-CORRECCIÓN

### Test Suite Esperado
```
ANTES:
  Tests ejecutados: 12
  Pasados: 10 ✅
  Fallos: 1 ❌
  Errores: 1 🔴
  
DESPUÉS (esperado):
  Tests ejecutados: 12
  Pasados: 12 ✅
  Fallos: 0
  Errores: 0
```

### Checklist de Validación
```
[ ] Compilación sin errores
[ ] Test 4 (Resonancia trigonométrica) ✅
[ ] Test 10 (Flow validation) ✅
[ ] Todos los demás tests aún pasen
[ ] No hay regresiones
[ ] Código sigue siendo legible
```

---

## 📈 IMPACTO Y ALCANCE

### Funcionalidad Habilitada por Correcciones
```
ANTES:
- ❌ y'' + y = sin(x) con resonancia
- ❌ y'' + 4y = cos(2x) con resonancia
- ❌ Cualquier trig con ω = frecuencia propia

DESPUÉS:
- ✅ Todas las resonancias trigonométricas
- ✅ Mejor cobertura de casos
- ✅ Suite de tests 100% verde
```

### Riesgo de Cambios
**Riesgo:** BAJO
- Las correcciones son localizadas
- No afectan la arquitectura general
- Casos sin resonancia ya funcionan

---

## ⏰ ESTIMACIÓN DE ESFUERZO

| Tarea | Tiempo | Dificultad |
|-------|--------|-----------|
| Diagnóstico de resonancia | 15-30 min | MEDIA |
| Corrección de derivadas | 30-60 min | MEDIA-ALTA |
| Prueba y validación | 10-15 min | BAJA |
| Validación de descripciones | 5-10 min | BAJA |
| **Total** | **60-115 min** | **MEDIA** |

---

## 📝 NOTAS TÉCNICAS

### Sobre Resonancia Trigonométrica
```
La resonancia ocurre cuando:
- g(x) = A*cos(ωx) + B*sin(ωx)
- Las raíces de la característica son ±iω

En este caso:
- Sin resonancia: y_p = A*cos(ωx) + B*sin(ωx)
- Con resonancia: y_p = x*(A*cos(ωx) + B*sin(ωx))

El factor x modifica significativamente las derivadas y
debe ser considerado en la construcción del sistema Ax=b.
```

### Verificación Manual
```
Para y'' + y = sin(x):
Raíces: r = ±i
g(x) = sin(x) (ω = 1)
Resonancia: ✅ i = 1*i

Forma sin resonancia: y_p = A*cos(x) + B*sin(x)
y_p' = -A*sin(x) + B*cos(x)
y_p'' = -A*cos(x) - B*sin(x)

Sustituyendo en y'' + y = sin(x):
(-A*cos(x) - B*sin(x)) + (A*cos(x) + B*sin(x)) = sin(x)
0 = sin(x)  ← ¡Inconsistencia! Por eso falla

Forma CON resonancia: y_p = x*(A*cos(x) + B*sin(x))
y_p' = A*cos(x) + B*sin(x) + x*(-A*sin(x) + B*cos(x))
y_p'' = -2A*sin(x) + 2B*cos(x) + x*(-A*cos(x) - B*sin(x))

Sustituyendo en y'' + y = sin(x):
[-2A*sin(x) + 2B*cos(x) + x*(-A*cos(x) - B*sin(x))]
+ [x*(A*cos(x) + B*sin(x))] = sin(x)

-2A*sin(x) + 2B*cos(x) = sin(x)

Sistema:
-2A = 1  →  A = -1/2
2B = 0   →  B = 0

y_p = -x/2 * cos(x)  ✅ Correcto
```

---

## 🔗 REFERENCIAS RELACIONADAS

- `ANALISIS_TECNICO_COMPLETO.md` - Arquitectura detallada
- `GUIA_TESTING.md` - Casos de prueba
- `UndeterminedCoeff.java` - Clase a corregir
- `UndeterminedCoeffResolver.java` - Solucionador de coeficientes

---

**Estado:** 📋 Listo para correcciones  
**Siguiente Paso:** Implementar Corrección #1 (Resonancia)  
**Responsable:** Equipo de desarrollo  
**Fecha Estimada de Resolución:** 15 de Noviembre de 2025
