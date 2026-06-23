# Plan Detallado — Etapa 5: Reportes y Estadísticas

> **Etapa:** 5 de 7
> **Proyecto:** Sistema de Control de Stock
> **Arquitectura:** Clean MVVM (Compose Desktop + Kotlin)
> **Lineamientos:** `docs/consideraciones_lineamientos.md`
> **Depende de:** Etapas 0, 1, 2, 3 y 4 completadas

---

## 1. Objetivo

Implementar la sección de reportes y estadísticas: una vista de sólo lectura que expone
cuatro perspectivas del negocio sobre el historial acumulado de transacciones.

- **Historial de transacciones** — registro inmutable y filtrable de todas las ventas.
- **Tendencias de ingresos** — gráfico de barras/línea con revenue agregado por período.
- **Rotación de productos** — ranking de productos más vendidos en un rango de fechas.
- **Márgenes** — tabla de margen bruto por producto y categoría.

La sección es navegable mediante tabs internos. Ninguna pantalla modifica datos; toda
escritura pertenece a las etapas anteriores.

---

## 2. Precondiciones

- **Etapa 0 ✅** — App compilable, navegación con sidebar funcional, tema definido.
- **Etapa 1 ✅** — Tablas `Products`, `Sales`, `SaleItems`, `CashRegisterSessions` creadas
  con campos `costPrice`, `salePrice`, `quantity`, `total`, `createdAt`.
- **Etapa 2 ✅** — Productos con `categoryId`, `costPrice`, `salePrice` en BD.
- **Etapa 3 ✅** — Ventas con `SaleItems` registradas transaccionalmente.
- **Etapa 4 ✅** — Dashboard funcional (valida que la BD tiene datos reales disponibles).

---

## 3. Tareas

### Fase A — Capa de Dominio

**Tarea 5A-1 — Value objects de filtros**
Crear `ReportFilters` y `ReportPeriod` en `domain/entity/`.
`ReportFilters` encapsula el rango de fechas (`from`/`to`) y la granularidad del período.
`ReportPeriod` es un enum con `DAILY`, `WEEKLY`, `MONTHLY`.
Incluir un factory `ReportFilters.default()` que devuelve los últimos 6 meses con
granularidad mensual. Esta lógica vive en el dominio, no en la UI.

**Tarea 5A-2 — Entities del historial**
Crear `TransactionHistoryEntry` y `TransactionItemDetail` en `domain/entity/`.
El historial es inmutable: ninguna entity expone operaciones de mutación.

**Tarea 5A-3 — Entities estadísticas**
Crear `RevenueSummary`, `RevenueDataPoint`, `ProductRotationEntry` y `MarginEntry`
en `domain/entity/`.
`RevenueSummary` agrega `totalRevenue`, `totalSales`, `averageTicket` y la lista de
`dataPoints` para el gráfico. `RevenueDataPoint` lleva la etiqueta del período y el
valor. `MarginEntry` computa `grossMargin` y `grossMarginPercent` a partir de
`costPrice` y `salePrice`; ese cálculo pertenece al mapper, no a la entity.

**Tarea 5A-4 — Interfaz `ReportsRepository`**
Declarar los cuatro contratos en `domain/repository/ReportsRepository.kt`:
- `getTransactionHistory(filters: ReportFilters, limit: Int, offset: Int): List<TransactionHistoryEntry>`
- `getRevenueSummary(filters: ReportFilters): RevenueSummary`
- `getProductRotation(filters: ReportFilters, topN: Int): List<ProductRotationEntry>`
- `getMargins(): List<MarginEntry>`

`getMargins()` no recibe filtros de fecha porque los márgenes reflejan precios actuales
y no dependen del período.

**Tarea 5A-5 — Implementaciones de casos de uso**
Implementar las cuatro clases directamente en `domain/usecase/`, sin interfaz separada,
consistente con la convención del proyecto.
Las constantes de negocio se definen en `companion object` de cada clase:

