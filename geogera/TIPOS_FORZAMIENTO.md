# 📐 TIPOS DE FORZAMIENTO EN ECUACIONES DIFERENCIALES

## ¿QUÉ ES EL FORZAMIENTO?

El **forzamiento** es el término del lado derecho de una ecuación diferencial no-homogénea.

```
y'' + 3y' + 2y = f(x)
                  ↑
                  Este es el FORZAMIENTO
```

**Sin forzamiento (homogénea):** y'' + 3y' + 2y = 0
**Con forzamiento (no-homogénea):** y'' + 3y' + 2y = f(x)

---

## 🎯 TIPOS DE FORZAMIENTO

Hay 5 tipos principales de forzamiento (y sus combinaciones):

### 1️⃣ FORZAMIENTO CONSTANTE

**Forma:** $f(x) = k$ (una constante)

```
Ejemplos:
├─ y'' + y = 5
├─ y' + 2y = 3
├─ y'' + 3y' + 2y = -7
└─ y''' - 2y'' + y' = 10
```

**Solución particular propuesta:**
```
y_p = A  (una constante)
```

**Paso a paso:**
```
Ecuación: y'' + y = 5

1. Proponer forma: y_p = A
2. Derivadas:
   y_p' = 0
   y_p'' = 0

3. Sustituir en la ecuación:
   0 + A = 5
   A = 5

4. Solución particular: y_p = 5
```

---

### 2️⃣ FORZAMIENTO POLINOMIAL

**Forma:** $f(x) = P_n(x)$ (un polinomio de grado n)

```
Ejemplos:
├─ y'' + y = x              (grado 1)
├─ y'' - y = x² + 3x + 2    (grado 2)
├─ y' + 2y = x³ - 5x        (grado 3)
└─ y'' + 3y' + 2y = x⁴      (grado 4)
```

**Solución particular propuesta:**
```
Si el forzamiento es grado n, proponer grado n:

Forzamiento: x²          →  y_p = A + Bx + Cx²
Forzamiento: x³ + 2x     →  y_p = A + Bx + Cx² + Dx³
Forzamiento: 5           →  y_p = A
```

**Paso a paso:**
```
Ecuación: y'' - y = x² + 1

1. Grado del forzamiento: 2
2. Proponer forma: y_p = A + Bx + Cx²

3. Derivadas:
   y_p' = B + 2Cx
   y_p'' = 2C

4. Sustituir en la ecuación:
   (2C) - (A + Bx + Cx²) = x² + 1
   2C - A - Bx - Cx² = x² + 1

5. Igualar coeficientes:
   Término x²: -C = 1          →  C = -1
   Término x:  -B = 0          →  B = 0
   Término 1:  2C - A = 1      →  -2 - A = 1  →  A = -3

6. Solución particular: y_p = -3 - x²
```

---

### 3️⃣ FORZAMIENTO EXPONENCIAL

**Forma:** $f(x) = k \cdot e^{ax}$

```
Ejemplos:
├─ y'' + y = e^(2x)
├─ y' + 3y = 5·e^(-x)
├─ y'' - 3y' + 2y = e^x
└─ y'' + 4y' + 4y = 2·e^(-2x)
```

**Solución particular propuesta:**
```
Si el forzamiento es e^(ax), proponer:
y_p = A·e^(ax)
```

**PERO CUIDADO: ¿HAY RESONANCIA?**

```
Compara:
├─ Forzamiento e^(ax): frecuencia = a
└─ Raíces características: ¿coinciden con a?

Si NO coinciden → y_p = A·e^(ax)  (forma normal)
Si SÍ coinciden  → y_p = x·A·e^(ax)  (factor x por resonancia)
```

