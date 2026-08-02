# Guía de aprendizaje: cómo funciona GolApp Attendances

Esta guía explica el proyecto siguiendo el recorrido real de los datos. El objetivo no es memorizar archivos, sino entender qué responsabilidad tiene cada capa y por qué esta separación ayuda a construir aplicaciones Android mantenibles.

## 1. Visión general

La aplicación usa Android moderno con:

- Kotlin y corrutinas para el trabajo asíncrono.
- Jetpack Compose para construir la interfaz de manera declarativa.
- Navigation 3 para representar la navegación como un `backStack` de destinos.
- ViewModel y `StateFlow` para conservar y exponer el estado de cada pantalla.
- Hilt para crear e inyectar dependencias.
- Casos de uso y repositorios para separar las reglas del negocio de la UI.
- Room como fuente local observable.
- Retrofit/OkHttp para comunicarse con el backend.
- DataStore para conservar la sesión.
- WorkManager para reintentar sincronizaciones en segundo plano.

El flujo principal se puede resumir así:

```text
Acción del usuario
      ↓
Composable / Screen
      ↓ evento
ViewModel
      ↓
Caso de uso
      ↓
Interfaz de Repository (dominio)
      ↓
Implementación del Repository (datos)
      ↓                    ↓
Room / DAO          RemoteDataSource / API
      ↓
Flow de datos actualizado
      ↓
ViewModel produce UiState
      ↓
Compose se recompone
```

La idea central es el **flujo unidireccional de datos**: la UI envía eventos hacia abajo y recibe estado nuevo hacia arriba. La pantalla no modifica directamente la base de datos ni llama a Retrofit.

## 2. Estructura real del proyecto

Aunque existen directorios `common`, `data` y `domain` en la raíz, actualmente `settings.gradle.kts` incluye únicamente el módulo `:app`. Dentro de ese módulo sí hay una separación lógica por paquetes:

```text
app/src/main/java/com/golapp/attendances/
├── core/       Infraestructura compartida: DI, navegación, red y workers
├── data/       Room, Retrofit, DTO, entidades, mappers y repositories
├── domain/     Modelos, contratos de repositories y casos de uso
├── feature/    UI y ViewModels organizados por funcionalidad
├── ui/theme/   colores, tipografía, formas y tema Material 3
├── MainActivity.kt
└── GolAppApplication.kt
```

Esto es una **separación por capas dentro de un solo módulo**, no una arquitectura multimódulo. Es una decisión válida para una aplicación de este tamaño: permite aprender y mantener límites claros sin introducir todavía la complejidad de varios módulos Gradle.

## 3. Arranque de la aplicación y sesión

### 3.1 `GolAppApplication`

`GolAppApplication` es el objeto de mayor duración del proceso. Configura componentes globales como Hilt, Timber, Coil y WorkManager. Se mantiene pequeño deliberadamente: una `Application` no debería conocer la lógica particular de cada pantalla.

### 3.2 `MainActivity`

`MainActivity.onCreate()` realiza cuatro tareas importantes:

1. Instala el splash screen.
2. solicita a `MainViewModel` resolver la sesión mediante `start()`;
3. observa `uiState` con `collectAsStateWithLifecycle()`;
4. cuando termina la carga, crea la navegación en `Home` o `Authentication`.

El splash permanece visible mientras la sesión está cargando o la navegación todavía no está preparada. Esto evita mostrar brevemente Login antes de descubrir que el usuario ya tenía una sesión válida.

`LaunchedEffect(uiState.isLoggedIn)` reemplaza el stack cuando cambia la sesión. Por ejemplo, al cerrar sesión no se agrega Login encima de Home: se reemplaza el historial, evitando que el botón Atrás regrese a una pantalla autenticada.

### 3.3 `MainViewModel`

`MainViewModel` es la fuente de verdad de la sesión para la raíz de la aplicación:

- `start()` es idempotente: la bandera `started` evita iniciar dos observaciones iguales.
- `checkLoginUseCase()` devuelve un `Flow<Boolean>` que informa cambios de sesión.
- si existe sesión, obtiene el usuario y publica un nuevo `HomeUiState`;
- `logout()` evita dos cierres simultáneos mediante `logoutJob`.

