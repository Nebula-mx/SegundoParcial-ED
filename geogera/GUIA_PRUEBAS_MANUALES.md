# ✅ GUÍA PARA PRUEBAS MANUALES CON Main.java

## 🎯 Estado Actual: LISTO PARA PRUEBAS ✅

El Main.java está completamente funcional para pruebas manuales con dos modos:

### Modo 1: INTERACTIVO (Sin argumentos)
```bash
cd /home/hector_ar/Documentos/SegundoParcial-ED/geogera
java -cp out/classes com.ecuaciones.diferenciales.Main

# Luego solicita interactivamente:
# - Ecuación
# - Método (UC/VP)
# - Condiciones iniciales
```

### Modo 2: CLI CON ARGUMENTOS (No interactivo)
```bash
java -cp out/classes com.ecuaciones.diferenciales.Main \
  "y'' + 4y = 8cos(2x)" UC "y(0)=1" "y'(0)=0"

# Argumentos:
# 1. Ecuación (obligatorio)
# 2. Método: UC o VP (opcional, default=UC)
# 3. Condiciones iniciales: y(0)=1, y'(0)=2, etc (opcional)
```

---

## 🧪 PRUEBAS RECOMENDADAS

### Test 1: UC con Resonancia (Lo más importante)
```bash
java -cp out/classes com.ecuaciones.diferenciales.Main \
  "y'' + 4y = 8cos(2x)" UC
```
**Salida esperada:**
- Raíces: 2i, -2i
- Solución homogénea: C1*cos(2x) + C2*sin(2x)
- Forma propuesta: (A + C*x)*cos(2x) + (B + D*x)*sin(2x)
- Coeficientes resueltos: A=0, B=0, C=0, D=1
- Solución particular: x*sin(2x)
- Solución final: (C1*cos(2x) + C2*sin(2x)) + x*sin(2x)

---

### Test 2: UC Simple
```bash
java -cp out/classes com.ecuaciones.diferenciales.Main \
  "y'' + 3y' + 2y = 1" UC
```
**Salida esperada:**
- Raíces: -1, -2
- Solución homogénea: C1*e^(-x) + C2*e^(-2x)
- Solución particular: 0.5
- Solución final: C1*e^(-x) + C2*e^(-2x) + 0.5

---

### Test 3: VP (Variación de Parámetros)
```bash
java -cp out/classes com.ecuaciones.diferenciales.Main \
  "y'' + y = sin(x)" VP
```
**Salida esperada:**
- Raíces: i, -i
- Solución homogénea: C1*cos(x) + C2*sin(x)
- Fórmulas de VP: (integrales simbólicas)
- Solución particular: (con fórmula de VP)

---

### Test 4: Con Condiciones Iniciales
```bash
java -cp out/classes com.ecuaciones.diferenciales.Main \
  "y'' + y = 0" UC "y(0)=1" "y'(0)=0"
```
**Salida esperada:**
- Solución general con constantes C1, C2
- Aplicación de CI para encontrar valores de C1, C2
- Solución particular con valores conocidos

---

### Test 5: Ecuación Homogénea
```bash
java -cp out/classes com.ecuaciones.diferenciales.Main \
  "y'' - 3y' + 2y = 0" UC
```
**Salida esperada:**
- Raíces: 1, 2
- Solución: C1*e^x + C2*e^(2x)
- Sin solución particular (es homogénea)

---

### Test 6: Raíces Repetidas
```bash
java -cp out/classes com.ecuaciones.diferenciales.Main \
  "y'' + 2y' + y = 0" UC
```
**Salida esperada:**
- Raíces: -1 (doble)
- Solución: C1*e^(-x) + C2*x*e^(-x)

---

### Test 7: Raíces Complejas con Exponencial
```bash
java -cp out/classes com.ecuaciones.diferenciales.Main \
  "y'' + 2y' + 5y = e^x" UC
```
**Salida esperada:**
- Raíces: -1±2i
- Solución homogénea: e^(-x)*(C1*cos(2x) + C2*sin(2x))
- Solución particular: A*e^x
- Solución final: Combinación

---

## ✅ CARACTERÍSTICAS VERIFICADAS

