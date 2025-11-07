package com.ecuaciones.diferenciales;

import java.util.Scanner;

import com.ecuaciones.diferenciales.model.EcuationParser;
import com.ecuaciones.diferenciales.model.templates.ExpressionData;

public class Main {

    public static void main(String[] args) {
        try (Scanner scanner = new Scanner(System.in)) {
            EcuationParser parser = new EcuationParser();

            System.out.println("Ecuaciones Diferenciales. proyecto del Terror");
            System.out.print("Ingresa una ecuación: ");
            String ecuacion = scanner.nextLine().toLowerCase();

            if (esEcuacionDiferencial(ecuacion)) {
                System.out.println("La ecuación ingresada es una ecuación diferencial.");

                int orden = obtenerOrden(ecuacion);
                System.out.println("Orden de la ecuación: " + orden);

                System.out.print("¿Deseas ingresar condiciones iniciales? (sí/no): ");
                String respuesta = scanner.nextLine().trim().toLowerCase();

                if (respuesta.equals("si")) {
                    ingresarCondicionesIniciales(scanner, orden);
                } else {
                    System.out.println("Sin condiciones iniciales. ");
                }

            } else {
                System.out.println(" La ecuación ingresada NO es una ecuación diferencial.");
            }

            ExpressionData data = parser.parse(ecuacion);

            System.out.println("\nDatos extraídos:");
            System.out.println("Expresión: " + data.getExpression());
            System.out.println("Notación: " + data.getNotation());
            System.out.println("Es homogénea: " + data.getIsHomogeneous());
            System.out.println("Orden: " + data.getOrder());
            System.out.println("Variables: " + data.getVariables());
            System.out.print("Coeficientes: ");
            Integer[] coeffs = data.getCoefficients();
            if (coeffs != null) {
                for (Integer coeff : coeffs) {
                    System.out.print(coeff + " ");
                }
                System.out.println("\n");
            }
        }
    }

    //para la notación
    public static boolean esEcuacionDiferencial(String ecuacion) {
        String[] patrones = {
            "dy/dx", "d2y/dx2", "d3y/dx3",
            "y'", "y''", "y'''",
            "d/dx", "d/dt"
        };
        for (String patron : patrones) {
            if (ecuacion.contains(patron)) {
                return true;
            }
        }
        return false;
    }

    // orden
    public static int obtenerOrden(String ecuacion) {
        if (ecuacion.contains("d3y/dx3") || ecuacion.contains("y'''")) return 3;
        if (ecuacion.contains("d2y/dx2") || ecuacion.contains("y''")) return 2;
        if (ecuacion.contains("dy/dx") || ecuacion.contains("y'")) return 1;
        return 0;
    }

    // C.I.
    public static void ingresarCondicionesIniciales(Scanner scanner, int orden) {
        System.out.println("\nIngreso de Condiciones Iniciales ");

        System.out.print("Valor inicial de x0 (o t0): ");
        double x0 = scanner.nextDouble();

        System.out.print("Valor de y(x0): ");
        double y0 = scanner.nextDouble();

        double[] derivadas = new double[Math.max(0, orden - 1)];
        for (int i = 0; i < derivadas.length; i++) {
            System.out.print("Valor de y" + "'".repeat(i + 1) + "(x0): ");
            derivadas[i] = scanner.nextDouble();
        }

    }
}
