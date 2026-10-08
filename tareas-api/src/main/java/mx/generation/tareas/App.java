package mx.generation.tareas;

import java.time.LocalDate;

/** Demo de consola: crea unas tareas y muestra el reporte. */
public class App {
    public static void main(String[] args) {
        TareaServicio servicio = new TareaServicio(new TareaRepositorio());
        LocalDate hoy = LocalDate.now();
        servicio.crear("Entregar informe mensual", "Enviar a finanzas", Prioridad.ALTA, hoy.plusDays(2));
        servicio.crear("Revisar PR del equipo", "", Prioridad.MEDIA, hoy.plusDays(1));
        servicio.crear("Actualizar dependencias", "Maven", Prioridad.BAJA, hoy.minusDays(3));
        servicio.completar(2);
        System.out.println(servicio.generarReporte(hoy));
    }
}
