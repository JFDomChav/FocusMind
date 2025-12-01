package com.jfdomchav.focusmind.controlador;

/**
 * Interfaz que deben implementar todas las clases que necesiten
 * ser notificadas por el HiloDeTiempo en cada actualización de segundo.
 */
public interface TiempoListener {

    /**
     * Método que se ejecuta en cada tic de segundo del HiloDeTiempo.
     * @param segundoActual El número total de segundos transcurridos desde el inicio del hilo.
     */
    void onUpdate(long segundoActual);
}