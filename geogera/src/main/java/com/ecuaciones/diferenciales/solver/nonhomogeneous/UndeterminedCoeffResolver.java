package com.ecuaciones.diferenciales.solver.nonhomogeneous;

import com.ecuaciones.diferenciales.model.templates.ExpressionData;
import com.ecuaciones.diferenciales.utils.LinearSystemSolver; 
import com.ecuaciones.diferenciales.utils.SymbolicDifferentiator;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

/**
 * Resuelve los coeficientes de la forma y_p* (A, B, C, ...) y genera el sistema lineal A|b.
 * Utiliza listas separadas para las filas (base) y las columnas (propuesta y_p*).
 */
public class UndeterminedCoeffResolver {

    private final ExpressionData data;
    // --- LISTAS CORREGIDAS ---
    private final List<String> baseUCTerms; // Para FILAS (i) y vector b
    private final List<String> ypStarTerms; // Para COLUMNAS (j)
    // -------------------------
    private final List<String> coeffNames; 
    private final Double[] coefficients; 
    private final int order;
    private final String gX;
    
    private static final double TOLERANCE = 1e-9;
    private static final String MARKER = "@@@"; 

    public UndeterminedCoeffResolver(ExpressionData data, UndeterminedCoeff ucSolver) {
        this.data = data;
        this.coefficients = data.getCoefficients();
        this.order = data.getOrder();
        this.gX = data.getIndependentTerm().get("g(x)");
        
        // --- ASIGNACIÓN CORREGIDA ---
        this.coeffNames = ucSolver.getCoeffNames(); 
        this.baseUCTerms = ucSolver.getBaseUCTerms(); // Términos Base (Filas)
        this.ypStarTerms = ucSolver.getYpStarTerms(); // Términos Propuestos (Columnas)
    }

    // ----------------------- FUNCIÓN DE EXTRACCIÓN CENTRAL (ROBUSTA) --------------------------
    
    /**
     * Función auxiliar robusta que aísla y parsea el coeficiente numérico de un término funcional específico.
     */
    private double getRobustExtractedCoeff(String expression, String functionalTerm) {
        
        // Simplificar ambas cadenas para una comparación consistente
        String normalizedExpr = SymbolicDifferentiator.simplify(expression); 
        String normalizedTerm = SymbolicDifferentiator.simplify(functionalTerm);
        
        if (normalizedExpr.equals("0")) return 0.0;
        
        // 1. Dividir la expresión por sumas/restas
        String[] terms = normalizedExpr.split("(?=[\\+\\-])");
        double accumulatedCoeff = 0.0;

        for (String term : terms) {
            
            String cleanTerm = term.replaceAll("^\\+", "").trim(); 
            if (cleanTerm.isEmpty()) continue;
            
            // 2a. Coincidencia Exacta (para C = +/-1)
            // (Ej: cleanTerm="cos(2x)" y normalizedTerm="cos(2x)")
            if (cleanTerm.equals(normalizedTerm)) {
                accumulatedCoeff += (term.startsWith("-") ? -1.0 : 1.0);
                continue;
            }

            // 2b. Coincidencia con Coeficiente (C * T(x) o T(x) * C)
            // Patrón para buscar: (* o ^) [normalizedTerm] (* o $)
            // Usamos quote para escapar caracteres especiales en el término (como '^')
            String patternString = "(^|\\*)" + Pattern.quote(normalizedTerm) + "(\\*|$)";
            
            Matcher m = Pattern.compile(patternString).matcher(cleanTerm);
            
            if (m.find()) {
                // Reemplazar el término funcional encontrado por un marcador
                String tempTerm = m.replaceAll(MARKER); 

                // Extraer el coeficiente: Quitar el marcador y cualquier '*' que lo rodee
                String coeffStr = tempTerm.replaceAll("\\*?(" + Pattern.quote(MARKER) + ")\\*?", "")
                                          .replaceAll("^\\+|\\*$", "").trim();
                
                double currentCoeff = 0.0;

                if (coeffStr.isEmpty()) {
                    currentCoeff = 1.0;
                } else if (coeffStr.equals("-")) {
                    currentCoeff = -1.0;
                } else if (coeffStr.equals("+")) {
                    currentCoeff = 1.0;
                } else {
                    try {
                        currentCoeff = Double.parseDouble(coeffStr);
                    } catch (NumberFormatException e) {
                        // Si queda "x", "cos", etc., no es una coincidencia válida
                        currentCoeff = 0.0; 
                    }
                }
                
                if (term.startsWith("-") && currentCoeff > 0) {
                     currentCoeff *= -1;
                }

                accumulatedCoeff += currentCoeff;
            }
        }
        
        return accumulatedCoeff;
    }
    