| Constante              | Valor | Clase                             |
|------------------------|-------|-----------------------------------|
| `HISTORY_PAGE_SIZE`    | `50`  | `GetTransactionHistoryUseCase`    |
| `ROTATION_TOP_N`       | `20`  | `GetProductRotationUseCase`       |

`GetTransactionHistoryUseCase` recibe `filters` y el número de página; calcula
`offset = page * HISTORY_PAGE_SIZE` antes de llamar al repositorio.

### Fase B — Capa de Datos

**Tarea 5B-1 — `ReportsMapper`**
Crear `data/mapper/ReportsMapper.kt` con extension functions sobre `ResultRow` y colecciones,
consistente con el patrón de `Mappers.kt` existente.
Responsabilidades:
- `ResultRow.toTransactionItemDetail()` y `ResultRow.toTransactionHistoryEntry(items)` para el historial.
- `ResultRow.toMarginEntry()` — calcula `grossMargin = salePrice - costPrice` y
  `grossMarginPercent = (grossMargin / salePrice) * 100`.
- `List<RevenueDataPoint>.toRevenueSummary()` — agrega `totalRevenue`, `totalSales` y `averageTicket`.
- `fun periodLabel(periodKey: String, period: ReportPeriod): String` — formatea la clave cruda de
  `strftime` a etiqueta legible (`"Ene 2025"`, `"Sem 3"`, `"15/01"`).

**Tarea 5B-2 — `ReportsRepositoryImpl`**
Implementar en `data/repository/ReportsRepositoryImpl.kt` con queries Exposed directas,
igual que el resto de los repositorios del proyecto. No hay capa de DataSource intermedia.

Queries principales:
- **Historial:** dos `SELECT` dentro del mismo `transaction {}`: primero pagina `SalesTable`
  con `limit/offset` (el conteo aplica sobre ventas, no filas del JOIN), luego trae ítems
  con `inList` sobre los IDs resultantes.
- **Ingresos:** `CustomFunction<String>("strftime", ...)` agrupado por período, filtrado por
  rango de fechas. El resultado se mapea a `List<RevenueDataPoint>` y se convierte a
  `RevenueSummary` vía el mapper.
- **Rotación:** JOIN `SaleItemsTable → SalesTable → ProductTable → CategoryTable` con `sum()`
  de cantidad y subtotal, agrupado por producto, ordenado descendente, limitado a `topN`.
- **Márgenes:** JOIN `ProductTable → CategoryTable`, sin filtro de fecha.

### Fase C — Inyección de Dependencias

**Tarea 5C-1 — Módulo Koin de reportes**
Agregar `reportsModule` en `di/AppModules.kt` (o en `di/ReportsModule.kt` separado).
Registrar: repositorio, cuatro casos de uso, dos ViewModels.

### Fase D — Capa de Presentación

**Tarea 5D-1 — Estados de UI**
Crear `TransactionHistoryUiState` y `StatisticsUiState` en `presentation/reports/`.

```kotlin
data class TransactionHistoryUiState(
    val transactions: List<TransactionHistoryEntry> = emptyList(),
    val filters: ReportFilters = ReportFilters.default(),
    val currentPage: Int = 0,
    val hasNextPage: Boolean = false,
    val isLoading: Boolean = false,
    val error: String? = null
)

data class StatisticsUiState(
    val revenueSummary: RevenueSummary? = null,
    val rotation: List<ProductRotationEntry> = emptyList(),
    val margins: List<MarginEntry> = emptyList(),
    val filters: ReportFilters = ReportFilters.default(),
    val isLoadingRevenue: Boolean = false,
    val isLoadingRotation: Boolean = false,
    val isLoadingMargins: Boolean = false,
    val error: String? = null
)
```

