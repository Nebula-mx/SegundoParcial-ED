# 📋 Estructura de DTOs (Data Transfer Objects)

## Resumen: 3 DTOs Principales

Tu proyecto usa **3 clases DTO principales** en `/src/main/java/com/ecuaciones/diferenciales/dto/`:

### 1. **DifferentialEquationResponse.java** (297 líneas)
**Propósito:** Respuesta general con información de la ecuación

**Campos principales:**
- `status` - "SUCCESS" o "ERROR"
- `code` - Código HTTP (200, 400, 500)
- `message` - Mensaje descriptivo
- `equation` - Ecuación original
- `roots` - List de RootInfo (raíces encontradas)
- `homogeneousSolution` - Solución homogénea
- `particulatSolution` - Solución particular
- `finalSolution` - Solución final completa
- `resolutionSteps` - List de pasos textuales

**Inner Class:**
- `RootInfo` - Información de cada raíz (index, real, imaginary, display)

**Uso:** Para respuestas generales sin necesidad de mucho detalle

---

### 2. **StepResponse.java** (226 líneas) ⭐
**Propósito:** Respuesta detallada con pasos paso a paso (Estilo Photomath)

**Campos principales:**
- `status` - "SUCCESS" o "ERROR"
- `message` - Mensaje descriptivo
- `equation` - Ecuación procesada
- `variable` - Variable independiente (usualmente "x")
- `steps` - **List de Step** (cada paso de la resolución)
- `finalSolution` - Solución final
- `solutionLatex` - Versión LaTeX
- `metadata` - Map de metadatos adicionales
- `executionTimeMs` - Tiempo de ejecución
- `stepCount` - Número total de pasos
- `success` - Booleano (true si fue exitoso)

**Inner Class:**
- `Step` - Cada paso individual con:
  - `type` - CLASSIFY, CHARACTERISTIC, ROOTS, HOMOGENEOUS_SOLUTION, PARTICULAR_SOLUTION, FINAL_SOLUTION
  - `title` - Título descriptivo con emoji
  - `order` - Número de orden del paso
  - `expressions` - List de expresiones matemáticas
  - `explanation` - Explicación en texto natural
  - `details` - Map de detalles adicionales

**Uso:** Para la resolución detallada paso a paso (recomendado para frontend)

---

### 3. **DifferentialEquationResponse.java** (Clase interna)
También tiene su propia clase interna `RootInfo` para información de raíces.

---

## 🎯 Comparación Rápida

| Aspecto | DifferentialEquationResponse | StepResponse |
|---------|------------------------------|--------------|
| **Detalle** | Básico | Detallado con pasos |
| **Usos** | API general | Frontend educativo |
| **Pasos** | Texto simple | Estructurados con tipo |
| **Complejidad** | Media | Alta (más información) |
| **Recomendado** | Para evaluaciones | Para tutoriales |

---

## 📂 Archivos Eliminados (Limpieza Reciente)

❌ `PhotomathResponse.java` - Duplicado (ahora es StepResponse)
❌ `PhotomathResponseService.java` - No utilizado

---

## 🔗 Dónde Se Usan

### StepResponse
```
Main.java
├─ Main.evaluateWithSteps()
├─ Main.evaluateWithStepsAsJson()
└─ EjemploNuevoFormatoJSON.java
   └─ DemoFormatoFinal.java
```

### DifferentialEquationResponse
```
EquationEvaluator.java
├─ evaluate(String)
├─ evaluate(String, String)
└─ EjemploParaTuAmigo.java
```

---

## 💡 Recomendaciones de Uso

### Para tu Frontend (Recomendado ⭐)
```java
// Usar StepResponse - Muestra todo paso a paso
StepResponse response = Main.evaluateWithSteps("y'' - 5*y' + 6*y = 0");

// O como JSON string
String json = Main.evaluateWithStepsAsJson("y'' - 5*y' + 6*y = 0");
```

### Para tu Evaluador (Simple)
```java
// Usar DifferentialEquationResponse - Más simple
DifferentialEquationResponse resp = EquationEvaluator.evaluate("y' + y = 0");
```

### Para Testing
```java
// Ambas funcionan, pero StepResponse es más completa
StepResponse response = Main.evaluateWithSteps(ecuacion);
assert response.getStatus().equals("SUCCESS");
assert response.getSteps().size() > 0;
```

---

## 📊 Estructura JSON Ejemplo

### StepResponse (Recomendado para Frontend)
```json
{
  "status": "SUCCESS",
  "message": "Ecuación resuelta exitosamente",
  "equation": "y' + y = 0",
  "variable": "x",
  "steps": [
    {
      "type": "CLASSIFY",
      "title": "📖 Clasificación",
      "order": 1,
      "expressions": ["EDO de orden 1"],
      "explanation": "La ecuación es...",
      "details": {"Tipo": "Homogénea"}
    }
  ],
  "finalSolution": "y(x) = C₁*e^(-x)",
  "solutionLatex": "$y(x) = C₁ \\cdot e^{-x}$",
  "stepCount": 5,
  "success": true
}
```

---

## ✅ Conclusión

Tu proyecto ahora usa **2 DTOs principales y limpios**:
- **StepResponse** - Para resoluciones detalladas paso a paso ⭐
- **DifferentialEquationResponse** - Para respuestas generales

No hay duplicados, código es limpio y mantenible.

