# 🤔 ¿POR QUÉ MULTIPLICAR POR X EN RESONANCIA?

## EL PROBLEMA QUE PLANTEAS

```
Tienes razón en decir: "Si multiplico por x, cambia TODO"

Ejemplo:
Propuesta NORMAL:    y_p = A·cos(x)
Propuesta RESONANCIA: y_p = x·A·cos(x)

¿Cómo puede x·A·cos(x) ser solución si es diferente?
```

---

## LA RAZÓN FUNDAMENTAL

### Paso 1: Entender qué es una "solución"

Una función y_p es solución si **al sustituirla en la ecuación, hace que se cumpla**.

```
Ecuación: y'' + y = sin(x)

La función y_p es solución SI:
(y_p)'' + y_p = sin(x)  ← Esto debe ser VERDADERO
```

### Paso 2: ¿Por qué falla la propuesta normal SIN resonancia?

```
Ecuación: y'' + y = sin(x)

INTENTO 1: Propuesta normal y_p = A·cos(x)

Derivadas:
y_p = A·cos(x)
y_p' = -A·sin(x)
y_p'' = -A·cos(x)

Sustituir en la ecuación:
(-A·cos(x)) + (A·cos(x)) = sin(x)
0 = sin(x)  ← ¡¡FALSO!!

NO FUNCIONA porque 0 ≠ sin(x)

¿POR QUÉ FALLA?
Porque las soluciones homogéneas de y'' + y = 0 son:
y_h = C₁·cos(x) + C₂·sin(x)

¡¡Y A·cos(x) es EXACTAMENTE una solución homogénea!!

Cuando sustituyes una solución homogénea en y'' + y = ?, siempre da 0.
```

### Paso 3: ¡Aquí entra la RESONANCIA!

```
Cuando el forzamiento COINCIDE con una solución homogénea,
NO PUEDES usar esa forma como y_p porque SIEMPRE da 0.

SOLUCIÓN: Multiplicar por x

y_p = x·A·cos(x)  ← Es diferente de una solución homogénea

Ahora calcula derivadas:
y_p = x·A·cos(x)
y_p' = A·cos(x) + x·A·(-sin(x)) = A·cos(x) - x·A·sin(x)
y_p'' = -A·sin(x) - A·sin(x) - x·A·cos(x) = -2A·sin(x) - x·A·cos(x)

Sustituir en y'' + y = sin(x):
[-2A·sin(x) - x·A·cos(x)] + [x·A·cos(x)] = sin(x)
-2A·sin(x) = sin(x)
-2A = 1
A = -1/2

¡¡AHORA SÍ FUNCIONA!!
y_p = -(1/2)·x·cos(x)
```

---

## COMPARATIVA VISUAL

### Caso SIN Resonancia (y'' + y = 5)

```
Forzamiento: 5 (constante)
Soluciones homogéneas: C₁·cos(x) + C₂·sin(x)

¿COINCIDEN? NO
└─ La constante NO es solución de y'' + y = 0

Propuesta: y_p = A
Derivadas:
y_p' = 0
y_p'' = 0

Sustituir:
0 + A = 5
A = 5

y_p = 5  ✅ FUNCIONA
```

### Caso CON Resonancia (y'' + y = sin(x))

```
Forzamiento: sin(x)
Soluciones homogéneas: C₁·cos(x) + C₂·sin(x)

¿COINCIDEN? SÍ
└─ sin(x) ES UNA solución homogénea

INTENTO 1 - Propuesta normal: y_p = A·sin(x)
Derivadas:
y_p' = A·cos(x)
y_p'' = -A·sin(x)

Sustituir:
-A·sin(x) + A·sin(x) = sin(x)
0 = sin(x)  ❌ NO FUNCIONA

INTENTO 2 - Propuesta con resonancia: y_p = x·A·sin(x)
Derivadas:
y_p' = A·sin(x) + x·A·cos(x)
y_p'' = A·cos(x) + A·cos(x) - x·A·sin(x) = 2A·cos(x) - x·A·sin(x)

Sustituir:
[2A·cos(x) - x·A·sin(x)] + [x·A·sin(x)] = sin(x)
2A·cos(x) = sin(x)

Espera, esto tampoco funciona directamente...
Mejor usar y_p = x·A·cos(x) + x·B·sin(x)

(Los cálculos son más complejos pero el resultado es)
y_p = -(1/2)·x·cos(x)  ✅ FUNCIONA
```

