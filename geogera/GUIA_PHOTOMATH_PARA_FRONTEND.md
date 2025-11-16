# 📸 Guía: Endpoint Photomath-Style para tu Frontend

## 🎯 Resumen Rápido

Tu backend ahora tiene un endpoint `/api/photomath/solve` que devuelve una EDO resuelta **paso a paso**, exactamente como Photomath.

---

## 🚀 Cómo Usar

### 1️⃣ URL del Endpoint

```
POST http://localhost:8080/api/photomath/solve
```

### 2️⃣ Headers

```
Content-Type: application/json
```

### 3️⃣ Body (Request JSON)

```json
{
  "equation": "y'' - 5*y' + 6*y = 0",
  "variable": "x",
  "method": "UC",
  "initialConditions": ["y(0)=1", "y'(0)=2"]
}
```

**Campos:**
- `equation` (string, **required**): La EDO a resolver
- `variable` (string, optional): Variable independiente (default: "x")
- `method` (string, optional): "UC" (Coeficientes Indeterminados) o "VP" (Variación de Parámetros), default: "UC"
- `initialConditions` (array, optional): Lista de condiciones iniciales

---

## 📥 Response (Lo que Recibes)

```json
{
  "status": "success",
  "message": "Ecuación procesada exitosamente",
  "expression": "y'' - 5*y' + 6*y = 0",
  "equation": "y'' - 5*y' + 6*y = 0",
  "variable": "x",
  "steps": [
    {
      "type": "CLASSIFY",
      "title": "📖 Parsing de la ecuación",
      "order": 1,
      "explanation": "Convertir la ecuación textual a estructura interna",
      "expressions": [
        "y'' - 5*y' + 6*y = 0"
      ],
      "details": {
        "Notación": "Normalizando a formato estándar..."
      }
    },
    {
      "type": "CLASSIFY",
      "title": "🏷️ Clasificación de la EDO",
      "order": 2,
      "explanation": "Determinar características de la ecuación",
      "expressions": [
        "EDO de coeficientes constantes"
      ],
      "details": {
        "Tipo": "Homogénea",
        "Coeficientes": "Constantes"
      }
    },
    {
      "type": "CHARACTERISTIC",
      "title": "📐 Ecuación característica",
      "order": 3,
      "explanation": "Construcción de la ecuación auxiliar",
      "expressions": [
        "r^n + coeficientes*r^(n-1) + ... = 0"
      ],
      "details": {
        "Método": "Sustitución exponencial y = e^(rx)"
      }
    },
    {
      "type": "FIND_ROOTS",
      "title": "🔍 Encontrar raíces",
      "order": 4,
      "explanation": "Resolver la ecuación característica",
      "expressions": [
        "Raíces calculadas del polinomio característico"
      ],
      "details": {
        "Método": "Análisis polinómico"
      }
    },
    {
      "type": "HOMOGENEOUS_SOLUTION",
      "title": "✨ Solución Homogénea",
      "order": 5,
      "explanation": "Construcción de y_h(x) basada en las raíces",
      "expressions": [
        "y_h(x) = C1*e^(r1*x) + C2*e^(r2*x) + ..."
      ],
      "details": {
        "Forma": "Combinación lineal de soluciones fundamentales"
      }
    },
    {
      "type": "FINAL_SOLUTION",
      "title": "✅ Solución Final",
      "order": 6,
      "explanation": "Respuesta del problema de EDO",
      "expressions": [
        "y(x) = [Solución completa]"
      ],
      "details": {
        "Estado": "Completada"
      }
    }
  ],
  "finalSolution": "y(x) = [Solución completa]",
  "solutionLatex": "$y(x) = [Solución completa]$",
  "metadata": {
    "Tipo": "Homogénea",
    "Pasos totales": "6",
    "Método": "UC",
    "Variable": "x"
  },
  "executionTimeMs": 45
}
```

---

## 🎨 Estructura de cada Step

Cada paso tiene esta estructura:

```typescript
interface Step {
  type: string;           // CLASSIFY, CHARACTERISTIC, FIND_ROOTS, HOMOGENEOUS_SOLUTION, etc.
  title: string;          // Título con emoji (ej: "📐 Ecuación característica")
  order: number;          // Número del paso (1, 2, 3...)
  explanation: string;    // Explicación en texto plano
  expressions: string[];  // Array de expresiones matemáticas
  details: {              // Objeto con información adicional
    [key: string]: string
  }
}
```

---

## 💡 Ejemplos de Request

### Ejemplo 1: Homogénea Simple

```bash
curl -X POST http://localhost:8080/api/photomath/solve \
  -H "Content-Type: application/json" \
  -d '{
    "equation": "y'\'' + 2*y = 0",
    "variable": "x",
    "method": "UC"
  }'
```

### Ejemplo 2: No-Homogénea con UC

```bash
curl -X POST http://localhost:8080/api/photomath/solve \
  -H "Content-Type: application/json" \
  -d '{
    "equation": "y'\'' - y = 2*x",
    "variable": "x",
    "method": "UC"
  }'
```