El ViewModel no contiene vistas ni referencias a la Activity. Esto permite que sobreviva a cambios de configuración y que su lógica pueda probarse fuera de Compose.

## 4. Compose: UI como función del estado

Una pantalla Compose sigue esta idea:

```kotlin
val uiState by viewModel.uiState.collectAsStateWithLifecycle()

ScreenContent(
    uiState = uiState,
    onEvent = viewModel::onEvent,
)
```

`collectAsStateWithLifecycle()` convierte el `StateFlow` en estado de Compose y deja de recolectar cuando la UI no está activa. Esto evita trabajo innecesario y es más seguro que recolectar manualmente sin considerar el ciclo de vida.

Las funciones de contenido reciben datos y callbacks. En lo posible, no conocen Hilt ni buscan el ViewModel por su cuenta. Esta separación permite:

- crear previews con estados falsos;
- probar la UI de forma aislada;
- reutilizar componentes;
- hacer evidente qué datos consume y qué acciones produce cada pantalla.

### UI adaptable

La app consulta `currentWindowAdaptiveInfo()` en lugar de asumir que todos los dispositivos son teléfonos.

En asistencias:

- `Compact`: la selección se expande dentro de la lista, apropiada para poco ancho.
- `Medium/Expanded`: `ListDetailPaneScaffold` presenta lista y detalle según el espacio disponible.

La adaptación se decide por el ancho disponible, no por nombres como “tablet”. Esto funciona mejor con ventanas redimensionables y plegables.

## 5. Estado, eventos y efectos

`AttendancesViewModel` ofrece el ejemplo más completo del patrón usado.

### Estado persistente de pantalla: `AttendancesUiState`

Contiene todo lo necesario para dibujar una captura de la pantalla:

- texto de búsqueda;
- día de clase seleccionado;
- lista filtrada;
- asistencia seleccionada;
- carga y sincronización;
- error bloqueante.

Es una `data class` inmutable. Cada versión describe la UI completa en ese instante. No se exponen varios booleanos mutables directamente a la pantalla.

### Eventos: `AttendancesUiEvent`

Describen intenciones del usuario, por ejemplo:

- buscar;
- seleccionar un jugador;
- tomar asistencia;
- sincronizar;
- reintentar.

La UI comunica **qué ocurrió**, no cómo debe ejecutarse. `onEvent()` traduce la intención a la operación correspondiente.

### Efectos: `AttendancesUiEffect`

Un Snackbar no es estado persistente de la pantalla: debe mostrarse una sola vez. Por eso los mensajes y acciones de reintento viajan por un `SharedFlow` con `replay = 0`.

La distinción es:

- `StateFlow`: conserva el último valor; sirve para dibujar.
- `SharedFlow` de efectos: entrega sucesos puntuales; sirve para Snackbar u otras acciones de una sola vez.

No conviene modelar un Snackbar como `error: String?` y luego “consumirlo” desde Compose, porque las recomposiciones y recreaciones pueden repetirlo.

## 6. Cómo se construye el estado de asistencias

El ViewModel mantiene pequeños flujos internos:

- `classDayIdFlow`;
- `queryFlow`;
- `selectedKeyFlow`;
- `isSyncingFlow`;
- `retryGenerationFlow`.

Después combina los resultados con `combine(...)` para producir un único `AttendancesUiState` público.

Conceptualmente:

```text
día + asistencias + búsqueda + selección + sincronización
                         ↓ combine
                AttendancesUiState
```

`flatMapLatest` es importante al cambiar de día: cancela la observación anterior y conserva solamente la petición más reciente. Sin esta política, un resultado antiguo podría llegar tarde y reemplazar los datos del día actual.

`stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), initialState)` convierte el flujo calculado en `StateFlow`:

- siempre existe un valor inicial;
- el último estado queda disponible para nuevos observadores;
- la recolección se mantiene brevemente durante cambios de configuración para evitar reinicios innecesarios.