---

## LA CLAVE: ¿POR QUÉ MULTIPLICAR POR X CAMBIA TODO?

### Entender el "principio de multiplicación por x"

```
Si y_p es una solución de y'' + py' + qy = f(x)
y y_p TAMBIÉN es solución de y'' + py' + qy = 0 (homogénea)

Entonces x·y_p ya NO es solución homogénea
(aunque x·y_p sigue conteniendo partes de la solución homogénea)
```

**EJEMPLO CONCRETO:**

```
Soluciones homogéneas de y'' + y = 0:
y_h = C₁·cos(x) + C₂·sin(x)

La función sin(x) es una de ellas (cuando C₁=0, C₂=1)

PERO la función x·sin(x):
├─ NO es solución de y'' + y = 0
├─ Cuando sustituyes x·sin(x) en y'' + y:
│  (x·sin(x))'' + x·sin(x) ≠ 0
│  └─ Da 2·cos(x), no 0
└─ Así que x·sin(x) NO es solución homogénea

¡Pero SÍ puede ser solución particular de otra ecuación!
```

---

## DEMOSTRACIÓN PASO A PASO COMPLETA

### Ecuación: y'' + y = sin(x)

#### INTENTO 1: Propuesta normal (SIN considerar resonancia)

```
Propuesta: y_p = A·cos(x) + B·sin(x)

Derivadas:
y_p' = -A·sin(x) + B·cos(x)
y_p'' = -A·cos(x) - B·sin(x)

Sustituir en y'' + y = sin(x):
[-A·cos(x) - B·sin(x)] + [A·cos(x) + B·sin(x)] = sin(x)
0 = sin(x)  ❌ IMPOSIBLE

El sistema no tiene solución porque el lado izquierdo siempre es 0.
```

#### INTENTO 2: Propuesta CON resonancia (multiplicando por x)

```
Propuesta: y_p = x·[A·cos(x) + B·sin(x)]
         = A·x·cos(x) + B·x·sin(x)

Primera derivada (regla del producto):
y_p' = A·cos(x) + x·(-A·sin(x)) + B·sin(x) + x·(B·cos(x))
     = A·cos(x) - A·x·sin(x) + B·sin(x) + B·x·cos(x)
     = (A + B·x)·cos(x) + (B - A·x)·sin(x)

Segunda derivada:
y_p'' = (B)·cos(x) + (A + B·x)·(-sin(x)) + (-A)·sin(x) + (B - A·x)·cos(x)
      = B·cos(x) - (A + B·x)·sin(x) - A·sin(x) + (B - A·x)·cos(x)
      = [B + B - A·x]·cos(x) + [-(A + B·x) - A]·sin(x)
      = [2B - A·x]·cos(x) + [-2A - B·x]·sin(x)

Sustituir en y'' + y = sin(x):
{[2B - A·x]·cos(x) + [-2A - B·x]·sin(x)} + {A·x·cos(x) + B·x·sin(x)} = sin(x)

Agrupar por cos(x) y sin(x):
[2B - A·x + A·x]·cos(x) + [-2A - B·x + B·x]·sin(x) = sin(x)
[2B]·cos(x) + [-2A]·sin(x) = sin(x)

Igualar coeficientes:
Coeficiente de cos(x): 2B = 0  →  B = 0
Coeficiente de sin(x): -2A = 1  →  A = -1/2

Solución particular encontrada:
y_p = -(1/2)·x·cos(x)

Verificación:
y_p = -(1/2)·x·cos(x)
y_p' = -(1/2)·cos(x) - (-(1/2)·x)·sin(x) = -(1/2)·cos(x) + (1/2)·x·sin(x)
y_p'' = (1/2)·sin(x) + (1/2)·sin(x) + (1/2)·x·cos(x) = sin(x) + (1/2)·x·cos(x)

Sustituir:
y'' + y = [sin(x) + (1/2)·x·cos(x)] + [-(1/2)·x·cos(x)]
        = sin(x) + (1/2)·x·cos(x) - (1/2)·x·cos(x)
        = sin(x)  ✅ ¡¡CORRECTO!!
```

