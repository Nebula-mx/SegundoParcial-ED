package com.ecuaciones.diferenciales.model.templates;

import java.util.List;
import java.util.ArrayList; 
import java.util.Map;
import java.util.HashMap;

/**
 * Contiene todos los datos parseados de la Ecuación Diferencial (EDO).
 * Implementa un constructor estándar para asegurar que cada análisis de EDO 
 * utilice una instancia limpia y segura de los datos.
 */
public class ExpressionData {
  
  private String expression;
  private String notation;
  private Integer order;
  private Boolean isHomogeneous;
  private List<String> variables;
  private Double[] coefficients; 
  private Map<String, String> independentTerm;
  private Map<String, Double> initialConditions; 
  
  // *** CAMBIO CLAVE: Singleton eliminado. La instancia es creada directamente por el parser. ***
  
  /**
   * Constructor estándar (público) que inicializa la estructura de datos limpia.
   * La clase EcuationParser debe instanciar esta clase directamente.
   */
  public ExpressionData() {
    // Se inicializa limpio, sin necesidad del método clearData()
    this.expression = "";
    this.notation = "";
    this.order = 0;
    this.isHomogeneous = true; 
    this.variables = new ArrayList<>();
    this.coefficients = new Double[]{}; 
    this.independentTerm = new HashMap<>(); 
    this.initialConditions = new HashMap<>(); 
  }
  
  // El método clearData() se elimina ya que el constructor garantiza un estado limpio.
  
  // ===================================
  // Getters y Setters
  // ===================================

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

  public Double[] getCoefficients() { 
    return coefficients;
  }

  public void setCoefficients(Double[] coefficients) { 
    this.coefficients = coefficients;
  }
  
  public Map<String, Double> getInitialConditions() {
    return initialConditions;
  }

  public void setInitialConditions(Map<String, Double> initialConditions) {
    this.initialConditions = initialConditions;
  }  
}
