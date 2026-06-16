# Etapa 1 — Modelo de Datos y Persistencia

> **Precondiciones:** Etapa 0 completada (Build system, Koin, DatabaseFactory configurado).
> **Objetivo:** Definir las entidades principales del dominio (Producto, Categoría) y su persistencia en la base de datos local usando SQLite y Exposed. Implementar los repositorios básicos para las operaciones CRUD respetando la arquitectura Clean MVVM.

---

## Tareas

### T1.1 — Definir Entidades de Dominio
**Archivos a crear:**
- `src/main/kotlin/com/app/domain/entity/Category.kt`
- `src/main/kotlin/com/app/domain/entity/Product.kt`

**Detalle:**
- Crear las `data class` puras de Kotlin que representan el núcleo del negocio.
- `Category`: `id`, `name`.
- `Product`: `id`, `barcode`, `name`, `categoryId`, `price`, `cost`, `stock`.

**Criterio de éxito:** Las entidades compilan y no tienen dependencias del framework ni de bases de datos.

- [ ] Completada

---

### T1.2 — Crear Tablas y Mappers (Data Layer)
**Archivos a crear:**
- `src/main/kotlin/com/app/data/local/CategoryTable.kt`
- `src/main/kotlin/com/app/data/local/ProductTable.kt`
- `src/main/kotlin/com/app/data/mapper/Mappers.kt`

**Archivos a modificar:**
- `src/main/kotlin/com/app/data/local/DatabaseFactory.kt`

**Detalle:**
- Crear los objetos de tabla heredando de `Table` de Exposed.
- Actualizar `DatabaseFactory.init()` para ejecutar `SchemaUtils.create(CategoryTable, ProductTable)` dentro de una transacción.
- Crear funciones de extensión en `Mappers.kt` para convertir de `ResultRow` (fila de BD) a Entidades puras de dominio.

**Criterio de éxito:** La app corre y las tablas se crean en `stock_control.db`.

- [ ] Completada

---

### T1.3 — Implementar Repositorios
**Archivos a crear:**
- `src/main/kotlin/com/app/domain/repository/InventoryRepository.kt` (Interfaz)
- `src/main/kotlin/com/app/data/repository/InventoryRepositoryImpl.kt` (Implementación)

**Detalle:**
- Definir la interfaz en `domain` con métodos básicos. Siguiendo el principio de Ponytail (YAGNI), empezaremos sólo con lo que necesitamos: `getProducts`, `insertProduct`.
- Implementar la interfaz en `data` usando operaciones y transacciones de Exposed.

**Criterio de éxito:** Las interfaces aíslan a los repositorios de la base de datos, manteniendo Clean Architecture.

- [ ] Completada

---

### T1.4 — Inyección de Dependencias
**Archivos a modificar:**
- `src/main/kotlin/com/app/di/AppModules.kt`

**Detalle:**
- Registrar `InventoryRepositoryImpl` como un `single` casteado a `InventoryRepository` en Koin.

**Criterio de éxito:** El módulo de Koin arranca sin errores de resolución.

- [ ] Completada

---

### T1.5 — Verificación Final y Commit
**Detalle:**
- Verificar que el proyecto compile sin errores.
- Ejecutar la aplicación para forzar a que Exposed inicialice el esquema de BD.
- Realizar el commit correspondiente.

**Criterio de éxito:** El commit pasa y la etapa se cierra exitosamente.

- [ ] Completada
