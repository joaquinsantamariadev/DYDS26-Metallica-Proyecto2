# Etapa 3 — Punto de Venta (POS)

> **Proyecto:** Sistema de control de stock  
> **Arquitectura:** Clean MVVM (Compose Desktop + Kotlin)  
> **Lineamientos:** Ver `docs/consideraciones_lineamientos.md`

---

## 1. Objetivo

Proveer al sistema de un módulo de Punto de Venta (POS) que permita:

- Buscar productos por código de barras o nombre y agregarlos a un carrito de venta.
- Calcular el total dinámicamente a medida que se modifican los ítems del carrito.
- Completar la venta seleccionando el método de pago, lo que reduce el stock de cada producto vendido y persiste la transacción.
- Gestionar sesiones de caja (apertura con monto inicial y cierre con monto final) como precondición para operar el POS.

---

## 2. Precondiciones

- ✅ **Etapa 0** completada: shell de navegación con panel POS vacío ya integrado en `App.kt`.
- ✅ **Etapa 1** completada: `Product` entity, `ProductsTable`, `InventoryRepository` (interfaz e implementación), `DatabaseFactory` operativo.
- ✅ **Etapa 2** completada: catálogo con CRUD completo. Se asume disponible `SearchProductsUseCase` y alguna forma de búsqueda por código de barras local (ver T-06).

---

## 3. Tareas

Las tareas respetan el orden de dependencias: `dominio → datos → DI → presentación → tests`. Cada tarea es un commit atómico.

---

### Fase A — Dominio

**T-01** `feat(domain): add Sale, SaleItem, PaymentMethod, CashRegisterSession entities`

Nuevas entities puras (solo transportan datos, sin lógica de negocio):

- `Sale`: `id: Int?`, `sessionId: Int`, `items: List<SaleItem>`, `total: Double`, `paymentMethod: PaymentMethod`, `createdAt: LocalDateTime`.
- `SaleItem`: `id: Int?`, `productId: Int`, `productName: String` (snapshot en el momento de la venta), `unitPrice: Double`, `quantity: Int`, `subtotal: Double`.
- `PaymentMethod`: enum — `CASH`, `CARD`, `TRANSFER`.
- `CashRegisterSession`: `id: Int?`, `openingAmount: Double`, `closingAmount: Double?`, `openedAt: LocalDateTime`, `closedAt: LocalDateTime?`, `status: SessionStatus` (enum `OPEN` / `CLOSED`).

---

**T-02** `feat(domain): add SaleRepository and CashRegisterRepository interfaces`

- `SaleRepository`:
    - `suspend fun save(sale: Sale): Sale`
    - `suspend fun getById(id: Int): Sale?`
    - `suspend fun getAll(): List<Sale>`
    - `suspend fun getBySessionId(sessionId: Int): List<Sale>`

- `CashRegisterRepository`:
    - `suspend fun open(openingAmount: Double): CashRegisterSession`
    - `suspend fun close(sessionId: Int, closingAmount: Double): CashRegisterSession`
    - `suspend fun getActive(): CashRegisterSession?`
    - `suspend fun getAll(): List<CashRegisterSession>`

Ambas interfaces pertenecen a `domain/repository/`, conforme a la ubicación del resto de contratos del proyecto.

---

**T-02A** `feat(domain): extend InventoryRepository with getProductById and updateStock methods`

Agregar dos métodos a la interfaz `InventoryRepository` (existente):

- `suspend fun getProductById(id: Int): Product?` — obtiene un producto por su ID (requerido por `ValidateCartStockUseCase`).
- `suspend fun updateStock(productId: Int, newStock: Int)` — actualiza el stock de un producto de forma explícita (requerido por `CompleteSaleUseCase`).

Implementar en `InventoryRepositoryImpl` usando Exposed transactions.

---

**T-03** `feat(domain): add ValidateCartStockUseCase`

- **Entrada:** `List<Pair<Int, Int>>` — `(productId, cantidadSolicitada)`.
- **Lógica:** para cada entrada, consulta `InventoryRepository.getProductById(id)` y verifica `product.stock >= cantidadSolicitada`.
- **Salida:** `Result<Unit>` — éxito si todo el stock es suficiente; falla con una excepción que lista los productos con stock insuficiente.
- **Dependencia:** `InventoryRepository` (interfaz existente).
- **Nota:** requiere agregar `suspend fun getProductById(id: Int): Product?` a `InventoryRepository` si no existe (ver T-02A).

