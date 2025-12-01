package com.jfdomchav.focusmind.modelo;

import com.jfdomchav.focusmind.controlador.Calendario;
import com.jfdomchav.focusmind.controlador.Configuracion;
import com.jfdomchav.focusmind.controlador.HiloDeTiempo;
import com.jfdomchav.focusmind.controlador.HiloDeTiempo.OyenteDeTiempo; // Importación del OyenteDeTiempo del HiloDeTiempo
import com.jfdomchav.focusmind.vista.TareaUI;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Clase que gestiona el proceso de repetición a corto plazo (diario) para una Tarea.
 * Implementa la interfaz OyenteDeTiempo del HiloDeTiempo para recibir actualizaciones.
 * La interfaz gráfica (TareaUI) se establece a través de un setter después de la construcción.
 */
public class TareaDeRepaso implements OyenteDeTiempo { 
    
    private static final Logger LOGGER = Logger.getLogger(TareaDeRepaso.class.getName());
    
    // Enumeración para el estado de la tarea
    public enum Estado {
        LISTA_PARA_REPASAR, 
        NO_LISTA_PARA_REPASAR
    }
    
    // Constante para el tiempo inicial de repetición (30 segundos). 
    // Nota: Este valor podría depender de una KEY_CONFIGURACION futura.
    private static final int TIEMPO_INICIAL_SEGUNDOS = 30;

    private final Tarea tareaBase;
    private TareaUI tareaUI; // Ya no es final, se inicializa con el setter
    
    private int repeticionesActuales;
    // Tiempo absoluto (segundos transcurridos) en el que la tarea debe estar lista para repasar
    private long tiempoRepasoEnSegundos;
    private Estado estado;

    /**
     * Constructor. Inicializa la tarea de repaso y la registra en el HiloDeTiempo.
     * La TareaUI se debe establecer usando setTareaUI() después.
     * @param tareaBase La instancia de Tarea con la información base.
     */
    public TareaDeRepaso(Tarea tareaBase) { // Se elimina TareaUI del constructor
        this.tareaBase = tareaBase;
        this.tareaUI = null; // Inicialmente nulo
        this.repeticionesActuales = 0;
        
        // El estado inicial es "lista" para que el primer repaso esté disponible
        this.estado = Estado.LISTA_PARA_REPASAR; 
        
        // Se inicializa el tiempo de repaso en 0 para que esté lista inmediatamente
        this.tiempoRepasoEnSegundos = 0; 
        
        // Registrarse para escuchar las actualizaciones del tiempo
        HiloDeTiempo.getInstancia().agregarOyente(this);
        LOGGER.log(Level.INFO, "Tarea de repaso creada: {0}", tareaBase.getNombre());
    }
    
    /**
     * Establece la interfaz gráfica asociada a esta tarea.
     * @param tareaUI La interfaz gráfica (TareaPanel) para notificaciones.
     */
    public void setTareaUI(TareaUI tareaUI) {
        this.tareaUI = tareaUI;
        LOGGER.log(Level.INFO, "UI asignada a TareaDeRepaso: {0}", tareaBase.getNombre());
    }

    // --- Lógica del Olvido ---

    /**
     * Calcula el nuevo tiempo de espera en segundos basándose en la repetición actual.
     * Se usa una progresión geométrica simple: 30s, 60s, 120s, 240s, ...
     * @param repeticiones El número de repeticiones (1, 2, 3, ...).
     * @return El tiempo de espera en segundos.
     */
    private static long calcularTiempoEsperaSegundos(int repeticiones) {
        // La primera repetición (n=1) espera 30s después de ser marcada.
        // Fórmula: 30 * 2^(n-1)
        if (repeticiones <= 0) {
            return TIEMPO_INICIAL_SEGUNDOS;
        }
        
        // Usamos Math.pow y luego Math.round para evitar desbordamiento y truncar correctamente
        double tiempoEspera = TIEMPO_INICIAL_SEGUNDOS * Math.pow(2, repeticiones - 1);
        return Math.round(tiempoEspera);
    }

