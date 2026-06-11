# Etapa 0 — Cimientos y Shell de Navegación

> **Precondiciones:** Etapa 1 de estructura base completada (paquetes MVVM, `.gitignore`, `App.kt`, `main.kt`)  
> **Objetivo:** Tener una aplicación compilable con build system configurado, tema visual, sidebar de navegación funcional, todos los paneles del sistema declarados (vacíos), y la base de datos conectada (sin tablas de negocio aún).

---

## Tareas

### T0.1 — Configurar el Build System (Gradle + dependencias)

**Archivos a crear:**
- `build.gradle.kts` (raíz)
- `settings.gradle.kts`
- `gradle.properties`
- `gradle/wrapper/gradle-wrapper.properties`
- `gradle/wrapper/gradle-wrapper.jar`

**Detalle:**
- Configurar un proyecto Compose Desktop con el plugin `org.jetbrains.compose`.
- Agregar dependencias:
  - `compose.desktop.currentOs` (UI)
  - `org.jetbrains.exposed:exposed-core`, `exposed-dao`, `exposed-jdbc` (ORM)
  - `org.xerial:sqlite-jdbc` (driver SQLite)
  - `io.insert-koin:koin-core` (DI)
  - `io.ktor:ktor-client-core`, `ktor-client-cio`, `ktor-client-content-negotiation`, `ktor-serialization-kotlinx-json` (HTTP)
  - `org.jetbrains.kotlinx:kotlinx-serialization-json` (serialización)
  - `org.jetbrains.kotlinx:kotlinx-coroutines-core` (coroutines)
- Configurar `application { mainClass = "com.app.presentation.MainKt" }`.
- Definir el Kotlin JVM target (17+).

**Criterio de éxito:** `./gradlew build` compila sin errores.

- [x] Completada

---

### T0.2 — Definir el Tema Visual (Design System)

**Archivos a crear:**
- `src/main/kotlin/com/app/presentation/utils/Theme.kt`
- `src/main/kotlin/com/app/presentation/utils/Colors.kt`

**Detalle:**
- Definir una paleta de colores coherente para la app (primario, secundario, fondo, superficie, error, texto).
- Tema oscuro como predeterminado (ideal para uso prolongado en mostrador/almacén).
- Definir tipografía (usar fuentes del sistema o embebidas).
- Crear un `@Composable fun AppTheme(content: @Composable () -> Unit)` que envuelva `MaterialTheme`.

**Criterio de éxito:** El tema se aplica correctamente al ejecutar la app.

- [x] Completada

---

### T0.3 — Implementar el Sistema de Navegación

**Archivos a crear:**
- `src/main/kotlin/com/app/presentation/navigation/Screen.kt`
- `src/main/kotlin/com/app/presentation/navigation/NavigationHost.kt`

**Archivos a modificar:**
- `src/main/kotlin/com/app/presentation/App.kt`

**Detalle:**
- Crear una `sealed class Screen` con las 5 secciones principales:
  ```kotlin
  sealed class Screen(val title: String, val icon: ...) {
      data object Dashboard : Screen("Dashboard", ...)
      data object Inventory : Screen("Inventario", ...)
      data object PointOfSale : Screen("Punto de Venta", ...)
      data object Reports : Screen("Reportes", ...)
      data object Settings : Screen("Configuración", ...)
  }
  ```
- Crear un `NavigationHost` composable que, dado un `Screen`, renderice el panel correspondiente.
- Implementar navegación basada en estado (`mutableStateOf<Screen>`) en `App.kt`.

**Criterio de éxito:** Se puede hacer clic en cada ítem del sidebar y el contenido cambia.

- [x] Completada

---

### T0.4 — Construir el Shell de la Aplicación (Layout + Sidebar)

**Archivos a crear/modificar:**
- `src/main/kotlin/com/app/presentation/App.kt` (modificar)
- `src/main/kotlin/com/app/presentation/utils/Sidebar.kt` (crear)

**Detalle:**
- Layout principal: `Row` con sidebar fijo a la izquierda + contenido dinámico a la derecha.
- El sidebar debe contener:
  - Logo o título de la app en la parte superior.
  - Los 5 ítems de navegación con iconos y texto.
  - Indicador visual del ítem activo (highlight, borde, fondo distinto).
- El contenido a la derecha ocupa todo el espacio restante y muestra el panel de la sección seleccionada.
- Responsive: el sidebar debe tener un ancho fijo (ej. 240dp) y el contenido expandirse.

**Criterio de éxito:** La app abre con sidebar visible, 5 secciones navegables, contenido cambia al hacer clic.

- [ ] Completada

---

### T0.5 — Crear Paneles Vacíos (Placeholders) para Cada Sección

