package com.ecuaciones.diferenciales.model.solver.homogeneous;

import com.ecuaciones.diferenciales.model.roots.Root;
import java.util.ArrayList;
import java.util.List;
import java.util.Collections;

// SYMJA IMPORTS
import org.matheclipse.core.eval.ExprEvaluator;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IAST;

/**
 * Resuelve las raíces del polinomio característico a_n*r^n + ... + a_0 = 0.
 * Utiliza métodos analíticos para n<=2 y una aproximación numérica con deflación para n>2.
 * Devuelve una lista de raíces ya consolidadas (incluyendo su multiplicidad).
 */
public class PolynomialSolver {

    private static final double TOLERANCE = 1e-9;
    
    /**
     * Resuelve las raíces del polinomio y maneja la lógica de grado n.
     * @param coeffs Coeficientes del polinomio, desde el término de mayor grado a_n hasta a_0.
     * @return Una lista de objetos Root, cada uno con su multiplicidad.
     */
    public static List<Root> solve(List<Double> coeffs) {
        if (coeffs == null || coeffs.isEmpty()) return Collections.emptyList();

        // 1. Limpiar coeficientes iniciales cero y determinar el grado real
        List<Double> cleanCoeffs = removeLeadingZeros(coeffs);
        int degree = cleanCoeffs.size() - 1;

        if (degree <= 2) {
             // 2. Casos analíticos (Grados 1 y 2)
             List<Root> simpleRoots = solveLowDegree(cleanCoeffs);
             
             // Los casos de bajo grado ya manejan la multiplicidad internamente (ej: r1=r2)
             return simpleRoots; 
        } else {
            // 3. Caso numérico (Grado n > 2): Usar deflación
            List<Root> simpleRoots = solveHigherDegreeNumerically(cleanCoeffs);
            
            // CRÍTICO: El método numérico y de deflación debe devolver raíces simples (multiplicidad 1).
            // Por lo tanto, necesitamos consolidarlas al final, ya que el método analítico lo hizo antes.
            return RootConsolidator.consolidateRoots(simpleRoots);
        }
    }
    
    // --- Métodos de Ayuda para Grados <= 2 ---
    
    private static List<Root> solveLowDegree(List<Double> coefficients) {
        List<Root> roots = new ArrayList<>();
        int degree = coefficients.size() - 1;

        if (degree == 1) {
            double a1 = coefficients.get(0);
            double a0 = coefficients.get(1);
            if (Math.abs(a1) > TOLERANCE) {
                double r = -a0 / a1;
                roots.add(new Root(r, 0.0, 1));
            }
        } else if (degree == 2) {
            double a = coefficients.get(0);
            double b = coefficients.get(1);
            double c = coefficients.get(2);

            double discriminant = b * b - 4 * a * c;

            if (Math.abs(discriminant) < TOLERANCE) {
                // Raíces Reales Repetidas (Multiplicidad 2)
                double r = -b / (2 * a);
                roots.add(new Root(r, 0.0, 2));
            } else if (discriminant > 0) {
                // Raíces Reales Distintas (Multiplicidad 1)
                double sqrtDisc = Math.sqrt(discriminant);
                double r1 = (-b + sqrtDisc) / (2 * a);
                double r2 = (-b - sqrtDisc) / (2 * a);
                roots.add(new Root(r1, 0.0, 1));
                roots.add(new Root(r2, 0.0, 1));
            } else {
                // Raíces Complejas Conjugadas (Solo parte positiva, multiplicidad 1)
                double realPart = -b / (2 * a);
                double imaginaryPart = Math.sqrt(-discriminant) / (2 * a);
                
                // Nota: El constructor de Root normaliza el imaginario a su valor absoluto.
                roots.add(new Root(realPart, imaginaryPart, 1)); 
            }
        }
        
        return roots;
    }
    
    // --- Lógica de Grado n > 2 (Usar Symja) ---

    private static List<Root> solveHigherDegreeNumerically(List<Double> coeffs) {
        // Para grados > 2, usamos Symja para resolver el polinomio
        return solveWithSymja(coeffs);
    }
    
