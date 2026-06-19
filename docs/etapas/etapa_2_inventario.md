# Etapa 2 — Gestión de Inventario

> **Precondiciones:** Etapa 1 completada (Persistencia y Repositorios configurados).
> **Objetivo:** Construir la pantalla principal de inventario, permitiendo ver el catálogo y agregar nuevos productos utilizando la API de OpenFoodFacts para carga automática mediante código de barras.

---

## Tareas

### T2.1 — Cliente API OpenFoodFacts (External Data)
**Archivos a crear:**
- `src/main/kotlin/com/app/data/external/OpenFoodFactsClient.kt`
- `src/main/kotlin/com/app/data/external/dto/OffResponse.kt`

**Detalle:**
- Implementar un cliente HTTP usando Ktor.
- Crear los DTOs básicos para parsear la respuesta JSON de `https://world.openfoodfacts.org/api/v2/product/{barcode}.json`.
- Exponer una función `fetchProductByBarcode(barcode: String): Product?` que mapee la respuesta a la entidad de dominio pura.

**Criterio de éxito:** El cliente puede hacer una petición real a la API y retornar una entidad pura `Product` poblada.

- [ ] Completada

---

### T2.2 — Lógica de Negocio (Use Case)
**Archivos a crear:**
- `src/main/kotlin/com/app/domain/usecase/ScanProductUseCase.kt`

**Detalle:**
- Aplicando nuestra regla de no sobre-ingeniar (Ponytail), crearemos casos de uso solo donde haya lógica de orquestación. Para traer la lista usaremos el repositorio directo, pero para el escaneo sí necesitamos orquestar:
- `ScanProductUseCase` recibe el código de barras y ejecuta este flujo:
  1. Busca en la BD local usando `InventoryRepository`.
  2. Si existe, lo devuelve.
  3. Si no existe localmente, llama a `OpenFoodFactsClient`.
  4. Si la API lo encuentra, lo guarda en `InventoryRepository` (para futura caché offline) y lo devuelve.

**Criterio de éxito:** La lógica de "fallback" (local -> remoto -> local) está centralizada y testeable, sin acoplarse a la interfaz gráfica.

- [ ] Completada

---

### T2.3 — ViewModel de Inventario (Presentation Layer)
**Archivos a crear:**
- `src/main/kotlin/com/app/presentation/inventory/InventoryViewModel.kt`

**Detalle:**
- Manejar el estado reactivo de la pantalla (`InventoryState`).
- El estado contendrá: `isLoading` (boolean), `products` (List<Product>), y manejo de errores.
- Proveer funciones: `loadAll()` y `scanBarcode(barcode: String)` que corran bajo corrutinas (`viewModelScope` o equivalentes en desktop).

**Criterio de éxito:** El ViewModel orquesta las llamadas asíncronas y expone estados limpios sin dependencias a librerías de UI de Compose.

- [ ] Completada

---

### T2.4 — Interfaz Gráfica Premium (UI Compose)
**Archivos a modificar:**
- `src/main/kotlin/com/app/presentation/inventory/InventoryScreen.kt`

**Detalle:**
- Reemplazar el actual "placeholder" por una UI real y **visualmente deslumbrante** (siguiendo nuestra convención de "Rich Aesthetics").
- Elementos requeridos:
  - Barra superior con un campo de texto moderno para escribir o simular el escaneo de un código de barras.
  - Botón de "Escanear / Agregar".
  - Lista en formato de tabla o "cards" mostrando los productos actuales, nombre, precio (USD) y stock.
- Conectar todo el estado provisto por el `InventoryViewModel`.

**Criterio de éxito:** La pantalla se ve profesional y moderna, y responde a las interacciones agregando productos desde la API al listado.

- [ ] Completada

---

### T2.5 — Inyección de Dependencias y Verificación
**Archivos a modificar:**
- `src/main/kotlin/com/app/di/AppModules.kt`

**Detalle:**
- Registrar `OpenFoodFactsClient` (`single`).
- Registrar `ScanProductUseCase` (`factory`).
- Registrar `InventoryViewModel` (`factory`).
- Ejecutar la aplicación, verificar que la inyección sea correcta, que la búsqueda de código de barras llame a la API y la lista se actualice.

**Criterio de éxito:** El ciclo completo (UI -> ViewModel -> UseCase -> API/BD) funciona perfectamente y sin errores de dependencias. Commit de cierre de etapa.

- [ ] Completada
