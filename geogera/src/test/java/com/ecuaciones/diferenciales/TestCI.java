package com.ecuaciones.diferenciales;

import com.ecuaciones.diferenciales.model.solver.InitialConditionsSolver;
import java.util.*;

public class TestCI {
    public static void main(String[] args) {
        // Prueba directa del InitialConditionsSolver
        
        String generalSolution = "C1 * e^(3*x) + C2 * e^(2*x)";
        int order = 2;
        
        System.out.println("════════════════════════════════════════════════════════");
        System.out.println("PRUEBA DIRECTA: InitialConditionsSolver");
        System.out.println("════════════════════════════════════════════════════════");
        System.out.println();
        System.out.println("Solución General: y(x) = " + generalSolution);
        System.out.println();
        
        try {
            InitialConditionsSolver solver = new InitialConditionsSolver(generalSolution, order);
            
            // Condiciones iniciales
            List<String> conditions = Arrays.asList("y(0)=1", "y'(0)=2");
            
            System.out.println("Condiciones Iniciales:");
            for (String c : conditions) {
                System.out.println("  • " + c);
            }
            System.out.println();
            
            // Parsear
            List<InitialConditionsSolver.InitialCondition> parsed = 
                InitialConditionsSolver.parseConditions(conditions);
            
            System.out.println("Condiciones Parseadas:");
            for (InitialConditionsSolver.InitialCondition ic : parsed) {
                System.out.println("  • " + ic);
            }
            System.out.println();
            
            // Resolver
            Map<String, Double> constants = solver.solveInitialConditions(parsed);
            
            System.out.println("Constantes Calculadas:");
            for (Map.Entry<String, Double> entry : constants.entrySet()) {
                System.out.println("  " + entry.getKey() + " = " + entry.getValue());
            }
            System.out.println();
            
            // Aplicar
            String particular = solver.applyConstants(constants);
            
            System.out.println("Solución Particular:");
            System.out.println("  y(x) = " + particular);
            System.out.println();
            System.out.println("════════════════════════════════════════════════════════");
            System.out.println("✅ PRUEBA EXITOSA");
            System.out.println("════════════════════════════════════════════════════════");
            
        } catch (Exception e) {
            System.out.println("❌ ERROR:");
            e.printStackTrace();
        }
    }
}