    /**
     * Incrementa la repetición y recalcula el próximo tiempo de repaso.
     * Si se alcanza el máximo diario (definido en Configuracion), promociona la tarea al calendario.
     */
    public void aumentarRepeticion() {
        if (estado == Estado.NO_LISTA_PARA_REPASAR) {
            LOGGER.warning("Intento de repasar tarea no lista: " + tareaBase.getNombre());
            return;
        }
        
        repeticionesActuales++;
        
        // ** USO DE CONFIGURACIÓN **
        if (repeticionesActuales >= Configuracion.getMaxRepeticionesDiarias()) {
            // Máximo de repeticiones diario alcanzado
            
            // 1. Promocionar al calendario a largo plazo
            Calendario.agregarTareaACalendario(tareaBase);
            
            // 2. Eliminar la tarea de esta gestión a corto plazo
            eliminar();
            
            // 3. Notificar a la UI para su eliminación visual
            if (tareaUI != null) {
                tareaUI.onDeleted();
            }
            
            LOGGER.log(Level.INFO, "Tarea '{0}' completó repeticiones diarias y fue promocionada.", tareaBase.getNombre());
            
        } else {
            // Repetición diaria normal
            long tiempoEspera = calcularTiempoEsperaSegundos(repeticionesActuales);
            this.tiempoRepasoEnSegundos = HiloDeTiempo.getInstancia().getSegundosTranscurridos() + tiempoEspera;
            this.estado = Estado.NO_LISTA_PARA_REPASAR;
            
            LOGGER.log(Level.INFO, "Repaso {0} de {1} completado para '{2}'. Próximo en {3} segundos (segundo absoluto: {4}).", 
                        new Object[]{repeticionesActuales, Configuracion.getMaxRepeticionesDiarias(), tareaBase.getNombre(), tiempoEspera, tiempoRepasoEnSegundos});
            
            // Notificar a la UI que la tarea ha entrado en estado de espera
            if (tareaUI != null) {
                tareaUI.onWaiting(tiempoEspera);
            }
        }
    }
    
    /**
     * Elimina esta instancia de la lista de oyentes del HiloDeTiempo.
     */
    public void eliminar() {
        HiloDeTiempo.getInstancia().eliminarOyente(this);
        LOGGER.log(Level.INFO, "Tarea de repaso eliminada de HiloDeTiempo: {0}", tareaBase.getNombre());
    }

    // --- Implementación de OyenteDeTiempo ---

    /**
     * Se llama cada segundo por el HiloDeTiempo.
     * Verifica si el tiempo de repaso ha llegado y actualiza el contador de tiempo restante en la UI.
     * @param segundoActual El segundo actual global.
     */
    @Override
    public void enSegundoPasado(long segundoActual) { 
        long tiempoRestante = this.tiempoRepasoEnSegundos - segundoActual;
        
        // Actualizar la UI con el tiempo restante
        if (tareaUI != null && estado == Estado.NO_LISTA_PARA_REPASAR) {
            // Se asume que TareaUI tiene un método para actualizar el contador
            tareaUI.onTimeUpdate(tiempoRestante); 
        }

        // Solo verificar si la tarea no está lista para repasar
        if (estado == Estado.NO_LISTA_PARA_REPASAR) {
            if (tiempoRestante <= 0) {
                this.estado = Estado.LISTA_PARA_REPASAR;
                if (tareaUI != null) {
                    tareaUI.onReady(); // Notificar a la interfaz
                }
                LOGGER.log(Level.FINE, "Tarea '{0}' lista para repasar en el segundo: {1}", 
                            new Object[]{tareaBase.getNombre(), segundoActual});
            }
        }
    }

    // --- Getters para TareaUI ---

    public Tarea getTareaBase() {
        return tareaBase;
    }

    public String getNombre() {
        return tareaBase.getNombre();
    }

    public String getDescripcion() {
        return tareaBase.getDescripcion();
    }

    public int getRepeticionesActuales() {
        return repeticionesActuales;
    }

    public long getTiempoRepasoEnSegundos() {
        return tiempoRepasoEnSegundos;
    }

    public Estado getEstado() {
        return estado;
    }
}