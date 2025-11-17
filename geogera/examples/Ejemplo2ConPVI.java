// Ejemplo 2: Con Condiciones Iniciales (PVI)
// Ecuación: y'' - 5y' + 6y = 0
// Condiciones: y(0)=1, y'(0)=0
// Esperado: y = 1.0*e^(3x) - 0.5*e^(2x)

import com.ecuaciones.diferenciales.service.EquationSolverService;
import com.ecuaciones.diferenciales.dto.DifferentialEquationResponse;

public class Ejemplo2ConPVI {
    public static void main(String[] args) {
        EquationSolverService solver = new EquationSolverService();
        
        DifferentialEquationResponse response = solver.solve(
            "y'' - 5*y' + 6*y = 0",
            true,   // con PVI
            "y(0)=1, y'(0)=0"
        );
        
        System.out.println("=== EJEMPLO 2: Con Condiciones Iniciales ===");
        System.out.println("Ecuación: y'' - 5y' + 6y = 0");
        System.out.println("Condiciones: y(0)=1, y'(0)=0");
        System.out.println("Solución Particular: " + response.getFinalSolution());
        System.out.println("C1 = " + response.getSolvedConstants().get("C1"));
        System.out.println("C2 = " + response.getSolvedConstants().get("C2"));
    }
}