    // ----------------------- MÉTODO PRINCIPAL DE RESOLUCIÓN --------------------------

    public Map<String, Double> resolveCoefficients() {
        
        int numCoeffs = coeffNames.size(); // Nombres (A, B...)
        int numCols = ypStarTerms.size();  // Columnas (Términos Yp*)
        int numRows = baseUCTerms.size();  // Filas (Términos Base)
        
        // El número de Coeficientes (A, B) debe ser igual al número de Términos de Columna (yp*)
        // El número de Filas (Términos Base) debe ser igual al número de Columnas (Términos Yp*)
        if (numCoeffs != numCols || numRows != numCols || numCoeffs == 0) {
             throw new IllegalStateException(
                 "Error: El sistema A|b está mal construido. Las dimensiones no coinciden. \n" +
                 "Coeficientes (Nombres): " + numCoeffs + "\n" +
                 "Términos Yp* (Columnas): " + numCols + "\n" +
                 "Términos Base (Filas): " + numRows
             );
        }

        double[][] matrixA = new double[numRows][numCols];
        double[] vectorB = new double[numRows];
        
        // 3. Llenar la Matriz A y el Vector B
        // i = Fila (Término Base)
        for (int i = 0; i < numRows; i++) { 
            String baseTerm_i = baseUCTerms.get(i); // Ej: cos(2x)
            
            // j = Columna (Término Yp*)
            for (int j = 0; j < numCols; j++) { 
                
                String ypTerm_j = ypStarTerms.get(j); // Ej: x*cos(2x)
                double totalCoefficientOfTerm_base = 0.0;
                
                // Aplicar el operador L[ypTerm_j]
                for (int k = 0; k <= order; k++) { 
                    double a_k = coefficients[order - k]; 
                    
                    String derived_tj = SymbolicDifferentiator.calculateDerivative(ypTerm_j, k);
                    
                    // Extraer el coeficiente del término base (FILA i) de la derivada (COLUMNA j)
                    double functionalCoeff = getRobustExtractedCoeff(derived_tj, baseTerm_i);
                    
                    totalCoefficientOfTerm_base += a_k * functionalCoeff;
                }
                
                matrixA[i][j] = totalCoefficientOfTerm_base;
            }
            
            // Llenar el Vector B
            // Extraer el coeficiente del término base (FILA i) de g(x)
            vectorB[i] = getRobustExtractedCoeff(gX, baseTerm_i);
        }

        // **********************************************
        // ******* DEPURACIÓN: IMPRIMIR MATRIZ A Y VECTOR B *******
        // **********************************************
        System.out.println("\n--- DEBUG: Sistema Lineal A|b ---");
        System.out.println("Coeficientes (Columnas/Nombres): " + coeffNames);
        System.out.println("Términos Yp* (Columnas): " + ypStarTerms); 
        System.out.println("Términos Base (Filas): " + baseUCTerms); 
        for (int i = 0; i < numRows; i++) {
            System.out.printf("Fila %d (Termino: %s) [", i, baseUCTerms.get(i));
            for (int j = 0; j < numCols; j++) {
                System.out.printf("%.4f\t", matrixA[i][j]);
            }
            System.out.printf("] | b = %.4f\n", vectorB[i]);
        }
        System.out.println("---------------------------------");
        // **********************************************

        // 4. Resolver el sistema lineal
        List<List<Double>> listA = new ArrayList<>();
        for (double[] row : matrixA) {
            List<Double> listRow = new ArrayList<>();
            for (double val : row) listRow.add(val);
            listA.add(listRow);
        }
        List<Double> listB = new ArrayList<>();
        for (double val : vectorB) listB.add(val);
        
        double[] solutions = LinearSystemSolver.solve(listA, listB);

        // 5. Mapear resultados
        Map<String, Double> solvedCoeffs = new HashMap<>();
        for (int i = 0; i < solutions.length; i++) {
            double solution = Math.abs(solutions[i]) < TOLERANCE ? 0.0 : solutions[i]; 
            solvedCoeffs.put(coeffNames.get(i), solution);
        }
        
        return solvedCoeffs;
    }
    
    public List<String> getSolvedCoeffNames() {
        return coeffNames;
    }
}