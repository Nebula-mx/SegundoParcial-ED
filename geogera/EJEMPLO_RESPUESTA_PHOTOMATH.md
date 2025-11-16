# 📊 Ejemplo Real: Respuesta JSON Photomath

Este es un ejemplo REAL de cómo se vería la respuesta del endpoint `/api/photomath/solve`.

## 🎯 Request

```json
{
  "equation": "y'' - 5*y' + 6*y = 0",
  "variable": "x",
  "method": "UC",
  "initialConditions": ["y(0)=1", "y'(0)=2"]
}
```

## 📥 Response (Respuesta Completa)

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
      "description": null,
      "expressions": [
        "y'' - 5*y' + 6*y = 0"
      ],
      "explanation": "Convertir la ecuación textual a estructura interna",
      "details": {
        "Notación": "Normalizando a formato estándar..."
      }
    },
    {
      "type": "CLASSIFY",
      "title": "🏷️ Clasificación de la EDO",
      "order": 2,
      "description": "Determinar características de la ecuación",
      "expressions": [
        "EDO de coeficientes constantes"
      ],
      "explanation": "Determinar características de la ecuación",
      "details": {
        "Tipo": "Homogénea",
        "Coeficientes": "Constantes"
      }
    },
    {
      "type": "CHARACTERISTIC",
      "title": "📐 Ecuación característica",
      "order": 3,
      "description": "Construcción de la ecuación auxiliar",
      "expressions": [
        "r^2 - 5*r + 6 = 0"
      ],
      "explanation": "Construcción de la ecuación auxiliar",
      "details": {
        "Método": "Sustitución exponencial y = e^(rx)"
      }
    },
    {
      "type": "FIND_ROOTS",
      "title": "🔍 Encontrar raíces",
      "order": 4,
      "description": "Resolver la ecuación característica",
      "expressions": [
        "r = 2, r = 3"
      ],
      "explanation": "Resolver la ecuación característica",
      "details": {
        "Método": "Análisis polinómico"
      }
    },
    {
      "type": "HOMOGENEOUS_SOLUTION",
      "title": "✨ Solución Homogénea",
      "order": 5,
      "description": "Construcción de y_h(x) basada en las raíces",
      "expressions": [
        "y_h(x) = C1*e^(2*x) + C2*e^(3*x)"
      ],
      "explanation": "Construcción de y_h(x) basada en las raíces",
      "details": {
        "Forma": "Combinación lineal de soluciones fundamentales"
      }
    },
    {
      "type": "APPLY_CONDITIONS",
      "title": "🔧 Aplicar condiciones iniciales",
      "order": 6,
      "description": "Sustituir valores iniciales en la solución general",
      "expressions": [
        "y(0)=1",
        "y'(0)=2"
      ],
      "explanation": "Sustituir valores iniciales en la solución general",
      "details": {
        "Número de CI": "2"
      }
    },
    {
      "type": "APPLY_CONDITIONS",
      "title": "🧮 Resolver sistema",
      "order": 7,
      "description": "Determinar constantes de integración",
      "expressions": [],
      "explanation": "Determinar constantes de integración",
      "details": {
        "Incógnitas": "C1, C2, ..., Cn"
      }
    },
    {
      "type": "FINAL_SOLUTION",
      "title": "✅ Solución Final",
      "order": 8,
      "description": "Respuesta del problema de EDO",
      "expressions": [
        "y(x) = e^(2x) - e^(3x)"
      ],
      "explanation": "Respuesta del problema de EDO",
      "details": {
        "Estado": "Completada"
      }
    }
  ],
  "finalSolution": "y(x) = e^(2x) - e^(3x)",
  "solutionLatex": "$y(x) = e^{2x} - e^{3x}$",
  "metadata": {
    "Tipo": "Homogénea",
    "Pasos totales": "8",
    "Método": "UC",
    "Variable": "x"
  },
  "executionTimeMs": 45,
  "error": null
}
```

---

## 🎬 Cómo Renderizar en Frontend

### Vue.js Example

```vue
<template>
  <div class="photomath-solver">
    <!-- Input -->
    <div class="input-section">
      <input v-model="equation" placeholder="y'' - 5*y' + 6*y = 0">
      <button @click="solve">Resolver</button>
    </div>

    <!-- Steps -->
    <div v-if="response && response.steps" class="steps">
      <div 
        v-for="(step, index) in response.steps" 
        :key="index"
        class="step"
        :class="{ active: expandedStep === index }"
        @click="expandedStep = expandedStep === index ? null : index"
      >
        <!-- Header del Step -->
        <div class="step-header">
          <span class="step-order">{{ step.order }}</span>
          <span class="step-title">{{ step.title }}</span>
          <span class="step-type">[{{ step.type }}]</span>
        </div>

        <!-- Contenido expandible -->
        <transition name="expand">
          <div v-if="expandedStep === index" class="step-content">
            <!-- Explicación -->
            <p class="explanation">{{ step.explanation }}</p>

            <!-- Expresiones Matemáticas -->
            <div class="expressions">
              <div v-for="(expr, i) in step.expressions" :key="i" class="expression">
                <code>{{ expr }}</code>
              </div>
            </div>

            <!-- Detalles -->
            <div v-if="Object.keys(step.details).length > 0" class="details">
              <div v-for="(value, key) in step.details" :key="key" class="detail-item">
                <strong>{{ key }}:</strong> {{ value }}
              </div>
            </div>
          </div>
        </transition>
      </div>
    </div>

    <!-- Solución Final -->
    <div v-if="response" class="final-solution">
      <h2>✅ Solución Final</h2>
      <p class="solution">{{ response.finalSolution }}</p>
      <p class="latex">{{ response.solutionLatex }}</p>
      <p class="time">⏱️ Tiempo: {{ response.executionTimeMs }}ms</p>
    </div>

    <!-- Error -->
    <div v-if="error" class="error">
      ❌ {{ error }}
    </div>
  </div>
