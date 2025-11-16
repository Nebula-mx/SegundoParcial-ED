# 🎯 GUÍA DE USO - PHOTOMATH ENDPOINT PARA EL FRONTEND

## 📌 Resumen Ejecutivo

Tu backend ahora tiene un **nuevo endpoint tipo Photomath** que:

✅ Resuelve EDOs paso a paso  
✅ Devuelve cada fase del proceso  
✅ Incluye explicaciones en texto plano  
✅ Proporciona expresiones matemáticas  
✅ Soporta condiciones iniciales  
✅ Maneja métodos UC y VP  

---

## 🚀 INICIO RÁPIDO (Quick Start)

### Paso 1: Iniciar el Backend

```bash
cd /home/hector_ar/Documentos/SegundoParcial-ED/geogera
mvn spring-boot:run
```

El servidor estará en: `http://localhost:8080`

### Paso 2: Hacer una solicitud

```bash
curl -X POST http://localhost:8080/api/photomath/solve \
  -H "Content-Type: application/json" \
  -d '{
    "equation": "y\" - 5*y\' + 6*y = 0",
    "variable": "x",
    "method": "UC"
  }'
```

### Paso 3: Procesar la respuesta

```json
{
  "status": "success",
  "finalSolution": "y(x) = C1*e^(3x) + C2*e^(2x)",
  "steps": [
    {
      "order": 1,
      "type": "CLASSIFY",
      "title": "📖 Parsing de la ecuación",
      "explanation": "Convertir la ecuación textual a estructura interna",
      "expressions": ["y'' - 5*y' + 6*y = 0"]
    },
    // ... más pasos ...
  ]
}
```

---

##详 EJEMPLO COMPLETO: UX Photomath

### REQUEST

**Entrada del usuario**:
```
Ecuación: y'' - 5*y' + 6*y = 0
Método: Automático (AUTO fallback UC → VP)
Condiciones: (sin CI)
```

### RESPONSE

```json
{
  "status": "success",
  "message": "Ecuación procesada exitosamente",
  "equation": "y'' - 5*y' + 6*y = 0",
  "variable": "x",
  "finalSolution": "y(x) = C1*e^(3x) + C2*e^(2x)",
  "solutionLatex": "$y(x) = C_1 e^{3x} + C_2 e^{2x}$",
  "steps": [
    {
      "order": 1,
      "type": "CLASSIFY",
      "title": "📖 Parsing de la ecuación",
      "description": "Convertir la ecuación textual a estructura interna",
      "explanation": "Convertir la ecuación textual a estructura interna",
      "expressions": ["y'' - 5*y' + 6*y = 0"],
      "details": {
        "Notación": "Normalizando a formato estándar..."
      }
    },
    {
      "order": 2,
      "type": "CLASSIFY",
      "title": "🏷️ Clasificación de la EDO",
      "description": "Determinar características de la ecuación",
      "explanation": "Determinar características de la ecuación",
      "expressions": ["EDO de coeficientes constantes"],
      "details": {
        "Tipo": "Homogénea",
        "Coeficientes": "Constantes"
      }
    },
    {
      "order": 3,
      "type": "CHARACTERISTIC",
      "title": "📐 Ecuación característica",
      "description": "Construcción de la ecuación auxiliar",
      "explanation": "Construcción de la ecuación auxiliar",
      "expressions": ["r^n + coeficientes*r^(n-1) + ... = 0"],
      "details": {
        "Método": "Sustitución exponencial y = e^(rx)"
      }
    },
    {
      "order": 4,
      "type": "FIND_ROOTS",
      "title": "🔍 Encontrar raíces",
      "description": "Resolver la ecuación característica",
      "explanation": "Resolver la ecuación característica",
      "expressions": ["r^2 - 5r + 6 = 0", "r = 3, r = 2"],
      "details": {
        "Método": "Análisis polinómico"
      }
    },
    {
      "order": 5,
      "type": "HOMOGENEOUS_SOLUTION",
      "title": "✨ Solución Homogénea",
      "description": "Construcción de y_h(x) basada en las raíces",
      "explanation": "Construcción de y_h(x) basada en las raíces",
      "expressions": ["y_h(x) = C1*e^(3x) + C2*e^(2x)"],
      "details": {
        "Forma": "Combinación lineal de soluciones fundamentales"
      }
    },
    {
      "order": 6,
      "type": "FINAL_SOLUTION",
      "title": "✅ Solución Final",
      "description": "Respuesta del problema de EDO",
      "explanation": "Respuesta del problema de EDO",
      "expressions": ["y(x) = C1*e^(3x) + C2*e^(2x)"],
      "details": {
        "Estado": "Completada"
      }
    }
  ],
  "metadata": {
    "Tipo": "Homogénea",
    "Pasos totales": "5",
    "Método": "UC",
    "Variable": "x"
  },
  "executionTimeMs": 45
}
```

