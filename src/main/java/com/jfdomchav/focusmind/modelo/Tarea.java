package com.jfdomchav.focusmind.modelo;

import com.jfdomchav.focusmind.utilidades.Utils;
import java.io.Serializable;

/**
 * Clase que representa la información base de una Tarea de estudio.
 * Al crearse, se le asigna un ID único (GUID/UUID) usando la clase Utils.
 */
public class Tarea implements Serializable {
    private final String idUnico;
    private String nombre;
    private String descripcion;
    private static final long serialVersionUID = 1L; 

    /**
     * Constructor para crear una nueva Tarea.
     * Requiere el nombre y automáticamente genera un ID único.
     * @param nombre El nombre de la tarea (obligatorio).
     */
    public Tarea(String nombre) {
        // Se genera el ID único usando la clase estática Utils
        this.idUnico = Utils.generarIdUnico();
        this.nombre = nombre;
        // Inicializar la descripción como vacía por defecto
        this.descripcion = "";
    }

    // --- Métodos de acceso (Getters) ---

    public String getIdUnico() {
        return idUnico;
    }

    public String getNombre() {
        return nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    // --- Métodos de modificación (Setters) ---

    /**
     * Establece o actualiza el nombre de la tarea.
     * Se asegura que el nombre no sea nulo ni vacío.
     * @param nombre El nuevo nombre de la tarea.
     */
    public void setNombre(String nombre) {
        if (nombre != null && !nombre.trim().isEmpty()) {
            this.nombre = nombre;
        }
    }

    /**
     * Establece o actualiza la descripción de la tarea.
     * @param descripcion La nueva descripción de la tarea.
     */
    public void setDescripcion(String descripcion) {
        // La descripción puede ser una cadena vacía, pero se acepta
        if (descripcion != null) {
            this.descripcion = descripcion;
        }
    }
}