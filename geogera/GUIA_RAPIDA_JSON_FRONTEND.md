# 📋 GUÍA RÁPIDA: EquationSolverService para Frontend

## ¿Qué es?

`EquationSolverService` es una clase Java simple que **resuelve ecuaciones diferenciales y retorna JSON**. No necesita API, base de datos, ni nada complicado. Solo llamar a un método.

---

## 🚀 Quick Start (2 minutos)

### Opción 1: Obtener JSON String

```java
// Crear instancia del servicio
EquationSolverService solver = new EquationSolverService();

// Resolver una ecuación
String json = solver.solve("y'' - 5*y' + 6*y = 0");

// El json contiene toda la información
System.out.println(json);
```

### Opción 2: Obtener un Map (más flexible)

```java
EquationSolverService solver = new EquationSolverService();

// Retorna un Map que puedes acceder directamente
Map<String, Object> result = solver.solveAsMap("y'' - 5*y' + 6*y = 0");

// Acceder a los datos
String status = (String) result.get("status");  // "SUCCESS" o "ERROR"
String solution = (String) result.get("finalSolution");
```

---

## 📦 ¿Qué retorna el JSON?

### Estructura Completa

```json
{
  "status": "SUCCESS",
  "code": 200,
  "equation": {
    "original": "y'' - 5*y' + 6*y = 0",
    "order": 2,
    "coefficients": [1.0, -5.0, 6.0],
    "isHomogeneous": true
  },
  "roots": [
    {
      "index": 1,
      "real": 2.0,
      "imaginary": 0.0,
      "display": "2.0"
    },
    {
      "index": 2,
      "real": 3.0,
      "imaginary": 0.0,
      "display": "3.0"
    }
  ],
  "solution": {
    "homogeneous": "C1 * e^(3x) + C2 * e^(2x)",
    "homogeneousLatex": "C1 * e^{3x} + C2 * e^{2x}",
    "particular": {}
  },
  "finalSolution": "y(x) = C1 * e^(3x) + C2 * e^(2x)",
  "finalSolutionLatex": "y(x) = C1 * e^{3x} + C2 * e^{2x}"
}
```

### Campos Principales

| Campo | Tipo | Descripción |
|-------|------|-------------|
| `status` | String | `SUCCESS` si funcionó, `ERROR` si hay problema |
| `code` | Int | 200 (éxito), 400 (error de entrada), 500 (error interno) |
| `equation` | Object | Información de la ecuación ingresada |
| `roots` | Array | Raíces del polinomio característico |
| `solution` | Object | `homogeneous` y `particular` |
| `finalSolution` | String | La solución general final (ESTO ES LO QUE QUIERES) |
| `finalSolutionLatex` | String | La solución en formato LaTeX para mostrar bonito |

---

## 💡 Ejemplos de Uso

### Ejemplo 1: Ecuación Homogénea Simple

```java
EquationSolverService solver = new EquationSolverService();

String json = solver.solve("y'' - 5*y' + 6*y = 0");
// Retorna:
// {
//   "status": "SUCCESS",
//   "finalSolution": "y(x) = C1 * e^(3x) + C2 * e^(2x)",
//   ...
// }
```

### Ejemplo 2: Ecuación No-Homogénea (Auto-detectar método)

```java
String json = solver.solve("y'' + 4*y = sin(2*x)");
// Automáticamente detecta resonancia y usa método UC o VP
```

### Ejemplo 3: Especificar Método Específico

```java
// Usar Coeficientes Indeterminados
String json1 = solver.solve("y'' + y = 3*x^2", "UC");

// Usar Variación de Parámetros
String json2 = solver.solve("y'' + y = 1/(1 + x^2)", "VP");

// Auto (intenta UC primero, fallback a VP)
String json3 = solver.solve("y'' + 4*y = sin(2*x)", "AUTO");
```

### Ejemplo 4: Con Condiciones Iniciales

```java
List<String> ci = Arrays.asList("y(0)=1", "y'(0)=2");
String json = solver.solve("y'' - 5*y' + 6*y = 0", "AUTO", ci);
// Las CI aparecen en el JSON en el campo "initialConditions"
```

### Ejemplo 5: Obtener como Map (más flexible)

```java
Map<String, Object> result = solver.solveAsMap("y'' - 5*y' + 6*y = 0");

// Verificar si fue exitoso
if ("SUCCESS".equals(result.get("status"))) {
    String solution = (String) result.get("finalSolution");
    System.out.println("Solución: " + solution);
    
    // Acceder a cada parte
    Map<String, Object> equation = (Map<String, Object>) result.get("equation");
    System.out.println("Orden: " + equation.get("order"));
    System.out.println("Es homogénea: " + equation.get("isHomogeneous"));
} else {
    String error = (String) result.get("message");
    System.out.println("Error: " + error);
}
```

---

## 🛠️ Métodos Disponibles

### `solve(String ecuacion)`
**Retorna:** JSON string  
**Parámetros:**
- `ecuacion`: La ecuación diferencial (ej: `"y'' - 5*y' + 6*y = 0"`)

```java
String json = solver.solve("y'' + y = 0");
```

---

### `solve(String ecuacion, String metodo)`
**Retorna:** JSON string  
**Parámetros:**
- `ecuacion`: La ecuación diferencial
- `metodo`: `"UC"`, `"VP"` o `"AUTO"` (default: AUTO)

```java
String json = solver.solve("y'' + 4*y = sin(2*x)", "UC");
```

---

