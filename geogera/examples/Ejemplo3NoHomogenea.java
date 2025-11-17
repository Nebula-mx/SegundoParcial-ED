// Ejemplo 3: No-Homogénea con UC
// Ecuación: y'' + 4y = 8*cos(2*x)
// Esperado: y_p generada por UC

import com.ecuaciones.diferenciales.service.EquationSolverService;
import com.ecuaciones.diferenciales.dto.DifferentialEquationResponse;

public class Ejemplo3NoHomogenea {
    public static void main(String[] args) {
        EquationSolverService solver = new EquationSolverService();
        
        DifferentialEquationResponse response = solver.solve(
            "y'' + 4*y = 8*cos(2*x)",
            false,
            ""
        );
        
        System.out.println("=== EJEMPLO 3: No-Homogénea (UC) ===");
        System.out.println("Ecuación: y'' + 4y = 8*cos(2*x)");
        System.out.println("y_h: " + response.getHomogeneousSolution());
        System.out.println("y_p: " + response.getParticularSolution());
        System.out.println("y_general: " + response.getGeneralSolution());
    }
}
