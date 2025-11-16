package com.ecuaciones.diferenciales;

/**
 * EXPLICACIÓN DETALLADA: ¿DÓNDE SUCEDE TODO?
 * 
 * Pregunta: "Entonces recibe una instancia de ExpressionData y llama métodos 
 *           del backend así? ¿Y en qué clase eso hace su parte?"
 * 
 * RESPUESTA: Exacto. Aquí está TODO el flujo paso a paso.
 */
public class DondeSucedeElFlujo {
    
    public static void main(String[] args) {
        System.out.println("\n" + "╔" + "═".repeat(90) + "╗");
        System.out.println("║" + " ".repeat(20) + "EXPLICACIÓN: ¿DÓNDE SUCEDE TODO?" + " ".repeat(36) + "║");
        System.out.println("╚" + "═".repeat(90) + "╝\n");
        
        mostrarFlujoCompleto();
        mostrarCadaClase();
        mostrarEjemploConCodigo();
    }
    
    private static void mostrarFlujoCompleto() {
        System.out.println("┌" + "─".repeat(88) + "┐");
        System.out.println("│ FLUJO COMPLETO: DE LA ECUACIÓN A LA RESPUESTA                             │");
        System.out.println("├" + "─".repeat(88) + "┤");
        System.out.println("""
            │
            │ INICIO: Tu amigo llama
            │ ┌─────────────────────────────────────────────────────┐
            │ │ String json = Main.evaluateWithStepsAsJson(         │
            │ │     "y'' - 5*y' + 6*y = 0"                         │
            │ │ );                                                  │
            │ └─────────────────────────────────────────────────────┘
            │
            ▼ PASO 1: Main.evaluateWithStepsAsJson() en Main.java
            ┌─────────────────────────────────────────────────────────────────────────────┐
            │ public static String evaluateWithStepsAsJson(String ecuacion, String m) {   │
            │     StepResponse response = evaluateWithSteps(ecuacion, metodo);            │
            │     ObjectMapper mapper = new ObjectMapper();                              │
            │     return mapper.writeValueAsString(response);                            │
            │ }                                                                           │
            └─────────────────────────────────────────────────────────────────────────────┘
            │
            ▼ PASO 2: Main.evaluateWithSteps() en Main.java
            ┌─────────────────────────────────────────────────────────────────────────────┐
            │ public static StepResponse evaluateWithSteps(String ecuacion, String m) {   │
            │     // AQUÍ COMIENZA LA MAGIA                                              │
            │     long startTime = System.currentTimeMillis();                           │
            │     EcuationParser parser = new EcuationParser();                          │
            │     ...                                                                    │
            │ }                                                                           │
            └─────────────────────────────────────────────────────────────────────────────┘
            │
            ▼ PASO 3: EcuationParser.parse() en EcuationParser.java
            ┌─────────────────────────────────────────────────────────────────────────────┐
            │ ExpressionData data = parser.parse("y'' - 5*y' + 6*y = 0");               │
            │                                                                             │
            │ ExpressionData contiene:                                                   │
            │ ├─ coefficients: [1.0, -5.0, 6.0]  (del polinomio r² - 5r + 6)          │
            │ ├─ order: 2                        (orden de la ecuación)                 │
            │ ├─ isHomogeneous: true             (¿es homogénea?)                       │
            │ └─ independentTerm: null           (no tiene término no homogéneo)        │
            └─────────────────────────────────────────────────────────────────────────────┘
            │
            ▼ PASO 4: PolynomialSolver.solve() en PolynomialSolver.java
            ┌─────────────────────────────────────────────────────────────────────────────┐
            │ List<Root> roots = PolynomialSolver.solve(data.getCoefficients());        │
            │                                                                             │
            │ Calcula raíces del polinomio característico:                              │
            │ r² - 5r + 6 = 0                                                            │
            │ └─ r₁ = 2                                                                  │
            │ └─ r₂ = 3                                                                  │
            │                                                                             │
            │ Retorna: List<Root> = [Root(2), Root(3)]                                 │
            └─────────────────────────────────────────────────────────────────────────────┘
            │
            ▼ PASO 5: HomogeneousSolver.generateHomogeneousSolution()
            ┌─────────────────────────────────────────────────────────────────────────────┐
            │ String solucion_h = HomogeneousSolver.                                     │
            │     generateHomogeneousSolution(roots);                                    │
            │                                                                             │
            │ Genera la solución homogénea:                                             │
            │ y(x) = C₁*e^(2x) + C₂*e^(3x)                                              │
            │                                                                             │
            │ (La fórmula es: para raíces reales r₁, r₂:                               │
            │  y_h = C₁*e^(r₁*x) + C₂*e^(r₂*x))                                        │
            └─────────────────────────────────────────────────────────────────────────────┘
            │
            ▼ PASO 6: StepByStepSolver.solve() en StepByStepSolver.java
            ┌─────────────────────────────────────────────────────────────────────────────┐
            │ StepByStepSolver stepSolver = new StepByStepSolver();                     │
            │ StepResponse response = stepSolver.solve(ecuacion);                       │
            │                                                                             │
            │ Genera todos los pasos:                                                    │
            │ ├─ Paso 1: Parsing                                                         │
            │ ├─ Paso 2: Clasificación                                                   │
            │ ├─ Paso 3: Ecuación característica                                        │
            │ ├─ Paso 4: Raíces                                                          │
            │ └─ Paso 5: Solución homogénea                                             │
            │                                                                             │
            │ Retorna: StepResponse con TODO                                            │
            └─────────────────────────────────────────────────────────────────────────────┘
            │
            ▼ PASO 7: ObjectMapper.writeValueAsString() en Jackson
            ┌─────────────────────────────────────────────────────────────────────────────┐
            │ String json = mapper.writeValueAsString(response);                         │
            │                                                                             │
            │ Convierte StepResponse a JSON:                                            │
            │ {                                                                           │
            │   "status": "SUCCESS",                                                     │
            │   "equation": "y'' - 5*y' + 6*y = 0",                                     │
            │   "steps": [...],                                                          │
            │   "finalSolution": "y(x) = C₁*e^(2x) + C₂*e^(3x)"                        │
            │ }                                                                           │
            │                                                                             │
            │ Retorna: String JSON                                                       │
            └─────────────────────────────────────────────────────────────────────────────┘
            │
            ▼ FINAL: Tu amigo recibe el JSON
            ┌─────────────────────────────────────────────────────────────────────────────┐
            │ String json = "{\"status\":\"SUCCESS\", ...}"                              │
            │                                                                             │
            │ ¡LISTO! Todo resuelto en una línea desde su perspectiva                   │
            └─────────────────────────────────────────────────────────────────────────────┘
            """);
        System.out.println("└" + "─".repeat(88) + "┘\n");
    }
    
