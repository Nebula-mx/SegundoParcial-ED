import com.ecuaciones.diferenciales.utils.SymbolicDifferentiator;

public class TestDerivative {
    public static void main(String[] args) {
        String expr = "C1*e^(3*x) + C2*e^(2*x)";
        System.out.println("Original: " + expr);
        
        String der1 = SymbolicDifferentiator.differentiate(expr, 1);
        System.out.println("Primera derivada: " + der1);
        
        String der2 = SymbolicDifferentiator.differentiate(expr, 2);
        System.out.println("Segunda derivada: " + der2);
    }
}