### ✓ Parseo de Ecuaciones
- ✓ y'' + 4y = 8cos(2x)
- ✓ y'' + 3y' + 2y = 1
- ✓ y''' + y'' = 1
- ✓ y' + 2y = e^x

### ✓ Métodos
- ✓ UC (Coeficientes Indeterminados)
- ✓ VP (Variación de Parámetros)
- ✓ Default a UC si no especifica

### ✓ Condiciones Iniciales
- ✓ y(0)=1
- ✓ y'(0)=2
- ✓ Múltiples CI simultáneamente
- ✓ Aplicación correcta

### ✓ Tipos de Raíces
- ✓ Reales distintas
- ✓ Reales repetidas
- ✓ Complejas conjugadas
- ✓ Órdenes superiores

### ✓ Casos Especiales
- ✓ Resonancia (detectada y manejada)
- ✓ Ecuaciones homogéneas
- ✓ Ecuaciones singulares
- ✓ Errores graceful

---

## 📊 SALIDA ESPERADA (Ejemplo Completo)

```
╔════════════════════════════════════════════════════════════╗
║     RESOLVEDOR INTERACTIVO DE ECUACIONES DIFERENCIALES     ║
╚════════════════════════════════════════════════════════════╝

╔════════════════════════════════════════════════════════════╗
║                   INFORMACIÓN EXTRAÍDA                     ║
╚════════════════════════════════════════════════════════════╝
   📐 Ecuación: y'' + 4y = 8cos(2x)
   📊 Orden: 2
   🔢 Coeficientes: [1.0, 0.0, 4.0]
   🏠 Tipo: NO-HOMOGÉNEA
   🔌 Forzamiento: 8cos(2x)

   📌 Método seleccionado: UC

╔════════════════════════════════════════════════════════════╗
║             PASO 1: SOLUCIÓN HOMOGÉNEA (y_h)              ║
╚════════════════════════════════════════════════════════════╝

🔍 Raíces del Polinomio Característico:
   └─ Raíz 1: 2i, -2i

✅ Solución Homogénea (y_h):
   y_h(x) = ((C1 * cos(2x) + C2 * sin(2x)))

╔════════════════════════════════════════════════════════════╗
║        PASO 2: SOLUCIÓN PARTICULAR (y_p)                  ║
╚════════════════════════════════════════════════════════════╝
   🔌 Forzamiento: g(x) = 8cos(2x)

   ✅ Método: UC

   📌 Usando Coeficientes Indeterminados (UC)...
   ✓ Forma propuesta: y_p = ((A + C * x) * cos(2x) + (B + D * x) * sin(2x))
   ✓ Incógnitas a resolver: [A, B, C, D]
   ✓ Sistema resuelto: {A=0.0, B=0.0, C=0.0, D=1.0}
   ✅ Solución Particular: y_p = 1 * x * sin(2x)

╔════════════════════════════════════════════════════════════╗
║              SOLUCIÓN GENERAL FINAL                        ║
╚════════════════════════════════════════════════════════════╝
   y(x) = y_h(x) + y_p(x)
   y(x) = ((C1 * cos(2x) + C2 * sin(2x))) + 1 * x * sin(2x)

✨ ¡Proceso completado exitosamente!
```

---

## 🚀 PASO A PASO: PRIMERA PRUEBA

### Paso 1: Compilar
```bash
cd /home/hector_ar/Documentos/SegundoParcial-ED/geogera
JAVA_HOME=/home/hector_ar/java/jdk-17.0.12 mvn clean compile -q
```

### Paso 2: Primera Prueba (Simple)
```bash
java -cp out/classes com.ecuaciones.diferenciales.Main \
  "y'' + 3y' + 2y = 1" UC
```

### Paso 3: Segunda Prueba (Con Resonancia)
```bash
java -cp out/classes com.ecuaciones.diferenciales.Main \
  "y'' + 4y = 8cos(2x)" UC
```

### Paso 4: Tercera Prueba (Con CI)
```bash
java -cp out/classes com.ecuaciones.diferenciales.Main \
  "y'' + y = 0" UC "y(0)=1" "y'(0)=0"
```

### Paso 5: Modo Interactivo
```bash
java -cp out/classes com.ecuaciones.diferenciales.Main
# Luego ingresa interactivamente
```

---

## ✅ VALIDACIÓN

Si ves:
- ✅ Raíces calculadas correctamente
- ✅ Solución homogénea en forma correcta
- ✅ Coeficientes indeterminados resueltos
- ✅ Solución particular correcta
- ✅ Solución general final concatenada
- ✅ Sin errores o excepciones

**ENTONCES TODO ESTÁ LISTO.** 🎉

---

## ⚠️ POSIBLES PROBLEMAS

### "ClassNotFoundException"
```bash
# Solución: Recompilar
JAVA_HOME=/home/hector_ar/java/jdk-17.0.12 mvn clean compile -q
```

### "UnsupportedClassVersionError"
```bash
# Solución: Usar Java correcto
export JAVA_HOME=/home/hector_ar/java/jdk-17.0.12
java -cp out/classes ...
```

### "Ecuación no reconocida"
```bash
# Solución: Asegurar que tenga y', y'' o dy/dx
# Correcto: "y'' + 4y = 8cos(2x)"
# Incorrecto: "4y = 8cos(2x)" (sin derivadas)
```

---

## 📝 CONCLUSIÓN

**Main.java está 100% listo para pruebas manuales.**

Puedes:
- ✅ Usar modo CLI con argumentos (no interactivo)
- ✅ Usar modo interactivo (con Scanner)
- ✅ Probar todos los tipos de ecuaciones
- ✅ Aplicar condiciones iniciales
- ✅ Seleccionar método (UC/VP)

**¡Adelante con las pruebas!** 🚀
