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
     * Calcula multiplicidades checando si raíces anulan derivadas sucesivas
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
                
                if (Math.abs(coeff) < 1e-15) {
                    continue;
                }
                
                if (polyStr.length() > 0 && coeff > 0) polyStr.append("+");
                
                if (currentDegree == 0) {
                    polyStr.append(String.format("%.6f", coeff));
                } else if (currentDegree == 1) {
                    polyStr.append(String.format("%.6f", coeff)).append("*r");
                } else {
                    polyStr.append(String.format("%.6f", coeff)).append("*r^").append(currentDegree);
                }
            }
            
            // Validación: Asegurar que el polinomio no esté vacío
            if (polyStr.length() == 0) {
                roots.add(new Root(-1.0, 0.0, 1));
                return roots;
            }
            
            String polynomial = polyStr.toString();
            ExprEvaluator evaluator = new ExprEvaluator();
            
            // Resolver: Solve[polinomio == 0, r]
            String solveCmd = "Solve[" + polynomial + "==0, r]";
            IExpr result = evaluator.eval(solveCmd);
            
            // Parsear resultados
            if (result.isList()) {
                IAST list = (IAST) result;
                
                // Iterar desde índice 1 (el 0 es el símbolo "List")
                for (int i = 1; i < list.size(); i++) {
                    IExpr elem = list.get(i);
                    
                    // Cada elemento es una lista con estructura {Head, Rule}
                    if (elem instanceof IAST) {
                        IAST innerList = (IAST) elem;
                        // innerList.size() = 2: [0]=List head, [1]=Rule expr
                        if (innerList.size() >= 2) {
                            IExpr ruleExpr = innerList.get(1);  // Esto es la Rule (r->valor)
                            
                            // Procesar como Rule AST: Rule[r, valor]
                            if (ruleExpr instanceof IAST) {
                                IAST ruleAst = (IAST) ruleExpr;
                                // Rule tiene estructura: [0]=Rule head, [1]=variable, [2]=valor
                                if (ruleAst.size() >= 3) {
                                    IExpr valueExpr = ruleAst.get(2);  // El valor de la raíz
                                    Root root = parseSymjaRoot(valueExpr);
                                    
                                    // CRÍTICO: Calcular multiplicidad checando derivadas
                                    int multiplicity = calculateMultiplicityViaDerivatives(polynomial, root, evaluator, degree);
                                    roots.add(new Root(root.getReal(), root.getImaginary(), multiplicity));
                                }
                            }
                        }
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
     * Calcula la multiplicidad de una raíz checando cuántas derivadas anula
     * Si p(r0)=0, p'(r0)=0, p''(r0)=0 pero p'''(r0)≠0, entonces multiplicidad=3
     */
    private static int calculateMultiplicityViaDerivatives(String polynomial, Root root, ExprEvaluator evaluator, int maxDegree) {
        try {
            double r0Real = root.getReal();
            double r0Imag = root.getImaginary();
            
            // Para raíces reales
            if (Math.abs(r0Imag) < TOLERANCE) {
                String r0Str = String.valueOf(r0Real);
                int multiplicity = 0;  // Empieza en 0, se incrementa si anula cada derivada
                
                // Evaluar p, p', p'', p''', etc. en r0
                String currentPoly = polynomial;
                for (int deriv = 0; deriv <= maxDegree; deriv++) {
                    // Evaluar derivada en r0
                    String evalCmd = "N[" + currentPoly + " /. r -> " + r0Str + "]";
                    IExpr evalResult = evaluator.eval(evalCmd);
                    
                    double value = 0.0;
                    try {
                        value = Math.abs(evalResult.evalDouble());
                    } catch (Exception e) {
                        value = 0.0;
                    }
                    
                    // Si evaluación es ~0, la raíz anula esta derivada
                    if (value < TOLERANCE) {
                        multiplicity++;
                        // Calcular siguiente derivada solo si hay más para evaluar
                        if (deriv < maxDegree) {
                            currentPoly = "D[" + currentPoly + ", r]";
                        }
                    } else {
                        // Encontramos la multiplicidad
                        break;
                    }
                }
                
                return multiplicity;
            } else {
                // Para raíces complejas, por ahora retornar 1
                return 1;
            }
        } catch (Exception e) {
            return 1;
        }
    }
    
    /**
     * Parsea una raíz desde la expresión Symja
     */
    private static Root parseSymjaRoot(IExpr expr) {
        try {
            // Usar Symja para obtener numéricamente las partes real e imaginaria
            ExprEvaluator evaluator = new ExprEvaluator();
            String exprStr = expr.toString().trim();

            // Evaluar Re y Im numéricamente (maneja fracciones y Sqrt[] correctamente)
            IExpr re = evaluator.eval("N[Re[" + exprStr + "]]");
            IExpr im = evaluator.eval("N[Im[" + exprStr + "]]");

            double realPart = 0.0;
            double imagPart = 0.0;

            try {
                realPart = re.evalDouble();
            } catch (Exception ex) {
                try { realPart = Double.parseDouble(re.toString()); } catch (Exception ex2) { realPart = 0.0; }
            }

            try {
                imagPart = im.evalDouble();
            } catch (Exception ex) {
                try { imagPart = Double.parseDouble(im.toString()); } catch (Exception ex2) { imagPart = 0.0; }
            }

            return new Root(realPart, imagPart, 1);
        } catch (Exception e) {
            System.err.println("Error parseando raíz: " + expr.toString());
            e.printStackTrace();
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