---

**T-04** `feat(domain): add CalculateCartTotalUseCase`

- **Entrada:** `List<Pair<Double, Int>>` — `(unitPrice, quantity)`.
- **Lógica:** suma pura de `unitPrice * quantity` para cada par.
- **Salida:** `Double`.
- Sin dependencias externas — lógica pura testeable sin fakes.

---

**T-05** `feat(domain): add CompleteSaleUseCase`

Orquesta el flujo completo de cierre de venta:

1. Delega en `ValidateCartStockUseCase` — falla rápido si el stock no alcanza.
2. Para cada ítem: llama a `InventoryRepository.updateStock(productId, newStock)`.
3. Construye la `Sale` entity y llama a `SaleRepository.save(sale)`.
4. Retorna `Result<Sale>`.

**Dependencias:** `ValidateCartStockUseCase`, `InventoryRepository`, `SaleRepository` — todas por interfaz (DIP).

> **Nota arquitectónica:** la reducción de stock y la persistencia de la venta son dos operaciones sobre repositorios distintos. A esta escala, la validación previa (paso 1) es la principal salvaguarda contra inconsistencias. Requiere agregar `suspend fun updateStock(productId: Int, newStock: Int)` a `InventoryRepository` (ver T-02A). Si se requiriera atomicidad total en el futuro, se introduciría una abstracción de `UnitOfWork`; por ahora no aplica.

---

**T-06** `feat(domain): reutilizar ScanProductUseCase de Etapa 2` *(ya implementado)*

- `ScanProductUseCase` ya existe de Etapa 2 y está registrado en DI.
- **No crear un nuevo `GetProductByBarcodeUseCase`.** Reutilizar directamente en `ProductSearchPanel` (presentación).
- El use case orquesta búsqueda local (`InventoryRepository.getProductByBarcode`) + fallback a OpenFoodFacts API.
- En contexto del POS, se usa para agregar productos al carrito sin creación duplicada de código.

---

**T-07** `feat(domain): add CashRegister use cases`

Tres use cases cohesivos en un mismo commit dado su tamaño reducido:

- `OpenCashRegisterUseCase`: verifica vía `GetActiveSessionUseCase` que no exista sesión activa; si hay una abierta, retorna `Result.failure`. Caso contrario, llama a `CashRegisterRepository.open(openingAmount)`.
- `CloseCashRegisterUseCase`: obtiene la sesión activa; si no existe, retorna `Result.failure`. Caso contrario, llama a `CashRegisterRepository.close(sessionId, closingAmount)`.
- `GetActiveSessionUseCase`: delega directamente en `CashRegisterRepository.getActive()`.

---

### Fase B — Datos

**T-08** `feat(data): add SalesTable, SaleItemsTable, CashRegisterSessionsTable and update DatabaseFactory`

Definición de tablas con Exposed DSL:

- `SalesTable`: `id` (integer autoincrement), `session_id` (integer FK a `CashRegisterSessionsTable`), `total` (double), `payment_method` (varchar), `created_at` (datetime).
- `SaleItemsTable`: `id` (integer autoincrement), `sale_id` (integer FK a `SalesTable`), `product_id` (integer), `product_name` (varchar), `unit_price` (double), `quantity` (integer), `subtotal` (double).
- `CashRegisterSessionsTable`: `id` (integer autoincrement), `opening_amount` (double), `closing_amount` (double nullable), `opened_at` (datetime), `closed_at` (datetime nullable), `status` (varchar).

Modificar `DatabaseFactory` para incluir las tres tablas en el `SchemaUtils.create(...)`.

---

**T-09** `feat(data): add SaleLocalDataSource and SaleLocalDataSourceImpl`

- **Interfaz** `SaleLocalDataSource`:
    - `insertSale(...): Int` (retorna el ID generado)
    - `insertSaleItem(saleId: Int, ...)`
    - `getSaleById(id: Int): ResultRow?`
    - `getAllSales(): List<ResultRow>`
    - `getSalesBySessionId(sessionId: Int): List<ResultRow>`

