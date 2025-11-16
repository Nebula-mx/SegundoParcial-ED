package com.ecuaciones.diferenciales;

import java.util.Arrays;
import com.ecuaciones.diferenciales.evaluator.EquationEvaluator;
import com.ecuaciones.diferenciales.dto.DifferentialEquationResponse;

/**
 * EJEMPLO: Cómo tu amigo usará esto en su frontend
 * 
 * SIN API, SIN SERVLET, SIN COMPLICACIONES
 * Solo: evaluator.evaluate(ecuacion) -> respuesta
 */
public class SimpleEvaluatorExample {
    
    public static void main(String[] args) {
        
        System.out.println("╔════════════════════════════════════════════════════════════╗");
        System.out.println("║          EVALUADOR DE ECUACIONES - Tipo Photomath         ║");
        System.out.println("╚════════════════════════════════════════════════════════════╝");
        
        // ============ EJEMPLO 1: Ecuación Homogénea ============
        System.out.println("\n\n📌 EJEMPLO 1: Ecuación Homogénea Simple");
        System.out.println("─".repeat(60));
        
        DifferentialEquationResponse response1 = 
            EquationEvaluator.evaluate("y'' - 5*y' + 6*y = 0");
        
        System.out.println("Entrada: " + response1.getEquation());
        System.out.println("Status: " + response1.getStatus());
        System.out.println("Código: " + response1.getCode());
        System.out.println("\nSolución: " + response1.getFinalSolution());
        System.out.println("LaTeX: " + response1.getFinalSolutionLatex());
        
        // ============ EJEMPLO 2: Ecuación con Resonancia ============
        System.out.println("\n\n📌 EJEMPLO 2: Ecuación con Resonancia");
        System.out.println("─".repeat(60));
        
        DifferentialEquationResponse response2 = 
            EquationEvaluator.evaluate("y'' + 4*y = sin(2*x)");
        
        System.out.println("Entrada: " + response2.getEquation());
        System.out.println("Status: " + response2.getStatus());
        System.out.println("Método usado: " + response2.getParticularMethod());
        System.out.println("\nSolución: " + response2.getFinalSolution());
        
        // ============ EJEMPLO 3: Con Condiciones Iniciales ============
        System.out.println("\n\n📌 EJEMPLO 3: Con Condiciones Iniciales");
        System.out.println("─".repeat(60));
        
        DifferentialEquationResponse response3 = EquationEvaluator.evaluate(
            "y'' - 5*y' + 6*y = 0",
            "AUTO",
            Arrays.asList("y(0)=1", "y'(0)=2")
        );
        
        System.out.println("Entrada: " + response3.getEquation());
        System.out.println("Condiciones iniciales: " + response3.getInitialConditions());
        System.out.println("Solución: " + response3.getFinalSolution());
        
        // ============ EJEMPLO 4: Variación de Parámetros ============
        System.out.println("\n\n📌 EJEMPLO 4: Variación de Parámetros");
        System.out.println("─".repeat(60));
        
        DifferentialEquationResponse response4 = 
            EquationEvaluator.evaluate("y'' + y = 1/(1 + x^2)", "VP");
        
        System.out.println("Entrada: " + response4.getEquation());
        System.out.println("Método: " + response4.getMethod());
        System.out.println("Status: " + response4.getStatus());
        System.out.println("Método usado: " + response4.getParticularMethod());
        
        // ============ EJEMPLO 5: Error (ecuación inválida) ============
        System.out.println("\n\n📌 EJEMPLO 5: Manejo de Errores");
        System.out.println("─".repeat(60));
        
        DifferentialEquationResponse response5 = 
            EquationEvaluator.evaluate("2*x + 3 = 5");
        
        System.out.println("Entrada: " + response5.getEquation());
        System.out.println("Status: " + response5.getStatus());
        System.out.println("Error: " + response5.getMessage());
        System.out.println("Código: " + response5.getCode());
        
        // ============ EJEMPLO 6: Acceso a todos los datos ============
        System.out.println("\n\n📌 EJEMPLO 6: Acceso a TODOS los datos (como Photomath)");
        System.out.println("─".repeat(60));
        
        DifferentialEquationResponse response6 = 
            EquationEvaluator.evaluate("y'' - 5*y' + 6*y = 0");
        
        if (response6.isSuccess()) {
            System.out.println("✅ Resolución exitosa\n");
            
            System.out.println("📋 INFORMACIÓN DE LA ECUACIÓN:");
            System.out.println("   • Ecuación: " + response6.getEquation());
            System.out.println("   • Orden: " + response6.getOrder());
            System.out.println("   • Es homogénea: " + response6.isHomogeneous());
            System.out.println("   • Coeficientes: " + response6.getCoefficients());
            
            System.out.println("\n📊 RAÍCES:");
            for (DifferentialEquationResponse.RootInfo root : response6.getRoots()) {
                System.out.println("   • Raíz " + root.getIndex() + ": " + root.getDisplay());
            }
            
            System.out.println("\n📐 SOLUCIONES:");
            System.out.println("   • y_h = " + response6.getHomogeneousSolution());
            System.out.println("   • y_p = " + response6.getParticulatSolution());
            System.out.println("   • y_final = " + response6.getFinalSolution());
            
            System.out.println("\n📋 PASOS DE RESOLUCIÓN:");
            int step = 1;
            for (String s : response6.getResolutionSteps()) {
                System.out.println("   " + step + ". " + s);
                step++;
            }
        }
        
        System.out.println("\n\n╔════════════════════════════════════════════════════════════╗");
        System.out.println("║              ✅ EJEMPLOS COMPLETADOS                      ║");
        System.out.println("║                                                            ║");
        System.out.println("║  Para tu amigo:                                            ║");
        System.out.println("║  • Importa: EquationEvaluator                             ║");
        System.out.println("║  • Llama: evaluate(ecuacion)                              ║");
        System.out.println("║  • Obtén: DifferentialEquationResponse con todo           ║");
        System.out.println("║                                                            ║");
        System.out.println("║  SIN API, SIN SERVLET, SIN COMPLICACIONES ✨              ║");
        System.out.println("╚════════════════════════════════════════════════════════════╝");
    }
}