**Tarea 5D-2 — `TransactionHistoryViewModel`**
Gestiona el historial con filtros y paginación.
Expone:
- `uiState: StateFlow<TransactionHistoryUiState>`
- `onFiltersChanged(filters: ReportFilters)` — resetea a página 0 y recarga.
- `loadNextPage()` — incrementa `currentPage` y agrega registros a la lista.
- `refresh()` — resetea todo y vuelve a cargar desde página 0.

**Tarea 5D-3 — `StatisticsViewModel`**
Gestiona las tres vistas estadísticas (ingresos, rotación, márgenes).
Al cambiar filtros, lanza en paralelo las queries de revenue y rotación; los márgenes
se cargan una sola vez al inicializar (no dependen de filtros de fecha).
Expone:
- `uiState: StateFlow<StatisticsUiState>`
- `onFiltersChanged(filters: ReportFilters)` — recarga revenue y rotación en paralelo.

**Tarea 5D-4 — Composable `RevenueChart`**
Implementar con la API `Canvas` de Compose (sin dependencias externas).
Recibe `List<RevenueDataPoint>` y renderiza un gráfico de barras verticales con:
- Eje X: etiquetas de período.
- Eje Y: valores de revenue escalados al máximo del dataset.
- Barra resaltada al hacer hover/click (opcional en primera iteración).
  El componente es stateless; recibe datos del ViewModel a través de la pantalla.

**Tarea 5D-5 — Sub-composables restantes**
Crear en `presentation/reports/components/`:
- `DateRangePicker` — dos `DatePicker` (from/to) que emiten un `ReportFilters` actualizado.
- `PeriodSelector` — selector de granularidad (chips: Diario / Semanal / Mensual).
- `TransactionTable` — tabla paginada con columnas: fecha/hora, cantidad de ítems, total.
  Fila expandible que muestra `TransactionItemDetail` al hacer click.
- `RotationPanel` — lista ordenada de productos con nombre, categoría, unidades vendidas
  y una barra de progreso visual proporcional al máximo del ranking.
- `MarginPanel` — tabla con columnas: producto, categoría, precio costo, precio venta,
  margen bruto ($), margen bruto (%). Color de la fila según rango de margen:
  rojo < 10%, amarillo 10–25%, verde > 25%.

**Tarea 5D-6 — Pantallas y shell de navegación**
Crear `TransactionHistoryScreen` y `StatisticsScreen` que consumen sus respectivos
ViewModels y componen los sub-composables.
Crear `ReportsScreen` como contenedor con tab row:
`[Historial | Ingresos | Rotación | Márgenes]`.
Las dos primeras tabs corresponden a `TransactionHistoryScreen`; las dos últimas a
`StatisticsScreen` (o tabs separadas dentro del mismo StatisticsScreen).
Conectar `ReportsScreen` al slot de navegación existente en `App.kt`.

### Fase E — Tests Unitarios

**Tarea 5E-1 — `ReportsRepositoryFake`**
Extraer a `test/.../data/ReportsRepositoryFake.kt`. Expone:
`historyResult`, `revenueResult`, `rotationResult`, `marginsResult`, `shouldThrowError`.
También registra `lastFilters`, `lastLimit`, `lastOffset` para verificar parámetros.

**Tarea 5E-2 — Tests de casos de uso**
Un archivo por caso de uso. Verificar:
- Delegación correcta al repositorio con parámetros esperados.
- Que `GetTransactionHistoryUseCase` calcula `offset = page * HISTORY_PAGE_SIZE`.
- Que `GetProductRotationUseCase` pasa `ROTATION_TOP_N` al repositorio.
- Que `GetRevenueSummaryUseCase` propaga los filtros sin modificarlos.
- Que `GetMarginsUseCase` llama a `getMargins()` sin parámetros de fecha.
- Propagación de excepción cuando `shouldThrowError = true` en todos los casos.

