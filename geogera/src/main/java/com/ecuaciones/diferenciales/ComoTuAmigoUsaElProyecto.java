package com.ecuaciones.diferenciales;

import com.ecuaciones.diferenciales.dto.StepResponse;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * EXACTAMENTE CÓMO TU AMIGO USA EL PROYECTO
 * 
 * ¿La pregunta?: "¿Cómo mi amigo llama a un método, se hace todo en el main y retorna el JSON?"
 * 
 * ¡LA RESPUESTA ES SÍ! Exactamente eso es.
 */
public class ComoTuAmigoUsaElProyecto {
    
    public static void main(String[] args) throws Exception {
        System.out.println("\n" + "═".repeat(80));
        System.out.println("DEMOSTRACIÓN: CÓMO TU AMIGO LLAMA A UN MÉTODO Y OBTIENE JSON");
        System.out.println("═".repeat(80) + "\n");
        
        // ═══════════════════════════════════════════════════════════════════════════════
        // OPCIÓN 1: Llamada MÁS SIMPLE (Sin ver pasos internos)
        // ═══════════════════════════════════════════════════════════════════════════════
        
        System.out.println("┌─ OPCIÓN 1: FORMA MÁS SIMPLE ─────────────────────────────────────┐");
        System.out.println("│ El amigo escribe 2 líneas y obtiene JSON                          │");
        System.out.println("└────────────────────────────────────────────────────────────────────┘\n");
        
        // ESTO ES TODO LO QUE TU AMIGO TIENE QUE ESCRIBIR:
        String json = Main.evaluateWithStepsAsJson("y' + y = 0");
        System.out.println("💻 Código que tu amigo escribe:");
        System.out.println("   String json = Main.evaluateWithStepsAsJson(\"y' + y = 0\");");
        System.out.println("   System.out.println(json);\n");
        
        System.out.println("📤 JSON Retornado:\n");
        System.out.println(json);
        
        // ═══════════════════════════════════════════════════════════════════════════════
        // OPCIÓN 2: Obteniendo el objeto (no JSON string)
        // ═══════════════════════════════════════════════════════════════════════════════
        
        System.out.println("\n\n" + "┌─ OPCIÓN 2: OBTENIENDO OBJETO (NO JSON STRING) ──────────────────┐");
        System.out.println("│ Tu amigo trabaja con objeto en lugar de JSON string              │");
        System.out.println("└────────────────────────────────────────────────────────────────────┘\n");
        
        // El amigo obtiene el objeto StepResponse
        StepResponse respuesta = Main.evaluateWithSteps("y'' - 5*y' + 6*y = 0");
        
        System.out.println("💻 Código que tu amigo escribe:");
        System.out.println("   StepResponse respuesta = Main.evaluateWithSteps(\"y'' - 5*y' + 6*y = 0\");");
        System.out.println("   System.out.println(respuesta.getFinalSolution());\n");
        
        System.out.println("✅ Status: " + respuesta.getStatus());
        System.out.println("📐 Ecuación: " + respuesta.getEquation());
        System.out.println("🎯 Solución Final: " + respuesta.getFinalSolution());
        System.out.println("📊 Número de pasos: " + respuesta.getStepCount());
        System.out.println("⏱️  Tiempo de ejecución: " + respuesta.getExecutionTimeMs() + "ms");
        
        // El amigo puede acceder a los pasos:
        System.out.println("\n📚 PASOS INTERNOS (a los que el amigo puede acceder):");
        if (respuesta.getSteps() != null) {
            for (int i = 0; i < respuesta.getSteps().size(); i++) {
                StepResponse.Step paso = respuesta.getSteps().get(i);
                System.out.println("  Paso " + (i+1) + ": " + paso.getTitle());
            }
        }
        
        // ═══════════════════════════════════════════════════════════════════════════════
        // OPCIÓN 3: Convertir el objeto a JSON manualmente
        // ═══════════════════════════════════════════════════════════════════════════════
        
        System.out.println("\n\n" + "┌─ OPCIÓN 3: CONVERTIR OBJETO A JSON MANUALMENTE ────────────────┐");
        System.out.println("│ Si tu amigo quiere JSON desde el objeto                           │");
        System.out.println("└────────────────────────────────────────────────────────────────────┘\n");
        
        ObjectMapper mapper = new ObjectMapper();
        String jsonDesdeObjeto = mapper.writerWithDefaultPrettyPrinter()
                                       .writeValueAsString(respuesta);
        
        System.out.println("💻 Código que tu amigo escribe:");
        System.out.println("   ObjectMapper mapper = new ObjectMapper();");
        System.out.println("   String json = mapper.writerWithDefaultPrettyPrinter()");
        System.out.println("                         .writeValueAsString(respuesta);");
        System.out.println("   System.out.println(json);\n");
        
        System.out.println("📄 JSON resultado:\n");
        System.out.println(jsonDesdeObjeto);
        
        // ═══════════════════════════════════════════════════════════════════════════════
        // OPCIÓN 4: Usar Map simple (sin JsonResponse)
        // ═══════════════════════════════════════════════════════════════════════════════
        
        System.out.println("\n\n" + "┌─ OPCIÓN 4: USAR Map<String, Object> (MÁS SIMPLE) ──────────────┐");
        System.out.println("│ Si tu amigo no quiere usar DTOs complicados                      │");
        System.out.println("└────────────────────────────────────────────────────────────────────┘\n");
        
        System.out.println("💻 Código que tu amigo escribe:");
        System.out.println("   Map<String, Object> resultado = Main.evaluate(\"y' + y = 0\");");
        System.out.println("   System.out.println(resultado.get(\"finalSolution\"));\n");
        
        java.util.Map<String, Object> resultado = Main.evaluate("y' + y = 0");
        System.out.println("✅ Status: " + resultado.get("status"));
        System.out.println("🎯 Solución: " + resultado.get("finalSolution"));
        System.out.println("⏱️  Tiempo: " + resultado.get("executionTimeMs") + "ms");
        
        // ═══════════════════════════════════════════════════════════════════════════════
        // RESUMEN
        // ═══════════════════════════════════════════════════════════════════════════════
        
        System.out.println("\n\n" + "═".repeat(80));
        System.out.println("RESUMEN: ¿CÓMO MI AMIGO LLAMA Y QUÉ RETORNA?");
        System.out.println("═".repeat(80) + "\n");
        
        System.out.println("┌──────────────────────────────────────────────────────────────────┐");
        System.out.println("│ FLUJO EXACTO:                                                    │");
        System.out.println("├──────────────────────────────────────────────────────────────────┤");
        System.out.println("│                                                                  │");
        System.out.println("│ 1️⃣  Tu amigo llama: Main.evaluateWithSteps(\"ecuacion\")        │");
        System.out.println("│                                                                  │");
        System.out.println("│ 2️⃣  DENTRO DE Main.java sucede TODO:                           │");
        System.out.println("│     ✓ EcuationParser parsea la ecuación                         │");
        System.out.println("│     ✓ PolynomialSolver encuentra raíces                        │");
        System.out.println("│     ✓ HomogeneousSolver resuelve parte homogénea               │");
        System.out.println("│     ✓ UndeterminedCoeff o VP resuelve parte particular         │");
        System.out.println("│     ✓ StepByStepSolver genera los pasos                        │");
        System.out.println("│                                                                  │");
        System.out.println("│ 3️⃣  Se retorna: StepResponse (objeto con TODA la info)        │");
        System.out.println("│     ✓ status: \"SUCCESS\"                                        │");
        System.out.println("│     ✓ steps: [array con todos los pasos]                        │");
        System.out.println("│     ✓ finalSolution: \"y(x) = ...\"                              │");
        System.out.println("│     ✓ solutionLatex: \"...\"                                     │");
        System.out.println("│     ✓ metadata: {información adicional}                         │");
        System.out.println("│                                                                  │");
        System.out.println("│ 4️⃣  Tu amigo puede hacer:                                       │");
        System.out.println("│     a) Acceder directamente: resp.getFinalSolution()            │");
        System.out.println("│     b) Convertir a JSON: mapper.writeValueAsString(resp)        │");
        System.out.println("│     c) Iterar pasos: resp.getSteps()                            │");
        System.out.println("│                                                                  │");
        System.out.println("└──────────────────────────────────────────────────────────────────┘");
        
        System.out.println("\n" + "═".repeat(80));
        System.out.println("¿DÓNDE SE HACE TODO? RESPUESTA: EN Main.java");
        System.out.println("═".repeat(80) + "\n");
        
        System.out.println("Cuando tu amigo llama: Main.evaluateWithSteps(ecuacion)");
        System.out.println("\nMain.java hace esto internamente:\n");
        System.out.println("  public static StepResponse evaluateWithSteps(String ecuacion) {");
        System.out.println("      return evaluateWithSteps(ecuacion, \"AUTO\");");
        System.out.println("  }");
        System.out.println("  ");
        System.out.println("  public static StepResponse evaluateWithSteps(String ecuacion, String metodo) {");
        System.out.println("      // 1. Parsing");
        System.out.println("      EcuationParser parser = new EcuationParser();");
        System.out.println("      ExpressionData data = parser.parse(ecuacion);");
        System.out.println("      ");
        System.out.println("      // 2. Resolver raíces");
        System.out.println("      List<Root> roots = PolynomialSolver.solve(coefficients);");
        System.out.println("      ");
        System.out.println("      // 3. Generar solución");
        System.out.println("      String solucion_h = HomogeneousSolver.generate(...);");
        System.out.println("      String solucion_p = UndeterminedCoeff.generate(...);");
        System.out.println("      ");
        System.out.println("      // 4. Generar pasos");
        System.out.println("      StepByStepSolver stepSolver = new StepByStepSolver();");
        System.out.println("      StepResponse response = stepSolver.solve(ecuacion);");
        System.out.println("      ");
        System.out.println("      // 5. Retornar");
        System.out.println("      return response;  // <-- TODO EN UN SOLO RETORNO");
        System.out.println("  }");
        
        System.out.println("\n" + "═".repeat(80));
        System.out.println("✅ CONCLUSIÓN");
        System.out.println("═".repeat(80) + "\n");
        
        System.out.println("SÍ, tu amigo:");
        System.out.println("✅ Llama a UN SOLO MÉTODO: Main.evaluateWithSteps(ecuacion)");
        System.out.println("✅ Se hace TODO internamente en Main.java y sus colaboradores");
        System.out.println("✅ Obtiene TODO en UN SOLO objeto: StepResponse");
        System.out.println("✅ Puede extraer: solución, pasos, LaTeX, todo");
        System.out.println("✅ Puede convertir a JSON si lo necesita");
        System.out.println("\n¡Así de simple! 🎉\n");
    }
}