El reintento incrementa `retryGenerationFlow`. Reasignar el mismo ID no funcionaría porque `StateFlow` no emite un valor igual; la generación garantiza una emisión nueva sin alterar la identidad del día.

## 7. Restauración con `SavedStateHandle`

Un ViewModel sobrevive a una rotación, pero no necesariamente a la muerte del proceso. `SavedStateHandle` conserva claves pequeñas para reconstruir la pantalla:

- el email del login, pero nunca la contraseña;
- la búsqueda;
- el ID del día o grupo;
- la clave del elemento seleccionado.

No se guardan listas completas, respuestas HTTP ni objetos pesados. Esos datos durables pertenecen a Room o DataStore y se vuelven a consultar. La restauración correcta consiste en guardar la **llave para reconstruir**, no duplicar toda la base de datos dentro del estado de UI.

## 8. Casos de uso y capa de dominio

La capa `domain` expresa el lenguaje del negocio sin conocer Compose, Retrofit ni las entidades de Room.

Ejemplo: `TakeAttendanceUseCase` recibe `AttendanceWithPlayer`, valida que haya un ID y un valor, y solicita al contrato `AttendanceRepository` actualizar la asistencia.

Un caso de uso es valioso cuando:

- representa una operación del negocio;
- combina repositorios;
- aplica validaciones o reglas;
- facilita probar la regla sin Android.

No necesita ser una clase enorme. Su valor está en que la UI depende de una intención como “tomar asistencia”, no de detalles como `UPDATE attendances` o `POST /attendance`.

Las interfaces de repository viven en `domain/repositories`. Esto invierte la dependencia:

```text
ViewModel → UseCase → AttendanceRepository (interfaz)
                              ↑
                   AttendanceRepositoryImpl
```

El dominio define lo que necesita; la capa de datos decide cómo cumplirlo.

## 9. Repository: coordinación local y remota

`AttendanceRepositoryImpl` coordina Room y el servidor. Al cambiar una asistencia ejecuta una estrategia **local-first**:

1. actualiza inmediatamente Room;
2. busca el registro actualizado;
3. intenta enviarlo al backend;
4. si funciona, elimina cualquier pendiente de sincronización;
5. si falla, inserta el ID en `attendance_sync` para reintentarlo después.

Gracias a esto, la UI recibe el cambio desde el `Flow` de Room sin esperar la red. Es una base de funcionamiento offline: la base local es la fuente observable para la pantalla y la red sirve para sincronizar.

Un matiz del comportamiento actual es que el repositorio captura el fallo remoto después de guardar localmente y lo encola. Por tanto, el caso de uso normalmente termina con éxito aunque la red haya fallado; el error queda registrado y pendiente de sincronización. Esta política debe entenderse como una decisión de producto: prioriza que el instructor pueda seguir tomando asistencia sin conexión.

Las operaciones masivas usan `db.withTransaction`. Una transacción garantiza que el conjunto de cambios se aplique completo o no se aplique, evitando bases parcialmente actualizadas.

## 10. DAO, entidades y modelos

Room usa tres conceptos principales:

- `Entity`: forma en que se guarda un registro en SQLite.
- `Dao`: consultas y modificaciones permitidas.
- `Database`: punto de acceso y límite de las transacciones.

Las consultas que devuelven `Flow` son observables. Cuando `updateValueById()` modifica una fila relacionada, Room vuelve a ejecutar la consulta observada y emite una lista nueva. Esa emisión sube por Repository → ViewModel → Compose, produciendo la recomposición automática.

No se usan las entidades de Room como modelos de UI. Los `mappers` convierten entre:

```text
DTO remoto ↔ modelo de dominio ↔ Entity local
```

Esta separación evita que un cambio en el JSON o en la tabla SQLite obligue a cambiar todas las pantallas.

## 11. RemoteDataSource y Retrofit

`AttendanceRemoteDataSourceImpl` encapsula `GolappAPI` y realiza trabajo concreto:

- construye parámetros de las solicitudes;
- llama al endpoint adecuado;
- extrae `data` de la respuesta;
- convierte DTO y requests hacia/desde modelos de dominio.

