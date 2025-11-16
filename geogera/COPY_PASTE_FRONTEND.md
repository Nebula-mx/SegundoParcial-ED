# 📋 Copy-Paste: Lo Que Tu Amigo Necesita

## 🎯 Paso 1: Obtener el Backend

### Copiar el JAR

```bash
# Copiar desde aquí:
/home/hector_ar/Documentos/SegundoParcial-ED/geogera/target/geogera-0.1.jar

# A tu proyecto:
cp /home/hector_ar/Documentos/SegundoParcial-ED/geogera/target/geogera-0.1.jar ./backend/
```

### O Usar Directamente

```bash
cd /home/hector_ar/Documentos/SegundoParcial-ED/geogera
java -jar target/geogera-0.1.jar
```

---

## 🚀 Paso 2: Iniciar el Backend

```bash
java -jar geogera-0.1.jar
```

**Output esperado:**
```
  .   ____          _            __ _ _
 /\\ / ___'_ __ _ _(_)_ __  __ _ \ \ \ \
( ( )\___ | '_ | '_| | '_ \/ _` | \ \ \ \
 \\/  ___)| |_)| | | | | || (_| |  ) ) ) )
  '  |____| .__|_| |_|_| |_|\__, | / / / /
 =========|_|==============|___/=/_/_/_/

Tomcat started on port(s): 8080
```

---

## 💻 Paso 3: Copiar Endpoint URL

```
http://localhost:8080/api/photomath/solve
```

---

## 🎨 Paso 4: JavaScript/React - Copy-Paste

### HTML Puro

```html
<!DOCTYPE html>
<html>
<head>
    <title>Photomath Solver</title>
    <style>
        body {
            font-family: Arial, sans-serif;
            max-width: 900px;
            margin: 0 auto;
            padding: 20px;
        }
        .container {
            background: #f5f5f5;
            padding: 20px;
            border-radius: 8px;
        }
        input {
            width: 100%;
            padding: 10px;
            margin-bottom: 10px;
            border: 1px solid #ddd;
            border-radius: 4px;
        }
        button {
            background: #007bff;
            color: white;
            padding: 10px 20px;
            border: none;
            border-radius: 4px;
            cursor: pointer;
        }
        .step {
            background: white;
            margin: 10px 0;
            padding: 15px;
            border-left: 4px solid #007bff;
            border-radius: 4px;
        }
        .solution {
            background: #d4edda;
            padding: 15px;
            border-radius: 4px;
            margin-top: 20px;
        }
    </style>
</head>
<body>
    <div class="container">
        <h1>🧮 Photomath Solver</h1>
        
        <input id="equation" value="y'' - 5*y' + 6*y = 0" placeholder="Ingresa ecuación">
        <button onclick="solve()">Resolver</button>
        
        <div id="steps"></div>
        <div id="solution"></div>
    </div>

    <script>
        async function solve() {
            const equation = document.getElementById('equation').value;
            
            try {
                const response = await fetch('http://localhost:8080/api/photomath/solve', {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify({
                        equation: equation,
                        variable: 'x',
                        method: 'UC'
                    })
                });
                
                const data = await response.json();
                
                if (data.status === 'error') {
                    alert('Error: ' + data.message);
                    return;
                }
                
                // Mostrar pasos
                let stepsHtml = '';
                data.steps.forEach(step => {
                    stepsHtml += `
                        <div class="step">
                            <h4>${step.order}. ${step.title}</h4>
                            <p>${step.explanation}</p>
                            <ul>
                                ${step.expressions.map(e => `<li><code>${e}</code></li>`).join('')}
                            </ul>
                        </div>
                    `;
                });
                
                document.getElementById('steps').innerHTML = stepsHtml;
                
                // Mostrar solución
                document.getElementById('solution').innerHTML = `
                    <h2>✅ Solución Final</h2>
                    <p><strong>${data.finalSolution}</strong></p>
                    <p><em>${data.solutionLatex}</em></p>
                `;
            } catch (error) {
                alert('Error: ' + error.message);
            }
        }
    </script>
