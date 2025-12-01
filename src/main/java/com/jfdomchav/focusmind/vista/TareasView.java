package com.jfdomchav.focusmind.vista;

import com.jfdomchav.focusmind.modelo.Tarea;
import com.jfdomchav.focusmind.modelo.TareaDeRepaso;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Vista contenedora principal para las tareas de repaso diarias (TareaPanel).
 * Utiliza un JScrollPane para permitir la visualización de una lista larga de tareas.
 * También gestiona la adición y eliminación visual de los paneles de tareas.
 */
public class TareasView extends JPanel {

    private static final Logger LOGGER = Logger.getLogger(TareasView.class.getName());

    // Panel interno que contendrá los TareaPanel, usando BoxLayout verticalmente.
    private final JPanel tasksPanel; 
    
    // Componentes para la entrada de nueva tarea
    private JTextField nuevaTareaField;
    private JButton agregarTareaButton;
    
    // Colores para la estética
    private static final Color COLOR_FONDO_PRINCIPAL = new Color(235, 235, 235); // Gris claro, para el fondo general
    private static final Color COLOR_BOTON_AGREGAR = new Color(60, 179, 113); // Verde medio

    /**
     * Constructor para TareasView. Configura el JScrollPane y el layout.
     */
    public TareasView() {
        // Configuración del panel principal (TareasView)
        setLayout(new BorderLayout());
        setBackground(COLOR_FONDO_PRINCIPAL);

        // 1. Crear el panel de control superior para agregar tareas
        add(createHeaderPanel(), BorderLayout.NORTH);

        // 2. Inicializar el panel interno para las tareas (donde se añadirán TareaPanel)
        tasksPanel = new JPanel();
        // Usamos BoxLayout vertical para que los paneles se apilen de arriba hacia abajo
        tasksPanel.setLayout(new BoxLayout(tasksPanel, BoxLayout.Y_AXIS));
        tasksPanel.setBackground(Color.WHITE); // Fondo blanco para la lista de tareas en sí
        
        // 3. Crear un JScrollPane y envolver el panel de tareas
        JScrollPane scrollPane = new JScrollPane(tasksPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder()); // Eliminar el borde feo por defecto
        scrollPane.getViewport().setBackground(Color.WHITE); // Asegurar que el fondo del viewport también sea blanco
        
        // 4. Añadir un pequeño margen superior/inferior al panel de tareas
        tasksPanel.setBorder(new EmptyBorder(10, 10, 10, 10)); // Añadido margen lateral
        
        // 5. Añadir el JScrollPane al panel principal
        add(scrollPane, BorderLayout.CENTER);
        
        LOGGER.log(Level.INFO, "TareasView inicializada.");
    }
    
