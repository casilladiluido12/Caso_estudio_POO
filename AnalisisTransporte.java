package analisis;

import modelo.EstadoRuta;
import modelo.Registro;

import java.time.Duration;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
    Operaciones funcionales sobre los registros de transporte (tareas a) a f) del caso).
 */
public final class AnalisisTransporte {

    private AnalisisTransporte() {
    }

    // ------------------------------------------------------------------
    // Predicados y su composición
    // ------------------------------------------------------------------

    /** Predicado: el registro es una entrada. */
    private static final Predicate<Registro> ES_ENTRADA = Registro::esEntrada;

    /** Predicado compuesto: una salida es lo contrario de una entrada. */
    private static final Predicate<Registro> ES_SALIDA = ES_ENTRADA.negate();

    /** Predicado que acepta cualquier registro. */
    private static final Predicate<Registro> CUALQUIERA = registro -> true;

    /** Dos registros consecutivos forman un traslado si van de una entrada a una salida en otra estación. */
    private static final BiPredicate<Registro, Registro> ES_TRASLADO = (origen, destino) ->
            ES_ENTRADA.test(origen)
                    && ES_SALIDA.test(destino)
                    && !origen.getEstacion().equals(destino.getEstacion());

    // ------------------------------------------------------------------
    // Funciones de orden superior reutilizables
    // ------------------------------------------------------------------

    /**
        Cuenta los registros que cumplen un filtro, agrupados por una clave.
        Es una función de orden superior: recibe DOS funciones filtro y  clave.
     */
    static <K> Map<K, Long> contarPor(Stream<Registro> registros,
                                      Predicate<Registro> filtro,
                                      Function<Registro, K> clave) {
        return registros
                .filter(filtro)
                .collect(Collectors.collectingAndThen(
                        Collectors.groupingBy(clave, Collectors.counting()),
                        Map::copyOf));
    }

    /**
     * Las claves con mayor conteo, de mayor a menor (desempata por clave para que el resultado sea estable).
     *
     *  lista inmutable de entradas clave -> cantidad
     */
    public static <K extends Comparable<K>> List<Map.Entry<K, Long>> topN(Map<K, Long> conteos, int n) {
        return conteos.entrySet().stream()
                .sorted(Map.Entry.<K, Long>comparingByValue().reversed()
                        .thenComparing(Map.Entry.<K, Long>comparingByKey()))
                .limit(n)
                .toList();
    }

    // ------------------------------------------------------------------
    // a) Afluencia por estación
    // ------------------------------------------------------------------

    /** Cuántas entradas hay en cada estación durante el día. */
    public static Map<String, Long> afluenciaPorEstacion(Stream<Registro> registros) {
        return contarPor(registros, ES_ENTRADA, Registro::getEstacion);
    }

    // ------------------------------------------------------------------
    // b) Horas pico
    // ------------------------------------------------------------------

    /** Cuántos registros hay en cada hora del día (0 a 23). */
    public static Map<Integer, Long> registrosPorHora(Stream<Registro> registros) {
        return contarPor(registros, CUALQUIERA, Registro::getHora);
    }

    /**
     * Devuelve una FUNCIÓN que, dado un flujo de registros, entrega las horas de mayor flujo.
     * Es composición: registrosPorHora seguida de topN.
     */
    public static Function<Stream<Registro>, List<Map.Entry<Integer, Long>>> horasPico(int n) {
        Function<Stream<Registro>, Map<Integer, Long>> porHora = AnalisisTransporte::registrosPorHora;
        return porHora.andThen(conteos -> topN(conteos, n));
    }

    // ------------------------------------------------------------------
    // c) Rutas más utilizadas
    // ------------------------------------------------------------------

    /** Volumen de uso (cantidad de entradas) de cada ruta. */
    public static Map<String, Long> usoPorRuta(Stream<Registro> registros) {
        return contarPor(registros, ES_ENTRADA, Registro::getRuta);
    }

    /**
     * Devuelve una FUNCIÓN que, dado un flujo de registros, entrega las rutas más usadas.
     * Es composición: usoPorRuta seguida de topN.
     */
    public static Function<Stream<Registro>, List<Map.Entry<String, Long>>> rutasMasUsadas(int n) {
        Function<Stream<Registro>, Map<String, Long>> uso = AnalisisTransporte::usoPorRuta;
        return uso.andThen(conteos -> topN(conteos, n));
    }

    // ------------------------------------------------------------------
    // d) Patrones de viaje por usuario
    // ------------------------------------------------------------------

    /**
     * Para cada usuario, la lista de estaciones en el orden en que aparecen sus registros.
     *
     * @return mapa inmutable usuario -> lista inmutable de estaciones
     */
    public static Map<String, List<String>> patronesDeViaje(Stream<Registro> registros) {
        return registros.collect(Collectors.collectingAndThen(
                Collectors.groupingBy(Registro::getIdUsuario,
                        Collectors.mapping(Registro::getEstacion,
                                Collectors.collectingAndThen(Collectors.toList(), List::copyOf))),
                Map::copyOf));
    }

    // ------------------------------------------------------------------
    // e) Tiempo promedio entre estaciones
    // ------------------------------------------------------------------

    /**
     * Minutos que tarda cada traslado (de una entrada a la salida siguiente del mismo usuario en otra estación).
     * Los registros de cada usuario se ordenan por hora antes de formar los pares.
     */
    public static List<Long> minutosEntreEstaciones(Stream<Registro> registros) {
        return registros
                .collect(Collectors.groupingBy(Registro::getIdUsuario))
                .values().stream()
                .flatMap(AnalisisTransporte::traslados)
                .toList();
    }

    /** Promedio, en minutos, de todos los traslados (0.0 si no hay ninguno). */
    public static double tiempoPromedioEntreEstaciones(Stream<Registro> registros) {
        return minutosEntreEstaciones(registros).stream()
                .mapToLong(Long::longValue)
                .average()
                .orElse(0.0);
    }

    /** Traslados de un usuario: pares consecutivos de sus registros (ordenados) que cumplen  ES_TRASLADO. */
    private static Stream<Long> traslados(List<Registro> registrosDelUsuario) {
        List<Registro> ordenados = registrosDelUsuario.stream()
                .sorted(Comparator.comparing(Registro::getTimestamp))
                .toList();
        return IntStream.range(1, ordenados.size())
                .filter(i -> ES_TRASLADO.test(ordenados.get(i - 1), ordenados.get(i)))
                .mapToObj(i -> Duration.between(
                        ordenados.get(i - 1).getTimestamp(),
                        ordenados.get(i).getTimestamp()).toMinutes());
    }

    // ------------------------------------------------------------------
    // f) Detección de sobrecarga en rutas
    // ------------------------------------------------------------------

    /**
     * Marca cada ruta como CRITICA si su uso supera el umbral (simulado),
     * o NORMAL en caso contrario.
     *
     *  mapa inmutable ruta -> estado
     */
    public static Map<String, EstadoRuta> estadoDeRutas(Map<String, Long> usoPorRuta, long umbral) {
        return usoPorRuta.entrySet().stream()
                .collect(Collectors.toUnmodifiableMap(
                        Map.Entry::getKey,
                        entrada -> entrada.getValue() > umbral ? EstadoRuta.CRITICA : EstadoRuta.NORMAL));
    }
}
