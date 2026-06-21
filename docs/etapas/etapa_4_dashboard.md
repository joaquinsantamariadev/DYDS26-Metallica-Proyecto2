# Plan Detallado — Etapa 4: Dashboard

> **Etapa:** 4 de 7
> **Proyecto:** Sistema de Control de Stock
> **Arquitectura:** Clean MVVM (Compose Desktop + Kotlin)
> **Lineamientos:** `docs/consideraciones_lineamientos.md`
> **Depende de:** Etapas 0, 1, 2 y 3 completadas

---

## 1. Objetivo

Implementar el panel principal del sistema: una vista de sólo lectura que consolida métricas
clave del negocio en tiempo real, expone alertas accionables (bajo stock, vencimientos próximos)
y presenta un feed de las últimas ventas registradas.

El dashboard **no genera ni modifica datos**. Toda su lógica de cómputo vive en casos de uso
específicos que consultan un `DashboardRepository` dedicado —con queries de agregación propias—
sin contaminar los repositorios de entidades existentes (`ProductRepository`, `SaleRepository`, etc.).

---

## 2. Precondiciones

- **Etapa 0 ✅** — App compilable, navegación con sidebar funcional, tema definido.
- **Etapa 1 ✅** — Entities `Product`, `Category`, `Sale`, `SaleItem`, `CashRegisterSession`
  definidas y sus tablas creadas en BD.
- **Etapa 2 ✅** — Productos con campos `minStock` y `expiryDate` presentes en `Product` y en
  la tabla correspondiente.
- **Etapa 3 ✅** — Ventas registradas con reducción transaccional de stock y sesiones de caja
  con registros de apertura/cierre en BD.

---

## 3. Tareas

### Fase A — Capa de Dominio

**Tarea 4A-1 — Entities del dashboard**
Crear las cuatro data classes puras en `domain/entity/`:
`DashboardMetrics`, `StockAlert`, `ExpiryAlert`, `RecentSaleEntry`.
No contienen lógica; sólo transportan datos hacia la presentación.

**Tarea 4A-2 — Interfaz `DashboardRepository`**
Declarar los cuatro contratos de consulta en `domain/repository/DashboardRepository.kt`.
Cada método retorna su propia entidad; ninguno devuelve tipos de Exposed ni primitivos sueltos.

**Tarea 4A-3 — Casos de uso**
Implementar las cuatro clases (`GetDashboardMetricsUseCase`, etc.), cada una con un `operator fun invoke`.
Las constantes de negocio (`EXPIRY_ALERT_DAYS`, `RECENT_SALES_LIMIT`) se definen aquí,
no en el repositorio ni en la UI. No se deben crear interfaces para los casos de uso, son clases puras.

### Fase B — Capa de Datos

**Tarea 4B-1 — `DashboardRepositoryImpl`**
Implementar las queries de agregación consultando Exposed directamente (`ProductsTable`, `SalesTable`, etc.)
dentro de bloques `transaction { }`. No se deben crear DataSources intermedios.

**Tarea 4B-2 — Mappers de extensión**
Agregar funciones de extensión puras a `ResultRow` en `data/mapper/Mappers.kt` para mapear los resultados
de Exposed hacia las entities del dashboard (`toStockAlert()`, `toRecentSaleEntry()`, etc.).
`daysRemaining` en `ExpiryAlert` se calcula aquí, a partir de `expiryDate` y `LocalDate.now()`.

### Fase C — Inyección de Dependencias

**Tarea 4C-1 — Módulo Koin del dashboard**
Agregar un bloque `val dashboardModule = module { ... }` en `di/AppModules.kt`.
Registrar: repositorio (como `single`), los cuatro casos de uso (como `factory`) y ViewModel (como `factory`).

### Fase D — Capa de Presentación

**Tarea 4D-1 — `DashboardUiState`**
Sealed class o data class con campos para métricas, listas de alertas, feed de ventas,
flag `isLoading` y campo `error: String?`.

**Tarea 4D-2 — `DashboardViewModel`**
Lanza las cuatro consultas **en paralelo** usando `coroutineScope { async { } }` dentro de
`viewModelScope.launch`. Expone `uiState: StateFlow<DashboardUiState>` y un método `refresh()`
público que re-ejecuta la carga completa.

**Tarea 4D-3 — Sub-composables**
Crear los componentes en `presentation/dashboard/components/`:
- `KpiCard` — tarjeta reutilizable (título, valor formateado, ícono opcional).
- `StockAlertsPanel` — lista de `StockAlert`, ordenada por `currentStock / minStock` ascendente
  (los más críticos primero). Cada ítem muestra nombre, stock actual y mínimo.
