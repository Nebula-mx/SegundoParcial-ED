/**
 * EJEMPLOS DE USO - Resolvedor de Ecuaciones Diferenciales Ordinarias
 * 
 * Este archivo contiene ejemplos de diferentes tipos de ecuaciones
 * que pueden ser resueltas por el sistema.
 */

// ==================== EJEMPLO 1: ECUACIÓN HOMOGÉNEA ====================
// Ecuación: y'' - 5y' + 6y = 0
// Raíces características: r=2, r=3
// Solución: y(x) = C1*e^(2x) + C2*e^(3x)
//
// Uso interactivo:
//   Ingresa: y'' - 5y' + 6y = 0
//   Responde: n (sin condiciones iniciales)


// ==================== EJEMPLO 2: CON CONDICIONES INICIALES ====================
// Ecuación: y'' - 5y' + 6y = 0
// Condiciones iniciales: y(0) = 1, y'(0) = 0
// 
// Solución general: y(x) = C1*e^(2x) + C2*e^(3x)
// Aplicando CI: y(0) = 1 => C1 + C2 = 1
//               y'(0) = 0 => 2C1 + 3C2 = 0
// Solución: C1 = -3, C2 = 4
// Resultado: y(x) = -3*e^(2x) + 4*e^(3x)
//
// Uso interactivo:
//   Ingresa: y'' - 5y' + 6y = 0
//   Responde: s (sí agregar condiciones iniciales)
//   Ingresa: y(0)=1, y'(0)=0


// ==================== EJEMPLO 3: ECUACIÓN NO-HOMOGÉNEA ====================
// Ecuación: y'' + 4y = 2*sin(x)
// Ecuación homogénea: y'' + 4y = 0 → r² + 4 = 0 → r = ±2i
// Solución homogénea: y_h = C1*cos(2x) + C2*sin(2x)
// 
// Método: Coeficientes Indeterminados (UC)
// Forma de y_p: A*sin(x) + B*cos(x)
// Resolviendo: A = 2/3, B = 0
// Solución particular: y_p = (2/3)*sin(x)
// 
// Solución general: y(x) = C1*cos(2x) + C2*sin(2x) + (2/3)*sin(x)
//
// Uso interactivo:
//   Ingresa: y'' + 4y = 2*sin(x)
//   Responde: n (sin condiciones iniciales)


// ==================== EJEMPLO 4: ORDEN SUPERIOR ====================
// Ecuación: y''' - 6y'' + 11y' - 6y = 0
// Polinomio característico: r³ - 6r² + 11r - 6 = 0
// Raíces: r = 1, 2, 3
// 
// Solución: y(x) = C1*e^(x) + C2*e^(2x) + C3*e^(3x)
//
// Uso interactivo:
//   Ingresa: y''' - 6y'' + 11y' - 6y = 0
//   Responde: n


// ==================== EJEMPLO 5: RAÍCES REPETIDAS ====================
// Ecuación: y'' + 4y' + 4y = 0
// Polinomio característico: r² + 4r + 4 = 0 → (r+2)² = 0
// Raíz: r = -2 (multiplicidad 2)
//
// Solución: y(x) = C1*e^(-2x) + C2*x*e^(-2x)
//
// Uso interactivo:
//   Ingresa: y'' + 4y' + 4y = 0
//   Responde: n


// ==================== EJEMPLO 6: RAÍCES COMPLEJAS ====================
// Ecuación: y'' + 2y' + 5y = 0
// Polinomio característico: r² + 2r + 5 = 0
// Raíces: r = -1 ± 2i (complejo conjugado)
//
// Solución: y(x) = e^(-x) * (C1*cos(2x) + C2*sin(2x))
//
// Uso interactivo:
//   Ingresa: y'' + 2y' + 5y = 0
//   Responde: n