---

## 🎨 RECOMENDACIÓN DE VISUALIZACIÓN FRONTEND

### Layout Propuesto:

```
┌─────────────────────────────────────────┐
│ 📝 ECUACIÓN INGRESADA                   │
│ y'' - 5*y' + 6*y = 0                   │
└─────────────────────────────────────────┘

┌─────────────────────────────────────────┐
│ 📋 PASO A PASO                          │
├─────────────────────────────────────────┤
│ Paso 1: 📖 Parsing de la ecuación       │
│ ├─ Explicación:                         │
│ │  "Convertir la ecuación a estructura" │
│ └─ Expresión:                           │
│    y'' - 5*y' + 6*y = 0                │
├─────────────────────────────────────────┤
│ Paso 2: 🏷️ Clasificación                │
│ ├─ Tipo: Homogénea                      │
│ ├─ Coeficientes: Constantes             │
│ └─ Expresión: EDO de coeficientes...    │
├─────────────────────────────────────────┤
│ Paso 3: 📐 Ecuación característica      │
│ ├─ r^2 - 5r + 6 = 0                    │
├─────────────────────────────────────────┤
│ Paso 4: 🔍 Encontrar raíces            │
│ ├─ r₁ = 3                               │
│ ├─ r₂ = 2                               │
├─────────────────────────────────────────┤
│ Paso 5: ✨ Solución Homogénea           │
│ ├─ y_h(x) = C₁e^(3x) + C₂e^(2x)        │
├─────────────────────────────────────────┤
│ Paso 6: ✅ Solución Final               │
│ └─ y(x) = C₁e^(3x) + C₂e^(2x)          │
└─────────────────────────────────────────┘

┌─────────────────────────────────────────┐
│ 🏁 RESULTADO FINAL                      │
│ y(x) = C₁e^(3x) + C₂e^(2x)              │
│                                         │
│ LaTeX: $y(x) = C_1 e^{3x} + C_2 e^{2x}$│
└─────────────────────────────────────────┘

┌─────────────────────────────────────────┐
│ ⏱️ Tiempo de cálculo: 45 ms             │
│ 📊 Total de pasos: 6                    │
└─────────────────────────────────────────┘
```

---

## 💻 CÓDIGO FRONTEND (JavaScript/React)

### Ejemplo básico:

```javascript
import React, { useState } from 'react';

function ODESolver() {
  const [equation, setEquation] = useState('');
  const [response, setResponse] = useState(null);
  const [loading, setLoading] = useState(false);

  const handleSolve = async () => {
    setLoading(true);
    try {
      const res = await fetch('http://localhost:8080/api/photomath/solve', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
          equation: equation,
          variable: 'x',
          method: 'UC'
        })
      });
      
      const data = await res.json();
      setResponse(data);
    } catch (err) {
      console.error('Error:', err);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div>
      <input 
        value={equation}
        onChange={(e) => setEquation(e.target.value)}
        placeholder="Ej: y'' - 5*y' + 6*y = 0"
      />
      <button onClick={handleSolve} disabled={loading}>
        {loading ? 'Resolviendo...' : 'Resolver'}
      </button>

      {response && (
        <div>
          <h3>📋 Pasos:</h3>
          {response.steps.map((step) => (
            <div key={step.order} className="step">
              <h4>{step.title}</h4>
              <p>{step.explanation}</p>
              {step.expressions.map((expr, i) => (
                <div key={i}>
                  <code>{expr}</code>
                </div>
              ))}
              {Object.entries(step.details).map(([key, val]) => (
                <small key={key}>{key}: {val}</small>
              ))}
            </div>
          ))}

          <h3>✅ Solución Final:</h3>
          <h2>{response.finalSolution}</h2>
          
          {/* Para renderizar LaTeX, usa MathJax o KaTeX */}
          <div>{response.solutionLatex}</div>
        </div>
      )}
    </div>
  );
}

export default ODESolver;
```

