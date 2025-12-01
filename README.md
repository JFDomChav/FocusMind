# FocusMind: Gestión de Tareas Basada en la Curva del Olvido

## Descripción del Proyecto

**FocusMind** es una herramienta diseñada para **optimizar el estudio y la gestión de tareas** basada en la influyente teoría de la **Curva del Olvido** de Hermann Ebbinghaus. Con FocusMind, los usuarios pueden registrar tareas y recibir recordatorios en intervalos crecientes (**Repetición Espaciada**) para reforzar el aprendizaje de manera eficiente.

El objetivo principal es **combatir la pérdida de memoria** programando repeticiones estratégicas, asegurando que el material de estudio se revise justo antes de que la retención empiece a decaer significativamente.

---

## Características Implementadas

### Funcionalidad Central (Repetición Espaciada)

* **Repetición Espaciada (Spaced Repetition):** Las tareas pasan del estado "Esperando" al estado "**¡LISTO AHORA!**" en intervalos definidos, siguiendo el patrón óptimo de la curva del olvido.
* **Contador de Repeticiones:** Seguimiento visual de la repetición actual de la tarea (Ej: **Repetición: X / Máx. Diario**).
* **Temporizador:** Muestra el tiempo restante (MM:SS) para que la tarea esté lista para el siguiente repaso.

### Interfaz de Usuario (UI)

* **Estado Visual:** El fondo del panel de la tarea cambia a un color **verde suave** cuando está lista para ser repasada.
* **Botón de Repaso:** Se activa solo cuando la tarea está lista, con un color de botón que indica acción.
* **Eliminación Segura:** Botón de "Eliminar" con cuadro de diálogo de confirmación para prevenir borrados accidentales.

### Notificaciones y Feedback

* **Notificación de Sonido:** Al cambiar el estado de la tarea a "lista para repasar", se reproduce un sonido para alertar al usuario.
* **Notificación del Sistema:** Cuando una tarea está lista, se envía una **notificación emergente nativa** al sistema operativo (utilizando `java.awt.SystemTray` y `TrayIcon`), asegurando que el usuario reciba el recordatorio incluso si la aplicación está en segundo plano.

---

## Tecnologías Utilizadas

* **Java:** Lenguaje principal de desarrollo.
* **Swing (Java AWT/Swing):** Utilizado para la construcción de la Interfaz Gráfica de Usuario (GUI).
* **`javax.sound.sampled`:** Manejo de la reproducción de audio para las alertas de tareas listas.
* **`java.awt.SystemTray`:** Para la integración de notificaciones a nivel de sistema operativo.
