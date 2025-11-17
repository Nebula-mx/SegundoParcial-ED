# 🎯 ¿CÓMO MI AMIGO USA EL PROYECTO?

## La Gran Pregunta

> **"¿Mi amigo entonces cómo llama a un método, se hace todo en el main y retorna el JSON?"**

## ✅ LA RESPUESTA: SÍ, EXACTAMENTE ESO

Tu amigo llama a **UN SOLO MÉTODO** y obtiene **TODO RESUELTO** incluyendo pasos.

---

## 📞 Opción 1: FORMA MÁS SIMPLE (La que tu amigo probablemente quiere)

```java
// TU AMIGO ESCRIBE ESTO (2 líneas):
String json = Main.evaluateWithStepsAsJson("y' + y = 0");
System.out.println(json);

// Y OBTIENE ESTE JSON:
{
  "status": "SUCCESS",
  "equation": "y' + y = 0",
  "steps": [
    {"type": "CLASSIFY", "title": "📖 Parsing...", ...},
    {"type": "CHARACTERISTIC", "title": "📐 Ecuación...", ...},
    {"type": "ROOTS", "title": "🔍 Raíces...", ...},
    {"type": "HOMOGENEOUS_SOLUTION", "title": "✨ Solución...", ...}
  ],
  "finalSolution": "y(x) = C₁*e^(-x)",
  "stepCount": 4
}
```

**¿Dónde se hace todo?** 
- **EN MAIN.JAVA** - Todos los métodos se llaman internamente
- Tu amigo solo ve el resultado final

---

## 🏗️ ¿QUÉ SUCEDE INTERNAMENTE?

Cuando tu amigo llama a `Main.evaluateWithStepsAsJson()`:

```
┌─────────────────────────────────────────────────────────────────┐
│ Tu amigo llama:                                                 │
│ Main.evaluateWithStepsAsJson("y' + y = 0")                    │
└────────────┬────────────────────────────────────────────────────┘
             │
             ▼
┌─────────────────────────────────────────────────────────────────┐
│ DENTRO DE Main.java sucede AUTOMÁTICAMENTE:                    │
│                                                                 │
│ 1. EcuationParser parser = new EcuationParser();              │
│    → Parsea "y' + y = 0"                                       │
│                                                                 │
│ 2. ExpressionData data = parser.parse(ecuacion);              │
│    → Extrae estructura de la ecuación                          │
│                                                                 │
│ 3. List<Root> roots = PolynomialSolver.solve(coeffs);         │
│    → Encuentra raíces: r = -1                                  │
│                                                                 │
│ 4. String solucion_h = HomogeneousSolver.generate(...);       │
│    → Genera: y_h = C₁*e^(-x)                                   │
│                                                                 │
│ 5. StepByStepSolver stepSolver = new StepByStepSolver();      │
│    → Genera los pasos paso a paso                              │
│                                                                 │
│ 6. StepResponse response = stepSolver.solve(ecuacion);        │
│    → Crea objeto con todo: status, steps[], solución, etc.    │
│                                                                 │
│ 7. ObjectMapper mapper = new ObjectMapper();                   │
│    String json = mapper.writeValueAsString(response);         │
│    → Convierte el objeto a JSON string                         │
└────────────┬────────────────────────────────────────────────────┘
             │
             ▼
┌─────────────────────────────────────────────────────────────────┐
│ Se retorna al amigo:                                            │
│ {                                                               │
│   "status": "SUCCESS",                                          │
│   "equation": "y' + y = 0",                                     │
│   "steps": [ {...}, {...}, {...}, {...} ],                    │
│   "finalSolution": "y(x) = C₁*e^(-x)",                        │
│   "stepCount": 4                                                │
│ }                                                               │
└─────────────────────────────────────────────────────────────────┘
```

---

## 4️⃣ OPCIONES DE USO PARA TU AMIGO

### Opción 1: JSON String (LA MÁS FÁCIL) ⭐

```java
// 2 líneas y listo:
String json = Main.evaluateWithStepsAsJson("y'' - 5*y' + 6*y = 0");
System.out.println(json);
```

**Ventaja:** Directo a JSON, sin procesamiento adicional

---

### Opción 2: Objeto StepResponse

```java
// Trabajo con el objeto en Java:
StepResponse resp = Main.evaluateWithSteps("y'' - 5*y' + 6*y = 0");

// Acceder a lo que necesite:
System.out.println(resp.getFinalSolution());      // "y(x) = C₁*e^(2x) + C₂*e^(3x)"
System.out.println(resp.getStatus());              // "SUCCESS"
System.out.println(resp.getStepCount());           // 5

// Iterar los pasos:
for (StepResponse.Step paso : resp.getSteps()) {
    System.out.println(paso.getTitle());
    System.out.println(paso.getExplanation());
}
```

