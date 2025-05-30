package focusmind;

import javax.swing.ImageIcon;

public class MainView extends javax.swing.JFrame {
    private final String PATH = "resources";
    private final String createImage = "create.png";
    private final String calendarImage = "calendar.png";

    public MainView() {
        initComponents();
        this.setLocationRelativeTo(null);
        setLabelImages();
    }
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        ButtonCreate = new javax.swing.JButton();
        ButtonCalendar = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        LabelImageCreate = new javax.swing.JLabel();
        LabelImageCalendar = new javax.swing.JLabel();
        ButtonOptions = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        ButtonCreate.setText("Crear nueva tarea");
        ButtonCreate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ButtonCreateActionPerformed(evt);
            }
        });

        ButtonCalendar.setText("Calendario");
        ButtonCalendar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ButtonCalendarActionPerformed(evt);
            }
        });

        jLabel1.setFont(new java.awt.Font("Corbel", 1, 24)); // NOI18N
        jLabel1.setText("FocusMind");

        ButtonOptions.setText("op");
        ButtonOptions.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ButtonOptionsActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(30, 30, 30)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(LabelImageCreate, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(ButtonCreate, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 96, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(ButtonCalendar, javax.swing.GroupLayout.DEFAULT_SIZE, 122, Short.MAX_VALUE)
                    .addComponent(LabelImageCalendar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(30, 30, 30))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel1)
                .addGap(77, 77, 77)
                .addComponent(ButtonOptions, javax.swing.GroupLayout.PREFERRED_SIZE, 59, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel1)
                    .addComponent(ButtonOptions, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(29, 29, 29)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(LabelImageCreate, javax.swing.GroupLayout.DEFAULT_SIZE, 122, Short.MAX_VALUE)
                    .addComponent(LabelImageCalendar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(ButtonCalendar, javax.swing.GroupLayout.DEFAULT_SIZE, 48, Short.MAX_VALUE)
                    .addComponent(ButtonCreate, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(25, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void ButtonCreateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ButtonCreateActionPerformed
        TasksView tv = new TasksView();
        tv.setVisible(true);
        this.dispose();
    }//GEN-LAST:event_ButtonCreateActionPerformed

    private void ButtonCalendarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ButtonCalendarActionPerformed
        CalendarView cv = new CalendarView();
        cv.setVisible(true);
        this.dispose();
    }//GEN-LAST:event_ButtonCalendarActionPerformed

    private void ButtonOptionsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ButtonOptionsActionPerformed
        OptionsView opv = new OptionsView();
        opv.setVisible(true);
        this.setVisible(false);
        this.dispose();
    }//GEN-LAST:event_ButtonOptionsActionPerformed
    
    private void setLabelImages(){
        String actualRoute = System.getProperty("user.dir");
        LabelImageCreate.setIcon(new ImageIcon(actualRoute+"/"+PATH+"/"+createImage));
        LabelImageCalendar.setIcon(new ImageIcon(actualRoute+"/"+PATH+"/"+calendarImage));
    }
    
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new MainView().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton ButtonCalendar;
    private javax.swing.JButton ButtonCreate;
    private javax.swing.JButton ButtonOptions;
    private javax.swing.JLabel LabelImageCalendar;
    private javax.swing.JLabel LabelImageCreate;
    private javax.swing.JLabel jLabel1;
    // End of variables declaration//GEN-END:variables
}
