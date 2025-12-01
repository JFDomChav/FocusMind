package com.jfdomchav.focusmind.vista;

import com.jfdomchav.focusmind.controlador.HiloDeTiempo;
import com.jfdomchav.focusmind.controlador.Configuracion;
import com.jfdomchav.focusmind.modelo.TareaDeRepaso;
import com.jfdomchav.focusmind.modelo.TareaDeRepaso.Estado;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.TrayIcon.MessageType; // Importar el tipo de mensaje de la bandeja

// Imports para la funcionalidad de sonido
import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import javax.sound.sampled.*; 

/**
 * JPanel que implementa TareaUI. Muestra la información de una TareaDeRepaso 
 * y permite al usuario marcarla como repasada. Estilizado para una UI agradable.
 */
public class TareaPanel extends JPanel implements TareaUI {

	private final TareaDeRepaso tareaDeRepaso;
	private final TareasView parentView; // Referencia a la vista contenedora para eliminar
	
	// Componentes de la UI
	private JLabel nombreLabel;
	private JLabel descLabel;
	private JLabel repeticionesLabel;
	private JLabel tiempoRestanteLabel;
	private JButton repasoButton;
	private JButton eliminarButton; // Nuevo componente para eliminar

	// Colores para la estética
	private static final Color COLOR_FONDO_DEFAULT = new Color(245, 245, 245); // Gris muy claro
	private static final Color COLOR_FONDO_LISTA = new Color(220, 255, 220);	// Verde pastel suave
	private static final Color COLOR_BORDE = new Color(180, 180, 180);	 	 	// Gris medio para el separador
	private static final Color COLOR_BOTON_DEFAULT = new Color(100, 150, 250); // Azul suave
	private static final Color COLOR_BOTON_READY = new Color(40, 170, 80);	 	// Verde fuerte
	private static final Color COLOR_BOTON_ELIMINAR = new Color(220, 70, 70); // Rojo para eliminar