- `ExpiryAlertsPanel` — lista de `ExpiryAlert`, ordenada por `daysRemaining` ascendente.
  Cada ítem muestra nombre, fecha de vencimiento y días restantes.
- `RecentActivityPanel` — lista de `RecentSaleEntry` con fecha, monto total formateado
  e ítem count.

**Tarea 4D-4 — `DashboardScreen`**
Layout principal que compone los paneles anteriores. El estado de carga y error se manejan
aquí con un `CircularProgressIndicator` o un mensaje de error centrado.
Conectar al slot de navegación existente en `App.kt`.

### Fase E — Tests Unitarios

**Tarea 4E-1 — `DashboardRepositoryFake`**
Extraer el fake a `test/.../data/DashboardRepositoryFake.kt` (nunca como clase privada
dentro de un archivo de test). Expone propiedades mutables públicas: `metricsResult`,
`stockAlertsResult`, `expiryAlertsResult`, `recentSalesResult`, `shouldThrowError`.

**Tarea 4E-2 — Tests de casos de uso**
Un archivo por caso de uso. Verificar:
- Retorno correcto al delegar al repositorio.
- Propagación de excepción cuando `shouldThrowError = true`.
- Que `GetExpiryAlertsUseCase` pasa `EXPIRY_ALERT_DAYS` al repositorio.
- Que `GetRecentSalesUseCase` pasa `RECENT_SALES_LIMIT` al repositorio.

**Tarea 4E-3 — `DashboardViewModelTest`**
Usar `UnconfinedTestDispatcher`. Verificar:
- Estado inicial (`isLoading = false`, datos vacíos).
- Estado durante carga (`isLoading = true`).
- Estado tras carga exitosa (datos poblados, `isLoading = false`, `error = null`).
- Estado tras error (`error != null`, `isLoading = false`).
- `refresh()` re-ejecuta la carga y vuelve a poblar el estado.

---

## 4. Diseño de Entities y Contratos

### Entities de Dominio

```kotlin
// domain/entity/DashboardMetrics.kt
data class DashboardMetrics(
    val totalProducts: Int,
    val inventoryValue: Double,
    val salesToday: Int,
    val revenueToday: Double,
    val lowStockCount: Int,
    val nearExpiryCount: Int,
    val hasActiveSession: Boolean
)

// domain/entity/StockAlert.kt
data class StockAlert(
    val productId: Long,
    val productName: String,
    val currentStock: Int,
    val minStock: Int
)

// domain/entity/ExpiryAlert.kt
data class ExpiryAlert(
    val productId: Long,
    val productName: String,
    val expiryDate: LocalDate,
    val daysRemaining: Int
)

// domain/entity/RecentSaleEntry.kt
data class RecentSaleEntry(
    val saleId: Long,
    val dateTime: LocalDateTime,
    val itemCount: Int,
    val total: Double
)
```

### Interfaz del Repositorio

```kotlin
// domain/repository/DashboardRepository.kt
interface DashboardRepository {
    suspend fun getMetrics(): DashboardMetrics
    suspend fun getLowStockAlerts(): List<StockAlert>
    suspend fun getExpiryAlerts(withinDays: Int): List<ExpiryAlert>
    suspend fun getRecentSales(limit: Int): List<RecentSaleEntry>
}
```

### Clases de Casos de Uso (Ejemplo)

```kotlin
class GetDashboardMetricsUseCase(private val repository: DashboardRepository) {
    suspend operator fun invoke(): DashboardMetrics {
        return repository.getMetrics()
    }
}
// Repetir patrón para GetLowStockAlertsUseCase, GetExpiryAlertsUseCase, GetRecentSalesUseCase
```

### Estado de UI

```kotlin
// presentation/dashboard/DashboardUiState.kt
data class DashboardUiState(
    val metrics: DashboardMetrics? = null,
    val stockAlerts: List<StockAlert> = emptyList(),
    val expiryAlerts: List<ExpiryAlert> = emptyList(),
    val recentSales: List<RecentSaleEntry> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)
```

### Carga en Paralelo en el ViewModel

```kotlin
// Esquema de carga paralela — no escribir así literalmente; adaptar al ViewModel real
private fun load() {
    viewModelScope.launch {
        _uiState.update { it.copy(isLoading = true, error = null) }
        try {
            coroutineScope {
                val metrics = async { getMetrics() }
                val stock   = async { getLowStockAlerts() }
                val expiry  = async { getExpiryAlerts() }
                val sales   = async { getRecentSales() }
                _uiState.update {
                    it.copy(
                        metrics      = metrics.await(),
                        stockAlerts  = stock.await(),
                        expiryAlerts = expiry.await(),
                        recentSales  = sales.await(),
                        isLoading    = false
                    )
                }
            }
        } catch (e: Exception) {
            _uiState.update { it.copy(isLoading = false, error = e.message) }
        }
    }
}
```

