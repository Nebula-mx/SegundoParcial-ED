# ✅ ¿QUÉ RETORNA EL JSON? - RESPUESTA DIRECTA

## 🎯 Respuesta Rápida

El endpoint **retorna un JSON con la solución resuelta paso a paso**, como Photomath.

---

## 📮 EJEMPLO REAL

### 1️⃣ TÚ ENVÍAS:

```bash
curl -X POST http://localhost:8080/api/photomath/solve \
  -H "Content-Type: application/json" \
  -d '{
    "equation": "y'' - 5*y' + 6*y = 0",
    "variable": "x",
    "method": "UC"
  }'
```

### 2️⃣ EL BACKEND RETORNA:

```json
{
  "status": "success",
  "statusCode": 200,
  "message": "Ecuación resuelta exitosamente",
  "equation": "y'' - 5*y' + 6*y = 0",
  "method": "HOMOGENEOUS",
  "steps": [
    {
      "type": "CLASSIFY",
      "order": 1,
      "title": "Clasificación de la Ecuación",
      "explanation": "La ecuación es homogénea de orden 2 con coeficientes constantes",
      "expressions": [
        "y'' - 5y' + 6y = 0",
        "Orden: 2",
        "Coeficientes: 1, -5, 6 (constantes)"
      ]
    },
    {
      "type": "CHARACTERISTIC_EQUATION",
      "order": 2,
      "title": "Ecuación Característica",
      "explanation": "Sustituimos y = e^(rx) para obtener la ecuación característica",
      "expressions": [
        "r² - 5r + 6 = 0",
        "(r - 2)(r - 3) = 0"
      ]
    },
    {
      "type": "FIND_ROOTS",
      "order": 3,
      "title": "Encontrar Raíces",
      "explanation": "Las raíces son los valores de r que satisfacen la ecuación característica",
      "expressions": [
        "r₁ = 2",
        "r₂ = 3"
      ]
    },
    {
      "type": "GENERAL_SOLUTION",
      "order": 4,
      "title": "Solución General",
      "explanation": "Para raíces reales distintas, la solución es una combinación lineal de exponenciales",
      "expressions": [
        "y(x) = C₁·e^(2x) + C₂·e^(3x)"
      ]
    }
  ],
  "finalSolution": "y(x) = C₁·e^(2x) + C₂·e^(3x)",
  "solutionLatex": "y(x) = C_1 e^{2x} + C_2 e^{3x}",
  "metadata": {
    "processingTimeMs": "45",
    "orderOfEquation": "2",
    "numberRoots": "2",
    "characteristicEquation": "r² - 5r + 6 = 0"
  }
}
```

---

## 📋 ESTRUCTURA DEL JSON RETORNADO

| Campo | Tipo | Descripción |
|-------|------|-------------|
| `status` | string | `"success"` o `"error"` |
| `statusCode` | number | 200 (OK) o 400/500 (error) |
| `message` | string | Mensaje descriptivo |
| `equation` | string | La ecuación que resolviste |
| `method` | string | `"HOMOGENEOUS"`, `"UC"`, o `"VP"` |
| `steps` | array | **Array de pasos de resolución** ← ESTO ES LO IMPORTANTE |
| `finalSolution` | string | La solución final en texto plano |
| `solutionLatex` | string | La solución en LaTeX (para renderizar con MathJax) |
| `metadata` | object | Información adicional (tiempo, orden, etc.) |

---

## 🔍 EL ARRAY `steps[]` EXPLICADO

Cada objeto en `steps[]` tiene:

```json
{
  "type": "CLASSIFY",                           // Tipo de paso
  "order": 1,                                   // Orden del paso
  "title": "Clasificación de la Ecuación",      // Título legible
  "explanation": "La ecuación es...",           // Explicación de qué hace
  "expressions": ["expresión 1", "expresión 2"] // Las matemáticas
}
```

### Tipos de pasos posibles:

- `CLASSIFY` - Clasifica la ecuación (homogénea/no-homogénea, etc.)
- `CHARACTERISTIC_EQUATION` - Forma la ecuación característica
- `FIND_ROOTS` - Encuentra las raíces
- `RESONANCE_CHECK` - Verifica si hay resonancia
- `PARTICULAR_SOLUTION` - Encuentra la solución particular
- `GENERAL_SOLUTION` - Combina homogénea + particular
- `APPLY_INITIAL_CONDITIONS` - Aplica condiciones iniciales

