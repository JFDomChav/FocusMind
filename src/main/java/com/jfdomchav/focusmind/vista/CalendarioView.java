package com.jfdomchav.focusmind.vista;

// Importaciones requeridas para el modelo y el controlador
import com.jfdomchav.focusmind.modelo.Tarea;
import com.jfdomchav.focusmind.modelo.TareaDeCalendario; 
import com.jfdomchav.focusmind.controlador.Calendario; // Nueva importación para acceder al gestor

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.List;
import java.util.ArrayList;

/**
 * Vista contenedora principal para las tareas programadas en el calendario
 * de repaso a largo plazo. Utiliza un JScrollPane para manejar listas largas.
 */
public class CalendarioView extends JPanel {

    private static final Logger LOGGER = Logger.getLogger(CalendarioView.class.getName());

    // Panel interno que contendrá los TareaCalendarioPanel, usando BoxLayout verticalmente.
    private final JPanel tasksPanel; 
    
    // Lista para simular el modelo de datos de las tareas mostradas
    private final List<TareaDeCalendario> tareasEnCalendario = new ArrayList<>(); 
    
    // Colores para la estética
    private static final Color COLOR_FONDO_PRINCIPAL = new Color(240, 240, 245); // Gris azulado muy claro

    /**
     * Constructor para CalendarioView. Configura el JScrollPane y el layout.
     */
    public CalendarioView() {
        // Configuración del panel principal (CalendarioView)
        setLayout(new BorderLayout());
        setBackground(COLOR_FONDO_PRINCIPAL);

        // 1. Inicializar el panel interno para las tareas
        tasksPanel = new JPanel();
        // Usamos BoxLayout vertical para que los paneles se apilen
        tasksPanel.setLayout(new BoxLayout(tasksPanel, BoxLayout.Y_AXIS));
        tasksPanel.setBackground(Color.WHITE); // Fondo blanco para la lista de tareas en sí
        
        // 2. Crear un JScrollPane y envolver el panel de tareas
        JScrollPane scrollPane = new JScrollPane(tasksPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder()); // Eliminar el borde por defecto
        scrollPane.getViewport().setBackground(Color.WHITE); 
        
        // 3. Añadir un margen superior/inferior/lateral al panel de tareas
        tasksPanel.setBorder(new EmptyBorder(15, 15, 15, 15));
        
        // 4. Añadir el JScrollPane al panel principal
        add(scrollPane, BorderLayout.CENTER);

        // Opcional: Agregar un título en la parte superior si se desea un mejor encabezado
        JLabel headerLabel = new JLabel("<html><h1 style='color:#333366; margin-left:10px;'>Tareas Programadas</h1></html>");
        headerLabel.setBorder(new EmptyBorder(10, 10, 10, 10));
        add(headerLabel, BorderLayout.NORTH);
        
        LOGGER.log(Level.INFO, "CalendarioView inicializada.");
    }
    
    /**
     * Agrega una nueva Tarea al calendario y crea su representación visual (TareaCalendarioPanel).
     * @param tareaCalendario La TareaDeCalendario a agregar (con toda su lógica de repetición).
     */
    public void agregarTareaCalendario(TareaDeCalendario tareaCalendario) {
        
        // El callback Runnable que será ejecutado por TareaCalendarioPanel al solicitarse la eliminación.
        // Lógica de VISTA: La vista llama al Controlador (Calendario.eliminarTarea)
        Runnable eliminacionCallback = () -> onTareaEliminada(tareaCalendario);
        
        // Paso 1: Crear el componente visual, usando el nuevo constructor
        TareaCalendarioPanel panel = new TareaCalendarioPanel(tareaCalendario, eliminacionCallback);
        
        // Añadir al modelo de vista local
        tareasEnCalendario.add(tareaCalendario);
        
        // Paso 2: Añadir el panel a la vista y agregar un espacio vertical
        tasksPanel.add(panel);
        tasksPanel.add(Box.createVerticalStrut(10)); // Separación entre tareas
        
        // Paso 3: Actualizar la vista
        tasksPanel.revalidate();
        tasksPanel.repaint();

        LOGGER.log(Level.INFO, "Tarea '{0}' agregada a la vista de calendario.", tareaCalendario.getNombre());
    }
    
    /**
     * Maneja la notificación de que una tarea ha sido marcada para eliminación por su panel.
     * En una aplicación real, este método llama al Controlador/Servicio para eliminar la tarea y persistir el cambio.
     * @param tareaEliminada La instancia de TareaDeCalendario marcada para eliminación.
     */
    private void onTareaEliminada(TareaDeCalendario tareaEliminada) {
        // 1. Eliminar la tarea del modelo de datos de la vista (simulación de limpieza local)
        tareasEnCalendario.removeIf(t -> t == tareaEliminada); 
        
        // 2. Llamar al Gestor de Calendario estático para eliminar la tarea de la persistencia.
        // El Gestor Calendario.eliminarTarea se encargará de buscar el ID único y persistir el cambio.
        Calendario.eliminarTarea(tareaEliminada.getIdUnico());
        
        LOGGER.log(Level.INFO, "Controlador notificado: Tarea '{0}' eliminada de la persistencia.", tareaEliminada.getNombre());
    }
    
    /**
     * Carga todas las tareas persistentes del calendario y las añade a la vista.
     */
    public void cargarTareasIniciales() {
        LOGGER.log(Level.INFO, "Iniciando la carga de tareas persistentes del Calendario.");
        
        // 1. Limpiar la vista anterior antes de cargar
        tasksPanel.removeAll();
        tareasEnCalendario.clear(); // Limpiar el modelo de vista interno
        
        // 2. Obtener la lista de tareas del controlador/gestor estático (Calendario)
        List<TareaDeCalendario> tareasCargadas = Calendario.getTareas();
        
        if (tareasCargadas.isEmpty()) {
            LOGGER.log(Level.INFO, "No se encontraron tareas en el Calendario.");
        }
        
        // 3. Iterar y agregar cada tarea a la vista
        for (TareaDeCalendario tarea : tareasCargadas) {
            // Se usa el método que crea el panel de la vista y añade la tarea a la lista interna.
            agregarTareaCalendario(tarea); 
        }
        
        // 4. Actualizar la vista para reflejar los cambios
        tasksPanel.revalidate();
        tasksPanel.repaint();
        
        LOGGER.log(Level.INFO, "{0} tareas cargadas y mostradas en CalendarioView.", tareasCargadas.size());
    }

    /**
     * Método para eliminar un panel específico de la vista (ej. si la tarea llega a su finalización).
     * @param panel El TareaCalendarioPanel a eliminar.
     */
    public void eliminarTareaCalendarioPanel(TareaCalendarioPanel panel) {
        // La lógica de eliminación de la vista se maneja principalmente desde el panel individual, 
        // pero este método se mantiene para compatibilidad si fuera necesario eliminar paneles de otra forma.
        
        Component[] components = tasksPanel.getComponents();
        int index = -1;

        // Encontrar el índice del panel
        for (int i = 0; i < components.length; i++) {
            if (components[i] == panel) {
                index = i;
                break;
            }
        }
        
        if (index != -1) {
            tasksPanel.remove(panel);
            // Intentar eliminar el separador vertical (Strut) que le sigue
            if (index < tasksPanel.getComponentCount() && tasksPanel.getComponent(index) instanceof Box.Filler) {
                tasksPanel.remove(index);
            }

            tasksPanel.revalidate();
            tasksPanel.repaint();
            LOGGER.log(Level.INFO, "TareaCalendarioPanel eliminado de la vista de calendario (Ruta alternativa).");
        }
    }
}