import java.util.*;

public class test_pvi_debug {
    public static void main(String[] args) {
        // Test para verificar el PVI con solución particular constante
        // Ecuación: y' + 2y = 4
        // Condición: y(0) = 1
        
        // Solución esperada:
        // 1. Raíz: -2
        // 2. Solución homogénea: C1*e^(-2*x)
        // 3. Solución particular: 2
        // 4. Solución general: y(x) = C1*e^(-2*x) + 2
        // 5. Con y(0) = 1: C1*e^(0) + 2 = 1 → C1*1 + 2 = 1 → C1 = -1
        // 6. Solución particular del PVI: y(x) = -1*e^(-2*x) + 2 = 2 - e^(-2*x)
        
        System.out.println("=== TEST PVI: y' + 2y = 4, y(0)=1 ===\n");
        System.out.println("Esperado:");
        System.out.println("  C1 = -1.0");
        System.out.println("  Solución: y(x) = 2 - e^(-2*x)");
        System.out.println("\nLlamando al solver...\n");
        
        // Este es un placeholder - necesitamos ejecutar el programa real
        // El programa se llama via Main.java con parámetros JSON o similar
    }
}