Por eso esta capa no es un wrapper inútil. Mantiene Retrofit y el contrato HTTP fuera del Repository y del dominio. Si se eliminara, esas responsabilidades tendrían que trasladarse explícitamente a otro lugar.

Los interceptores agregan preocupaciones transversales como autenticación y logging. `TokenAuthenticator` atiende la renovación de credenciales cuando corresponde. Estas piezas deben permanecer fuera de los ViewModels: una pantalla no debería saber cómo se agrega un Bearer token.

## 12. Concurrencia y cancelación

Las corrutinas permiten ejecutar trabajo sin bloquear el hilo principal, pero requieren una política clara.

### `viewModelScope`

Las tareas iniciadas en `viewModelScope` se cancelan cuando el ViewModel se destruye. Esto evita que una pantalla abandonada continúe publicando estado.

### Dispatchers inyectados

El dispatcher de IO se inyecta con Hilt. Así el código indica dónde ocurre el trabajo y las pruebas pueden reemplazar el dispatcher por uno controlable.

### `Mutex`

`AttendancesViewModel` usa:

- `attendanceMutationMutex.withLock`: serializa cambios de asistencia para conservar su orden;
- `syncMutex.tryLock`: ignora un segundo toque de sincronización mientras el primero sigue activo.

Esto resuelve condiciones de carrera reales. No todo evento necesita un `Mutex`; se usa donde dos trabajos simultáneos producirían duplicados o un orden ambiguo.

### `CancellationException`

Los `catch (Exception)` llaman a `rethrowIfCancellation()`. Cancelar una corrutina no es un error de negocio. Si se transforma la cancelación en `Result.Failure` o en un Snackbar, el trabajo puede continuar indebidamente y la UI mostraría errores falsos al navegar.

## 13. Inyección de dependencias con Hilt

Hilt construye el grafo de objetos:

- `@Inject constructor` explica cómo crear una clase concreta;
- `@Binds` conecta una interfaz con su implementación;
- `@Provides` construye objetos que requieren configuración especial;
- los componentes y scopes determinan cuánto vive cada instancia.

Por ejemplo, un ViewModel solicita `AttendancesUseCases`, no lo instancia con `AttendancesUseCases(...)`. Hilt encuentra sus dependencias recursivamente hasta llegar a DAO, API y dispatchers.

`ViewModelModule` también proporciona estados iniciales. Esto parece pequeño, pero permite crear un ViewModel en una prueba con un estado específico sin depender siempre de valores escritos dentro del constructor.

## 14. Navegación y estado raíz

`GolAppState` centraliza el `backStack`, la navegación de nivel superior y el estado de conectividad. `GolappNavHost` traduce cada `NavKey` a contenido Compose.

Los decoradores de Navigation 3 conservan:

- estado `rememberSaveable` por entrada;
- un almacén de ViewModels asociado a la vida de cada entrada.

Esto ayuda a que una pantalla mantenga su estado mientras pertenece al stack y lo libere cuando deja de pertenecer a él.

`NavigationSuiteScaffold` adapta la navegación principal al espacio disponible. En vez de construir por separado una barra inferior y un rail lateral, el componente selecciona la presentación apropiada usando `WindowAdaptiveInfo`.

## 15. WorkManager y sincronización pendiente

`AttendanceSyncWorker` procesa asistencias almacenadas en `attendance_sync`. WorkManager es adecuado porque este trabajo:

- debe continuar aunque la pantalla ya no exista;
- puede esperar a que se cumplan restricciones como conectividad;
- necesita reintentos administrados por el sistema.

La regla práctica es:

- interacción ligada a una pantalla: `viewModelScope`;
- trabajo durable que debe sobrevivir a navegación o reinicios: WorkManager.

## 16. Qué significa “MVI” en este proyecto

El proyecto usa ideas de MVI sin imponer un framework universal:

```text
Intent/Event → ViewModel → nuevo State → UI
                     └────→ Effect de una sola vez
```

No todos los ViewModels tienen que copiar exactamente la misma plantilla. Lo importante es conservar las propiedades:

