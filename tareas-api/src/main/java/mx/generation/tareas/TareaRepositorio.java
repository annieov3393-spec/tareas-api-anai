package mx.generation.tareas;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Repositorio en memoria. Asigna ids consecutivos empezando en 1.
 */
public class TareaRepositorio {

    private final Map<Integer, Tarea> tareas = new LinkedHashMap<>();
    private int siguienteId = 1;

    public Tarea guardar(Tarea tarea) {
        if (tarea.getId() == 0) {
            tarea.setId(siguienteId++);
        }
        tareas.put(tarea.getId(), tarea);
        return tarea;
    }

    /** Devuelve la tarea o null si no existe. */
    public Tarea buscar(int id) {
        return tareas.get(id);
    }

    public boolean eliminar(int id) {
        return tareas.remove(id) != null;
    }

    public List<Tarea> todas() {
        return new ArrayList<>(tareas.values());
    }

    /**
     * Busca tareas cuyo título contenga el texto, sin distinguir mayúsculas de minúsculas.
     * Por ejemplo, "informe" encuentra "Entregar INFORME mensual".
     */
    public List<Tarea> buscarPorTitulo(String texto) {
        List<Tarea> resultado = new ArrayList<>();
        for (Tarea t : tareas.values()) {
            if (t.getTitulo().contains(texto)) {
                resultado.add(t);
            }
        }
        return resultado;
    }

    public int contar() {
        return tareas.size();
    }
}
