# 📐 GEOGERA - Solver de Ecuaciones Diferenciales Ordinarias

**Nivel Académico**: Licenciatura - Segundo Parcial  
**Tecnología**: Java 17 + Maven  
**Estado**: ✅ Producción-Ready  
**Versión**: 1.0 Final

---

## 🎯 ¿Qué es GEOGERA?

GEOGERA es un **resolutor completo de ecuaciones diferenciales ordinarias (EDO)** que implementa métodos matemáticos fundamentales:

### Métodos Soportados

| Método | Descripción |
|--------|------------|
| **Ecuaciones Homogéneas** | Resuelve EDO homogéneas extrayendo raíces características |
| **Coeficientes Indeterminados (UC)** | Método para términos polinomiales y trigonométricos |
| **Variación de Parámetros (VP)** | Método general con fallback automático |
| **Problemas de Valor Inicial (PVI)** | Aplicación automática de condiciones iniciales |
| **Órdenes Arbitrarias** | Soporta ecuaciones de orden 2, 3, 4+ |

### Características Principales

✅ **Resolución automática** de ecuaciones diferenciales  
✅ **Aplicación de PVI** con constantes resueltas numéricamente  
✅ **Formato limpio** de expresiones simplificadas  
✅ **Fallback inteligente** UC → VP en caso de resonancia  
✅ **Soporte CAS** mediante Symja/Matheclipse  
✅ **Salida JSON** para integración con otras aplicaciones

---

## 🚀 Inicio Rápido

### Requisitos
- Java 17+
- Maven 3.9+

### Compilar
\`\`\`bash
mvn clean compile
\`\`\`

### Ejecutar Interactivamente
\`\`\`bash
mvn exec:java -Dexec.mainClass="com.ecuaciones.diferenciales.Main"
\`\`\`

### Crear JAR Ejecutable
\`\`\`bash
mvn clean package
java -jar target/geogera-1.0.jar
\`\`\`

---

## 💻 Ejemplos de Uso

### Ejemplo 1: Homogénea Simple
\`\`\`
Ingrese la ecuación: y'' - 5y' + 6y = 0
¿Tiene condiciones iniciales? (s/n): n

Salida:
  Solución Homogénea: y_h = C1*e^(3x) + C2*e^(2x)
  Orden: 2
  Es Homogénea: true
\`\`\`

### Ejemplo 2: Con Condiciones Iniciales
\`\`\`
Ingrese la ecuación: y'' - 5y' + 6y = 0
¿Tiene condiciones iniciales? (s/n): s
Ingrese y(0): 1
Ingrese y'(0): 0

Salida:
  Solución Particular: y = 1.0*e^(3x) - 0.5*e^(2x)
  C1 = 1.0, C2 = -0.5
\`\`\`

### Ejemplo 3: No-Homogénea (UC)
\`\`\`
Ingrese la ecuación: y'' + 4y = 8*cos(2*x)

Salida:
  y_h = C1*cos(2x) + C2*sin(2x)
  y_p = 2*x*sin(2x)
  y_general = C1*cos(2x) + C2*sin(2x) + 2*x*sin(2x)
\`\`\`

### Ejemplo 4: Orden Superior
\`\`\`
Ingrese la ecuación: y''' - y' = x^2

Salida:
  y_h = C1 + C2*e^x + C3*e^(-x)
  y_p = - 0.3333 * x^3
  y_general = (C1 + C2*e^x + C3*e^(-x)) + (- 0.3333 * x^3)
\`\`\`

---

## 📚 Documentación

| Archivo | Contenido |
|---------|-----------|
| **README.md** | Este archivo (inicio) |
| **DOCUMENTACION.md** | Arquitectura técnica y métodos |
| **GUIA_RAPIDA.md** | Cómo usar con ejemplos |

---

## 🏗️ Arquitectura

\`\`\`
Main.java (Interfaz CLI)
    ↓
EquationSolverService (Orquestador)
    ├─ EcuationParser (Parseo)
    ├─ PolynomialSolver (Raíces)
    ├─ HomogeneousSolver (y_h)
    ├─ UndeterminedCoeff (y_p)
    ├─ VariationOfParametersSolverV2 (Fallback)
    └─ InitialConditionsSolver (PVI)
        └─ LinearSystemSolver (Sistema Lineal)
            └─ SymjaEngine (CAS)
\`\`\`

---

## 📊 Características Validadas

### ✅ Resolución de Ecuaciones
- Homogéneas de cualquier orden
- No-homogéneas con UC y VP
- Con condiciones iniciales (PVI)

### ✅ Generación de Salidas
- Solución homogénea limpia
- Solución particular sin artefactos
- Constantes numéricas calculadas
- Formato JSON estructurado

### ✅ Casos Especiales
- Raíces complejas
- Raíces repetidas
- Resonancia: Fallback automático UC→VP
- Coeficientes cero: Eliminados automáticamente

---

## 🔧 Estructura del Proyecto

\`\`\`
geogera/
├── README.md                          ← Este archivo
├── DOCUMENTACION.md                   ← Referencia técnica
├── GUIA_RAPIDA.md                    ← Cómo usar
├── pom.xml                            ← Configuración Maven
├── LICENSE                            ← Licencia
└── src/
    ├── main/java/com/ecuaciones/diferenciales/
    │   ├── Main.java                 ← Punto de entrada
    │   ├── model/
    │   ├── service/
    │   ├── evaluator/
    │   └── utils/
    └── test/java/
        └── com/ecuaciones/diferenciales/
\`\`\`

---

## 🎓 Nivel Académico: LICENCIATURA

### Conceptos Implementados

1. **Ecuaciones Diferenciales de Orden n**
   - Homogéneas y no-homogéneas
   - Coeficientes constantes
   - Raíces reales y complejas

2. **Métodos de Resolución**
   - Ecuación característica
   - Superposición
   - Coeficientes indeterminados
   - Variación de parámetros

3. **Problemas de Valor Inicial**
   - Aplicación de condiciones
   - Resolución de sistemas lineales
   - Cálculo de constantes

4. **Análisis Matemático**
   - Independencia lineal (Wronskiano)
   - Validación de soluciones
   - Simplificación de expresiones

---

## 📝 Licencia

Proyecto académico - Uso educativo (2025)

---

## 🎉 Estado Final

**🏆 COMPLETAMENTE FUNCIONAL Y LISTO PARA USO**

✅ Código limpio y organizado  
✅ Documentación completa  
✅ Ejemplos funcionando  
✅ Sin archivos temporales  

---

**Última actualización**: 17 de noviembre de 2025
