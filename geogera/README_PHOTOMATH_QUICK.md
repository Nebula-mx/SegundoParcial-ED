# 🚀 QUICK START: Para tu Amigo del Frontend

## ⚡ TL;DR (Lo Más Importante)

Tu backend tiene un endpoint que devuelve ecuaciones diferenciales **resueltas paso a paso** como Photomath.

---

## 📌 Endpoint

```
POST http://localhost:8080/api/photomath/solve
```

---

## 💾 Guardar el JAR

En tu proyecto web, guarda el JAR:

```bash
# Copiar el JAR a tu proyecto frontend
cp /ruta/a/geogera/target/*.jar ./backend/geogera.jar

# O usarlo directamente desde el proyecto backend
java -jar geogera.jar
```

---

## 🎯 Cómo Usarlo (3 Pasos)

### Paso 1: Enviar Ecuación

```javascript
const equation = "y'' - 5*y' + 6*y = 0";

const response = await fetch('http://localhost:8080/api/photomath/solve', {
  method: 'POST',
  headers: { 'Content-Type': 'application/json' },
  body: JSON.stringify({
    equation: equation,
    variable: 'x'
  })
});

const data = await response.json();
```

### Paso 2: Procesar Pasos

```javascript
data.steps.forEach(step => {
  console.log(`${step.order}. ${step.title}`);
  console.log(`   → ${step.explanation}`);
  step.expressions.forEach(expr => {
    console.log(`      ${expr}`);
  });
});
```

### Paso 3: Mostrar Solución

```javascript
console.log("SOLUCIÓN: " + data.finalSolution);
```

---

## 📦 Body del Request

```json
{
  "equation": "y'' - 5*y' + 6*y = 0",
  "variable": "x",
  "method": "UC",
  "initialConditions": ["y(0)=1", "y'(0)=2"]
}
```

**Campos:**
- `equation` ✅ Requerido
- `variable` ⭕ Optional (default: "x")
- `method` ⭕ Optional (default: "UC")
- `initialConditions` ⭕ Optional (default: empty)

---

## 📨 Response

```json
{
  "status": "success",
  "steps": [
    {
      "type": "CLASSIFY",
      "title": "📖 Parsing de la ecuación",
      "order": 1,
      "explanation": "...",
      "expressions": ["..."],
      "details": { "key": "value" }
    },
    // ... más steps
  ],
  "finalSolution": "y(x) = C1*e^(2x) + C2*e^(3x)",
  "solutionLatex": "$y(x) = C_1 \\cdot e^{2x} + C_2 \\cdot e^{3x}$",
  "metadata": {
    "Tipo": "Homogénea",
    "Pasos totales": "6"
  },
  "executionTimeMs": 45
}
```

---

## 🎨 Tipos de Steps

| Type | Significado |
|------|------------|
| `CLASSIFY` | Clasificación de la EDO |
| `CHARACTERISTIC` | Ecuación característica |
| `FIND_ROOTS` | Encontrar raíces |
| `HOMOGENEOUS_SOLUTION` | Solución homogénea |
| `PARTICULAR_SOLUTION` | Solución particular |
| `GENERAL_SOLUTION` | Solución general |
| `APPLY_CONDITIONS` | Aplicar condiciones iniciales |
| `FINAL_SOLUTION` | Solución final |

---

## 💡 Ejemplos Rápidos

### Ejemplo 1: Orden 1
```json
{
  "equation": "y' + 2*y = 0",
  "variable": "x"
}
```

### Ejemplo 2: Con Condiciones Iniciales
```json
{
  "equation": "y'' + y = 0",
  "initialConditions": ["y(0)=1", "y'(0)=0"]
}
```

### Ejemplo 3: No-Homogénea
```json
{
  "equation": "y'' - y = 2*x",
  "method": "UC"
}
```

### Ejemplo 4: Orden 3
```json
{
  "equation": "y''' - y' = 0"
}
```

---

## 🎬 Renderizar Pasos (Ejemplo Simple)

```html
<!-- HTML -->
<div id="solver">
  <input id="equation" value="y'' - 5*y' + 6*y = 0" />
  <button onclick="solve()">Resolver</button>
  <div id="steps"></div>
  <div id="solution"></div>
</div>

<script>
async function solve() {
  const equation = document.getElementById('equation').value;
  const response = await fetch('http://localhost:8080/api/photomath/solve', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ equation, variable: 'x' })
  });
  
  const data = await response.json();
  
  // Mostrar pasos
  let html = '';
  data.steps.forEach(step => {
    html += `
      <div class="step">
        <h4>${step.order}. ${step.title}</h4>
        <p>${step.explanation}</p>
        <ul>
          ${step.expressions.map(e => `<li><code>${e}</code></li>`).join('')}
        </ul>
      </div>
    `;
  });
  
  document.getElementById('steps').innerHTML = html;
  document.getElementById('solution').innerHTML = 
    `<strong>Solución:</strong> ${data.finalSolution}`;
}
</script>
```

---

## ⚠️ Errores Comunes

| Error | Causa | Solución |
|-------|-------|----------|
| `Connection refused` | Backend no está corriendo | `java -jar geogera.jar` |
| `CORS error` | Dominio no permitido | Ya está configurado en el backend |
| `Empty equation` | Campo vacío | Llenar el campo `equation` |
| Status `error` | Ecuación inválida | Ver el campo `error` en response |

---

## 🔗 Links Útiles

- **Guía Completa**: `GUIA_PHOTOMATH_PARA_FRONTEND.md`
- **Ejemplo Respuesta**: `EJEMPLO_RESPUESTA_PHOTOMATH.md`
- **Documentación Backend**: `PROYECTO_COMPLETADO.md`

---

## ✅ Checklist

- [ ] Copié el JAR a mi proyecto
- [ ] Ejecuté `java -jar geogera.jar`
- [ ] El servidor está en `http://localhost:8080`
- [ ] Hice un POST a `/api/photomath/solve`
- [ ] Recibí respuesta con `steps` y `finalSolution`
- [ ] Rendericé los pasos en mi frontend
- [ ] Mostré la solución final
- [ ] 🎉 ¡Funcionó!

---

## 🎯 Próximos Pasos

1. **Copiar el endpoint** a tu frontend
2. **Diseñar la interfaz** para mostrar los pasos
3. **Agregar animaciones** para que sea más visual
4. **Renderizar LaTeX** si quieres matemáticas bonitas (usa MathJax/KaTeX)
5. **Personalizar colores/temas** según tu diseño

---

## 🚀 ¡Ya Está!

Con esto tienes todo lo necesario para integrar Photomath en tu frontend.

**¿Preguntas? Mira los otros archivos MD en la carpeta.**

¡Que lo disfrutes! 🎉