- **Implementación** `SaleLocalDataSourceImpl`: usa Exposed transactions. No hace mapping — solo devuelve `ResultRow`.

---

**T-10** `feat(data): add CashRegisterLocalDataSource and CashRegisterLocalDataSourceImpl`

- **Interfaz** `CashRegisterLocalDataSource`:
    - `insertSession(openingAmount: Double, openedAt: LocalDateTime): Int`
    - `updateSession(id: Int, closingAmount: Double, closedAt: LocalDateTime, status: String)`
    - `getActiveSession(): ResultRow?`
    - `getAllSessions(): List<ResultRow>`

- **Implementación** `CashRegisterLocalDataSourceImpl`: Exposed transactions.

---

**T-11** `feat(data): add SaleMapper and SaleItemMapper`

- `SaleMapper`: `ResultRow → Sale`. Los ítems se reciben como `List<ResultRow>` y se mapean internamente via `SaleItemMapper`.
- `SaleItemMapper`: `ResultRow → SaleItem`.

Los mappers no se filtran hacia los repositorios ni hacia fuera de la capa de datos.

---

**T-12** `feat(data): add CashRegisterMapper`

- `CashRegisterMapper`: `ResultRow → CashRegisterSession`. Parsea `status` de `String` a `SessionStatus` enum.

---

**T-13** `feat(data): add SaleRepositoryImpl`

- Implementa `SaleRepository`.
- Orquesta `SaleLocalDataSource` y `SaleMapper` para las operaciones CRUD.
- Agnóstico al origen: no menciona "Exposed", "SQLite" ni detalles del esquema SQL.

---

**T-14** `feat(data): add CashRegisterRepositoryImpl`

- Implementa `CashRegisterRepository`.
- Orquesta `CashRegisterLocalDataSource` y `CashRegisterMapper`.

---

**T-15** `feat(di): register POS dependencies in AppModules`

Registrar en Koin (todos los bindings nuevos de esta etapa):

- Data sources: `SaleLocalDataSource`, `CashRegisterLocalDataSource`.
- Mappers: `SaleMapper`, `SaleItemMapper`, `CashRegisterMapper`.
- Repositorios: `SaleRepository` → `SaleRepositoryImpl`, `CashRegisterRepository` → `CashRegisterRepositoryImpl`.
- Use cases: todos los de Fase A.
- ViewModels: `PosViewModel`, `CashRegisterViewModel` (como `viewModel { }` factories de Koin).

---

### Fase C — Presentación: 3A Venta Activa

**T-16** `feat(pos): add PosState, PosEvent, and CartItem`

- `CartItem` (data class de presentación): envuelve `Product` con `quantity: Int` y `subtotal: Double` calculado. No es una entity de dominio — existe solo mientras dura la sesión de UI.
- `PosState`: `activeSession: CashRegisterSession?`, `cartItems: List<CartItem>`, `cartTotal: Double`, `isLoading: Boolean`, `error: String?`, `saleCompleted: Boolean`.
- `PosEvent` (sealed class): `AddItem(product, quantity)`, `RemoveItem(productId)`, `UpdateQuantity(productId, quantity)`, `CompleteSale(paymentMethod)`, `ClearCart`, `DismissError`.

---

**T-17** `feat(pos): add PosViewModel`

- Expone `StateFlow<PosState>`.
- Al inicializar: consulta `GetActiveSessionUseCase` y popula `activeSession` en el estado.
- `onEvent(PosEvent)`:
    - `AddItem` / `RemoveItem` / `UpdateQuantity`: manipula `cartItems` en memoria y recalcula total vía `CalculateCartTotalUseCase`.
    - `CompleteSale`: llama a `ValidateCartStockUseCase` + `CompleteSaleUseCase`; en éxito limpia el carrito y setea `saleCompleted = true`.
    - Si no hay `activeSession`, `CompleteSale` no se ejecuta (falla silenciosa con error en estado).
- Nunca referencia implementaciones concretas de repositorios — solo use cases (DIP en presentación).

---

**T-18** `feat(pos): add POS composable components`