**Tarea 5E-3 — `TransactionHistoryViewModelTest`**
Usar `UnconfinedTestDispatcher`. Verificar:
- Estado inicial (lista vacía, `currentPage = 0`, `isLoading = false`).
- Carga exitosa de página 0.
- `loadNextPage()` incrementa `currentPage` y acumula registros.
- `onFiltersChanged()` resetea a página 0 y descarta registros previos.
- Error: `error != null`, `isLoading = false`, lista vacía.
- `refresh()` resetea y recarga correctamente.

**Tarea 5E-4 — `StatisticsViewModelTest`**
Usar `UnconfinedTestDispatcher`. Verificar:
- Estado inicial (`isLoading*` en `false`, datos vacíos).
- Carga inicial: revenue, rotación y márgenes se populan.
- Márgenes se cargan una sola vez al inicializar (no se recargan al cambiar filtros).
- `onFiltersChanged()` recarga revenue y rotación en paralelo pero no márgenes.
- Error: `error != null`, `isLoading*` en `false`.

---

## 4. Diseño de Entities y Contratos

### Value Objects de Filtros

```kotlin
// domain/entity/ReportPeriod.kt
enum class ReportPeriod { DAILY, WEEKLY, MONTHLY }

// domain/entity/ReportFilters.kt
data class ReportFilters(
    val from: LocalDate,
    val to: LocalDate,
    val period: ReportPeriod = ReportPeriod.MONTHLY
) {
    companion object {
        fun default(): ReportFilters {
            val today = LocalDate.now()
            return ReportFilters(
                from = today.withDayOfMonth(1).minusMonths(5),
                to = today
            )
        }
    }
}
```

### Entities del Historial

```kotlin
// domain/entity/TransactionItemDetail.kt
data class TransactionItemDetail(
    val productName: String,
    val quantity: Int,
    val unitPrice: Double,
    val subtotal: Double
)

// domain/entity/TransactionHistoryEntry.kt
data class TransactionHistoryEntry(
    val saleId: Long,
    val dateTime: LocalDateTime,
    val items: List<TransactionItemDetail>,
    val total: Double
)
```

### Entities Estadísticas

```kotlin
// domain/entity/RevenueDataPoint.kt
data class RevenueDataPoint(
    val periodLabel: String,
    val revenue: Double,
    val salesCount: Int
)

// domain/entity/RevenueSummary.kt
data class RevenueSummary(
    val totalRevenue: Double,
    val totalSales: Int,
    val averageTicket: Double,
    val dataPoints: List<RevenueDataPoint>
)

// domain/entity/ProductRotationEntry.kt
data class ProductRotationEntry(
    val productId: Long,
    val productName: String,
    val categoryName: String,
    val unitsSold: Int,
    val revenue: Double
)

// domain/entity/MarginEntry.kt
data class MarginEntry(
    val productId: Long,
    val productName: String,
    val categoryName: String,
    val costPrice: Double,
    val salePrice: Double,
    val grossMargin: Double,
    val grossMarginPercent: Double
)
```

### Interfaz del Repositorio

```kotlin
// domain/repository/ReportsRepository.kt
interface ReportsRepository {
    suspend fun getTransactionHistory(
        filters: ReportFilters,
        limit: Int,
        offset: Int
    ): List<TransactionHistoryEntry>

    suspend fun getRevenueSummary(filters: ReportFilters): RevenueSummary

    suspend fun getProductRotation(
        filters: ReportFilters,
        topN: Int
    ): List<ProductRotationEntry>

    suspend fun getMargins(): List<MarginEntry>
}
```

### Carga en Paralelo en StatisticsViewModel

```kotlin
private fun loadStatistics(filters: ReportFilters) {
    viewModelScope.launch {
        _uiState.update { it.copy(isLoadingRevenue = true, isLoadingRotation = true) }
        try {
            coroutineScope {
                val revenue  = async { getRevenueSummary(filters) }
                val rotation = async { getProductRotation(filters) }
                _uiState.update {
                    it.copy(
                        revenueSummary    = revenue.await(),
                        rotation          = rotation.await(),
                        isLoadingRevenue  = false,
                        isLoadingRotation = false
                    )
                }
            }
        } catch (e: Exception) {
            _uiState.update {
                it.copy(
                    isLoadingRevenue  = false,
                    isLoadingRotation = false,
                    error             = e.message
                )
            }
        }
    }
}
```