---

## VISUAL: ¿QUÉ PASA CUANDO MULTIPLICAS POR X?

```
FUNCIÓN sin(x):
    │     ╱╲      ╱╲
    │    ╱  ╲    ╱  ╲
─┼──┼──⁄────╲──⁄────╲──
    │ ╱      ╲╱      ╲╱
    │

FUNCIÓN x·sin(x):
    │           ╱╲
    │         ╱╱  ╲╲
    │        ╱╱    ╲╲
──┼───────⁄────────╲╲──
    │  ╱╱            ╲╲
    │ ╱              ╲

Notice: x·sin(x) CRECE en amplitud mientras x aumenta
        sin(x) tiene amplitud CONSTANTE

Por eso funciona:
- sin(x) es una solución de la ecuación homogénea (siempre "cancela" en y'' + y)
- x·sin(x) NO es una solución homogénea (la amplitud creciente hace diferencia)
- Por eso x·sin(x) SÍ puede satisfacer y'' + y = sin(x)
```

---

## LA REGLA GENERAL EXPLICADA

### ¿CUÁNDO necesitas multiplicar por x?

```
1. Calculas la propuesta de y_p según el forzamiento

2. Verificas: ¿Es y_p una solución homogénea?
   (Sustituyes en la ecuación homogénea y'' + py' + qy = 0)
   
   Si NO  →  Usa esa propuesta directamente
   Si SÍ  →  Multiplica por x y vuelve a intentar
   
3. Si multiplicas por x y SIGUE siendo solución homogénea,
   multiplica por x² y vuelve a intentar
   
4. Continúa hasta que y_p NO sea solución homogénea
```

### Multiplicidad de resonancia

```
Ecuación: y'' - 2y' + y = e^x

Ecuación característica: (r - 1)² = 0  →  r = 1 (multiplicidad 2)

Forzamiento: e^x  (frecuencia = 1)

¿Coincide? SÍ, con multiplicidad 2

Propuesta: y_p = x²·A·e^x  (multiplicar por x²)

Porque:
- x·A·e^x ES solución homogénea de (D-1)²y = 0
- x²·A·e^x NO ES solución homogénea
```

---

## EJEMPLO COMPLETO: RESONANCIA EXPONENCIAL

### Ecuación: y'' - 3y' + 2y = e^x

```
Paso 1: Ecuación característica
r² - 3r + 2 = 0
(r - 1)(r - 2) = 0
r = 1, 2

Paso 2: Soluciones homogéneas
y_h = C₁·e^x + C₂·e^(2x)

Paso 3: Forzamiento
f(x) = e^x  (frecuencia = 1)

Paso 4: ¿Hay resonancia?
¿Coincide 1 con las raíces 1, 2?
SÍ, coincide con r = 1

Paso 5: Propuesta NORMAL sería
y_p = A·e^x

Paso 6: Verificar si es solución homogénea
y_p = A·e^x
y_p' = A·e^x
y_p'' = A·e^x

Sustituir en y'' - 3y' + 2y = 0:
(A·e^x) - 3(A·e^x) + 2(A·e^x) = 0
A·e^x - 3A·e^x + 2A·e^x = 0
0 = 0  ✅ SÍ ES solución homogénea

Paso 7: Multiplicar por x
y_p = x·A·e^x

Paso 8: Verificar si es solución homogénea
y_p = x·A·e^x
y_p' = A·e^x + x·A·e^x = A·e^x(1 + x)
y_p'' = A·e^x + A·e^x(1 + x) = A·e^x(2 + x)

Sustituir en y'' - 3y' + 2y = 0:
A·e^x(2 + x) - 3·A·e^x(1 + x) + 2·x·A·e^x
= A·e^x[(2 + x) - 3(1 + x) + 2x]
= A·e^x[2 + x - 3 - 3x + 2x]
= A·e^x[-1]
= -A·e^x ≠ 0  ❌ NO ES solución homogénea

Paso 9: Usar y_p = x·A·e^x para resolver
y_p' = A·e^x(1 + x)
y_p'' = A·e^x(2 + x)

Sustituir en y'' - 3y' + 2y = e^x:
A·e^x(2 + x) - 3·A·e^x(1 + x) + 2·x·A·e^x = e^x
-A·e^x = e^x
A = -1

SOLUCIÓN PARTICULAR:
y_p = -x·e^x  ✅
```