---

## 5. Archivos a Crear / Modificar

### Nuevos — Domain
```
domain/entity/DashboardMetrics.kt
domain/entity/StockAlert.kt
domain/entity/ExpiryAlert.kt
domain/entity/RecentSaleEntry.kt
domain/repository/DashboardRepository.kt
domain/usecase/GetDashboardMetricsUseCase.kt
domain/usecase/GetLowStockAlertsUseCase.kt
domain/usecase/GetExpiryAlertsUseCase.kt
domain/usecase/GetRecentSalesUseCase.kt
```

### Nuevos — Data
```
data/repository/DashboardRepositoryImpl.kt
```

### Modificados — Data
```
data/mapper/Mappers.kt    ← añadir extensiones toStockAlert(), toExpiryAlert(), etc.
```

### Nuevos — Presentation
```
presentation/dashboard/DashboardUiState.kt
presentation/dashboard/DashboardViewModel.kt
presentation/dashboard/DashboardScreen.kt
presentation/dashboard/components/KpiCard.kt
presentation/dashboard/components/StockAlertsPanel.kt
presentation/dashboard/components/ExpiryAlertsPanel.kt
presentation/dashboard/components/RecentActivityPanel.kt
```

### Nuevos — Tests
```
test/.../data/DashboardRepositoryFake.kt
test/.../domain/GetDashboardMetricsUseCaseTest.kt
test/.../domain/GetLowStockAlertsUseCaseTest.kt
test/.../domain/GetExpiryAlertsUseCaseTest.kt
test/.../domain/GetRecentSalesUseCaseTest.kt
test/.../presentation/DashboardViewModelTest.kt
```

### Modificados
```
di/AppModules.kt          ← añadir dashboardModule (o importar DashboardModule.kt)
presentation/App.kt       ← conectar DashboardScreen al slot de navegación existente
```

---

## 6. Constantes de Negocio

Definidas dentro de la implementación de cada caso de uso. Nunca hardcodeadas en el
repositorio, en el data source ni en la UI.

| Constante            | Valor | Descripción                                             |
|----------------------|-------|---------------------------------------------------------|
| `EXPIRY_ALERT_DAYS`  | `7`   | Días de anticipación para alertar vencimientos próximos |
| `RECENT_SALES_LIMIT` | `10`  | Cantidad de ventas recientes en el feed de actividad    |

---

## 7. Criterios de Éxito

- `DashboardScreen` muestra los 4 KPI cards con datos reales de BD: total de productos,
  valor del inventario, ventas del día y estado de la sesión de caja activa.
- El panel de bajo stock lista únicamente productos con `currentStock <= minStock`,
  ordenados de mayor a menor criticidad (menor ratio `currentStock / minStock` primero).
- El panel de vencimientos lista productos cuyo `expiryDate` cae dentro de los próximos
  7 días, ordenados cronológicamente (más urgente primero).
- El feed de actividad muestra las últimas 10 ventas con fecha/hora, monto y cantidad de ítems.
- `refresh()` del ViewModel recarga el estado completo correctamente.
- Las 4 queries al repositorio se lanzan en paralelo (`async`) en el ViewModel.
- Todos los tests pasan usando `UnconfinedTestDispatcher` sin `advanceUntilIdle` manual.
- No hay código muerto. No hay comentarios descriptivos de "qué". No hay imports sin uso.

---

## 8. Checklist de Progreso

### Fase A — Dominio
- [X] 4A-1 `DashboardMetrics`, `StockAlert`, `ExpiryAlert`, `RecentSaleEntry` definidas
- [X] 4A-2 Interfaz `DashboardRepository` declarada
- [X] 4A-3 Clases de los 4 casos de uso implementadas con constantes de negocio

### Fase B — Datos
- [X] 4B-1 `DashboardRepositoryImpl` con queries Exposed implementado
- [X] 4B-2 Funciones de extensión en `Mappers.kt` agregadas

### Fase C — DI
- [X] 4C-1 Bindings de Koin registrados (repo, use cases, ViewModel)

### Fase D — Presentación
- [X] 4D-1 `DashboardUiState` definido
- [X] 4D-2 `DashboardViewModel` con carga paralela y `refresh()`
- [X] 4D-3 Sub-composables: `KpiCard`, `StockAlertsPanel`, `ExpiryAlertsPanel`,
  `RecentActivityPanel`
- [X] 4D-4 `DashboardScreen` integrado en `App.kt`

### Fase E — Tests
- [X] 4E-1 `DashboardRepositoryFake` en archivo propio
- [X] 4E-2 Tests de los 4 casos de uso (éxito + error)
- [X] 4E-3 `DashboardViewModelTest` (estado inicial, carga, error, refresh)
