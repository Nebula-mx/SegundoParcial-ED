package com.ecuaciones.diferenciales;

import com.ecuaciones.diferenciales.model.EcuationParser;
import com.ecuaciones.diferenciales.model.templates.ExpressionData;
import com.ecuaciones.diferenciales.model.solver.nonhomogeneous.UndeterminedCoeff;
import com.ecuaciones.diferenciales.model.solver.nonhomogeneous.UndeterminedCoeffResolver;
import com.ecuaciones.diferenciales.model.roots.Root;
import java.util.List;
import java.util.Map;
import java.util.Arrays;

public class QuickTest {
    public static void main(String[] args) throws Exception {
        System.out.println("=== TEST: y'' - 3y' + 2y = e^(x) ===\n");
        
        EcuationParser parser = new EcuationParser();
        ExpressionData data = parser.parse("y'' - 3*y' + 2*y = e^(x)");
        
        System.out.println("Orden: " + data.getOrder());
        System.out.println("g(x): " + data.getIndependentTerm().get("g(x)"));
        
        try {
            // Raíces de r^2 - 3r + 2 = 0 son r=1 y r=2
            List<Root> roots = Arrays.asList(
                new Root(1.0, 0.0, 1),
                new Root(2.0, 0.0, 1)
            );
            
            UndeterminedCoeff ucSolver = new UndeterminedCoeff(roots);
            System.out.println("baseUCTerms: " + ucSolver.getBaseUCTerms());
            System.out.println("ypStarTerms: " + ucSolver.getYpStarTerms());
            
            UndeterminedCoeffResolver resolver = new UndeterminedCoeffResolver(data, ucSolver);
            Map<String, Double> coeffs = resolver.resolveCoefficients();
            System.out.println("Coeficientes: " + coeffs);
            System.out.println("SUCCESS");
        } catch (Exception e) {
            System.out.println("ERROR: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