- una fuente de verdad por pantalla;
- estado inmutable;
- eventos explícitos;
- escrituras de estado ordenadas;
- efectos separados;
- errores y cancelación tratados intencionalmente.

Una arquitectura es buena cuando hace difícil introducir errores y facilita probar el comportamiento; no cuando acumula interfaces o nombres de patrones.

## 17. Recorrido completo: marcar una asistencia

Este es el flujo que conviene seguir con el depurador:

1. El usuario selecciona un estado en `AttendancesScreen` o `AttendanceDetailScreen`.
2. La pantalla emite `AttendancesUiEvent.OnTakeAttendance`.
3. `AttendancesViewModel.onEvent()` llama a `takeAttendance()`.
4. `attendanceMutationMutex` espera su turno y evita mutaciones simultáneas.
5. `TakeAttendanceUseCase` valida los datos y llama a `AttendanceRepository`.
6. `AttendanceRepositoryImpl` actualiza primero la fila local mediante `AttendanceDao`.
7. Room invalida la consulta observable y emite una lista actualizada.
8. El ViewModel combina la lista nueva con búsqueda, selección y carga.
9. `collectAsStateWithLifecycle()` recibe el nuevo estado y Compose recompone únicamente las partes que leen valores modificados.
10. En paralelo secuencial, el Repository intenta sincronizar con `AttendanceRemoteDataSourceImpl`.
11. Si no hay red, registra el ID en `attendance_sync`; WorkManager podrá reintentarlo.

Este recorrido muestra por qué la aplicación puede responder rápido y seguir funcionando con conectividad inestable.

## 18. Cómo estudiar este proyecto en orden

### Etapa 1: UI declarativa

1. `HomeScreen.kt`
2. `GroupsScreen.kt`
3. `AttendancesScreen.kt`
4. `AttendanceDetailScreen.kt`

Preguntas: ¿qué recibe cada Composable?, ¿qué estado lee?, ¿qué callback ejecuta?, ¿qué cambia entre Compact y Expanded?

### Etapa 2: estado y ciclo de vida

1. `AttendancesUiState`, eventos y efectos.
2. `AttendancesViewModel.onEvent()`.
3. `combine`, `flatMapLatest` y `stateIn`.
4. `SavedStateHandle`.

Preguntas: ¿qué es persistente?, ¿qué es un suceso único?, ¿qué trabajo se cancela cuando cambia la selección?

### Etapa 3: dominio y datos

1. `TakeAttendanceUseCase.kt`.
2. `AttendanceRepository.kt`.
3. `AttendanceRepositoryImpl.kt`.
4. `AttendanceDao.kt`.
5. `AttendanceRemoteDataSourceImpl.kt` y `GolappAPI.kt`.

Pregunta principal: ¿en qué capa debería vivir cada decisión?

### Etapa 4: infraestructura

1. módulos de `core/di`;
2. `SessionManager` e interceptores de autenticación;
3. `AttendanceSyncWorker`;
4. manejo de cancelación y reporte de errores.

### Etapa 5: pruebas

Revisar los tests existentes de:

- destino inicial de sesión;
- restauración de Auth;
- estado y credenciales;
- propagación de cancelación;
- reporte defensivo de errores.

Luego escribir pruebas de ViewModel con dispatchers controlados y repositorios falsos. Una buena prueba observa entradas y salidas públicas, sin depender de detalles privados.

## 19. Buenas prácticas que se pueden reutilizar

- La UI muestra estado y emite acciones; no coordina fuentes de datos.
- El ViewModel no contiene referencias a Activity o Composables.
- Room es la fuente observable cuando se necesita comportamiento local-first.
- El dominio no importa clases de Retrofit, Room ni Compose.
- Se guardan en `SavedStateHandle` claves pequeñas y no secretos.
- Los efectos puntuales se separan del estado persistente.
- La concurrencia tiene una política explícita: cancelar lo anterior, serializar o rechazar duplicados.
- `CancellationException` siempre conserva su significado de cancelación.
- Se adapta por tamaño de ventana y no por tipo supuesto de dispositivo.
- La arquitectura se mantiene tan sencilla como permita el riesgo real.

