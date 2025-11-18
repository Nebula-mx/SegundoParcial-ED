// Test simple para verificar el cálculo del PVI
public class test_pvi {
    public static void main(String[] args) {
        // Para y' + 2y = 4, con y(0) = 1
        
        // Solución general esperada: y(x) = C1*e^(-2x) + 2
        // Funciones base: [e^(-2x), 1]
        
        // Sistema PVI:
        // y(0) = 1
        // Entonces: C1*e^(0) + 2 = 1
        // C1*1 + 2 = 1
        // C1 = -1
        
        System.out.println("Expected: C1 = -1");
        
        // Pero si el programa calcula C1 = 1, entonces:
        // C1*e^(0) = 1
        // 1*1 = 1
        // C1 = 1
        
        // Esto significa que o:
        // 1. La función base extraída es SOLO e^(-2x) (sin la constante 1)
        // 2. La solución general que se pasa es solo y(x) = C1*e^(-2x)
        // 3. La solución particular 2 no se está considerando en el PVI
    }
}