**Archivos a crear:**
- `src/main/kotlin/com/app/presentation/dashboard/DashboardScreen.kt`
- `src/main/kotlin/com/app/presentation/inventory/InventoryScreen.kt`
- `src/main/kotlin/com/app/presentation/pos/PosScreen.kt`
- `src/main/kotlin/com/app/presentation/reports/ReportsScreen.kt`
- `src/main/kotlin/com/app/presentation/settings/SettingsScreen.kt`

**Detalle:**
- Cada archivo contiene un `@Composable fun XxxScreen()` que muestra un placeholder centrado con el nombre de la sección y un ícono grande.
- Estos serán reemplazados por UI real en etapas posteriores.
- Los paquetes `home/` y `detail/` genéricos de la Etapa 1 se eliminan (eran placeholders genéricos, ahora tienen nombre real).

**Archivos a eliminar:**
- `src/main/kotlin/com/app/presentation/home/.gitkeep`
- `src/main/kotlin/com/app/presentation/detail/.gitkeep`

**Criterio de éxito:** Cada sección muestra su nombre al navegar a ella.

- [ ] Completada

---

### T0.6 — Configurar la Base de Datos (Conexión SQLite + Exposed)

**Archivos a crear:**
- `src/main/kotlin/com/app/data/local/DatabaseFactory.kt`

**Archivos a modificar:**
- `src/main/kotlin/com/app/di/AppModules.kt`

**Detalle:**
- Crear un objeto `DatabaseFactory` que:
  - Establezca la conexión con SQLite (archivo local, ej. `stock_control.db`).
  - Exponga un método `init()` que cree las tablas (vacío por ahora, se pobla en Etapa 1).
- Registrar `DatabaseFactory` en el módulo Koin.
- Inicializar Koin en `main.kt` con `startKoin { modules(appModule) }`.

**Criterio de éxito:** La app arranca, crea el archivo `.db` en disco, y no hay errores de conexión.

- [ ] Completada

---

### T0.7 — Verificación Final y Commit

**Detalle:**
- Ejecutar `./gradlew build` y verificar compilación exitosa.
- Ejecutar `./gradlew run` y verificar que la app abre, el sidebar funciona, las secciones navegan correctamente.
- Verificar que el archivo `.db` se crea en la ubicación esperada.
- Verificar que `.gitignore` excluye `build/`, `.gradle/`, `.idea/`, y el archivo `.db`.
- Eliminar todo código muerto y `.gitkeep` en carpetas que ya tienen archivos.

**Archivos a modificar:**
- `.gitignore` (agregar `*.db` si corresponde)

**Criterio de éxito:** La app compila y ejecuta sin errores ni warnings. La estructura de paquetes refleja las 5 secciones del sistema.

- [ ] Completada

---

## Resumen de Archivos

### Archivos Nuevos (a crear)
| Archivo | Propósito |
|---------|-----------|
| `build.gradle.kts` | Build system y dependencias |
| `settings.gradle.kts` | Nombre del proyecto |
| `gradle.properties` | Propiedades de Gradle/Kotlin |
| `gradle/wrapper/*` | Wrapper de Gradle |
| `presentation/utils/Theme.kt` | Tema visual (MaterialTheme) |
| `presentation/utils/Colors.kt` | Paleta de colores |
| `presentation/navigation/Screen.kt` | Sealed class de navegación |
| `presentation/navigation/NavigationHost.kt` | Router de composables |
| `presentation/utils/Sidebar.kt` | Sidebar de la app |
| `presentation/dashboard/DashboardScreen.kt` | Panel Dashboard (placeholder) |
| `presentation/inventory/InventoryScreen.kt` | Panel Inventario (placeholder) |
| `presentation/pos/PosScreen.kt` | Panel POS (placeholder) |
| `presentation/reports/ReportsScreen.kt` | Panel Reportes (placeholder) |
| `presentation/settings/SettingsScreen.kt` | Panel Config (placeholder) |
| `data/local/DatabaseFactory.kt` | Conexión SQLite |

### Archivos Modificados
| Archivo | Cambio |
|---------|--------|
| `presentation/App.kt` | Integrar tema, sidebar, navegación |
| `presentation/main.kt` | Inicializar Koin |
| `di/AppModules.kt` | Registrar DatabaseFactory |
| `.gitignore` | Agregar `*.db` |

### Archivos Eliminados
| Archivo | Razón |
|---------|-------|
| `presentation/home/.gitkeep` | Reemplazado por `dashboard/` |
| `presentation/detail/.gitkeep` | Reemplazado por secciones reales |

---

## Diagrama de Dependencias de esta Etapa

```
main.kt
  └── startKoin(appModule)
  └── Window { App() }
          └── AppTheme { Row { Sidebar + NavigationHost } }
                              │           │
                              │           ├── DashboardScreen()
                              │           ├── InventoryScreen()
                              │           ├── PosScreen()
                              │           ├── ReportsScreen()
                              │           └── SettingsScreen()
                              │
                              └── currentScreen (state)

AppModules.kt
  └── single { DatabaseFactory }

DatabaseFactory
  └── SQLite connection ("stock_control.db")
```
