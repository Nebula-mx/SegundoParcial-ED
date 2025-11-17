# 📊 Capacidades de Coeficientes Indeterminados (UC) en el Proyecto

## ✅ Respuesta Corta

**SÍ**, el código tiene capacidad de hacer propuestas de formas para **prácticamente cualquier combinación** de:
- Polinomios
- Exponenciales  
- Funciones trigonométricas (sin, cos)
- Sus productos combinados

---

## 📋 Tipos de Funciones Soportadas

### 1. **CONSTANT** - Constantes puras
```
g(x) = 5                      → Propone: y_p = A
g(x) = -3                     → Propone: y_p = A
```

### 2. **POLYNOMIAL** - Polinomios puros
```
g(x) = x                      → Propone: y_p = Ax + B
g(x) = x^2                    → Propone: y_p = Ax^2 + Bx + C
g(x) = 3x^3 - 2x + 1         → Propone: y_p = Ax^3 + Bx^2 + Cx + D
```

### 3. **EXPONENTIAL** - Exponenciales puras
```
g(x) = e^(2x)                 → Propone: y_p = A*e^(2x)
g(x) = e^(-x)                 → Propone: y_p = A*e^(-x)
g(x) = 5*e^(3x)               → Propone: y_p = A*e^(3x)
```

### 4. **SINUSOIDAL** - Funciones trigonométricas puras
```
g(x) = sin(2x)                → Propone: y_p = A*sin(2x) + B*cos(2x)
g(x) = cos(x)                 → Propone: y_p = A*cos(x) + B*sin(x)
g(x) = sin(3x) + cos(3x)      → Propone: y_p = A*sin(3x) + B*cos(3x)
                                (se FUSIONAN automáticamente)
```

### 5. **PRODUCT** - Productos de funciones básicas

#### 5a. Polinomio × Exponencial
```
g(x) = x*e^(2x)               → Propone: y_p = (Ax + B)*e^(2x)
g(x) = x^2*e^(-x)             → Propone: y_p = (Ax^2 + Bx + C)*e^(-x)
```

#### 5b. Exponencial × Trigonométrica
```
g(x) = e^(x)*sin(2x)          → Propone: y_p = e^(x)*(A*sin(2x) + B*cos(2x))
g(x) = e^(-x)*cos(x)          → Propone: y_p = e^(-x)*(A*cos(x) + B*sin(x))
```

#### 5c. Polinomio × Exponencial × Trigonométrica
```
g(x) = x*e^(2x)*sin(x)        → Propone: y_p = (Ax + B)*e^(2x)*(C*sin(x) + D*cos(x))
g(x) = e^(x)*sin(3x)          → Propone: y_p = e^(x)*(A*sin(3x) + B*cos(3x))
```

### 6. **SUM** - Sumas de funciones (Superposición)
```
g(x) = 3 + x*e^(2x)           → Propone: y_p = A + (Bx + C)*e^(2x)
g(x) = sin(x) + e^(2x)        → Propone: y_p = A*sin(x) + B*cos(x) + C*e^(2x)
g(x) = x^2 + 3*e^(x)*sin(2x)  → Propone: y_p = Ax^2 + Bx + C + 
                                           e^(x)*(D*sin(2x) + E*cos(2x))
```

### 7. **UNKNOWN** - No soportadas (Usa Variación de Parámetros)
```
g(x) = tan(x)                 ❌ No soportada
g(x) = ln(x)                  ❌ No soportada
g(x) = 1/x                    ❌ No soportada
g(x) = arcsin(x)              ❌ No soportada
g(x) = 1/(1+x^2)              ❌ No soportada
```

---

## 🔧 Cómo Funciona el Algoritmo

### Paso 1: **Análisis de Términos** (`FunctionAnalyzer`)
Se divide `g(x)` por signos (+/-) y analiza cada término:
- Detecta si contiene `e^(ax)`
- Detecta si contiene `sin(bx)` o `cos(bx)`
- Detecta el grado del polinomio
- Clasifica como CONSTANT, POLYNOMIAL, EXPONENTIAL, SINUSOIDAL, PRODUCT, o UNKNOWN

### Paso 2: **Fusión de Términos Trigonométricos**
Si hay `sin(bx)` y `cos(bx)` con **el mismo** `b`:
```
sin(2x) + cos(2x)  →  Fusiona a UN solo término con forma:
                      y_p = A*sin(2x) + B*cos(2x)
```

### Paso 3: **Generación de Forma Propuesta**
Usa `UndeterminedCoeff.getFormForSingleTerm()` para generar la propuesta específica:

```
CONSTANT       → A
POLYNOMIAL     → A*x^n + B*x^(n-1) + ... + Z
EXPONENTIAL    → A*e^(ax)
SINUSOIDAL     → A*sin(bx) + B*cos(bx)
PRODUCT        → (Polinomio) * (Exponencial) * (Trigonométrica)
SUM            → Suma de las formas anteriores
```

### Paso 4: **Resolución de Coeficientes**
Se sustituye la forma propuesta en la ecuación original y se resuelve el sistema para A, B, C, etc.

---

## 📐 Ejemplos de Combinaciones Complejas

