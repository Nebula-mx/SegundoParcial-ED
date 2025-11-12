package com.ecuaciones.diferenciales.utils;

import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.AbstractMap; // Importación para Map.Entry

/**
 * Utilidad para calcular la derivada simbólica de las funciones base de la EDO.
 * Soporta e^(ax), x^m, cos(bx), sin(bx) y la Regla del Producto (P(x) * f(x)).
 * Versión V3: Lógica de descomposición robusta.
 */
public class SymbolicDifferentiator {

    private static final double TOLERANCE = 1e-9;
    
    // Patrones para identificar funciones (anclados al inicio ^ y fin $ de la cadena)
    // ^ = inicio, $ = fin
    private static final Pattern EXP_PATTERN = Pattern.compile("^e\\^\\(?([\\+\\-]?\\d*\\.?\\d*)x\\)?$");
    private static final Pattern TRIG_PATTERN_COS = Pattern.compile("^cos\\(?([\\+\\-]?\\d*\\.?\\d*)x\\)?$");
    private static final Pattern TRIG_PATTERN_SIN = Pattern.compile("^sin\\(?([\\+\\-]?\\d*\\.?\\d*)x\\)?$");
    private static final Pattern X_PATTERN = Pattern.compile("^x$");
    private static final Pattern X_POWER_PATTERN = Pattern.compile("^x\\^(\\d+)$");
    
    // Patron para extraer C*x^m (el polinomio P(x)) o solo C.
    private static final Pattern POLY_COEFF_PATTERN = 
        Pattern.compile("^([\\+\\-]?)(([0-9]*\\.?[0-9]+)\\*?)?(x(?:\\^(\\d+))?)?$");
    
    
    public static String calculateDerivative(String y, int order) {
        String currentY = simplify(y.trim()); 
        
        for (int i = 0; i < order; i++) {
            currentY = deriveSingleStep(currentY);
            currentY = simplify(currentY); 
            if (currentY.equals("0")) break;
        }
        
        return currentY.replaceAll("^\\+", "").trim();
    }

    // Paso 1: Divide la expresión en términos aditivos (ej: 5x*cos(x) + 2x)
    private static String deriveSingleStep(String y) {
        
        List<String> terms = new ArrayList<>();
        // Divide en + o - que NO están dentro de paréntesis (ej: e^(-2x) no se rompe)
        Matcher m = Pattern.compile("[\\+\\-](?![^\\(]*\\))").matcher(y);
        
        int lastStart = 0;
        String firstSign = "";
        if (y.startsWith("-")) {
             firstSign = "-";
             lastStart = 1;
        } else if (y.startsWith("+")) {
             lastStart = 1;
        }

        while (m.find(lastStart)) {
            String term = y.substring(lastStart, m.start());
            if (!term.isEmpty()) {
                terms.add(firstSign + term);
            }
            firstSign = m.group(); 
            lastStart = m.end(); 
        }
        String lastTerm = y.substring(lastStart);
        if (!lastTerm.isEmpty()) {
             terms.add(firstSign + lastTerm);
        }

        if(terms.isEmpty() && !y.isEmpty()) terms.add(y);

        // Derivar cada término
        StringBuilder derivedResult = new StringBuilder();
        for (String term : terms) {
            String derivedTerm = deriveTerm(term.replaceAll("^\\+", ""));
            if (derivedTerm.equals("0")) continue;
            
            if (derivedResult.length() > 0 && !derivedTerm.startsWith("-")) {
                derivedResult.append("+");
            }
            derivedResult.append(derivedTerm);
        }

        return derivedResult.length() == 0 ? "0" : derivedResult.toString();
    }
    
