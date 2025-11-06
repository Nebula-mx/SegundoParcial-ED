package com.ecuaciones.diferenciales;

import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class PrimaryController {

    @FXML private TextField equationField;
    @FXML private TextField x0Field;
    @FXML private TextField y0Field;
    @FXML private TextArea outputArea;

    @FXML
    private void onParse() {
        outputArea.clear();
        String eq = equationField.getText();
        try {
            ODEParser.Result res = ODEParser.parse(eq);
            String expr = res.rhsExpression;
            // construir expression (esto validará tokens y paréntesis)
            Expression expression = new Expression(expr);

            // validar condiciones iniciales
            double x0 = parseDoubleOrThrow(x0Field.getText(), "x₀");
            double y0 = parseDoubleOrThrow(y0Field.getText(), "y₀");

            outputArea.appendText("Ecuación parseada correctamente.\n");
            outputArea.appendText("Expresión RHS: " + expr + "\n");
            outputArea.appendText(String.format("Condición inicial: x0=%.6f, y0=%.6f\n", x0, y0));
            outputArea.appendText("Puedes evaluar la RHS con el botón 'Evaluar ejemplo'.\n");
        } catch (Exception ex) {
            outputArea.appendText("Error: " + ex.getMessage());
        }
    }

    @FXML
    private void onEvaluateExample() {
        outputArea.clear();
        try {
            ODEParser.Result res = ODEParser.parse(equationField.getText());
            Expression expr = new Expression(res.rhsExpression);
            double x0 = parseDoubleOrThrow(x0Field.getText(), "x₀");
            double y0 = parseDoubleOrThrow(y0Field.getText(), "y₀");
            double val = expr.evaluate(x0, y0);
            outputArea.appendText(String.format("Evaluación RHS en (x=%.6f, y=%.6f) = %.8f\n", x0, y0, val));
        } catch (Exception ex) {
            outputArea.appendText("Error: " + ex.getMessage());
        }
    }

    private double parseDoubleOrThrow(String txt, String name) {
        if (txt == null || txt.trim().isEmpty()) throw new IllegalArgumentException(name + " está vacío.");
        try {
            return Double.parseDouble(txt.trim());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(name + " no es un número válido.");
        }
    }
}
