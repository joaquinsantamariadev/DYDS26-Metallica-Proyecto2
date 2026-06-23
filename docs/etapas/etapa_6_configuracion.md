# Plan Detallado — Etapa 6: Configuración del Sistema

> **Etapa:** 6 de 6
> **Proyecto:** Sistema de Control de Stock
> **Arquitectura:** Clean MVVM (Compose Desktop + Kotlin)
> **Lineamientos:** `docs/consideraciones_lineamientos.md`
> **Depende de:** Etapas 0, 1, 2, 3, 4 y 5 completadas

---

## 1. Objetivo

Implementar la sección de configuración del sistema: una pantalla que permite al usuario
ajustar los datos del negocio, gestionar parámetros operativos del sistema y exportar datos
a formatos estándar (CSV) para backup o análisis externo.

La configuración **modifica datos transversales** que afectan el comportamiento de otros módulos
(ej. nombre del local en encabezados, moneda por defecto, umbrales de alertas). Toda escritura
se persiste en una tabla dedicada `SettingsTable` con formato clave-valor.

---

## 2. Precondiciones

- **Etapa 0 ✅** — App compilable, navegación con sidebar funcional, tema definido.
- **Etapa 1 ✅** — Entities y tablas de productos, categorías, ventas y sesiones creadas.
- **Etapa 2 ✅** — CRUD de inventario funcional.
- **Etapa 3 ✅** — Ventas registradas con historial transaccional.
- **Etapa 4 ✅** — Dashboard funcional con métricas.
- **Etapa 5 ✅** — Reportes con historial, gráficos y márgenes.

---

## 3. Tareas

### Fase A — Capa de Dominio

**Tarea 6A-1 — Entities de configuración**
Crear las data classes en `domain/entity/`:
- `StoreSettings` — datos del negocio (nombre, dirección, teléfono, moneda, logo path).
- `SystemSettings` — parámetros operativos (umbral de bajo stock por defecto, días de alerta
  de vencimiento, tamaño de página del historial, top N de rotación).
- `ExportFormat` — enum con `CSV` (extensible a `EXCEL` en el futuro si se agrega Apache POI).

**Tarea 6A-2 — Interfaz `SettingsRepository`**
Declarar los contratos en `domain/repository/SettingsRepository.kt`:
- `getStoreSettings(): StoreSettings`
- `saveStoreSettings(settings: StoreSettings)`
- `getSystemSettings(): SystemSettings`
- `saveSystemSettings(settings: SystemSettings)`

**Tarea 6A-3 — Interfaz `ExportRepository`**
Declarar los contratos en `domain/repository/ExportRepository.kt`:
- `exportProducts(filePath: String, format: ExportFormat)`
- `exportSales(filePath: String, format: ExportFormat)`
- `exportCategories(filePath: String, format: ExportFormat)`

**Tarea 6A-4 — Casos de uso**
Implementar como clases concretas sin interfaz (consistente con el resto del proyecto):
- `GetStoreSettingsUseCase` — delega al repositorio.
- `SaveStoreSettingsUseCase` — valida campos mínimos (nombre no vacío) y delega.
- `GetSystemSettingsUseCase` — delega al repositorio.
- `SaveSystemSettingsUseCase` — valida rangos lógicos (umbral > 0, días > 0) y delega.
- `ExportDataUseCase` — orquesta la exportación invocando `ExportRepository`.

### Fase B — Capa de Datos

**Tarea 6B-1 — `SettingsTable`**
Crear la tabla en `data/local/settings/SettingsTable.kt` con formato clave-valor:
- `key: VARCHAR(100) PRIMARY KEY`
- `value: TEXT`

Registrar la creación de la tabla en `DatabaseFactory` junto con las demás tablas.

**Tarea 6B-2 — `SettingsRepositoryImpl`**
Implementar en `data/repository/SettingsRepositoryImpl.kt` con queries Exposed directas
dentro de `transaction {}`. Las settings se serializan/deserializan como pares clave-valor.
No se crea capa de DataSource intermedia.

**Tarea 6B-3 — `ExportRepositoryImpl`**
Implementar en `data/repository/ExportRepositoryImpl.kt`.
La exportación CSV se implementa con `java.io.BufferedWriter` estándar (sin dependencias externas).
Cada método consulta la tabla correspondiente y escribe las filas en formato CSV con cabecera.

**Tarea 6B-4 — Mappers de extensión**
Agregar funciones de extensión en `data/mapper/Mappers.kt`:
- `Map<String, String>.toStoreSettings(): StoreSettings`
- `Map<String, String>.toSystemSettings(): SystemSettings`
- `StoreSettings.toKeyValuePairs(): Map<String, String>`
- `SystemSettings.toKeyValuePairs(): Map<String, String>`

### Fase C — Inyección de Dependencias

**Tarea 6C-1 — Módulo Koin de configuración**
Agregar los bindings en `di/AppModules.kt`:
Registrar: los dos repositorios (`single`), los cinco casos de uso (`factory`)
y el ViewModel (`factory`).

### Fase D — Capa de Presentación

