package modelo;

/** Estado de ocupación de una ruta (tarea f). */
public enum EstadoRuta {
    /** La ocupación está por debajo o igual al umbral. */
    NORMAL,
    /** La ocupación supera el umbral. */
    CRITICA
}
