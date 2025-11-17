// Ejemplo 4: Orden Superior
// Ecuación: y''' - y' = x^2
// Esperado: y_h = C1 + C2*e^x + C3*e^(-x), y_p = -0.3333*x^3

import com.ecuaciones.diferenciales.service.EquationSolverService;
import com.ecuaciones.diferenciales.dto.DifferentialEquationResponse;

public class Ejemplo4OrdenSuperior {
    public static void main(String[] args) {
        EquationSolverService solver = new EquationSolverService();
        
        DifferentialEquationResponse response = solver.solve(
            "y''' - y' = x^2",
            false,
            ""
        );
        
        System.out.println("=== EJEMPLO 4: Orden Superior ===");
        System.out.println("Ecuación: y''' - y' = x^2");
        System.out.println("Orden: " + response.getOrder());
        System.out.println("y_h: " + response.getHomogeneousSolution());
        System.out.println("y_p: " + response.getParticularSolution());
        System.out.println("y_general: " + response.getGeneralSolution());
    }
}
