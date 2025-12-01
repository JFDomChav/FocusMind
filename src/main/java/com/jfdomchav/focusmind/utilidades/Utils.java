package com.jfdomchav.focusmind.utilidades;

import java.util.UUID;

/**
 * Clase estática de utilidad para generar identificadores únicos.
 * La instancia de Tarea la utilizará para asignar un ID único a cada tarea.
 */
public final class Utils {

    // Constructor privado para prevenir la instanciación de esta clase estática
    private Utils() {
        // Esta clase no debe ser instanciada
    }

    /**
     * Genera un identificador único universal (UUID) como String.
     * Este método se utilizará para asignar IDs únicos a las instancias de Tarea.
     * @return Una cadena de texto que representa el ID único.
     */
    public static String generarIdUnico() {
        return UUID.randomUUID().toString();
    }
}