- `ProductSearchPanel.kt`: campo de texto para código de barras + campo de búsqueda por nombre. Al confirmar barcode llama al evento; resultados listados con botón "Agregar al carrito".
- `CartPanel.kt`: lista scrollable de `CartItem` con controles `+` / `−` por ítem, botón de eliminar, y total acumulado al pie. Botón "Cobrar" que abre el diálogo de pago.
- `PaymentDialog.kt`: diálogo modal con selector de `PaymentMethod` (tres opciones) y botones "Confirmar" / "Cancelar". Confirmar dispara `PosEvent.CompleteSale`.

---

**T-19** `feat(pos): add PosScreen and wire navigation`

- `PosScreen.kt`: layout de dos paneles horizontales (`ProductSearchPanel` | `CartPanel`) con una barra de sesión en el encabezado (ver T-23 para el guard). Recibe `PosViewModel` por inyección Koin.
- Modificar `App.kt` / navegación: conectar la `Screen.POS` a `PosScreen`.

---

### Fase D — Presentación: 3B Cierre de Caja

**T-20** `feat(pos/cashregister): add CashRegisterState and CashRegisterEvent`

- `CashRegisterState`: `activeSession: CashRegisterSession?`, `sessionHistory: List<CashRegisterSession>`, `isLoading: Boolean`, `error: String?`.
- `CashRegisterEvent`: `OpenSession(openingAmount: Double)`, `CloseSession(closingAmount: Double)`, `LoadHistory`.

---

**T-21** `feat(pos/cashregister): add CashRegisterViewModel`

- Expone `StateFlow<CashRegisterState>`.
- En `init`: carga `activeSession` y el historial.
- `onEvent(CashRegisterEvent)`: delega en `OpenCashRegisterUseCase`, `CloseCashRegisterUseCase` o `CashRegisterRepository.getAll()` según corresponda.

---

**T-22** `feat(pos/cashregister): add CashRegisterScreen`

- Si no hay sesión activa: muestra formulario de apertura con campo `openingAmount` y botón "Abrir Caja".
- Si hay sesión activa: muestra resumen del turno (hora de apertura, monto inicial, cantidad de ventas del turno) + formulario de cierre con campo `closingAmount` y botón "Cerrar Caja".
- Lista de sesiones anteriores con estado y montos.

---

**T-23** `feat(pos): integrate session guard into PosScreen`

- Agregar `SessionStatusBar.kt` en `presentation/pos/components/`: composable que muestra el estado de la caja activa y un botón para navegar a `CashRegisterScreen`.
- En `PosScreen`: integrar `SessionStatusBar` en el encabezado.
- Deshabilitar el botón "Cobrar" en `CartPanel` si `PosState.activeSession == null`; mostrar aviso contextual.

---

### Fase E — Tests

**T-24** `test: add SaleRepositoryFake and CashRegisterRepositoryFake`

Conforme al lineamiento: archivos propios, organizados por capa, sin sobreingeniería.

- `SaleRepositoryFake.kt`: implementa `SaleRepository`. Propiedades públicas mutables: `savedSale: Sale?`, `shouldThrowError: Boolean`.
- `CashRegisterRepositoryFake.kt`: implementa `CashRegisterRepository`. Propiedades: `activeSession: CashRegisterSession?`, `shouldThrowError: Boolean`, `closeCalled: Boolean`.

Si `InventoryRepositoryFake` no existe de Etapa 2, crearlo aquí con: `stockUpdated: Map<Int, Int>` y `shouldThrowError: Boolean`.

---

**T-25** `test: add ValidateCartStockUseCaseTest and CalculateCartTotalUseCaseTest`

`ValidateCartStockUseCaseTest`:
- Happy path: todo el stock disponible → `Result.success`.
- Un producto sin stock suficiente → `Result.failure`.
- Múltiples productos insuficientes → falla con todos identificados.

`CalculateCartTotalUseCaseTest`:
- Suma correcta con múltiples ítems.
- Carrito vacío → retorna `0.0`.

---

**T-26** `test: add CompleteSaleUseCaseTest`

- Happy path: `savedSale != null`, `InventoryRepositoryFake.stockUpdated` contiene las reducciones correctas para cada ítem.
- Validación de stock falla → use case retorna `Result.failure` y `savedSale == null` (sin efecto secundario).
- `SaleRepositoryFake.shouldThrowError = true` → use case retorna `Result.failure`.
- Usar `assertFailsWith` para los paths de error que lanzan excepciones.
- Verificar side effect con flag booleano: `InventoryRepositoryFake.updateStockCalled == true` en el happy path.

