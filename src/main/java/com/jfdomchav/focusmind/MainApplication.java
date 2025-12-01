package com.jfdomchav.focusmind;

import com.jfdomchav.focusmind.controlador.Configuracion;
import com.jfdomchav.focusmind.vista.PrincipalView;

import javax.swing.*;
import java.awt.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Clase principal que inicializa y lanza la aplicación.
 * Es el JFrame contenedor que aloja la PrincipalView (JPanel).
 */
public class MainApplication extends JFrame {

    private static final Logger LOGGER = Logger.getLogger(MainApplication.class.getName());

    /**
     * Constructor. Configura la ventana y añade el contenido principal.
     */
    public MainApplication() {
        // 1. Configuración básica de la ventana
        setTitle("FocusMind - Sistema de Repaso Espaciado");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        // 2. Crear la vista principal (el JPanel con pestañas)
        PrincipalView mainPanel = new PrincipalView();
        
        // 3. Añadir el panel principal al JFrame
        getContentPane().add(mainPanel, BorderLayout.CENTER);
        
        // 4. Ajustar el tamaño y centrar la ventana
        pack(); // Ajusta el tamaño de la ventana a los tamaños preferidos/mínimos del mainPanel
        setLocationRelativeTo(null); // Centra la ventana en la pantalla
        
        LOGGER.log(Level.INFO, "MainApplication (JFrame) inicializada y lista para mostrar.");
    }

    /**
     * Método principal de la aplicación.
     * @param args Argumentos de línea de comandos.
     */
    public static void main(String[] args) {
        // 1. Cargar la configuración estática de la aplicación antes de cualquier cosa
        Configuracion.cargarConfiguracion();
        
        // 2. Asegurar que la UI se inicie en el hilo de despacho de eventos de Swing
        SwingUtilities.invokeLater(() -> {
            try {
                // Configurar el look and feel del sistema para una apariencia nativa
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                // Si falla, usar el look and feel por defecto de Swing
                LOGGER.log(Level.WARNING, "No se pudo establecer el Look and Feel del sistema.", e);
            }
            
            MainApplication frame = new MainApplication();
            frame.setVisible(true);
        });
    }
}