// ==================== EJEMPLO 7: CON RESONANCIA ====================
// Ecuación: y'' + 4y = 2*sin(2x)
// Ecuación homogénea: y'' + 4y = 0 → r = ±2i
// Solución homogénea: y_h = C1*cos(2x) + C2*sin(2x)
// 
// ⚠️ RESONANCIA DETECTADA: 2i es raíz de la ecuación homogénea
//    y f(x) = 2*sin(2x) coincide en frecuencia
//
// Método: Coeficientes Indeterminados (UC) con corrección por resonancia
// Forma de y_p: x*(A*sin(2x) + B*cos(2x))
// Resolviendo: A = -1/2, B = 0
// Solución particular: y_p = -x/2 * sin(2x)
//
// Solución general: y(x) = C1*cos(2x) + C2*sin(2x) - (x/2)*sin(2x)
//
// Uso interactivo:
//   Ingresa: y'' + 4y = 2*sin(2x)
//   Responde: n


// ==================== EJEMPLO 8: VARIACIÓN DE PARÁMETROS ====================
// Ecuación: y'' + y = sec(x)
// Ecuación homogénea: y'' + y = 0 → r = ±i
// Solución homogénea: y_h = C1*cos(x) + C2*sin(x)
//
// Método: Variación de Parámetros (VP)
// Función f(x) = sec(x) - Es complicada, mejor usar VP que UC
//
// W = determinante Wronskiano = 1
// Integración compleja (ver código para detalles)
//
// Solución particular se calcula automáticamente
// Solución general: y(x) = C1*cos(x) + C2*sin(x) + y_p
//
// Uso interactivo:
//   Ingresa: y'' + y = sec(x)
//   Responde: n


// ==================== CÓMO USAR EL PROGRAMA ====================
// 
// 1. Ejecutar el programa:
//    $ mvn exec:java@main
//
// 2. Responder "s" cuando se pregunte si deseas resolver una ecuación
//
// 3. Ingresar la ecuación en uno de estos formatos:
//    - Homogénea: y'' - 5y' + 6y = 0
//    - No-homogénea: y'' + 4y = 2*sin(x)
//    - Orden 1: y' + 2y = 4
//    - Orden superior: y''' - 6y'' + 11y' - 6y = 0
//
// 4. (Opcional) Agregar condiciones iniciales:
//    - Responder "s" si quieres agregar CI
//    - Ingresa: y(0)=1, y'(0)=2
//    - Usa comas para separar múltiples condiciones
//
// 5. Ver la solución completa y detallada
//
// 6. (Opcional) Resolver otra ecuación


// ==================== FORMATOS SOPORTADOS ====================
//
// VARIABLES:
//   y    = función desconocida
//   x    = variable independiente
//
// DERIVADAS:
//   y'   = primera derivada
//   y''  = segunda derivada
//   y''' = tercera derivada
//   y^(4) = cuarta derivada (usar notación exponencial)
//
// OPERACIONES:
//   +, -, *, /, ^ (potencia)
//   
// FUNCIONES ESPECIALES:
//   sin(x), cos(x), tan(x)
//   sec(x), csc(x), cot(x)
//   e^x, ln(x), sqrt(x)
//   x^n (potencias)
//
// EJEMPLOS VÁLIDOS:
//   y' + 2y = 4
//   y'' - 5y' + 6y = 0
//   y'' + 4y = 2*sin(x)
//   y''' - y' = e^x
//   y^(4) - 16y = 0
//   y'' + 2y' + 5y = 0
//
// CONDICIONES INICIALES:
//   y(0) = 1
//   y'(0) = 2
//   y''(0) = 0


// ==================== CASOS ESPECIALES MANEJADOS ====================
//
// ✅ Raíces reales distintas → y = C1*e^(r1*x) + C2*e^(r2*x)
// ✅ Raíces repetidas → y = C1*e^(rx) + C2*x*e^(rx)
// ✅ Raíces complejas → y = e^(αx)*(C1*cos(βx) + C2*sin(βx))
// ✅ Orden superior (hasta 4+)
// ✅ Detección automática de resonancia
// ✅ Coeficientes indeterminados (UC)
// ✅ Variación de parámetros (VP)
// ✅ Aplicación de condiciones iniciales (PVI)
