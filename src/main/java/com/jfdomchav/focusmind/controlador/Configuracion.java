package com.jfdomchav.focusmind.controlador;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Clase que gestiona la configuración de la aplicación, guardándola y cargándola
 * desde un fichero de texto ("config.dat"). Utiliza el formato: |ID:VALOR.
 * Las configuraciones se guardan en variables estáticas para ser accesibles
 * globalmente por el resto de la aplicación.
 */
public class Configuracion {
    private static final Logger LOGGER = Logger.getLogger(Configuracion.class.getName());

    // Fichero de configuración
    private static final String NOMBRE_FICHERO = "config.dat";
    private static final char SEPARADOR_1 = '|';
    private static final char SEPARADOR_2 = ':';

    // IDs de Configuración (clave interna)
    public static final String KEY_FICHERO_CALENDARIO = "001";
    public static final String KEY_MAX_REPETICIONES_DIARIAS = "002";
    public static final String KEY_MAX_REPETICIONES_CALENDARIO = "003";

    // Valores por defecto
    private static final String DEFAULT_FICHERO_CALENDARIO = "calendario.dat";
    private static final int DEFAULT_MAX_REPETICIONES_DIARIAS = 5;
    private static final int DEFAULT_MAX_REPETICIONES_CALENDARIO = 10;

    // Variables accesibles (valores de configuración)
    private static String nombreFicheroCalendario = DEFAULT_FICHERO_CALENDARIO;
    private static int maxRepeticionesDiarias = DEFAULT_MAX_REPETICIONES_DIARIAS;
    private static int maxRepeticionesCalendario = DEFAULT_MAX_REPETICIONES_CALENDARIO;
    
    // Mapa interno para el CRUD
    private static final Map<String, String> configuraciones = new HashMap<>();

    /**
     * Constructor privado para prevenir la instanciación.
     * La clase se usa de forma estática.
     */
    private Configuracion() {
        // Clase de utilidad estática
    }

    // --- Getters para acceder a la configuración ---

    public static String getNombreFicheroCalendario() {
        return nombreFicheroCalendario;
    }

    public static int getMaxRepeticionesDiarias() {
        return maxRepeticionesDiarias;
    }

    public static int getMaxRepeticionesCalendario() {
        return maxRepeticionesCalendario;
    }

    // --- Métodos de CRUD y Gestión ---

    /**
     * Carga todas las configuraciones desde el fichero. Si el fichero no existe,
     * guarda los valores por defecto y sale.
     */
    public static void cargarConfiguracion() {
        File file = new File(NOMBRE_FICHERO);
        if (!file.exists()) {
            // Si el archivo no existe, creamos el archivo con la configuración por defecto.
            guardarConfiguracion();
            return;
        }

        configuraciones.clear();
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                // El formato esperado es: |ID:VALOR
                if (linea.length() > 3 && linea.charAt(0) == SEPARADOR_1) {
                    int idx = linea.indexOf(SEPARADOR_2);
                    if (idx > 1) {
                        String id = linea.substring(1, idx);
                        String valor = linea.substring(idx + 1);
                        configuraciones.put(id, valor);
                    }
                }
            }
            // Aplicar los valores cargados a las variables estáticas
            aplicarConfiguracion();

        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error al cargar el fichero de configuración.", e);
        }
    }

    /**
     * Aplica los valores cargados del mapa interno a las variables estáticas accesibles,
     * manejando las conversiones de tipo y los valores por defecto en caso de error.
     */
    private static void aplicarConfiguracion() {
        nombreFicheroCalendario = configuraciones.getOrDefault(KEY_FICHERO_CALENDARIO, DEFAULT_FICHERO_CALENDARIO);
        
        try {
            maxRepeticionesDiarias = Integer.parseInt(configuraciones.getOrDefault(
                    KEY_MAX_REPETICIONES_DIARIAS, String.valueOf(DEFAULT_MAX_REPETICIONES_DIARIAS)));
        } catch (NumberFormatException e) {
            LOGGER.warning("Valor no numérico en KEY_MAX_REPETICIONES_DIARIAS. Usando valor por defecto.");
            maxRepeticionesDiarias = DEFAULT_MAX_REPETICIONES_DIARIAS;
        }

        try {
            maxRepeticionesCalendario = Integer.parseInt(configuraciones.getOrDefault(
                    KEY_MAX_REPETICIONES_CALENDARIO, String.valueOf(DEFAULT_MAX_REPETICIONES_CALENDARIO)));
        } catch (NumberFormatException e) {
            LOGGER.warning("Valor no numérico en KEY_MAX_REPETICIONES_CALENDARIO. Usando valor por defecto.");
            maxRepeticionesCalendario = DEFAULT_MAX_REPETICIONES_CALENDARIO;
        }
    }

    /**
     * Guarda la configuración actual de las variables estáticas en el fichero.
     */
    public static void guardarConfiguracion() {
        // 1. Actualizar el mapa interno con los valores actuales de las variables estáticas
        configuraciones.put(KEY_FICHERO_CALENDARIO, nombreFicheroCalendario);
        configuraciones.put(KEY_MAX_REPETICIONES_DIARIAS, String.valueOf(maxRepeticionesDiarias));
        configuraciones.put(KEY_MAX_REPETICIONES_CALENDARIO, String.valueOf(maxRepeticionesCalendario));
        
        // 2. Escribir el mapa al fichero
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(NOMBRE_FICHERO))) {
            for (Map.Entry<String, String> entry : configuraciones.entrySet()) {
                // Formato: |ID:VALOR
                bw.write(SEPARADOR_1 + entry.getKey() + SEPARADOR_2 + entry.getValue());
                bw.newLine();
            }
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error al guardar el fichero de configuración.", e);
        }
    }

    /**
     * Método para cambiar un valor de configuración y guardarlo inmediatamente.
     * @param key El ID de la configuración a cambiar (e.g., KEY_FICHERO_CALENDARIO).
     * @param value El nuevo valor como String.
     */
    public static void setConfiguracion(String key, String value) {
        switch (key) {
            case KEY_FICHERO_CALENDARIO:
                nombreFicheroCalendario = value;
                break;
            case KEY_MAX_REPETICIONES_DIARIAS:
                try {
                    maxRepeticionesDiarias = Integer.parseInt(value);
                } catch (NumberFormatException e) {
                    LOGGER.warning("Valor no numérico para repeticiones diarias: " + value);
                }
                break;
            case KEY_MAX_REPETICIONES_CALENDARIO:
                try {
                    maxRepeticionesCalendario = Integer.parseInt(value);
                } catch (NumberFormatException e) {
                    LOGGER.warning("Valor no numérico para repeticiones en calendario: " + value);
                }
                break;
            default:
                LOGGER.warning("ID de configuración desconocido: " + key);
                return;
        }
        // Guardar la configuración después de la actualización exitosa
        guardarConfiguracion();
    }
}