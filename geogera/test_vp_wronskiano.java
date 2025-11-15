import com.ecuaciones.diferenciales.model.roots.Root;
import com.ecuaciones.diferenciales.model.variation.WronskianCalculator;
import com.ecuaciones.diferenciales.utils.SymbolicDifferentiator;
import java.util.List;

public class test_vp_wronskiano {
    public static void main(String[] args) {
        // Crear raíces complejas: 2i y -2i
        List<Root> roots = List.of(
            new Root(0, 2, true),      // 0 + 2i
            new Root(0, -2, true)      // 0 - 2i
        );
        
        WronskianCalculator wc = new WronskianCalculator(roots);
        List<String> fundamentalSet = wc.generateFundamentalSet();
        
        System.out.println("🔍 Conjunto Fundamental:");
        for (int i = 0; i < fundamentalSet.size(); i++) {
            System.out.println("   y_" + (i+1) + " = " + fundamentalSet.get(i));
        }
        
        // Probar derivadas directamente
        System.out.println("\n🔧 Prueba de Derivadas:");
        String[] testExprs = {"cos(2x)", "sin(2x)"};
        for (String expr : testExprs) {
            String deriv = SymbolicDifferentiator.differentiate(expr, 1);
            System.out.println("   d/dx[" + expr + "] = " + deriv);
        }
        
        // Obtener fórmula del Wronskiano
        System.out.println("\n📊 Matriz de Wronskiano:");
        String wronskianFormula = wc.calculateWronskianFormula();
        System.out.println(wronskianFormula);
    }
}
