package com.jfdomchav.focusmind.controlador;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Hilo en segundo plano que actúa como el reloj central de la aplicación.
 * Implementa el patrón Singleton para asegurar una única instancia de tiempo.
 * Se encarga de contar los segundos transcurridos desde el inicio de la aplicación
 * y notificar a los oyentes registrados cada segundo.
 */
public class HiloDeTiempo implements Runnable {

    private static final Logger LOGGER = Logger.getLogger(HiloDeTiempo.class.getName());

    // Patrón Singleton
    private static HiloDeTiempo instancia;

    // Contador de tiempo. Volatile para asegurar la visibilidad entre hilos.
    private volatile long segundosTranscurridos = 0;
    
    // Lista de oyentes (listeners) que serán notificados cada segundo.
    private final List<OyenteDeTiempo> oyentes = new ArrayList<>();
    
    // Bandera para controlar la ejecución del hilo
    private volatile boolean running = true;

    /**
     * Interfaz funcional interna para los componentes que desean escuchar el tiempo.
     * Se define aquí para mantener el acoplamiento solo dentro de HiloDeTiempo.
     */
    @FunctionalInterface
    public static interface OyenteDeTiempo {
        void enSegundoPasado(long segundosActuales);
    }

    /**
     * Constructor privado. Inicia el hilo de tiempo.
     */
    private HiloDeTiempo() {
        Thread hilo = new Thread(this, "HiloDeTiempo");
        hilo.setDaemon(true); // Permite que la JVM se cierre si solo quedan hilos demonio
        hilo.start();
        LOGGER.log(Level.INFO, "HiloDeTiempo iniciado.");
    }

    /**
     * Obtiene la única instancia de HiloDeTiempo (Singleton).
     * @return La instancia única de HiloDeTiempo.
     */
    public static HiloDeTiempo getInstancia() {
        if (instancia == null) {
            synchronized (HiloDeTiempo.class) {
                if (instancia == null) {
                    instancia = new HiloDeTiempo();
                }
            }
        }
        return instancia;
    }

    /**
     * Bucle principal de ejecución del hilo. Incrementa el contador cada segundo.
     */
    @Override
    public void run() {
        while (running) {
            try {
                // Dormir por 1000 milisegundos (1 segundo)
                Thread.sleep(1000); 
                
                segundosTranscurridos++;
                
                // Notificar a todos los oyentes
                notificarOyentes();

            } catch (InterruptedException e) {
                LOGGER.log(Level.WARNING, "HiloDeTiempo interrumpido.", e);
                // Restaurar el estado de interrupción
                Thread.currentThread().interrupt(); 
                running = false;
            }
        }
        LOGGER.log(Level.INFO, "HiloDeTiempo detenido.");
    }
    
    /**
     * Detiene la ejecución del hilo de forma segura.
     */
    public void detener() {
        running = false;
    }

    /**
     * Agrega un oyente (listener) que será notificado cada segundo.
     * @param oyente La instancia de OyenteDeTiempo.
     */
    public void agregarOyente(OyenteDeTiempo oyente) {
        synchronized (oyentes) {
            oyentes.add(oyente);
        }
    }

    /**
     * Elimina un oyente de la lista.
     * @param oyente La instancia de OyenteDeTiempo a eliminar.
     */
    public void eliminarOyente(OyenteDeTiempo oyente) {
        synchronized (oyentes) {
            oyentes.remove(oyente);
        }
    }

    /**
     * Notifica a todos los oyentes registrados el segundo actual.
     */
    private void notificarOyentes() {
        // Usamos una copia para evitar ConcurrentModificationException
        List<OyenteDeTiempo> oyentesCopia;
        synchronized (oyentes) {
            oyentesCopia = new ArrayList<>(oyentes);
        }
        
        long segundosActuales = segundosTranscurridos;
        for (OyenteDeTiempo oyente : oyentesCopia) {
            oyente.enSegundoPasado(segundosActuales);
        }
    }

    /**
     * Obtiene el total de segundos transcurridos desde el inicio del hilo.
     * @return El número de segundos transcurridos.
     */
    public long getSegundosTranscurridos() {
        return segundosTranscurridos;
    }
}