# 🌐 Guía para Tu Amigo: Servlet en Java + JAR Backend

## ⚡ Resumen Rápido

Tu amigo tiene:
- ✅ Un proyecto web con Servlet en Java
- ✅ Un JAR del backend (`geogera-0.1.jar`)
- ❌ NO necesita la API REST

**Lo que necesita:** Usar directamente las clases del backend desde su código Java.

---

## 🎯 Configuración del Proyecto Web

### 1️⃣ Agregar el JAR como Dependencia

En su proyecto web (Maven o Gradle):

#### Si usa Maven (`pom.xml`)
```xml
<dependency>
    <groupId>com.ecuaciones</groupId>
    <artifactId>geogera</artifactId>
    <version>0.1</version>
    <scope>system</scope>
    <systemPath>${project.basedir}/lib/geogera-0.1.jar</systemPath>
</dependency>
```

**O** simplemente copiar el JAR en `WEB-INF/lib/`:
```
proyecto-web/
└── WebContent/
    └── WEB-INF/
        └── lib/
            └── geogera-0.1.jar  ← Copiar aquí
```

#### Si usa Gradle
```gradle
dependencies {
    compile files('lib/geogera-0.1.jar')
}
```

### 2️⃣ Crear el Servlet

```java
package com.miempresa.web;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

// Importar las clases del JAR
import com.ecuaciones.diferenciales.model.Expression;
import com.ecuaciones.diferenciales.model.ODEParser;
import com.ecuaciones.diferenciales.model.EcuationParser;
import com.ecuaciones.diferenciales.api.service.ODESolver;
import com.ecuaciones.diferenciales.api.dto.SolutionResponse;

import java.io.IOException;
import java.io.PrintWriter;
import com.google.gson.Gson;

@WebServlet("/solveEquation")
public class EquationSolverServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // Configurar CORS
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
        response.setHeader("Content-Type", "application/json;charset=UTF-8");

        try {
            // Obtener parámetros
            String equation = request.getParameter("equation");
            String variable = request.getParameter("variable");
            if (variable == null || variable.isEmpty()) {
                variable = "x";
            }

            // Validar
            if (equation == null || equation.trim().isEmpty()) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, 
                    "Ecuación vacía");
                return;
            }

            // AQUÍ VIENE LA MAGIA: Usar el JAR del backend
            long startTime = System.currentTimeMillis();

            // Parsear la ecuación
            ODEParser parser = new ODEParser();
            Expression expr = parser.parse(equation, variable);

            // Resolver
            ODESolver solver = new ODESolver();
            SolutionResponse solution = solver.solve(expr);

            long executionTime = System.currentTimeMillis() - startTime;

            // Agregar metadata
            solution.getMetadata().put("executionTimeMs", String.valueOf(executionTime));

            // Convertir a JSON y enviar
            String jsonResponse = new Gson().toJson(solution);
            
            response.setStatus(HttpServletResponse.SC_OK);
            PrintWriter out = response.getWriter();
            out.print(jsonResponse);
            out.flush();

        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            PrintWriter out = response.getWriter();
            
            // Retornar error en JSON
            String errorJson = "{\"status\":\"error\",\"message\":\"" + 
                e.getMessage() + "\"}";
            out.print(errorJson);
            out.flush();
        }
    }

    @Override
    protected void doOptions(HttpServletRequest request, 
            HttpServletResponse response) throws ServletException, IOException {
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
        response.setStatus(HttpServletResponse.SC_OK);
    }
}
```

---

## 🖥️ Frontend (HTML/JavaScript)

El frontend del servlet puede ser tan simple como:

