package com.ecuaciones.diferenciales.dto;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * CLASE MODELO: Respuesta de la resolución de una ecuación diferencial
 * Similar a la estructura que retorna Photomath
 */
public class DifferentialEquationResponse {
    
    // =============== INFORMACIÓN DE ENTRADA ===============
    private String equation;              // La ecuación original
    private String method;                // Método usado (UC, VP, AUTO)
    private List<String> initialConditions;  // Condiciones iniciales
    
    // =============== INFORMACIÓN EXTRAÍDA ===============
    private int order;                    // Orden de la ecuación
    private boolean isHomogeneous;        // ¿Es homogénea?
    private String forcingTerm;           // Término de forzamiento g(x)
    private List<Double> coefficients;    // Coeficientes del polinomio característico
    
    // =============== RAÍCES ===============
    private List<RootInfo> roots;         // Raíces del polinomio característico
    
    // =============== SOLUCIONES ===============
    private String homogeneousSolution;   // y_h(x)
    private String homogeneousSolutionLatex;  // y_h(x) en LaTeX
    
    private String particulatSolution;    // y_p(x)
    private String particulatSolutionLatex;   // y_p(x) en LaTeX
    private String particularMethod;      // Método usado para y_p
    
    private List<String> resolutionSteps; // Pasos de resolución
    
    // =============== RESULTADO FINAL ===============
    private String finalSolution;         // y(x) = y_h + y_p
    private String finalSolutionLatex;    // Versión LaTeX
    
    // =============== ESTADO ===============
    private String status;                // SUCCESS o ERROR
    private String message;               // Mensaje de error si aplica
    private int code;                     // 200, 400, 500, etc.
    
    // =============== CONSTRUCTORES ===============
    
    public DifferentialEquationResponse() {
        this.roots = new ArrayList<>();
        this.coefficients = new ArrayList<>();
        this.resolutionSteps = new ArrayList<>();
        this.initialConditions = new ArrayList<>();
        this.status = "PENDING";
        this.code = 100;
    }
    
    // =============== GETTERS Y SETTERS ===============
    
    public String getEquation() {
        return equation;
    }
    
    public void setEquation(String equation) {
        this.equation = equation;
    }
    
    public String getMethod() {
        return method;
    }
    
    public void setMethod(String method) {
        this.method = method;
    }
    
    public List<String> getInitialConditions() {
        return initialConditions;
    }
    
    public void setInitialConditions(List<String> initialConditions) {
        this.initialConditions = initialConditions;
    }
    
    public int getOrder() {
        return order;
    }
    
    public void setOrder(int order) {
        this.order = order;
    }
    
    public boolean isHomogeneous() {
        return isHomogeneous;
    }
    
    public void setHomogeneous(boolean homogeneous) {
        isHomogeneous = homogeneous;
    }
    
    public String getForcingTerm() {
        return forcingTerm;
    }
    
    public void setForcingTerm(String forcingTerm) {
        this.forcingTerm = forcingTerm;
    }
    
    public List<Double> getCoefficients() {
        return coefficients;
    }
    
    public void setCoefficients(List<Double> coefficients) {
        this.coefficients = coefficients;
    }
    
    public List<RootInfo> getRoots() {
        return roots;
    }
    
    public void setRoots(List<RootInfo> roots) {
        this.roots = roots;
    }
    
    public void addRoot(RootInfo root) {
        this.roots.add(root);
    }
    
    public String getHomogeneousSolution() {
        return homogeneousSolution;
    }
    
    public void setHomogeneousSolution(String homogeneousSolution) {
        this.homogeneousSolution = homogeneousSolution;
    }
    
    public String getHomogeneousSolutionLatex() {
        return homogeneousSolutionLatex;
    }
    
    public void setHomogeneousSolutionLatex(String homogeneousSolutionLatex) {
        this.homogeneousSolutionLatex = homogeneousSolutionLatex;
    }
    
    public String getParticulatSolution() {
        return particulatSolution;
    }
    
    public void setParticulatSolution(String particulatSolution) {
        this.particulatSolution = particulatSolution;
    }
    
    public String getParticulatSolutionLatex() {
        return particulatSolutionLatex;
    }
    
    public void setParticulatSolutionLatex(String particulatSolutionLatex) {
        this.particulatSolutionLatex = particulatSolutionLatex;
    }
    
    public String getParticularMethod() {
        return particularMethod;
    }
    
    public void setParticularMethod(String particularMethod) {
        this.particularMethod = particularMethod;
    }
    
    public List<String> getResolutionSteps() {
        return resolutionSteps;
    }
    
    public void setResolutionSteps(List<String> resolutionSteps) {
        this.resolutionSteps = resolutionSteps;
    }
    
    public void addResolutionStep(String step) {
        this.resolutionSteps.add(step);
    }
    
    public String getFinalSolution() {
        return finalSolution;
    }
    
    public void setFinalSolution(String finalSolution) {
        this.finalSolution = finalSolution;
    }
    
    public String getFinalSolutionLatex() {
        return finalSolutionLatex;
    }
    
    public void setFinalSolutionLatex(String finalSolutionLatex) {
        this.finalSolutionLatex = finalSolutionLatex;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
    
    public int getCode() {
        return code;
    }
    
    public void setCode(int code) {
        this.code = code;
    }
    
    public boolean isSuccess() {
        return "SUCCESS".equals(status);
    }
    
    public boolean isError() {
        return "ERROR".equals(status);
    }
    
    // =============== MÉTODO PARA CONVERTIR A JSON ===============
    
    public Map<String, Object> toMap() {
        Map<String, Object> map = new java.util.HashMap<>();
        
        map.put("status", status);
        map.put("code", code);
        map.put("message", message);
        
        map.put("equation", equation);
        map.put("method", method);
        map.put("order", order);
        map.put("isHomogeneous", isHomogeneous);
        map.put("forcingTerm", forcingTerm);
        map.put("coefficients", coefficients);
        map.put("initialConditions", initialConditions);
        
        map.put("roots", roots);
        
        map.put("homogeneousSolution", homogeneousSolution);
        map.put("homogeneousSolutionLatex", homogeneousSolutionLatex);
        map.put("particulatSolution", particulatSolution);
        map.put("particulatSolutionLatex", particulatSolutionLatex);
        map.put("particularMethod", particularMethod);
        map.put("resolutionSteps", resolutionSteps);
        
        map.put("finalSolution", finalSolution);
        map.put("finalSolutionLatex", finalSolutionLatex);
        
        return map;
    }
    
    public String toJson() {
        try {
            com.fasterxml.jackson.databind.ObjectMapper mapper = 
                new com.fasterxml.jackson.databind.ObjectMapper();
            return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(this.toMap());
        } catch (Exception e) {
            return this.toString();
        }
    }
    
    @Override
    public String toString() {
        return "DifferentialEquationResponse{" +
                "equation='" + equation + '\'' +
                ", status='" + status + '\'' +
                ", finalSolution='" + finalSolution + '\'' +
                ", code=" + code +
                '}';
    }
    
    // =============== CLASE INTERNA: RootInfo ===============
    
    public static class RootInfo {
        private int index;
        private double real;
        private double imaginary;
        private String display;
        
        public RootInfo(int index, double real, double imaginary, String display) {
            this.index = index;
            this.real = real;
            this.imaginary = imaginary;
            this.display = display;
        }
        
        // Getters
        public int getIndex() {
            return index;
        }
        
        public double getReal() {
            return real;
        }
        
        public double getImaginary() {
            return imaginary;
        }
        
        public String getDisplay() {
            return display;
        }
        
        @Override
        public String toString() {
            return display;
        }
    }
}