**Tarea 6D-1 — `SettingsUiState`**
Crear la data class con campos para:
- `storeSettings: StoreSettings`
- `systemSettings: SystemSettings`
- `isLoading: Boolean`
- `isSaving: Boolean`
- `saveSuccess: Boolean`
- `exportStatus: String?`
- `error: String?`

**Tarea 6D-2 — `SettingsViewModel`**
Expone `uiState: StateFlow<SettingsUiState>` y métodos:
- `loadSettings()` — carga store + system settings en paralelo.
- `saveStoreSettings(settings: StoreSettings)` — valida y persiste.
- `saveSystemSettings(settings: SystemSettings)` — valida y persiste.
- `exportData(type: String, filePath: String, format: ExportFormat)` — ejecuta export.

**Tarea 6D-3 — Sub-composables**
Crear los componentes en `presentation/settings/components/`:
- `StoreSettingsForm` — formulario editable con campos de texto para nombre, dirección,
  teléfono y moneda. Botón "Guardar" al final.
- `SystemSettingsForm` — formulario para parámetros numéricos (umbral bajo stock, días
  alerta vencimiento). Usa `OutlinedTextField` con validación numérica.
- `ExportPanel` — panel con botones para exportar productos, ventas y categorías.
  Incluye selector de ruta de archivo (`JFileChooser` de Swing integrado vía
  `ComposePanel` o `remember { }` pattern).

**Tarea 6D-4 — `SettingsScreen`**
Reemplazar el placeholder actual de `SettingsScreen.kt` con la implementación real.
Layout con tabs o secciones verticales: "Datos del Local", "Parámetros del Sistema",
"Exportar Datos". Conectar al ViewModel inyectado vía Koin.

### Fase E — Tests Unitarios

**Tarea 6E-1 — `SettingsRepositoryFake` y `ExportRepositoryFake`**
Crear en `test/.../data/` con el patrón establecido: campos mutables, `shouldThrowError`,
captura de parámetros.

**Tarea 6E-2 — Tests de casos de uso**
Un archivo por caso de uso. Verificar:
- Delegación correcta al repositorio.
- Validación de campos (nombre vacío lanza error en `SaveStoreSettingsUseCase`).
- Validación de rangos (umbral <= 0 lanza error en `SaveSystemSettingsUseCase`).
- Propagación de excepción cuando `shouldThrowError = true`.

**Tarea 6E-3 — `SettingsViewModelTest`**
Usar `UnconfinedTestDispatcher`. Verificar:
- Estado inicial tras `loadSettings()` (datos cargados, no loading, no error).
- `saveStoreSettings` actualiza estado y muestra `saveSuccess`.
- `saveSystemSettings` actualiza estado y muestra `saveSuccess`.
- Error en save propaga al `error` del estado.
- `exportData` actualiza `exportStatus`.

---

## 4. Diseño de Entities y Contratos

### Entities de Dominio

```kotlin
// domain/entity/StoreSettings.kt
data class StoreSettings(
    val storeName: String = "",
    val address: String = "",
    val phone: String = "",
    val currency: String = "ARS"
)

// domain/entity/SystemSettings.kt
data class SystemSettings(
    val defaultLowStockThreshold: Int = 5,
    val expiryAlertDays: Int = 7,
    val historyPageSize: Int = 50,
    val rotationTopN: Int = 20
)

// domain/entity/ExportFormat.kt
enum class ExportFormat { CSV }
```

### Interfaz del Repositorio

```kotlin
// domain/repository/SettingsRepository.kt
interface SettingsRepository {
    suspend fun getStoreSettings(): StoreSettings
    suspend fun saveStoreSettings(settings: StoreSettings)
    suspend fun getSystemSettings(): SystemSettings
    suspend fun saveSystemSettings(settings: SystemSettings)
}

// domain/repository/ExportRepository.kt
interface ExportRepository {
    suspend fun exportProducts(filePath: String, format: ExportFormat)
    suspend fun exportSales(filePath: String, format: ExportFormat)
    suspend fun exportCategories(filePath: String, format: ExportFormat)
}
```

### Estado de UI

```kotlin
// presentation/settings/SettingsUiState.kt
data class SettingsUiState(
    val storeSettings: StoreSettings = StoreSettings(),
    val systemSettings: SystemSettings = SystemSettings(),
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val saveSuccess: Boolean = false,
    val exportStatus: String? = null,
    val error: String? = null
)
```

---

## 5. Archivos a Crear / Modificar

### Nuevos — Domain
```
domain/entity/StoreSettings.kt
domain/entity/SystemSettings.kt
domain/entity/ExportFormat.kt
domain/repository/SettingsRepository.kt
domain/repository/ExportRepository.kt
domain/usecase/settings/GetStoreSettingsUseCase.kt
domain/usecase/settings/SaveStoreSettingsUseCase.kt
domain/usecase/settings/GetSystemSettingsUseCase.kt
domain/usecase/settings/SaveSystemSettingsUseCase.kt
domain/usecase/settings/ExportDataUseCase.kt
```

### Nuevos — Data
```
data/local/settings/SettingsTable.kt
data/repository/SettingsRepositoryImpl.kt
data/repository/ExportRepositoryImpl.kt
```