---

## 5. Archivos a Crear / Modificar

### Nuevos — Domain
```
domain/entity/ReportPeriod.kt
domain/entity/ReportFilters.kt
domain/entity/TransactionItemDetail.kt
domain/entity/TransactionHistoryEntry.kt
domain/entity/RevenueDataPoint.kt
domain/entity/RevenueSummary.kt
domain/entity/ProductRotationEntry.kt
domain/entity/MarginEntry.kt
domain/repository/ReportsRepository.kt
domain/usecase/GetTransactionHistoryUseCase.kt
domain/usecase/GetRevenueSummaryUseCase.kt
domain/usecase/GetProductRotationUseCase.kt
domain/usecase/GetMarginsUseCase.kt
```

### Nuevos — Data
```
data/mapper/ReportsMapper.kt
data/repository/ReportsRepositoryImpl.kt
```

### Nuevos — Presentation
```
presentation/reports/TransactionHistoryUiState.kt
presentation/reports/StatisticsUiState.kt
presentation/reports/TransactionHistoryViewModel.kt
presentation/reports/StatisticsViewModel.kt
presentation/reports/ReportsScreen.kt
presentation/reports/TransactionHistoryScreen.kt
presentation/reports/StatisticsScreen.kt
presentation/reports/components/DateRangePicker.kt
presentation/reports/components/PeriodSelector.kt
presentation/reports/components/TransactionTable.kt
presentation/reports/components/RevenueChart.kt
presentation/reports/components/RotationPanel.kt
presentation/reports/components/MarginPanel.kt
```

### Nuevos — Tests
```
test/.../data/ReportsRepositoryFake.kt
test/.../domain/GetTransactionHistoryUseCaseTest.kt
test/.../domain/GetRevenueSummaryUseCaseTest.kt
test/.../domain/GetProductRotationUseCaseTest.kt
test/.../domain/GetMarginsUseCaseTest.kt
test/.../presentation/TransactionHistoryViewModelTest.kt
test/.../presentation/StatisticsViewModelTest.kt
```

### Modificados
```
di/AppModules.kt        ← añadir reportsModule
presentation/App.kt     ← conectar ReportsScreen al slot de navegación existente
```

---

## 6. Decisiones de Implementación

### Gráfico de ingresos sin dependencias externas
`RevenueChart` se implementa con la API `Canvas` de Compose Desktop (`drawRect`, `drawLine`,
`drawIntoCanvas`). No se agrega ninguna librería de gráficos externa. El componente es
stateless: recibe `List<RevenueDataPoint>` y el tamaño disponible; calcula la escala
internamente. Si en etapas posteriores se requieren gráficos más complejos (Etapa 6),
se puede reemplazar sin impacto en el dominio.

### Paginación del historial
Paginación por offset: el ViewModel mantiene `currentPage` y calcula
`offset = currentPage * HISTORY_PAGE_SIZE` antes de invocar el caso de uso.
`hasNextPage` se determina verificando si el repositorio devolvió exactamente
`HISTORY_PAGE_SIZE` registros (si devuelve menos, no hay más páginas).

### Márgenes sin filtro de fecha
`getMargins()` opera sobre precios actuales (`costPrice`/`salePrice` del catálogo vigente),
no sobre el historial de ventas. Filtrar por fecha no tiene semántica útil aquí; si
cambiaron los precios en el pasado, esa información no está registrada en el modelo actual.
Si en el futuro se requiere historial de precios, será una feature de Etapa 6.