```html
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>Solucionador de Ecuaciones</title>
    <style>
        * {
            margin: 0;
            padding: 0;
            box-sizing: border-box;
        }
        
        body {
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            min-height: 100vh;
            display: flex;
            justify-content: center;
            align-items: center;
            padding: 20px;
        }
        
        .container {
            background: white;
            border-radius: 15px;
            box-shadow: 0 20px 60px rgba(0, 0, 0, 0.3);
            padding: 40px;
            width: 100%;
            max-width: 600px;
        }
        
        h1 {
            color: #333;
            margin-bottom: 30px;
            text-align: center;
            font-size: 28px;
        }
        
        .form-group {
            margin-bottom: 20px;
        }
        
        label {
            display: block;
            margin-bottom: 8px;
            color: #555;
            font-weight: 600;
        }
        
        input {
            width: 100%;
            padding: 12px;
            border: 2px solid #ddd;
            border-radius: 8px;
            font-size: 16px;
            transition: border-color 0.3s;
        }
        
        input:focus {
            outline: none;
            border-color: #667eea;
        }
        
        button {
            width: 100%;
            padding: 14px;
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            color: white;
            border: none;
            border-radius: 8px;
            font-size: 16px;
            font-weight: 600;
            cursor: pointer;
            transition: transform 0.2s;
        }
        
        button:hover {
            transform: translateY(-2px);
            box-shadow: 0 10px 20px rgba(102, 126, 234, 0.4);
        }
        
        button:active {
            transform: translateY(0);
        }
        
        .loading {
            display: none;
            text-align: center;
            padding: 20px;
        }
        
        .loading.show {
            display: block;
        }
        
        .spinner {
            border: 4px solid #f3f3f3;
            border-top: 4px solid #667eea;
            border-radius: 50%;
            width: 40px;
            height: 40px;
            animation: spin 1s linear infinite;
            margin: 0 auto;
        }
        
        @keyframes spin {
            0% { transform: rotate(0deg); }
            100% { transform: rotate(360deg); }
        }
        
        .result {
            display: none;
            margin-top: 30px;
            animation: slideIn 0.5s ease-out;
        }
        
        .result.show {
            display: block;
        }
        
        @keyframes slideIn {
            from {
                opacity: 0;
                transform: translateY(20px);
            }
            to {
                opacity: 1;
                transform: translateY(0);
            }
        }
        
        .result-section {
            margin-bottom: 20px;
            padding: 15px;
            background: #f8f9fa;
            border-left: 4px solid #667eea;
            border-radius: 4px;
        }
        
        .result-section h3 {
            color: #333;
            margin-bottom: 10px;
            font-size: 16px;
        }
        
        .result-section p {
            color: #666;
            line-height: 1.6;
        }
        
        .step {
            margin: 15px 0;
            padding: 12px;
            background: white;
            border-radius: 6px;
            border-left: 3px solid #667eea;
        }
        
        .step-title {
            font-weight: 600;
            color: #667eea;
            margin-bottom: 5px;
        }
        
        .step-content {
            color: #666;
            font-size: 14px;
        }
        
        .error {
            color: #e74c3c;
            padding: 15px;
            background: #fadbd8;
            border-radius: 6px;
            margin-top: 20px;
            border-left: 4px solid #e74c3c;
        }
    </style>
</head>
<body>
    <div class="container">
        <h1>🧮 Solucionador de Ecuaciones</h1>
        
        <form id="equationForm">
            <div class="form-group">
                <label for="equation">Ecuación Diferencial:</label>
                <input 
                    type="text" 
                    id="equation" 
                    name="equation"
                    placeholder="Ej: y'' - 5*y' + 6*y = 0"
                    required
                >
            </div>
            
            <div class="form-group">
                <label for="variable">Variable (opcional):</label>
                <input 
                    type="text" 
                    id="variable" 
                    name="variable"
                    placeholder="Defecto: x"
                    value="x"
                >
            </div>
            
            <button type="submit">Resolver</button>
        </form>
        
        <div class="loading" id="loading">
            <div class="spinner"></div>
            <p>Resolviendo...</p>
        </div>
        
        <div class="result" id="result">
            <div class="result-section">
                <h3>✅ Solución Final</h3>
                <p id="solution"></p>
            </div>
            
            <div class="result-section">
                <h3>📊 Pasos de Resolución</h3>
                <div id="steps"></div>
            </div>
            
            <div class="result-section">
                <h3>⏱️ Información</h3>
                <p id="metadata"></p>
            </div>
        </div>
        
        <div class="error" id="error" style="display: none;"></div>
    </div>

    <script>
        const form = document.getElementById('equationForm');
        const loading = document.getElementById('loading');
        const result = document.getElementById('result');
        const errorDiv = document.getElementById('error');

        form.addEventListener('submit', async (e) => {
            e.preventDefault();
            
            const equation = document.getElementById('equation').value;
            const variable = document.getElementById('variable').value || 'x';
            
            // Limpiar resultados previos
            result.classList.remove('show');
            errorDiv.style.display = 'none';
            loading.classList.add('show');
            
            try {
                // Llamar al servlet
                const response = await fetch('/context-root/solveEquation', {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/x-www-form-urlencoded',
                    },
                    body: new URLSearchParams({
                        equation: equation,
                        variable: variable
                    })
                });
                
                if (!response.ok) {
                    throw new Error(`Error HTTP: ${response.status}`);
                }
                
                const data = await response.json();
                
                // Mostrar solución
                document.getElementById('solution').textContent = 
                    data.finalSolution || 'No disponible';
                
                // Mostrar pasos
                const stepsDiv = document.getElementById('steps');
                stepsDiv.innerHTML = '';
                if (data.steps && Array.isArray(data.steps)) {
                    data.steps.forEach((step, index) => {
                        const stepEl = document.createElement('div');
                        stepEl.className = 'step';
                        stepEl.innerHTML = `
                            <div class="step-title">${index + 1}. ${step.title}</div>
                            <div class="step-content">${step.explanation}</div>
                        `;
                        stepsDiv.appendChild(stepEl);
                    });
                }
                
                // Mostrar metadata
                const metadataEl = document.getElementById('metadata');
                if (data.metadata) {
                    metadataEl.innerHTML = Object.entries(data.metadata)
                        .map(([k, v]) => `<strong>${k}:</strong> ${v}`)
                        .join('<br>');
                }
                
                result.classList.add('show');
                
            } catch (error) {
                errorDiv.style.display = 'block';
                errorDiv.textContent = `❌ Error: ${error.message}`;
            } finally {
                loading.classList.remove('show');
            }
        });
    </script>
</body>
</html>
```