---

## 💻 CÓMO USARLO EN JAVASCRIPT

```javascript
// Enviar solicitud
fetch('http://localhost:8080/api/photomath/solve', {
  method: 'POST',
  headers: { 'Content-Type': 'application/json' },
  body: JSON.stringify({
    equation: "y'' - 5*y' + 6*y = 0",
    variable: 'x',
    method: 'AUTO'
  })
})
.then(response => response.json())
.then(data => {
  // ✅ Acceder a los campos
  console.log(data.status);           // "success"
  console.log(data.equation);         // "y'' - 5*y' + 6*y = 0"
  console.log(data.finalSolution);    // "y(x) = C₁·e^(2x) + C₂·e^(3x)"
  console.log(data.solutionLatex);    // Para MathJax
  
  // ✅ Iterar sobre los pasos
  data.steps.forEach((step, index) => {
    console.log(`Paso ${step.order}: ${step.title}`);
    console.log(step.explanation);
    step.expressions.forEach(expr => console.log(`  • ${expr}`));
  });
  
  // ✅ Renderizar con MathJax
  document.getElementById('solution').innerHTML = 
    `$$${data.solutionLatex}$$`;
  MathJax.typesetPromise();
})
.catch(error => console.error('Error:', error));
```

---

## 🌐 ENDPOINTS DISPONIBLES

### Resolver ecuación (PRINCIPAL)
```
POST /api/photomath/solve
```
Retorna: Array de pasos + solución final

### Ver ejemplos
```
GET /api/photomath/examples
```
Retorna: Lista de ecuaciones de ejemplo para probar

### Health check
```
GET /api/photomath/health
```
Retorna:
```json
{
  "status": "UP",
  "service": "Photomath-style ODE Solver",
  "version": "1.0.0"
}
```

### Ver documentación
```
GET /api/ode/docs
```
Retorna: Documentación completa de la API

---

## ❌ EJEMPLO DE ERROR

Si algo falla, el JSON será:

```json
{
  "status": "error",
  "statusCode": 400,
  "message": "Ecuación vacía o inválida",
  "error": "ExpressionException: Invalid syntax"
}
```

---

## 🚀 PARA TU AMIGO: QUICK START

**Si tu amigo solo quiere usar el endpoint:**

```javascript
// 1. Copiar y pegar esto en su console del navegador
fetch('http://localhost:8080/api/photomath/solve', {
  method: 'POST',
  headers: { 'Content-Type': 'application/json' },
  body: JSON.stringify({
    equation: "y'' - 3*y' + 2*y = e^x",
    variable: 'x'
  })
})
.then(r => r.json())
.then(d => console.log(JSON.stringify(d, null, 2)))

// 2. Ver el JSON en la consola ✅

// 3. Para mostrar la solución:
//    • Usa data.finalSolution para texto
//    • Usa data.solutionLatex + MathJax para ecuaciones bonitas
//    • Usa data.steps para mostrar pasos paso a paso como Photomath
```

---

## 📊 CASOS DE USO

### 1. Solo la solución final
```javascript
const data = await response.json();
alert(`Solución: ${data.finalSolution}`);
```

### 2. Mostrar pasos como Photomath
```javascript
const data = await response.json();
data.steps.forEach(step => {
  // Crear card con step.title, step.explanation, step.expressions
  console.log(`${step.order}. ${step.title}`);
});
```

### 3. Renderizar con LaTeX
```javascript
const data = await response.json();
// Mostrar data.solutionLatex con MathJax
document.getElementById('equation').textContent = data.solutionLatex;
MathJax.typesetPromise();
```

---

## 💡 RESUMEN

✅ **El backend retorna:**
- Estado de la solicitud (`status`)
- Código de estado HTTP (`statusCode`)
- **Array de pasos** (for loop y renderizar)
- **Solución final** (mostrar al usuario)
- **LaTeX** (para ecuaciones bonitas)
- **Metadata** (info adicional)

✅ **Datos que tu amigo puede usar:**
```javascript
data.finalSolution      // ← Texto de la solución
data.solutionLatex      // ← Ecuación formateada
data.steps[i].title     // ← Título de cada paso
data.steps[i].expressions // ← Expresiones matemáticas
data.method             // ← Método usado (HOMOGENEOUS/UC/VP)
```

---

*Listo para que tu amigo integre esto en su frontend* 🚀
