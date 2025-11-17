import com.ecuaciones.diferenciales.utils.SymjaEngine;
import com.ecuaciones.diferenciales.utils.SymbolicDifferentiator;

public class DiagnoseSymjaIssue {
    public static void main(String[] args) {
        System.out.println("╔═══════════════════════════════════════════════════════╗");
        System.out.println("║     DIAGNÓSTICO DE PROBLEMAS CON SYMJA                 ║");
        System.out.println("╚═══════════════════════════════════════════════════════╝\n");
        
        // PROBLEMA 1: Derivadas simbólicas con exponenciales
        System.out.println("PROBLEMA 1: Derivadas simbólicas");
        System.out.println("─────────────────────────────────");
        String expr1 = "e^(3*x)";
        System.out.println("Expresión original: " + expr1);
        String der1 = SymbolicDifferentiator.differentiate(expr1, 1);
        System.out.println("Primera derivada (simbólica): " + der1);
        System.out.println("Problema: ¿Está en sintaxis Symja o estándar?\n");
        
        // PROBLEMA 2: Evaluación numérica con sintaxis Symja
        System.out.println("PROBLEMA 2: Evaluación numérica");
        System.out.println("──────────────────────────────");
        String expr2 = "Exp[3*x]";  // Sintaxis Symja
        System.out.println("Expresión Symja: " + expr2);
        try {
            double val = SymjaEngine.evaluateNumerical(expr2, 1.0);
            System.out.println("Evaluación en x=1: " + val);
        } catch (Exception e) {
            System.out.println("ERROR al evaluar: " + e.getMessage());
        }
        System.out.println();
        
        // PROBLEMA 3: Forma de entrada vs salida
        System.out.println("PROBLEMA 3: Inconsistencia entrada/salida");
        System.out.println("───────────────────────────────────────");
        System.out.println("Entrada esperada: e^(3*x)");
        System.out.println("Salida de Symja:  Exp[3*x]");
        System.out.println("Necesita conversión bidireccional\n");
        
        // PROBLEMA 4: Derivadas de orden superior
        System.out.println("PROBLEMA 4: Derivadas de orden superior");
        System.out.println("───────────────────────────────────────");
        String expr4 = "C1*e^(3*x) + C2*e^(2*x)";
        System.out.println("Expresión: " + expr4);
        String der4_1 = SymbolicDifferentiator.differentiate(expr4, 1);
        System.out.println("Primera derivada: " + der4_1);
        String der4_2 = SymbolicDifferentiator.differentiate(expr4, 2);
        System.out.println("Segunda derivada: " + der4_2);
        System.out.println();
    }
}