---

## 📝 Ubicación del Archivo HTML

```
proyecto-web/
├── WebContent/
│   ├── index.html
│   ├── solveEquation.html  ← Este archivo
│   ├── css/
│   └── js/
├── src/
│   └── com/miempresa/web/
│       └── EquationSolverServlet.java
├── WebContent/WEB-INF/
│   ├── web.xml
│   └── lib/
│       └── geogera-0.1.jar
└── pom.xml (o build.gradle)
```

---

## 🚀 Ejemplo de Uso

### 1. Tu amigo accede a:
```
http://localhost:8080/proyecto-web/solveEquation.html
```

### 2. Ingresa una ecuación:
```
y'' - 5*y' + 6*y = 0
```

### 3. El servlet (usando el JAR) resuelve y retorna JSON

### 4. El HTML renderiza la solución

---

## 🔗 Llamar desde JSP (Alternativa)

Si tu amigo prefiere JSP:

```jsp
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.ecuaciones.diferenciales.model.*" %>
<%@ page import="com.ecuaciones.diferenciales.api.service.*" %>
<%@ page import="com.ecuaciones.diferenciales.api.dto.*" %>
<%@ page import="com.google.gson.Gson" %>

<%
    String equation = request.getParameter("equation");
    String variable = request.getParameter("variable");
    
    if (variable == null || variable.isEmpty()) {
        variable = "x";
    }
    
    if (equation != null && !equation.trim().isEmpty()) {
        try {
            // Usar el JAR
            ODEParser parser = new ODEParser();
            Expression expr = parser.parse(equation, variable);
            
            ODESolver solver = new ODESolver();
            SolutionResponse solution = solver.solve(expr);
            
            // Convertir a JSON
            String json = new Gson().toJson(solution);
            response.setContentType("application/json");
            out.print(json);
            
        } catch (Exception e) {
            response.setContentType("application/json");
            out.print("{\"error\":\"" + e.getMessage() + "\"}");
        }
    }
%>
```

---

## 🛠️ Agregar Dependencias (Maven)

Tu amigo necesita agregar a su `pom.xml`:

```xml
<dependencies>
    <!-- Gson para JSON -->
    <dependency>
        <groupId>com.google.code.gson</groupId>
        <artifactId>gson</artifactId>
        <version>2.10.1</version>
    </dependency>
    
    <!-- Jakarta Servlet (si no está) -->
    <dependency>
        <groupId>jakarta.servlet</groupId>
        <artifactId>jakarta.servlet-api</artifactId>
        <version>6.0.0</version>
        <scope>provided</scope>
    </dependency>
    
    <!-- El JAR del backend (local) -->
    <dependency>
        <groupId>com.ecuaciones</groupId>
        <artifactId>geogera</artifactId>
        <version>0.1</version>
        <scope>system</scope>
        <systemPath>${project.basedir}/lib/geogera-0.1.jar</systemPath>
    </dependency>
</dependencies>
```

---

## 📊 Flujo Completo

```
1. Cliente abre HTML en navegador
   ↓
2. Usuario ingresa ecuación
   ↓
3. JavaScript hace POST a /solveEquation
   ↓
4. Servlet recibe parámetros
   ↓
5. Servlet importa clases del JAR
   ↓
6. Servlet resuelve la ecuación
   ↓
7. Servlet retorna JSON con solución
   ↓
8. JavaScript renderiza solución en HTML
   ↓
9. Usuario ve resultado ✅
```

---

## ✨ Ventajas de Este Enfoque

✅ No necesita servidor backend separado  
✅ Usa directamente el JAR  
✅ Todo en un solo proyecto web  
✅ Fácil de desplegar  
✅ Mejor rendimiento (sin HTTP extra)  
✅ Control total del frontend  

---

## 💡 Notas Importantes

1. **El contexto de la aplicación:** Cambiar `/context-root/solveEquation` por el real
2. **CORS:** Si el frontend está en otro servidor, configurar CORS en el servlet
3. **Dependencias del JAR:** Asegurarse de que Gson y otras deps estén disponibles
4. **Tomcat/JBoss:** Cualquier servidor Java Servlet funciona

---

## 🎉 ¡Listo!

Tu amigo ya tiene todo lo que necesita para:
- ✅ Integrar el JAR backend
- ✅ Crear un servlet que lo use
- ✅ Hacer un frontend HTML/JavaScript bonito
- ✅ Resolver ecuaciones sin API REST

**¡Que disfrute! 🚀**
