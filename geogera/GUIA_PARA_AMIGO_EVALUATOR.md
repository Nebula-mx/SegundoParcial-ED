# 🎯 PARA TU AMIGO: Cómo Usar EquationEvaluator

## Lo que tu amigo necesita saber

Tu amigo **NO necesita API, NO necesita servlets, NO necesita nada complicado**. Solo:

```java
// 1. Importar
import com.ecuaciones.diferenciales.evaluator.EquationEvaluator;
import com.ecuaciones.diferenciales.dto.DifferentialEquationResponse;

// 2. Crear el evaluador
DifferentialEquationResponse response = EquationEvaluator.evaluate("y'' - 5*y' + 6*y = 0");

// 3. Acceder a los datos
System.out.println(response.getFinalSolution());
```

**¡Eso es todo!**

---

## 📋 Ejemplo 1: Lo más simple

```java
// Resolver una ecuación homogénea
DifferentialEquationResponse response = EquationEvaluator.evaluate("y'' - 5*y' + 6*y = 0");

if (response.isSuccess()) {
    System.out.println("✅ " + response.getFinalSolution());
    // Output: y(x) = C1 * e^(3x) + C2 * e^(2x)
} else {
    System.out.println("❌ " + response.getMessage());
}
```

---

## 📋 Ejemplo 2: Con método especificado

```java
// Resolver con Coeficientes Indeterminados
DifferentialEquationResponse response = EquationEvaluator.evaluate(
    "y'' + 4*y = sin(2*x)",
    "UC"
);

System.out.println("Solución: " + response.getFinalSolution());
System.out.println("Método usado: " + response.getParticularMethod());
```

---

## 📋 Ejemplo 3: Con condiciones iniciales

```java
import java.util.Arrays;

DifferentialEquationResponse response = EquationEvaluator.evaluate(
    "y'' - 5*y' + 6*y = 0",
    "AUTO",
    Arrays.asList("y(0)=1", "y'(0)=2")
);

System.out.println("Solución con CI: " + response.getFinalSolution());
```

---

## 📊 ¿Qué retorna?

El objeto `DifferentialEquationResponse` tiene estos campos:

```java
response.getStatus()              // "SUCCESS" o "ERROR"
response.isSuccess()              // true si fue exitoso
response.getMessage()             // Mensaje descriptivo

response.getEquation()            // La ecuación original
response.getOrder()               // Orden de la EDO
response.isHomogeneous()          // true si es homogénea
response.getCoefficients()        // Los coeficientes

response.getFinalSolution()       // ← LA SOLUCIÓN (esto es lo que quieres)
response.getFinalSolutionLatex()  // Para mostrar en LaTeX

response.getHomogeneousSolution() // Solución homogénea
response.getParticulatSolution()  // Solución particular

response.getRoots()               // Las raíces encontradas
response.getResolutionSteps()     // Pasos de resolución
response.getParticularMethod()    // Método usado para particular (UC/VP)
```

---

## 🎯 Caso de Uso Real: Para tu Frontend

### Opción 1: Desde Java (más simple)

```java
public class MiApp {
    public static void main(String[] args) {
        // El evaluador
        DifferentialEquationResponse response = EquationEvaluator.evaluate(
            "y'' + 4*y = 8*cos(2*x)",
            "UC"
        );
        
        // Acceder a los datos
        if (response.isSuccess()) {
            String solution = response.getFinalSolution();
            String latex = response.getFinalSolutionLatex();
            String method = response.getParticularMethod();
            
            System.out.println("Solución: " + solution);
            System.out.println("LaTeX: " + latex);
            System.out.println("Método: " + method);
            
            // Si quieres los pasos
            for (String step : response.getResolutionSteps()) {
                System.out.println("  • " + step);
            }
        }
    }
}
```

### Opción 2: Desde una API REST (si tu amigo quiere)

Si tu amigo quiere exponer esto por HTTP, puede hacer:

```java
@RestController
@RequestMapping("/api/solve")
public class SolveController {
    
    @PostMapping
    public DifferentialEquationResponse solve(@RequestBody SolveRequest request) {
        // Tan simple como esto
        return EquationEvaluator.evaluate(
            request.getEquation(),
            request.getMethod(),
            request.getInitialConditions()
        );
    }
}
```

Luego desde el frontend:

```javascript
fetch('http://localhost:8080/api/solve', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({
        equation: "y'' - 5*y' + 6*y = 0",
        method: "AUTO",
        initialConditions: ["y(0)=1"]
    })
})
.then(r => r.json())
.then(data => {
    console.log(data.finalSolution);
    console.log(data.finalSolutionLatex);
    data.resolutionSteps.forEach(step => console.log("  • " + step));
});
```

---

## ✅ Todos los métodos disponibles

### Opciones simples