    /**
     * Crea y configura el panel superior para la entrada de nuevas tareas.
     * @return JPanel con el campo de texto y el botón.
     */
    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout(10, 10));
        headerPanel.setBorder(new EmptyBorder(10, 15, 10, 15));
        headerPanel.setBackground(COLOR_FONDO_PRINCIPAL.brighter());

        // Campo de texto para el nombre de la nueva tarea
        nuevaTareaField = new JTextField("Ingresa aquí una nueva tarea...");
        nuevaTareaField.setFont(new Font("SansSerif", Font.PLAIN, 14));
        // Limpiar el texto al obtener el foco
        nuevaTareaField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                if (nuevaTareaField.getText().equals("Ingresa aquí una nueva tarea...")) {
                    nuevaTareaField.setText("");
                }
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                if (nuevaTareaField.getText().isEmpty()) {
                    nuevaTareaField.setText("Ingresa aquí una nueva tarea...");
                }
            }
        });

        // Botón para agregar
        agregarTareaButton = new JButton("Agregar");
        agregarTareaButton.setFont(new Font("SansSerif", Font.BOLD, 14));
        agregarTareaButton.setForeground(Color.WHITE);
        agregarTareaButton.setBackground(COLOR_BOTON_AGREGAR);
        agregarTareaButton.setFocusPainted(false);
        agregarTareaButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Acción del botón
        agregarTareaButton.addActionListener(e -> onAgregarTareaClicked());
        
        headerPanel.add(nuevaTareaField, BorderLayout.CENTER);
        headerPanel.add(agregarTareaButton, BorderLayout.EAST);
        
        return headerPanel;
    }
    
    /**
     * Maneja la lógica al hacer clic en el botón Agregar Tarea.
     */
    private void onAgregarTareaClicked() {
        String nombreTarea = nuevaTareaField.getText().trim();
        
        if (nombreTarea.isEmpty() || nombreTarea.equals("Ingresa aquí una nueva tarea...")) {
            JOptionPane.showMessageDialog(this, 
                "Por favor, ingresa un nombre para la tarea.", 
                "Error", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Crear e incrustar la nueva tarea
        Tarea nuevaTarea = new Tarea(nombreTarea);
        agregarTarea(nuevaTarea);
        
        // Limpiar el campo
        nuevaTareaField.setText("");
    }

    /**
     * Agrega una nueva Tarea al sistema de repaso diario y crea su representación visual (TareaPanel).
     * @param tarea La Tarea base (Tarea) a agregar.
     */
    public void agregarTarea(Tarea tarea) {
        
        // Paso 1: Crear la lógica de la tarea de repaso.
        TareaDeRepaso tareaLogica = new TareaDeRepaso(tarea);
        
        // Paso 2: Crear el componente visual (TareaPanel)
        // Se asume la existencia de TareaPanel y TareaDeRepaso.
        // Como TareaPanel requiere 'this' (TareasView), se debe crear la clase TareaPanel
        // para que esta compilación sea exitosa.
        TareaPanel panel = new TareaPanel(tareaLogica, this);
        
        // Paso 3: Establecer la TareaUI en la TareaDeRepaso usando el setter.
        tareaLogica.setTareaUI(panel);
        
        // Paso 4: Añadir el panel a la vista y asegurar que el separador vertical (Strut) 
        // esté justo después para espaciado consistente.
        tasksPanel.add(panel);
        tasksPanel.add(Box.createVerticalStrut(10)); // Separación entre tareas
        
        tasksPanel.revalidate();
        tasksPanel.repaint();

        LOGGER.log(Level.INFO, "Tarea '{0}' agregada a la vista.", tarea.getNombre());
    }
    
    /**
     * Método llamado por TareaPanel.onDeleted() cuando la tarea diaria finaliza.
     * Elimina el panel de la lista visual y actualiza la vista.
     * @param panel El TareaPanel a eliminar.
     */
    public void eliminarTareaPanel(TareaPanel panel) {
        // Encontrar el panel y el Strut (separador vertical) que le sigue o le precede y eliminar ambos.
        
        int index = -1;
        Component[] components = tasksPanel.getComponents();

        for (int i = 0; i < components.length; i++) {
            if (components[i] == panel) {
                index = i;
                break;
            }
        }
        
        if (index != -1) {
            tasksPanel.remove(panel);
            // Intentar eliminar el separador vertical (Strut) que le sigue, si existe.
            if (index < tasksPanel.getComponentCount() && tasksPanel.getComponent(index) instanceof Box.Filler) {
                tasksPanel.remove(index);
            }
            
            tasksPanel.revalidate();
            tasksPanel.repaint();
            LOGGER.log(Level.INFO, "TareaPanel eliminado de la vista.");
        }
    }
    
    /**
     * Este método se mantiene como un placeholder. Se eliminará cuando se implemente
     * la lógica de persistencia y se obtengan las tareas de la fuente de datos real.
     */
    public void cargarTareasIniciales() {
        LOGGER.log(Level.INFO, "Método cargarTareasIniciales ejecutado. Añadiendo tareas de prueba.");
        // SIMULACIÓN DE TAREAS INICIALES
        agregarTarea(new Tarea("Repasar conceptos de Java Swing"));
        agregarTarea(new Tarea("Leer el capítulo 3 de POO"));
    }
}