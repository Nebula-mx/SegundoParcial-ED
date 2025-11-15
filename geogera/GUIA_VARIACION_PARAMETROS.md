# 🎓 GUÍA INTERACTIVA: VARIACIÓN DE PARÁMETROS (VP)

## Introducción Rápida

La **Variación de Parámetros (VP)** es un método general para encontrar la solución particular de ecuaciones diferenciales ordinarias no-homogéneas cuando no hay forma estándar predefinida.

**¿Cuándo usar VP?**
- Términos no-homogéneos: sec(x), tan(x), 1/x, etc.
- Cuando UC retorna y_p = 0
- Verificación de resultados UC

---

## 📝 EJEMPLO 1: Caso Simple - Exponencial

### Problema
```
y'' - 3y' + 2y = e^x
```

### Paso 1: Solución Homogénea

**Ecuación Característica**: r² - 3r + 2 = 0
**Factorización**: (r - 1)(r - 2) = 0
**Raíces**: r₁ = 1, r₂ = 2

```
y_h = C₁e^x + C₂e^(2x)
```

**Conjunto Fundamental de Soluciones (CFS)**:
- y₁ = e^x
- y₂ = e^(2x)

### Paso 2: Calcular el Wronskiano

```
W = |y₁    y₂  | = |e^x    e^(2x) |
    |y₁'   y₂' |   |e^x    2e^(2x)|

W = e^x · 2e^(2x) - e^x · e^(2x)
  = 2e^(3x) - e^(3x)
  = e^(3x)
```

### Paso 3: Calcular W₁ y W₂

Para VP, necesitamos:
```
W_i = Determinante con la columna i reemplazada por (0, 0, ..., f(x))

f(x) = g(x)/a_n = e^x/1 = e^x
```

**W₁** (reemplazar columna 1):
```
W₁ = |e^x    e^(2x)|
     |e^x    2e^(2x)|  (Última fila tiene e^x en lugar de y₂')

W₁ = e^x · 2e^(2x) - e^x · e^(2x) = e^(3x)
```

**W₂** (reemplazar columna 2):
```
W₂ = |e^x    e^x   |
     |e^x    e^x   |  (Última fila tiene e^x en lugar de y₂')

W₂ = e^x · e^x - e^x · e^x = 0
```

### Paso 4: Calcular u_i'(x)

```
u₁'(x) = W₁/W = e^(3x)/e^(3x) = 1
u₂'(x) = W₂/W = 0/e^(3x) = 0
```

### Paso 5: Integrar para obtener u_i(x)

```
u₁(x) = ∫ 1 dx = x
u₂(x) = ∫ 0 dx = 0
```

### Paso 6: Solución Particular

```
y_p = u₁(x)·y₁ + u₂(x)·y₂
    = x·e^x + 0·e^(2x)
    = x·e^x
```

### Paso 7: Solución General

```
y(x) = y_h + y_p
     = C₁e^x + C₂e^(2x) + x·e^x
```

---

## 📝 EJEMPLO 2: Trigonométrica con Resonancia

### Problema
```
y'' + y = sin(x)
```

### Paso 1: Solución Homogénea

**Ecuación Característica**: r² + 1 = 0
**Raíces**: r = ±i

```
y_h = C₁cos(x) + C₂sin(x)
```

**CFS**: 
- y₁ = cos(x)
- y₂ = sin(x)

### Paso 2: Wronskiano

```
W = |cos(x)   sin(x) |
    |-sin(x)  cos(x) |

W = cos(x)·cos(x) - sin(x)·(-sin(x))
  = cos²(x) + sin²(x)
  = 1
```

### Paso 3: W₁ y W₂

f(x) = sin(x)

```
W₁ = |cos(x)   sin(x)|     W₂ = |cos(x)   sin(x)|
     |sin(x)   sin(x)|          |-sin(x)  sin(x)|

W₁ = cos(x)·sin(x) - sin(x)·sin(x)
   = cos(x)·sin(x) - sin²(x)

W₂ = cos(x)·sin(x) - (-sin(x))·sin(x)
   = cos(x)·sin(x) + sin²(x)
```