```java
// Solo la ecuación
DifferentialEquationResponse response = EquationEvaluator.evaluate("y'' + y = 0");

// Con método
response = EquationEvaluator.evaluate("y'' + 4*y = sin(2*x)", "UC");

// Con todo
response = EquationEvaluator.evaluate(
    "y'' + y = 0",
    "AUTO",
    Arrays.asList("y(0)=1", "y'(0)=2")
);
```

### Métodos de resolución disponibles

- `"AUTO"` → Intenta UC primero, fallback a VP (RECOMENDADO)
- `"UC"` → Coeficientes Indeterminados (más rápido)
- `"VP"` → Variación de Parámetros (más general)

---

## 🔍 Acceder a los datos del resultado

```java
DifferentialEquationResponse response = EquationEvaluator.evaluate("y'' - 5*y' + 6*y = 0");

// Status
System.out.println(response.getStatus());        // "SUCCESS"
System.out.println(response.getCode());          // 200

// Información de la ecuación
System.out.println(response.getEquation());      // "y'' - 5*y' + 6*y = 0"
System.out.println(response.getOrder());         // 2
System.out.println(response.isHomogeneous());    // true

// Raíces
for (DifferentialEquationResponse.RootInfo root : response.getRoots()) {
    System.out.println("r" + root.index + " = " + root.display);
    // r1 = 2.0
    // r2 = 3.0
}

// Soluciones
System.out.println(response.getHomogeneousSolution());      // "C1 * e^(3x) + C2 * e^(2x)"
System.out.println(response.getFinalSolution());            // "y(x) = C1 * e^(3x) + C2 * e^(2x)"
System.out.println(response.getFinalSolutionLatex());        // Para LaTeX

// Pasos
List<String> steps = response.getResolutionSteps();
for (String step : steps) {
    System.out.println("  • " + step);
}

// Si tiene solución particular
System.out.println(response.getParticularMethod());         // "UC" o "VP"
System.out.println(response.getParticulatSolution());       // La particular
```

---

## 💡 Formato de Ecuaciones Soportadas

```
y' + 2*y = 4                    // Primer orden
y'' - 5*y' + 6*y = 0           // Segundo orden homogénea
y'' + 4*y = 8*x^2              // Polinomio
y'' + y = e^x                  // Exponencial
y'' + 4*y = 2*sin(x)           // Trigonométrica
y'' + y = e^x*cos(x)           // Mixta
y''' - y' = x^2                // Orden superior
```

---

## ❌ Manejo de errores

```java
DifferentialEquationResponse response = EquationEvaluator.evaluate("2*x + 3 = 5");

if (!response.isSuccess()) {
    System.out.println("Status: " + response.getStatus());    // "ERROR"
    System.out.println("Código: " + response.getCode());      // 400 o 500
    System.out.println("Mensaje: " + response.getMessage()); // Descripción del error
}
```

---

## 🚀 Copy-Paste Listo (para tu amigo)

```java
import java.util.Arrays;
import com.ecuaciones.diferenciales.evaluator.EquationEvaluator;
import com.ecuaciones.diferenciales.dto.DifferentialEquationResponse;

public class MiApp {
    public static void main(String[] args) {
        // Crear el evaluador y resolver
        DifferentialEquationResponse response = EquationEvaluator.evaluate(
            "y'' - 5*y' + 6*y = 0",      // La ecuación
            "AUTO",                        // Método
            Arrays.asList("y(0)=1")        // Condiciones iniciales
        );
        
        // Verificar éxito
        if (response.isSuccess()) {
            // Mostrar resultado
            System.out.println("✅ Ecuación resuelta:");
            System.out.println("   Ecuación: " + response.getEquation());
            System.out.println("   Solución: " + response.getFinalSolution());
            System.out.println("   LaTeX: " + response.getFinalSolutionLatex());
            
            // Mostrar pasos
            System.out.println("\n   Pasos de resolución:");
            for (String step : response.getResolutionSteps()) {
                System.out.println("     • " + step);
            }
        } else {
            // Mostrar error
            System.out.println("❌ Error: " + response.getMessage());
        }
    }
}
```

---

## 📝 Resumen para tu amigo

**Lo IMPORTANTE:**
1. Tu amigo NO necesita crear una API
2. Tu amigo solo llama a `EquationEvaluator.evaluate()`
3. Recibe un objeto `DifferentialEquationResponse` con todos los datos
4. Accede a `getFinalSolution()` para la respuesta
5. Accede a `getResolutionSteps()` para mostrar los pasos como Photomath

**Sin complicaciones. Simple. Directo.**

```java
// Esto es TODO lo que necesita:
DifferentialEquationResponse response = EquationEvaluator.evaluate("y'' - 5*y' + 6*y = 0");
System.out.println(response.getFinalSolution());
```

---

**¡Listo para que tu amigo lo use!** 🚀
