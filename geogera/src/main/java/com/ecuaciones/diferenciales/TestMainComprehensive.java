package com.ecuaciones.diferenciales;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import com.ecuaciones.diferenciales.model.EcuationParser;
import com.ecuaciones.diferenciales.model.roots.Root;
import com.ecuaciones.diferenciales.model.solver.homogeneous.HomogeneousSolver;
import com.ecuaciones.diferenciales.model.solver.homogeneous.PolynomialSolver;
import com.ecuaciones.diferenciales.model.solver.nonhomogeneous.UndeterminedCoeff;
import com.ecuaciones.diferenciales.model.solver.nonhomogeneous.UndeterminedCoeffResolver;
import com.ecuaciones.diferenciales.model.templates.ExpressionData;

public class TestMainComprehensive {
    
    public static void main(String[] args) {
        System.out.println("\n🧪 PRUEBAS RÁPIDAS\n");
        
        String[] ecuaciones = {
            "y'' + 4*y = 8*cos(2*x)",
            "y'' + y = cos(x)",
            "y'' + 4*y = sin(2*x)",
            "y'' + 4*y = cos(x)",
            "y'' - 5*y' + 6*y = 1",
        };
        
        for (String ecu : ecuaciones) {
            try {
                prueba(ecu);
            } catch (Exception e) {
                System.out.println("❌ " + ecu + " → ERROR: " + e.getMessage());
            }
        }
        
        System.out.println("\n✅ PRUEBAS COMPLETADAS\n");
    }
    
    private static void prueba(String ecuacion) throws Exception {
        EcuationParser parser = new EcuationParser();
        ExpressionData data = parser.parse(ecuacion);
        
        if (data == null) {
            System.out.println("❌ " + ecuacion + " → Parse error");
            return;
        }
        
        Double[] coeffsArray = data.getCoefficients();
        List<Double> coeffs = Arrays.asList(coeffsArray);
        
        // Resolver raíces
        List<Root> roots = PolynomialSolver.solve(coeffs);
        HomogeneousSolver hSolver = new HomogeneousSolver();
        String yh = hSolver.generateHomogeneousSolution(roots);
        
        if (data.getIsHomogeneous()) {
            System.out.println("✅ " + ecuacion);
            System.out.println("   y_h = " + yh);
        } else {
            String g_x = data.getIndependentTerm().get("g(x)");
            UndeterminedCoeff ucSolver = new UndeterminedCoeff(roots);
            String ypForm = ucSolver.getParticularSolutionForm(g_x);
            
            UndeterminedCoeffResolver ucResolver = new UndeterminedCoeffResolver(data, ucSolver);
            Map<String, Double> solvedCoeffs = ucResolver.resolveCoefficients();
            String yp = ucSolver.generateParticularSolution(ypForm, solvedCoeffs);
            
            String cleanYp = yp.replaceAll("^y_p\\(x\\)\\s*=\\s*", "").trim();
            
            System.out.println("✅ " + ecuacion);
            System.out.println("   y_h = " + yh);
            System.out.println("   y_p = " + cleanYp);
        }
        System.out.println();
    }
}