    private static void mostrarCadaClase() {
        System.out.println("┌" + "─".repeat(88) + "┐");
        System.out.println("│ CADA CLASE Y SU RESPONSABILIDAD                                       │");
        System.out.println("├" + "─".repeat(88) + "┤");
        
        System.out.println("""
            │
            │ 1️⃣  Main.java (Orquestador)
            │    ├─ Ubicación: /src/main/java/com/ecuaciones/diferenciales/Main.java
            │    ├─ Responsabilidad: Coordinar todo
            │    ├─ Métodos principales:
            │    │  ├─ evaluateWithSteps(String ecuacion)
            │    │  ├─ evaluateWithSteps(String ecuacion, String metodo)
            │    │  ├─ evaluateWithStepsAsJson(String ecuacion)
            │    │  └─ evaluate(String ecuacion)
            │    └─ Lo que hace: Llama a EcuationParser, luego PolynomialSolver,
            │                    luego HomogeneousSolver, etc.
            │
            ▼
            │
            │ 2️⃣  EcuationParser.java (Parseador)
            │    ├─ Ubicación: /src/main/.../model/EcuationParser.java
            │    ├─ Responsabilidad: Convertir texto a estructura
            │    ├─ Entrada: "y'' - 5*y' + 6*y = 0"
            │    ├─ Salida: ExpressionData
            │    └─ ExpressionData contiene:
            │       ├─ coefficients: [1.0, -5.0, 6.0]
            │       ├─ order: 2
            │       ├─ isHomogeneous: true
            │       └─ independentTerm: null
            │
            ▼
            │
            │ 3️⃣  PolynomialSolver.java (Calculador de Raíces)
            │    ├─ Ubicación: /src/main/.../model/solver/homogeneous/PolynomialSolver.java
            │    ├─ Responsabilidad: Encontrar raíces
            │    ├─ Entrada: coefficients [1.0, -5.0, 6.0]
            │    ├─ Salida: List<Root>
            │    └─ Retorna: [Root(2.0), Root(3.0)]
            │
            ▼
            │
            │ 4️⃣  HomogeneousSolver.java (Generador de Solución)
            │    ├─ Ubicación: /src/main/.../model/solver/homogeneous/HomogeneousSolver.java
            │    ├─ Responsabilidad: Generar solución homogénea
            │    ├─ Entrada: List<Root> = [Root(2), Root(3)]
            │    ├─ Salida: String con solución
            │    └─ Retorna: "y(x) = C₁*e^(2x) + C₂*e^(3x)"
            │
            ▼
            │
            │ 5️⃣  StepByStepSolver.java (Generador de Pasos)
            │    ├─ Ubicación: /src/main/.../service/StepByStepSolver.java
            │    ├─ Responsabilidad: Generar pasos de resolución
            │    ├─ Entrada: String ecuacion
            │    ├─ Salida: StepResponse
            │    └─ StepResponse contiene:
            │       ├─ status: "SUCCESS"
            │       ├─ steps: [5+ pasos]
            │       ├─ finalSolution: "y(x) = ..."
            │       └─ metadata: {...}
            │
            ▼
            │
            │ 6️⃣  StepResponse.java (DTO)
            │    ├─ Ubicación: /src/main/.../dto/StepResponse.java
            │    ├─ Responsabilidad: Estructura de datos
            │    ├─ Contiene:
            │    │  ├─ String status
            │    │  ├─ String equation
            │    │  ├─ List<Step> steps
            │    │  ├─ String finalSolution
            │    │  └─ Map<String, String> metadata
            │    └─ Inner class Step:
            │       ├─ String type
            │       ├─ String title
            │       ├─ List<String> expressions
            │       ├─ String explanation
            │       └─ Map<String, Object> details
            │
            ▼
            │
            │ 7️⃣  ObjectMapper (Jackson Library)
            │    ├─ Librería: com.fasterxml.jackson.databind.ObjectMapper
            │    ├─ Responsabilidad: Serializar a JSON
            │    ├─ Entrada: StepResponse (objeto Java)
            │    └─ Salida: String JSON
            │
            """);
        System.out.println("└" + "─".repeat(88) + "┘\n");
    }
    
