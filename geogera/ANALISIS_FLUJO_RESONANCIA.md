# 🔍 ANÁLISIS - FLUJO DE LECTURA DE ECUACIONES Y RESONANCIA

**Fecha:** 14 de Noviembre de 2025, 23:20 UTC  
**Tema:** Cómo se leen las ecuaciones y cómo afecta la resonancia  
**Problema Identificado:** Resonancia trigonométrica no se detecta correctamente

---

## 📋 FLUJO COMPLETO DE LECTURA

```
┌──────────────────────────────────────────────────────────┐
│  INPUT: "y'' + y = sin(x)"                               │
└──────────────────────────────────────────────────────────┘
                         ↓
┌──────────────────────────────────────────────────────────┐
│  1. ExpressionData (DTO - API)                           │
│     equation: "y'' + y = sin(x)"                         │
│     variable: "x"                                        │
│     initialConditions: []                                │
└──────────────────────────────────────────────────────────┘
                         ↓
┌──────────────────────────────────────────────────────────┐
│  2. EcuationParser.parse()                               │
│     ├─ Divide por "="                                    │
│     │  ├─ EDO: "y'' + y"                                │
│     │  └─ g(x): "sin(x)"                                │
│     │                                                    │
│     ├─ Extrae coeficientes:                             │
│     │  ├─ y'': coeff = +1                              │
│     │  └─ y:   coeff = +1                              │
│     │                                                    │
│     └─ Crea ExpressionData (modelo interno)            │
│        ├─ orden: 2                                      │
│        ├─ coeficientes: [1, 1, 1]                       │
│        ├─ rightSide: "sin(x)"                           │
│        └─ homogeneo: false                              │
└──────────────────────────────────────────────────────────┘
                         ↓
┌──────────────────────────────────────────────────────────┐
│  3. ODESolver.solveDifferentialEquation()                │
│     ├─ Detecta: "No-homogénea"                          │
│     │                                                    │
│     ├─ PolynomialSolver.solve([1, 1, 1])               │
│     │  ├─ Ecuación característica: r² + 1 = 0          │
│     │  └─ Raíces: r = ±i                               │
│     │                                                    │
│     ├─ HomogeneousSolver.generate(roots)                │
│     │  └─ y_h = C1*cos(x) + C2*sin(x)                  │
│     │                                                    │
│     └─ [NO-HOMOGÉNEA] UndeterminedCoeff(roots)         │
│        ├─ Recibe: roots = [±i]                         │
│        ├─ Recibe: g(x) = "sin(x)"                      │
│        │                                                │
│        └─ 🔴 AQUÍ EMPIEZA EL PROBLEMA                  │
│           ├─ Detecta g(x) = sin(x)                     │
│           ├─ Detecta raíces = ±i (frecuencia = 1)     │
│           ├─ ¿RESONANCIA? ω = |Im(raíz)| = 1          │
│           │  ✅ SÍ, hay resonancia                     │
│           │                                             │
│           ├─ Propone forma: ???                        │
│           │  ✅ DEBERÍA: y_p = x*(A*cos(x) + B*sin(x)) │
│           │  ❌ ACTUALMENTE: y_p = A*cos(x) + B*sin(x) │
│           │                                             │
│           └─ Construye sistema Ax = b                  │
│              ├─ Matriz A queda SINGULAR                │
│              └─ Sistema NO se puede resolver           │
└──────────────────────────────────────────────────────────┘
```

---

## 🔴 PROBLEMA IDENTIFICADO

### En UndeterminedCoeff.getParticularSolutionForm()

**Ubicación:** `UndeterminedCoeff.java`, línea ~230

**Código Actual (INCORRECTO):**
```java
public String getParticularSolutionForm(String gX) {
    // ... análisis de g(x) ...
    
    if (isCosinusoidal(gX)) {
        // Extrae frecuencia ω de cos(ωx) o sin(ωx)
        double omega = extractFrequency(gX);
        
        // 🔴 FALTA AQUÍ: Detectar resonancia
        // if (hasResonance(omega)) {
        //     return "x*(A*cos(" + omega + "x) + B*sin(" + omega + "x))";
        // }
        
        // Actualmente solo hace:
        return "A*cos(" + omega + "x) + B*sin(" + omega + "x)";  // SIN FACTOR X
    }
    // ...
}
```

---

## ✅ SOLUCIÓN PROPUESTA

### Paso 1: Crear método detectResonance()

```java
private boolean detectResonance(double omega) {
    // Buscar si ±iω está en la lista de raíces
    for (Root root : homogeneousRoots) {
        double real = Math.abs(root.getReal());
        double imag = Math.abs(root.getImaginary());
        
        // Resonancia: real ≈ 0 AND |imag| ≈ omega
        if (real < TOLERANCE && Math.abs(imag - omega) < TOLERANCE) {
            return true;  // ✅ RESONANCIA DETECTADA
        }
    }
    return false;  // Sin resonancia
}
```

### Paso 2: Modificar getParticularSolutionForm()

```java
public String getParticularSolutionForm(String gX) {
    FunctionAnalyzer analyzer = new FunctionAnalyzer();
    FunctionType type = analyzer.analyzeType(gX);
    
    if (type == FunctionType.SINUSOIDAL) {
        double omega = extractFrequency(gX);
        
        // 🔑 CLAVE: Detectar resonancia
        if (detectResonance(omega)) {
            // ✅ CON RESONANCIA: Incluir factor x
            return "x*(A*cos(" + omega + "x) + B*sin(" + omega + "x))";
        } else {
            // SIN RESONANCIA: Sin factor x
            return "A*cos(" + omega + "x) + B*sin(" + omega + "x)";
        }
    }
    // ... resto del código ...
}
```

