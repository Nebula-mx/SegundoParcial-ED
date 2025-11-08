package com.ecuaciones.diferenciales.model.templates;

import java.util.List;
import java.util.Map;

public class ExpressionData {
  private String expression;
  private String notation;
  private Integer order;
  private Boolean isHomogeneous;
  private List<String> variables;
  private Integer[] coefficients;
  private Map<String, String> independentTerm;

  private static ExpressionData instance = null;

  private  ExpressionData() {
    if(instance != null) {
      throw new IllegalStateException("Ya existe una instancia de ExpressionData. Use getInstance() para acceder a ella.");
    } else {
        this.expression = "";
        this.isHomogeneous = true;
        this.coefficients = new Integer[]{};
        this.independentTerm = null;
        this.initialConditions = Map.of();
    }
  }

  public static ExpressionData getInstance() {
    if (instance == null) {
      instance = new ExpressionData();
    }
    return instance;
  }
  
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
}