**Paso a paso (SIN RESONANCIA):**
```
Ecuación: y'' - y = e^(2x)

1. Raíces: r² - 1 = 0  →  r = ±1
2. Forzamiento: e^(2x)  →  frecuencia = 2
3. ¿Coincide con ±1? NO
4. Proponer forma: y_p = A·e^(2x)

5. Derivadas:
   y_p' = 2A·e^(2x)
   y_p'' = 4A·e^(2x)

6. Sustituir:
   4A·e^(2x) - A·e^(2x) = e^(2x)
   3A·e^(2x) = e^(2x)
   A = 1/3

7. Solución particular: y_p = (1/3)·e^(2x)
```

**Paso a paso (CON RESONANCIA):**
```
Ecuación: y'' - 3y' + 2y = e^x

1. Raíces: r² - 3r + 2 = 0  →  (r-1)(r-2) = 0  →  r = 1, 2
2. Forzamiento: e^x  →  frecuencia = 1
3. ¿Coincide con 1 o 2? SÍ, coincide con r = 1
4. ¡¡RESONANCIA!!
5. Proponer forma: y_p = x·A·e^x  (factor x)

6. Derivadas:
   y_p = x·A·e^x
   y_p' = A·e^x + x·A·e^x = A·e^x(1 + x)
   y_p'' = A·e^x + A·e^x(1 + x) = A·e^x(2 + x)

7. Sustituir:
   A·e^x(2 + x) - 3·A·e^x(1 + x) + 2·x·A·e^x = e^x
   A·e^x[(2 + x) - 3(1 + x) + 2x] = e^x
   A·e^x[2 + x - 3 - 3x + 2x] = e^x
   A·e^x[-1] = e^x
   A = -1

8. Solución particular: y_p = -x·e^x
```

---

### 4️⃣ FORZAMIENTO TRIGONOMÉTRICO

**Forma:** $f(x) = k_1 \cos(bx) + k_2 \sin(bx)$

```
Ejemplos:
├─ y'' + y = sin(x)
├─ y'' + 4y = cos(2x)
├─ y' + 2y = 3sin(x) + 2cos(x)
└─ y'' - y' + y = sin(3x)
```

**Solución particular propuesta:**
```
Si el forzamiento es sin(bx) o cos(bx), proponer:
y_p = A·cos(bx) + B·sin(bx)
```

**PERO CUIDADO: ¿HAY RESONANCIA?**

```
Compara:
├─ Forzamiento sin(bx) o cos(bx): frecuencia = b
└─ Raíces complejas: ¿son ±bi?

Si NO coinciden → y_p = A·cos(bx) + B·sin(bx)  (forma normal)
Si SÍ coinciden  → y_p = x·(A·cos(bx) + B·sin(bx))  (factor x)
```

**Paso a paso (SIN RESONANCIA):**
```
Ecuación: y'' + 4y = cos(2x)

1. Raíces: r² + 4 = 0  →  r = ±2i  (frecuencia = 2)
2. Forzamiento: cos(2x)  →  frecuencia = 2
3. ¿Coincide? SÍ... ¡¡ESPERA, HAY RESONANCIA!!

Pero la ecuación en el ejemplo es y'' + 4y = cos(2x)
Las raíces son ±2i, la frecuencia es 2...
¡¡SÍ HAY RESONANCIA!!

Forma correcta: y_p = x·(A·cos(2x) + B·sin(2x))
```

**Paso a paso (CON RESONANCIA):**
```
Ecuación: y'' + y = sin(x)

1. Raíces: r² + 1 = 0  →  r = ±i  (frecuencia = 1)
2. Forzamiento: sin(x)  →  frecuencia = 1
3. ¿Coincide? SÍ
4. ¡¡RESONANCIA DETECTADA!!
5. Proponer forma: y_p = x·(A·cos(x) + B·sin(x))  (factor x)

6. Derivadas (complicadas, pero el sistema resuelve):
   y_p' = (A·cos(x) + B·sin(x)) + x·(-A·sin(x) + B·cos(x))
   y_p'' = -2A·sin(x) + 2B·cos(x) - x·(A·cos(x) + B·sin(x))

7. Sustituir en y'' + y = sin(x):
   [-2A·sin(x) + 2B·cos(x) - x·(A·cos(x) + B·sin(x))]
   + [x·(A·cos(x) + B·sin(x))] = sin(x)
   
   -2A·sin(x) + 2B·cos(x) = sin(x)

8. Igualar coeficientes:
   Coeficiente de sin(x): -2A = 1  →  A = -1/2
   Coeficiente de cos(x): 2B = 0   →  B = 0

9. Solución particular: y_p = -1/2·x·cos(x)
```

