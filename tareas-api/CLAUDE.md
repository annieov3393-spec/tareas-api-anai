# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

**tareas-api** es un gestor de tareas pendientes (TODO list) en Java 17 para la mentoría "Programar con Claude" de Generation México (CH70 Java).

## Commands

```bash
# Ejecutar tests
mvn test                    # todos los tests
mvn test -Dtest=TareaServicioTest  # solo una clase de test

# Compilar y ejecutar
mvn compile                 # compilar código fuente
mvn -q exec:java           # ejecutar App.java (demo de consola)
mvn clean package          # compilar y empaquetar JAR

# Verificar versiones
java -version              # debe ser Java 17+
mvn -v                     # debe ser Maven 3.8+
```

## Architecture

El proyecto sigue una arquitectura de tres capas:

### Entity Layer
- **`Tarea`**: Entidad del dominio con título, descripción, prioridad (enum `Prioridad`), fecha límite y estado de completada
- IDs asignados por el repositorio al guardar (0 = no guardada todavía)
- Inmutabilidad parcial: campos finales excepto `completada` e `id`

### Repository Layer  
- **`TareaRepositorio`**: Persistencia en memoria usando `LinkedHashMap<Integer, Tarea>`
- Asigna IDs consecutivos empezando en 1 con un contador `siguienteId`
- Métodos CRUD básicos + `buscarPorTitulo()` y `todas()`

### Service Layer
- **`TareaServicio`**: Casos de uso del negocio (crear, completar, eliminar, listar, reportes)
- Punto de entrada para controladores REST o CLI futuros
- Lanza `TareaNoEncontradaException` cuando un ID no existe (en `completar()` y `obtener()`)

### Entry Point
- **`App.main()`**: Demo de consola que crea tareas de ejemplo y muestra un reporte

## Known Issues

Hay 3 bugs documentados en `docs/01-mapa-del-proyecto.md`:

1. **`TareaServicio.diasRestantes()`** (línea 67): Calcula días al revés. Devuelve valores negativos para tareas futuras y positivos para tareas vencidas. Los parámetros de `ChronoUnit.DAYS.between()` están invertidos.

2. **`TareaRepositorio.buscarPorTitulo()`** (línea 44): No ignora mayúsculas/minúsculas aunque el Javadoc dice que sí. Test deshabilitado en `TareaRepositorioTest.buscarPorTituloIgnoraMayusculas()` confirma el bug.

3. **`TareaServicio.listarPorPrioridadMinima()`** (línea 51): Usa `>` en lugar de `>=`, excluyendo el nivel de prioridad mínimo que debería incluir según el Javadoc.

## Test Coverage

- Hay 13 tests: 12 pasan, 1 está deshabilitado (`@Disabled`) por el bug conocido de mayúsculas
- TODOs en tests señalan cobertura faltante:
  - `TareaServicioTest`: falta test para `TareaNoEncontradaException` (línea 39)
  - `TareaServicioTest`: no hay tests de `eliminar(int)` (línea 102)
  - `Tarea`: constructor no valida título vacío (línea 19)

## Important Patterns

- **Exception handling**: El servicio convierte `null` del repositorio en `TareaNoEncontradaException` (método privado `obtener()`)
- **String concatenation**: `generarReporte()` usa concatenación `r = r + ...` en lugar de `StringBuilder` (ineficiente pero legible para reportes pequeños)
- **Validation**: Validación mínima en constructores. `Tarea` acepta título vacío (TODO pendiente), pero requiere `prioridad` no-null
