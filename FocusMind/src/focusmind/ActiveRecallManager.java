package focusmind;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ActiveRecallManager extends Thread{
    private StudyTasksList list = new StudyTasksList();
    private ArrayList<Integer> TasksReadyToStudy = new ArrayList<>();
    
    // Basic potencial math model. 30 *2^i seconds.
    private static long calcSeconds(int iteration){
        return (long)( 30*(Math.pow(2, iteration)) );
    }
    
    // Return the finish time of study task
    private static LocalTime calcTime(int iteration){
        return LocalTime.now().plusSeconds(ActiveRecallManager.calcSeconds(iteration));
    }
    
    public ArrayList<Integer> getTaksReadyToStudy(){
        ArrayList<Integer> ret = this.TasksReadyToStudy;
        return ret;
    }
    
    @Override
    public void run(){
        while(true){
            if(this.list.check(this.TasksReadyToStudy)){
                this.notifyAll();
            }
            /*
            Siguiente:
                -Estudiar con active recall
                -Calendario que muestre cuando y qué debo estudiar
            El programa checa cada segundo si hay alguna tarea nueva que se deba estudiar
            y si la hay entonces la agrega al arraylist y notifica a todos
            los hilos que esperan su respuesta.
            Falta probar y programar la eliminacion de una tarea y su 
            actualizacion de tiempo cuando el usuario haya terminado de 
            estudiarla
            */
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ex) {
                Logger.getLogger(ActiveRecallManager.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    // List to save the study tasks
    private class StudyTasksList{
        private int ActualFreeId = 0;
        private StudyTasksNode firstNode = null;
        
        // Return the ID
        public int add(){
            if(this.firstNode == null){
                this.firstNode = new StudyTasksNode(this.ActualFreeId, ActiveRecallManager.calcTime(0));
            }else{
                this.firstNode.add(new StudyTasksNode(this.ActualFreeId, ActiveRecallManager.calcTime(0)));
            }
            this.ActualFreeId++;
            return (this.ActualFreeId-1);
        }
        public boolean check(ArrayList<Integer> list){
            if(this.firstNode != null){
                return this.firstNode.check(list, false);
            }
            return false;
        }
    }
    private class StudyTasksNode{
        private int idTask;
        private LocalTime finishTimeTask;
        private StudyTasksNode nextNode = null;
        private boolean timeFinish = false;
        
        public StudyTasksNode(int idTask, LocalTime finishTimeTask){
            this.idTask = idTask;
            this.finishTimeTask = finishTimeTask;
        }
        
        public void add(StudyTasksNode node){
            if(this.nextNode == null){
                this.nextNode = node;
            }else{
                this.nextNode.add(node);
            }
        }
        
        public boolean check(ArrayList<Integer> list, boolean status){
            if(!this.timeFinish){
                if(this.finishTimeTask.equals(LocalTime.now())){
                    list.add(this.idTask);
                    this.timeFinish = true;
                    status = true;
                }
            }
            if(this.nextNode == null){
                return status;
            }else{
                return this.nextNode.check(list, status);
            }
        }
        
        public boolean isTimeToStudyIt(){
            boolean ret = this.timeFinish;
            return ret;
        }
    }
}
