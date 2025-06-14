package focusmind;

import java.awt.Dimension;
import java.awt.Image;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

public class TasksView extends javax.swing.JFrame {
    private ActiveRecallManager ARM;
    private TasksList list;
    private boolean running = true;
    private int MAX_STUDY_ITERATIONS;
    /**
     * Creates new form TasksView
     */
    public TasksView() {
        this.MAX_STUDY_ITERATIONS = Options.getInstance().getMAX_STUDY_ITERATIONS();
        initComponents();
        this.setLocationRelativeTo(null);
        this.list = new TasksList();
        this.ARM = new ActiveRecallManager(this.MAX_STUDY_ITERATIONS);
        this.ARM.start();
        waitARM().start();
        Task.setJFrame(this);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        ScrollPane = new javax.swing.JScrollPane();
        ScrollPanePanel = new javax.swing.JPanel();
        jButton1 = new javax.swing.JButton();
        InputTaskName = new javax.swing.JTextField();
        ButtonBack = new javax.swing.JButton();

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 100, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 100, Short.MAX_VALUE)
        );

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        ScrollPane.setAlignmentX(0.0F);
        ScrollPane.setAlignmentY(0.0F);
        ScrollPane.setMaximumSize(new java.awt.Dimension(652, 247));

        ScrollPanePanel.setAlignmentX(0.0F);
        ScrollPanePanel.setMaximumSize(new java.awt.Dimension(650, 32767));
        ScrollPanePanel.setName(""); // NOI18N
        ScrollPanePanel.setPreferredSize(new java.awt.Dimension(650, 268));
        ScrollPanePanel.setLayout(new javax.swing.BoxLayout(ScrollPanePanel, javax.swing.BoxLayout.Y_AXIS));
        ScrollPane.setViewportView(ScrollPanePanel);
        ScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        jButton1.setText("Añadir");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        ButtonBack.setText("Atras");
        ButtonBack.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ButtonBackActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap(100, Short.MAX_VALUE)
                .addComponent(InputTaskName, javax.swing.GroupLayout.PREFERRED_SIZE, 430, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(ScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(ButtonBack))
                .addGap(0, 0, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addComponent(ButtonBack)
                .addGap(2, 2, 2)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(InputTaskName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton1))
                .addGap(18, 18, 18)
                .addComponent(ScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 265, Short.MAX_VALUE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        this.list.createTask(InputTaskName.getText());
    }//GEN-LAST:event_jButton1ActionPerformed

    private void ButtonBackActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ButtonBackActionPerformed
        this.running = false;
        synchronized(this.ARM){
            this.ARM.notifyAll();
        }
        MainView mv = new MainView();
        mv.setVisible(true);
    }//GEN-LAST:event_ButtonBackActionPerformed

    /**
     * @param args the command line arguments
     */
    
    private Thread waitARM(){
        Thread waitarm = new Thread(){
            @Override
            public void run(){
                ArrayList<Integer> taskReady;
                try {
                    while(running){
                        synchronized(ARM){
                            ARM.wait();
                            if(running){
                                taskReady = ARM.getTaksReadyToStudy();
                                for(int id : taskReady){
                                    if(list == null){
                                    }
                                    list.setReadyToStudy(id);
                                }
                                ScrollPanePanel.revalidate();
                            }
                        }
                    }
                    synchronized(ARM.getLocker()){
                        ARM.finish();
                        ARM.getLocker().wait();
                    }
                    dispose();
                } catch (InterruptedException ex) {
                    Logger.getLogger(TasksView.class.getName()).log(Level.SEVERE, null, ex);
                }
                
            }
        };
        return waitarm;
    }
    
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
            java.util.logging.Logger.getLogger(TasksView.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(TasksView.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(TasksView.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(TasksView.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new TasksView().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton ButtonBack;
    private javax.swing.JTextField InputTaskName;
    private javax.swing.JScrollPane ScrollPane;
    private javax.swing.JPanel ScrollPanePanel;
    private javax.swing.JButton jButton1;
    private javax.swing.JPanel jPanel1;
    // End of variables declaration//GEN-END:variables

    private class TasksList{
        private HashMap<Integer, Task> TasksFunction = new HashMap<>();
        
        public void createTask(String taskName){
            int idTask = ARM.addTask();
            Task task = new Task(idTask);
            JPanel taskPanel = task.createTask(taskName);
            configurateScrollPanePanel(taskPanel.getPreferredSize().height);
            ScrollPanePanel.add(taskPanel);
            TasksFunction.put(idTask, task);
        }
        public void configurateScrollPanePanel(int panelHeight){
            if(ScrollPanePanel.getPreferredSize().height < (panelHeight*(this.TasksFunction.size()+1))){
                ScrollPanePanel.setPreferredSize( new Dimension(ScrollPanePanel.getSize().width, (panelHeight*(this.TasksFunction.size()+1))));
            }
            ScrollPanePanel.revalidate();
        }
        public void setReadyToStudy(int id){
           this.TasksFunction.get(id).setReadyToStudy();
        }
        public void deleteTask(int id,  Task task){
            this.TasksFunction.remove(id, task);
        }
    }
    
    private class Task{
        private static JFrame frame;
        private final int taskId;
        private final int ICON_SIZE = 37;
        private String taskName;
        private JPanel TaskPanel;
        private JLabel LabelTask;
        private JLabel iconStatusTask;
        private JButton ButtonDelete;
        private JButton ButtonRenewTime;
        
        public Task(int id){
            this.taskId = id;
        }
        
        public int getId(){
            return this.taskId;
        }
        
        public static void setJFrame(JFrame frame_father){
            frame = frame_father;
        }
        
        public JPanel createTask(String taskName){
            // Create the task panel
            this.taskName = taskName;
            this.TaskPanel = new JPanel();
            this.LabelTask = new JLabel(taskName);
            this.iconStatusTask = new JLabel("");

            this.ButtonDelete = new JButton("Eliminar");
            this.ButtonDelete.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    ButtonDeleteActionPerformed(evt);
                }
            });

            this.ButtonRenewTime = new JButton("Reiniciar tiempo");
            this.ButtonRenewTime.setEnabled(false);
            this.ButtonRenewTime.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    ButtonRenewTimeActionPerformed(evt);
                }
            });
            
            this.iconStatusTask.setMaximumSize(new java.awt.Dimension(ICON_SIZE, ICON_SIZE));            
            this.iconStatusTask.setPreferredSize(new java.awt.Dimension(ICON_SIZE, ICON_SIZE));
            setIcon(false);
            
            javax.swing.GroupLayout TaskPanelLayout = new javax.swing.GroupLayout(this.TaskPanel);
            this.TaskPanel.setLayout(TaskPanelLayout);
            TaskPanelLayout.setHorizontalGroup(
                TaskPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(TaskPanelLayout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(this.LabelTask, javax.swing.GroupLayout.DEFAULT_SIZE, 240, Short.MAX_VALUE)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(this.ButtonDelete)
                    .addGap(18, 18, 18)
                    .addComponent(this.ButtonRenewTime)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(this.iconStatusTask, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap())
            );
            TaskPanelLayout.setVerticalGroup(
                TaskPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(TaskPanelLayout.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(TaskPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, TaskPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(this.LabelTask)
                            .addComponent(this.ButtonDelete)
                            .addComponent(this.ButtonRenewTime))
                        .addComponent(this.iconStatusTask))
                    .addContainerGap())
            );
            return this.TaskPanel;
        }
        
        private void setIcon(boolean readyToStudy){
            
            String actualRoute = System.getProperty("user.dir");
            ImageIcon preIcon;
            if(readyToStudy){
                preIcon = new ImageIcon(actualRoute+"/resources/ready.png");
            }else{
                preIcon = new ImageIcon(actualRoute+"/resources/waiting.png");
            }
            Image icon = preIcon.getImage().getScaledInstance(ICON_SIZE, ICON_SIZE, Image.SCALE_SMOOTH);
            this.iconStatusTask.setIcon(new ImageIcon(icon));
        }
        
        public void setReadyToStudy(){
            this.ButtonRenewTime.setEnabled(true);
            setIcon(true);
        }
        
        public void setStudied(){
            this.ButtonRenewTime.setEnabled(false);
            setIcon(false);
        }
        
        private void removeTask(){
            list.deleteTask(this.taskId, this);
            ARM.removeTask(this.taskId);
            ScrollPanePanel.remove(this.TaskPanel);
            ScrollPanePanel.revalidate();
            ScrollPanePanel.repaint();
        }
        
        private void ButtonDeleteActionPerformed(java.awt.event.ActionEvent evt) {                                             
            removeTask();
        }
        
        private void ButtonRenewTimeActionPerformed(java.awt.event.ActionEvent evt) {                                             
            if(ARM.taskAlreadyStudied(this.taskId) == 1){
                CalendarManager cm = new CalendarManager();
                cm.putTask(this.taskName, frame);
                ScrollPanePanel.remove(this.TaskPanel);
            }else{
                setStudied();
                ScrollPanePanel.revalidate();
            }
            ScrollPanePanel.revalidate();
            ScrollPanePanel.repaint();
        }
    }
}
