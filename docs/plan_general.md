# Plan General — Sistema de Control de Stock

> **Proyecto:** Sistema de control de stock para negocios y almacenes  
> **Arquitectura:** Clean MVVM (Compose Desktop + Kotlin)  
> **Lineamientos:** Ver `docs/consideraciones_lineamientos.md`

---

## Visión General

El sistema se construye en **7 etapas progresivas**. Cada etapa tiene su propio plan detallado en `docs/etapas/` que se genera al momento de comenzar a trabajarla. Las etapas están ordenadas por dependencia: cada una construye sobre la anterior, pero los módulos *dentro* de cada etapa son lo más independientes posible.

---

## Mapa de Etapas

| # | Etapa | Estado | Plan Detallado | Descripción |
|---|-------|--------|----------------|-------------|
| 0 | Cimientos y Shell de Navegación | ✅ Completada | `docs/etapas/etapa_0_cimientos.md` | Build system, dependencias, tema visual, shell de la app con sidebar y paneles vacíos, configuración de BD |
| 1 | Modelo de Datos y Persistencia | 🔄 En progreso | `docs/etapas/etapa_1_persistencia.md` | Entities, tablas de BD, repository interfaces + implementaciones CRUD base |
| 2 | Gestión de Inventario | 🔲 Pendiente | _Se genera al iniciar_ | Catálogo, Categorías, Ingreso con escáner (OpenFoodFacts), Vencimientos |
| 3 | Punto de Venta (POS) | 🔲 Pendiente | _Se genera al iniciar_ | Venta activa, carrito, impacto transaccional de stock, cierre de caja |
| 4 | Dashboard | 🔲 Pendiente | _Se genera al iniciar_ | Métricas, alertas de bajo stock / vencimientos, actividad reciente |
| 5 | Reportes y Estadísticas | 🔲 Pendiente | _Se genera al iniciar_ | Historial de transacciones, gráficos de rotación, márgenes, ingresos |
| 6 | Configuración del Sistema | 🔲 Pendiente | _Se genera al iniciar_ | Datos del local, usuarios/roles, integraciones API, backup/export |

> **Leyenda:** 🔲 Pendiente · 🔄 En progreso · ✅ Completada

---

## Orden y Justificación de las Etapas

### Etapa 0 — Cimientos y Shell de Navegación
**¿Por qué primero?** Sin build system, sin navegación, sin BD y sin tema, ningún módulo puede funcionar. Este es el "cambio grande inicial" que habilita todo el trabajo posterior. Se crean TODOS los paneles como composables vacíos para que la navegación esté completa desde el día uno.

**Entregable:** App compilable con sidebar funcional, 5 secciones navegables (vacías), BD conectada (sin tablas), tema visual definido.

### Etapa 1 — Modelo de Datos y Persistencia
**¿Por qué segundo?** Todos los módulos (inventario, POS, reportes) operan sobre los mismos datos. Definir las entidades, las tablas y los repositorios CRUD base antes de tocar UI evita refactorizaciones masivas.

**Entregable:** Todas las entities del dominio, tablas SQL creadas, repositorios con operaciones CRUD básicas testeadas.

### Etapa 2 — Gestión de Inventario
**¿Por qué tercero?** Es el núcleo del sistema. Sin productos cargados, no hay nada que vender, ni métricas que mostrar, ni reportes que generar. Se subdivide en:
- **2A:** Catálogo (listado, búsqueda, paginación, edición, eliminación)
- **2B:** Categorías / Rubros (ABM, márgenes predeterminados)
- **2C:** Ingreso con Escáner (integración OpenFoodFacts API + carga manual fallback)
- **2D:** Control de Vencimientos (vista dedicada, ordenamiento temporal)

**Entregable:** CRUD completo de productos y categorías, integración con API, vista de vencimientos.

### Etapa 3 — Punto de Venta (POS)
**¿Por qué cuarto?** Depende de que exista inventario para poder registrar ventas.
- **3A:** Venta Activa (escaneo, carrito, total dinámico, reducción transaccional de stock)
- **3B:** Cierre de Caja (flujo de efectivo inicial/final por turno)

**Entregable:** Flujo de venta completo end-to-end, con impacto en stock y registro de transacciones.