### Ejemplo 1: Resonancia Simple
```
y'' - 4y = e^(2x)

Raíces homogéneas: r = ±2
→ e^(2x) está EN las raíces (resonancia)

Propuesta sin resonancia: y_p = A*e^(2x)
Pero A es INCOMPATIBLE... 

Entonces UC DETECTA resonancia y propone:
→ y_p = A*x*e^(2x)  (multiplica por x)
```

### Ejemplo 2: Resonancia Trigonométrica
```
y'' + 4y = sin(2x)

Raíces homogéneas: r = ±2i
→ sin(2x) está EN las raíces (resonancia trigonométrica)

Propuesta sin resonancia: y_p = A*sin(2x) + B*cos(2x)
Pero esto NO puede resolver la EDO...

Entonces UC DETECTA y propone:
→ y_p = x*(A*sin(2x) + B*cos(2x))  (multiplica por x)
```

### Ejemplo 3: Combinación Triple
```
y'' - 3y' + 2y = x^2*e^(x)*sin(2x)

Analiza: 
- Polinomio grado 2
- Exponencial: e^(1*x)
- Trigonométrica: sin(2x)

Propone:
→ y_p = (Ax^2 + Bx + C)*e^(x)*(D*sin(2x) + E*cos(2x))

Resuelve 5 ecuaciones para los 5 coeficientes A, B, C, D, E
```

---

## 🎯 Flujo de Decisión

```
¿Es la función soportada?
  │
  ├─ SÍ (CONSTANT, POLY, EXP, SIN, PRODUCT, SUM)
  │  ├─ ¿Hay resonancia?
  │  │  ├─ SÍ → Multiplica por x (o x²/x³ según sea necesario)
  │  │  └─ NO → Usa forma directa
  │  └─ Resuelve el sistema de coeficientes
  │
  └─ NO (UNKNOWN: tan, ln, arcsin, etc.)
     └─ Fallback a Variación de Parámetros (VP)
```

---

## ✨ Capacidades Adicionales

### ✅ **Detección de Resonancia**
- Detecta si `e^(ax)` coincide con raíces homogéneas
- Detecta si `sin(bx), cos(bx)` coinciden con raíces homogéneas
- Ajusta la propuesta multiplicando por `x`, `x²`, etc. según sea necesario

### ✅ **Principio de Superposición**
- Si `g(x) = g₁(x) + g₂(x)`, propone `y_p = y_p1 + y_p2`
- Cada componente se resuelve por separado

### ✅ **Simplificación Automática**
- Elimina coeficientes cero
- Simplifica expresiones
- Fusiona términos trigonométricos

---

## ⚠️ Limitaciones

1. **No soporta funciones UNKNOWN**:
   - `tan(x)`, `cot(x)` → Usa VP
   - `ln(x)`, `log(x)` → Usa VP
   - `1/x`, `1/(1+x²)` → Usa VP
   - Funciones especiales (Bessel, Legendre, etc.) → Usa VP

2. **Resonancia múltiple** (teórica):
   - Maneja bien resonancia simple (multiplica por x)
   - Resonancia doble se maneja bien (multiplica por x²)
   - Resonancia extrema (raíz triple) se maneja pero es rara

3. **Términos muy complejos**:
   - Producto de 3+ funciones diferentes puede ser lento
   - Pero SÍ soportado teóricamente

---

## 📊 Tabla Resumen

| Tipo | Ejemplo | Propuesta | ¿Soportado? |
|------|---------|-----------|------------|
| Constante | `5` | `A` | ✅ |
| Polinomio | `x²+2x` | `Ax²+Bx+C` | ✅ |
| Exponencial | `e^(2x)` | `A*e^(2x)` | ✅ |
| Seno/Coseno | `sin(3x)` | `A*sin(3x)+B*cos(3x)` | ✅ |
| Poly×Exp | `x*e^(2x)` | `(Ax+B)*e^(2x)` | ✅ |
| Exp×Trig | `e^(x)*sin(2x)` | `e^(x)*(A*sin(2x)+B*cos(2x))` | ✅ |
| Poly×Exp×Trig | `x*e^(2x)*sin(x)` | `(Ax+B)*e^(2x)*(C*sin(x)+D*cos(x))` | ✅ |
| Suma (Superposición) | `x + e^(2x)` | `Ax+B + C*e^(2x)` | ✅ |
| Resonancia | `e^(2x)` cuando r=2 | `A*x*e^(2x)` | ✅ |
| Tangente | `tan(x)` | ❌ | VP |
| Logaritmo | `ln(x)` | ❌ | VP |
| Función racional | `1/(1+x²)` | ❌ | VP |

---

## 🚀 Conclusión

**SÍ**, el código tiene una **capacidad muy completa** para proponer formas de `y_p` automáticamente. Soporta:
- ✅ Todas las combinaciones algebraicas de polinomios, exponenciales y trigonométricas
- ✅ Resonancia con multiplicadores (x, x², etc.)
- ✅ Superposición (múltiples términos)
- ⚠️ Si encuentra algo que NO soporta → Fallback a Variación de Parámetros

El único caso donde **NO** propone es cuando hay funciones "especiales" (logaritmo, tangente, etc.), en cuyo caso automáticamente cambia a VP.
