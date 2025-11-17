import com.ecuaciones.diferenciales.Main;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.*;

public class TestEvaluateWithCI {
    public static void main(String[] args) throws Exception {
        System.out.println("════════════════════════════════════════════════════════════════");
        System.out.println("PRUEBA: Main.evaluate() con Condiciones Iniciales");
        System.out.println("════════════════════════════════════════════════════════════════");
        System.out.println();
        
        // Ecuación: y'' - 5y' + 6y = 0
        // Raíces: 3, 2
        // Solución general: y = C1*e^(3x) + C2*e^(2x)
        // Con CI: y(0)=1, y'(0)=2
        
        String ecuacion = "y'' - 5*y' + 6*y = 0";
        List<String> conditions = Arrays.asList("y(0)=1", "y'(0)=2");
        
        System.out.println("Ecuación: " + ecuacion);
        System.out.println("Condiciones Iniciales:");
        for (String ci : conditions) {
            System.out.println("  • " + ci);
        }
        System.out.println();
        
        // Llamar al método
        Map<String, Object> resultado = Main.evaluate(ecuacion, "AUTO", conditions);
        
        // Mostrar resultado
        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(resultado);
        System.out.println("Resultado JSON:");
        System.out.println(json);
        System.out.println();
        System.out.println("════════════════════════════════════════════════════════════════");
        
        // Verificar
        if (resultado.containsKey("constants")) {
            System.out.println("✅ ÉXITO: Constantes calculadas");
            Map<String, Object> constants = (Map<String, Object>) resultado.get("constants");
            for (Map.Entry<String, Object> e : constants.entrySet()) {
                System.out.println("   " + e.getKey() + " = " + e.getValue());
            }
        } else {
            System.out.println("⚠️ Sin constantes en el resultado");
        }
        
        if (resultado.containsKey("withInitialConditions")) {
            System.out.println("✅ Solución con CI aplicadas");
        }
    }
}