**Ventaja:** Procesamiento en Java antes de usar

---

### Opción 3: Map Simple (Lo MÁS simple)

```java
// Sin DTOs complicados:
Map<String, Object> resultado = Main.evaluate("y' + y = 0");

// Acceder:
System.out.println(resultado.get("finalSolution"));  // "y(x) = C₁*e^(-x)"
System.out.println(resultado.get("status"));         // "SUCCESS"
```

**Ventaja:** Súper simple, sin necesidad de entender DTOs

---

### Opción 4: JSON desde Objeto

```java
// Si obtiene el objeto pero quiere JSON:
StepResponse resp = Main.evaluateWithSteps("y' + y = 0");

ObjectMapper mapper = new ObjectMapper();
String json = mapper.writerWithDefaultPrettyPrinter()
                     .writeValueAsString(resp);

System.out.println(json);
```

**Ventaja:** Máxima flexibilidad

---

## 🎯 RESUMEN VISUAL

```
TU AMIGO LLAMA UNA VEZ:
┌─────────────────────────────────┐
│ Main.evaluateWithSteps(ecuacion)│
└────────────┬────────────────────┘
             │
             ▼
TODO SE HACE AQUÍ:
┌──────────────────────────────────────────┐
│ ✓ Parsing                                │
│ ✓ Encontrar raíces                       │
│ ✓ Resolver homogénea                     │
│ ✓ Resolver particular (si aplica)        │
│ ✓ Generar pasos                          │
│ ✓ Formatear solución                     │
│ ✓ Convertir a JSON (si lo pide)          │
└────────────┬─────────────────────────────┘
             │
             ▼
RETORNA TODO EN UN SOLO OBJETO:
┌──────────────────────────────────────────┐
│ StepResponse {                           │
│   status: "SUCCESS"                      │
│   steps: [paso1, paso2, paso3, ...]     │
│   finalSolution: "y(x) = ..."           │
│   solutionLatex: "$...$"                │
│   metadata: {...}                        │
│   executionTimeMs: 42                    │
│ }                                        │
└──────────────────────────────────────────┘
```

---

## 📊 FLUJO EXACTO

```
1. TU AMIGO LLAMA:
   StepResponse resp = Main.evaluateWithSteps("y' + y = 0");

2. MAIN.JAVA INTERNAMENTE:
   ├─ EcuationParser parser = new EcuationParser();
   ├─ ExpressionData data = parser.parse(ecuacion);
   ├─ List<Root> roots = PolynomialSolver.solve(coeffs);
   ├─ String y_h = HomogeneousSolver.generate(roots);
   ├─ (Si no homogénea) String y_p = UndeterminedCoeff.generate(...);
   └─ StepByStepSolver genera pasos y retorna StepResponse

3. TU AMIGO RECIBE:
   StepResponse con TODO adentro:
   - status
   - equation
   - steps (array completo)
   - finalSolution
   - solutionLatex
   - metadata
   - execution time
```

---

## ✅ VENTAJAS

✅ **Una sola llamada** - Tu amigo llama `Main.evaluateWithSteps()`  
✅ **Todo interno** - Main.java hace todo automáticamente  
✅ **Resultado completo** - Obtiene solución + pasos + metadata + JSON  
✅ **Flexible** - Puede usar como objeto Java o JSON string  
✅ **Sin complicaciones** - No necesita entender la arquitectura interna  

---

## 🎓 EJEMPLOS REALES

### Ejemplo 1: Ecuación Simple
```java
StepResponse r = Main.evaluateWithSteps("y' + y = 0");
// Retorna: status=SUCCESS, steps=4, finalSolution="y(x) = C₁*e^(-x)"
```

### Ejemplo 2: Orden 2 con Resonancia
```java
String json = Main.evaluateWithStepsAsJson("y'' + 4*y = sin(2*x)");
// Retorna JSON con steps que incluye detección automática de resonancia
```

### Ejemplo 3: Variation of Parameters
```java
StepResponse r = Main.evaluateWithSteps("y'' + y = 1/cos(x)", "VP");
// Retorna: steps con método VP elegido automáticamente
```

---

## 🚀 CONCLUSIÓN

**¿Se hace todo en Main y retorna JSON?**

✅ **SÍ**, exactamente:

1. Tu amigo llama `Main.evaluateWithSteps(ecuacion)`
2. Todo sucede internamente en Main.java
3. Se retorna `StepResponse` con TODO (pasos, solución, JSON-ready)
4. Tu amigo puede:
   - Acceder directo: `resp.getFinalSolution()`
   - Convertir a JSON: `mapper.writeValueAsString(resp)`
   - Iterar pasos: `resp.getSteps()`

**¡Simple, limpio y profesional!** 🎉

