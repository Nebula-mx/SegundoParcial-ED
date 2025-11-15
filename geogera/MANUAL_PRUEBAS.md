# 📖 Manual de Pruebas - Main Interactivo

## 🎯 Qué es esto

El **Main interactivo** es una herramienta temporal para pruebas manuales donde puedes:

1. ✅ **Ingresar una ecuación diferencial** en formato natural
2. ✅ **Agregar condiciones iniciales** (opcionales)
3. ✅ **Elegir método** para la solución particular (UC o VP)
4. ✅ **Ver la solución paso a paso**

---

## 🚀 Cómo usar

### Opción 1: Script directo

```bash
cd /home/hector_ar/Documentos/SegundoParcial-ED/geogera
chmod +x run_interactive.sh
./run_interactive.sh
```

### Opción 2: Maven directo

```bash
cd /home/hector_ar/Documentos/SegundoParcial-ED/geogera
mvn exec:java -Dexec.mainClass="com.ecuaciones.diferenciales.Main"
```

---

## 📝 Ejemplos de uso

### ✏️ Ejemplo 1: Ecuación Homogénea Simple (Sin CI)

```
Ecuación: y'' + y = 0
¿CI? n
```

**Salida:**
```
🔍 Raíces: i, -i
✅ y_h = C1 * cos(x) + C2 * sin(x)
```

---

### ✏️ Ejemplo 2: Ecuación No-Homogénea Con CI

```
Ecuación: y'' + 3y' + 2y = 1
¿CI? s
  y(0)=1
  y'(0)=0
  [Enter vacío para terminar]
Método: 1 (UC)
```

**Salida:**
```
🔍 Raíces: -1, -2
✅ y_h = C1*e^(-x) + C2*e^(-2x)
✓ Forma: y_p = A
✓ Sistema resuelto: A=0.5
📌 Solución: y(x) = C1*e^(-x) + C2*e^(-2x) + 0.5
CI: [y(0)=1, y'(0)=0]
```

---

### ✏️ Ejemplo 3: Con Resonancia

```
Ecuación: y'' - y = e^x
¿CI? n
Método: 1 (UC)
```

**Salida:**
```
🔍 Raíces: 1, -1
⚠️ Detección: Hay resonancia (1 es raíz)
✓ Forma ajustada: y_p = x * A * e^x
📌 Solución: y(x) = ... + x*e^x
```

---

### ✏️ Ejemplo 4: Trigonométrico

```
Ecuación: y'' + 4y = cos(2x)
¿CI? s
  [Ingresa CIs opcionales]
Método: 1 (UC)
```

---

### ✏️ Ejemplo 5: Orden Superior (Grado 3)

```
Ecuación: y''' + 2y'' + y' = 0
¿CI? n
```

> ⚠️ **Nota:** Para grado > 3, usa Symja (automático)

---

## 🎮 Navegación Interactiva

| Acción | Input |
|--------|-------|
| Ingresar ecuación | `y'' + 2y = e^x` |
| Sin CI | `n` |
| Con CI | `s` |
| Agregar CI | `y(0)=1` |
| Terminar CIs | `[Enter vacío]` |
| Coeficientes Indeterminados | `1` |
| Variación de Parámetros | `2` |

---

## 📋 Casos de Prueba Recomendados

### Homogéneas

```bash
y'' + y = 0
y'' - 4y' + 4y = 0
y''' - y = 0
y'''' + y'' + y = 0
```

### No-Homogéneas (Polinomial)

```bash
y'' + y = x
y'' + 2y' + y = x^2
y''' + y = 1
```

### No-Homogéneas (Exponencial)

```bash
y'' - y = e^x
y'' + y' = 2e^(-x)
```

### No-Homogéneas (Trigonométrica)

```bash
y'' + y = sin(x)
y'' + 4y = cos(2x)
y''' + y'' = sin(x)
```

### Con Resonancia

```bash
y'' - y = e^x
y'' + y = sin(x)
```

---

## 🔧 Elementos de UI

| Símbolo | Significado |
|---------|------------|
| 📝 | Entrada de datos |
| ❓ | Pregunta |
| ✅ | Completado exitosamente |
| 🔍 | Información de búsqueda/análisis |
| 🔌 | Término de forzamiento |
| 📌 | Nota importante |
| ❌ | Error |
| ⚠️ | Advertencia |
| ✨ | Finalización |

---

## 💡 Funcionalidades

- ✅ **Parser flexible:** Acepta notaciones `y''`, `dy/dx`, `d2y/dx2`
- ✅ **Detección automática:** Identifica homogéneas vs no-homogéneas
- ✅ **Resonancia:** Ajusta forma de `y_p` automáticamente
- ✅ **Múltiples métodos:** UC (recomendado) o VP
- ✅ **Condiciones iniciales:** Se guardan para futura integración web
- ✅ **Grados arbitrarios:** Usa Symja para grado > 2
- ✅ **Presentación clara:** Paso a paso con emojis

---

## ⚠️ Notas Importantes

1. **CIs temporales:** Las condiciones iniciales se muestran pero no se aplican aún (integración futura web)
2. **VP limitado:** Método de Variación de Parámetros llega hasta la formulación, no integra simbólicamente
3. **Grado > 2:** Usa Symja automáticamente para resolver polinomios característicos
4. **Notación:** Usa `*` para multiplicación, `^` para potencias, `e^x` para exponencial

---

## 🔗 Próximos Pasos

- [ ] Integración de CIs en solución general
- [ ] Interfaz web con React
- [ ] Soporte para condiciones de contorno
- [ ] Gráficas de soluciones
- [ ] Exportar a LaTeX

---

## 📞 Soporte

Si encuentras problemas:

1. Verifica que Maven esté instalado: `mvn -v`
2. Intenta recompilar: `mvn clean compile`
3. Revisa el formato de la ecuación
4. Verifica los logs de error en la salida

**Exitoso: 126 tests pasando ✅**