## 20. Aspectos que todavía conviene practicar o fortalecer

- Agregar más pruebas de `AttendancesViewModel` para orden de mutaciones, reintentos y restauración.
- Realizar una prueba real de muerte de proceso; rotar la pantalla no cubre este caso.
- Documentar explícitamente cuándo y con qué restricciones se programa `AttendanceSyncWorker`.
- Revisar si los fallos de sincronización en segundo plano necesitan una señal visible adicional para el usuario.
- A medida que crezca el proyecto, evaluar módulos Gradle por límites y tiempos de compilación, no solamente para “cumplir” una arquitectura.

## 21. Regla mental para agregar una funcionalidad nueva

Antes de escribir código, responder:

1. ¿Cuál es el estado completo que necesita la UI?
2. ¿Qué eventos puede generar el usuario?
3. ¿Qué efectos ocurren una sola vez?
4. ¿Cuál es la regla de negocio y en qué caso de uso vive?
5. ¿Cuál es la fuente de verdad: Room, red, DataStore o memoria?
6. ¿Qué debe sobrevivir a rotación, muerte de proceso o cierre de la app?
7. ¿Qué pasa si llegan dos eventos simultáneos?
8. ¿Qué pasa si la red falla o la corrutina se cancela?
9. ¿Cómo se comporta en Compact, Medium y Expanded?
10. ¿Cómo se verificará con una prueba?

Si esas respuestas están claras, la implementación suele resultar mucho más sencilla y predecible.

## 22. Actualización automática desde Google Play

La aplicación usa **Google Play In-App Updates** con el flujo `IMMEDIATE`. No descarga APK directamente ni instala archivos por su cuenta: consulta a Google Play y, cuando existe una versión superior disponible y Play permite una actualización inmediata, abre la interfaz oficial administrada por la Play Store.

El recorrido es:

```text
MainActivity.onCreate()
        ↓
PlayUpdateCoordinator.checkForUpdate()
        ↓
AppUpdateManager consulta a Google Play
        ↓
ImmediateUpdatePolicy valida disponibilidad y tipo permitido
        ↓
startUpdateFlowForResult(IMMEDIATE)
        ↓
Google Play muestra y administra la actualización
```

Las responsabilidades están separadas así:

- `MainActivity` registra el `ActivityResultLauncher`, inicia la consulta y registra si el flujo termina sin éxito.
- `PlayUpdateCoordinator` encapsula las clases de Play Core y el inicio del flujo.
- `ImmediateUpdatePolicy` contiene decisiones puras y verificables mediante pruebas unitarias.

`MainActivity.onResume()` llama a `resumeUpdateIfNeeded()`. Esto cubre el caso en que Google Play ya había iniciado una actualización inmediata pero la Activity fue recreada o volvió al primer plano. Solo se reanuda cuando Play informa `DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS`.

La consulta no bloquea el arranque ni sustituye la resolución de sesión. Si Google Play no está disponible, la consulta falla o no existe una actualización, la aplicación continúa normalmente y el problema se registra con Timber.

### Limitaciones y prueba correcta

- La función depende de que la app haya sido instalada desde Google Play; no se valida correctamente con una instalación normal desde Android Studio.
- Google Play compara el `versionCode` instalado con el de una versión publicada en un track accesible para la cuenta de prueba.
- Para probarla, se debe publicar un Android App Bundle con un `versionCode` superior en Internal App Sharing o en un track de pruebas y abrir la versión anterior instalada desde el enlace de Play.
- El flujo `IMMEDIATE` bloquea el uso hasta completar o abandonar la actualización. Es apropiado si se desea mantener a todos los instructores en una versión compatible con el backend.
- Si en el futuro las actualizaciones dejan de ser obligatorias, se puede introducir una política flexible sin mezclar esa decisión con Compose o la sesión.

Los archivos principales son:

- `core/update/PlayUpdateCoordinator.kt`;
- `MainActivity.kt`;
- `ImmediateUpdatePolicyTest.kt`;
- la dependencia `com.google.android.play:app-update` declarada en el catálogo de versiones.
