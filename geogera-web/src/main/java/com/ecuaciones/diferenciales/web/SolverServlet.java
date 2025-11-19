package com.ecuaciones.diferenciales.web;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;

import com.ecuaciones.diferenciales.Main;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

class SolverRequest {
    String expr;
    String method;
    List<String> conditions;
}

@WebServlet(name = "SolverServlet", urlPatterns = {"/solve"})
public class SolverServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.getRequestDispatcher("/index.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        SolverRequest solverRequest = gson.fromJson(req.getReader(), SolverRequest.class);
        if (solverRequest.expr == null || solverRequest.expr.trim().isEmpty()) {
            req.setAttribute("error", "Ingresa una ecuación válida.");
            req.getRequestDispatcher("/index.jsp").forward(req, resp);
            return;
        }

        try {
            String data = Main.evaluateWithStepsAsJson(solverRequest.expr, solverRequest.method);

            if(solverRequest.conditions != null && !solverRequest.conditions.isEmpty()) {
                Map<String, Object> Cdata = Main.evaluate(solverRequest.expr, solverRequest.method, solverRequest.conditions);

                JsonObject dataJson = JsonParser.parseString(data).getAsJsonObject();
                JsonArray initialConditionsArray = new JsonArray();
                
                for(String condition : solverRequest.conditions) {
                    initialConditionsArray.add(condition);
                }

                dataJson.add("initialConditionsList", initialConditionsArray);
                dataJson.addProperty("conditionsSolution", Cdata.get("finalSolution").toString());

                data = gson.toJson(dataJson);
            }
            
            resp.setContentType(    "application/json; charset=UTF-8");
            PrintWriter out = resp.getWriter();
            
            out.write(data);
            out.flush();
            
        } catch (IOException e) {
            req.setAttribute("error", "Error al analizar la ecuación: " + e.getMessage());
            req.getRequestDispatcher("/index.jsp").forward(req, resp);
        }
    }
}
