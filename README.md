# TecnoMovil Data: módulo de procesamiento funcional (EA2)

Solución en **Java estándar** (sin frameworks) al caso de estudio de transporte urbano de TecnoValle.
Aplica funciones puras, lambdas, funciones de orden superior, composición, Streams y colecciones inmutables.

## Estructura

```
src/
├── modelo/
│   ├── Registro.java            clase inmutable: atributos final, constructor y getters, sin setters
│   ├── Accion.java              enum ENTRADA / SALIDA
│   └── EstadoRuta.java          enum NORMAL / CRITICA
├── datos/
│   └── DatosSimulados.java      30 registros escritos a mano (lista inmutable)
├── analisis/
│   └── AnalisisTransporte.java  tareas a) a f) como funciones puras sobre Stream<Registro>
└── app/
    └── Main.java                orquesta e imprime los resultados
```

## Cómo ejecutar (JDK 16 o superior)

```
javac -d out $(find src -name "*.java")
java -cp out app.Main
```

En Windows (PowerShell): `javac -d out (Get-ChildItem -Recurse src -Filter *.java).FullName`

Se necesita JDK 16 o superior porque el código usa `Stream.toList()`.

## Dónde se ve cada concepto

| Concepto | Dónde |
|---|---|
| Funciones puras | `Registro.esEntrada()`, `Registro.getHora()` y todos los métodos de `AnalisisTransporte` (no imprimen ni modifican nada) |
| Inmutabilidad | `Registro` (clase `final`, atributos `final`, sin setters), `List.of` en `DatosSimulados`, `Map.copyOf`, `List.copyOf`, `toUnmodifiableMap`, `toList()` |
| Lambdas | Predicados (`ES_ENTRADA`, `CUALQUIERA`, `ES_TRASLADO`), claves de agrupación y comparadores |
| Funciones de orden superior | `contarPor(stream, filtro, clave)` recibe dos funciones; `horasPico(n)` y `rutasMasUsadas(n)` devuelven funciones |
| Composición | `ES_ENTRADA.negate()`, `ES_TRASLADO` (reutiliza los predicados), `registrosPorHora.andThen(topN)` |
| Streams | Todos los pipelines (no hay `for` ni `while`) |

## Resultados esperados (calculados a mano) vs. obtenidos

Los datos son 15 viajes (una entrada y su salida): 30 registros.

| Tarea | Resultado esperado | Obtenido |
|---|---|---|
| a) Afluencia por estación (entradas) | Centro 7, Norte 3, Sur 2, Terminal 3 | Coincide |
| b) Horas pico (top 3) | 07h: 8, 08h: 6, 17h: 5 | Coincide |
| c) Rutas más usadas (entradas) | R10: 9, R20: 4, R30: 2 | Coincide |
| d) Patrón de viaje | U1: Centro, Norte, Norte, Centro, Centro, Sur (y los demás usuarios) | Coincide |
| e) Tiempo promedio | 15 traslados, suma 460 min, promedio 30,7 min | Coincide |
| f) Rutas críticas (umbral 8) | R10 crítica (9 > 8); R20 y R30 normales | Coincide |

## Interpretaciones y limitaciones

- **Afluencia (a):** se cuentan eventos de entrada, no usuarios distintos.
- **Rutas (c, f):** el uso u ocupación de una ruta se mide por su número de entradas.
- **Tiempo entre estaciones (e):** un traslado es una entrada seguida de la siguiente salida del mismo usuario en otra estación; no se cuentan los tiempos de espera entre viajes.
- **Umbral (f):** es una constante simulada (`UMBRAL_OCUPACION = 8` en `Main`).
- **Volumen:** el caso habla de un conjunto grande de datos; esta entrega usa 30 registros para poder verificar a mano cada resultado.
