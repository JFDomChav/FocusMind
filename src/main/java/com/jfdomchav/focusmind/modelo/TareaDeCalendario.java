package com.jfdomchav.focusmind.modelo;

import com.jfdomchav.focusmind.controlador.Configuracion; // Importar la clase de configuración
import java.io.Serializable;
import java.util.Date;
import java.util.Calendar; // Importar Calendar para manipulación de fechas

/**
 * Clase que NO extiende Tarea. En su lugar, utiliza composición,
 * guardando una instancia de Tarea como variable. Contiene los metadatos
 * de repetición a largo plazo (días/meses).
 * Implementa Serializable para permitir la persistencia en el fichero del Calendario.
 */
public class TareaDeCalendario implements Serializable {
	
	// SerialVersionUID para asegurar la compatibilidad de la serialización
	private static final long serialVersionUID = 2L; 

	// Relación de composición: Tarea base guardada como variable
	private Tarea tareaBase;
	
	// Fecha del próximo repaso (basado en el día)
	private Date proximoRepaso; 
	
	// Días que deben pasar hasta el siguiente repaso (intervalo)
	private int intervaloDias; 	 
	
	// Nivel de éxito en el repaso (0-100), usado para ajustar el intervalo.
	private int nivelDeExito; 	 	
	
	// Contador de cuántas veces ha sido repasada esta tarea en el calendario
	private int repeticionesEnCalendario;
	
	// Bandera para marcar la tarea para su eliminación del calendario (NUEVO)
	private boolean marcadaParaEliminar;

	/**
	 * Constructor que promociona una Tarea base al Calendario.
	 * @param tareaBase La Tarea de corto plazo que ha cumplido sus repeticiones diarias.
	 */
	public TareaDeCalendario(Tarea tareaBase) {
		// Guarda la instancia de la Tarea base (Composición)
		this.tareaBase = tareaBase;	
		
		// Inicialización de metadatos de calendario
		this.proximoRepaso = new Date(); // Fecha de hoy al inicio
		this.intervaloDias = 1; 	 	 	
		this.nivelDeExito = 0; 	 	 	
		this.repeticionesEnCalendario = 0;
		this.marcadaParaEliminar = false; // Inicialización del nuevo campo
	}

	// --- Métodos de acceso a Tarea Base (Delegación) ---
	
	public String getIdUnico() {
		return tareaBase.getIdUnico();
	}
	
	public String getNombre() {
		return tareaBase.getNombre();
	}

	public String getDescripcion() {
		return tareaBase.getDescripcion();
	}

	public void setNombre(String nombre) {
		tareaBase.setNombre(nombre);
	}

	public void setDescripcion(String descripcion) {
		tareaBase.setDescripcion(descripcion);
	}
	
	// Permite obtener la Tarea base completa si es necesario
	public Tarea getTareaBase() {
		return tareaBase;
	}


	// --- Métodos de acceso a Metadatos de Calendario ---
	
	public Date getProximoRepaso() {
		return proximoRepaso;
	}

	public int getIntervaloDias() {
		return intervaloDias;
	}

	public int getNivelDeExito() {
		return nivelDeExito;
	}

	public int getRepeticionesEnCalendario() {
		return repeticionesEnCalendario;
	}
	
	/**
	 * Verifica si la tarea está marcada para ser eliminada.
	 * @return true si la tarea debe ser eliminada.
	 */
	public boolean isMarcadaParaEliminar() {
		return marcadaParaEliminar;
	}

	// --- Métodos de modificación de Metadatos ---
	
	public void setProximoRepaso(Date proximoRepaso) {
		this.proximoRepaso = proximoRepaso;
	}

	public void setIntervaloDias(int intervaloDias) {
		this.intervaloDias = intervaloDias;
	}

	public void setNivelDeExito(int nivelDeExito) {
		this.nivelDeExito = nivelDeExito;
	}

	public void setRepeticionesEnCalendario(int repeticionesEnCalendario) {
		this.repeticionesEnCalendario = repeticionesEnCalendario;
	}
	
	/**
	 * Marca la tarea para su eliminación del Calendario.
	 * Este método es llamado por la vista/controlador antes de que la tarea sea
	 * eliminada de la persistencia.
	 */
	public void marcarParaEliminar() {
		this.marcadaParaEliminar = true;
	}
	
	/**
	 * Verifica si la tarea ha alcanzado el número máximo de repeticiones
	 * definidas en la configuración.
	 * @return true si la tarea ha completado su ciclo de repaso, false en caso contrario.
	 */
	public boolean haCompletadoCiclo() {
		return this.repeticionesEnCalendario >= Configuracion.getMaxRepeticionesCalendario();
	}
    
    /**
     * Calcula la próxima fecha de repaso sumando el intervalo actual (en días)
     * a la fecha de próximo repaso actual.
     * @return La nueva fecha de repaso calculada.
     */
    private Date calcularNuevaFechaDeRepaso() {
        Calendar cal = Calendar.getInstance();
        // Establecer la fecha de inicio al próximo repaso actual
        cal.setTime(this.proximoRepaso); 
        
        // Sumar los días del intervalo
        cal.add(Calendar.DAY_OF_YEAR, this.intervaloDias);
        
        return cal.getTime();
    }
	
	/**
	 * Actualiza la tarea después de un repaso exitoso.	
	 * Incrementa el contador de repeticiones en calendario y calcula el nuevo
     * intervalo y la próxima fecha de repaso.
	 */
	public void actualizarRepasoExitoso() {
		this.repeticionesEnCalendario++;
        
        // 1. Lógica de cálculo del nuevo intervalo (se implementaría aquí la complejidad)
        // Ejemplo simple: Duplicar el intervalo para cada éxito
        this.intervaloDias = Math.max(1, this.intervaloDias * 2);
        
        // 2. Calcular y establecer la nueva fecha de repaso
        this.proximoRepaso = calcularNuevaFechaDeRepaso();
	}
}