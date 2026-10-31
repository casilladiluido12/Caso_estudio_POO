package modelo;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Registro de un evento de transporte (entrada o salida de un usuario en una estación).
 *
    Es una clase inmutable al definirla como final, así que nadie puede crear una subclase que la haga mutable. 
    todos los atributos son private final se asignan una sola vez, en el constructor
    Sus métodos sonfunciones puras solo dependen de los atributos del propio registro
    y no modifican nada.
 */
public final class Registro {

    /** Identificador del usuario. */
    private final String idUsuario;

    /** Ruta en la que ocurre el evento. */
    private final String ruta;

    /** Estación en la que ocurre el evento. */
    private final String estacion;

    /** Acción registrada: ENTRADA SALIDA*/
    private final Accion accion;

    /** Momento exacto del evento. */
    private final LocalDateTime timestamp;

    public Registro(String idUsuario, String ruta, String estacion, Accion accion, LocalDateTime timestamp) {
        this.idUsuario = idUsuario;
        this.ruta = ruta;
        this.estacion = estacion;
        this.accion = accion;
        this.timestamp = timestamp;
    }

    
    public String getIdUsuario() {
        return idUsuario;
    }

    
    public String getRuta() {
        return ruta;
    }

    
    public String getEstacion() {
        return estacion;
    }

    
    public Accion getAccion() {
        return accion;
    }

    
    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    
    public boolean esEntrada() {
        return accion == Accion.ENTRADA;
    }

   
    public int getHora() {
        return timestamp.getHour();
    }

    /** Dos registros son iguales si tienen los mismos valores en todos sus atributos. */
    @Override
    public boolean equals(Object otro) {
        if (this == otro) {
            return true;
        }
        if (otro == null || getClass() != otro.getClass()) {
            return false;
        }
        Registro that = (Registro) otro;
        return Objects.equals(idUsuario, that.idUsuario)
                && Objects.equals(ruta, that.ruta)
                && Objects.equals(estacion, that.estacion)
                && accion == that.accion
                && Objects.equals(timestamp, that.timestamp);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idUsuario, ruta, estacion, accion, timestamp);
    }

    @Override
    public String toString() {
        return "Registro{idUsuario='" + idUsuario + "', ruta='" + ruta + "', estacion='" + estacion
                + "', accion=" + accion + ", timestamp=" + timestamp + "}";
    }
}
