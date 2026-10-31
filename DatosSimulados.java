package datos;

import modelo.Accion;
import modelo.Registro;

import java.time.LocalDate;
import java.util.List;

import static modelo.Accion.ENTRADA;
import static modelo.Accion.SALIDA;

/**
 * Datos simulados del sistema de transporte de TecnoValle.
*/
public final class DatosSimulados {

    private static final LocalDate DIA = LocalDate.of(2026, 10, 28);

    /** Los 30 registros, ordenados cronológicamente. Colección inmutable. */
    public static final List<Registro> REGISTROS = List.of(
            registro("U1", "R10", "Centro",  ENTRADA,  7,  5),
            registro("U2", "R10", "Centro",  ENTRADA,  7, 10),
            registro("U3", "R10", "Sur",     ENTRADA,  7, 15),
            registro("U1", "R10", "Norte",   SALIDA,   7, 25),
            registro("U4", "R20", "Terminal", ENTRADA,  7, 30),
            registro("U2", "R10", "Terminal", SALIDA,   7, 40),
            registro("U3", "R10", "Centro",  SALIDA,   7, 45),
            registro("U5", "R10", "Centro",  ENTRADA,  7, 50),
            registro("U4", "R20", "Sur",     SALIDA,   8,  5),
            registro("U6", "R10", "Centro",  ENTRADA,  8, 10),
            registro("U5", "R10", "Norte",   SALIDA,   8, 15),
            registro("U1", "R10", "Norte",   ENTRADA,  8, 20),
            registro("U1", "R10", "Centro",  SALIDA,   8, 45),
            registro("U6", "R10", "Sur",     SALIDA,   8, 50),
            registro("U2", "R20", "Terminal", ENTRADA, 12, 10),
            registro("U2", "R20", "Centro",  SALIDA,  12, 40),
            registro("U3", "R30", "Centro",  ENTRADA, 13,  0),
            registro("U3", "R30", "Norte",   SALIDA,  13, 30),
            registro("U4", "R10", "Sur",     ENTRADA, 17,  5),
            registro("U5", "R10", "Centro",  ENTRADA, 17, 20),
            registro("U4", "R10", "Centro",  SALIDA,  17, 35),
            registro("U6", "R10", "Norte",   ENTRADA, 17, 40),
            registro("U5", "R10", "Terminal", SALIDA,  17, 55),
            registro("U1", "R20", "Centro",  ENTRADA, 18,  5),
            registro("U6", "R10", "Centro",  SALIDA,  18, 10),
            registro("U1", "R20", "Sur",     SALIDA,  18, 40),
            registro("U3", "R30", "Norte",   ENTRADA, 19,  0),
            registro("U2", "R20", "Terminal", ENTRADA, 19, 15),
            registro("U3", "R30", "Terminal", SALIDA,  19, 30),
            registro("U2", "R20", "Norte",   SALIDA,  19, 50));

    private DatosSimulados() {
    }

    /** Fábrica de registros: construye el timestamp a partir de la hora y el minuto del día simulado. */
    private static Registro registro(String idUsuario, String ruta, String estacion,
                                     Accion accion, int hora, int minuto) {
        return new Registro(idUsuario, ruta, estacion, accion, DIA.atTime(hora, minuto));
    }
}
