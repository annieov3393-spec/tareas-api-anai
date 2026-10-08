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
        Tarea tarea = repositorio.buscar(id);
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
        String r = "";
        r = r + "=== REPORTE DE TAREAS ===\n";
        int total = 0;
        int hechas = 0;
        int vencidas = 0;
        int altas = 0;
        for (Tarea t : repositorio.todas()) {
            total = total + 1;
            if (t.isCompletada()) {
                hechas = hechas + 1;
            } else {
                if (t.getFechaLimite() != null) {
                    if (t.getFechaLimite().isBefore(hoy)) {
                        vencidas = vencidas + 1;
                    }
                }
                if (t.getPrioridad() == Prioridad.ALTA) {
                    altas = altas + 1;
                }
            }
        }
        r = r + "Total: " + total + "\n";
        r = r + "Completadas: " + hechas + "\n";
        r = r + "Pendientes: " + (total - hechas) + "\n";
        r = r + "Vencidas: " + vencidas + "\n";
        r = r + "Alta prioridad pendientes: " + altas + "\n";
        if (total > 0) {
            int porcentaje = hechas * 100 / total;
            if (porcentaje >= 80) {
                r = r + "Estado: EXCELENTE (" + porcentaje + "%)\n";
            } else if (porcentaje >= 50) {
                r = r + "Estado: BIEN (" + porcentaje + "%)\n";
            } else {
                r = r + "Estado: ATRASADO (" + porcentaje + "%)\n";
            }
        } else {
            r = r + "Estado: SIN TAREAS\n";
        }
        r = r + "--- Pendientes ---\n";
        for (Tarea t : repositorio.todas()) {
            if (!t.isCompletada()) {
                r = r + "* " + t.getTitulo();
                if (t.getFechaLimite() != null) {
                    r = r + " (vence " + t.getFechaLimite() + ")";
                }
                r = r + "\n";
            }
        }
        return r;
    }

    private Tarea obtener(int id) {
        Tarea tarea = repositorio.buscar(id);
        if (tarea == null) {
            throw new TareaNoEncontradaException(id);
        }
        return tarea;
    }
}
