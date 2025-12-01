package com.jfdomchav.focusmind.vista;

import com.jfdomchav.focusmind.controlador.Configuracion;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Vista de la interfaz de usuario para configurar los parámetros de la aplicación.
 * Permite modificar los tres valores de configuración gestionados por la clase Configuracion.
 */
public class ConfiguracionView extends JPanel {

    private static final Logger LOGGER = Logger.getLogger(ConfiguracionView.class.getName());
    
    // Componentes de entrada para los 3 parámetros de configuración
    private JTextField ficheroCalendarioField;
    private JSpinner maxRepeticionesDiariasSpinner;
    private JSpinner maxRepeticionesCalendarioSpinner;
    
    // Colores para la estética
    private static final Color COLOR_FONDO_PRINCIPAL = new Color(245, 245, 245); // Gris muy claro
    private static final Color COLOR_BOTON_GUARDAR = new Color(50, 150, 200);  // Azul medio

    /**
     * Constructor. Configura el layout y añade los componentes de configuración.
     */
    public ConfiguracionView() {
        // Configuración básica del panel
        setLayout(new BorderLayout());
        setBackground(COLOR_FONDO_PRINCIPAL);
        setBorder(new EmptyBorder(20, 20, 20, 20)); // Relleno alrededor del contenido
        
        // Contenedor principal del formulario, usa BoxLayout vertical para apilar elementos
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setOpaque(false);

        // Título
        JLabel titleLabel = new JLabel("<html><h2>Configuración del Sistema FocusMind</h2></html>");
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        contentPanel.add(titleLabel);
        contentPanel.add(Box.createVerticalStrut(20)); // Espacio vertical

        // --- Panel de Configuración (usando GridLayout para alineación de etiquetas y campos) ---
        JPanel configGridPanel = new JPanel(new GridLayout(3, 2, 10, 10)); // 3 filas, 2 columnas, espaciado
        configGridPanel.setOpaque(false);
        configGridPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // 1. Configuración de Nombre de Fichero de Calendario
        configGridPanel.add(crearLabel("Nombre del Fichero de Calendario:"));
        ficheroCalendarioField = crearTextField(Configuracion.getNombreFicheroCalendario());
        configGridPanel.add(ficheroCalendarioField);
        
        // 2. Configuración de Máximo de Repeticiones Diarias
        configGridPanel.add(crearLabel("Máximo de Repeticiones Diarias:"));
        maxRepeticionesDiariasSpinner = crearSpinner(Configuracion.getMaxRepeticionesDiarias());
        configGridPanel.add(maxRepeticionesDiariasSpinner);
        
        // 3. Configuración de Máximo de Repeticiones en Calendario
        configGridPanel.add(crearLabel("Máximo de Repeticiones de Calendario:"));
        maxRepeticionesCalendarioSpinner = crearSpinner(Configuracion.getMaxRepeticionesCalendario());
        configGridPanel.add(maxRepeticionesCalendarioSpinner);

        contentPanel.add(configGridPanel);
        contentPanel.add(Box.createVerticalStrut(30)); // Espacio vertical

        // --- Botón de Guardar ---
        JButton guardarButton = new JButton("Guardar Configuración");
        guardarButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        guardarButton.setFont(new Font("SansSerif", Font.BOLD, 14));
        guardarButton.setForeground(Color.WHITE);
        guardarButton.setBackground(COLOR_BOTON_GUARDAR);
        guardarButton.setFocusPainted(false);
        guardarButton.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20)); 
        guardarButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Acción del botón
        guardarButton.addActionListener(e -> guardarConfiguracion());
        
        contentPanel.add(guardarButton);

        // Añadir el panel de contenido al centro para que se mantenga en el tope
        add(contentPanel, BorderLayout.NORTH);
        
        LOGGER.log(Level.INFO, "ConfiguracionView inicializada con 3 parámetros.");
    }
    
    /**
     * Helper para crear etiquetas estandarizadas.
     */
    private JLabel crearLabel(String texto) {
        JLabel label = new JLabel(texto);
        label.setFont(new Font("SansSerif", Font.PLAIN, 14));
        return label;
    }
    
    /**
     * Helper para crear campos de texto estandarizados.
     */
    private JTextField crearTextField(String valorInicial) {
        JTextField field = new JTextField(valorInicial);
        field.setFont(new Font("SansSerif", Font.PLAIN, 14));
        return field;
    }
    
    /**
     * Helper para crear spinners estandarizados para valores enteros.
     */
    private JSpinner crearSpinner(int valorInicial) {
        SpinnerNumberModel model = new SpinnerNumberModel(
            valorInicial, // Valor actual
            1,            // Mínimo
            50,           // Máximo
            1             // Incremento
        );
        JSpinner spinner = new JSpinner(model);
        spinner.setPreferredSize(new Dimension(80, 28));
        return spinner;
    }
    
    /**
     * Captura los valores de los componentes de la UI y actualiza la configuración
     * estática llamando al método setConfiguracion de la clase Configuracion.
     */
    private void guardarConfiguracion() {
        boolean guardadoExitoso = true;

        try {
            // 1. Guardar Nombre del Fichero de Calendario (String)
            String nombreFichero = ficheroCalendarioField.getText().trim();
            if (!nombreFichero.isEmpty()) {
                Configuracion.setConfiguracion(Configuracion.KEY_FICHERO_CALENDARIO, nombreFichero);
            } else {
                 JOptionPane.showMessageDialog(this, 
                    "El nombre del fichero no puede estar vacío.", 
                    "Error de Validación", 
                    JOptionPane.ERROR_MESSAGE);
                return;
            }

            // 2. Guardar Máximo de Repeticiones Diarias (Int)
            int maxDiarias = (Integer) maxRepeticionesDiariasSpinner.getValue();
            if (maxDiarias > 0) {
                 Configuracion.setConfiguracion(Configuracion.KEY_MAX_REPETICIONES_DIARIAS, String.valueOf(maxDiarias));
            } else {
                JOptionPane.showMessageDialog(this, 
                    "El máximo de repeticiones diarias debe ser mayor a 0.", 
                    "Error de Validación", 
                    JOptionPane.ERROR_MESSAGE);
                return;
            }

            // 3. Guardar Máximo de Repeticiones en Calendario (Int)
            int maxCalendario = (Integer) maxRepeticionesCalendarioSpinner.getValue();
            if (maxCalendario > 0) {
                Configuracion.setConfiguracion(Configuracion.KEY_MAX_REPETICIONES_CALENDARIO, String.valueOf(maxCalendario));
            } else {
                JOptionPane.showMessageDialog(this, 
                    "El máximo de repeticiones de calendario debe ser mayor a 0.", 
                    "Error de Validación", 
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            LOGGER.log(Level.INFO, "Configuración guardada. Fichero: {0}, Diarias: {1}, Calendario: {2}", 
                        new Object[]{nombreFichero, maxDiarias, maxCalendario});

            // Mostrar mensaje de éxito
            JOptionPane.showMessageDialog(this, 
                "Configuración guardada exitosamente.", 
                "Éxito", 
                JOptionPane.INFORMATION_MESSAGE);
                
        } catch (Exception ex) {
            guardadoExitoso = false;
            LOGGER.log(Level.SEVERE, "Error al guardar la configuración.", ex);
            JOptionPane.showMessageDialog(this, 
                "Ocurrió un error al intentar guardar la configuración: " + ex.getMessage(), 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
        }
        
        // Opcionalmente, recargar la configuración después de guardar para asegurar que 
        // los valores estáticos reflejen el archivo (aunque setConfiguracion ya lo hace).
        if (guardadoExitoso) {
            Configuracion.cargarConfiguracion(); 
        }
    }
}