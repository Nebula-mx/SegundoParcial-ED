# 📸 Photomath-Style API Guide

## ¿Qué es?

Tu backend ahora tiene un nuevo endpoint **tipo Photomath** que resuelve ecuaciones diferenciales paso a paso, mostrando cada etapa del proceso de resolución.

## 📍 Base URL

```
http://localhost:8080/api/photomath
```

---

## 🚀 Endpoints Disponibles

### 1️⃣ **POST /api/photomath/solve** - Resolver EDO con Pasos

**Descripción**: Resuelve una ecuación diferencial y devuelve cada paso del proceso.

**Request**:
```json
{
  "equation": "y'' + 3*y' + 2*y = e^x",
  "variable": "x",
  "initialConditions": ["y(0)=1", "y'(0)=0"],
  "method": "UC"
}
```

**Response** (200 OK):
```json
{
  "status": "success",
  "message": "Ecuación procesada exitosamente",
  "expression": "y'' + 3*y' + 2*y = e^x",
  "equation": "y'' + 3*y' + 2*y = e^x",
  "variable": "x",
  "finalSolution": "y(x) = [Solución completa]",
  "solutionLatex": "$y(x) = [Solución completa]$",
  "steps": [
    {
      "type": "CLASSIFY",
      "order": 1,
      "title": "📖 Parsing de la ecuación",
      "description": "Convertir la ecuación textual a estructura interna",
      "explanation": "Normalizando a formato estándar...",
      "expressions": ["y'' + 3*y' + 2*y = e^x"],
      "details": {
        "Notación": "Normalizando a formato estándar..."
      }
    },
    {
      "type": "CLASSIFY",
      "order": 2,
      "title": "🏷️ Clasificación de la EDO",
      "description": "Determinar características de la ecuación",
      "explanation": "Determinar características de la ecuación",
      "expressions": ["EDO de coeficientes constantes"],
      "details": {
        "Tipo": "No-homogénea",
        "Coeficientes": "Constantes"
      }
    },
    {
      "type": "CHARACTERISTIC",
      "order": 3,
      "title": "📐 Ecuación característica",
      "description": "Construcción de la ecuación auxiliar",
      "explanation": "Construcción de la ecuación auxiliar",
      "expressions": ["r^n + coeficientes*r^(n-1) + ... = 0"],
      "details": {
        "Método": "Sustitución exponencial y = e^(rx)"
      }
    },
    // ... más pasos ...
    {
      "type": "FINAL_SOLUTION",
      "order": 7,
      "title": "✅ Solución Final",
      "description": "Respuesta del problema de EDO",
      "explanation": "Respuesta del problema de EDO",
      "expressions": ["y(x) = [Solución completa]"],
      "details": {
        "Estado": "Completada"
      }
    }
  ],
  "metadata": {
    "Tipo": "No-homogénea",
    "Pasos totales": "7",
    "Método": "UC",
    "Variable": "x"
  },
  "executionTimeMs": 45
}
```

**Parámetros**:

| Parámetro | Tipo | Requerido | Descripción |
|-----------|------|-----------|-------------|
| `equation` | string | ✅ | La ecuación diferencial (ej: `y'' - 5*y' + 6*y = 0`) |
| `variable` | string | ❌ | Variable independiente (default: `x`) |
| `method` | string | ❌ | Método de resolución: `UC` o `VP` (default: `UC`) |
| `initialConditions` | array[string] | ❌ | Condiciones iniciales (ej: `["y(0)=1", "y'(0)=2"]`) |

**Ejemplos**:

#### Homogénea de Orden 2:
```bash
curl -X POST http://localhost:8080/api/photomath/solve \
  -H "Content-Type: application/json" \
  -d '{
    "equation": "y\" - 5*y\" + 6*y = 0",
    "variable": "x"
  }'
```

#### No-homogénea con Coeficientes Indeterminados:
```bash
curl -X POST http://localhost:8080/api/photomath/solve \
  -H "Content-Type: application/json" \
  -d '{
    "equation": "y\" - y = 2*x",
    "method": "UC"
  }'
```

