package com.ecuaciones.diferenciales.model;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Expression {
    private final List<Token> rpn;

    private static final Set<String> FUNCTIONS = Set.of("sin","cos","tan","exp","log","sqrt","abs","pow");
    private static final Map<String, Integer> PRECEDENCE = Map.of(
            "+", 2, "-", 2,
            "*", 3, "/", 3,
            "^", 4
    );

    //  Token types 
    private interface Token {}
    private static class NumberToken implements Token { final double value; NumberToken(double v){value=v;} }
    private static class VariableToken implements Token { final String name; VariableToken(String n){name=n;} }
    private static class OpToken implements Token { final String op; OpToken(String o){op=o;} }
    private static class FuncToken implements Token { final String func; FuncToken(String f){func=f;} }

    //Constructor: recibe infix, convierte a RPN 
    public Expression(String expr) {
        this.rpn = toRPN(expr);
    }

    // Evalúa la expresión con variables dadas
    public double evaluate(double x, double y) {
        Deque<Double> stack = new ArrayDeque<>();
        for (Token t : rpn) {
            if (t instanceof NumberToken) stack.push(((NumberToken) t).value);
            else if (t instanceof VariableToken) {
                String n = ((VariableToken) t).name;
                if (n.equalsIgnoreCase("x")) stack.push(x);
                else if (n.equalsIgnoreCase("y")) stack.push(y);
                else throw new RuntimeException("Variable desconocida: " + n);
            } else if (t instanceof OpToken) {
                String op = ((OpToken) t).op;
                if (op.equals("+") || op.equals("-") || op.equals("*") || op.equals("/") || op.equals("^")) {
                    double b = stack.pop();
                    double a = stack.pop();
                    stack.push(applyBinary(op, a, b));
                } else throw new RuntimeException("Operador desconocido: " + op);
            } else if (t instanceof FuncToken) {
                String f = ((FuncToken) t).func;
                if (f.equals("pow")) {
                    double exp = stack.pop();
                    double base = stack.pop();
                    stack.push(Math.pow(base, exp));
                } else {
                    double val = stack.pop();
                    stack.push(applyFunc(f, val));
                }
            }
        }
        if (stack.size() != 1) throw new RuntimeException("Error al evaluar la expresión.");
        return stack.pop();
    }

// apply :)
private static double applyBinary(String op, double a, double b) {
    switch (op) {
        case "+": return a + b;
        case "-": return a - b;
        case "*": return a * b;
        case "/": return a / b;
        case "^": return Math.pow(a, b);
        default:
            throw new RuntimeException("Operador no soportado: " + op);
    }
}

private static double applyFunc(String f, double v) {
    switch (f) {
        case "sin": return Math.sin(v);
        case "cos": return Math.cos(v);
        case "tan": return Math.tan(v);
        case "exp": return Math.exp(v);
        case "log": return Math.log(v);
        case "sqrt": return Math.sqrt(v);
        case "abs": return Math.abs(v);
        default:
            throw new RuntimeException("Función no soportada: " + f);
    }
}

    // Tokenización y shunting-yard... revisar bien .................................................
    private List<Token> toRPN(String input) {
        List<String> tokens = tokenize(input);
        Deque<String> ops = new ArrayDeque<>();
        List<Token> output = new ArrayList<>();

        for (int i = 0; i < tokens.size(); i++) {
            String tk = tokens.get(i);
            if (tk.isEmpty()) continue;
            if (isNumber(tk)) {
                output.add(new NumberToken(Double.parseDouble(tk)));
            } else if (isVariable(tk)) {
                output.add(new VariableToken(tk));
            } else if (FUNCTIONS.contains(tk.toLowerCase())) {
                ops.push(tk.toLowerCase());
            } else if (tk.equals(",")) {
                while (!ops.isEmpty() && !ops.peek().equals("(")) output.add(makeOpOrFuncToken(ops.pop()));
                if (ops.isEmpty() || !ops.peek().equals("(")) throw new RuntimeException("Separador de argumentos mal ubicado");
            } else if (PRECEDENCE.containsKey(tk)) { // operator
                String o1 = tk;
                while (!ops.isEmpty() && PRECEDENCE.containsKey(ops.peek())) {
                    String o2 = ops.peek();
                    int p1 = PRECEDENCE.get(o1);
                    int p2 = PRECEDENCE.get(o2);
                    // ^ es right-associative
                    if ((p1 <= p2 && !o1.equals("^")) || (p1 < p2 && o1.equals("^"))) {
                        output.add(makeOpOrFuncToken(ops.pop()));
                    } else break;
                }
                ops.push(o1);
            } else if (tk.equals("(")) {
                ops.push(tk);
            } else if (tk.equals(")")) {
                while (!ops.isEmpty() && !ops.peek().equals("(")) output.add(makeOpOrFuncToken(ops.pop()));
                if (ops.isEmpty() || !ops.peek().equals("(")) throw new RuntimeException("Paréntesis desbalanceado");
                ops.pop(); // quita (
                if (!ops.isEmpty() && FUNCTIONS.contains(ops.peek().toLowerCase())) {
                    output.add(new FuncToken(ops.pop().toLowerCase()));
                }
            } else {
            
                throw new RuntimeException("Token desconocido: " + tk);
            }
        }

        while (!ops.isEmpty()) {
            String op = ops.pop();
            if (op.equals("(") || op.equals(")")) throw new RuntimeException("Paréntesis desbalanceado");
            output.add(makeOpOrFuncToken(op));
        }

        return output;
    }

    // convierte string de operador/func a Token
    private Token makeOpOrFuncToken(String s) {
        if (PRECEDENCE.containsKey(s)) return new OpToken(s);
        if (FUNCTIONS.contains(s.toLowerCase())) return new FuncToken(s.toLowerCase());
        throw new RuntimeException("Operador/función inválida en salida: " + s);
    }

    // tokenizador simple: números, identificadores, operadores, etc.
    private List<String> tokenize(String s) {
        List<String> out = new ArrayList<>();
        s = s.replaceAll("\\s+", "");
        int i = 0;
        while (i < s.length()) {
            char c = s.charAt(i);
            if (Character.isDigit(c) || (c == '.')) {
                int j = i + 1;
                while (j < s.length() && (Character.isDigit(s.charAt(j)) || s.charAt(j) == '.')) j++;
                out.add(s.substring(i, j));
                i = j;
            } else if (Character.isLetter(c)) {
                int j = i + 1;
                while (j < s.length() && Character.isLetter(s.charAt(j))) j++;
                String id = s.substring(i, j);
                // funciones y variables
                out.add(id);
                i = j;
            } else if (c == ',') {
                out.add(",");
                i++;
            } else if ("+-*/^()".indexOf(c) >= 0) {
                // Manejar unary minus: si es '-' y va al inicio o después de '(' o de otro operador -> lo convertimos a "0" "-" "x" (por simplicidad)
                if (c == '-') {
                    boolean unary = (i == 0) || ("(+-*/^".indexOf(s.charAt(i-1)) >= 0);
                    if (unary) {
                        out.add("0");
                        out.add("-");
                        i++;
                        continue;
                    }
                }
                out.add(String.valueOf(c));
                i++;
            } else {
                throw new RuntimeException("Caracter inválido en expresión: '" + c + "'");
            }
        }
        return out;
    }

    private static boolean isNumber(String tk) {
        try { Double.valueOf(tk); return true; } catch (NumberFormatException e) { return false; }
    }
    private static boolean isVariable(String tk) {
        return tk.equalsIgnoreCase("x") || tk.equalsIgnoreCase("y");
    }
}