</body>
</html>
```

### React Component

```jsx
import React, { useState } from 'react';

function PhotomathSolver() {
    const [equation, setEquation] = useState("y'' - 5*y' + 6*y = 0");
    const [data, setData] = useState(null);
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState(null);

    const handleSolve = async () => {
        setLoading(true);
        setError(null);
        
        try {
            const response = await fetch('http://localhost:8080/api/photomath/solve', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({
                    equation: equation,
                    variable: 'x',
                    method: 'UC'
                })
            });
            
            const result = await response.json();
            
            if (result.status === 'error') {
                setError(result.message);
                return;
            }
            
            setData(result);
        } catch (err) {
            setError(err.message);
        } finally {
            setLoading(false);
        }
    };

    return (
        <div style={{ maxWidth: '900px', margin: '0 auto', padding: '20px' }}>
            <h1>🧮 Photomath Solver</h1>
            
            <div style={{ marginBottom: '20px' }}>
                <input
                    value={equation}
                    onChange={(e) => setEquation(e.target.value)}
                    placeholder="y'' - 5*y' + 6*y = 0"
                    style={{
                        width: '100%',
                        padding: '10px',
                        marginBottom: '10px',
                        border: '1px solid #ddd',
                        borderRadius: '4px'
                    }}
                />
                <button
                    onClick={handleSolve}
                    disabled={loading}
                    style={{
                        background: '#007bff',
                        color: 'white',
                        padding: '10px 20px',
                        border: 'none',
                        borderRadius: '4px',
                        cursor: loading ? 'not-allowed' : 'pointer'
                    }}
                >
                    {loading ? 'Resolviendo...' : 'Resolver'}
                </button>
            </div>

            {error && (
                <div style={{
                    background: '#f8d7da',
                    color: '#721c24',
                    padding: '12px',
                    borderRadius: '4px',
                    marginBottom: '20px'
                }}>
                    ❌ {error}
                </div>
            )}

            {data && (
                <>
                    {/* Pasos */}
                    {data.steps.map((step) => (
                        <div
                            key={step.order}
                            style={{
                                background: 'white',
                                margin: '10px 0',
                                padding: '15px',
                                borderLeft: '4px solid #007bff',
                                borderRadius: '4px'
                            }}
                        >
                            <h4>{step.order}. {step.title}</h4>
                            <p>{step.explanation}</p>
                            <ul>
                                {step.expressions.map((expr, i) => (
                                    <li key={i}><code>{expr}</code></li>
                                ))}
                            </ul>
                        </div>
                    ))}

                    {/* Solución */}
                    <div style={{
                        background: '#d4edda',
                        padding: '15px',
                        borderRadius: '4px',
                        marginTop: '20px'
                    }}>
                        <h2>✅ Solución Final</h2>
                        <p style={{ fontSize: '18px', fontWeight: 'bold' }}>
                            {data.finalSolution}
                        </p>
                        <p style={{ fontStyle: 'italic', color: '#666' }}>
                            {data.solutionLatex}
                        </p>
                        <p style={{ fontSize: '12px', color: '#999' }}>
                            ⏱️ Tiempo: {data.executionTimeMs}ms
                        </p>
                    </div>
                </>
            )}
        </div>
    );
}

export default PhotomathSolver;
```

### Vue 3 Component

```vue
<template>
    <div class="container">
        <h1>🧮 Photomath Solver</h1>
        
        <div class="input-section">
            <input
                v-model="equation"
                placeholder="y'' - 5*y' + 6*y = 0"
            />
            <button @click="solve" :disabled="loading">
                {{ loading ? 'Resolviendo...' : 'Resolver' }}
            </button>
        </div>

        <div v-if="error" class="error">
            ❌ {{ error }}
        </div>

        <div v-if="data" class="result">
            <!-- Pasos -->
            <div v-for="step in data.steps" :key="step.order" class="step">
                <h4>{{ step.order }}. {{ step.title }}</h4>
                <p>{{ step.explanation }}</p>
                <ul>
                    <li v-for="(expr, i) in step.expressions" :key="i">
                        <code>{{ expr }}</code>
                    </li>
                </ul>
            </div>

            <!-- Solución -->
            <div class="solution">
                <h2>✅ Solución Final</h2>
                <p class="final">{{ data.finalSolution }}</p>
                <p class="latex">{{ data.solutionLatex }}</p>
                <p class="time">⏱️ Tiempo: {{ data.executionTimeMs }}ms</p>
            </div>
        </div>
    </div>
