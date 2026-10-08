package mx.generation.tareas;

/** Se lanza cuando se pide una tarea por un id que no existe. */
public class TareaNoEncontradaException extends RuntimeException {
    public TareaNoEncontradaException(int id) {
        super("No existe la tarea con id " + id);
    }
}
