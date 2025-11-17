// Ejemplo 1: Ecuación Homogénea Simple
// Ecuación: y'' - 5y' + 6y = 0
// Esperado: y = C1*e^(3x) + C2*e^(2x)

import com.ecuaciones.diferenciales.service.EquationSolverService;
import com.ecuaciones.diferenciales.dto.DifferentialEquationResponse;

public class Ejemplo1Homogenea {
    public static void main(String[] args) {
        EquationSolverService solver = new EquationSolverService();
        
        DifferentialEquationResponse response = solver.solve(
            "y'' - 5*y' + 6*y = 0",
            false,  // sin PVI
            ""
        );
        
        System.out.println("=== EJEMPLO 1: Ecuación Homogénea ===");
        System.out.println("Ecuación: y'' - 5y' + 6y = 0");
        System.out.println("Orden: " + response.getOrder());
        System.out.println("Solución: " + response.getHomogeneousSolution());
        System.out.println("Es Homogénea: " + response.isHomogeneous());
    }
}