</template>

<script>
export default {
  data() {
    return {
      equation: "y'' - 5*y' + 6*y = 0",
      response: null,
      error: null,
      expandedStep: 0
    };
  },
  methods: {
    async solve() {
      try {
        this.error = null;
        const res = await fetch('http://localhost:8080/api/photomath/solve', {
          method: 'POST',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify({
            equation: this.equation,
            variable: 'x',
            method: 'UC'
          })
        });
        this.response = await res.json();
      } catch (e) {
        this.error = e.message;
      }
    }
  }
};
</script>

<style scoped>
.photomath-solver {
  max-width: 900px;
  margin: 0 auto;
  font-family: 'Segoe UI', sans-serif;
}

.input-section {
  display: flex;
  gap: 10px;
  margin-bottom: 30px;
}

.input-section input {
  flex: 1;
  padding: 12px;
  font-size: 16px;
  border: 2px solid #ddd;
  border-radius: 8px;
}

.input-section button {
  padding: 12px 30px;
  background: #007bff;
  color: white;
  border: none;
  border-radius: 8px;
  cursor: pointer;
  font-weight: bold;
}

.steps {
  margin: 30px 0;
}

.step {
  margin: 15px 0;
  border-left: 4px solid #007bff;
  background: #f8f9fa;
  border-radius: 4px;
  overflow: hidden;
  cursor: pointer;
  transition: all 0.3s ease;
}

