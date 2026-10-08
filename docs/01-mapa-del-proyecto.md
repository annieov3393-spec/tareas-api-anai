# Mapa del Proyecto: tareas-api

## 1. ¿Qué hace el proyecto?

Este proyecto es un gestor de tareas pendientes (TODO list) en memoria que permite crear, completar, eliminar y listar tareas con prioridades y fechas límite. Genera reportes diarios del estado de las tareas incluyendo cuántas están vencidas, completadas y pendientes.

## 2. Tabla de clases

| Clase | Responsabilidad | Depende de |
|-------|----------------|------------|
| `App` | Punto de entrada de la aplicación de consola; crea tareas de ejemplo y muestra el reporte | `TareaServicio`, `TareaRepositorio`, `Prioridad` |
| `Tarea` | Representa una tarea con título, descripción, prioridad, fecha límite y estado de completada | `Prioridad` |
| `Prioridad` | Enum que define los niveles de prioridad (BAJA, MEDIA, ALTA) | - |
| `TareaRepositorio` | Almacena tareas en memoria usando un Map, asigna IDs consecutivos y permite buscar, eliminar y listar | `Tarea` |
| `TareaServicio` | Casos de uso del negocio: crear, completar, eliminar tareas; listar por criterios; calcular días restantes; generar reportes | `TareaRepositorio`, `Tarea`, `Prioridad`, `TareaNoEncontradaException` |
| `TareaNoEncontradaException` | Excepción que se lanza cuando se busca una tarea que no existe | - |
| `TareaRepositorioTest` | Tests unitarios del repositorio | `TareaRepositorio`, `Tarea`, `Prioridad` |
| `TareaServicioTest` | Tests unitarios del servicio | `TareaServicio`, `TareaRepositorio`, `Tarea`, `Prioridad` |

## 3. Cómo se compila y se prueba

```bash
# Compilar el proyecto
mvn compile

# Ejecutar todos los tests
mvn test

# Ejecutar la aplicación de consola
mvn exec:java

# Limpiar y compilar desde cero
mvn clean compile

# Empaquetar como JAR
mvn package
```

## 4. Tres cosas sospechosas o incompletas

### 4.1. Cálculo de días restantes invertido
**Archivo:** `src/main/java/mx/generation/tareas/TareaServicio.java:67`

El método `diasRestantes()` calcula los días al revés usando `ChronoUnit.DAYS.between(tarea.getFechaLimite(), hoy)`. Esto devuelve -3 para una tarea que vence en 3 días (cuando debería ser +3), y +3 para una tarea vencida hace 3 días (cuando debería ser -3). Los parámetros deberían estar en orden inverso: `between(hoy, tarea.getFechaLimite())`.

### 4.2. Búsqueda por título no ignora mayúsculas
**Archivo:** `src/main/java/mx/generation/tareas/TareaRepositorio.java:44`

El método `buscarPorTitulo()` usa `contains()` directamente sin convertir a minúsculas, pero el comentario en las líneas 38-39 promete que "no distingue mayúsculas de minúsculas". Hay un test deshabilitado en `TareaRepositorioTest.java:33-39` que confirma que este caso falla. Debería usar `toLowerCase()` en ambos strings antes de comparar.

### 4.3. Filtro de prioridad excluye el nivel mínimo
**Archivo:** `src/main/java/mx/generation/tareas/TareaServicio.java:51`

El método `listarPorPrioridadMinima()` usa `t.getPrioridad().ordinal() > minima.ordinal()` en lugar de `>=`. Según el comentario en las líneas 45-46, si se pasa `MEDIA` debería devolver tareas MEDIA y ALTA, pero con `>` solo devuelve las ALTA (excluye las MEDIA). El test en `TareaServicioTest.java:52-58` espera 2 resultados con prioridad BAJA, lo que confirma el comportamiento incorrecto.
