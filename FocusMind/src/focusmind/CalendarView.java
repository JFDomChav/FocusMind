package focusmind;

import java.awt.Dimension;
import java.util.HashMap;
import java.util.Map.Entry;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class CalendarView extends javax.swing.JFrame {

    /**
     * Creates new form calendarView
     */
    public CalendarView() {
        initComponents();
        tasksToday();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        ScrollPane = new javax.swing.JScrollPane();
        ScrollPanePanel = new javax.swing.JPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jLabel1.setText("Pendientes:");

        ScrollPane.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        ScrollPane.setAlignmentX(0.0F);
        ScrollPane.setAlignmentY(0.0F);

        ScrollPanePanel.setAlignmentX(0.5F);
        ScrollPanePanel.setLayout(new javax.swing.BoxLayout(ScrollPanePanel, javax.swing.BoxLayout.Y_AXIS));
        ScrollPane.setViewportView(ScrollPanePanel);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(37, 37, 37)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(6, 6, 6)
                        .addComponent(ScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 300, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jLabel1))
                .addContainerGap(125, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(26, 26, 26)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(ScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 282, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(CalendarView.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(CalendarView.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(CalendarView.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(CalendarView.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new CalendarView().setVisible(true);
            }
        });
    }
    
    private void tasksToday(){
        CalendarManager cm = new CalendarManager();
        HashMap<String, String> tasks = cm.getAllTasksToday();
        for(Entry<String,String> entry : tasks.entrySet()){
            Task task = new Task(entry.getKey(), entry.getValue());
            ScrollPanePanel.add(task.getPanel());
            configurateScrollPanePanel(task, tasks.size());
        }
    }
    
    private void configurateScrollPanePanel(Task task, int hashmapLength){
        if(ScrollPanePanel.getPreferredSize().height < (task.getPanelHeight()*(hashmapLength+1))){
            ScrollPanePanel.setPreferredSize( new Dimension(ScrollPanePanel.getSize().width, (task.getPanelHeight()*(hashmapLength+1))));
        }
        ScrollPanePanel.revalidate();
    }
    
    private class Task{
        private String taskIdCM;
        private String taskName;
        private JLabel LabelTaskName;
        private JButton ButtonCompleteTask;
        private JButton ButtonDeleteTask;
        private JPanel taskPanel;
        
        public Task(String taskIdCM, String taskName){
            this.taskIdCM = taskIdCM;
            this.taskName = taskName;
        }
        
        public JPanel getPanel(){
            this.LabelTaskName = new JLabel(this.taskName);
            this.ButtonCompleteTask = new JButton("Completar");
            this.ButtonCompleteTask.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    ButtonCompleteTaskActionPerformed(evt);
                }
            });
            this.ButtonDeleteTask = new JButton("Eliminar");
            this.ButtonDeleteTask.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    ButtonDeleteActionPerformed(evt);
                }
            });
            this.taskPanel = new JPanel();
            javax.swing.GroupLayout taskPanelLayout = new javax.swing.GroupLayout(this.taskPanel);
            this.taskPanel.setLayout(taskPanelLayout);
            taskPanelLayout.setHorizontalGroup(
                taskPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(taskPanelLayout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(LabelTaskName, javax.swing.GroupLayout.DEFAULT_SIZE, 121, Short.MAX_VALUE)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addGroup(taskPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(ButtonCompleteTask, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(ButtonDeleteTask, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addContainerGap())
            );
            taskPanelLayout.setVerticalGroup(
                taskPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, taskPanelLayout.createSequentialGroup()
                    .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(taskPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addGroup(taskPanelLayout.createSequentialGroup()
                            .addComponent(ButtonCompleteTask)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(ButtonDeleteTask))
                        .addComponent(LabelTaskName, javax.swing.GroupLayout.PREFERRED_SIZE, 54, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addContainerGap())
            );
            return this.taskPanel;
        }
        private void ButtonDeleteActionPerformed(java.awt.event.ActionEvent evt) {                                             
            // Remove panel
            ScrollPanePanel.remove(this.taskPanel);
            // Remove with CalendarManager
            CalendarManager cm = new CalendarManager();
            cm.deleteTask(this.taskIdCM);
            ScrollPanePanel.revalidate();
            ScrollPanePanel.repaint();
        }
        private void ButtonCompleteTaskActionPerformed(java.awt.event.ActionEvent evt) {                                             
            // Remove panel
            ScrollPanePanel.remove(this.taskPanel);
            // update with CalendarManager
            CalendarManager cm = new CalendarManager();
            cm.updateTask(this.taskIdCM);
            ScrollPanePanel.revalidate();
            ScrollPanePanel.repaint();
        }
        public int getPanelHeight(){
            return this.taskPanel.size().height;
        }
        public String getID(){
            return new String(this.taskIdCM);
        }
        public String getName(){
            return new String(this.taskName);
        }
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane ScrollPane;
    private javax.swing.JPanel ScrollPanePanel;
    private javax.swing.JLabel jLabel1;
    // End of variables declaration//GEN-END:variables
}
