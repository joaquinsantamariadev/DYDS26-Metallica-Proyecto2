# Etapa 1 — Modelo de Datos y Persistencia

> **Precondiciones:** Etapa 0 completada (Build system, Koin, DatabaseFactory configurado).
> **Objetivo:** Definir las entidades principales del dominio (Producto, Categoría, Tipo de Cambio) y su persistencia en la base de datos local usando SQLite y Exposed. Implementar los repositorios básicos para las operaciones CRUD respetando la arquitectura Clean MVVM.

---

## Tareas

### T1.1 — Definir Entidades de Dominio
**Archivos a crear:**
- `src/main/kotlin/com/app/domain/entity/Category.kt`
- `src/main/kotlin/com/app/domain/entity/Product.kt`
- `src/main/kotlin/com/app/domain/entity/ExchangeRate.kt`

**Detalle:**
- Crear las `data class` puras de Kotlin que representan el núcleo del negocio.
- `Category`: `id`, `name`.
- `Product`: `id`, `barcode`, `name`, `categoryId`, `price` (en USD), `cost` (en USD), `stock`.
- `ExchangeRate`: `currencyPair` (ej: "USD/ARS"), `rate` (tasa de cambio, ej: 1000.0), `lastUpdated` (timestamp en ms).

**Criterio de éxito:** Las entidades compilan y no tienen dependencias del framework ni de bases de datos.

- [x] Completada

---

### T1.2 — Crear Tablas y Mappers (Data Layer)
**Archivos a crear:**
- `src/main/kotlin/com/app/data/local/CategoryTable.kt`
- `src/main/kotlin/com/app/data/local/ProductTable.kt`
- `src/main/kotlin/com/app/data/local/ExchangeRateTable.kt`
- `src/main/kotlin/com/app/data/mapper/Mappers.kt`

**Archivos a modificar:**
- `src/main/kotlin/com/app/data/local/DatabaseFactory.kt`

**Detalle:**
- Crear los objetos de tabla heredando de `Table` de Exposed.
- Actualizar `DatabaseFactory.init()` para ejecutar `SchemaUtils.create(CategoryTable, ProductTable, ExchangeRateTable)` dentro de una transacción.
- Crear funciones de extensión en `Mappers.kt` para convertir de `ResultRow` (fila de BD) a Entidades puras de dominio.

**Criterio de éxito:** La app corre y las tablas se crean en `stock_control.db`.

- [x] Completada

---

### T1.3 — Implementar Repositorios
**Archivos a crear:**
- `src/main/kotlin/com/app/domain/repository/InventoryRepository.kt` (Interfaz)
- `src/main/kotlin/com/app/data/repository/InventoryRepositoryImpl.kt` (Implementación)
- `src/main/kotlin/com/app/domain/repository/ExchangeRateRepository.kt` (Interfaz)
- `src/main/kotlin/com/app/data/repository/ExchangeRateRepositoryImpl.kt` (Implementación local/caché)

**Detalle:**
- Definir la interfaz de inventario con métodos básicos (`getProducts`, `insertProduct`).
- Definir la interfaz de tipo de cambio con métodos para obtener la tasa local (`getLocalRate()`) y actualizarla (`saveRate(ExchangeRate)`). El consumo de la API externa se conectará en el POS o Configuración, pero la base de persistencia queda lista.
- Implementar las interfaces en `data` usando operaciones y transacciones de Exposed.

**Criterio de éxito:** Las interfaces aíslan a los repositorios de la base de datos, manteniendo Clean Architecture.

- [x] Completada

---

### T1.4 — Inyección de Dependencias
**Archivos a modificar:**
- `src/main/kotlin/com/app/di/AppModules.kt`

**Detalle:**
- Registrar `InventoryRepositoryImpl` y `ExchangeRateRepositoryImpl` en Koin.

**Criterio de éxito:** El módulo de Koin arranca sin errores de resolución.

- [x] Completada

---

### T1.5 — Verificación Final y Commit
**Detalle:**
- Verificar que el proyecto compile sin errores.
- Ejecutar la aplicación para forzar a que Exposed inicialice el esquema de BD con las nuevas tablas.
- Realizar el commit correspondiente.

**Criterio de éxito:** El commit pasa y la etapa se cierra exitosamente.

- [x] Completada