</template>

<script>
export default {
    data() {
        return {
            equation: "y'' - 5*y' + 6*y = 0",
            data: null,
            loading: false,
            error: null
        };
    },
    methods: {
        async solve() {
            this.loading = true;
            this.error = null;
            
            try {
                const response = await fetch('http://localhost:8080/api/photomath/solve', {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify({
                        equation: this.equation,
                        variable: 'x',
                        method: 'UC'
                    })
                });
                
                const result = await response.json();
                
                if (result.status === 'error') {
                    this.error = result.message;
                    return;
                }
                
                this.data = result;
            } catch (err) {
                this.error = err.message;
            } finally {
                this.loading = false;
            }
        }
    }
};
</script>

<style scoped>
.container {
    max-width: 900px;
    margin: 0 auto;
    padding: 20px;
    font-family: Arial, sans-serif;
}

.input-section {
    display: flex;
    gap: 10px;
    margin-bottom: 20px;
}

input {
    flex: 1;
    padding: 10px;
    border: 1px solid #ddd;
    border-radius: 4px;
}

button {
    background: #007bff;
    color: white;
    padding: 10px 20px;
    border: none;
    border-radius: 4px;
    cursor: pointer;
}

button:disabled {
    background: #ccc;
    cursor: not-allowed;
}

.error {
    background: #f8d7da;
    color: #721c24;
    padding: 12px;
    border-radius: 4px;
    margin-bottom: 20px;
}

.step {
    background: white;
    margin: 10px 0;
    padding: 15px;
    border-left: 4px solid #007bff;
    border-radius: 4px;
}

.solution {
    background: #d4edda;
    padding: 15px;
    border-radius: 4px;
    margin-top: 20px;
}

.final {
    font-size: 18px;
    font-weight: bold;
}

.latex {
    font-style: italic;
    color: #666;
}

.time {
    font-size: 12px;
    color: #999;
}
</style>
```

---

## 🔌 Curl Command (Para Probar)

```bash
curl -X POST http://localhost:8080/api/photomath/solve \
  -H "Content-Type: application/json" \
  -d '{
    "equation": "y'\'' - 5*y'\'' + 6*y = 0",
    "variable": "x",
    "method": "UC"
  }'
```

---

## 📱 Postman

1. Crear nuevo request POST
2. URL: `http://localhost:8080/api/photomath/solve`
3. Headers: `Content-Type: application/json`
4. Body (raw JSON):

```json
{
  "equation": "y'' - 5*y' + 6*y = 0",
  "variable": "x",
  "method": "UC"
}
```

5. Send ▶️

---

## 🎯 Ejemplos Rápidos

### Ecuación 1: Simple

```json
{
  "equation": "y' + 2*y = 0"
}
```

### Ecuación 2: Orden 2

```json
{
  "equation": "y'' - y = 0"
}
```

### Ecuación 3: Con Condiciones

```json
{
  "equation": "y'' + y = 0",
  "initialConditions": ["y(0)=1", "y'(0)=0"]
}
```

### Ecuación 4: No-Homogénea

```json
{
  "equation": "y'' - y = 2*x",
  "method": "UC"
}
```

---

## ✅ Checklist Final

- [ ] Descargué el JAR
- [ ] Ejecuté `java -jar geogera-0.1.jar`
- [ ] El servidor está en `http://localhost:8080`
- [ ] Copié el código HTML/React/Vue
- [ ] Cambié la URL a `http://localhost:8080/api/photomath/solve`
- [ ] Probé una ecuación
- [ ] Recibi la respuesta con steps
- [ ] Rendericé los pasos en mi UI
- [ ] ¡Funcionó! 🎉

---

**¡Eso es todo! Ready to go! 🚀**