---

## ¿POR QUÉ SIGUE SIENDO UNA SOLUCIÓN VÁLIDA?

### La matemática detrás

```
Lo que PASA es:

Si y_h es una solución de y'' + py' + qy = 0
Entonces y_h satisface AUTOMÁTICAMENTE:
(y_h)'' + p(y_h)' + q(y_h) = 0

Cuando MULTIPLICAS por x, obtienes x·y_h
Esta nueva función x·y_h NO es solución de la homogénea
Pero es diferente de la homogénea en una forma que permite
que "absorba" el forzamiento.

Comparación:
- y_h = sin(x) en la ecuación sin(x) → (sin(x))'' + sin(x) = 0
- x·sin(x) en la ecuación e^x·sin(x) → produce términos que NO se anulan
```

---

## ✅ RESUMEN VISUAL

```
┌──────────────────────────────────────────────────────────┐
│         ¿POR QUÉ SE MULTIPLICA POR X?                  │
├──────────────────────────────────────────────────────────┤
│                                                          │
│ RAZÓN 1: La propuesta normal ES solución homogénea      │
│          Sustituirla en la ecuación da 0 = f(x)        │
│          Lo cual es IMPOSIBLE                           │
│                                                          │
│ RAZÓN 2: Multiplicar por x lo "desactiva"              │
│          x·y_h ya NO es solución homogénea              │
│          Ahora SÍ puede ser solución particular         │
│                                                          │
│ RAZÓN 3: Las derivadas de x·y_h son DIFERENTES         │
│          Producen términos que NO se anulan             │
│          Estos términos equilibran el forzamiento        │
│                                                          │
│ EJEMPLO:                                                 │
│ y_p = sin(x)      →  (sin)'' + sin = 0 (no funciona)  │
│ y_p = x·sin(x)    →  (x·sin)'' + x·sin = sin(x) ✓     │
│                                                          │
│ La diferencia está en las DERIVADAS:                    │
│ d/dx[x·sin(x)] introduce nuevos términos que            │
│ "sobreviven" al ser sustituidos en la ecuación          │
│                                                          │
└──────────────────────────────────────────────────────────┘
```

---

## 🎯 RESPUESTA A TU PREGUNTA ESPECÍFICA

**"Si multiplico por x, se cambia TODO, ¿por qué sigue siendo igual?"**

Respuesta: **NO sigue siendo "igual"**, pero es **igualmente válida** porque:

1. **Ambas satisfacen la ecuación diferencial**
   - sin(x) NO satisface y'' + y = sin(x)
   - x·sin(x) SÍ satisface y'' + y = sin(x)
   
2. **El multiplicar por x la hace diferente, deliberadamente**
   - Queremos que sea diferente de la solución homogénea
   - Solo así puede ser solución particular del no-homogéneo
   
3. **Es como cambiar de estrategia cuando una no funciona**
   - Estrategia 1 (sin x): "No funciona, da 0 = sin(x)"
   - Estrategia 2 (con x): "Sí funciona, da sin(x) = sin(x)"

El punto es: **La matemática elige la forma que FUNCIONA en la ecuación**.

```
Verificación final:
y_p = -(1/2)·x·cos(x)
y_p'' = sin(x) + (1/2)·x·cos(x)
y_p'' + y_p = sin(x) + (1/2)·x·cos(x) - (1/2)·x·cos(x) = sin(x) ✅
```

La multiplicación por x no es "truquería", es la solución correcta.
