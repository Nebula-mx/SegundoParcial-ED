# ⚡ Guía Rápida - Cómo Usar el Solver

## 🚀 Inicio Rápido

### 1. Preparar el Entorno
```bash
cd /home/hector_ar/Documentos/SegundoParcial-ED/geogera
mvn clean compile
```

### 2. Ejecutar Interactivamente
```bash
mvn exec:java -Dexec.mainClass="com.ecuaciones.diferenciales.Main"
```

### 3. Ingresar una Ecuación
```
Ejemplo: y'' - 5y' + 6y = 0
o bien: y'' + 4y = 8*cos(2*x)
o bien: y''' - y' = x^2
```

### 4. Especificar si hay Condiciones Iniciales
```
¿Es problema de valor inicial? (s/n): s
```

### 5. Ingresar las Condiciones (si aplica)
```
y(0)=1
y'(0)=0
```

---

## 📋 Ejemplos de Ecuaciones

### Homogénea Simple
```
Ecuación: y'' - 5y' + 6y = 0
Salida: y_h(x) = C1*e^(3x) + C2*e^(2x)
```

### Con Condiciones Iniciales
```
Ecuación: y'' - 5y' + 6y = 0
CI: y(0)=1, y'(0)=0
Salida: y(x) = 1.0*e^(3x) - 0.5*e^(2x)
```

### No-Homogénea (Coeficientes Indeterminados)
```
Ecuación: y'' + 4y = 8*cos(2*x)
Salida: 
  y_h = C1*cos(2x) + C2*sin(2x)
  y_p = 2*x*sin(2x)
  y_general = C1*cos(2x) + C2*sin(2x) + 2*x*sin(2x)
```

### Orden Superior
```
Ecuación: y''' - y' = x^2
Salida: 
  y_h = C1 + C2*e^x + C3*e^(-x)
  y_p = - 0.3333 * x^3
```

---

## 🔧 Uso Programático

### Código de Ejemplo
```java
import com.ecuaciones.diferenciales.service.EquationSolverService;
import com.ecuaciones.diferenciales.dto.DifferentialEquationResponse;

public class MiAplicacion {
    public static void main(String[] args) {
        EquationSolverService solver = new EquationSolverService();
        
        // Resolver ecuación con PVI
        DifferentialEquationResponse response = solver.solve(
            "y'' - 5y' + 6y = 0",
            true,
            "y(0)=1, y'(0)=0"
        );
        
        System.out.println("Solución: " + response.getGeneralSolution());
        System.out.println("C1 = " + response.getSolvedConstants().get("C1"));
        System.out.println("C2 = " + response.getSolvedConstants().get("C2"));
    }
}
```

---

## 📊 Formatos de Entrada Soportados

### Operadores
| Símbolo | Significado |
|---------|------------|
| `+` | Suma |
| `-` | Resta |
| `*` | Multiplicación |
| `^` | Potencia |
| `/` | División (raro) |

### Funciones
| Función | Ejemplo |
|---------|---------|
| Exponencial | `e^x`, `e^(2*x)` |
| Trigonométrica | `cos(x)`, `sin(2*x)` |
| Polinomial | `x^2`, `x^3 + 2*x` |
| Combinadas | `x*e^x`, `x^2*cos(x)` |

### Condiciones Iniciales
```
y(0)=1           # Valor inicial
y'(0)=2          # Primera derivada
y''(0)=0         # Segunda derivada
```

---

## ✅ Casos Especiales Manejados

| Caso | Cómo Funciona |
|------|--------------|
| **Resonancia** | Fallback automático UC → VP |
| **Raíces complejas** | Genera soluciones $e^{ax}\cos(bx)$, $e^{ax}\sin(bx)$ |
| **Raíces repetidas** | Agrega factor $x$ a términos: $C_1 e^{rx} + C_2 x e^{rx}$ |
| **Orden arbitrario** | Soporta órdenes 2, 3, 4, 5+ |
| **Coef. cero en y_p** | Limpia automáticamente $0 \cdot x$ terms |

---

## 🐛 Solución de Problemas

### "Error: No se puede parsear la ecuación"
✅ **Solución:** Asegúrate de usar `*` para multiplicación
```
❌ Incorrecto: y'' - 5y' + 6y = 0
✅ Correcto:   y'' - 5y' + 6y = 0
```

### "ArithmeticException en UC"
✅ **Solución:** Sistema automáticamente intenta VP
```
El solver automáticamente:
1. Intenta Coeficientes Indeterminados
2. Si falla → Intenta Variación de Parámetros
3. Si ambas fallan → Error descriptivo
```

### "Solución con muchos ceros"
✅ **Solución:** Es normal, se limpian automáticamente
```
Antes:  y_p = "0 + 0*x - 0.3333*x^3 + 0"
Después: y_p = "- 0.3333*x^3"
```

---

## 📦 Estructura de Salida

### JSON Response
```json
{
  "order": 2,
  "isHomogeneous": false,
  "homogeneousSolution": "C1*e^(3x) + C2*e^(2x)",
  "particularSolution": "- 0.3333*x^3",
  "generalSolution": "(C1*e^(3x) + C2*e^(2x)) + (- 0.3333*x^3)",
  "solvedConstants": {
    "C1": 1.0,
    "C2": -0.5
  },
  "finalSolution": "1.0*e^(3x) - 0.5*e^(2x) - 0.3333*x^3"
}
```

---

## 🎯 Comandos Maven Útiles

```bash
# Compilar
mvn clean compile

# Ejecutar interactivamente
mvn exec:java -Dexec.mainClass="com.ecuaciones.diferenciales.Main"

# Crear JAR
mvn clean package

# Limpiar archivos generados
mvn clean
```

---

## 💡 Tips & Tricks

1. **Copiar solución:** La salida en JSON es fácil de copiar a otras aplicaciones
2. **Formato LaTeX:** Las soluciones pueden convertirse a LaTeX fácilmente
3. **Orden arbitrario:** No hay límite teórico en el orden de la ecuación
4. **Precisión:** Usa valores flotantes para aproximaciones: `0.3333` en lugar de `1/3`

---

**¡Listo para usar! 🎉**

Para más detalles, consulta `DOCUMENTACION.md`
