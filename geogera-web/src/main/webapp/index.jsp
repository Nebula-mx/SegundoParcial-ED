<%@ page contentType="text/html; charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Geogera - Solver Web</title>
    <link rel="stylesheet" type="text/css" href="styles/main.css">
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Montserrat:ital,wght@0,100..900;1,100..900&display=swap" rel="stylesheet">
</head>
<body id="root">
  <header id="appHeader">
    <h2>Calculadora de Ecuaciónes Diferenciales</h2>
    <legend>Resuelve ecuaciones diferenciales con diferentes métodos analíticos</legend>
  </header>
  <section id="contentContainer">
    <aside id="contentAside">
      <section id="CA-equationForm">
          <h3>Ecuación Diferencial</h3>
          <legend>Ingresa la ecuación que deseas resolver</legend>

          <form action="solve" method="post">
              <label for="equation">Ecuación:</label><br>
              <input type="text" id="equation" name="equation" placeholder="Ejemplo: dy/dx + y = x" required><br><br>
          </form>

          <div class="CA-notationEx">
            <legend>Ejemplos de notación:</legend>
            <ul>
              <li>Derivada: dy/dx</li>
              <li>y' o y'' también son válidos</li>
              <li>Funciones: sin(x), cos(x), exp(x), ln(x)</li>
          </div>
      </section>

      <section id="CA-initialConditionsForm">
        <h3>Condiciones Iniciales</h3>
        <legend>Define los valores iniciales conocidos</legend>
        <form>
          <input type="text" class="IC-variableValues" name="initialConditions" placeholder="y(0)" required><br><br>
          <input type="text" class="IC-variableResult" name="initialConditions" placeholder="Valor" required><br><br>
          <button type="button" onclick="addInitialCondition()">Agregar otra condición inicial</button>
        </form>
        <button type="submit" formmethod="post" formaction="solve">Resolver Ecuación</button>
      </section>
    </aside>
    <main id="EQ-resolutionMain">
      <section id="EQ-resMethod">
        <h3>Métodos de Resolución</h3>
        <legend>Selecciona el método que deseas utilizar para resolver la ecuación</legend>
        <div>
          <input type="radio" id="method1" name="method" value="method1" checked>
          <label for="method1">Coeficientes Indeterminados</label><br>

          <input type="radio" id="method2" name="method" value="method2">
          <label for="method2">Variacion de Parámetros</label><br>
        </div>
      </section>
      <section id="EQ-finalRes">
        <h3>Resultado</h3>
        <legend>La solución de la ecuación diferencial aparecerá aquí</legend>
        <div id="result">
          <% if (request.getAttribute("solution") != null) { %>
              <p><strong>Solución:</strong> <%= request.getAttribute("solution") %></p>
          <% } else { %>
              <p>Aún no se ha resuelto ninguna ecuación.</p>
          <% } %>
        </div>
    </main>
  </section>
  <script type="module" src="js/index.js"></script>
</body>
</html>