.step:hover {
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.step.active {
  background: #e7f3ff;
}

.step-header {
  display: flex;
  align-items: center;
  gap: 15px;
  padding: 15px;
  font-weight: 500;
}

.step-order {
  background: #007bff;
  color: white;
  width: 30px;
  height: 30px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 12px;
}

.step-title {
  flex: 1;
  font-size: 16px;
}

.step-type {
  font-size: 12px;
  color: #999;
  background: white;
  padding: 4px 8px;
  border-radius: 4px;
}

.step-content {
  padding: 0 15px 15px 60px;
  border-top: 1px solid #ddd;
}

.explanation {
  margin: 10px 0;
  color: #555;
  line-height: 1.6;
}

.expressions {
  margin: 15px 0;
}

.expression {
  background: white;
  padding: 10px;
  margin: 5px 0;
  border-radius: 4px;
  border-left: 3px solid #28a745;
  font-family: 'Courier New', monospace;
}

.details {
  margin-top: 15px;
}

.detail-item {
  padding: 8px 0;
  border-bottom: 1px solid #eee;
}

.final-solution {
  margin-top: 40px;
  padding: 20px;
  background: #d4edda;
  border-radius: 8px;
  border-left: 4px solid #28a745;
}

.final-solution h2 {
  margin-top: 0;
  color: #155724;
}

.solution {
  font-size: 18px;
  font-weight: bold;
  color: #155724;
  margin: 10px 0;
}

.latex {
  font-style: italic;
  color: #666;
}

.time {
  font-size: 12px;
  color: #999;
  margin-top: 10px;
}

.error {
  margin-top: 20px;
  padding: 15px;
  background: #f8d7da;
  color: #721c24;
  border-radius: 4px;
  border-left: 4px solid #dc3545;
}

.expand-enter-active, .expand-leave-active {
  transition: max-height 0.3s ease;
}

.expand-enter, .expand-leave-to {
  max-height: 0;
  overflow: hidden;
}
</style>
```

---

## 📱 React Example

```jsx
import React, { useState } from 'react';
import './PhotomathSolver.css';

export default function PhotomathSolver() {
  const [equation, setEquation] = useState("y'' - 5*y' + 6*y = 0");
  const [response, setResponse] = useState(null);
  const [expandedStep, setExpandedStep] = useState(0);
  const [error, setError] = useState(null);

  const handleSolve = async () => {
    try {
      setError(null);
      const res = await fetch('http://localhost:8080/api/photomath/solve', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
          equation,
          variable: 'x',
          method: 'UC'
        })
      });
      setResponse(await res.json());
    } catch (e) {
      setError(e.message);
    }
  };

  return (
    <div className="photomath-solver">
      <div className="input-section">
        <input
          value={equation}
          onChange={(e) => setEquation(e.target.value)}
          placeholder="y'' - 5*y' + 6*y = 0"
        />
        <button onClick={handleSolve}>Resolver</button>
      </div>

      {response && response.steps && (
        <div className="steps">
          {response.steps.map((step, i) => (
            <div
              key={i}
              className={`step ${expandedStep === i ? 'active' : ''}`}
              onClick={() => setExpandedStep(expandedStep === i ? -1 : i)}
            >
              <div className="step-header">
                <span className="step-order">{step.order}</span>
                <span className="step-title">{step.title}</span>
                <span className="step-type">[{step.type}]</span>
              </div>

              {expandedStep === i && (
                <div className="step-content">
                  <p className="explanation">{step.explanation}</p>
                  <div className="expressions">
                    {step.expressions.map((expr, j) => (
                      <div key={j} className="expression">
                        <code>{expr}</code>
                      </div>
                    ))}
                  </div>
                  {Object.keys(step.details).length > 0 && (
                    <div className="details">
                      {Object.entries(step.details).map(([key, value]) => (
                        <div key={key} className="detail-item">
                          <strong>{key}:</strong> {value}
                        </div>
                      ))}
                    </div>
                  )}
                </div>
              )}
            </div>
          ))}
        </div>
      )}

      {response && (
        <div className="final-solution">
          <h2>✅ Solución Final</h2>
          <p className="solution">{response.finalSolution}</p>
          <p className="latex">{response.solutionLatex}</p>
          <p className="time">⏱️ Tiempo: {response.executionTimeMs}ms</p>
        </div>
      )}

      {error && <div className="error">❌ {error}</div>}
    </div>
  );
}
```

---

## 🎯 Estructura de Tipos (TypeScript)

```typescript
interface SolutionResponse {
  status: 'success' | 'error' | 'partial' | 'unsupported';
  message: string;
  expression: string;
  equation: string;
  variable: string;
  steps: Step[];
  finalSolution: string;
  solutionLatex: string;
  metadata: Record<string, string>;
  executionTimeMs: number;
  error?: string;
}

interface Step {
  type: StepType;
  title: string;
  order: number;
  description: string | null;
  expressions: string[];
  explanation: string;
  details: Record<string, string>;
}

type StepType = 
  | 'CLASSIFY'
  | 'CHARACTERISTIC'
  | 'FIND_ROOTS'
  | 'HOMOGENEOUS_SOLUTION'
  | 'PARTICULAR_SOLUTION'
  | 'GENERAL_SOLUTION'
  | 'APPLY_CONDITIONS'
  | 'FINAL_SOLUTION'
  | 'VERIFICATION';
```

---

## 🎬 Animación de Steps (CSS)

```css
@keyframes slideIn {
  from {
    opacity: 0;
    transform: translateX(-20px);
  }
  to {
    opacity: 1;
    transform: translateX(0);
  }
}

.step {
  animation: slideIn 0.3s ease-out;
}

.step:nth-child(1) { animation-delay: 0.1s; }
.step:nth-child(2) { animation-delay: 0.2s; }
.step:nth-child(3) { animation-delay: 0.3s; }
.step:nth-child(4) { animation-delay: 0.4s; }
.step:nth-child(5) { animation-delay: 0.5s; }
```

---

## 🎉 ¡Listo! Tu Frontend Photomath

Con esto ya tienes todo para mostrar los pasos paso a paso como Photomath.

**Próximos pasos:**
1. Copia el endpoint URL: `http://localhost:8080/api/photomath/solve`
2. Usa uno de los ejemplos Vue o React
3. Personaliza los estilos según tu diseño
4. ¡Disfruta! 🚀