---

**T-27** `test: add OpenCashRegisterUseCaseTest and CloseCashRegisterUseCaseTest`

`OpenCashRegisterUseCaseTest`:
- Sin sesión activa → sesión creada y retornada.
- Con sesión activa ya existente → `Result.failure`.

`CloseCashRegisterUseCaseTest`:
- Con sesión activa → sesión cerrada; `CashRegisterRepositoryFake.closeCalled == true`.
- Sin sesión activa → `Result.failure`.

---

**T-28** `test: add PosViewModelTest`

Usar `UnconfinedTestDispatcher` para el dispatcher de corrutinas.

- `AddItem` → `cartItems` actualizado, `cartTotal` recalculado.
- `RemoveItem` → ítem eliminado, total actualizado.
- `CompleteSale` con `activeSession == null` → `saleCompleted` permanece `false`, `error` no nulo.
- `CompleteSale` con sesión activa y stock suficiente → `saleCompleted == true`, `cartItems` vacío.
- Error en `CompleteSaleUseCase` → `error` propagado al estado, carrito intacto.
- Ejercitar `onEvent(...)` explícitamente — no depender solo del bloque `init`.

---

**T-29** `test: add CashRegisterViewModelTest`

Usar `UnconfinedTestDispatcher`.

- `OpenSession` exitosa → `activeSession` no nulo en el estado.
- `OpenSession` con sesión ya abierta → `error` propagado.
- `CloseSession` exitosa → `activeSession == null` en el estado.
- `CloseSession` sin sesión activa → `error` propagado.

---

## 4. Archivos a crear / modificar

### Nuevos — Dominio
```
domain/entity/Sale.kt
domain/entity/SaleItem.kt
domain/entity/PaymentMethod.kt
domain/entity/CashRegisterSession.kt
domain/entity/SessionStatus.kt
domain/repository/SaleRepository.kt
domain/repository/CashRegisterRepository.kt
domain/usecase/cart/ValidateCartStockUseCase.kt
domain/usecase/cart/CalculateCartTotalUseCase.kt
domain/usecase/sale/CompleteSaleUseCase.kt
domain/usecase/sale/GetProductByBarcodeUseCase.kt   ← solo si no existe de Etapa 2
domain/usecase/cashregister/OpenCashRegisterUseCase.kt
domain/usecase/cashregister/CloseCashRegisterUseCase.kt
domain/usecase/cashregister/GetActiveSessionUseCase.kt
```

### Nuevos — Datos
```
data/local/SalesTable.kt
data/local/SaleItemsTable.kt
data/local/CashRegisterSessionsTable.kt
data/local/SaleLocalDataSource.kt
data/local/SaleLocalDataSourceImpl.kt
data/local/CashRegisterLocalDataSource.kt
data/local/CashRegisterLocalDataSourceImpl.kt
data/mapper/SaleMapper.kt
data/mapper/SaleItemMapper.kt
data/mapper/CashRegisterMapper.kt
data/repository/SaleRepositoryImpl.kt
data/repository/CashRegisterRepositoryImpl.kt
```

### Nuevos — Presentación
```
presentation/pos/PosScreen.kt
presentation/pos/PosViewModel.kt
presentation/pos/PosState.kt
presentation/pos/PosEvent.kt
presentation/pos/CartItem.kt
presentation/pos/components/ProductSearchPanel.kt
presentation/pos/components/CartPanel.kt
presentation/pos/components/PaymentDialog.kt
presentation/pos/components/SessionStatusBar.kt
presentation/pos/cashregister/CashRegisterScreen.kt
presentation/pos/cashregister/CashRegisterViewModel.kt
presentation/pos/cashregister/CashRegisterState.kt
presentation/pos/cashregister/CashRegisterEvent.kt
```

### Nuevos — Tests
```
test/fakes/SaleRepositoryFake.kt
test/fakes/CashRegisterRepositoryFake.kt
test/fakes/InventoryRepositoryFake.kt              ← solo si no existe de Etapa 2
test/domain/usecase/ValidateCartStockUseCaseTest.kt
test/domain/usecase/CalculateCartTotalUseCaseTest.kt
test/domain/usecase/CompleteSaleUseCaseTest.kt
test/domain/usecase/OpenCashRegisterUseCaseTest.kt
test/domain/usecase/CloseCashRegisterUseCaseTest.kt
test/presentation/pos/PosViewModelTest.kt
test/presentation/pos/cashregister/CashRegisterViewModelTest.kt
```

