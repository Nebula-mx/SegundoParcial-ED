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
        <form action="solve" method="post" id="initialConditionsForm">
          <div class="formDoubleInputsContainer">
            <input type="text" name="conditionVariable" class="IC-variableValues" name="initialConditions" placeholder="y(0)" required value="y(0)">
            <input type="text" name="conditionValue" class="IC-variableResult" name="initialConditions" placeholder="Valor" required>
          </div>
        </form>
        <button id="addInitialCondition" type="button">Agregar otra condición</button>
      </section>
    </aside>
    <main id="EQ-resolutionMain">
      <section id="EQ-resMethod">
        <h3>Métodos de Resolución</h3>
        <legend>Selecciona el método que deseas utilizar para resolver la ecuación</legend>
        <form id="resMethod-select">
          <input type="radio" id="autoMethod" name="method" value="AUTO" checked>
          <label for="autoMethod">Atomático</label>

          <input type="radio" id="method1" name="method" value="UC">
          <label for="method1">Coeficientes Indeterminados</label>

          <input type="radio" id="method2" name="method" value="VP">
          <label for="method2">Variacion de Parámetros</label>
        </form>
        <button id="resolveEquation" type="submit" formmethod="post" formaction="solve">Resolver Ecuación</button>
      </section>
      <section id="EQ-finalRes">
        <div id="result"></div>
    </main>
  </section>
  <script type="module" src="js/index.js"></script>
</body>
</html>