### Ejemplo 3: Con Condiciones Iniciales

```bash
curl -X POST http://localhost:8080/api/photomath/solve \
  -H "Content-Type: application/json" \
  -d '{
    "equation": "y'\'' + y = 0",
    "variable": "x",
    "method": "UC",
    "initialConditions": ["y(0)=1", "y'\''(0)=0"]
  }'
```

### Ejemplo 4: Orden 3

```bash
curl -X POST http://localhost:8080/api/photomath/solve \
  -H "Content-Type: application/json" \
  -d '{
    "equation": "y'\'\'\' - 6*y'\'\' + 11*y'\'' - 6*y = 0",
    "variable": "x"
  }'
```

---

## 📱 Cómo Renderizar en tu Frontend

### React/Vue Example:

```javascript
// 1. Hacer el request
const response = await fetch('http://localhost:8080/api/photomath/solve', {
  method: 'POST',
  headers: { 'Content-Type': 'application/json' },
  body: JSON.stringify({
    equation: "y'' - 5*y' + 6*y = 0",
    variable: "x"
  })
});

const data = await response.json();

// 2. Renderizar los pasos
data.steps.forEach(step => {
  console.log(`${step.order}. ${step.title}`);
  console.log(`   ${step.explanation}`);
  step.expressions.forEach(expr => {
    console.log(`   → ${expr}`);
  });
  console.log(`   Detalles: ${JSON.stringify(step.details)}\n`);
});

// 3. Mostrar solución final
console.log("SOLUCIÓN FINAL:");
console.log(data.finalSolution);
console.log("LaTeX:", data.solutionLatex);
```

---

## 🔗 Otros Endpoints Disponibles

### Health Check
```
GET http://localhost:8080/api/photomath/health
```

**Response:**
```json
{
  "status": "UP",
  "service": "Photomath-style ODE Solver",
  "version": "1.0.0"
}
```

### Ver Ejemplos
```
GET http://localhost:8080/api/photomath/examples
```

---

## 🎬 Flujo Completo de Uso

```
┌─────────────────────────────────────────────────────────────┐
│ 1. Usuario ingresa ecuación en frontend                      │
│    "y'' - 5*y' + 6*y = 0"                                   │
└──────────────────────┬──────────────────────────────────────┘
                       │
                       ▼
┌─────────────────────────────────────────────────────────────┐
│ 2. Frontend envía POST a /api/photomath/solve                │
└──────────────────────┬──────────────────────────────────────┘
                       │
                       ▼
┌─────────────────────────────────────────────────────────────┐
│ 3. Backend procesa y devuelve JSON con steps                 │
│    (CLASSIFY, CHARACTERISTIC, FIND_ROOTS, etc.)             │
└──────────────────────┬──────────────────────────────────────┘
                       │
                       ▼
┌─────────────────────────────────────────────────────────────┐
│ 4. Frontend renderiza cada step animadamente                 │
│    - Muestra título + emoji                                 │
│    - Muestra expresiones matemáticas                        │
│    - Muestra explicación                                    │
│    - Muestra detalles adicionales                           │
└──────────────────────┬──────────────────────────────────────┘
                       │
                       ▼
┌─────────────────────────────────────────────────────────────┐
│ 5. Muestra SOLUCIÓN FINAL con LaTeX renderizado             │
│    en formato bonito (como Photomath)                       │
└─────────────────────────────────────────────────────────────┘
```

---

## ✨ Ventajas de este Enfoque

✅ **Educativo**: Los usuarios ven cada paso de la resolución  
✅ **Transparencia**: No es una "caja negra"  
✅ **Flexible**: Puedes mostrar/ocultar pasos según necesites  
✅ **Escalable**: Fácil agregar más tipos de steps  
✅ **Compatible**: JSON simple, funciona con cualquier frontend  

---

## 🚨 Manejo de Errores

Si algo sale mal, recibirás:

```json
{
  "status": "error",
  "message": "Error al procesar la ecuación",
  "error": "[Detalles del error]",
  "expression": "y'' - 5*y' + 6*y = 0"
}
```

---

## 📝 Notas Importantes

1. **El JAR está en tu proyecto web**, no necesitas el servidor corriendo localmente
2. **Los pasos son informativos**, el resolver real lo hace el backend
3. **Puedes personalizar el rendering** según tu diseño
4. **La estructura JSON es estable**, no cambiará en futuras versiones

---

## 🎓 Preguntas Frecuentes

**P: ¿Cómo renderizo LaTeX en mi frontend?**  
R: Usa librerías como MathJax o KaTeX:
```javascript
// Con KaTeX
katex.render(data.solutionLatex, element);
```

**P: ¿Puedo agregar más steps personalizados?**  
R: Sí, puedes extender el endpoint para agregar más tipos de steps.

**P: ¿Funciona con cualquier ecuación?**  
R: Orden 1, 2, 3+. Homogéneas, no-homogéneas, UC, VP, con CI, sin CI.

---

## 🎉 ¡Listo!

Tu frontend está listo para consumir este endpoint tipo Photomath. ¡Que disfrutes desarrollando! 🚀
