package com.ecuaciones.diferenciales;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ODEParser {

    // Acepta: y' y  dy/dx para las ec. dif 
    private static final Pattern PATTERN = Pattern.compile(
            "\\s*(?:y'|dy\\s*/\\s*dx)\\s*=\\s*(.+)\\s*", Pattern.CASE_INSENSITIVE);

    public static class Result {
        public final String rhsExpression;
        public Result(String rhs) { this.rhsExpression = rhs; }
    }

    public static Result parse(String input) throws IllegalArgumentException {
        if (input == null || input.trim().isEmpty()) {
            throw new IllegalArgumentException("La ecuación está vacía.");
        }
        Matcher m = PATTERN.matcher(input);
        if (!m.matches()) {
            throw new IllegalArgumentException("Formato inválido. Use: y' = expresión  o  dy/dx = expresión");
        }
        String rhs = m.group(1).trim();
        if (rhs.isEmpty()) throw new IllegalArgumentException("No se encontró la expresión a la derecha del =");
        return new Result(rhs);
    }
}
