# Plan Detallado — Etapa 7: Integración Dólar API

> **Etapa:** 7 de 7  
> **Proyecto:** Sistema de Control de Stock  
> **Arquitectura:** Clean MVVM (Compose Desktop + Kotlin)  
> **Lineamientos:** `docs/consideraciones_lineamientos.md`  
> **Depende de:** Etapas 0–6 completadas

---

## 1. Objetivo

Integrar la API pública `dolarapi.com` para incorporar la cotización del dólar blue en
tiempo real. Los datos de esta API se **combinan** con los datos de productos ya existentes
(persistidos localmente y enriquecidos vía OpenFoodFacts) para mostrar precios en USD en
el catálogo de inventario y una cotización en el Dashboard.

Esta etapa cierra el requisito de consumir al menos dos APIs públicas e integrar sus datos.

---

## 2. Precondiciones

- **Etapas 0–6 ✅** — App completa con inventario, POS, dashboard, reportes y configuración.
- Ktor Client ya declarado como dependencia (usado en Etapa 2C para OpenFoodFacts).
- `kotlinx.serialization` ya configurado en el proyecto.

---

## 3. Tareas

### Fase A — Capa de Dominio

**Tarea 7A-1 — Entity `ExchangeRate`**  
Crear `domain/entity/ExchangeRate.kt`:
- `buy: Double` — precio de compra del dólar blue.
- `sell: Double` — precio de venta del dólar blue.
- `name: String` — nombre del tipo de cambio (ej. "Blue").

**Tarea 7A-2 — Interfaz `ExchangeRateRepository`**  
Declarar en `domain/repository/ExchangeRateRepository.kt`:
- `getBlueRate(): ExchangeRate`

**Tarea 7A-3 — `GetExchangeRateUseCase`**  
Clase concreta en `domain/usecase/exchangerate/GetExchangeRateUseCase.kt`.
Delega directamente al repositorio. Sin validaciones (datos de solo lectura).

### Fase B — Capa de Datos

**Tarea 7B-1 — DTO `ExchangeRateResponse`**  
Crear `data/external/dolar/ExchangeRateResponse.kt` con `@Serializable`.
Campos: `buy`, `sell`, `name` mapeados con `@SerialName` desde el JSON de la API
(`compra`, `venta`, `nombre`).

**Tarea 7B-2 — `ExchangeRateRemoteDataSource`**  
Crear `data/external/dolar/ExchangeRateRemoteDataSource.kt`.
Usa el `HttpClient` de Ktor ya configurado en el proyecto.
Endpoint: `GET https://dolarapi.com/v1/dolares/blue`.

**Tarea 7B-3 — Mapper**  
Agregar en `data/mapper/Mappers.kt`:
- `ExchangeRateResponse.toExchangeRate(): ExchangeRate`

**Tarea 7B-4 — `ExchangeRateRepositoryImpl`**  
Crear `data/repository/ExchangeRateRepositoryImpl.kt`.
Llama a `remoteDataSource.getBlueRate()` y aplica el mapper.

### Fase C — Inyección de Dependencias

**Tarea 7C-1 — Bindings Koin**  
Agregar en `di/AppModules.kt`:
- `ExchangeRateRemoteDataSource` (`single`)
- `ExchangeRateRepositoryImpl` como `ExchangeRateRepository` (`single`)
- `GetExchangeRateUseCase` (`factory`)

### Fase D — Presentación

**Tarea 7D-1 — Dashboard: actualizar `DashboardUiState`**  
Agregar campos:
- `exchangeRate: ExchangeRate?`
- `exchangeRateError: Boolean`

**Tarea 7D-2 — Dashboard: actualizar `DashboardViewModel`**  
Inyectar `GetExchangeRateUseCase` y cargarlo en paralelo con las métricas existentes
dentro del `launch` inicial. Error aislado: si falla, solo `exchangeRateError = true`,
no afecta el resto del estado.

**Tarea 7D-3 — `ExchangeRateWidget`**  
Crear `presentation/dashboard/components/ExchangeRateWidget.kt`.
Muestra precio de compra y venta del blue. Estado de error con mensaje.

**Tarea 7D-4 — `DashboardScreen`: integrar widget**  
Añadir `ExchangeRateWidget` en la pantalla del Dashboard.

**Tarea 7D-5 — Inventario: actualizar `InventoryUiState`**  
Agregar campos:
- `exchangeRate: ExchangeRate?`
- `exchangeRateError: Boolean`

**Tarea 7D-6 — Inventario: actualizar `InventoryViewModel`**  
Inyectar `GetExchangeRateUseCase` y cargarlo al inicializar junto con los productos.

**Tarea 7D-7 — Inventario: mostrar precio en USD**  
En el composable que lista productos, mostrar el precio en USD calculado
(`precio ARS / exchangeRate.sell`) junto al precio en ARS.
Integración explícita: datos de OpenFoodFacts (producto) + Dólar API (cotización).

### Fase E — Tests Unitarios

**Tarea 7E-1 — `ExchangeRateRepositoryFake`**  
Crear en `test/.../data/` con `shouldThrowError` y valor por defecto configurable.