    /**
     * Paso 2: Deriva un TÉRMINO ÚNICO (ej: 5*x*cos(2x))
     */
    private static String deriveTerm(String term) {
        if (term.isEmpty() || term.equals("+") || term.equals("-")) return "0";
        
        // Descomponer C * P(x) * F(x)
        TermStructure ts = getFunctionStructure(term);

        // Si es solo una constante (C), la derivada es 0
        if (ts.Px.equals("1") && ts.Fx.equals("1")) {
             return "0"; 
        }
        
        // Aplicar Regla del Producto: d/dx [C * P(x) * F(x)] = C * [P'(x)F(x) + P(x)F'(x)]
        
        String P_prime = deriveSimpleFunction(ts.Px); // Derivada de x^m
        String F_prime = deriveSimpleFunction(ts.Fx); // Derivada de e^ax*cos(bx)
        
        String term1 = simplifyProduct(P_prime, ts.Fx); // P'*F
        String term2 = simplifyProduct(ts.Px, F_prime); // P*F'
        
        String combined = sumStrings(term1, term2);

        // Aplicar el coeficiente C al resultado final
        return formatTerm(ts.C, combined);
    }
    
    // Estructura para C * Px * Fx
    private static class TermStructure {
        double C = 1.0;
        String Px = "1"; // Polinomial (x^m)
        String Fx = "1"; // Función (e^ax*cos(bx)...)
    }
    
    /**
     * Paso 3: Descompone un término (ej: -5*x*e^2x) en:
     * C = -5.0
     * Px = "x"
     * Fx = "e^2x"
     */
    private static TermStructure getFunctionStructure(String term) {
        TermStructure ts = new TermStructure();
        String remainingTerm = term;

        // 1. Extraer el Coeficiente Numérico (C)
        // Busca un número al inicio (con signo) que esté seguido de *
        Matcher mCoeff = Pattern.compile("^([\\+\\-]?\\d*\\.?\\d+)(\\*|(?=[a-z]))").matcher(remainingTerm);
        
        if (mCoeff.find() && !mCoeff.group(1).isEmpty()) {
            String coeffStr = mCoeff.group(1);
            if (coeffStr.equals("+")) {
                ts.C = 1.0;
            } else if (coeffStr.equals("-")) {
                ts.C = -1.0;
            } else {
                try {
                    ts.C = Double.parseDouble(coeffStr);
                } catch (NumberFormatException e) { ts.C = 1.0; }
            }
            // Quitar el coeficiente del término
            remainingTerm = remainingTerm.substring(mCoeff.end(1)).replaceAll("^\\*", "");
        } else if (remainingTerm.startsWith("-")) {
             // Manejar el caso -x*cos(x)
             ts.C = -1.0;
             remainingTerm = remainingTerm.substring(1).replaceAll("^\\*", "");
        } else if (remainingTerm.startsWith("+")) {
             remainingTerm = remainingTerm.substring(1).replaceAll("^\\*", "");
        }


        // 2. Extraer la parte Polinomial (Px = x^m)
        // Buscar x o x^n al inicio del término restante
        Matcher mPoly = Pattern.compile("^(x(?:\\^\\d+)?)(\\*|$)").matcher(remainingTerm);
        if (mPoly.find()) {
            ts.Px = mPoly.group(1); // ej: "x" o "x^2"
            // Quitar el polinomio del término
            remainingTerm = remainingTerm.substring(mPoly.end(1)).replaceAll("^\\*|\\*$", "");
        }

        // 3. Lo que queda es la Función (Fx)
        if (remainingTerm.isEmpty()) {
            // Si no quedó nada (ej, el término era solo "5*x"), Fx es 1
            ts.Fx = "1";
        } else {
            ts.Fx = remainingTerm;
        }
        
        return ts;
    }
    