---

## 📊 IMPACTO EN CONDICIONES INICIALES

El problema de resonancia también afecta cómo se leen las condiciones iniciales:

```
Ejemplo: y'' + y = sin(x) con CI: y(0)=0, y'(0)=0

FLUJO DE CI:
───────────
1. InitialConditionsSolver.parseConditions()
   ├─ Parsea: "y(0)=0" → InitialCondition(derivOrder=0, x0=0, value=0)
   ├─ Parsea: "y'(0)=0" → InitialCondition(derivOrder=1, x0=0, value=0)
   └─ ✅ Parsing correcto

2. InitialConditionsSolver.solveInitialConditions()
   ├─ Extrae funciones base de y_general
   │  ├─ SI CORRECCIÓN: ["cos(x)", "sin(x)", "x*cos(x)", "x*sin(x)"]
   │  └─ ACTUALMENTE: ["cos(x)", "sin(x)"]
   │
   └─ Construye matriz A
      ├─ A[0,0] = cos(0) = 1
      ├─ A[0,1] = sin(0) = 0
      ├─ A[1,0] = d/dx[cos(x)]|x=0 = -sin(0) = 0
      ├─ A[1,1] = d/dx[sin(x)]|x=0 = cos(0) = 1
      │
      ├─ A = [1, 0]  ← Sistema 2x2 (puede estar mal)
      │       [0, 1]
      │
      └─ 🔴 SIN RESONANCIA: Sistema singular
      └─ ✅ CON RESONANCIA: Sistema tiene más términos
```

---

## 🔗 CONEXIÓN ENTRE COMPONENTES

### Flujo Actual (INCORRECTO)

```
UndeterminedCoeff.getParticularSolutionForm()
  ├─ NO detecta resonancia ❌
  ├─ Propone forma SIN factor x
  │
  └─→ UndeterminedCoeffResolver.buildSystemMatrix()
      ├─ Construye derivadas de términos sin x
      ├─ Sustituye en la ecuación
      │
      └─→ LinearSystemSolver.solve()
          ├─ Matriz A queda singular
          ├─ System.err: "El sistema es singular"
          └─ Falla la resolución ❌
```

### Flujo Corregido (CORRECTO)

```
UndeterminedCoeff.getParticularSolutionForm()
  ├─ DETECTA resonancia ✅
  ├─ Propone forma CON factor x
  │
  └─→ UndeterminedCoeffResolver.buildSystemMatrix()
      ├─ Construye derivadas de x*sin(x) y x*cos(x)
      ├─ d/dx[x*sin(x)] = sin(x) + x*cos(x)
      ├─ d²/dx²[x*sin(x)] = 2*cos(x) - x*sin(x)
      ├─ Sustituye en la ecuación
      │
      └─→ LinearSystemSolver.solve()
          ├─ Matriz A no es singular ✅
          ├─ Sistema tiene solución única
          └─ Obtiene A, B valores ✅
```

---

## 🛠️ PASOS PARA CORREGIR

### 1. Modificar UndeterminedCoeff.java

**Agregar método:**
```java
private boolean detectResonance(double omega) {
    for (Root root : homogeneousRoots) {
        if (root.getReal() == 0 && 
            Math.abs(root.getImaginary()) == omega) {
            return true;
        }
    }
    return false;
}
```

**Modificar getParticularSolutionForm():**
- Línea ~230: Agregar check de resonancia
- Retornar forma CON x si hay resonancia

### 2. Verificar SymbolicDifferentiator.java

Asegurar que calcula correctamente:
- `d/dx[x*sin(x)]` = `sin(x) + x*cos(x)`
- `d²/dx²[x*sin(x)]` = `2*cos(x) - x*sin(x)`

### 3. Actualizar Test

```java
@Test
void testResonanceSinusoidalTerm() {
    // Cambiar expectativa:
    // De: Buscar "x*" en la solución
    // A:  Buscar "x*cos" y "x*sin" EN LA FORMA y_p
    
    assertTrue(
        ucSolver.getParticularSolutionForm("sin(x)").contains("x*"),
        "Debe incluir factor x cuando hay resonancia"
    );
}
```

---

## 📈 VALIDACIÓN POST-CORRECCIÓN

```
Ecuación: y'' + y = sin(x)
Raíces: ±i
g(x): sin(x) con ω = 1
Resonancia: Sí (±i = ±1i)

ANTES:
  y_p_form = "A*cos(x) + B*sin(x)"
  Sistema: Singular ❌
  
DESPUÉS:
  y_p_form = "x*(A*cos(x) + B*sin(x))"
  Sistema: Resoluble ✅
  Solución: y_p = -x/2*cos(x)
  y_final = C1*cos(x) + C2*sin(x) - x/2*cos(x) ✅
```

---

## 📝 RESUMEN

El problema de resonancia trigonométrica está en:
1. **UndeterminedCoeff** no detecta resonancia cuando g(x) es trigonométrica
2. No aplica el factor x a la forma propuesta
3. Esto causa que el sistema A sea singular

**Solución:**
- Agregar `detectResonance()` en UndeterminedCoeff
- Modificar `getParticularSolutionForm()` para incluir factor x
- Validar con Test 4

**Impacto en CI:** La lectura de condiciones iniciales es correcta, el problema está en la forma de y_p.

---

**Archivo a modificar:** `UndeterminedCoeff.java`  
**Línea aproximada:** 230-280  
**Complejidad:** MEDIA  
**Tiempo estimado:** 30 minutos