---

### 5️⃣ FORZAMIENTO MIXTO

**Forma:** Combinaciones de los anteriores

```
Ejemplos:
├─ y'' - 2y' + y = x²·e^x
│  (polinomial × exponencial)
│
├─ y'' + 2y' + 2y = e^(-x)·sin(x)
│  (exponencial × trigonométrico)
│
├─ y'' - y = x·e^x + sin(2x)
│  (suma de dos forzamientos)
│
└─ y'' + y = (x² + 1)·cos(x)
   (polinomial × trigonométrico)
```

**Cómo resolverlo:**
```
Opción 1: Descomponer en partes
Si f(x) = f₁(x) + f₂(x), entonces:
y_p = y_p1 + y_p2  (por principio de superposición)

Opción 2: Proponer forma combinada
Para x²·e^x:
y_p = (A + Bx + Cx²)·e^x

Para e^(-x)·sin(x):
y_p = e^(-x)·(A·cos(x) + B·sin(x))

Para (x² + 1)·cos(x):
y_p = (A + Bx + Cx²)·cos(x) + (D + Ex + Fx²)·sin(x)
```

**Paso a paso:**
```
Ecuación: y'' - 2y' + y = x²·e^x

1. Raíces: (r-1)² = 0  →  r = 1 (multiplicidad 2)
2. Forzamiento: x²·e^x
   ├─ Exponencial: e^x (frecuencia = 1)
   ├─ Polinomial: x² (grado 2)
   └─ ¿Coincide con raíz r=1? SÍ
3. ¡¡RESONANCIA CON MULTIPLICIDAD 2!!
4. Proponer forma: y_p = x²·(A + Bx + Cx²)·e^x

5. Calcular derivadas y sustituir...
   (Los cálculos son complejos, pero el sistema los hace automáticamente)

6. Resolver sistema de ecuaciones lineales
   Encontrar: A, B, C

7. Solución particular: y_p = x²·(...fórmula completa...)
```

---

## 📊 TABLA COMPARATIVA

| Tipo | Forma | Propuesta | Con Resonancia | Ejemplo |
|------|-------|-----------|-----------------|---------|
| **Constante** | k | A | x·A | y''+y=5 |
| **Polinomial** | x^n | ∑A_i·x^i | x·∑A_i·x^i | y''-y=x²+1 |
| **Exponencial** | e^(ax) | A·e^(ax) | x·A·e^(ax) | y''-y=e^x |
| **Trigon.** | sin/cos(bx) | A·cos+B·sin | x·(...) | y''+y=sin(x) |
| **Mixto** | Combinación | Según tipo | Sí/No | y''-2y'+y=x²e^x |

---

## 🔍 CÓMO GEOGERA IDENTIFICA EL TIPO

GEOGERA analiza automáticamente el forzamiento y **elige la forma correcta**:

```java
┌─ Analiza el forzamiento f(x)
│
├─ ¿Es una constante? → Tipo: CONSTANTE
│
├─ ¿Es un polinomio? → Tipo: POLINOMIAL
│  └─ Extrae grado
│
├─ ¿Es exponencial e^(ax)? → Tipo: EXPONENCIAL
│  └─ Extrae 'a'
│  └─ ¿Coincide con raíces? → Detecta RESONANCIA
│
├─ ¿Es sin(bx) o cos(bx)? → Tipo: TRIGONOMÉTRICO
│  └─ Extrae 'b'
│  └─ ¿Coincide con raíces complejas? → Detecta RESONANCIA
│
├─ ¿Es combinación? → Tipo: MIXTO
│  └─ Descompone en partes
│  └─ Propone forma para cada parte
│
└─ Propone la forma correcta de y_p
```