### Etapa 4 — Dashboard
**¿Por qué quinto?** Necesita datos reales (productos, ventas, movimientos) para mostrar métricas significativas.

**Entregable:** Panel con métricas en tiempo real, alertas de bajo stock y vencimientos próximos, feed de actividad reciente.

### Etapa 5 — Reportes y Estadísticas
**¿Por qué sexto?** Requiere historial de transacciones acumulado de las etapas anteriores.

**Entregable:** Historial inmutable de movimientos, gráficos de rotación, márgenes y tendencias mensuales.

### Etapa 6 — Configuración del Sistema
**¿Por qué último?** Son ajustes transversales que no bloquean funcionalidad core. Se pueden ir agregando gradualmente.

**Entregable:** Configuración del local, gestión de usuarios/roles, backup/export (CSV/Excel), configuración de integraciones.

---

## Stack Tecnológico Propuesto

| Componente | Tecnología | Justificación |
|------------|------------|---------------|
| UI | Compose Desktop (Jetpack Compose) | Ya definido en la estructura actual |
| Lenguaje | Kotlin | Ya definido |
| DI | Koin | Ya configurado en `AppModules.kt` |
| Base de Datos | SQLite + Exposed (ORM de JetBrains) | Liviano, embebido, sin servidor externo, ideal para desktop |
| HTTP Client | Ktor Client | Nativo Kotlin, coroutines-first, ideal para OpenFoodFacts API |
| Navegación | Manual con estado en ViewModel | Compose Desktop no tiene Navigation Component, se usa sealed class + state |
| Gráficos | Compose Charts (o alternativa ligera) | Para las estadísticas de la Etapa 5 |
| Export | Apache POI (Excel) / opencsv (CSV) | Para backup/export en Etapa 6 |

---

## Estructura de Paquetes Final (Proyección)

```text
📦 src/main/kotlin/com/app
 ┣ 📂 data
 ┃ ┣ 📂 local              ← DAOs / Data Sources locales (Exposed)
 ┃ ┣ 📂 external            ← Clientes API (OpenFoodFacts via Ktor)
 ┃ ┣ 📂 repository          ← Implementaciones de repositorios
 ┃ ┗ 📂 mapper              ← Mappers Data → Entity
 ┣ 📂 di
 ┃ ┗ 📜 AppModules.kt       ← Módulos Koin por capa
 ┣ 📂 domain
 ┃ ┣ 📂 entity              ← Product, Category, Sale, Transaction, etc.
 ┃ ┣ 📂 repository          ← Interfaces de repositorios
 ┃ ┗ 📂 usecase             ← Casos de uso (lógica de negocio)
 ┗ 📂 presentation
   ┣ 📂 dashboard           ← Panel de inicio
   ┣ 📂 inventory           ← Gestión de inventario (catálogo, ingreso, categorías, vencimientos)
   ┣ 📂 pos                 ← Punto de venta (venta activa, cierre de caja)
   ┣ 📂 reports             ← Reportes y estadísticas
   ┣ 📂 settings            ← Configuración del sistema
   ┣ 📂 utils               ← Composables comunes, tema, extensiones
   ┣ 📜 App.kt              ← Composable raíz (shell + navegación)
   ┗ 📜 main.kt             ← Punto de entrada
```

---

## Convenciones para los Planes de Etapa

Cada plan detallado de etapa (`docs/etapas/etapa_N_xxx.md`) debe contener:

1. **Objetivo:** Qué se logra al completar la etapa.
2. **Precondiciones:** Qué etapas deben estar completadas antes.
3. **Tareas:** Lista ordenada de tareas atómicas (cada una = un commit potencial).
4. **Archivos a crear/modificar:** Lista explícita de archivos nuevos y existentes impactados.
5. **Criterios de éxito:** Cómo verificar que la etapa está completa.
6. **Checklist de progreso:** Checkboxes que se marcan al completar cada tarea.

---

## Reglas de Progresión

1. **No saltar etapas.** Cada etapa asume que la anterior está completa.
2. **Los planes detallados se generan al comenzar cada etapa**, no antes. Esto evita que se desactualicen.
3. **Commits atómicos.** Cada tarea dentro de una etapa debe poder commitearse de forma independiente.
4. **Los lineamientos de `consideraciones_lineamientos.md` aplican siempre.** Ningún plan de etapa puede contradecirlos.
