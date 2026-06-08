# Plan de Implementación - Etapa 1 (Estructura Base MVVM)

Este plan define el primer commit funcional para el nuevo proyecto. El objetivo es puramente estructural: asentar los cimientos de la arquitectura Clean MVVM mediante la creación de los directorios y paquetes fundamentales, sin implementar lógicas complejas de negocio todavía. Esta estructura previene violaciones a la separación de responsabilidades a futuro.

## 1. Estructura de Directorios

Se deberá crear la siguiente jerarquía de paquetes en el código fuente principal (por ejemplo, dentro de `src/main/kotlin/com/tuorganizacion/app` o equivalente, dependiendo de la plataforma de destino):

```text
📦 src/main/kotlin/com/org/app
 ┣ 📂 data
 ┃ ┣ 📂 local           # Contratos e implementaciones de fuentes locales (BBDD, Caché)
 ┃ ┣ 📂 external        # Contratos e implementaciones de fuentes externas (APIs, Red)
 ┃ ┣ 📂 repository      # Implementaciones de las interfaces de repositorios del Dominio
 ┃ ┗ 📂 mapper          # Funciones o clases para mapear de modelos Data a Entities
 ┣ 📂 di
 ┃ ┗ 📜 AppModules.kt   # Configuración base de Inyección de Dependencias
 ┣ 📂 domain
 ┃ ┣ 📂 entity          # Modelos de datos puros (Value Objects sin reglas duras)
 ┃ ┣ 📂 repository      # Interfaces (Contratos) de los repositorios
 ┃ ┗ 📂 usecase         # Interfaces e implementaciones de los Casos de Uso
 ┗ 📂 presentation
   ┣ 📂 feature_a       # Paquete para una pantalla específica (ej. home)
   ┣ 📂 feature_b       # Paquete para otra pantalla específica (ej. detail)
   ┣ 📂 utils           # Composables comunes, extensiones de UI, themes
   ┣ 📜 App.kt          # Composable raíz de la aplicación
   ┗ 📜 main.kt         # Punto de entrada principal y configuración de Navegación
```

## 2. Tareas a ejecutar para el Primer Commit

1. **Creación del esqueleto de paquetes:** Generar todas las carpetas mencionadas en la estructura de arriba dentro del módulo principal (ej. `desktopMain/kotlin/` o `app/src/main/kotlin/`).
2. **Configuración inicial de control de versiones (`.gitignore`):** 
   - Es crítico asegurar que las carpetas del entorno local (`.idea/`, `build/`, `.gradle/`, archivos de dependencias de SO) estén ignoradas desde el primer instante. Nunca commitear configuraciones locales.
3. **Poblado inicial "Dummy" (Opcional):** 
   - Para evitar que Git omita las carpetas vacías y poder subir la estructura completa, se pueden agregar archivos `.gitkeep` en los paquetes que aún no tengan código.
   - Crear un archivo `App.kt` básico en `presentation`.
   - Crear un archivo `main.kt` básico como punto de entrada.
4. **Verificación Estructural:** 
   - Comprobar que no haya errores de sintaxis en los archivos creados.
   - Validar que el proyecto compila correctamente, aunque su funcionalidad sea nula en este punto.

---

**Criterios de Éxito de la Etapa 1:**
Al finalizar esta etapa, el agente o equipo de desarrollo podrá visualizar la segregación de capas y comenzar a colocar las entidades, casos de uso, repositorios y vistas en el lugar correcto. Se evitará así el *anti-patrón* de mezclar lógicas de red con lógica de negocio o de presentación en las siguientes etapas.
