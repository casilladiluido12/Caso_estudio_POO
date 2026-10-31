package modelo;

/**
 * Acción registrada por un validador en una estación.
 * Se usa un enum (en lugar de los textos "entrada" y "salida") para evitar errores de escritura.
 */
public enum Accion {
    /** El usuario ingresa a la estación. */
    ENTRADA,
    /** El usuario valida para salir o cambia de ruta. */
    SALIDA
}