    /**
     * Paso 4: Deriva una función simple (e^ax, x^m, cos(bx), sin(bx), o un producto e*trig)
     */
    private static String deriveSimpleFunction(String f) {
        if (f.equals("1") || f.isEmpty()) return "0";
        
        // --- Derivadas Simples (Deben ser ancladas ^$) ---

        if (X_PATTERN.matcher(f).matches()) return "1"; // x

        Matcher mExp = EXP_PATTERN.matcher(f); // e^ax
        if (mExp.matches()) { 
             double a = parseCoeff(mExp.group(1));
             return formatCoeff(a) + "*" + f; 
        }

        Matcher mCos = TRIG_PATTERN_COS.matcher(f); // cos(bx)
        if (mCos.matches()) { 
             double b = parseCoeff(mCos.group(1));
             String argument = (mCos.group(1) == null || mCos.group(1).isEmpty() ? "" : mCos.group(1)) + "x";
             double coeffValue = -b;
             String coeff = formatCoeff(coeffValue);
             return coeff + "*" + "sin(" + argument + ")"; 
        }
        
        Matcher mSin = TRIG_PATTERN_SIN.matcher(f); // sin(bx)
        if (mSin.matches()) { 
             double b = parseCoeff(mSin.group(1));
             String argument = (mSin.group(1) == null || mSin.group(1).isEmpty() ? "" : mSin.group(1)) + "x";
             double coeffValue = b;
             String coeff = formatCoeff(coeffValue);
             return coeff + "*" + "cos(" + argument + ")";
        }
        
        Matcher mXPower = X_POWER_PATTERN.matcher(f); // x^m
        if (mXPower.matches()) {
            int m_val = Integer.parseInt(mXPower.group(1));
            if (m_val == 0) return "0"; 
            
            int newPower = m_val - 1;
            String coeffStr = formatCoeff((double)m_val);
            String newXPart = newPower == 0 ? "1" : (newPower == 1 ? "x" : "x^" + newPower);
            
            return simplifyProduct(coeffStr, newXPart);
        }
        
        // --- Caso Producto/Composición (ej: e^ax*cos(bx)) ---
        
        if (f.contains("e^") && (f.contains("cos(") || f.contains("sin("))) {
             
             String f1, f2;
             // Re-usamos el patrón de find()
             Matcher mExpFull = Pattern.compile("e\\^\\(?([\\+\\-]?\\d*\\.?\\d*)x\\)?").matcher(f);
             if (mExpFull.find()) {
                  f1 = f.substring(mExpFull.start(), mExpFull.end());
                  f2 = f.replace(f1, "").replaceAll("^\\*|\\*$", "");
             } else {
                 return "d/dx(" + f + ")";
             }
             
             String f1_prime = deriveSimpleFunction(f1);
             String f2_prime = deriveSimpleFunction(f2);

             String term1 = simplifyProduct(f1_prime, f2);
             String term2 = simplifyProduct(f1, f2_prime);
             
             return sumStrings(term1, term2);
        }
        
        System.err.println("WARN: SymbolicDifferentiator no pudo derivar el término: " + f);
        return "d/dx(" + f + ")"; // Fallback
    }
    
    /**
     * Extrae el coeficiente C del polinomio Px y deja solo la parte x^m (o 1).
     * Este método es un PLACHOLDER y depende de POLY_COEFF_PATTERN.
     */
    private static Map.Entry<Double, String> extractCoeffFromPolynomial(String Px) {
         Px = Px.replaceAll("^\\*|\\*$", "").trim();
         
         if (Px.equals("x")) {
             return new AbstractMap.SimpleEntry<>(1.0, "x");
         } else if (Px.equals("-x")) {
             return new AbstractMap.SimpleEntry<>(-1.0, "x");
         }

         Matcher m = POLY_COEFF_PATTERN.matcher(Px);
         double C = 1.0;
         String remainingX = "1";
         
         if (m.matches()) {
             String sign = m.group(1); 
             String coeffMag = m.group(3); 
             String xPart = m.group(4); 
             
             if (coeffMag != null && !coeffMag.isEmpty()) {
                 try {
                     C = Double.parseDouble(coeffMag);
                 } catch (NumberFormatException e) { C = 1.0; }
             }
             
             if (sign != null && sign.equals("-")) {
                 C *= -1;
             }
             
             if (xPart != null && !xPart.isEmpty()) {
                 remainingX = xPart;
             }
             
         } else if (!Px.equals("1") && !Px.isEmpty()) {
             try {
                 C = Double.parseDouble(Px);
                 remainingX = "1";
             } catch (NumberFormatException e) {
                 // No es un polinomio simple
             }
         }
         
         return new AbstractMap.SimpleEntry<>(C, remainingX);
    }

