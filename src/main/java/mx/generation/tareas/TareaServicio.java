package mx.generation.tareas;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

/**
 * Casos de uso del gestor de tareas. Es la clase que usarían un controlador REST o una CLI.
 */
public class TareaServicio {

    private final TareaRepositorio repositorio;

    public TareaServicio(TareaRepositorio repositorio) {
        this.repositorio = repositorio;
    }

    public Tarea crear(String titulo, String descripcion, Prioridad prioridad, LocalDate fechaLimite) {
        return repositorio.guardar(new Tarea(titulo, descripcion, prioridad, fechaLimite));
    }

    /** Marca la tarea como completada. Si el id no existe, lanza TareaNoEncontradaException. */
    public Tarea completar(int id) {
        Tarea tarea = obtener(id);
        tarea.marcarCompletada();
        return tarea;
    }

    public boolean eliminar(int id) {
        return repositorio.eliminar(id);
    }

    public List<Tarea> listarPendientes() {
        List<Tarea> pendientes = new ArrayList<>();
        for (Tarea t : repositorio.todas()) {
            if (!t.isCompletada()) {
                pendientes.add(t);
            }
        }
        return pendientes;
    }

    /**
     * Tareas pendientes con prioridad igual o mayor a la indicada.
     * Con MEDIA devuelve las MEDIA y las ALTA; con ALTA, solo las ALTA.
     */
    public List<Tarea> listarPorPrioridadMinima(Prioridad minima) {
        List<Tarea> resultado = new ArrayList<>();
        for (Tarea t : listarPendientes()) {
            if (t.getPrioridad().ordinal() > minima.ordinal()) {
                resultado.add(t);
            }
        }
        return resultado;
    }

    /**
     * Días que faltan para la fecha límite: positivo si está en el futuro, 0 si es hoy,
     * negativo si ya se venció.
     */
    public long diasRestantes(int id, LocalDate hoy) {
        Tarea tarea = obtener(id);
        if (tarea.getFechaLimite() == null) {
            return Long.MAX_VALUE;
        }
        return ChronoUnit.DAYS.between(tarea.getFechaLimite(), hoy);
    }

    /**
     * Reporte en texto plano para el resumen diario.
     */
    public String generarReporte(LocalDate hoy) {
        StringBuilder sb = new StringBuilder();
        sb.append("=== REPORTE DE TAREAS ===\n");

        EstadisticasTareas stats = contarEstadisticas(hoy);

        sb.append("Total: ").append(stats.total).append("\n");
        sb.append("Completadas: ").append(stats.completadas).append("\n");
        sb.append("Pendientes: ").append(stats.total - stats.completadas).append("\n");
        sb.append("Vencidas: ").append(stats.vencidas).append("\n");
        sb.append("Alta prioridad pendientes: ").append(stats.altasPendientes).append("\n");
        sb.append("Estado: ").append(calificarAvance(stats.total, stats.completadas)).append("\n");
        sb.append("--- Pendientes ---\n");
        sb.append(listarPendientesTexto());

        return sb.toString();
    }

    private EstadisticasTareas contarEstadisticas(LocalDate hoy) {
        int total = 0;
        int completadas = 0;
        int vencidas = 0;
        int altasPendientes = 0;

        for (Tarea t : repositorio.todas()) {
            total++;
            if (t.isCompletada()) {
                completadas++;
            } else {
                if (estaVencida(t, hoy)) {
                    vencidas++;
                }
                if (t.getPrioridad() == Prioridad.ALTA) {
                    altasPendientes++;
                }
            }
        }

        return new EstadisticasTareas(total, completadas, vencidas, altasPendientes);
    }

    private boolean estaVencida(Tarea t, LocalDate hoy) {
        return t.getFechaLimite() != null && t.getFechaLimite().isBefore(hoy);
    }

    private String calificarAvance(int total, int completadas) {
        if (total == 0) {
            return "SIN TAREAS";
        }
        int porcentaje = completadas * 100 / total;
        if (porcentaje >= 80) {
            return "EXCELENTE (" + porcentaje + "%)";
        }
        if (porcentaje >= 50) {
            return "BIEN (" + porcentaje + "%)";
        }
        return "ATRASADO (" + porcentaje + "%)";
    }

    private String listarPendientesTexto() {
        StringBuilder sb = new StringBuilder();
        for (Tarea t : repositorio.todas()) {
            if (!t.isCompletada()) {
                sb.append("* ").append(t.getTitulo());
                if (t.getFechaLimite() != null) {
                    sb.append(" (vence ").append(t.getFechaLimite()).append(")");
                }
                sb.append("\n");
            }
        }
        return sb.toString();
    }

    private static class EstadisticasTareas {
        final int total;
        final int completadas;
        final int vencidas;
        final int altasPendientes;

        EstadisticasTareas(int total, int completadas, int vencidas, int altasPendientes) {
            this.total = total;
            this.completadas = completadas;
            this.vencidas = vencidas;
            this.altasPendientes = altasPendientes;
        }
    }

    private Tarea obtener(int id) {
        Tarea tarea = repositorio.buscar(id);
        if (tarea == null) {
            throw new TareaNoEncontradaException(id);
        }
        return tarea;
    }
}
