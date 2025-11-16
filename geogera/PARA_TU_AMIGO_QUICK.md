# 🎯 RESUMIDO: Lo que Tu Amigo Necesita

## Tu amigo quiere una clase que resuelva ecuaciones diferenciales sin complicaciones

### ✅ Respuesta: Usa `EquationEvaluator`

```java
// Importar
import com.ecuaciones.diferenciales.evaluator.EquationEvaluator;
import com.ecuaciones.diferenciales.dto.DifferentialEquationResponse;

// Usar
DifferentialEquationResponse response = EquationEvaluator.evaluate("y'' - 5*y' + 6*y = 0");

// Acceder
System.out.println(response.getFinalSolution());
System.out.println(response.getFinalSolutionLatex());
System.out.println(response.getResolutionSteps());
```

---

## 🚀 Tres formas de usar (elige una)

### Opción 1: Simple (solo ecuación)
```java
DifferentialEquationResponse response = EquationEvaluator.evaluate("y'' - 5*y' + 6*y = 0");
```

### Opción 2: Con método
```java
DifferentialEquationResponse response = EquationEvaluator.evaluate(
    "y'' + 4*y = sin(2*x)",
    "UC"  // O "VP" o "AUTO"
);
```

### Opción 3: Con todo
```java
DifferentialEquationResponse response = EquationEvaluator.evaluate(
    "y'' + y = 0",
    "AUTO",
    Arrays.asList("y(0)=1", "y'(0)=2")
);
```

---

## 📊 ¿Qué retorna?

Un objeto `DifferentialEquationResponse` con:

```java
response.isSuccess()              // ✅ true o ❌ false
response.getFinalSolution()       // "y(x) = ..."  ← ESTO
response.getFinalSolutionLatex()  // Para mostrar bonito
response.getResolutionSteps()     // Array de pasos tipo Photomath
response.getMessage()             // Si hay error
```

---

## 💻 Ejemplo Completo (copy-paste)

```java
import java.util.Arrays;
import com.ecuaciones.diferenciales.evaluator.EquationEvaluator;
import com.ecuaciones.diferenciales.dto.DifferentialEquationResponse;

public class TestEvaluator {
    public static void main(String[] args) {
        // Resolver
        DifferentialEquationResponse response = EquationEvaluator.evaluate(
            "y'' - 5*y' + 6*y = 0",
            "AUTO"
        );
        
        // Mostrar resultado
        if (response.isSuccess()) {
            System.out.println("✅ " + response.getFinalSolution());
            for (String step : response.getResolutionSteps()) {
                System.out.println("  • " + step);
            }
        } else {
            System.out.println("❌ " + response.getMessage());
        }
    }
}
```

---

## 🎓 Ejemplo para Frontend (JavaScript)

**Si tu amigo quiere desde JavaScript:**

```javascript
// Opción 1: Desde un endpoint REST que tu amigo cree
fetch('/api/solve', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({
        equation: "y'' - 5*y' + 6*y = 0",
        method: "AUTO"
    })
})
.then(r => r.json())
.then(data => {
    console.log(data.finalSolution);
    console.log(data.finalSolutionLatex);
    data.resolutionSteps.forEach(step => console.log("• " + step));
});
```

Y el endpoint (en su backend Java):

```java
@RestController
@PostMapping("/api/solve")
public DifferentialEquationResponse solve(@RequestBody SolveRequest req) {
    return EquationEvaluator.evaluate(
        req.getEquation(),
        req.getMethod(),
        req.getInitialConditions()
    );
}
```

---

## ✨ Lo Mejor

✅ **No necesita API complicada** - Solo una clase con un método `evaluate()`  
✅ **Sin Spring obligatorio** - Funciona en cualquier proyecto Java  
✅ **Sin base de datos** - Todo en memoria  
✅ **Tipo Photomath** - Retorna pasos de resolución  
✅ **Flexible** - Soporta UC, VP, AUTO  
✅ **Simple** - Una línea de código para resolver

---

## 📚 Si tu amigo quiere saber MÁS

Ver documentos:
- `GUIA_PARA_AMIGO_EVALUATOR.md` - Guía completa de uso
- `QUE_RETORNA_EL_JSON.md` - Explicación detallada del JSON

---

**¡YA ESTÁ LISTO PARA TU AMIGO!** 🎉
