package com.jfdomchav.focusmind.controlador;

import com.jfdomchav.focusmind.modelo.Tarea;
import com.jfdomchav.focusmind.modelo.TareaDeCalendario;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Clase Gestora de Calendario. Se encarga de la gestión (CRUD) de las
 * TareaDeCalendario a largo plazo, incluyendo la persistencia en un fichero
 * binario (serialización de objetos) definido en la Configuración.
 * Es una clase estática (singleton de funcionalidad) con acceso global a las tareas
 * persistentes.
 */
public class Calendario {

    private static final Logger LOGGER = Logger.getLogger(Calendario.class.getName());
    
    // Lista de tareas cargadas en memoria.
    private static List<TareaDeCalendario> tareas;
    
    /**
     * Constructor privado para evitar la instanciación de la clase estática
     */
    private Calendario() {}

    /**
     * Inicializa el calendario cargando las tareas desde el fichero.
     * Debe llamarse al inicio de la aplicación.
     */
    public static void inicializar() {
        if (tareas == null) {
            tareas = cargarTareas();
            LOGGER.log(Level.INFO, "Calendario inicializado. {0} tareas cargadas.", tareas.size());
        }
    }

    /**
     * Carga todas las tareas serializadas desde el fichero.
     * @return Una lista de TareaDeCalendario. Si el archivo no existe o falla, devuelve una lista vacía.
     */
    private static List<TareaDeCalendario> cargarTareas() {
        String nombreFichero = Configuracion.getNombreFicheroCalendario();
        File file = new File(nombreFichero);
        
        if (!file.exists()) {
            LOGGER.info("Fichero de calendario no encontrado. Creando lista vacía.");
            return new ArrayList<>();
        }

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            // Se asume que el fichero guarda la lista de tareas completa
            Object objetoLeido = ois.readObject();
            if (objetoLeido instanceof List) {
                // Se realiza un casting seguro y se devuelve la lista
                return (List<TareaDeCalendario>) objetoLeido;
            } else {
                LOGGER.warning("El contenido del fichero no es una lista de TareaDeCalendario. Se devuelve lista vacía.");
                return new ArrayList<>();
            }
            
        } catch (EOFException e) {
            LOGGER.log(Level.SEVERE, "Error al cargar tareas del fichero " + nombreFichero +": "+e.getCause(), e);
            return new ArrayList<>();
        }
        catch (IOException | ClassNotFoundException e) {
            LOGGER.log(Level.SEVERE, "Error al cargar tareas del fichero " + nombreFichero, e);
            return new ArrayList<>();
        }
        
    }

    /**
     * Guarda la lista actual de tareas en memoria en el fichero de persistencia.
     */
    private static void guardarTareas() {
        String nombreFichero = Configuracion.getNombreFicheroCalendario();
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(nombreFichero))) {
            oos.writeObject(tareas);
            LOGGER.log(Level.INFO, "Tareas guardadas exitosamente en {0}. Total: {1}", 
                       new Object[]{nombreFichero, tareas.size()});
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error al guardar tareas en el fichero " + nombreFichero, e);
        }
    }
    
    // --- Métodos de Gestión (CRUD) ---

    /**
     * Agrega una nueva tarea de repaso a largo plazo.
     * Se usa para promocionar una Tarea base desde el sistema de repaso diario.
     * @param tarea La instancia de Tarea a promocionar.
     */
    public static void agregarTareaACalendario(Tarea tarea) {
        if (tareas == null) inicializar();
        
        // Crear la instancia de TareaDeCalendario usando la composición
        TareaDeCalendario nuevaTarea = new TareaDeCalendario(tarea);
        tareas.add(nuevaTarea);
        guardarTareas();
        
        LOGGER.log(Level.INFO, "Tarea '{0}' promocionada y guardada en Calendario.", tarea.getNombre());
    }
    
    /**
     * Actualiza la información de una tarea existente en el calendario.
     * @param tareaActualizada La TareaDeCalendario con la información modificada.
     * @return true si la tarea fue encontrada y actualizada, false en caso contrario.
     */
    public static boolean actualizarTarea(TareaDeCalendario tareaActualizada) {
        if (tareas == null) inicializar();
        
        for (int i = 0; i < tareas.size(); i++) {
            // Se usa el ID de la Tarea base para buscar
            if (tareas.get(i).getIdUnico().equals(tareaActualizada.getIdUnico())) {
                tareas.set(i, tareaActualizada); // Reemplazar la tarea antigua
                guardarTareas();
                LOGGER.log(Level.INFO, "Tarea '{0}' actualizada en Calendario.", tareaActualizada.getNombre());
                return true;
            }
        }
        LOGGER.log(Level.WARNING, "No se encontró la tarea con ID {0} para actualizar.", tareaActualizada.getIdUnico());
        return false;
    }
    
    /**
     * Elimina una tarea del calendario usando su ID.
     * @param idUnico El ID único de la tarea a eliminar.
     * @return true si la tarea fue eliminada, false si no se encontró.
     */
    public static boolean eliminarTarea(String idUnico) {
        if (tareas == null) inicializar();
        
        // Usamos removeIf para encontrar y eliminar la tarea basada en su ID único
        boolean eliminado = tareas.removeIf(t -> t.getIdUnico().equals(idUnico));
        if (eliminado) {
            guardarTareas();
            LOGGER.log(Level.INFO, "Tarea con ID {0} eliminada del Calendario.", idUnico);
        } else {
            LOGGER.log(Level.WARNING, "No se encontró la tarea con ID {0} para eliminar.", idUnico);
        }
        return eliminado;
    }

    /**
     * Obtiene todas las tareas actualmente en el calendario.
     * @return Una lista inmutable de TareaDeCalendario.
     */
    public static List<TareaDeCalendario> getTareas() {
        if (tareas == null) inicializar();
        // Devolver una copia para evitar modificación externa de la lista interna.
        return new ArrayList<>(tareas);
    }
}