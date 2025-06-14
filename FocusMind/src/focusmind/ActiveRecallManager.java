package focusmind;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ActiveRecallManager extends Thread{ // TESTED ☑
    private StudyTasksList list = new StudyTasksList();
    private ArrayList<Integer> TasksReadyToStudy = new ArrayList<>();
    private final int MAX_ITERATIONS;
    private boolean running = true;
    private final Object lock = new Object();
    
    public ActiveRecallManager(int max_iterations){
        this.MAX_ITERATIONS = max_iterations;
    }
    
    public Object getLocker(){
        return this.lock;
    }
    
    public void finish(){
        this.running = false;
    }
    
    private int getMaxIterations(){
        int max = this.MAX_ITERATIONS;
        return max;
    }
    
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
    
    public int addTask(){
        int ret = this.list.add();
        return ret;
    }
    /* return status code:
        0 = time renewed, nothing more.
        1 = the task reach the max iterations and was removed
        -1= the list of tasks is empty
        -2= the task was not found 
    */
    public int taskAlreadyStudied(int taskId){
        this.TasksReadyToStudy.remove(this.TasksReadyToStudy.indexOf(taskId));
        int ret = this.list.taskRenewCooldownTimeOf(taskId);
        if(ret == 1){
            this.list.remove(taskId);
        }
        return ret;
    }
    
    public void removeTask(int id){
        this.list.remove(id);
    }
    
    @Override
    public void run(){
        while(running){
            if(list.check(TasksReadyToStudy)){
                synchronized(this){
                    this.notifyAll();
                }
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ex) {
                Logger.getLogger(ActiveRecallManager.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        synchronized(this.lock){
            this.lock.notifyAll();
        }
    }
    
    // List to save the study tasks
    private class StudyTasksList{
        private ArrayList<Integer> ids = new ArrayList<>();
        private StudyTasksNode firstNode = null;
        
        // Return the ID
        public int add(){
            int id = this.ActualFreeId();
            if(this.firstNode == null){
                this.firstNode = new StudyTasksNode(id, ActiveRecallManager.calcTime(0));
            }else{
                this.firstNode.add(new StudyTasksNode(id, ActiveRecallManager.calcTime(0)));
            }
            this.ids.add(id);
            return id;
        }
        
        private int ActualFreeId(){
            if(this.ids == null || this.ids.isEmpty()){
                return 0;
            }
            int max_id = 1023;
            int id = 0;
            while(id <= max_id){
                if(!this.ids.contains(id)){
                    return id;
                }else{
                    id++;
                }
            }
            return -1;
        }
        
        public void remove(int taskId){
            if(this.firstNode != null){
                if(this.firstNode.getId() == taskId){
                    this.firstNode = this.firstNode.getNext();
                }else{
                    this.firstNode.removeTaskById(taskId);
                }
            }
            this.ids.remove(this.ids.indexOf(taskId));
        }
        
        public boolean check(ArrayList<Integer> list){
            if(this.firstNode != null){
                return this.firstNode.check(list, false);
            }
            return false;
        }
        public int taskRenewCooldownTimeOf(int taskId){
            if(this.firstNode != null){
                return this.firstNode.renewCooldownTimeOf(taskId);
            }
            return -1;
        }
    }
    private class StudyTasksNode{
        private final int idTask;
        private LocalTime finishTimeTask;
        private StudyTasksNode nextNode = null;
        private boolean timeFinish = false;
        private int actualIteration = 0;
        
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
                if(this.finishTimeTask.compareTo(LocalTime.now()) < 0){
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
        
        public StudyTasksNode getNext(){
            return this.nextNode;
        }
        
        public void removeTaskById(int taskId){
            if(this.nextNode != null){
                if(this.nextNode.getId() == taskId){
                    this.nextNode = this.nextNode.nextNode;
                }else{
                    this.nextNode.removeTaskById(taskId);
                }
            }
        }
        
        public int getId(){
            int ret = this.idTask;
            return ret;
        }
        
        public int renewCooldownTimeOf(int taskId){
            if(this.idTask == taskId){
                this.timeFinish = false;
                this.actualIteration++;
                if(this.actualIteration <= getMaxIterations()){
                    this.finishTimeTask = ActiveRecallManager.calcTime(this.actualIteration);
                    return 0;
                }
                return 1;
            }else if(this.nextNode != null){
                return this.nextNode.renewCooldownTimeOf(taskId);
            }
            return -2;
        }
    }
}