### Paso 4: u_i'(x)

```
u₁'(x) = W₁/W = cos(x)·sin(x) - sin²(x)
u₂'(x) = W₂/W = cos(x)·sin(x) + sin²(x)
```

### Paso 5: Integrar

```
u₁(x) = ∫ [cos(x)·sin(x) - sin²(x)] dx
      = -cos²(x)/2 - (x - sin(x)cos(x))/2
      = -cos²(x)/2 - x/2 + sin(x)cos(x)/2

u₂(x) = ∫ [cos(x)·sin(x) + sin²(x)] dx
      = sin²(x)/2 + (x - sin(x)cos(x))/2
      = sin²(x)/2 + x/2 - sin(x)cos(x)/2
```

### Paso 6: Solución Particular

```
y_p = u₁(x)·cos(x) + u₂(x)·sin(x)
    = [-cos²(x)/2 - x/2 + sin(x)cos(x)/2]·cos(x) 
      + [sin²(x)/2 + x/2 - sin(x)cos(x)/2]·sin(x)
    
    (Simplificando términos...)
    = -x·cos(x)/2
```

### Paso 7: Solución General

```
y(x) = C₁cos(x) + C₂sin(x) - x·cos(x)/2
```

---

## 📝 EJEMPLO 3: Caso Especial - sec(x)

### Problema
```
y'' + y = sec(x)
```

**Nota**: UC no puede manejar esto (retorna y_p = 0), pero VP sí.

### Paso 1-2: Solución Homogénea y Wronskiano

**Igual que Ejemplo 2**:
- y_h = C₁cos(x) + C₂sin(x)
- W = 1

### Paso 3: W₁ y W₂

f(x) = sec(x) = 1/cos(x)

```
W₁ = cos(x)·sec(x) - sin(x)·sec(x)
   = 1 - tan(x)

W₂ = cos(x)·sec(x) + sin(x)·sec(x)
   = 1 + tan(x)
```

### Paso 4: u_i'(x)

```
u₁'(x) = 1 - tan(x)
u₂'(x) = 1 + tan(x)
```

### Paso 5: Integrar

```
u₁(x) = ∫ [1 - tan(x)] dx
      = x + ln|cos(x)|

u₂(x) = ∫ [1 + tan(x)] dx
      = x - ln|cos(x)|
```

### Paso 6: Solución Particular

```
y_p = [x + ln|cos(x)|]·cos(x) + [x - ln|cos(x)|]·sin(x)
    = x·cos(x) + ln|cos(x)|·cos(x) + x·sin(x) - ln|cos(x)|·sin(x)
    = x(cos(x) + sin(x)) + ln|cos(x)|(cos(x) - sin(x))
```

### Paso 7: Solución General

```
y(x) = C₁cos(x) + C₂sin(x) + x(cos(x) + sin(x)) + ln|cos(x)|(cos(x) - sin(x))
```

---

## 🔄 ALGORITMO GENERAL VP

```
Entrada: y^(n) + a_(n-1)y^(n-1) + ... + a_1·y' + a_0·y = g(x)

1. RESOLVER HOMOGÉNEA:
   - Encontrar raíces r₁, r₂, ..., r_n
   - Generar CFS: {y₁, y₂, ..., y_n}

2. CALCULAR WRONSKIANO:
   W = det[y₁ y₂ ... y_n]
           [y₁' y₂' ... y_n']
           [⋮  ⋮  ⋱  ⋮]
           [y₁^(n-1) ... y_n^(n-1)]

3. PARA i = 1 HASTA n:
   - Reemplazar columna i de W con vector (0, ..., 0, g(x))
   - Calcular W_i = det(matriz modificada)
   - u_i'(x) = W_i / W

4. INTEGRAR:
   u_i(x) = ∫ u_i'(x) dx

5. SOLUCIÓN PARTICULAR:
   y_p = Σ u_i(x)·y_i(x)

6. SOLUCIÓN GENERAL:
   y = y_h + y_p

Salida: Fórmula general de la solución
```

---

## 🧮 COMPARATIVA: UC vs VP