#### Con Condiciones Iniciales:
```bash
curl -X POST http://localhost:8080/api/photomath/solve \
  -H "Content-Type: application/json" \
  -d '{
    "equation": "y\" + 4*y = 0",
    "initialConditions": ["y(0)=1", "y\'(0)=2"]
  }'
```

---

### 2️⃣ **GET /api/photomath/examples** - Obtener Ejemplos

**Descripción**: Devuelve ejemplos de ecuaciones para probar.

**Response**:
```json
{
  "homogeneous": [
    "y'' + 4y = 0",
    "y'' - 3y' + 2y = 0",
    "y''' - y'' = 0",
    "y'' - y = 0"
  ],
  "nonHomogeneous_UC": [
    "y'' - 3y' + 2y = e^x",
    "y'' - y = 2*x",
    "y'' + 4y = 8*cos(2*x)"
  ],
  "nonHomogeneous_VP": [
    "y'' + y = sec(x)",
    "y'' - y = e^x/x"
  ],
  "withInitialConditions": {
    "equation": "y'' + 4y = 0",
    "initialConditions": ["y(0)=1", "y'(0)=2"]
  },
  "description": "Ecuaciones diferenciales para pruebas. Úsalas para validar el solver."
}
```

**Uso**:
```bash
curl -X GET http://localhost:8080/api/photomath/examples
```

---

### 3️⃣ **GET /api/photomath/health** - Health Check

**Descripción**: Verifica que el servidor esté funcionando.

**Response**:
```json
{
  "status": "UP",
  "service": "Photomath-style ODE Solver",
  "version": "1.0.0"
}
```

**Uso**:
```bash
curl -X GET http://localhost:8080/api/photomath/health
```

---

## 📝 Estructura de Response

### SolutionResponse

```json
{
  "status": "success" | "error" | "partial" | "unsupported",
  "message": "string (Descripción del resultado)",
  "expression": "string (La ecuación ingresada)",
  "equation": "string (La ecuación)",
  "variable": "string (Variable independiente)",
  "finalSolution": "string (Respuesta final)",
  "solutionLatex": "string (En formato LaTeX para renderizar)",
  "steps": [ /* Array de Step */ ],
  "metadata": {
    "Tipo": "Homogénea | No-homogénea",
    "Pasos totales": "number",
    "Método": "UC | VP",
    "Variable": "string"
  },
  "executionTimeMs": number
}
```

### Step

```json
{
  "type": "CLASSIFY | CHARACTERISTIC | FIND_ROOTS | HOMOGENEOUS_SOLUTION | PARTICULAR_SOLUTION | GENERAL_SOLUTION | APPLY_CONDITIONS | FINAL_SOLUTION",
  "order": number,
  "title": "string (Título con emojis)",
  "description": "string (Descripción breve)",
  "explanation": "string (Explicación detallada)",
  "expressions": [ "string (Expresión matemática 1)", "..." ],
  "details": {
    "key1": "value1",
    "key2": "value2"
  }
}
```

---

## 🎯 Tipos de Steps

| Tipo | Descripción |
|------|-------------|
| `CLASSIFY` | Parsing y clasificación de la ecuación |
| `CHARACTERISTIC` | Formación de la ecuación característica |
| `FIND_ROOTS` | Encontrar las raíces del polinomio |
| `HOMOGENEOUS_SOLUTION` | Construcción de y_h(x) |
| `PARTICULAR_SOLUTION` | Construcción de y_p(x) (UC o VP) |
| `GENERAL_SOLUTION` | Combinación y_h + y_p |
| `APPLY_CONDITIONS` | Aplicar condiciones iniciales |
| `FINAL_SOLUTION` | Solución final con CI |

---

## 💡 Ejemplo Completo: Frontend Integration

