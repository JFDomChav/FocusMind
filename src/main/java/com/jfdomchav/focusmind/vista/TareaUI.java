package com.jfdomchav.focusmind.vista;

/**
 * Interfaz placeholder que representa la parte de la interfaz gráfica
 * de una tarea de repaso. TareaDeRepaso la usa para notificar cambios.
 */
public interface TareaUI {

    /**
     * Se llama cuando el estado de la tareaDeRepaso cambia a lista para repasar.
     * La UI debe activar el botón de repaso y cambiar la imagen de estado.
     */
    void onReady();

    /**
     * Se llama cuando la tarea de repaso ha alcanzado el máximo de repeticiones
     * diarias y debe ser eliminada de la vista de tareas activas.
     */
    void onDeleted();
    
    void onWaiting(long tiempo);
    
    void onTimeUpdate(long timepo);
}