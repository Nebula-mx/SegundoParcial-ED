package com.ecuaciones.diferenciales.model;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.ecuaciones.diferenciales.model.templates.ExpressionData;

public class EcuationParser {
  private String input;
  private List<String> variables;
  private ExpressionData expressionData;

  // Acepta ecuaciones en forma: d3y/dx + 3d2y/dx + 5dy/dx + 6y = 0
  private String leibnizRegex = "(?:\\d*d\\d*[a-zA-Z]/d[a-zA-Z])(?:[\\s]*[+\\-][\\s]*\\d*d\\d*[a-zA-Z]/d[a-zA-Z])*\\s*[+\\-]?\\s*\\d*[a-zA-Z]\\s*=.*";
  private String lagrangeRegex = "(?:\\d*[a-zA-Z](?:'*|\\(\\d+\\)))(?:[+\\-]\\d*[a-zA-Z](?:'*|\\(\\d+\\)))*=.*";

  public int getOrder() {
    if (expressionData.getNotation() == null) {
      throw new IllegalStateException("Debe llamar a parse() antes de getOrder()");
    }

    int maxOrder = 0;
    for (String var : variables) {
      int order = 0;
      
      if (expressionData.getNotation().equals("Leibniz")) {
        // Para Leibniz (d2y/dx2, d^2y/dx^2) - busca el primer número después de 'd'
        Matcher m = Pattern.compile("d(\\d+)").matcher(var);
        order = m.find() ? Integer.parseInt(m.group(1)) : 1;
      } else if (expressionData.getNotation().equals("Lagrange")) {
        // Para Lagrange (y', y'', y(2)) - cuenta primas o toma el número del exponente
        if (var.contains("(")) {
          // Caso y(n)
          Matcher m = Pattern.compile("\\((\\d+)\\)").matcher(var);
          order = m.find() ? Integer.parseInt(m.group(1)) : 0;
        } else {
          // Caso y'
          order = (int) var.chars().filter(ch -> ch == '\'').count();
        }
      }

      if (order > maxOrder) {
        maxOrder = order;
      }
    }
    return maxOrder;
  }

  public Integer[] getCoefficients() {
    List<Integer> coeffs = new ArrayList<>();
    
    for (String var : variables) {
      if (expressionData.getNotation().equals("Leibniz")) {
        // Busca números antes de 'd' para Leibniz
        Matcher m = Pattern.compile("^(\\d+)?d").matcher(var);
        if (m.find() && m.group(1) != null) {
          coeffs.add(Integer.parseInt(m.group(1)));
        } else {
          coeffs.add(1); // Si no hay coeficiente explícito, es 1
        }
      } else if (expressionData.getNotation().equals("Lagrange")) {
        // Busca números antes de 'y' para Lagrange
        Matcher m = Pattern.compile("^(\\d+)?[a-zA-Z]").matcher(var);
        if (m.find() && m.group(1) != null) {
          coeffs.add(Integer.parseInt(m.group(1)));
        } else {
          coeffs.add(1); // Si no hay coeficiente explícito, es 1
        }
      }
    }
    
    return coeffs.toArray(new Integer[0]);
  }

  public Boolean isHomogeneous() {
    int equalIndex = input.indexOf('=');
    if (equalIndex >= 0) {
      String rhs = input.substring(equalIndex + 1).trim();
      if (!rhs.equals("0")) {
        return false;
      } else {
        return true;
      }
    }
    // Por simplicidad, retornamos false aquí
    return false;
  }

  public ExpressionData parse(String input) {
    expressionData = new ExpressionData();
    this.input = input;

    //identifica la notacion, variables y orden.
    if(input.matches(leibnizRegex)) {
      expressionData.setNotation("Leibniz");
      // Para extraer términos individuales usamos un patrón más específico
      variables = findAll("\\d*d\\d*[a-zA-Z]/d[a-zA-Z]|\\d*[a-zA-Z](?!['/])", input);
    } else if(input.matches(lagrangeRegex)) {
      // Para buscar términos individuales usamos un patrón más específico
      variables = findAll("\\d*[a-zA-Z](?:'*|\\(\\d+\\))", input);
      expressionData.setNotation("Lagrange");
    } else {
      throw new IllegalArgumentException("Notación de ecuación diferencial no reconocida.");
    }

    // Implementar el análisis léxico y sintáctico aquí
    // Retornar un objeto ExpressionData con los datos extraídos
    expressionData.setExpression(this.input);
    expressionData.setOrder(getOrder());
    expressionData.setIsHomogeneous(isHomogeneous());
    expressionData.setVariables(this.variables);
    expressionData.setCoefficients(getCoefficients());
    return expressionData;
  }

  public static List<String> findAll(String regex, String text) {
    Pattern p = Pattern.compile(regex);
    Matcher m = p.matcher(text);
    List<String> matches = new ArrayList<>();
    while (m.find()) matches.add(m.group());
    return matches;
  }


}
