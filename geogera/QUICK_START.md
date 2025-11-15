# ⚡ INICIO RÁPIDO - 5 MINUTOS

**Lee esto primero si tienes prisa.**

---

## 1️⃣ Descarga y Compila

```bash
cd ~/Documentos/SegundoParcial-ED/geogera
mvn clean package -q
# ✅ Listo. JAR en: target/geogera-0.1.jar
```

## 2️⃣ Inicia el Servidor

```bash
java -jar target/geogera-0.1.jar --server.port=5555
# ✅ Escucha en: http://localhost:5555
```

## 3️⃣ Prueba una Ecuación

```bash
curl -X POST http://localhost:5555/api/ode/solve \
  -H "Content-Type: application/json" \
  -d '{"equation":"y' + y = 0"}'
```

## 4️⃣ Ves Respuesta JSON

```json
{
  "status": "success",
  "finalSolution": "C1 * e^(-x)",
  "steps": [...]
}
```

## ✅ ¡Listo!

---

## 📚 Más Información

| Necesito... | Ir a... |
|-----------|---------|
| Entender rápido | [PARA_ISMA_FINAL.md](PARA_ISMA_FINAL.md) |
| Más ejemplos | [USAGE_EXAMPLES.md](USAGE_EXAMPLES.md) |
| Detalles técnicos | [SOLVER_TECHNICAL_GUIDE.md](SOLVER_TECHNICAL_GUIDE.md) |
| Índice completo | [DOCUMENTATION_INDEX.md](DOCUMENTATION_INDEX.md) |

---

**Estado**: ✅ Listo para producción  
**Versión**: 1.0  
**Última actualización**: 14 Nov 2025
