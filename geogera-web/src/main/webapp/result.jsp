<%@ page contentType="text/html; charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Resultado - Geogera</title>
</head>
<body>
<h1>Resultado</h1>
<% if (request.getAttribute("expression") != null) { %>
    <p><strong>Expresión:</strong> <%= request.getAttribute("expression") %></p>
    <p><strong>Notación:</strong> <%= request.getAttribute("notation") %></p>
    <p><strong>Orden:</strong> <%= request.getAttribute("order") %></p>
    <p><strong>Homogénea:</strong> <%= request.getAttribute("isHomogeneous") %></p>
    <p><strong>Variables:</strong>
        <%= java.util.Arrays.toString((java.util.List)request.getAttribute("variables")) %>
    </p>
    <p><strong>Coeficientes:</strong>
        <%= java.util.Arrays.toString((Object[])request.getAttribute("coefficients")) %>
    </p>
<% } else { %>
    <p>No hay datos para mostrar.</p>
<% } %>

<p><a href="index.jsp">Volver</a></p>
</body>
</html>