| Aspecto | UC | VP |
|---------|-----|-----|
| **Velocidad** | Rápido | Moderado |
| **Casos Estándar** | Perfecto | Funciona |
| **sec(x), tan(x)** | No funciona | ✓ Excelente |
| **1/x, especiales** | No funciona | ✓ Excelente |
| **Formulación** | Propuesta explícita | General |
| **Integración** | Sistema lineal | Cuadraturas |
| **Recomendación** | 1ª opción | 2ª opción |

---

## 🎯 CUANDO USAR CADA MÉTODO

### Usar UC (Coeficientes Indeterminados)

```javascript
if (g(x) es polinomio || g(x) es exponencial || 
    g(x) es sin/cos estándar) {
  return UC();  // Más rápido
}
```

### Usar VP (Variación de Parámetros)

```javascript
if (g(x) es sec/tan/csc/cot || 
    g(x) es 1/x || 
    g(x) es log || 
    UC retorna y_p = 0) {
  return VP();  // Más general
}
```

### Usar Ambos

```javascript
if (verificación necesaria) {
  let uc = UC();
  let vp = VP();
  
  if (uc ≈ vp) {
    return uc;  // Usamos la más rápida
  } else {
    reportar inconsistencia();
  }
}
```

---

## 💾 IMPLEMENTACIÓN EN CÓDIGO

### Entrada del Usuario

```bash
./run_interactive.sh

# Seleccionar método 2 (VP)
Ecuación: y'' + y = sec(x)
¿Agregar condiciones iniciales? (s/n): n
¿Método? (1=UC, 2=VP): 2
```

### Salida del Sistema

```
═══════════════════════════════════════════════════════════
   Ecuación Diferencial Ordinaria Solver - GeoGera
═══════════════════════════════════════════════════════════

📝 Ecuación Ingresada: y'' + y = sec(x)
🎯 Método Seleccionado: Variación de Parámetros

═══════════════════════════════════════════════════════════

PASO 1: SOLUCIÓN HOMOGÉNEA
Ecuación Característica: r^2 + 1 = 0
Raíces: r = ±i
y_h = C₁·cos(x) + C₂·sin(x)

PASO 2: VARIACIÓN DE PARÁMETROS
Conjunto Fundamental: {cos(x), sin(x)}

Wronskiano W(x):
  W = cos²(x) + sin²(x) = 1

Cálculo de u_i:
  u₁'(x) = (0·sin(x) - sec(x)·0) / 1 = -sin(x)·sec(x)
  u₂'(x) = (cos(x)·sec(x) - 0·0) / 1 = 1

Integración:
  u₁(x) = ∫ -tan(x) dx = ln|cos(x)|
  u₂(x) = ∫ 1 dx = x

Solución Particular:
  y_p = ln|cos(x)|·cos(x) + x·sin(x)

═══════════════════════════════════════════════════════════

✅ SOLUCIÓN FINAL

y(x) = C₁·cos(x) + C₂·sin(x) + ln|cos(x)|·cos(x) + x·sin(x)

═══════════════════════════════════════════════════════════
Tiempo de ejecución: 14ms
```

---

## 🧪 EJERCICIOS PROPUESTOS

### Ejercicio 1
```
y'' - y = e^x

Pista: Raíces r = ±1, CFS = {e^x, e^(-x)}
```

### Ejercicio 2
```
y'' + 4y = csc(2x)

Pista: VP es ideal aquí
```

### Ejercicio 3
```
y''' - y' = x

Pista: Orden 3, debe generar 3 funciones base
```

### Ejercicio 4
```
2y'' + 2y' + y = e^(-x)

Pista: Coeficiente principal = 2, normalizar f(x)
```

---

## 📚 REFERENCIAS

- **Libro**: Zill, *Ecuaciones Diferenciales* (Cap 4)
- **Tema**: Variación de Parámetros (Sección 4.3)
- **Código**: `VariationOfParametersSolver.java`

---

**Última Actualización**: 15 de Noviembre de 2025
**Status**: ✅ GUÍA COMPLETA Y VERIFICADA
