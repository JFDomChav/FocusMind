package com.jfdomchav.focusmind.vista;

// Importamos ambas clases, ya que TareaDeCalendario utiliza Tarea
import com.jfdomchav.focusmind.modelo.Tarea;
import com.jfdomchav.focusmind.modelo.TareaDeCalendario; 

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.text.SimpleDateFormat; // Importar para formatear fechas
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Componente visual (JPanel) que representa una Tarea en el calendario de repaso a largo plazo.
 * Muestra el nombre de la tarea, su descripción y la próxima fecha de repaso.
 */
public class TareaCalendarioPanel extends JPanel {

	private static final Logger LOGGER = Logger.getLogger(TareaCalendarioPanel.class.getName());
	
	private final TareaDeCalendario tareaCalendario; // Ahora utiliza la clase de modelo completa
	// Este Runnable se utiliza para notificar al controlador/vista padre que el modelo ha cambiado 
	// (eliminación o actualización de fecha) y debe guardarse y recargarse.
	private final Runnable onPersistenciaSolicitada; 
	
	// Componentes de la UI
	private JLabel nombreLabel;
	private JLabel descLabel;
	private JLabel fechaRepasoLabel;
	private JButton marcarRepasadoButton;
	private JButton eliminarButton;	

	// Colores para la estética
	private static final Color COLOR_FONDO = new Color(250, 250, 255); // Blanco azulado suave
	private static final Color COLOR_BORDE = new Color(200, 200, 230); // Azul claro para el borde
	private static final Color COLOR_FECHA_PENDIENTE = new Color(150, 50, 50); // Rojo oscuro para la fecha pendiente
    private static final Color COLOR_FECHA_REPROGRAMADA = new Color(50, 150, 50); // Verde para la fecha reprogramada
	private static final Color COLOR_BOTON_REPASAR = new Color(80, 150, 80); // Verde para la acción de repasar
	private static final Color COLOR_BOTON_ELIMINAR = new Color(200, 50, 50); // Rojo para la acción de eliminar

	/**
	 * Constructor. Inicializa el panel y sus componentes con la información de la Tarea.
	 * @param tareaCalendario La instancia de TareaDeCalendario a mostrar.
	 * @param onPersistenciaSolicitada El Runnable a ejecutar para notificar al controlador/padre que debe guardar y actualizar la vista.
	 */
	public TareaCalendarioPanel(TareaDeCalendario tareaCalendario, Runnable onPersistenciaSolicitada) {
		this.tareaCalendario = tareaCalendario;
		this.onPersistenciaSolicitada = onPersistenciaSolicitada;
		
		// Configuración básica del panel
		setLayout(new BorderLayout(15, 5));
		
		// Estilo de calendario/tarjeta
		setBorder(BorderFactory.createCompoundBorder(
			BorderFactory.createLineBorder(COLOR_BORDE, 1, true), // Borde redondeado sutil
			new EmptyBorder(10, 15, 10, 15) // Relleno interno
		));
		setBackground(COLOR_FONDO);
		setMaximumSize(new Dimension(Integer.MAX_VALUE, 100)); // Limitar la altura para listas
		
		initComponents();
		actualizarContenido(); // Rellenar con datos reales
		
		LOGGER.log(Level.INFO, "TareaCalendarioPanel creado para: {0}", tareaCalendario.getNombre());
	}

