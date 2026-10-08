package mx.generation.tareas;

import java.time.LocalDate;
import java.util.Objects;

/**
 * Una tarea pendiente. El id lo asigna el repositorio al guardarla (0 = todavía no guardada).
 */
public class Tarea {

    private int id;
    private final String titulo;
    private final String descripcion;
    private final Prioridad prioridad;
    private final LocalDate fechaLimite;
    private boolean completada;

    public Tarea(String titulo, String descripcion, Prioridad prioridad, LocalDate fechaLimite) {
        // TODO: validar que el título no venga vacío ni en blanco (hoy se acepta "")
        this.titulo = titulo;
        this.descripcion = descripcion == null ? "" : descripcion;
        this.prioridad = Objects.requireNonNull(prioridad, "prioridad");
        this.fechaLimite = fechaLimite;
        this.completada = false;
    }

    public int getId() { return id; }
    void setId(int id) { this.id = id; }
    public String getTitulo() { return titulo; }
    public String getDescripcion() { return descripcion; }
    public Prioridad getPrioridad() { return prioridad; }
    public LocalDate getFechaLimite() { return fechaLimite; }
    public boolean isCompletada() { return completada; }

    public void marcarCompletada() { this.completada = true; }

    @Override
    public String toString() {
        return "#" + id + " [" + prioridad + "] " + titulo + (completada ? " (hecha)" : "");
    }
}