### Modificados — Data
```
data/mapper/Mappers.kt          ← añadir extensiones toStoreSettings(), toKeyValuePairs(), etc.
data/local/DatabaseFactory.kt   ← registrar SettingsTable
```

### Nuevos — Presentation
```
presentation/settings/SettingsUiState.kt
presentation/settings/SettingsViewModel.kt
presentation/settings/components/StoreSettingsForm.kt
presentation/settings/components/SystemSettingsForm.kt
presentation/settings/components/ExportPanel.kt
```

### Modificados — Presentation
```
presentation/settings/SettingsScreen.kt  ← reemplazar placeholder con implementación real
```

### Modificados — DI
```
di/AppModules.kt  ← añadir bindings de settings
```

### Nuevos — Tests
```
test/.../data/SettingsRepositoryFake.kt
test/.../data/ExportRepositoryFake.kt
test/.../domain/usecase/GetStoreSettingsUseCaseTest.kt
test/.../domain/usecase/SaveStoreSettingsUseCaseTest.kt
test/.../domain/usecase/GetSystemSettingsUseCaseTest.kt
test/.../domain/usecase/SaveSystemSettingsUseCaseTest.kt
test/.../domain/usecase/ExportDataUseCaseTest.kt
test/.../presentation/SettingsViewModelTest.kt
```

---

## 6. Decisiones de Implementación

### Tabla clave-valor para settings
Se utiliza una tabla genérica `SettingsTable(key, value)` en lugar de una tabla con columnas
tipadas. Esto evita migraciones de esquema cada vez que se agrega un nuevo parámetro de
configuración. Los mappers se encargan de serializar/deserializar los valores.

### Export CSV sin dependencias externas
La exportación CSV se implementa con `BufferedWriter` de la biblioteca estándar de Java.
No se agrega Apache POI ni opencsv como dependencia. Si en el futuro se necesita Excel,
se puede agregar la dependencia en ese momento (YAGNI).

### `ExportFormat` como enum extensible
Hoy solo contiene `CSV`. Está preparado para recibir `EXCEL` sin cambiar la interfaz
del repositorio ni del caso de uso, solo agregando una rama al `when` en la implementación.

### Validación en los Use Cases, no en el ViewModel
Las reglas de validación (`nombre no vacío`, `umbral > 0`) viven en los casos de uso,
no en el ViewModel ni en la UI. Esto es consistente con la filosofía Clean del proyecto:
la lógica de negocio pertenece al dominio.

### JFileChooser para selección de ruta
En Compose Desktop no existe un selector de archivos nativo de Compose. Se usa
`javax.swing.JFileChooser` invocado desde un `remember { }` block. Es la solución
estándar en proyectos Compose Desktop.

---

## 7. Criterios de Éxito

- La pantalla **Configuración** muestra tres secciones funcionales: Datos del Local,
  Parámetros del Sistema y Exportar Datos.
- Los datos del local (nombre, dirección, teléfono, moneda) se persisten en BD y se
  recuperan correctamente al reabrir la app.
- Los parámetros del sistema (umbral de stock, días de alerta) se persisten y los
  módulos que los consumen (Dashboard, Reportes) los respetan.
- La exportación CSV genera archivos válidos con cabecera y datos correctos para
  productos, ventas y categorías.
- La validación rechaza nombres vacíos y umbrales negativos/cero, mostrando error en UI.
- Todos los tests pasan con `UnconfinedTestDispatcher`. No hay `advanceUntilIdle` manual.
- No hay código muerto. No hay comentarios de "qué". No hay imports sin uso.

---

## 8. Checklist de Progreso

### Fase A — Dominio
- [X] 6A-1 `StoreSettings`, `SystemSettings`, `ExportFormat` definidos
- [X] 6A-2 Interfaz `SettingsRepository` declarada
- [X] 6A-3 Interfaz `ExportRepository` declarada
- [X] 6A-4 5 casos de uso implementados con validaciones

### Fase B — Datos
- [X] 6B-1 `SettingsTable` creada y registrada en `DatabaseFactory`
- [X] 6B-2 `SettingsRepositoryImpl` con queries Exposed directas
- [X] 6B-3 `ExportRepositoryImpl` con CSV vía `BufferedWriter`
- [X] 6B-4 Mappers de extensión en `Mappers.kt`

### Fase C — DI
- [X] 6C-1 Bindings Koin registrados (repos, use cases, ViewModel)

### Fase D — Presentación
- [X] 6D-1 `SettingsUiState` definido
- [X] 6D-2 `SettingsViewModel` con carga, guardado y exportación
- [X] 6D-3 Sub-composables: `StoreSettingsForm`, `SystemSettingsForm`, `ExportPanel`
- [X] 6D-4 `SettingsScreen` reemplaza placeholder e integrado en navegación

### Fase E — Tests
- [X] 6E-1 `SettingsRepositoryFake` y `ExportRepositoryFake` en archivos propios
- [X] 6E-2 Tests de los 5 casos de uso (éxito, validación, error)
- [X] 6E-3 `SettingsViewModelTest` (carga, guardado, exportación, errores)
