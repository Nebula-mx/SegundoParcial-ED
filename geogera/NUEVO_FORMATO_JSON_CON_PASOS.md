# 📸 Nuevo Formato JSON: Pasos Detallados (Estilo Photomath)

## ¿Qué Cambió?

Tu amigo quería un formato que mostrara los **pasos paso a paso**, como lo hace Photomath. Ahora tu proyecto soporta exactamente eso.

---

## 📋 Estructura del Nuevo Formato

```json
{
  "status": "SUCCESS",
  "message": "Ecuación resuelta exitosamente",
  "equation": "y' + y = 0",
  "variable": "x",
  "steps": [
    {
      "type": "CLASSIFY",
      "title": "📖 Parsing de la ecuación",
      "order": 1,
      "expressions": ["Entrada: y' + y = 0"],
      "explanation": "Convertir la ecuación textual a estructura interna",
      "details": {}
    },
    {
      "type": "CLASSIFY",
      "title": "🏷️ Clasificación de la EDO",
      "order": 2,
      "expressions": ["EDO de orden 1, Homogénea"],
      "details": {
        "Tipo": "Homogénea",
        "Orden": "1"
      },
      "explanation": "La ecuación es de orden 1 y homogénea"
    },
    {
      "type": "CHARACTERISTIC",
      "title": "📐 Formar la ecuación característica",
      "order": 3,
      "expressions": ["r + 1 = 0"],
      "details": {
        "Método": "Sustitución exponencial"
      },
      "explanation": "Reemplazar y con e^(rx)"
    },
    {
      "type": "ROOTS",
      "title": "🔍 Encontrar las raíces",
      "order": 4,
      "expressions": ["r = -1"],
      "details": {
        "Tipo de raíces": "Reales Distintas"
      },
      "explanation": "Se obtiene una raíz real: r = -1"
    },
    {
      "type": "HOMOGENEOUS_SOLUTION",
      "title": "✨ Construir la solución homogénea",
      "order": 5,
      "expressions": ["y(x) = C₁*e^(-x)"],
      "details": {
        "Número de constantes": "1"
      },
      "explanation": "La solución general es y(x) = C₁*e^(-x)"
    }
  ],
  "finalSolution": "y(x) = C₁*e^(-x)",
  "solutionLatex": "$y(x) = C₁ \\cdot e^{-x}$",
  "metadata": {
    "Orden": "1",
    "Tipo": "Homogénea",
    "Pasos totales": "5",
    "Raíces": "Reales Distintas",
    "Método": "Ecuación característica"
  },
  "executionTimeMs": 4,
  "stepCount": 5,
  "success": true
}
```

---

## 🔑 Campos Principales

| Campo | Tipo | Descripción |
|-------|------|-------------|
| `status` | String | `"SUCCESS"` o `"ERROR"` |
| `message` | String | Mensaje descriptivo |
| `equation` | String | Ecuación procesada |
| `variable` | String | Variable independiente (usualmente "x") |
| `steps` | Array | Array de pasos con detalles |
| `finalSolution` | String | Solución final |
| `solutionLatex` | String | Versión LaTeX |
| `metadata` | Object | Información adicional |
| `executionTimeMs` | Number | Tiempo en milisegundos |
| `stepCount` | Number | Número total de pasos |
| `success` | Boolean | `true` si fue exitoso |

---

## 🎯 Estructura de Cada Paso (Step)

```json
{
  "type": "CLASSIFY|CHARACTERISTIC|ROOTS|HOMOGENEOUS_SOLUTION|PARTICULAR_SOLUTION|FINAL_SOLUTION",
  "title": "Título descriptivo con emoji",
  "order": 1,
  "expressions": ["Expresión matemática"],
  "explanation": "Explicación en texto natural",
  "details": {
    "Clave1": "Valor1",
    "Clave2": "Valor2"
  }
}
```

### Tipos de Pasos (`type`)

- **CLASSIFY** - Clasificación de la ecuación (orden, tipo)
- **CHARACTERISTIC** - Formación de ecuación característica
- **ROOTS** - Cálculo de raíces
- **HOMOGENEOUS_SOLUTION** - Solución homogénea
- **PARTICULAR_SOLUTION** - Solución particular (si no homogénea)
- **FINAL_SOLUTION** - Solución completa final

---

## 💻 Cómo Usarlo

### **Opción 1: Obtener StepResponse (Objeto Java)**

```java
import com.ecuaciones.diferenciales.dto.StepResponse;

StepResponse response = Main.evaluateWithSteps("y' + y = 0");

System.out.println("Status: " + response.getStatus());
System.out.println("Solución: " + response.getFinalSolution());
System.out.println("Pasos: " + response.getStepCount());

for (StepResponse.Step step : response.getSteps()) {
    System.out.println(step.getTitle());
    System.out.println("  → " + step.getExplanation());
}
```

