# ⚡ INSTRUCCIONES RÁPIDAS - TestGeneralCompleto

## 🎯 Objetivo
Ejecutar la clase interactiva que prueba **TODOS** los casos de ecuaciones diferenciales.

---

## 🚀 Ejecución Rápida

### **Método 1: Terminal (Recomendado)**
```bash
cd /home/hector_ar/Documentos/SegundoParcial-ED/geogera
mvn exec:java -Dexec.mainClass="com.ecuaciones.diferenciales.TestGeneralCompleto"
```

### **Método 2: Desde IDE**
1. Abrir: `src/test/java/com/ecuaciones/diferenciales/TestGeneralCompleto.java`
2. Click derecho → "Run 'TestGeneralCompleto.main()'"
3. Ver menú en consola

---

## 📋 Qué Verás

### Banner de Bienvenida:
```
╔════════════════════════════════════════════════════════════╗
║  🎓 PRUEBAS GENERALES - ECUACIONES DIFERENCIALES 🎓        ║
║                                                            ║
║  ✅ Homogéneas (todas las órdenes)                         ║
║  ✅ No-Homogéneas (UC, VP)                                 ║
║  ✅ Resonancia detectada automáticamente                   ║
║  ✅ Condiciones iniciales (CI)                             ║
║  ✅ Simplificación algebraica con Symja                    ║
║                                                            ║
╚════════════════════════════════════════════════════════════╝
```

### Menú Principal:
```
┌─────────────────────────────────────────────────────┐
│  MENÚ PRINCIPAL                                     │
├─────────────────────────────────────────────────────┤
│  1. 🏠 Ecuaciones Homogéneas                        │
│  2. 🔧 No-Homogéneas: Coeficientes Indeterminados │
│  3. 🔄 No-Homogéneas: Variación de Parámetros      │
│  4. ⚡ Casos de Resonancia                         │
│  5. 📍 Condiciones Iniciales                       │
│  6. 🎯 Casos Especiales                            │
│  7. 🚀 EJECUTAR TODOS LOS CASOS                    │
│  0. 🚪 Salir                                        │
└─────────────────────────────────────────────────────┘

📌 Selecciona una opción: 
```

---

## 📊 Opciones y Lo Que Muestran

| Opción | Casos | Tiempo | Descripción |
|--------|-------|--------|-------------|
| 1 | 6 | ~2s | Órdenes 1, 2, 3+ |
| 2 | 5 | ~3s | UC con e^x, cos, sin, polinomios |
| 3 | 5 | ~4s | VP con sec, tan, exponencial |
| 4 | 5 | ~3s | Resonancia pura, cruzada, múltiple |
| 5 | 5 | ~4s | CI aplicadas a soluciones generales |
| 6 | 6 | ~2s | Raíces repetidas, complejas, etc. |
| 7 | ~27 | ~18s | TODOS los casos anteriores |

---

## ✨ Ejemplo de Salida

Cuando selecciones una opción, verás:

```
──────────────────────────────────────────────────────
CASO UC-1: y'' - 3y' + 2y = e^x
──────────────────────────────────────────────────────
📐 Ecuación: y'' - 3y' + 2y = e^x
📊 Orden: 2
📌 Método: UC

✅ SOLUCIÓN:

Raíces: r = 1, r = 2
Solución homogénea: y_h = C1*e^x + C2*e^(2x)
Forma propuesta: y_p = A*e^x
Sistema: [1 2; 1 4] * [A B]^T = [1 0]^T
Coeficientes: A = 0.5, B = 0
Solución particular: y_p = 0.5*e^x
Solución general: y = C1*e^x + C2*e^(2x) + 0.5*e^x
```

---

## 🎯 Casos Importantes a Verificar

### **Resonancia (Opción 4)**
```
Caso: y'' + 4y = 8*cos(2*x)

Salida incluirá:
⚠️ Sistema singular detectado (posible RESONANCIA).
   La forma con factor x ya fue propuesta automáticamente.

y_p = 2*x*sin(2*x)  ← IMPORTANTE: Tiene factor x
```