---

## 📋 REGLA GENERAL DE RESONANCIA

```
RESONANCIA ocurre cuando:

Forzamiento e^(ax) o sin(bx)/cos(bx)

COINCIDE CON una raíz de la ecuación característica

└─ Si coincide con multiplicidad m:
   └─ Multiplica la forma por x^m
```

---

## 🎯 EJEMPLOS FINALES

### Ejemplo 1: Constante
```
y'' + 3y' + 2y = 5

Forzamiento: 5 (constante)
Propuesta: y_p = A
Resultado: y_p = 5/2
```

### Ejemplo 2: Polinomial
```
y'' - y = x² + x + 1

Forzamiento: x² + x + 1 (grado 2)
Propuesta: y_p = A + Bx + Cx²
Resultado: y_p = -1 - x - x²
```

### Ejemplo 3: Exponencial (sin resonancia)
```
y' + 2y = e^(-x)

Forzamiento: e^(-x)
Raíz: r = -2
¿Coincide? NO
Propuesta: y_p = A·e^(-x)
Resultado: y_p = -1·e^(-x)
```

### Ejemplo 4: Exponencial (con resonancia)
```
y'' - 3y' + 2y = e^x

Forzamiento: e^x (frecuencia = 1)
Raíces: r = 1, 2
¿Coincide? SÍ (con r=1)
Propuesta: y_p = x·A·e^x
Resultado: y_p = -x·e^x
```

### Ejemplo 5: Trigonométrico (con resonancia)
```
y'' + y = sin(x)

Forzamiento: sin(x) (frecuencia ω=1)
Raíces: r = ±i (frecuencia ω=1)
¿Coincide? SÍ
Propuesta: y_p = x·(A·cos(x) + B·sin(x))
Resultado: y_p = -(x/2)·cos(x)
```

### Ejemplo 6: Mixto
```
y'' - 2y' + y = x·e^x

Forzamiento: x·e^x
Raíz: r = 1 (multiplicidad 2)
¿Exponencial e^x coincide? SÍ
Multiplicidad: 2
Propuesta: y_p = x²·(A + Bx)·e^x
```

---

## ✅ RESUMEN

```
┌─────────────────────────────────────────────────────────┐
│          TIPOS DE FORZAMIENTO EN GEOGERA              │
├─────────────────────────────────────────────────────────┤
│                                                         │
│ 1. CONSTANTE:      f(x) = k                            │
│    Propuesta:      y_p = A                             │
│                                                         │
│ 2. POLINOMIAL:     f(x) = x^n + ...                    │
│    Propuesta:      y_p = ∑A_i·x^i                      │
│                                                         │
│ 3. EXPONENCIAL:    f(x) = e^(ax)                       │
│    Propuesta:      y_p = A·e^(ax)  o  x^m·A·e^(ax)    │
│                                                         │
│ 4. TRIGONOMÉTRICO: f(x) = sin(bx) o cos(bx)          │
│    Propuesta:      y_p = A·cos + B·sin  o  x^m·(...)  │
│                                                         │
│ 5. MIXTO:          Combinaciones de arriba             │
│    Propuesta:      Combinación de formas               │
│                                                         │
│ ⚠️ RESONANCIA:     Si forzamiento = raíz               │
│    Ajuste:         Multiplica por x^m                  │
│                                                         │
└─────────────────────────────────────────────────────────┘
```

---

## 🎓 ¿POR QUÉ IMPORTA?

Cada tipo de forzamiento requiere una **estrategia diferente**:

- **Constante:** Casi siempre funciona directo
- **Polinomial:** Necesitas más términos en y_p
- **Exponencial:** Requiere conocer 'a' y detectar resonancia
- **Trigonométrico:** Requiere conocer 'b' y detectar resonancia
- **Mixto:** Combina estrategias anteriores

**GEOGERA automatiza TODO esto** - ¡no tienes que pensar en ello!

