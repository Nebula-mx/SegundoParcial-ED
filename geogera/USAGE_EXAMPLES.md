# 📚 Ejemplos de Uso - GEOGERA con Solvers Reales

## Tabla de Contenidos
1. [Uso desde REST API](#rest-api)
2. [Uso desde Java](#java)
3. [Casos de Uso Completos](#casos-completos)
4. [Troubleshooting](#troubleshooting)

---

## REST API

### Endpoint Principal
```
POST http://localhost:5555/api/ode/solve
Content-Type: application/json
```

### Request Format
```json
{
  "equation": "y'' + 3*y' + 2*y = 0",
  "initialConditions": ["y(0) = 1", "y'(0) = 0"],
  "variable": "x"
}
```

### Response Format
```json
{
  "status": "success|error|partial|unsupported",
  "message": "Descripción del resultado",
  "expression": "Ecuación original",
  "steps": [
    {
      "type": "CLASSIFY|CHARACTERISTIC|HOMOGENEOUS_SOLUTION|PARTICULAR_SOLUTION|GENERAL_SOLUTION",
      "title": "Nombre del paso",
      "expressions": ["expr1", "expr2"],
      "explanation": "Explicación del paso",
      "order": 1
    }
  ],
  "finalSolution": "C1 * e^(-x) + C2 * e^(-2x)",
  "solutionLatex": "$C1  \\cdot  e^(-x) + C2  \\cdot  e^(-2x)$",
  "executionTimeMs": 2
}
```

---

## Ejemplos con cURL

### Ejemplo 1: Ecuación Simple de Primer Orden

```bash
curl -X POST http://localhost:5555/api/ode/solve \
  -H "Content-Type: application/json" \
  -d '{
    "equation": "y'\'' + 2*y = 0"
  }'
```

**Respuesta**:
```json
{
  "status": "success",
  "expression": "y' + 2*y = 0",
  "steps": [
    {
      "type": "CLASSIFY",
      "title": "Clasificación de la EDO",
      "expressions": ["EDO de orden 1, Homogénea"]
    },
    {
      "type": "CHARACTERISTIC",
      "title": "Cálculo de raíces",
      "expressions": ["r = -2.0000"]
    },
    {
      "type": "HOMOGENEOUS_SOLUTION",
      "title": "Construir la solución homogénea",
      "expressions": ["y_h(x) = C1 * e^(-2x)"]
    }
  ],
  "finalSolution": "C1 * e^(-2x)",
  "solutionLatex": "$C1 \\cdot e^{-2x}$"
}
```

---

### Ejemplo 2: Ecuación de Segundo Orden con Raíces Reales

```bash
curl -X POST http://localhost:5555/api/ode/solve \
  -H "Content-Type: application/json" \
  -d '{
    "equation": "y'\'\' + 5*y'\'' + 6*y = 0"
  }'
```

**Solución esperada**:
- Ecuación característica: r² + 5r + 6 = 0
- Factorización: (r+2)(r+3) = 0
- Raíces: r₁ = -2, r₂ = -3
- **Solución**: y(x) = C₁e⁻²ˣ + C₂e⁻³ˣ

---

### Ejemplo 3: Ecuación con Raíces Complejas

```bash
curl -X POST http://localhost:5555/api/ode/solve \
  -H "Content-Type: application/json" \
  -d '{
    "equation": "y'\'\' + 4*y = 0"
  }'
```

**Análisis**:
- Ecuación característica: r² + 4 = 0
- Raíces: r = ±2i
- **Solución**: y(x) = C₁cos(2x) + C₂sin(2x)

**Respuesta JSON**:
```json
{
  "steps": [
    {
      "type": "CHARACTERISTIC",
      "expressions": ["r = 0.0000 ± 2.0000i"]
    },
    {
      "type": "HOMOGENEOUS_SOLUTION",
      "expressions": ["y_h(x) = (C1 * cos(2x) + C2 * sin(2x))"]
    }
  ],
  "finalSolution": "(C1 * cos(2x) + C2 * sin(2x))",
  "solutionLatex": "$(C_1 \\cos(2x) + C_2 \\sin(2x))$"
}
```

---

### Ejemplo 4: Ecuación con Raíces Repetidas

```bash
curl -X POST http://localhost:5555/api/ode/solve \
  -H "Content-Type: application/json" \
  -d '{
    "equation": "y'\'\' - 4*y'\'' + 4*y = 0"
  }'
```

**Análisis**:
- Ecuación característica: r² - 4r + 4 = 0
- Factorización: (r-2)² = 0
- Raíz repetida: r = 2 (multiplicidad 2)
- **Solución**: y(x) = (C₁ + C₂x)e²ˣ

---

### Ejemplo 5: Ecuación No-Homogénea

```bash
curl -X POST http://localhost:5555/api/ode/solve \
  -H "Content-Type: application/json" \
  -d '{
    "equation": "y'\'' - 3*y = e^(2x)"
  }'
```

**Análisis**:
- Lado homogéneo: y' - 3y = 0 → r = 3
- Solución homogénea: y_h = C₁e³ˣ
- Lado no-homogéneo: f(x) = e²ˣ
- **Solución completa**: y(x) = C₁e³ˣ + [particular]

---

### Ejemplo 6: Ecuación con Condiciones Iniciales

```bash
curl -X POST http://localhost:5555/api/ode/solve \
  -H "Content-Type: application/json" \
  -d '{
    "equation": "y'\'\' + y = 0",
    "initialConditions": ["y(0) = 1", "y'\''(0) = 0"]
  }'
```

**Análisis**:
1. Solución general: y(x) = C₁cos(x) + C₂sin(x)
2. Aplicar CI: y(0) = 1 → C₁ = 1
3. Aplicar CI: y'(0) = 0 → -C₁sin(0) + C₂cos(0) = 0 → C₂ = 0
4. **Solución particular**: y(x) = cos(x)

---

## Java

### Uso Directo desde Servlet

```java
import com.ecuaciones.diferenciales.api.service.ODESolver;
import com.ecuaciones.diferenciales.api.dto.ExpressionData;
import com.ecuaciones.diferenciales.api.dto.SolutionResponse;

// 1. Crear datos de entrada
ExpressionData data = new ExpressionData();
data.setEquation("y'' + 3*y' + 2*y = 0");
data.setVariable("x");

// 2. Crear solver
ODESolver solver = new ODESolver();

// 3. Resolver
SolutionResponse response = solver.solve(data);

// 4. Usar resultados
if (response.getStatus() == Status.SUCCESS) {
    String solution = response.getFinalSolution();
    System.out.println("Solución: " + solution);
    
    // Acceder a pasos
    List<Step> steps = response.getSteps();
    for (Step step : steps) {
        System.out.println(step.getTitle() + ": " + step.getExpressions());
    }
}
```

---

### Uso desde Spring Controller

```java
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ode")
public class ODEController {
    
    @PostMapping("/solve")
    public ResponseEntity<SolutionResponse> solve(@RequestBody ExpressionData equation) {
        // Validación
        String error = equation.getValidationError();
        if (error != null) {
            return ResponseEntity.badRequest().body(
                SolutionResponse.error(equation.getEquation(), error)
            );
        }
        
        // Resolver
        ODESolver solver = new ODESolver();
        SolutionResponse response = solver.solve(equation);
        
        return ResponseEntity.ok(response);
    }
}
```

---

### Uso desde Servlet (Para Isma)

```java
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.google.gson.Gson;

@WebServlet("/solveODE")
public class ODEServlet extends HttpServlet {
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        // Leer JSON
        BufferedReader reader = request.getReader();
        Gson gson = new Gson();
        ExpressionData data = gson.fromJson(reader, ExpressionData.class);
        
        // Validar
        String validationError = data.getValidationError();
        if (validationError != null) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write(
                gson.toJson(SolutionResponse.error(data.getEquation(), validationError))
            );
            return;
        }
        
        // Resolver
        ODESolver solver = new ODESolver();
        SolutionResponse result = solver.solve(data);
        
        // Responder
        response.setContentType("application/json");
        response.getWriter().write(gson.toJson(result));
    }
}
```

---

## Casos Completos

### Caso 1: Circuito RLC (Ingeniería Eléctrica)

**Problema**: Un circuito RLC tiene R=4Ω, L=1H, C=0.25F, sin fuente.

**EDO**: L(d²q/dt²) + R(dq/dt) + q/C = 0  
→ d²q/dt² + 4(dq/dt) + 4q = 0

```bash
curl -X POST http://localhost:5555/api/ode/solve \
  -H "Content-Type: application/json" \
  -d '{
    "equation": "q'\'\' + 4*q'\'' + 4*q = 0",
    "initialConditions": ["q(0) = 1", "q'\''(0) = 0"],
    "variable": "t"
  }'
```

**Respuesta**:
```json
{
  "finalSolution": "q(t) = (C1 + C2*t) * e^(-2t)",
  "steps": [
    {"type": "CHARACTERISTIC", "expressions": ["r = -2 (multiplicidad 2)"]},
    {"type": "HOMOGENEOUS_SOLUTION", "expressions": ["q_h = (C1 + C2*t)*e^(-2t)"]}
  ]
}
```

Con CI: q(t) = (1 + t)e⁻²ᵗ (descarga crítica amortiguada)

---

### Caso 2: Sistema Masa-Resorte (Física Clásica)

**Problema**: Masa m=1kg en resorte k=1N/m, sin amortiguamiento.

**EDO**: m(d²x/dt²) + k*x = 0  
→ d²x/dt² + x = 0

```bash
curl -X POST http://localhost:5555/api/ode/solve \
  -H "Content-Type: application/json" \
  -d '{
    "equation": "x'\'\' + x = 0",
    "initialConditions": ["x(0) = 1", "x'\''(0) = 0"],
    "variable": "t"
  }'
```

**Respuesta**:
```json
{
  "finalSolution": "x(t) = cos(t)",
  "steps": [
    {"type": "CHARACTERISTIC", "expressions": ["r = ±i"]},
    {"type": "HOMOGENEOUS_SOLUTION", "expressions": ["x_h = C1*cos(t) + C2*sin(t)"]}
  ]
}
```

Solución: x(t) = cos(t) (movimiento armónico simple)

---

### Caso 3: Ecuación de Calor (Termodinámica)

**Problema**: Conducción de calor en barra unidimensional.

**EDO**: ∂u/∂t = α²∂²u/∂x² (separación de variables)  
→ d²X/dx² + λX = 0

```bash
curl -X POST http://localhost:5555/api/ode/solve \
  -H "Content-Type: application/json" \
  -d '{
    "equation": "X'\'\' + 9*X = 0",
    "variable": "x"
  }'
```

**Respuesta**:
```json
{
  "finalSolution": "X(x) = C1*cos(3x) + C2*sin(3x)",
  "steps": [
    {"type": "CHARACTERISTIC", "expressions": ["r = ±3i"]}
  ]
}
```

---

### Caso 4: Decaimiento Radiactivo

**Problema**: Material radiactivo con vida media T₁/₂ = 10 años

**EDO**: dN/dt = -λN (donde λ = ln(2)/T₁/₂)

```bash
curl -X POST http://localhost:5555/api/ode/solve \
  -H "Content-Type: application/json" \
  -d '{
    "equation": "N' + 0.0693*N = 0",
    "initialConditions": ["N(0) = 100"],
    "variable": "t"
  }'
```

**Respuesta**:
```json
{
  "finalSolution": "N(t) = 100 * e^(-0.0693*t)",
  "steps": [
    {"type": "CHARACTERISTIC", "expressions": ["r = -0.0693"]}
  ]
}
```

---

## Troubleshooting

### Error 400: Validación Fallida

**Causa**: Ecuación inválida

**Soluciones**:
```bash
# ❌ Malo - Variable no es 'y'
{"equation": "z'' + z = 0"}

# ✅ Correcto
{"equation": "y'' + y = 0"}

# ❌ Malo - No contiene 'y'
{"equation": "x^2 = 0"}

# ✅ Correcto
{"equation": "y'' + y = 0"}

# ❌ Malo - Vacío
{"equation": ""}

# ✅ Correcto
{"equation": "y' + y = 0"}
```

---

### Error 500: Error Interno

**Verificar**:
1. ¿El servidor está corriendo?
2. ¿Puerto 5555 disponible?
3. ¿Coeficientes numéricos válidos?

```bash
# Reiniciar servidor
./start_server.sh

# O manualmente
java -jar target/geogera-0.1.jar --server.port=5555
```

---

### Respuesta Status "unsupported"

**Significa**: Tipo de ecuación no soportado aún

**Tipos soportados**:
- ✅ Homogéneas de cualquier orden
- ✅ No-homogéneas (parcialmente)
- ❌ Ecuaciones con coeficientes variables
- ❌ Ecuaciones diferenciales parciales (PDE)

**Solución**: Simplificar ecuación o usar método manual

---

## Endpoints Adicionales

### Health Check
```bash
GET http://localhost:5555/api/health

Respuesta:
{
  "status": "UP",
  "timestamp": "2025-11-14T20:08:00Z"
}
```

### Ejemplos Disponibles
```bash
GET http://localhost:5555/api/ode/examples

Respuesta:
{
  "examples": [
    {"equation": "y' + y = 0"},
    {"equation": "y'' + 4*y = 0"},
    {"equation": "y'' + 3*y' + 2*y = 0"}
  ]
}
```

---

## Referencias

- **Zill, Wright**: Ecuaciones Diferenciales con Problemas de Valor en la Frontera (Capítulo 4)
- **Nagle**: Fundamentals of Differential Equations (Capítulo 4)
- **MIT OpenCourseWare**: 18.03 Differential Equations