```javascript
// En tu frontend (React, Vue, Angular, etc.)

async function resolverEDO(ecuacion, metodo = "UC") {
  const response = await fetch('http://localhost:8080/api/photomath/solve', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json'
    },
    body: JSON.stringify({
      equation: ecuacion,
      method: metodo,
      variable: 'x'
    })
  });

  const data = await response.json();
  
  if (data.status === 'success') {
    // Mostrar cada paso
    data.steps.forEach((step, index) => {
      console.log(`Paso ${step.order}: ${step.title}`);
      console.log(`${step.explanation}`);
      step.expressions.forEach(expr => {
        console.log(`  📌 ${expr}`);
      });
    });
    
    // Mostrar solución final
    console.log(`\n✅ Solución Final: ${data.finalSolution}`);
    console.log(`LaTeX: ${data.solutionLatex}`);
  } else {
    console.error(`Error: ${data.message}`);
  }
}

// Uso:
resolverEDO("y'' - 5*y' + 6*y = 0");
resolverEDO("y'' - y = 2*x", "UC");
```

---

## ✅ Formatos Soportados de Ecuación

✅ **Válidos**:
- `y'' - 5*y' + 6*y = 0`
- `y'' + 4*y = 8*cos(2*x)`
- `y' + 2*y = e^(-x)`
- `y''' - y'' = 0`
- `y'' + y = sin(x)`
- `y' - 2*y + 3 = 0`

❌ **No válidos**:
- `y"` (sin aclaración de orden)
- `d2y/dx2` (usar notación de derivadas)
- Ecuaciones con orden > 5 (pueden no ser soportadas)

---

## 🔧 Integración Backend

Tu amigo puede usar el JAR del backend así:

```bash
# 1. Compilar
cd /home/hector_ar/Documentos/SegundoParcial-ED/geogera
mvn clean package

# 2. Ejecutar servidor
java -jar target/geogera-0.1.jar

# 3. El servidor estará en:
# http://localhost:8080
```

---

## 📌 Estructura JSON Esperada por el Frontend

Para que el frontend renderice correctamente los pasos, puede iterar así:

```javascript
{
  steps: [
    {
      order: 1,              // Número del paso
      type: "CLASSIFY",      // Tipo de paso (para styling)
      title: "📖 Parsing...", // Título con emoji
      explanation: "...",    // Explicación en lenguaje natural
      expressions: [         // Expresiones matemáticas
        "y'' - 5y' + 6y = 0"
      ],
      details: {             // Información adicional
        "Notación": "Normalizando..."
      }
    },
    // ... más steps
  ],
  finalSolution: "y(x) = C1*e^(3x) + C2*e^(2x)",
  solutionLatex: "$y(x) = C_1 e^{3x} + C_2 e^{2x}$",
  metadata: {
    "Tipo": "Homogénea",
    "Pasos totales": "5"
  }
}
```

---

## 🎨 Recomendaciones para el Frontend

1. **Mostrar cada paso en orden** usando el campo `order`
2. **Usar emojis** del campo `title` para visual appeal
3. **Renderizar expresiones** con MathJax o KaTeX usando `solutionLatex`
4. **Mostrar detalles** en un acordeón colapsable
5. **Dar opción** de seleccionar método (UC vs VP)

---

## 🆘 Troubleshooting

**P: "Connection refused"**
- R: Asegúrate que el servidor esté corriendo con `mvn spring-boot:run`

**P: "Ecuación no puede estar vacía"**
- R: Verifica que estés enviando el campo `equation` en el JSON

**P: "CORS error"**
- R: El controller tiene `@CrossOrigin(origins = "*")`, debería funcionar desde cualquier origin

**P: "Timeout en respuesta"**
- R: Algunas ecuaciones complejas pueden tardar más. Aumenta el timeout en tu frontend.

---

## 📞 Contacto

Si tienes dudas sobre la integración, revisa:
- Los exemplos en `/api/photomath/examples`
- El response structure en la documentación
- Los tests en `test_main_interactive.sh`

¡Listo para el frontend! 🚀