	/**
	 * Constructor. Inicializa el panel y sus componentes.
	 * @param tareaDeRepaso La lógica de la tarea asociada a este panel.
	 * @param parentView La vista contenedora para manejar la eliminación.
	 */
	public TareaPanel(TareaDeRepaso tareaDeRepaso, TareasView parentView) {
		this.tareaDeRepaso = tareaDeRepaso;
		this.parentView = parentView;
		
		// Configuración básica del panel
		setLayout(new BorderLayout(10, 5));
		setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createMatteBorder(0, 0, 1, 0, COLOR_BORDE), // Separador inferior
				new EmptyBorder(10, 15, 10, 15) // Relleno interno
		));
		setBackground(COLOR_FONDO_DEFAULT);
		
		initComponents();
		updateUIState(tareaDeRepaso.getEstado());
	}
	
	/**
	 * Inicializa y coloca todos los componentes Swing.
	 */
	private void initComponents() {
		// --- Panel de Info (Centro) ---
		JPanel infoPanel = new JPanel(new GridBagLayout());
		infoPanel.setOpaque(false); // Transparente para usar el color de fondo del padre
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.insets = new Insets(0, 5, 2, 5);
		gbc.fill = GridBagConstraints.HORIZONTAL;

		// Nombre (Título)
		nombreLabel = new JLabel("<html><b>" + tareaDeRepaso.getNombre() + "</b></html>");
		nombreLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.weightx = 1.0; // Ocupa el espacio horizontal
		infoPanel.add(nombreLabel, gbc);

		// Repeticiones (Derecha)
		repeticionesLabel = new JLabel();
		repeticionesLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
		repeticionesLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		gbc.gridx = 1;
		gbc.gridy = 0;
		gbc.weightx = 0.0;
		infoPanel.add(repeticionesLabel, gbc);
		
		// Descripción (Línea Inferior)
		descLabel = new JLabel("<html><small>" + tareaDeRepaso.getDescripcion() + "</small></html>");
		descLabel.setFont(new Font("SansSerif", Font.ITALIC, 11));
		descLabel.setForeground(Color.GRAY.darker());
		gbc.gridx = 0;
		gbc.gridy = 1;
		gbc.gridwidth = 2; // Ocupa ambas columnas
		infoPanel.add(descLabel, gbc);

		add(infoPanel, BorderLayout.CENTER);

		// --- Panel de Control (Sur) ---
		JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
		controlPanel.setOpaque(false);
		
		// Etiqueta de tiempo restante
		tiempoRestanteLabel = new JLabel("Pendiente...");
		tiempoRestanteLabel.setFont(new Font("SansSerif", Font.BOLD, 12));
		tiempoRestanteLabel.setForeground(Color.RED.darker());
		controlPanel.add(tiempoRestanteLabel);
		
		controlPanel.add(Box.createRigidArea(new Dimension(15, 0))); // Espacio entre etiqueta y botones

		// Botón de Eliminar
		eliminarButton = new JButton("Eliminar");
		eliminarButton.setFont(new Font("SansSerif", Font.BOLD, 12));
		eliminarButton.setForeground(Color.WHITE);
		eliminarButton.setBackground(COLOR_BOTON_ELIMINAR);
		eliminarButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
		eliminarButton.setFocusPainted(false);
		eliminarButton.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));	
		eliminarButton.setOpaque(true);
		eliminarButton.setBorderPainted(false);	
		
		eliminarButton.addActionListener(e -> {
			// Se usa JOptionPane.showConfirmDialog ya que Swing es la plataforma de esta aplicación.
			int dialogResult = JOptionPane.showConfirmDialog(
					this,	
					"¿Estás seguro de que quieres eliminar esta tarea? Esto no se puede deshacer.",	
					"Confirmar Eliminación",	
					JOptionPane.YES_NO_OPTION,
					JOptionPane.WARNING_MESSAGE);
			if(dialogResult == JOptionPane.YES_OPTION){
				onDeleted();
			}
		});
		
		controlPanel.add(eliminarButton);
		controlPanel.add(Box.createRigidArea(new Dimension(10, 0))); // Pequeño espacio entre botones

		// Botón de Repaso
		repasoButton = new JButton("¡Repasar!");
		repasoButton.setFont(new Font("SansSerif", Font.BOLD, 12));
		repasoButton.setForeground(Color.WHITE);
		repasoButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
		repasoButton.setFocusPainted(false);
		repasoButton.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));	
		repasoButton.setOpaque(true);
		repasoButton.setBorderPainted(false);	
		
		// Acción del botón
		repasoButton.addActionListener(e -> onRepasarClicked());

		controlPanel.add(repasoButton);
		
		add(controlPanel, BorderLayout.SOUTH);
		
		// Cargar los valores iniciales y el estado
		actualizarInfoLabels();
	}
	
	/**
	 * Reproduce un sonido desde la ruta de archivo especificada.
	 * @param soundFilePath La ruta al archivo de sonido (e.g., "Resources/Sounds/ready.mp3").
	 */
	private void playSound(String soundFilePath) {
	    try {
	        File soundFile = new File(soundFilePath);
	        
	        // NOTA: Para archivos .mp3 puede ser necesario instalar librerías externas.
	        // Se recomienda usar .wav para máxima compatibilidad con Java Clip.
	        AudioInputStream audioIn = AudioSystem.getAudioInputStream(soundFile);
	        Clip clip = AudioSystem.getClip();
	        clip.open(audioIn);
	        clip.start();
	        
	        clip.addLineListener(event -> {
	            if (event.getType() == LineEvent.Type.STOP) {
	                clip.close();
	            }
	        });

	    } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
	        System.err.println("Error al reproducir el sonido: " + soundFilePath);
	        e.printStackTrace();
	    }
	}
	
	/**
	 * Muestra una notificación en la bandeja del sistema (SystemTray).
	 * Utiliza un ícono temporal que se remueve después de 5 segundos para evitar múltiples íconos.
	 * @param title Título de la notificación.
	 * @param message Cuerpo del mensaje.
	 * @param type Tipo de mensaje (INFO, WARNING, ERROR).
	 */
	private void showTrayNotification(String title, String message, MessageType type) {
	    if (SystemTray.isSupported()) {
	        try {
	            SystemTray tray = SystemTray.getSystemTray();
	            
	            // IMPORTANTE: El TrayIcon requiere una imagen válida. 
	            // Usamos un placeholder, pero en una app real, debe ser un recurso cargado.
	            // Para esta demostración, usamos un recurso que se asumiría existe.
	            Image image = Toolkit.getDefaultToolkit().getImage("Resources/Images/icon.png"); 
	            
	            TrayIcon trayIcon = new TrayIcon(image, "FocusMind Repaso");
	            trayIcon.setImageAutoSize(true);
	            
	            // Añadir el ícono a la bandeja para poder mostrar el mensaje
	            tray.add(trayIcon);
	            
	            // Mostrar el mensaje de burbuja
	            trayIcon.displayMessage(title, message, type);
	            
	            // Usamos un temporizador Swing para remover el ícono después de 5 segundos
	            Timer removeTimer = new Timer(5000, e -> { 
	                tray.remove(trayIcon);
	            });
	            removeTimer.setRepeats(false);
	            removeTimer.start();
	            
	        } catch (AWTException e) {
	            System.err.println("Error AWT al añadir el ícono de bandeja: " + e.getMessage());
	        }
	    } else {
	        System.out.println("SystemTray no soportado en este sistema. No se pudo enviar la notificación.");
	    }
	}

	/**
	 * Actualiza las etiquetas de información estática y el contador de repeticiones.
	 */
	private void actualizarInfoLabels() {
		repeticionesLabel.setText(String.format("Repetición: %d / %d", 
			tareaDeRepaso.getRepeticionesActuales(), 
			Configuracion.getMaxRepeticionesDiarias()));
	}

	/**
	 * Lógica ejecutada al pulsar el botón "¡Repasar!".
	 */
	private void onRepasarClicked() {
		if (tareaDeRepaso.getEstado() == Estado.LISTA_PARA_REPASAR) {
			// Se llama a la lógica de repetición.
			tareaDeRepaso.aumentarRepeticion();
		} else {
			JOptionPane.showMessageDialog(this, 
										  "La tarea aún no está lista para ser repasada.", 
										  "Espera", 
										  JOptionPane.WARNING_MESSAGE);
		}
	}
	
	/**
	 * Actualiza la apariencia del panel según el estado de la tarea.
	 * @param estado El estado actual de TareaDeRepaso.
	 */
	private void updateUIState(Estado estado) {
		if (estado == Estado.LISTA_PARA_REPASAR) {
			setBackground(COLOR_FONDO_LISTA);
			repasoButton.setEnabled(true);
			repasoButton.setBackground(COLOR_BOTON_READY);
			repasoButton.setText("¡REPASAR!");
			tiempoRestanteLabel.setText("¡LISTO AHORA!");
			tiempoRestanteLabel.setForeground(COLOR_BOTON_READY.darker().darker());
		} else {
			setBackground(COLOR_FONDO_DEFAULT);
			repasoButton.setEnabled(false);
			repasoButton.setBackground(COLOR_BOTON_DEFAULT);
			repasoButton.setText("Esperando...");
		}
	}

	// --- Implementación de TareaUI ---

	@Override
	public void onReady() {
        // 1. Reproducir sonido de "listo"
        playSound("Resources/Sounds/ready.mp3"); 
        
        // 2. Mandar notificación al sistema operativo (Windows/Linux/Mac)
        showTrayNotification(
            "¡Tarea Lista!", 
            "La tarea '" + tareaDeRepaso.getNombre() + "' está lista para ser repasada ahora.", 
            MessageType.INFO
        );
        
		// 3. Actualizar la UI
		actualizarInfoLabels();	
		updateUIState(Estado.LISTA_PARA_REPASAR);
	}
	
	@Override
	public void onWaiting(long tiempo) {
		// Se llama cuando la tarea entra en modo espera (después de un repaso exitoso)
		actualizarInfoLabels();
		updateUIState(Estado.NO_LISTA_PARA_REPASAR);
		// Mostrar el tiempo inicial de espera
		onTimeUpdate(tiempo);	
	}
	
	/**
	 * Se llama cada segundo con el tiempo restante.
	 * @param timepo El tiempo restante en segundos.
	 */
	@Override
	public void onTimeUpdate(long timepo) {
		long tiempoRestante = timepo;

		if (tareaDeRepaso.getEstado() == Estado.NO_LISTA_PARA_REPASAR) {
			if (tiempoRestante > 0) {
				long minutos = TimeUnit.SECONDS.toMinutes(tiempoRestante);
				long segundos = tiempoRestante - TimeUnit.MINUTES.toSeconds(minutos);
				
				String tiempoFormateado = String.format("%02d:%02d", minutos, segundos);
				tiempoRestanteLabel.setText("Repaso en: " + tiempoFormateado);
				tiempoRestanteLabel.setForeground(Color.ORANGE.darker());
			} else {
				// Estado intermedio justo antes de onReady()
				tiempoRestanteLabel.setText("Cargando...");
				tiempoRestanteLabel.setForeground(Color.GRAY);
			}
		}
	}

	@Override
	public void onDeleted() {
		// 1. Eliminar la tarea del hilo de tiempo
		tareaDeRepaso.eliminar();	
		// 2. Notificar a la vista padre para que elimine este panel de la lista visual
		parentView.eliminarTareaPanel(this);
	}
}