---

### **Opción 2: Obtener JSON String Directamente**

```java
String jsonResponse = Main.evaluateWithStepsAsJson("y'' - 5*y' + 6*y = 0");
System.out.println(jsonResponse);

// Parsear con Jackson
ObjectMapper mapper = new ObjectMapper();
StepResponse resp = mapper.readValue(jsonResponse, StepResponse.class);
```

---

### **Opción 3: Para Tu Frontend (desde JavaScript/Postman)**

Aunque no tienes API, puedes serializar y enviar el JSON:

```javascript
// En tu frontend
const response = {
  status: "SUCCESS",
  equation: "y' + y = 0",
  steps: [...],
  finalSolution: "y(x) = C₁*e^(-x)"
};

// Renderizar cada paso
response.steps.forEach(step => {
  console.log(`${step.title}`);
  console.log(`Explicación: ${step.explanation}`);
  step.expressions.forEach(expr => console.log(`  • ${expr}`));
});
```

---

## 🎓 Ejemplos de Respuestas

### Ejemplo 1: Ecuación Homogénea Simple

**Entrada:** `y' + y = 0`

**Pasos generados:**
1. Parsing de la ecuación
2. Clasificación (Orden 1, Homogénea)
3. Ecuación característica
4. Encontrar raíces
5. Construir solución

**Solución final:** `y(x) = C₁*e^(-x)`

---

### Ejemplo 2: Ecuación de Orden 2 con Raíces Distintas

**Entrada:** `y'' - 5*y' + 6*y = 0`

**Pasos generados:**
1. Parsing
2. Clasificación (Orden 2, Homogénea)
3. Ecuación característica: `r² - 5r + 6 = 0`
4. Raíces: `r₁ = 2, r₂ = 3`
5. Solución: `y(x) = C₁*e^(2x) + C₂*e^(3x)`

---

### Ejemplo 3: Ecuación con Resonancia

**Entrada:** `y'' + 4*y = sin(2*x)`

**Pasos generados:**
1. Parsing
2. Clasificación (Orden 2, No Homogénea con Resonancia)
3. Ecuación característica: `r² + 4 = 0`
4. Raíces: `r = ±2i`
5. Solución homogénea: `y_h = C₁*cos(2x) + C₂*sin(2x)`
6. Detectar Resonancia ⚠️
7. Solución particular: `y_p = -x/4 * cos(2x)` (forma resonante)
8. Solución final: `y(x) = (C₁*cos(2x) + C₂*sin(2x)) + (-x/4*cos(2x))`

---

## 🚀 Archivos Relacionados

- **StepResponse.java** - DTO con estructura de pasos
- **StepByStepSolver.java** - Genera los pasos
- **EjemploNuevoFormatoJSON.java** - Ejemplo de uso
- **Main.evaluateWithSteps()** - Método principal
- **Main.evaluateWithStepsAsJson()** - Retorna JSON string

---

## 📊 Comparación: Antes vs Ahora

### ❌ Antes (Formato Simple)
```json
{
  "status": "SUCCESS",
  "equation": "y' + y = 0",
  "finalSolution": "y(x) = C₁*e^(-x)",
  "solutionLatex": "..."
}
```

### ✅ Ahora (Con Pasos)
```json
{
  "status": "SUCCESS",
  "equation": "y' + y = 0",
  "steps": [
    { "type": "CLASSIFY", "title": "...", "explanation": "..." },
    { "type": "CHARACTERISTIC", "title": "...", "explanation": "..." },
    { "type": "ROOTS", "title": "...", "explanation": "..." },
    { "type": "HOMOGENEOUS_SOLUTION", "title": "...", "explanation": "..." }
  ],
  "finalSolution": "y(x) = C₁*e^(-x)",
  "metadata": { ... },
  "stepCount": 4
}
```

---

## 🎯 Para Tu Evaluador

Dile que puede ejecutar:

```java
StepResponse resultado = Main.evaluateWithSteps("y'' - 5*y' + 6*y = 0");
System.out.println(resultado.getFinalSolution());

// O en JSON
String json = Main.evaluateWithStepsAsJson("y'' + 4*y = sin(2*x)");
System.out.println(json);
```

**¡Y obtiene todo paso a paso!**

---

## ✨ Beneficios

✅ **Educativo** - Muestra cómo se resuelve paso a paso  
✅ **Transparent** - Explicación de cada decisión  
✅ **Photomath-like** - Formato similar a apps profesionales  
✅ **Parseable** - Fácil de leer en JavaScript  
✅ **Completo** - Incluye todas las ecuaciones soportadas  

---

**¡Tu proyecto ahora tiene formato Photomath!** 📸✨
