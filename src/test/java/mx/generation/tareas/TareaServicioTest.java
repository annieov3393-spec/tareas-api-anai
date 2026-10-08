package mx.generation.tareas;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TareaServicioTest {

    private static final LocalDate HOY = LocalDate.of(2026, 10, 8);

    private TareaServicio servicio;

    @BeforeEach
    void setUp() {
        servicio = new TareaServicio(new TareaRepositorio());
    }

    @Test
    void crearAsignaIdConsecutivo() {
        Tarea a = servicio.crear("Uno", "", Prioridad.BAJA, null);
        Tarea b = servicio.crear("Dos", "", Prioridad.BAJA, null);
        assertEquals(1, a.getId());
        assertEquals(2, b.getId());
    }

    @Test
    void completarMarcaLaTarea() {
        Tarea t = servicio.crear("Pagar luz", "", Prioridad.MEDIA, HOY);
        servicio.completar(t.getId());
        assertTrue(t.isCompletada());
        assertFalse(servicio.listarPendientes().contains(t));
    }

    // TODO: falta probar que completar(999) lanza TareaNoEncontradaException

    @Test
    void listarPendientesExcluyeCompletadas() {
        Tarea a = servicio.crear("A", "", Prioridad.BAJA, null);
        servicio.crear("B", "", Prioridad.BAJA, null);
        servicio.completar(a.getId());
        List<Tarea> pendientes = servicio.listarPendientes();
        assertEquals(1, pendientes.size());
        assertEquals("B", pendientes.get(0).getTitulo());
    }

    @Test
    void listarPorPrioridadMinimaBajaDevuelveMediaYAlta() {
        servicio.crear("baja", "", Prioridad.BAJA, null);
        servicio.crear("media", "", Prioridad.MEDIA, null);
        servicio.crear("alta", "", Prioridad.ALTA, null);
        List<Tarea> resultado = servicio.listarPorPrioridadMinima(Prioridad.BAJA);
        assertEquals(2, resultado.size());
    }

    @Test
    void diasRestantesDeUnaTareaFutura() {
        Tarea t = servicio.crear("Entrega", "", Prioridad.ALTA, HOY.plusDays(3));
        // Vence en 3 días
        assertEquals(-3, servicio.diasRestantes(t.getId(), HOY));
    }

    @Test
    void diasRestantesSinFechaEsMaximo() {
        Tarea t = servicio.crear("Sin fecha", "", Prioridad.BAJA, null);
        assertEquals(Long.MAX_VALUE, servicio.diasRestantes(t.getId(), HOY));
    }

    @Test
    void reporteConTareas() {
        servicio.crear("Informe", "", Prioridad.ALTA, HOY.plusDays(2));
        Tarea b = servicio.crear("Revisar PR", "", Prioridad.MEDIA, HOY.plusDays(1));
        servicio.crear("Dependencias", "", Prioridad.BAJA, HOY.minusDays(3));
        servicio.completar(b.getId());

        String esperado = String.join("\n",
                "=== REPORTE DE TAREAS ===",
                "Total: 3",
                "Completadas: 1",
                "Pendientes: 2",
                "Vencidas: 1",
                "Alta prioridad pendientes: 1",
                "Estado: ATRASADO (33%)",
                "--- Pendientes ---",
                "* Informe (vence 2026-10-10)",
                "* Dependencias (vence 2026-10-05)",
                "");
        assertEquals(esperado, servicio.generarReporte(HOY));
    }

    @Test
    void reporteSinTareas() {
        String reporte = servicio.generarReporte(HOY);
        assertTrue(reporte.contains("Estado: SIN TAREAS"));
        assertTrue(reporte.contains("Total: 0"));
    }

    // TODO: no hay pruebas de eliminar(int)
}
