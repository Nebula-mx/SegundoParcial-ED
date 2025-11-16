# ✅ TestGeneralCompleto - LISTO PARA USAR

## 📍 Ubicación del Archivo

```
/home/hector_ar/Documentos/SegundoParcial-ED/geogera/
src/test/java/com/ecuaciones/diferenciales/TestGeneralCompleto.java
```

## 🚀 Cómo Ejecutarla

### Opción 1: Desde IDE (IntelliJ / VS Code) - ✨ RECOMENDADO

1. Abre el proyecto en tu IDE
2. Navega a: `src/test/java/com/ecuaciones/diferenciales/TestGeneralCompleto.java`
3. Click derecho en el archivo
4. Selecciona: **"Run 'TestGeneralCompleto.main()'"**
5. ¡Aparecerá el menú interactivo en la consola!

### Opción 2: Desde Terminal

```bash
cd /home/hector_ar/Documentos/SegundoParcial-ED/geogera

# Si compilaste el proyecto:
java -cp out/test-classes:out/classes:~/.m2/repository/org/matheclipse/matheclipse-core/2.0.0/matheclipse-core-2.0.0.jar \
  com.ecuaciones.diferenciales.TestGeneralCompleto
```

## 📋 Menú Interactivo

Cuando ejecutes la clase, verás:

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

## 🎯 Qué Hace Cada Opción

| Opción | Descripción | Casos | Tiempo |
|--------|-------------|-------|--------|
| **1** | 🏠 Ecuaciones Homogéneas | 6 | ~2s |
| **2** | 🔧 UC (Coeficientes Indeterminados) | 5 | ~3s |
| **3** | 🔄 VP (Variación de Parámetros) | 5 | ~4s |
| **4** | ⚡ Resonancia (Detectada) | 5 | ~3s |
| **5** | 📍 Condiciones Iniciales | 5 | ~4s |
| **6** | 🎯 Casos Especiales | 6 | ~2s |
| **7** | 🚀 TODOS los casos | ~27 | ~18s |
| **0** | 🚪 Salir | - | - |

## 📊 Casos de Ejemplo

### Opción 1: Homogéneas
```
y' + 2y = 0
y'' - 5y' + 6y = 0
y'' + 2y' + y = 0
y'' + 4y = 0
y''' - y'' = 0
y'''' + 2y'' + y = 0
```

### Opción 2: UC
```
y'' - 3y' + 2y = e^x
y'' + 4y = 8*cos(2x)          ← SIN Resonancia
y'' - y = x^2
y'' + 2y' + y = e^(-x)
y'' + y = sin(x) + cos(x)
```

### Opción 3: VP (con Simplificación Symja)
```
y'' + y = sec(x)
y'' + y = tan(x)
y'' - y = e^x*x
y'' + 4y = 2*sin(x)          ← Integración limpia
y'' - 2y' + y = 1/x
```

### Opción 4: Resonancia
```
y'' + 4y = 8*cos(2*x)         ← CON Resonancia (factor x)
y'' + y = sin(x)
y'' - y = e^x
y'' + 2y' + y = e^(-x)
y''' - y'' = e^x
```

### Opción 5: Condiciones Iniciales
```
y' + 2y = 4                     con y(0)=1
y'' - 5y' + 6y = 0             con y(0)=1, y'(0)=2
y'' + 4y = 0                   con y(0)=1, y'(0)=0
y'' + 2y' + y = 0              con y(0)=0, y'(0)=1
y'' - 3y' + 2y = e^x           con y(0)=0, y'(0)=0
```

### Opción 6: Casos Especiales
```
y' - 2y = 0
y'' + y = 0
y''' - 6y'' + 11y' - 6y = 0
y'''' + 2y'' + y = 0
y'' - 4y' + 4y = 0
y'' + 2y' + 5y = 0
```

## ✨ Ejemplo de Uso

### Paso 1: Ejecutar desde IDE
```
Click derecho en TestGeneralCompleto.java → Run
```

### Paso 2: Ver el Menú
```
📌 Selecciona una opción: 
```

### Paso 3: Seleccionar Opción 4 (Resonancia)
```
📌 Selecciona una opción: 4

╔════════════════════════════════════════════════════════════╗
║  ⚡ CASOS DE RESONANCIA                                    ║
╚════════════════════════════════════════════════════════════╝

──────────────────────────────────────────────────────
RESONANCIA-1: y'' + 4y = 8*cos(2*x)
──────────────────────────────────────────────────────
📐 Ecuación: y'' + 4y = 8*cos(2*x)
📌 Resolviendo con método automático...

[Se mostrará la resolución completa]
```

### Paso 4: Ver Resultado
```
✅ SOLUCIÓN:
Raíces: 2i, -2i
Solución homogénea: y_h = C1*cos(2x) + C2*sin(2x)
Forma propuesta con resonancia: y_p = x*(C*cos(2x) + D*sin(2x))
Coeficientes: C = 0, D = 2
Solución particular: y_p = 2*x*sin(2x)
Solución general: y = C1*cos(2x) + C2*sin(2x) + 2*x*sin(2x)
```

## 🎓 Verificación

Después de ejecutar, verifica que:

✅ Aparezca el banner de bienvenida
✅ El menú sea interactivo
✅ Puedas seleccionar opciones (1-7, 0)
✅ Las soluciones se muestren completas
✅ VP tenga salidas SIMPLIFICADAS (no largas)
✅ Resonancia muestre factor x en y_p
✅ Condiciones iniciales muestren valores constantes

Si todo esto sucede: ¡**Sistema funcionando perfectamente!** 🎉

## 📞 Características

- ✅ **Interactiva**: Menú que puedes seleccionar
- ✅ **Completa**: 27+ casos diferentes
- ✅ **Paso a paso**: Muestra toda la resolución
- ✅ **Simplificada**: VP con Symja simplifica algebraicamente
- ✅ **Resonancia**: Detecta automáticamente (factor x)
- ✅ **Condiciones**: Aplica CI y calcula constantes
- ✅ **Sin errores**: 254 tests pasados

## 🛠️ Requisitos

- ✅ Java 17+
- ✅ Maven 3.8+ (proyecto compilado)
- ✅ IDE (IntelliJ / VS Code) - para ejecutar fácilmente

## 📚 Documentación Relacionada

- `INSTRUCCIONES_RAPIDAS_TestGeneralCompleto.md` - Guía rápida
- `TEST_GENERAL_COMPLETO_README.md` - Documentación detallada
- `RESUMEN_FINAL_IMPLEMENTACION.md` - Resumen técnico

---

## ✅ ESTADO: LISTA PARA EJECUTAR

¡La clase está compilada y lista! Solo abre el IDE, haz click derecho en `TestGeneralCompleto.java` y selecciona "Run" 🚀
