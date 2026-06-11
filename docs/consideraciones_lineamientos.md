# Lineamientos y Consideraciones Arquitectónicas (Clean MVVM)

Este documento recopila las mejores prácticas, principios, convenciones y correcciones asimiladas a lo largo de etapas previas, con el fin de establecer un estándar de calidad sólido para el desarrollo del nuevo proyecto desde cero.

## 1. Arquitectura (Clean MVVM)
La aplicación debe seguir una arquitectura en capas estricta: `Presentation -> Domain <- Data`.

- **Presentation (Presentación):**
  - Contiene únicamente la lógica de la interfaz de usuario (Pantallas, ViewModels, Composables).
  - Depende exclusivamente de la capa de **Domain**.
  - **Estructura:** Un paquete por pantalla (ej. `home`, `detail`), un paquete `utils` para componentes reutilizables (ej. `CommonComposables`), y los puntos de entrada (`App`, `main`, Navegación) en la raíz de presentación.
- **Domain (Dominio):**
  - Contiene la lógica de negocio pura.
  - No debe tener dependencias a librerías externas ni a otras capas de la aplicación.
  - **Componentes:**
    - **Entities:** Clases de datos puras (Value Objects). No deben contener reglas de negocio duras ni evaluación de condiciones en sí mismas; solo transportan datos.
    - **Use Cases:** Encapsulan la lógica de negocio (ej. ordenamientos, lógicas de umbrales). Deben contar con interfaces o abstracciones para facilitar los tests (no usar clases concretas sin interfaz).
    - **Repository Interfaces:** Definen los contratos que la capa de datos deberá cumplir.
- **Data (Datos):**
  - Provee los datos a la aplicación, abstrayendo el origen (local, remoto, combinaciones).
  - Contiene las **implementaciones** de las interfaces de repositorio definidas en Dominio.
  - Los repositorios deben ser **completamente agnósticos**: no deben conocer detalles específicos de las fuentes de datos (ej. no mencionar "TMDB", no armar URLs explícitas). Simplemente orquestan los Data Sources.
  - **Mappers:** Cada fuente de datos externa/local debe tener su propio Mapper para traducir desde su modelo nativo (Data) al modelo de Dominio (Entity). Estas traducciones no deben filtrarse al repositorio ni a los brokers.
- **DI (Inyección de Dependencias):**
  - Se encarga de instanciar y proveer todos los objetos y dependencias de la aplicación en el arranque.

## 2. Principios SOLID y Patrones de Diseño
- **ISP (Segregación de Interfaces):** Las interfaces deben ser lo más específicas posible. Si una fuente externa solo provee detalles y no listas, no la obligues a implementar un método de lista. Divide las interfaces (ej. separar `MoviesListExternalSource` de `MovieDetailExternalSource`).
- **DIP (Inversión de Dependencias):** Los repositorios y mediadores (brokers) dependen de abstracciones (interfaces), no de implementaciones concretas. Esto permite testearlos de forma aislada inyectando *fakes*.
- **Patrón Broker (Mediador):** Si se usan múltiples fuentes para un mismo fin (múltiples APIs que traen lo mismo), se debe crear un `Broker` que reciba dichas dependencias por interfaz, combine o decida qué resultados devolver, y se inyecte de manera transparente en el repositorio (implementando la misma interfaz del Data Source original). Este broker no debe tener lógicas de armado de URIs (Fuga de Conocimiento Específico), eso pertenece al Data Source o Mapper.

## 3. Convenciones de Nomenclatura y Estructura
- **Interfaces e Implementaciones:** Toda implementación debe llevar el sufijo `Impl`. Ejemplo: Interfaz `LocalDataSource`, implementación `LocalDataSourceImpl`.
- **Nombres de Parámetros de Inyección:** Los parámetros deben coincidir con el nombre de la interfaz inyectada (en lowerCamelCase). Ejemplo: `val localDataSource: LocalDataSource`. No pasar prefijos como `local` sueltos.
- **Ubicación de Contratos:** La interfaz (contrato) debe vivir en el mismo nivel jerárquico que agrupa a sus implementaciones (para mantener la cohesión).
- **Archivos de Configuración IDE:** Archivos como los de la carpeta `.idea/` **jamás** deben ser commiteados. Debe existir un archivo `.gitignore` estricto desde el día cero.

## 4. Prácticas de Testing Unitario
- **Un Archivo de Test por Clase:** Seguir estrictamente la convención `NombreClaseTest.kt`. Nunca agrupar pruebas de múltiples implementaciones o interfaces en un solo archivo de test.
- **Manejo de Fakes:** 
  - Deben extraerse a archivos propios y organizarse por capa (ej. `data/RepositoryFakes.kt`). 
  - ¡No dejarlos como clases privadas dentro de los archivos de testing!
  - Deben ser simples y directos, sin sobreingeniería (exponer propiedades públicas mutables como `result`, `shouldThrowError`).
- **Verificación de Efectos Secundarios (Side Effects):** Para funciones asíncronas que retornan `Unit` (ej. guardado en caché), se debe verificar explícitamente que la invocación ocurrió, usando *flags booleanos* dentro de los fakes (ej. `saveCalled = true`).
- **Cobertura de la API Pública:** Los tests en la capa de Presentación (ViewModels) deben ejercer explícitamente los métodos públicos (ej. invocar `viewModel.onEvent(...)`). Nunca depender solo del bloque `init` para disparar acciones a testear.
- **Corrutinas en Tests de ViewModels:** Utilizar `UnconfinedTestDispatcher` para los ViewModels y evitar la complejidad y fragilidad de sentencias manuales como `advanceUntilIdle` y `runCurrent`.
- **Manejo de Excepciones en Tests:** Para validar errores, usar el bloque idiomático `assertFailsWith<Exception> { ... }` en lugar de envolturas `try/catch` con `assertTrue(false)`.
- **Tests de Combinación (Brokers):** Al testear la combinación de resultados de diferentes orígenes, las aserciones (`assertEquals`) deben validar fehacientemente los campos que provienen de *ambas* fuentes para garantizar que la combinación ocurrió.

## 5. Buenas Prácticas de Código (Clean Code)
- **Cero Comentarios de "Qué":** Se penalizan los comentarios redundantes que describen *qué* está haciendo el código. El código debe ser autodescriptivo mediante buenos nombres de variables y métodos. Los comentarios solo se admiten para explicar *por qué* se tomó una decisión técnica puntual.
- **Código Muerto:** Todo código declarado que no sea invocado o utilizado (funciones sin referencias, imports sin uso) es considerado un error grave y debe borrarse.
- **Concurrencia Defensiva:** Al orquestar múltiples peticiones a servicios, deben usarse peticiones en paralelo (bloques `async` de corrutinas) limitadas por tiempos máximos de espera (`withTimeout`) para evitar que la caída o lentitud de un servicio bloquee todo el flujo.
- **Cero Manejo de Errores Redundante:** Extraer las lógicas repetitivas de captura de excepciones (`try/catch` de corrutinas) hacia un helper centralizado (como un método `safeCall`). No envolver en un `try/catch` llamadas que internamente ya manejan y capturan sus errores.
- **Evitar Shadowing de Variables (Warnings):** Prestar atención a los warnings del IDE. Nombrar parámetros de funciones internas de forma diferente a los miembros de la clase inyectados para evitar ocultamiento visual y errores lógicos.
- **Atomics Commits:** En el proceso de PR, repartir los commits entre el equipo. Estos deben ser pequeños, atómicos y focalizados.