    private static String simplifyProduct(String f, String g) {
        if (f.equals("0") || g.equals("0")) return "0";
        if (f.equals("1")) return g;
        if (g.equals("1")) return f;
        
        if (f.equals("-")) return "-" + g;
        if (g.equals("-")) return "-" + f;
        
        try {
            double c1 = Double.parseDouble(f);
            double c2 = Double.parseDouble(g);
            return formatCoeff(c1 * c2);
        } catch (NumberFormatException e) {
            // Ignorar
        }

        return f + "*" + g;
    }

    private static String sumStrings(String s1, String s2) {
        if (s1.equals("0")) return s2;
        if (s2.equals("0")) return s1;
        
        String result = s1;
        if (!s2.startsWith("-")) result += "+";
        result += s2;
        
        return result;
    }
    
    private static String formatTerm(double C, String f) {
        if (f.equals("0")) return "0";
        if (Math.abs(C) < TOLERANCE) return "0";
        
        String C_str = formatCoeff(C); 
        
        String formattedF = f;
        // Envolver en paréntesis si C no es 1 Y f es una suma
        if ((f.contains("+") || (f.contains("-") && !f.startsWith("-") && !f.startsWith("(-"))) && 
            Math.abs(C - 1.0) > TOLERANCE && Math.abs(C + 1.0) > TOLERANCE) {
            formattedF = "(" + f + ")";
        }
        
        if (C_str.isEmpty()) return formattedF; 
        if (C_str.equals("-")) return "-" + formattedF; 
        if (C_str.equals("0")) return "0";
        
        return C_str + "*" + formattedF;
    }

    public static String simplify(String expression) {
        // Normaliza a x*y*z (sin espacios)
        String simplified = expression.replaceAll("\\s", "")
                         .replaceAll("\\+-", "-")
                         .replaceAll("^\\+", "")
                         .replaceAll("\\*\\+", "*");
        
        // No insertar * dentro de paréntesis
        simplified = simplified.replaceAll("(\\d(?:\\.\\d+)?)(\\(|e|x|c|s)(?![^\\(]*\\))", "$1*$2");
        simplified = simplified.replaceAll("([a-zA-Z\\)])(\\d)(?![^\\(]*\\))", "$1*$2");
        simplified = simplified.replaceAll("([x\\)])(\\()", "$1*$2"); 
        simplified = simplified.replaceAll("([a-zA-Z\\)])(x)(?![^\\(]*\\))", "$1*$2"); 

        
        simplified = simplified.replaceAll("1\\*([a-zA-Z\\(])", "$1"); 
        simplified = simplified.replaceAll("\\*1(?![0-9])", ""); 
        simplified = simplified.replaceAll("e\\^1x", "e^x");
        simplified = simplified.replaceAll("e\\^-1x", "e^-x");
        simplified = simplified.replaceAll("^-1\\*", "-"); 
        simplified = simplified.replaceAll("([\\+\\-])1\\*([a-zA-Z\\(])", "$1$2"); 
        
        simplified = simplified.replaceAll("\\*\\*", "*");
        
        return simplified.replaceAll("^\\*|\\*$", ""); 
    }
    
    private static double parseCoeff(String coeffStr) {
        coeffStr = coeffStr.replaceAll("\\s", "");
        if (coeffStr.equals("+") || coeffStr.equals("*")) return 1.0;
        
        if (coeffStr.equals("-")) return -1.0; 

        if (coeffStr.isEmpty() || coeffStr.equals(".")) return 1.0;
        try {
            coeffStr = coeffStr.replaceAll("\\*$", "");
            return Double.parseDouble(coeffStr);
        } catch (NumberFormatException e) {
            return 1.0; 
        }
    }

    private static String formatCoeff(double value) {
        if (Math.abs(value - 1.0) < TOLERANCE) return "";
        if (Math.abs(value + 1.0) < TOLERANCE) return "-";
        if (Math.abs(value) < TOLERANCE) return "0";
        
        if (Math.abs(value - Math.round(value)) < TOLERANCE) return String.valueOf((long) Math.round(value));
        return String.format(Locale.US, "%.4f", value);
    }
}