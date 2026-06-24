# Sistema de Gestión POS e Inventario

Un sistema completo de Punto de Venta (POS) y Gestión de Inventario de escritorio, desarrollado con **Kotlin** y **Jetpack Compose Desktop**.

## Arquitectura
El proyecto sigue los principios de **Clean Architecture** implementando el patrón **MVVM** (Model-View-ViewModel) y está organizado bajo una estructura estricta orientada a **Features** (Características).

Cada módulo está autocontenido e incluye sus propias capas:
- `domain`: Casos de uso (UseCases) y Entidades de negocio.
- `data`: Implementación de repositorios, DTOs, fuentes de datos locales (Base de Datos) y remotas (APIs).
- `presentation`: Componentes de interfaz de usuario (Jetpack Compose), manejo de Estado e Interacciones (UiState/Events) y ViewModels.

## Tecnologías Utilizadas
- **Lenguaje:** Kotlin 
- **UI Framework:** Jetpack Compose Desktop
- **Base de Datos / ORM:** SQLite + JetBrains Exposed
- **Inyección de Dependencias:** Koin
- **Cliente HTTP:** Ktor Client (consumo de APIs externas).
- **Programación Asíncrona:** Kotlin Coroutines & Flow.
- **Serialización:** Kotlinx Serialization (JSON).
- **Testing:** JUnit & Coroutines Test.

## Módulos y Funcionalidades (Features)

* **Dashboard:** Panel principal con resumen de métricas clave, alertas de bajo stock, alertas de productos próximos a vencer y widget de cotización del dólar en tiempo real.
* **Inventario:** Gestión de productos y stock. Integración con la API de *OpenFoodFacts* para buscar y autocompletar información del producto al escanear el código de barras. Soporta la definición de precios base en dólares (USD) y la conversión en tiempo real a moneda local (ARS).
* **Punto de Venta (POS):** Sistema de facturación. Maneja sesiones de caja (Apertura y Cierre / Arqueo de caja), carrito de compras interactivo, búsqueda de productos y finalización de ventas con múltiples métodos de pago (Efectivo, Tarjeta, Transferencia).
* **Reportes y Estadísticas:** Análisis de ventas, historial de transacciones, panel de márgenes de ganancia, rotación de productos y gráficos de ingresos.
* **Cotización (Exchange Rate):** Integración con *DolarAPI* para obtener el tipo de cambio financiero actualizado del día.
* **Configuración:** Ajustes generales del local, umbrales de alerta del sistema, tema claro/oscuro y utilidades para exportar la base de datos a formato CSV.

## Estructura de Directorios

```text
src/main/kotlin/com/app/
│
├── common/        # Utilidades, Mappers y componentes UI compartidos (Theme, Sidebar)
├── dashboard/     # Feature: Métricas iniciales y alertas
├── di/            # Configuración de inyección de dependencias (AppModules)
├── exchangerate/  # Feature: Consulta de cotización (DolarAPI)
├── inventory/     # Feature: CRUD de productos y conexión con OpenFoodFacts
├── pos/           # Feature: Interfaz de ventas, carrito y sesiones de caja
├── reports/       # Feature: Historial, estadísticas y gráficos de métricas
└── settings/      # Feature: Configuración de la app y Exportación a CSV
```

## Testing
La aplicación contiene una extensa suite de **Tests Unitarios**.
Los casos de uso y los ViewModels se prueban aislando completamente la capa de datos. En lugar de frameworks de mocking tradicionales, se implementan *Fakes* en memoria (ej. `InventoryRepositoryFake`, `SaleRepositoryFake`), lo que garantiza que las pruebas sean deterministas, ultra rápidas y no dependan de la infraestructura o la base de datos de SQLite.

## Ejecución del Proyecto

El proyecto se puede ejecutar a través de Gradle utilizando el plugin de Compose Desktop:

```bash
# Compilar y ejecutar la aplicación de escritorio
./gradlew run
```

O abriendo el proyecto en **IntelliJ IDEA** y ejecutando directamente la clase principal `com.app.presentation.MainKt`.
