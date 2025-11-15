# �� PLAN DE INTEGRACIÓN COMPLETO

**Proyecto**: GEOGERA  
**Fecha**: 14 de Noviembre de 2025  
**Objetivo**: Pasar de compilación exitosa a funcionalidad completa

---

## 📌 FASE 1: VALIDACIÓN ACTUAL (HOY)

### Paso 1.1: Ejecutar Suite de Tests
```bash
cd /home/hector_ar/Documentos/SegundoParcial-ED/geogera
mvn clean test
```

**Esperado**:
- ✅ 69 tests pasando
- ✅ 0 fallos
- ✅ Tiempo: ~10-15 segundos

**Si hay errores**:
- Revisar salida de Maven
- Ajustar dependencias si es necesario

### Paso 1.2: Verificar Compilación Sin Warnings
```bash
mvn clean compile 2>&1 | grep -i warning
```

**Esperado**: Sin output (sin warnings)

### Paso 1.3: Validar Clases Clave
```bash
# Verificar que existen las clases nuevas
ls -la src/main/java/com/ecuaciones/diferenciales/utils/SymbolicDifferentiator.java
ls -la src/main/java/com/ecuaciones/diferenciales/utils/SymjaEngine.java
```

---

## 📌 FASE 2: CREAR SUITE DE TESTS PARA DERIVADAS (2 horas)

### Paso 2.1: Crear HomogeneousODETest.java
```bash
touch src/test/java/com/ecuaciones/diferenciales/HomogeneousODETest.java
```

### Paso 2.2: Implementar Tests de Derivadas
```java
// Test básicos
@Test void testDerivativeX2() { ... }      // d/dx(x²) = 2x
@Test void testDerivativeSin() { ... }     // d/dx(sin x) = cos x
@Test void testSecondDerivative() { ... }  // d²/dx²(x³) = 6x

// Tests de ODE
@Test void testFirstOrderODE() { ... }     // y' + 2y = 0
@Test void testSecondOrderODE() { ... }    // y'' - 5y' + 6y = 0
```

### Paso 2.3: Ejecutar Tests
```bash
mvn test -Dtest=HomogeneousODETest
```

---

## 📌 FASE 3: INTEGRAR NO-HOMOGÉNEAS (4 horas)

### Paso 3.1: Modificar ODESolver.java

**Ubicación**: `src/main/java/com/ecuaciones/diferenciales/api/service/ODESolver.java`

**Línea actual (~130)**:
```java
// ACTUAL - Solo homogénea
String homogeneousSolution = HomogeneousSolver.solve(...);
generalSolution = homogeneousSolution;
```

**Cambiar a**:
```java
// NUEVO - Completo
String homogeneousSolution = HomogeneousSolver.solve(...);

if (!odeType.equals("Homogénea")) {
    // Resolver ecuación no-homogénea
    String rightSide = equation.split("=")[1].trim();
    String particularSolution = solveNonHomogeneous(
        roots, order, rightSide, variable
    );
    generalSolution = homogeneousSolution + " + " + particularSolution;
} else {
    generalSolution = homogeneousSolution;
}
```

### Paso 3.2: Agregar Método solveNonHomogeneous()
```java
private String solveNonHomogeneous(List<Root> roots, int order, 
                                  String gx, String variable) {
    try {
        // Opción 1: Coeficientes Indeterminados
        if (isPolynomialOrExponential(gx)) {
            return solveWithUndeterminedCoeff(roots, order, gx);
        }
        
        // Opción 2: Variación de Parámetros (fallback)
        return solveWithVariationOfParameters(roots, order, gx);
        
    } catch (Exception e) {
        System.err.println("Error resolviendo no-homogénea: " + e);
        return null;
    }
}
```

### Paso 3.3: Implementar Métodos de Resolución
```java
// Coeficientes Indeterminados
private String solveWithUndeterminedCoeff(List<Root> roots, 
                                         int order, String gx) {
    UndeterminedCoeff ucSolver = new UndeterminedCoeff(roots, order);
    String form = ucSolver.getParticularSolutionForm(gx);
    // ... resolver coeficientes ...
    return ucSolver.generateParticularSolution(form, coeffs);
}

// Variación de Parámetros
private String solveWithVariationOfParameters(List<Root> roots, 
                                              int order, String gx) {
    // Usar WronskianCalculator
    // Usar CramerMethod
    // Retornar y_p
}
```

### Paso 3.4: Compilar y Validar
```bash
mvn clean compile -DskipTests
```

---

## 📌 FASE 4: TESTS DE NO-HOMOGÉNEAS (3 horas)