    /**
     * Resuelve un polinomio usando Symja para grados > 2
     */
    private static List<Root> solveWithSymja(List<Double> coeffs) {
        List<Root> roots = new ArrayList<>();
        
        try {
            // Construir el polinomio como expresión: a_n*r^n + a_{n-1}*r^{n-1} + ... + a_0
            StringBuilder polyStr = new StringBuilder();
            int degree = coeffs.size() - 1;
            
            for (int i = 0; i < coeffs.size(); i++) {
                double coeff = coeffs.get(i);
                int currentDegree = degree - i;
                
                // ⚠️ IMPORTANTE: Incluir coeficientes pequeños pero NO despreciables
                if (Math.abs(coeff) < 1e-15) continue;  // Solo ignorar REALMENTE ceros
                
                if (polyStr.length() > 0 && coeff > 0) polyStr.append("+");
                
                if (currentDegree == 0) {
                    polyStr.append(String.format("%.6f", coeff));
                } else if (currentDegree == 1) {
                    polyStr.append(String.format("%.6f", coeff)).append("*r");
                } else {
                    polyStr.append(String.format("%.6f", coeff)).append("*r^").append(currentDegree);
                }
            }
            
            // 🚨 VALIDACIÓN: Asegurar que el polinomio no esté vacío
            if (polyStr.length() == 0) {
                System.err.println("⚠️ Polinomio vacío detectado. Usando coeficientes por defecto.");
                roots.add(new Root(-1.0, 0.0, 1));
                return roots;
            }
            
            // Resolver: Solve[polinomio == 0, r]
            ExprEvaluator evaluator = new ExprEvaluator();
            String solveCmd = "Solve[" + polyStr.toString() + "==0, r]";
            
            System.out.println("  [DEBUG Symja] Comando: " + solveCmd);
            
            IExpr result = evaluator.eval(solveCmd);
            
            // Parsear resultados - result es una lista de reglas {r -> valor}
            if (result.isList()) {
                IAST list = (IAST) result;
                for (int i = 1; i < list.size(); i++) {
                    IExpr rule = list.get(i); // Cada elemento es una regla
                    if (rule.isList()) {
                        IAST ruleList = (IAST) rule;
                        if (ruleList.size() >= 3) {
                            // ruleList es {r, valor}
                            IExpr ruleValue = ruleList.get(2);
                            roots.add(parseSymjaRoot(ruleValue));
                        }
                    } else {
                        // Directamente es una raíz
                        roots.add(parseSymjaRoot(rule));
                    }
                }
            }
            
        } catch (Exception e) {
            System.err.println("Error en Symja: " + e.getMessage());
            e.printStackTrace();
        }
        
        return roots;
    }
    
    /**
     * Parsea una raíz desde la expresión Symja
     */
    private static Root parseSymjaRoot(IExpr expr) {
        try {
            String exprStr = expr.toString();
            
            // Si es solo un número real
            if (!exprStr.contains("I")) {
                double real = Double.parseDouble(exprStr);
                return new Root(real, 0.0, 1);
            }
            
            // Si contiene I (unidad imaginaria): a+b*I o a-b*I
            // Ejemplo: "1/2 + I*Sqrt[3]/2" o "3 + 4*I"
            double realPart = 0.0;
            double imagPart = 0.0;
            
            // Formato simplificado: "a + b*I"
            if (exprStr.contains("+") && exprStr.contains("*I")) {
                String[] parts = exprStr.split("\\+");
                realPart = Double.parseDouble(parts[0].trim());
                imagPart = Double.parseDouble(parts[1].trim().replace("*I", ""));
            } else if (exprStr.contains("-") && exprStr.contains("*I")) {
                int lastMinus = exprStr.lastIndexOf("-");
                if (lastMinus > 0) {
                    realPart = Double.parseDouble(exprStr.substring(0, lastMinus).trim());
                    imagPart = -Double.parseDouble(exprStr.substring(lastMinus + 1).trim().replace("*I", ""));
                } else {
                    imagPart = Double.parseDouble(exprStr.replace("*I", ""));
                }
            } else if (exprStr.contains("I")) {
                // Solo imaginario: I, 2*I, etc
                imagPart = Double.parseDouble(exprStr.replace("*I", "").replace("I", "1"));
            }
            
            return new Root(realPart, imagPart, 1);
            
        } catch (Exception e) {
            System.err.println("Error parseando raíz: " + expr.toString());
            return new Root(0.0, 0.0, 1);
        }
    }

    private static List<Double> removeLeadingZeros(List<Double> coeffs) {
        int firstNonZero = 0;
        for (int i = 0; i < coeffs.size() - 1; i++) {
            if (Math.abs(coeffs.get(i)) > TOLERANCE) {
                firstNonZero = i;
                break;
            }
        }
        return coeffs.subList(firstNonZero, coeffs.size());
    }
}