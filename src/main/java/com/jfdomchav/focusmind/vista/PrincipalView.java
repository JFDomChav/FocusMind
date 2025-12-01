package com.jfdomchav.focusmind.vista;

import javax.swing.*;
import java.awt.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * La vista principal de la aplicación.
 * Utiliza un JTabbedPane para gestionar la navegación entre las principales áreas:
 * 1. Repaso Diario (TareasView)
 * 2. Repaso a Largo Plazo (CalendarioView)
 * 3. Configuración (ConfiguracionView)
 *
 * NOTA: Esta clase extiende JPanel y está diseñada para ser incrustada en un JFrame.
 */
public class PrincipalView extends JPanel {

    private static final Logger LOGGER = Logger.getLogger(PrincipalView.class.getName());
    
    // Instancias de las vistas
    private final TareasView tareasView;
    private final CalendarioView calendarioView;
    private final ConfiguracionView configuracionView;

    /**
     * Constructor. Configura el layout y el JTabbedPane.
     */
    public PrincipalView() {
        // 1. Configuración básica del panel principal
        setLayout(new BorderLayout());
        // Establecer tamaños preferidos/mínimos para cuando sea incrustado
        setMinimumSize(new Dimension(800, 600)); 
        setPreferredSize(new Dimension(1000, 700)); 
        
        // 2. Inicializar las vistas (paneles)
        this.tareasView = new TareasView();
        this.calendarioView = new CalendarioView();
        this.configuracionView = new ConfiguracionView();

        // 3. Crear el contenedor de pestañas
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("SansSerif", Font.BOLD, 14));
        
        // 4. Agregar las vistas al JTabbedPane
        
        // Pestaña 1: Repaso Diario (ÍNDICE 0)
        tabbedPane.addTab("Repaso Diario", createIcon(0xE557), tareasView); // Icono: 'alarm'
        
        // Pestaña 2: Calendario (Largo Plazo) (ÍNDICE 1)
        final int INDICE_CALENDARIO = 1; // Definimos el índice para referencia
        tabbedPane.addTab("Calendario", createIcon(0xE878), calendarioView); // Icono: 'calendar_today'
        
        // Pestaña 3: Configuración (ÍNDICE 2)
        tabbedPane.addTab("Configuración", createIcon(0xE8B8), configuracionView); // Icono: 'settings'
        
        // 5. Agregar el ChangeListener para el Calendario
        tabbedPane.addChangeListener(new ChangeListener() {
            /**
             * Se dispara cada vez que se cambia de pestaña.
             */
            @Override
            public void stateChanged(ChangeEvent e) {
                JTabbedPane sourceTabbedPane = (JTabbedPane) e.getSource();
                int selectedIndex = sourceTabbedPane.getSelectedIndex();
                
                // Si el índice seleccionado es el del Calendario (1), recargamos sus tareas.
                if (selectedIndex == INDICE_CALENDARIO) {
                    LOGGER.log(Level.INFO, "Pestaña Calendario seleccionada. Recargando tareas.");
                    calendarioView.cargarTareasIniciales();
                }
            }
        });
        
        // 6. Añadir el JTabbedPane al panel principal
        add(tabbedPane, BorderLayout.CENTER);
        
        LOGGER.log(Level.INFO, "PrincipalView (JPanel) y JTabbedPane inicializados con listener.");
    }

    /**
     * Helper para crear íconos básicos usando una fuente de íconos (simulación con código Unicode).
     */
    private Icon createIcon(int unicode) {
        // Intentar usar un ícono básico si el font lo soporta
        JLabel iconLabel = new JLabel(new String(Character.toChars(unicode)));
        iconLabel.setFont(new Font("Dialog", Font.BOLD, 18));
        iconLabel.setForeground(new Color(50, 50, 50));
        return new Icon() {
            @Override
            public void paintIcon(Component c, Graphics g, int x, int y) {
                iconLabel.setBounds(x, y, getIconWidth(), getIconHeight());
                iconLabel.paint(g);
            }

            @Override
            public int getIconWidth() {
                return 20;
            }

            @Override
            public int getIconHeight() {
                return 20;
            }
        };
    }
}