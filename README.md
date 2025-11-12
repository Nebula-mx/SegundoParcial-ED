# SegundoParcial-ED

Un programa hecho para el segundo parcial de ecuaciones diferenciales

## Uso del singleton ExpressionData
este singleton contiene la informacion resultante del parse de la ecuacion una vez que paso por el input, para acceder a la informacion sin necesidad de llamar al metodo parse de EcuationParser una y otra vez, solo se tiene que declarar una variable de tipo **ExpressionData** y en lugar de llamar al constructor, hacer lo siguiente:

- `ExpressionData data = ExpressionData.getInstance();`
Con eso se puede acceder a toda la informacion sobre la ecuacion que se este usando mediante los getters y setters que proporciona.

# TODO-LIST

## Estructura Base & Entrada de Datos

- ✅ Diseñar interfaz de usuario (consola)
- ✅ Implementar parser de ecuaciones diferenciales (isma)
- ✅ Manejar entrada de condiciones iniciales (Alma)
- ✅ Validar formato de ecuaciones(Alma)
- ✅ Crear estructura de clases base

### archivos

- `main`
- `parser`
- `interface`
- `validator`

## Ecuaciones Homogéneas

- ✅ Resolver polinomio característico
- 🏗 Manejar raíces reales distintas/repetidas
- ✅ Manejar raíces complejas
- ✅ Generar solución general homogénea

### archivos

- `homogeneous` - Solucionador homogéneo
- `polynomial_solver` - Resolución de polinomios
- `roots_analyzer` - Análisis de raíces

## Coeficientes Indeterminados (Isma)

- 🏗 Identificar tipo de función no homogénea
- 🏗 Proponer forma de solución particular
- 🏗 Determinar coeficientes
- 🏗 Manejar casos de duplicación

### archivos

- `undetermined_coeff` - Método de coeficientes
- `function_analyzer` - Análisis de funciones
- `particular_solver` - Solución particular

## Variación de Parámetros (Isma)

- 🏗 Calcular Wronskiano
- 🏗 Calcular funciones u₁(x) y u₂(x)
- 🏗 Construir solución particular
- ❌ Aplicar condiciones iniciales

## archivos

- `variation_params` - Variación de parámetros
- `wronskian` - Cálculo del Wronskiano
- `integration` - Métodos de integración
