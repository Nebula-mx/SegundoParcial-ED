import org.matheclipse.core.eval.ExprEvaluator;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IAST;

public class TestSymja {
    public static void main(String[] args) {
        try {
            ExprEvaluator evaluator = new ExprEvaluator();
            
            // Prueba: Resolver r^3 - 1 = 0
            String polyCmd = "Solve[r^3 - 1 == 0, r]";
            System.out.println("Comando: " + polyCmd);
            
            IExpr result = evaluator.eval(polyCmd);
            System.out.println("Resultado completo: " + result);
            System.out.println("Tipo: " + result.getClass().getSimpleName());
            
            if (result.isList()) {
                IAST list = (IAST) result;
                System.out.println("Es una lista con " + list.size() + " elementos");
                
                for (int i = 0; i < list.size(); i++) {
                    System.out.println("  [" + i + "]: " + list.get(i));
                }
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