	/**
	 * Inicializa y coloca todos los componentes Swing en el panel.
	 */
	private void initComponents() {
		// --- Panel de Info Central ---
		JPanel infoPanel = new JPanel(new BorderLayout(0, 4));
		infoPanel.setOpaque(false);

		// Nombre (Título)
		nombreLabel = new JLabel();	
		nombreLabel.setFont(new Font("SansSerif", Font.BOLD, 15));
		infoPanel.add(nombreLabel, BorderLayout.NORTH);

		// Descripción
		descLabel = new JLabel();
		descLabel.setFont(new Font("SansSerif", Font.ITALIC, 11));
		descLabel.setForeground(Color.GRAY.darker());
		infoPanel.add(descLabel, BorderLayout.CENTER);

		add(infoPanel, BorderLayout.CENTER);

		// --- Panel de Control y Fecha (Este) ---
		JPanel controlPanel = new JPanel();
		controlPanel.setLayout(new BoxLayout(controlPanel, BoxLayout.Y_AXIS));
		controlPanel.setOpaque(false);
		controlPanel.setBorder(new EmptyBorder(0, 10, 0, 0)); // Espacio entre info y control

		// Panel para alinear Fecha y Botones
		JPanel alignmentPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
		alignmentPanel.setOpaque(false);
		alignmentPanel.setAlignmentX(Component.RIGHT_ALIGNMENT);

		// Fecha de Repaso
		fechaRepasoLabel = new JLabel();
		fechaRepasoLabel.setFont(new Font("SansSerif", Font.BOLD, 13));
		// El color se asigna en actualizarContenido
		fechaRepasoLabel.setAlignmentX(Component.RIGHT_ALIGNMENT);
		alignmentPanel.add(fechaRepasoLabel);
		
		controlPanel.add(alignmentPanel);
		controlPanel.add(Box.createVerticalStrut(8)); // Espacio

		// Panel para los botones (para que queden uno al lado del otro horizontalmente)
		JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
		buttonPanel.setOpaque(false);
		buttonPanel.setAlignmentX(Component.RIGHT_ALIGNMENT);
		
		// Botón de Eliminar Tarea
		eliminarButton = new JButton("Eliminar Tarea");
		eliminarButton.setFont(new Font("SansSerif", Font.BOLD, 12));
		eliminarButton.setForeground(Color.WHITE);
		eliminarButton.setBackground(COLOR_BOTON_ELIMINAR);
		eliminarButton.setFocusPainted(false);
		eliminarButton.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));	
		eliminarButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
		
		eliminarButton.addActionListener(e -> onEliminarClicked());
		buttonPanel.add(eliminarButton);


		// Botón de Marcar Repasado
		marcarRepasadoButton = new JButton("Marcar Repasado");
		marcarRepasadoButton.setFont(new Font("SansSerif", Font.BOLD, 12));
		marcarRepasadoButton.setForeground(Color.WHITE);
		marcarRepasadoButton.setBackground(COLOR_BOTON_REPASAR);
		marcarRepasadoButton.setFocusPainted(false);
		marcarRepasadoButton.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));	
		marcarRepasadoButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
		
		// Acción del botón
		marcarRepasadoButton.addActionListener(e -> onMarcarRepasadoClicked());

		buttonPanel.add(marcarRepasadoButton);
		
		controlPanel.add(buttonPanel);
		
		add(controlPanel, BorderLayout.EAST);
	}
	
	/**
	 * Rellena las etiquetas con la información actualizada de la tarea.
	 * Elimina todos los placeholders.
	 */
	private void actualizarContenido() {
		// Formateador de fecha simple
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

		// --- Datos de Tarea ---
		// Incluimos el contador de repeticiones en el nombre
		String nombreHtml = String.format(
			"<html><b>%s</b> <span style='font-size:10px; color: #555;'>[Rep: %d - Int: %d d]</span></html>",
			tareaCalendario.getNombre(), 
			tareaCalendario.getRepeticionesEnCalendario(), 
			tareaCalendario.getIntervaloDias()
		);
		nombreLabel.setText(nombreHtml);
		
		String descripcion = tareaCalendario.getDescripcion();
		descLabel.setText("<html><small>" + (descripcion.isEmpty() ? "Sin descripción" : descripcion) + "</small></html>");
		
		// --- Fecha de Repaso ---
		String fechaFormateada = sdf.format(tareaCalendario.getProximoRepaso());
		fechaRepasoLabel.setText("Prox. Repaso: " + fechaFormateada);
        fechaRepasoLabel.setForeground(COLOR_FECHA_PENDIENTE); // Color por defecto

        // --- Estado y Botones ---
        if (tareaCalendario.haCompletadoCiclo()) {
            marcarRepasadoButton.setText("Ciclo Terminado");
            marcarRepasadoButton.setBackground(Color.LIGHT_GRAY);
            marcarRepasadoButton.setEnabled(false);
            eliminarButton.setEnabled(false); // No permitir eliminación si ya se completó (o dejar solo si el usuario quiere limpiar)
            fechaRepasoLabel.setText("¡Completada!");
            fechaRepasoLabel.setForeground(COLOR_FECHA_REPROGRAMADA.darker());
        } else {
            marcarRepasadoButton.setText("Marcar Repasado");
            marcarRepasadoButton.setBackground(COLOR_BOTON_REPASAR);
            marcarRepasadoButton.setEnabled(true);
            eliminarButton.setEnabled(true);
        }
	}
	
	/**
	 * Lógica ejecutada al pulsar el botón "Marcar Repasado".
	 * 1. Actualiza el modelo con el repaso exitoso (nueva fecha, nuevo intervalo).
	 * 2. Si el ciclo se completa, marca la tarea para eliminación.
	 * 3. Notifica al controlador para que persista los cambios y recargue la lista.
	 */
	private void onMarcarRepasadoClicked() {
		LOGGER.log(Level.INFO, "Tarea de calendario '{0}' marcada como repasada. Reprogramando...", tareaCalendario.getNombre());
		
		// 1. Actualizar el modelo (incrementa repetición, calcula nuevo intervalo y nueva fecha)
		tareaCalendario.actualizarRepasoExitoso();

		// 2. Si la tarea ha completado su ciclo, la marcamos para eliminación en el modelo.
        if (tareaCalendario.haCompletadoCiclo()) {
            LOGGER.log(Level.INFO, "Tarea '{0}' completó su ciclo y será marcada para eliminación.", tareaCalendario.getNombre());
            tareaCalendario.marcarParaEliminar();
        }

		// 3. Notificar al controlador principal para que guarde el modelo actualizado
		// y refresque la vista (eliminando si fue marcada para eliminar).
		if (onPersistenciaSolicitada != null) {
			 onPersistenciaSolicitada.run(); 
			 LOGGER.log(Level.INFO, "Callback de actualización/eliminación ejecutado: Notificando al controlador para guardar y actualizar.");
		}
		
		// 4. Actualizar inmediatamente el panel con la nueva fecha y estado
		actualizarContenido();
	}
	
	/**
	 * Lógica ejecutada al pulsar el botón "Eliminar Tarea".
	 */
	private void onEliminarClicked() {
		// Usamos JOptionPane ya que es una aplicación de escritorio Swing y funciona como confirmación modal.
		int dialogResult = JOptionPane.showConfirmDialog(
			this,	
			"¿Estás seguro de que quieres eliminar esta tarea del calendario? Esto no se puede deshacer.",	
			"Confirmar Eliminación",	
			JOptionPane.YES_NO_OPTION,
			JOptionPane.WARNING_MESSAGE);
			
		if(dialogResult == JOptionPane.YES_OPTION){
			
			LOGGER.log(Level.INFO, "Iniciando eliminación de tarea de calendario: {0}", tareaCalendario.getNombre());

			// 1. Llama al método del modelo para marcar la tarea para su eliminación
			tareaCalendario.marcarParaEliminar();
			LOGGER.log(Level.INFO, "Tarea de calendario '{0}' marcada para eliminación en el modelo.", tareaCalendario.getNombre());
			
			// 2. Ejecuta el callback para notificar al controlador principal/servicio
			if (onPersistenciaSolicitada != null) {
				onPersistenciaSolicitada.run();	
				LOGGER.log(Level.INFO, "Callback de eliminación ejecutado: Notificando al controlador para guardar y actualizar.");
			}
			
			// 3. Eliminación del componente visual de la lista (el controlador recargará la lista completa al guardar)
			Container parent = getParent();
			if (parent != null) {
				parent.remove(this);
				parent.revalidate(); // Recalcular el layout
				parent.repaint();	// Repintar el contenedor
			}
			LOGGER.log(Level.INFO, "Componente de tarea de calendario eliminado de la vista.");
		}
	}
}