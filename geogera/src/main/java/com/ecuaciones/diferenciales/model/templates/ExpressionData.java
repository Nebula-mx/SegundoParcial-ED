package com.ecuaciones.diferenciales.model.templates;

import java.util.List;
import java.util.Map;

public class ExpressionData {
  public String expression;
  public String notation;
  public Integer order;
  public Boolean isHomogeneous;
  public List<String> variables;
  public Integer[] coefficients;
  public Map<String, String> independentTerm;
  
  public List<String> getVariables() {
    return variables;
  }

  public void setVariables(List<String> variables) {
    this.variables = variables;
  }
  
  public String getNotation() {
    return notation;
  }

  public void setNotation(String notation) {
    this.notation = notation;
  }

  public Integer getOrder() {
    return order;
  }

  public void setOrder(Integer order) {
    this.order = order;
  }

  public Map<String, Double> initialConditions;

  public Map<String, String> getIndependentTerm() {
    return independentTerm;
  }
  public void setIndependentTerm(Map<String, String> independentTerm) {
    this.independentTerm = independentTerm;
  }

  public String getExpression() {
    return expression;
  }

  public void setExpression(String expression) {
    this.expression = expression;
  }

  public Boolean getIsHomogeneous() {
    return isHomogeneous;
  }

  public void setIsHomogeneous(Boolean isHomogeneous) {
    this.isHomogeneous = isHomogeneous;
  }

  public Integer[] getCoefficients() {
    return coefficients;
  }

  public void setCoefficients(Integer[] coefficients) {
    this.coefficients = coefficients;
  }
  public Map<String, Double> getInitialConditions() {
    return initialConditions;
  }

  public void setInitialConditions(Map<String, Double> initialConditions) {
    this.initialConditions = initialConditions;
  }

  public ExpressionData() {
    // Inicializar con valores predeterminados
    this.expression = "";
    this.isHomogeneous = true;
    this.coefficients = new Integer[]{};
    this.independentTerm = null;
    this.initialConditions = Map.of();
  }

  
}
