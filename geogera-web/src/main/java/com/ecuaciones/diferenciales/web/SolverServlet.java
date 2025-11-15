package com.ecuaciones.diferenciales.web;

import java.io.IOException;

import com.ecuaciones.diferenciales.model.EcuationParser;
import com.ecuaciones.diferenciales.model.templates.ExpressionData;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet(name = "SolverServlet", urlPatterns = {"/solve"})
public class SolverServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.getRequestDispatcher("/index.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String expr = req.getParameter("expr");
        if (expr == null || expr.trim().isEmpty()) {
            req.setAttribute("error", "Ingresa una ecuación válida.");
            req.getRequestDispatcher("/index.jsp").forward(req, resp);
            return;
        }

        try {
            EcuationParser parser = new EcuationParser();
            ExpressionData data = parser.parse(expr);

            req.setAttribute("expression", data.getExpression());
            req.setAttribute("notation", data.getNotation());
            req.setAttribute("order", data.getOrder());
            req.setAttribute("isHomogeneous", data.getIsHomogeneous());
            req.setAttribute("variables", data.getVariables());
            Double[] coefs = data.getCoefficients();
            req.setAttribute("coefficients", coefs == null ? new Double[]{} : coefs);

            req.getRequestDispatcher("/result.jsp").forward(req, resp);
        } catch (Exception e) {
            req.setAttribute("error", "Error al analizar la ecuación: " + e.getMessage());
            req.getRequestDispatcher("/index.jsp").forward(req, resp);
        }
    }
}