**Tarea 7E-2 — `GetExchangeRateUseCaseTest`**  
Verificar delegación correcta y propagación de excepción.

**Tarea 7E-3 — Actualizar `DashboardViewModelTest`**  
Agregar casos: cotización cargada correctamente, `exchangeRateError = true` cuando falla.

---

## 4. Diseño de Entities y Contratos

```kotlin
// domain/entity/ExchangeRate.kt
data class ExchangeRate(
    val buy: Double,
    val sell: Double,
    val name: String
)

// domain/repository/ExchangeRateRepository.kt
interface ExchangeRateRepository {
    suspend fun getBlueRate(): ExchangeRate
}
```

---

## 5. Archivos a Crear / Modificar

### Nuevos — Domain
```
domain/entity/ExchangeRate.kt
domain/repository/ExchangeRateRepository.kt
domain/usecase/exchangerate/GetExchangeRateUseCase.kt
```

### Nuevos — Data
```
data/external/dolar/ExchangeRateResponse.kt
data/external/dolar/ExchangeRateRemoteDataSource.kt
data/repository/ExchangeRateRepositoryImpl.kt
```

### Modificados — Data
```
data/mapper/Mappers.kt  ← añadir ExchangeRateResponse.toExchangeRate()
```

### Modificados — DI
```
di/AppModules.kt  ← añadir bindings de exchange rate
```

### Modificados — Presentation
```
presentation/dashboard/DashboardUiState.kt     ← añadir exchangeRate, exchangeRateError
presentation/dashboard/DashboardViewModel.kt   ← inyectar y cargar use case
presentation/dashboard/DashboardScreen.kt      ← integrar ExchangeRateWidget
presentation/inventory/InventoryUiState.kt     ← añadir exchangeRate, exchangeRateError
presentation/inventory/InventoryViewModel.kt   ← inyectar y cargar use case
```

### Nuevos — Presentation
```
presentation/dashboard/components/ExchangeRateWidget.kt
```

### Nuevos — Tests
```
test/.../data/ExchangeRateRepositoryFake.kt
test/.../domain/usecase/GetExchangeRateUseCaseTest.kt
```

### Modificados — Tests
```
test/.../presentation/DashboardViewModelTest.kt  ← nuevos casos de cotización
```

---

## 6. Decisiones de Implementación

### API sin autenticación
`dolarapi.com` no requiere API key. El `HttpClient` existente (configurado para
OpenFoodFacts) se reutiliza directamente sin ninguna cabecera adicional.

### Error aislado
Si la cotización falla (sin internet, API caída), solo se activa `exchangeRateError`.
El resto del Dashboard e Inventario funciona con normalidad. El widget muestra
"No disponible" en lugar de bloquear la pantalla.

### Sin caché local
La cotización es datos volátiles. No se persiste en SQLite. Si no hay conexión,
se muestra el estado de error. Esto es consistente con la naturaleza del dato.

### Precio USD calculado en la UI
`precioUSD = precioARS / exchangeRate.sell` se calcula en el composable,
no en el ViewModel ni en el use case. Es una transformación de presentación pura.

---

## 7. Criterios de Éxito

- El Dashboard muestra la cotización blue (compra/venta) en tiempo real.
- El catálogo de inventario muestra el precio en USD junto al precio en ARS.
- Si la API no está disponible, el error es localizado: no rompe otras secciones.
- `GetExchangeRateUseCaseTest` pasa con delegación y error cubiertos.
- `DashboardViewModelTest` cubre el nuevo estado de cotización.
- No se agrega ninguna dependencia nueva al proyecto (usa Ktor ya existente).

---

## 8. Checklist de Progreso

### Fase A — Dominio
- [X] 7A-1 Interfaz `ExchangeRateRepository` declarada
- [X] 7A-2 `GetExchangeRateUseCase` implementado

### Fase B — Datos
- [X] 7B-1 `ExchangeRateResponse` DTO con `@Serializable`
- [X] 7B-2 `ExchangeRateRemoteDataSource` con Ktor
- [X] 7B-3 Mapper `ExchangeRateResponse.toExchangeRate()` en `Mappers.kt`
- [X] 7B-4 `ExchangeRateRepositoryImpl` implementado

### Fase C — DI
- [X] 7C-1 Bindings Koin registrados

### Fase D — Presentación
- [X] 7D-1 `DashboardUiState` actualizado
- [X] 7D-2 `DashboardViewModel` actualizado
- [X] 7D-3 `ExchangeRateWidget` creado
- [X] 7D-4 `DashboardScreen` integra widget
- [X] 7D-5 `InventoryUiState` actualizado
- [X] 7D-6 `InventoryViewModel` actualizado
- [X] 7D-7 Lista de productos muestra precio en USD

### Fase E — Tests
- [X] 7E-1 `ExchangeRateRepositoryFake` creado
- [X] 7E-2 `GetExchangeRateUseCaseTest` con éxito y error
- [X] 7E-3 `DashboardViewModelTest` actualizado con casos de cotización