### `solve(String ecuacion, String metodo, List<String> condicionesIniciales)`
**Retorna:** JSON string  
**Parámetros:**
- `ecuacion`: La ecuación diferencial
- `metodo`: `"UC"`, `"VP"` o `"AUTO"`
- `condicionesIniciales`: Lista con CI (ej: `["y(0)=1", "y'(0)=2"]`)

```java
List<String> ci = Arrays.asList("y(0)=1", "y'(0)=2");
String json = solver.solve("y'' - 5*y' + 6*y = 0", "AUTO", ci);
```

---

### `solveAsMap(...)` - Variantes equivalentes
Mismo que `solve()` pero retornan `Map<String, Object>` en lugar de JSON string.

```java
Map<String, Object> result = solver.solveAsMap("y'' + y = 0");
Map<String, Object> result = solver.solveAsMap("y'' + 4*y = sin(2*x)", "UC");
Map<String, Object> result = solver.solveAsMap("y'' + y = 0", "AUTO", ci);
```

---

## ✅ Formatos Soportados

La clase soporta estos formatos de ecuación:

| Tipo | Ejemplos |
|------|----------|
| Primer orden | `y' + 2*y = 4` |
| Homogénea | `y'' - 5*y' + 6*y = 0` |
| Polinómio | `y'' + 4*y = 8*x^2` |
| Exponencial | `y'' + y = e^x` |
| Trigonométrica | `y'' + 4*y = sin(2*x)` |
| Mixta | `y'' + y = e^x*cos(x)` |
| Orden 3+ | `y''' - y' = x^2` |

---

## ❌ Manejo de Errores

```java
String json = solver.solve("2*x + 3 = 5");  // NO es ecuación diferencial
// Retorna:
// {
//   "status": "ERROR",
//   "code": 400,
//   "message": "No es una ecuación diferencial válida..."
// }
```

### Códigos de Error

| Code | Significado |
|------|------------|
| 200 | ✅ Éxito |
| 400 | ❌ Error en la entrada (ecuación inválida, vacía, etc.) |
| 500 | ❌ Error interno del servidor |

```java
Map<String, Object> result = solver.solveAsMap("invalid");

int code = (Integer) result.get("code");
if (code == 200) {
    // Procesar solución
} else {
    String error = (String) result.get("message");
    System.out.println("Error: " + error);
}
```

---

## 📝 Casos de Uso

### Para tu Frontend (ejemplo React/Vue)

```javascript
// En JavaScript, puedes llamar a un endpoint que use EquationSolverService
const response = await fetch('/api/solve', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({
        equation: "y'' - 5*y' + 6*y = 0",
        method: "AUTO",
        initialConditions: ["y(0)=1", "y'(0)=2"]
    })
});

const result = await response.json();
console.log(result.finalSolution);  // "y(x) = C1 * e^(3x) + C2 * e^(2x)"
console.log(result.finalSolutionLatex);  // Para mostrar en LaTeX
```

### Para tu Backend Java

```java
// En un servlet o Spring Controller
@PostMapping("/solve")
public Map<String, Object> solveEquation(@RequestBody SolveRequest request) {
    EquationSolverService solver = new EquationSolverService();
    return solver.solveAsMap(
        request.getEquation(),
        request.getMethod(),
        request.getInitialConditions()
    );
}
```

---

## 🎯 Lo Más Importante: Campos que Necesitas

Para tu frontend, probablemente necesites estos campos del JSON:

```json
{
  "status": "SUCCESS",           ← Verificar que fue exitoso
  "finalSolution": "...",        ← LA SOLUCIÓN (esto es lo que muestras)
  "finalSolutionLatex": "...",   ← Para mostrar con LaTeX bonito
  "solution": {
    "particular": {...}          ← Detalles de la solución particular
  },
  "roots": [...],                ← Las raíces del polinomio característico
  "message": "..."               ← Si hay error, aquí está el mensaje
}
```

---

## 🔍 Ejemplo Completo (copy-paste listo)

```java
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import com.ecuaciones.diferenciales.service.EquationSolverService;

public class MiApp {
    public static void main(String[] args) {
        // 1. Crear el solver
        EquationSolverService solver = new EquationSolverService();
        
        // 2. Resolver ecuación
        String ecuacion = "y'' - 5*y' + 6*y = 0";
        String metodo = "AUTO";
        List<String> ci = Arrays.asList("y(0)=1", "y'(0)=2");
        
        // 3. Obtener resultado
        Map<String, Object> result = solver.solveAsMap(ecuacion, metodo, ci);
        
        // 4. Verificar éxito
        if ("SUCCESS".equals(result.get("status"))) {
            // 5. Usar los datos
            String solucion = (String) result.get("finalSolution");
            String latex = (String) result.get("finalSolutionLatex");
            
            System.out.println("✅ Ecuación resuelta:");
            System.out.println("   Solución: " + solucion);
            System.out.println("   LaTeX: " + latex);
        } else {
            String error = (String) result.get("message");
            System.out.println("❌ Error: " + error);
        }
    }
}
```

---

## 📞 Soporte

Si tu amigo tiene dudas:
1. Ver `QuickStartExample.java` para más ejemplos
2. Revisar la documentación en `SUITE_TESTS_COMPLETADA.md`
3. Revisar documentación de ecuaciones en `EXHAUSTIVE_22_EQUATIONS_TEST_DOCUMENTATION.md`

---

**¡Eso es todo! Simple, directo, sin APIs complicadas.** 🚀
