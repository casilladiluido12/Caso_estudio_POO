package app;

import analisis.AnalisisTransporte;
import datos.DatosSimulados;
import modelo.Registro;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Punto de entrada: solo orquesta las tareas e imprime los resultados.
 * Toda la lógica de análisis está en  AnalisisTransporte, que no imprime ni modifica nada;
 * los efectos (escribir en consola) quedan aquí, en el borde del programa.
 */
public class Main {

    /** Umbral de ocupación simulado: una ruta con más entradas que este valor es "crítica". */
    private static final long UMBRAL_OCUPACION = 8;

    private static final DateTimeFormatter HORA = DateTimeFormatter.ofPattern("HH:mm");

    public static void main(String[] args) {
        List<Registro> registros = DatosSimulados.REGISTROS;

        mostrarDatos(registros);
        mostrarTareas(registros);
    }

    // ------------------------------------------------------------------
    // Datos de entrada
    // ------------------------------------------------------------------
    private static void mostrarDatos(List<Registro> registros) {
        titulo("DATOS DE ENTRADA (" + registros.size() + " registros)");
        registros.forEach(r -> System.out.printf("   %-3s %-4s %-9s %-8s %s%n",
                r.getIdUsuario(), r.getRuta(), r.getEstacion(), r.getAccion(), r.getTimestamp().format(HORA)));
    }

    // ------------------------------------------------------------------
    // Tareas a) a f)
    // ------------------------------------------------------------------
    private static void mostrarTareas(List<Registro> registros) {
        titulo("a) AFLUENCIA POR ESTACION (entradas)");
        new TreeMap<>(AnalisisTransporte.afluenciaPorEstacion(registros.stream()))
                .forEach((estacion, cantidad) -> System.out.printf("   %-9s %d%n", estacion, cantidad));

        titulo("b) HORAS PICO (3 horas con mas registros)");
        AnalisisTransporte.horasPico(3).apply(registros.stream())
                .forEach(e -> System.out.printf("   %02d:00 - %02d:59   %d registros%n", e.getKey(), e.getKey(), e.getValue()));

        titulo("c) RUTAS MAS UTILIZADAS (entradas)");
        AnalisisTransporte.rutasMasUsadas(3).apply(registros.stream())
                .forEach(e -> System.out.printf("   %-4s %d%n", e.getKey(), e.getValue()));

        titulo("d) PATRON DE VIAJE POR USUARIO (estaciones en orden de aparicion)");
        new TreeMap<>(AnalisisTransporte.patronesDeViaje(registros.stream()))
                .forEach((usuario, estaciones) -> System.out.printf("   %-3s %s%n", usuario, estaciones));

        titulo("e) TIEMPO PROMEDIO ENTRE ESTACIONES");
        List<Long> minutos = AnalisisTransporte.minutosEntreEstaciones(registros.stream());
        System.out.printf("   %d traslados, promedio %.1f minutos%n",
                minutos.size(), AnalisisTransporte.tiempoPromedioEntreEstaciones(registros.stream()));

        titulo("f) SOBRECARGA EN RUTAS (umbral = " + UMBRAL_OCUPACION + " entradas)");
        Map<String, Long> uso = AnalisisTransporte.usoPorRuta(registros.stream());
        new TreeMap<>(AnalisisTransporte.estadoDeRutas(uso, UMBRAL_OCUPACION))
                .forEach((ruta, estado) -> System.out.printf("   %-4s %-8s (%d entradas)%n", ruta, estado, uso.get(ruta)));
    }

    private static void titulo(String texto) {
        System.out.println("\n== " + texto + " ==");
    }
}