### Modificados
```
data/local/DatabaseFactory.kt        ← agregar SalesTable, SaleItemsTable, CashRegisterSessionsTable al SchemaUtils.create
di/AppModules.kt                     ← registrar todos los bindings nuevos de esta etapa
presentation/App.kt                  ← conectar PosScreen y CashRegisterScreen a la navegación existente
```

---

## 5. Criterios de Éxito

1. **Flujo end-to-end:** Abrir sesión de caja → buscar producto (barcode o nombre) → agregar al carrito → verificar recálculo de total → confirmar venta → comprobar en BD que el stock del producto se redujo en la cantidad vendida y que existe un registro en `SalesTable`.

2. **Guard de sesión:** El botón de cobro está deshabilitado y muestra aviso contextual cuando no hay sesión activa. No es posible despachar `CompleteSale` desde el ViewModel sin sesión activa.

3. **Cierre de caja:** Una sesión cerrada queda en estado `CLOSED` en la BD. Intentar abrir otra sin cerrar la anterior falla con error visible.

4. **Robustez transaccional:** Si `SaleRepositoryImpl.save` lanza una excepción (simulada via fake), `CompleteSaleUseCase` retorna `Result.failure`; el estado del carrito en el ViewModel permanece intacto.

5. **DIP verificable en tests:** `CompleteSaleUseCase`, `PosViewModel` y `CashRegisterViewModel` son instanciables en tests únicamente con fakes — sin dependencia alguna a clases concretas de la capa de datos.

6. **Tests verdes:** Todos los tests de la Fase E pasan. Cada use case tiene al menos un caso de error cubierto, y los side effects de escritura están verificados con flags booleanos.

7. **Sin código muerto:** Sin imports sin uso, sin funciones no referenciadas, sin comentarios que describan "qué" hace el código.

---

## 6. Checklist de Progreso

### Fase A — Dominio
- [X] T-01: Entities (Sale, SaleItem, PaymentMethod, CashRegisterSession, SessionStatus)
- [X] T-02: Interfaces (SaleRepository, CashRegisterRepository)
- [X] T-02A: Extend InventoryRepository (getProductById, updateStock)
- [X] T-03: ValidateCartStockUseCase
- [X] T-04: CalculateCartTotalUseCase
- [X] T-05: CompleteSaleUseCase
- [X] T-06: Reutilizar ScanProductUseCase de Etapa 2
- [X] T-07: Use cases de caja (Open, Close, GetActive)

### Fase B — Datos
- [X] T-08: Tablas + DatabaseFactory
- [X] T-09: SaleLocalDataSource + Impl
- [X] T-10: CashRegisterLocalDataSource + Impl
- [X] T-11: SaleMapper + SaleItemMapper
- [X] T-12: CashRegisterMapper
- [X] T-13: SaleRepositoryImpl
- [X] T-14: CashRegisterRepositoryImpl
- [X] T-15: AppModules.kt (DI)

### Fase C — Presentación 3A
- [ ] T-16: PosState, PosEvent, CartItem
- [ ] T-17: PosViewModel
- [ ] T-18: Componentes (ProductSearchPanel, CartPanel, PaymentDialog)
- [ ] T-19: PosScreen + navegación

### Fase D — Presentación 3B
- [ ] T-20: CashRegisterState, CashRegisterEvent
- [ ] T-21: CashRegisterViewModel
- [ ] T-22: CashRegisterScreen
- [ ] T-23: Session guard integrado en PosScreen

### Fase E — Tests
- [ ] T-24: Fakes (SaleRepositoryFake, CashRegisterRepositoryFake)
- [ ] T-25: ValidateCartStockUseCaseTest + CalculateCartTotalUseCaseTest
- [ ] T-26: CompleteSaleUseCaseTest
- [ ] T-27: OpenCashRegisterUseCaseTest + CloseCashRegisterUseCaseTest
- [ ] T-28: PosViewModelTest
- [ ] T-29: CashRegisterViewModelTest