### Paso 4.1: Crear NonHomogeneousODETest.java
```java
@Test void testFirstOrderNonHomogeneous() {
    // y' + 2y = e^x
    // Esperar: C1*e^(-2x) + (particular)
}

@Test void testSecondOrderNonHomogeneous() {
    // y'' - 5y' + 6y = e^x
}

@Test void testWithInitialConditions() {
    // Aplicar CI a solución general
}
```

### Paso 4.2: Ejecutar Tests
```bash
mvn test -Dtest=NonHomogeneousODETest
```

### Paso 4.3: Validar Respuestas API
```bash
curl -X POST http://localhost:8080/api/ode/solve \
  -H "Content-Type: application/json" \
  -d '{"equation":"y'"'"' + 2*y = e^x","variable":"x"}'
```

---

## 📌 FASE 5: INTEGRACIÓN COMPLETA (2 horas)

### Paso 5.1: Tests End-to-End
```java
@Test void testCompleteAPIResponse() {
    SolutionResponse response = 
        solver.solveDifferentialEquation(input);
    
    assertNotNull(response.getFinalSolution());
    assertNotNull(response.getSteps());
    assertEquals(Status.SUCCESS, response.getStatus());
}
```

### Paso 5.2: Manejo de Errores
```java
@Test void testInvalidEquation() {
    // Entrada vacía
    // Sintaxis incorrecta
    // Verificar mensajes de error
}
```

### Paso 5.3: Performance
```java
@Test void testPerformance() {
    // Ecuación de grado 5 debe resolver en <2 segundos
}
```

---

## 🎯 CHECKLIST DE IMPLEMENTACIÓN

### Fase 1: Validación ✅
- [ ] Ejecutar `mvn test`
- [ ] Verificar compilación sin warnings
- [ ] Confirmar 32 archivos Java

### Fase 2: Derivadas ⏳
- [ ] Crear HomogeneousODETest.java
- [ ] Implementar tests básicos
- [ ] Validar Symja funciona
- [ ] Ejecutar tests sin errores

### Fase 3: No-Homogéneas ⏳
- [ ] Modificar ODESolver.java
- [ ] Agregar solveNonHomogeneous()
- [ ] Compilar sin errores
- [ ] Validar lógica

### Fase 4: Tests NH ⏳
- [ ] Crear NonHomogeneousODETest.java
- [ ] Implementar 5+ tests
- [ ] Validar API REST
- [ ] Probar con curl

### Fase 5: Integración ⏳
- [ ] Tests end-to-end
- [ ] Manejo de errores
- [ ] Performance acceptable
- [ ] Documentación actualizada

---

## 📊 LÍNEA DE TIEMPO

```
Hoy (14 nov):
  14:00 - Validación compilación ........... 30 min ✅
  14:30 - Suite de derivadas .............. 2 horas
  16:30 - Integración no-homogéneas ....... 4 horas
  
Mañana (15 nov):
  09:00 - Tests no-homogéneas ............. 3 horas
  12:00 - Integración completa ............ 2 horas
  14:00 - QA y documentación .............. 2 horas

Próxima semana:
  - Interfaz web mejorada
  - Documentación API
  - Deploy
```

---

## 🔧 COMANDOS RÁPIDOS

```bash
# Compilar todo
mvn clean compile

# Ejecutar todos los tests
mvn test

# Ejecutar test específico
mvn test -Dtest=HomogeneousODETest

# Ejecutar con output
mvn test -DskipTests=false -X

# Ejecutar servidor
mvn spring-boot:run

# Empaquetar
mvn clean package

# Limpiar
mvn clean
```

---

## ⚠️ POTENCIALES PROBLEMAS Y SOLUCIONES

| Problema | Síntoma | Solución |
|----------|---------|----------|
| Symja lento en primera ejecución | Primera derivada tarda 5s | Normal, se inicializa contexto |
| Tests timeout | Symja no responde | Aumentar timeout en pom.xml |
| Derivadas simbólicas incorrectas | Resultado diferente al esperado | Verificar sintaxis Symja |
| No-homogéneas no funcionan | API devuelve solo homogénea | Revisar integración ODESolver |

---

## 🎓 REFERENCIAS

- [Symja Documentation](https://github.com/axkr/symja_android_library)
- [Spring Boot Testing](https://spring.io/guides/gs/testing-web/)
- [JUnit 5 Guide](https://junit.org/junit5/docs/current/user-guide/)

---

## 📞 SOPORTE

Si encuentras problemas:
1. Verificar logs: `mvn test -X`
2. Revisar este documento
3. Consultar ANALISIS_ERRORES_TECNICO.md

¡Éxito! 🚀