### Con Tailwind CSS:

```jsx
<div className="bg-gradient-to-b from-blue-50 to-white p-8">
  <div className="max-w-4xl mx-auto">
    <h1 className="text-4xl font-bold mb-6">🧮 Resolvedor de EDOs</h1>
    
    <div className="bg-white rounded-lg shadow-lg p-6 mb-6">
      <input 
        className="w-full px-4 py-2 border-2 border-blue-300 rounded-lg focus:outline-none focus:border-blue-500"
        placeholder="Ingresa la ecuación (ej: y'' - 5*y' + 6*y = 0)"
        value={equation}
        onChange={(e) => setEquation(e.target.value)}
      />
      <button 
        onClick={handleSolve}
        className="mt-4 bg-blue-500 hover:bg-blue-600 text-white font-bold py-2 px-6 rounded-lg transition"
      >
        Resolver Ecuación
      </button>
    </div>

    {response && (
      <>
        <div className="space-y-4">
          {response.steps.map((step) => (
            <div key={step.order} className="bg-white rounded-lg shadow p-4 border-l-4 border-blue-500">
              <h3 className="text-lg font-bold mb-2">{step.title}</h3>
              <p className="text-gray-700 mb-2">{step.explanation}</p>
              <div className="bg-gray-100 p-3 rounded font-mono text-sm mb-2">
                {step.expressions.join(', ')}
              </div>
            </div>
          ))}
        </div>

        <div className="mt-8 bg-gradient-to-r from-green-50 to-emerald-50 rounded-lg p-6 border-2 border-green-300">
          <h2 className="text-2xl font-bold text-green-700 mb-2">✅ Solución Final</h2>
          <p className="text-3xl font-bold text-gray-800">{response.finalSolution}</p>
          <small className="text-gray-600">Tiempo: {response.executionTimeMs}ms</small>
        </div>
      </>
    )}
  </div>
</div>
```

---

## 📊 CASOS DE USO

### 1. Ecuación Homogénea Simple

**Input**:
```json
{
  "equation": "y'' + 4*y = 0",
  "variable": "x"
}
```

**Output**: 5-6 pasos, solución con senos/cosenos

---

### 2. No-Homogénea con UC

**Input**:
```json
{
  "equation": "y'' - y = 2*x",
  "method": "UC"
}
```

**Output**: 7-8 pasos, incluye forma propuesta y coeficientes

---

### 3. Con Condiciones Iniciales

**Input**:
```json
{
  "equation": "y'' + 4*y = 0",
  "initialConditions": ["y(0)=1", "y'(0)=2"]
}
```

**Output**: 8-9 pasos, incluye cálculo de constantes

---

### 4. Orden Superior

**Input**:
```json
{
  "equation": "y''' - 6*y'' + 11*y' - 6*y = 0"
}
```

**Output**: 5-6 pasos, raíces múltiples

---

## ✅ CHECKLIST DE INTEGRACIÓN

- [ ] Backend corriendo en http://localhost:8080
- [ ] Endpoint `/api/photomath/solve` respondiendo
- [ ] Frontend lee y renderiza los `steps`
- [ ] Frontend muestra `finalSolution` destacado
- [ ] LaTeX renderizado con MathJax/KaTeX
- [ ] Diseño tipo Photomath implementado
- [ ] CORS funcionando sin errores
- [ ] Manejo de errores implementado

---

## 🆘 FAQ

**P: ¿Cómo renderizo LaTeX?**
```javascript
// Opción 1: MathJax
import MathJax from 'react-mathjax';
<MathJax.Node>{response.solutionLatex}</MathJax.Node>

// Opción 2: KaTeX
import katex from 'katex';
katex.render(response.solutionLatex, domElement);
```

**P: ¿Cómo manejo errores?**
```javascript
if (response.status !== 'success') {
  alert(`Error: ${response.message}`);
}
```

**P: ¿Cómo muestro todos los pasos?**
```javascript
response.steps.sort((a,b) => a.order - b.order).forEach(step => {
  // Renderizar
});
```

---

## 🎯 PRÓXIMOS PASOS

1. **Integra el endpoint** en tu frontend
2. **Renderiza los pasos** de forma visual
3. **Testa con ejemplos** del endpoint `/examples`
4. **Personaliza el styling** según tu diseño
5. **Optimiza la UX** según feedback de usuarios

---

¡Listo! El backend está preparado para tu frontend. 🚀