### **VP con Simplificación (Opción 3)**
```
Caso: y'' + 4y = 2*sin(x)

Salida incluirá:
u1(x) = -Sin(x)/2+Sin(3*x)/6
u2(x) = Cos(x)/2-Cos(3*x)/6

Forma Simplificada:
y_p(x) = 2/3*Sin(x)  ← SIMPLIFICADO con Symja
```

### **Condiciones Iniciales (Opción 5)**
```
Caso: y'' - 5y' + 6y = 0, y(0)=1, y'(0)=2

Salida incluirá:
Solución con constantes: y(x) = C1*e^x + C2*e^(2x)
Aplicando CI: y(0)=1 → C1 + C2 = 1
             y'(0)=2 → C1 + 2*C2 = 2
Solución particular: y(x) = 2*e^(2x) - e^x
```

---

## 🔍 Qué Verificar en la Salida

✅ **Formato correcto**: Todas las ecuaciones deben mostrar estructura completa
✅ **Resonancia detectada**: Casos con resonancia muestran "⚠️ Sistema singular"
✅ **Simplificación**: VP muestra fórmula SIMPLIFICADA de y_p (no productos largos)
✅ **Condiciones aplicadas**: CI casos muestran valores numéricos de constantes
✅ **Sin errores**: No debe haber mensajes de error rojo

---

## 🎬 Ejemplo Completo: Opción 7 (TODOS)

```bash
$ mvn exec:java -Dexec.mainClass="com.ecuaciones.diferenciales.TestGeneralCompleto"

[Banner y menú]

📌 Selecciona una opción: 7

🚀 EJECUTANDO TODOS LOS CASOS

✅ 1. HOMOGÉNEAS

──────────────────────────────────────────────────────
CASO 1: y' + 2y = 0
──────────────────────────────────────────────────────
[Solución...]

[... más casos ...]

✅ 2. NO-HOMOGÉNEAS (UC)

[... casos UC ...]

✅ 3. NO-HOMOGÉNEAS (VP)

[... casos VP con simplificación ...]

✅ 4. RESONANCIA

[... casos con resonancia detectada ...]

✅ 5. CONDICIONES INICIALES

[... casos con CI aplicadas ...]

✅ 6. CASOS ESPECIALES

[... casos especiales ...]

════════════════════════════════════════════════════════
✨ TODAS LAS PRUEBAS COMPLETADAS ✨
════════════════════════════════════════════════════════

📌 Selecciona una opción: 
```

---

## 💡 Tips

1. **Ejecutar lentamente**: Selecciona **opción 7** para ver TODOS
2. **Verificar resonancia**: Selecciona **opción 4** específicamente
3. **Ver VP limpio**: Selecciona **opción 3** para ver simplificación
4. **Verificar CI**: Selecciona **opción 5** para ver constantes calculadas

---

## 🆘 Si Algo Falla

### Error: "Class not found"
```bash
mvn clean compile  # Recompilar
```

### Error: "No pom.xml"
```bash
cd /home/hector_ar/Documentos/SegundoParcial-ED/geogera  # Ir a carpeta correcta
```

### Error: Symja
```bash
mvn clean package -DskipTests  # Descargar dependencias
```

---

## ✅ Verificación Final

Después de ejecutar, verifica:

1. ✅ Banner de bienvenida aparece
2. ✅ Menú principal se muestra
3. ✅ Puedes seleccionar opciones
4. ✅ Aparecen soluciones completas
5. ✅ VP muestra fórmulas SIMPLIFICADAS
6. ✅ Resonancia muestra "⚠️ Sistema singular"
7. ✅ Sin errores rojos en consola

**Si todo esto se cumple: ¡Sistema funcionando correctamente!** 🎉

---

## 📞 Archivos Relacionados

- 📄 `TestGeneralCompleto.java` - Código fuente
- 📄 `TEST_GENERAL_COMPLETO_README.md` - Documentación detallada
- 📄 `RESUMEN_FINAL_IMPLEMENTACION.md` - Resumen técnico

---

**¡Listo para usar!** 🚀