    private static void mostrarEjemploConCodigo() {
        System.out.println("┌" + "─".repeat(88) + "┐");
        System.out.println("│ EJEMPLO CON CÓDIGO REAL: CÓMO SUCEDE INTERNAMENTE                   │");
        System.out.println("├" + "─".repeat(88) + "┤");
        System.out.println("""
            │
            │ En Main.java, en el método evaluateWithSteps():
            │
            │ public static StepResponse evaluateWithSteps(String ecuacion, String metodo) {
            │     long startTime = System.currentTimeMillis();
            │     EcuationParser parser = new EcuationParser();
            │     
            │     // PASO 1: Parsear
            │     ExpressionData data = parser.parse(ecuacion);
            │                                          ↓
            │     // ExpressionData ahora contiene:
            │     // - coefficients: [1.0, -5.0, 6.0]
            │     // - order: 2
            │     // - isHomogeneous: true
            │     
            │     // PASO 2: Obtener coeficientes
            │     Double[] coeffsArray = data.getCoefficients();
            │     int order = data.getOrder();
            │     
            │     // PASO 3: Resolver raíces
            │     List<Double> coeffs = Arrays.asList(coeffsArray);
            │     List<Root> finalRoots = PolynomialSolver.solve(coeffs);
            │                                                      ↓
            │     // Ahora finalRoots = [Root(2.0), Root(3.0)]
            │     
            │     // PASO 4: Generar solución homogénea
            │     HomogeneousSolver hSolver = new HomogeneousSolver();
            │     String solution_h = hSolver.generateHomogeneousSolution(finalRoots);
            │                                                              ↓
            │     // solution_h = "y(x) = C₁*e^(2x) + C₂*e^(3x)"
            │     
            │     // PASO 5: Generar pasos
            │     StepByStepSolver stepSolver = new StepByStepSolver();
            │     StepResponse response = stepSolver.solve(ecuacion);
            │                                             ↓
            │     // response contiene:
            │     // - status: "SUCCESS"
            │     // - steps: [5 pasos]
            │     // - finalSolution: "y(x) = C₁*e^(2x) + C₂*e^(3x)"
            │     
            │     return response;
            │ }
            │
            │ Y luego en evaluateWithStepsAsJson():
            │
            │ public static String evaluateWithStepsAsJson(...) {
            │     StepResponse response = evaluateWithSteps(ecuacion, metodo);
            │     ObjectMapper mapper = new ObjectMapper();
            │     String json = mapper.writeValueAsString(response);
            │                                             ↓
            │     // json = "{\"status\":\"SUCCESS\",\"equation\":...}"
            │     
            │     return json;
            │ }
            │
            """);
        System.out.println("└" + "─".repeat(88) + "┘\n");
        
        System.out.println("┌" + "─".repeat(88) + "┐");
        System.out.println("│ RESUMEN: LA ARQUITECTURA EN CAPAS                                  │");
        System.out.println("├" + "─".repeat(88) + "┤");
        System.out.println("""
            │
            │ CAPA 1: ENTRADA (Frontend / Tu amigo)
            │ ├─ Llama: Main.evaluateWithStepsAsJson("y'' - 5*y' + 6*y = 0")
            │ └─ Obtiene: String JSON
            │
            ▼
            │
            │ CAPA 2: ORQUESTACIÓN (Main.java)
            │ ├─ Coordina: EcuationParser → PolynomialSolver → HomogeneousSolver
            │ └─ Retorna: StepResponse
            │
            ▼
            │
            │ CAPA 3: PARSEO (EcuationParser.java)
            │ ├─ Convierte: "y'' - 5*y' + 6*y = 0" → ExpressionData
            │ └─ Extrae: coefficients, order, isHomogeneous
            │
            ▼
            │
            │ CAPA 4: CÁLCULO (PolynomialSolver, HomogeneousSolver, etc.)
            │ ├─ Calcula: raíces, soluciones
            │ └─ Retorna: Datos estructurados
            │
            ▼
            │
            │ CAPA 5: GENERACIÓN DE PASOS (StepByStepSolver.java)
            │ ├─ Crea: List<Step> con explicaciones
            │ └─ Retorna: StepResponse completo
            │
            ▼
            │
            │ CAPA 6: SERIALIZACIÓN (ObjectMapper)
            │ ├─ Convierte: StepResponse → JSON String
            │ └─ Retorna: String JSON
            │
            ▼
            │
            │ CAPA 7: SALIDA (Frontend / Tu amigo)
            │ ├─ Recibe: String JSON con TODO
            │ └─ Usa: Lo que necesita (solución, pasos, etc.)
            │
            """);
        System.out.println("└" + "─".repeat(88) + "┘\n");
        
        System.out.println("╔" + "═".repeat(88) + "╗");
        System.out.println("║ RESPUESTA A TU PREGUNTA:                                                 ║");
        System.out.println("╠" + "═".repeat(88) + "╣");
        System.out.println("║                                                                          ║");
        System.out.println("║ ❓ \"¿Recibe una instancia de ExpressionData y llama métodos del       ║");
        System.out.println("║    backend así? ¿En qué clase eso hace su parte?\"                      ║");
        System.out.println("║                                                                          ║");
        System.out.println("║ ✅ RESPUESTA:                                                           ║");
        System.out.println("║                                                                          ║");
        System.out.println("║ SÍ, EXACTAMENTE. Así funciona:                                         ║");
        System.out.println("║                                                                          ║");
        System.out.println("║ 1. Main.java RECIBE la ecuación                                         ║");
        System.out.println("║ 2. Main.java LLAMA a EcuationParser.parse()                            ║");
        System.out.println("║    └─ Retorna: ExpressionData con coefficients, order, etc.           ║");
        System.out.println("║                                                                          ║");
        System.out.println("║ 3. Main.java LLAMA a PolynomialSolver.solve()                          ║");
        System.out.println("║    ├─ Entrada: ExpressionData.getCoefficients()                        ║");
        System.out.println("║    └─ Retorna: List<Root> con raíces                                   ║");
        System.out.println("║                                                                          ║");
        System.out.println("║ 4. Main.java LLAMA a HomogeneousSolver.generate()                      ║");
        System.out.println("║    ├─ Entrada: List<Root>                                              ║");
        System.out.println("║    └─ Retorna: String con solución                                     ║");
        System.out.println("║                                                                          ║");
        System.out.println("║ 5. Main.java LLAMA a StepByStepSolver.solve()                          ║");
        System.out.println("║    ├─ Entrada: String ecuacion                                         ║");
        System.out.println("║    └─ Retorna: StepResponse con TODOS los pasos                        ║");
        System.out.println("║                                                                          ║");
        System.out.println("║ 6. Main.java LLAMA a ObjectMapper.writeValueAsString()                 ║");
        System.out.println("║    ├─ Entrada: StepResponse                                            ║");
        System.out.println("║    └─ Retorna: String JSON                                             ║");
        System.out.println("║                                                                          ║");
        System.out.println("║ TODO ESTO SUCEDE EN Main.java (en el método evaluateWithStepsAsJson)  ║");
        System.out.println("║ pero CADA PASO es delegado a su clase específica.                      ║");
        System.out.println("║                                                                          ║");
        System.out.println("║ ¡ES ARQUITECTURA PROFESIONAL! ✅                                       ║");
        System.out.println("║                                                                          ║");
        System.out.println("╚" + "═".repeat(88) + "╝\n");
    }
}
