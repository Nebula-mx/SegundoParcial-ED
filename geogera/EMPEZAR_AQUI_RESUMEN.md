# 🚀 EMPEZAR AQUÍ - Guía Rápida

## Para tu Amigo Evaluador

Simplemente ejecuta lo siguiente:

```java
// Opción 1: Resolver y obtener objeto con pasos
StepResponse resultado = Main.evaluateWithSteps("y' + y = 0");
System.out.println(resultado.getFinalSolution());

// Opción 2: Obtener JSON directo
String json = Main.evaluateWithStepsAsJson("y'' - 5*y' + 6*y = 0");
System.out.println(json);
```

---

## 3 DTOs Principales

| Clase | Uso | Recomendado para |
|-------|-----|------------------|
| **StepResponse** | Con pasos detallados | ⭐ Frontend |
| **DifferentialEquationResponse** | Básico sin pasos | Backend simple |
| **EvaluationResult** (en progreso) | Formato Photomath | Futuro |

---

## Ejemplos Rápidos

### Ejemplo 1: Homogénea
```java
Main.evaluateWithSteps("y'' - 5*y' + 6*y = 0")
```
**Resultado:** 2 raíces reales (r₁=2, r₂=3)

### Ejemplo 2: Con Resonancia
```java
Main.evaluateWithSteps("y'' + 4*y = sin(2*x)")
```
**Resultado:** Detecta automáticamente resonancia

### Ejemplo 3: Variation of Parameters
```java
Main.evaluateWithSteps("y'' + y = 1/cos(x)", "VP")
```
**Resultado:** Usa método VP

---

## Archivos Importantes

📁 **DTOs:**
- `StepResponse.java` - Respuesta con pasos ⭐
- `DifferentialEquationResponse.java` - Respuesta simple

📁 **Servicios:**
- `StepByStepSolver.java` - Genera pasos
- `Main.java` - Punto de entrada

📁 **Documentación:**
- `ESTRUCTURA_DTOS.md` - Explicación de DTOs
- `ARQUITECTURA_COMPLETA.md` - Cómo funciona todo
- `NUEVO_FORMATO_JSON_CON_PASOS.md` - Formato JSON detallado

---

## ✅ Checklist Final

- ✅ Todos los archivos compilables
- ✅ 283 tests passing
- ✅ 22 ecuaciones soportadas
- ✅ Formato JSON con pasos (Photomath-like)
- ✅ Sin API complicada
- ✅ Sin Spring Boot necesario
- ✅ Fácil de usar: `Main.evaluateWithSteps(ecuacion)`

---

## 🎯 Próximos Pasos (si necesitas)

1. **Para Frontend:** Usa `Main.evaluateWithStepsAsJson()` y parsea el JSON
2. **Para Tests:** Copia ejemplos de `TwentyTwoEquationsTest.java`
3. **Para Entender:** Lee `ARQUITECTURA_COMPLETA.md`
4. **Para Modificar:** Edita `StepByStepSolver.java` para cambiar formato de pasos

---

**¡Proyecto listo para entregar!** 🎉