### Split de ViewModels
Se usan dos ViewModels en lugar de uno unificado porque sus responsabilidades divergen:
`TransactionHistoryViewModel` gestiona paginación y filtros de búsqueda; `StatisticsViewModel`
gestiona carga paralela de agregados y filtros de período. Unificarlos generaría un estado
compuesto grande y tests con excesivo setup.

### Use cases sin interfaz
Los casos de uso de esta etapa siguen la convención del resto del proyecto: clases concretas
sin interfaz separada, inyectadas directamente en los ViewModels vía Koin.

---

## 7. Criterios de Éxito

- La tab **Historial** muestra ventas paginadas (50 por página), filtradas por el rango
  de fechas seleccionado en `DateRangePicker`. Cada fila es expandible y muestra el detalle
  de ítems de la venta.
- La tab **Ingresos** muestra un gráfico de barras con revenue por período, métricas
  resumidas (total, cantidad de ventas, ticket promedio) y responde al cambio de
  granularidad (`PeriodSelector`) sin recargar márgenes ni rotación.
- La tab **Rotación** muestra el top 20 de productos más vendidos en el período,
  ordenados por unidades vendidas descendente.
- La tab **Márgenes** muestra todos los productos con color de fila según rango de margen
  (rojo / amarillo / verde). La tabla es ordenable por cualquier columna en la UI.
- `onFiltersChanged()` en `StatisticsViewModel` recarga revenue y rotación en paralelo;
  los márgenes **no** se recargan.
- `loadNextPage()` en `TransactionHistoryViewModel` acumula registros correctamente sin
  duplicar la página anterior.
- Todos los tests pasan con `UnconfinedTestDispatcher`. No hay `advanceUntilIdle` manual.
- No hay código muerto. No hay comentarios de "qué". No hay imports sin uso.

---

## 8. Checklist de Progreso

### Fase A — Dominio
- [X] 5A-1 `ReportFilters` y `ReportPeriod` con `default()` definidos
- [X] 5A-2 `TransactionHistoryEntry` y `TransactionItemDetail` definidos
- [X] 5A-3 `RevenueSummary`, `RevenueDataPoint`, `ProductRotationEntry`, `MarginEntry` definidos
- [X] 5A-4 Interfaz `ReportsRepository` declarada
- [X] 5A-5 `GetTransactionHistoryUseCase`, `GetRevenueSummaryUseCase`,
  `GetProductRotationUseCase`, `GetMarginsUseCase` implementados

### Fase B — Datos
- [X] 5B-1 `ReportsMapper` implementado (margen, etiquetas de período, `RevenueSummary`)
- [X] 5B-2 `ReportsRepositoryImpl` implementado con queries Exposed directas

### Fase C — DI
- [ ] 5C-1 Bindings Koin registrados (repo, use cases, 2 ViewModels)

### Fase D — Presentación
- [ ] 5D-1 `TransactionHistoryUiState` y `StatisticsUiState` definidos
- [ ] 5D-2 `TransactionHistoryViewModel` con paginación y filtros
- [ ] 5D-3 `StatisticsViewModel` con carga paralela y filtros
- [ ] 5D-4 `RevenueChart` con Canvas (sin dependencias externas)
- [ ] 5D-5 Sub-composables: `DateRangePicker`, `PeriodSelector`, `TransactionTable`,
  `RotationPanel`, `MarginPanel`
- [ ] 5D-6 `TransactionHistoryScreen`, `StatisticsScreen`, `ReportsScreen` con tab row
  integrada en `App.kt`

### Fase E — Tests
- [ ] 5E-1 `ReportsRepositoryFake` en archivo propio con captura de parámetros
- [ ] 5E-2 Tests de los 4 casos de uso (éxito, error, verificación de parámetros)
- [ ] 5E-3 `TransactionHistoryViewModelTest` (estado inicial, carga, paginación,
  filtro, error, refresh)
- [ ] 5E-4 `StatisticsViewModelTest` (estado inicial, carga paralela, cambio de filtros,
  márgenes no se recargan, error)