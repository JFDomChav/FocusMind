package focusmind;

import java.awt.Color;
import java.awt.Dialog;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.Random;
import javax.swing.BorderFactory;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

public class CalendarManager {
    private final File calendarFile;
    private String CALENDAR_FILE_NAME;
    private int MAX_ITERATIONS;
    private int NULL_ITERATION;
    private boolean ignore_all;
    public CalendarManager(){
        this.NULL_ITERATION = Options.getInstance().getNULL_ITERATION();
        this.MAX_ITERATIONS = Options.getInstance().getMAX_ITERATIONS();
        this.CALENDAR_FILE_NAME = Options.getInstance().getCALENDAR_FILE_NAME();
        this.ignore_all = Options.getInstance().getIGNORE_DUPLICATED_NAMES();
        String actualRoute = System.getProperty("user.dir");
        this.calendarFile = new File(actualRoute+"/"+CALENDAR_FILE_NAME);
        accessToFile();
    }
    private CalendarManager(File old_file){
        this.NULL_ITERATION = Options.getInstance().getNULL_ITERATION();
        this.MAX_ITERATIONS = Options.getInstance().getMAX_ITERATIONS();
        this.CALENDAR_FILE_NAME = old_file.getName();
        this.calendarFile = old_file;
    }
    private long calcDayTime(int iteration){ // Tested ☑
        long ret = (long) Math.pow(2, iteration);
        return ret;
    }
    private int caclBackwardness(LocalDate date){
        int days = LocalDate.now().getDayOfYear()-date.getDayOfYear();
        int yearsDiff = LocalDate.now().getYear()-date.getYear();
        int ret = days + (365*yearsDiff);
        return ret;
    }
    // Return the backwardness in days
    public int calcBackwardnessOf(String idTaskCM){ // Tested ☑
        FileInputStream calendarFIS;
        try {
            calendarFIS = new FileInputStream(this.calendarFile);
            getPositionOf(idTaskCM, calendarFIS); // index in date position
            calendarFIS.skip(1); // Skip to date position
            LocalDate date = fileDateToLocalDate(calendarFIS);
            if( !(date.equals(LocalDate.now())) && (date.compareTo(LocalDate.now()) < 0)){
                calendarFIS.close();
                return caclBackwardness(date);
            }
            calendarFIS.close();
        } 
        // File exception
        catch (FileNotFoundException ex) {
            Logger.getLogger(CalendarManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        // Byte exception
        catch (IOException ex) {
            Logger.getLogger(CalendarManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        return -1;
    }
    /*
        When the iterator is -1 that means the task reach the max of iterations
    */
    public void updateTask(String idTaskCM){ // Tested ☑
        try{
            FileInputStream calendarFIS = new FileInputStream(this.calendarFile);
            int startIn = getPositionOf(idTaskCM, calendarFIS)+1;// index in date position
            if(startIn>0){
                calendarFIS.skip(1); // Skip to date position
                LocalDate newDate = fileDateToLocalDate(calendarFIS);
                int iteration = Integer.parseInt(Character.toString(calendarFIS.read()))+1;
                if(iteration<(this.MAX_ITERATIONS+1)){
                    newDate = newDate.plusDays(calcDayTime(iteration));
                    changeDateIn(startIn, dateToString(newDate, false));
                    startIn+=11;
                    changeIterationIn(startIn, iteration);
                }else{
                    startIn+=11;
                    changeIterationIn(startIn, this.NULL_ITERATION);
                }
                }
            calendarFIS.close();
        }
        // File exception
        catch (FileNotFoundException ex) {
            Logger.getLogger(CalendarManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        // Byte exception
        catch (IOException ex) {
            Logger.getLogger(CalendarManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private static String byteArrayToString(byte[] array){ // Tested ☑
        String ret = "";
        for(int i = 0; i<array.length; i++){
            char character = (char) array[i];
            ret += character;
        }
        return ret;
    }
    
    /*  Input FIS: IIIIIIIIII:DD_MM_YYYY;C;name+
                              ▲
        Output FIS: IIIIIIIIII:DD_MM_YYYY;C;name+
                                          ▲
    */
    private LocalDate fileDateToLocalDate(FileInputStream calendarFIS) throws IOException{ // Tested ☑
        String numFinal = byteArrayToString(calendarFIS.readNBytes(2));
        int day = Integer.parseInt(numFinal);
        calendarFIS.skip(1);
        numFinal = byteArrayToString(calendarFIS.readNBytes(2));
        int month = Integer.parseInt(numFinal);
        calendarFIS.skip(1);
        numFinal = byteArrayToString(calendarFIS.readNBytes(4));
        int year = Integer.parseInt(numFinal);
        calendarFIS.skip(1);
        LocalDate newDate = LocalDate.of(year, month, day);
        return newDate;
    }
    /*
        Output: IIIIIIIIII:DD_MM_YYYY;C;name+
                          ▲ (index)
    */
    private int getPositionOf(String idTaskCM, FileInputStream calendarFIS) throws IOException{ // Tested ☑
        int startIn = 0;
        while(startIn < calendarFile.length()){
            // compare the ID
            byte[] id = calendarFIS.readNBytes(10);
            startIn+=10;
            if(Arrays.equals(id, idTaskCM.getBytes())){
                return startIn;
            }
            // Search a "/"
            else{
                /*
                    IIIIIIIIII:DD_MM_YYYY;C;name+
                    |        |              |
                    read (10) save jump (14)
                */
                calendarFIS.skipNBytes(14);
                startIn+=14;
                while(calendarFIS.read() != 47){
                    startIn++;
                }
                startIn++;
            }

        }
        return -1;
    }
    
    /*  Input: IIIIIIIIII:DD_MM_YYYY;C;name+, K
                                     ▲ (index)
        Result: IIIIIIIIII:DD_MM_YYYY;K;name+
    */
    private void changeIterationIn(int index, int newIteration) throws IOException{ // Tested ☑
        // Save 0 to index and index+2 to final info
        FileInputStream calendarFIS = new FileInputStream(this.calendarFile);
        byte[] first = calendarFIS.readNBytes(index);
        calendarFIS.skip(1); // length of iteration number
        byte[] last = calendarFIS.readAllBytes();
        // Create a new file
        FileOutputStream calendarFOS = new FileOutputStream(this.calendarFile, false);
        String it = ""+newIteration;
        calendarFOS.write(first);
        calendarFOS.write(it.getBytes());
        calendarFOS.write(last);
        calendarFOS.close();
    }
    
    private String dateToString(LocalDate date, boolean unspaced){ // Tested ☑
        // day to DD
        int day = date.getDayOfMonth();
        String dayString = "";
        if(day<10){
            dayString = "0"+day;
        }else{
            dayString = ""+day;
        }
        // month to MM
        int month = date.getMonthValue();
        String monthString = "";
        if(month<10){
            monthString = "0"+month;
        }else{
            monthString = ""+month;
        }
        String ret;
        if(unspaced){
            ret = dayString+monthString+date.getYear();
        }else{
            ret = dayString+"_"+monthString+"_"+date.getYear();
        }
        return ret;
    }
    
    private void changeDateIn(int index, String newDate) throws FileNotFoundException, IOException{ // Tested ☑
        // Save 0 to index and index+11 to final info
        FileInputStream calendarFIS = new FileInputStream(this.calendarFile);
        byte[] first = calendarFIS.readNBytes(index);
        calendarFIS.skip(10); // length of date
        byte[] last = calendarFIS.readAllBytes();
        // Create a new file
        FileOutputStream calendarFOS = new FileOutputStream(this.calendarFile, false);
        // DATE: DD_MM_YYYY = 10
        calendarFOS.write(first);
        calendarFOS.write(newDate.getBytes());
        calendarFOS.write(last);
        calendarFOS.close();
    }
    
    private String createID(String nameTask){ // Tested ☑
        // 10 len
        String id = Encoder.encode(nameTask,10);
        return id;
    }
    // Return if ignore button pressed
    public String putTask(String nameTask, JFrame frame){ // Tested ☑
        String id = "";
        if(this.calendarFile.canWrite()){
            try(FileOutputStream calendarFOS = new FileOutputStream(this.calendarFile, true)) {
                id = createID(nameTask);
                LocalDate date = LocalDate.now();
                int iteration = 0;
                TaskInfo info = new TaskInfo(
                            nameTask,
                            date,
                            iteration
                );
                if(!ignore_all){
                    boolean valid_name = true;
                    do{
                        if(this.getPositionOf(id, new FileInputStream(this.calendarFile)) >= 0){
                            DuplicatedTaskPanel panel = new DuplicatedTaskPanel();
                            panel.setTaskName(nameTask);
                            this.createWarning(panel, frame);
                            if(panel.ignore_pressed){
                                id = new_automatic_name(info);
                            }else if(panel.change_pressed){
                                id = createID(panel.getNewName());
                            }
                            valid_name = false;
                        }else{
                            valid_name = true;
                        }
                    }while(!valid_name);
                }else{
                    id = new_automatic_name(info);
                }
                String info_str = id+":"
                        // Date DD_MM_YYYY
                        +this.dateToString(date, false)
                        // Iteration number
                        +";"+Integer.toString(iteration)+";"
                        // task name
                        +info.name+"/"
                ;
                calendarFOS.write(info_str.getBytes());
                calendarFOS.close();
            }
            // Manage file exceptions
            catch (FileNotFoundException ex) {
                Logger.getLogger(CalendarManager.class.getName()).log(Level.SEVERE, null, ex);
            }
            // Manage getBytes() exception
            catch (IOException ex) {
                Logger.getLogger(CalendarManager.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return id;
    }
    private void accessToFile(){
        if(!calendarFile.exists()){
            createFile();
        }
    }
    
    private boolean createFile(){ // Tested ☑
        try {
            return calendarFile.createNewFile();
        } catch (IOException ex) {
            Logger.getLogger(CalendarManager.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }
    // Delete tasks that have already expired
    public void clearFile(){ // Tested ☑
        class TaskInfo{
            String id;
            String date;
            String iteration;
            String name;
            public TaskInfo(){}
            public TaskInfo(TaskInfo ts){
                this.id = ts.id;
                this.date = ts.date;
                this.iteration = ts.iteration;
                this.name = ts.name;
            }
            public String getToFile(){
                String ret = id+":"+date+";"+iteration+";"+name+"/";
                return ret;
            }
        }
        ArrayList<TaskInfo> tasks = new ArrayList<>();
        try {
            FileInputStream FIS = new FileInputStream(this.calendarFile);
            int index = 0;
            TaskInfo actualTaskInfo = new TaskInfo();
            /*
                IIIIIIIIII:DD_MM_YYYY;C;name+/
            */
            // fill the arraylist with the non-expired task
            while(index < calendarFile.length()){
                // Read the ID
                actualTaskInfo.id = byteArrayToString(FIS.readNBytes(10));
                index+=10;
                FIS.skip(1);
                index++;
                // Read the date
                actualTaskInfo.date = byteArrayToString(FIS.readNBytes(10));
                index+=10;
                FIS.skip(1);
                index++;
                // Read the iteration
                actualTaskInfo.iteration = byteArrayToString(FIS.readNBytes(1));
                index++;
                if(Integer.parseInt(actualTaskInfo.iteration) < NULL_ITERATION){
                    FIS.skip(1);
                    index++;
                    char character =(char) FIS.read();
                    index++;
                    String name = "";
                    // Read the name
                    while(character != 47){
                        name+=character;
                        character =(char) FIS.read();
                        index++;
                    }
                    actualTaskInfo.name = name;
                    TaskInfo add = new TaskInfo(actualTaskInfo);
                    tasks.add(add);
                    actualTaskInfo = new TaskInfo();
                }else{
                    FIS.skip(1);
                    index++;
                    char character =(char) FIS.read();
                    index++;
                    while(character != 47){
                        character =(char) FIS.read();
                        index++;
                    }
                }
            }
            FIS.close();
            this.calendarFile.delete(); //delete the file
            accessToFile(); // create new file
            FileOutputStream FOS = new FileOutputStream(this.calendarFile);
            for(TaskInfo task : tasks){
                FOS.write(task.getToFile().getBytes());
            }
            FOS.close();
        }
        // File exception
        catch (FileNotFoundException ex) {
            Logger.getLogger(CalendarManager.class.getName()).log(Level.SEVERE, null, ex);
        } 
        // Read exception
        catch (IOException ex) {
            Logger.getLogger(CalendarManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    public void deleteTask(String taskIdCM){ // Tested ☑
        try(
            FileInputStream calendarFIS = new FileInputStream(this.calendarFile);
        ) {
            int taskPos = this.getPositionOf(taskIdCM, calendarFIS)+12;
            this.changeIterationIn(taskPos, this.NULL_ITERATION);
            this.clearFile();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(CalendarManager.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(CalendarManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    public HashMap<String, String> getAllTasksInDate(LocalDate date){ // Tested ☑
        HashMap<String,String> tasks = new HashMap<>();
        FileInputStream FIS;
        try {
            FIS = new FileInputStream(this.calendarFile);
            int index = 0;
            /*
                IIIIIIIIII:DD_MM_YYYY;C;name+/
            */
            // fill the arraylist with the non-expired task
            while(index < calendarFile.length()){
                // Read the ID
                String readId = byteArrayToString(FIS.readNBytes(10));
                index+=11;
                FIS.skip(1);
                // Read the date
                LocalDate readDate = this.fileDateToLocalDate(FIS);
                index+=11;
                // Read the iteration
                int iteration = Integer.parseInt(byteArrayToString(FIS.readNBytes(1)));
                FIS.skip(1);
                index+=2;
                if((iteration!=this.NULL_ITERATION) && (readDate.equals(date))){
                    char character =(char) FIS.read();
                    index++;
                    String name = "";
                    // Read the name
                    while(character != 47){
                        name+=character;
                        character =(char) FIS.read();
                        index++;
                    }
                    tasks.put(new String(readId), new String(name));
                }else{
                    char character =(char) FIS.read();
                    index++;
                    while(character != 47){
                        character =(char) FIS.read();
                        index++;
                    }
                }
            }
            FIS.close();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(CalendarManager.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(CalendarManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        return tasks;
    }
    
    
    public HashMap<String,String> getAllPendingTasks(){
        HashMap<String,String> tasks = new HashMap<>();
        FileInputStream FIS;
        try {
            FIS = new FileInputStream(this.calendarFile);
            int index = 0;
            /*
                IIIIIIIIII:DD_MM_YYYY;C;name+/
            */
            // fill the arraylist with the non-expired pending task
            while(index < calendarFile.length()){
                // Read the ID
                String readId = byteArrayToString(FIS.readNBytes(10));
                index+=11;
                FIS.skip(1);
                // Read the date
                LocalDate readDate = this.fileDateToLocalDate(FIS);
                index+=11;
                // Read the iteration
                int iteration = Integer.parseInt(byteArrayToString(FIS.readNBytes(1)));
                FIS.skip(1);
                index+=2;
                if((iteration!=this.NULL_ITERATION) && (caclBackwardness(readDate) > 0)){
                    char character =(char) FIS.read();
                    index++;
                    String name = "";
                    // Read the name
                    while(character != 47){
                        name+=character;
                        character =(char) FIS.read();
                        index++;
                    }
                    tasks.put(new String(readId), new String(name));
                }else{
                    char character =(char) FIS.read();
                    index++;
                    while(character != 47){
                        character =(char) FIS.read();
                        index++;
                    }
                }
            }
            FIS.close();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(CalendarManager.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(CalendarManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        return tasks;
    }
    
    public HashMap<String, String> getAllTasksToday(){ // Tested ☑
        return this.getAllTasksInDate(LocalDate.now());
    }
    
    public String getDateOf(String idTask){
        FileInputStream calendarFIS;
        try {
            calendarFIS = new FileInputStream(this.calendarFile);
            this.getPositionOf(idTask, calendarFIS);
            calendarFIS.skip(1);
            String date = this.dateToString(this.fileDateToLocalDate(calendarFIS), false);
            date = date.replace('_', '/');
            return date;
        } catch (FileNotFoundException ex) {
            Logger.getLogger(CalendarManager.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(CalendarManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    
    public HashMap<String,String> getAllTasks(){
        HashMap<String,String> tasks = new HashMap<>();
        FileInputStream FIS;
        try {
            FIS = new FileInputStream(this.calendarFile);
            int index = 0;
            /*
                IIIIIIIIII:DD_MM_YYYY;C;name+/
            */
            // fill the arraylist with all the non-expired tasks
            while(index < calendarFile.length()){
                // Read the ID
                String readId = byteArrayToString(FIS.readNBytes(10));
                index+=11;
                FIS.skip(1);
                // Read the date
                LocalDate readDate = this.fileDateToLocalDate(FIS);
                index+=11;
                // Read the iteration
                int iteration = Integer.parseInt(byteArrayToString(FIS.readNBytes(1)));
                FIS.skip(1);
                index+=2;
                if((iteration!=this.NULL_ITERATION)){
                    char character =(char) FIS.read();
                    index++;
                    String name = "";
                    // Read the name
                    while(character != 47){
                        name+=character;
                        character =(char) FIS.read();
                        index++;
                    }
                    tasks.put(new String(readId), new String(name));
                }else{
                    char character =(char) FIS.read();
                    index++;
                    while(character != 47){
                        character =(char) FIS.read();
                        index++;
                    }
                }
            }
            FIS.close();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(CalendarManager.class.getName()).log(Level.SEVERE, null, ex);
            tasks = null;
        } catch (IOException ex) {
            Logger.getLogger(CalendarManager.class.getName()).log(Level.SEVERE, null, ex);
            tasks = null;
        }
        return tasks;
    }
    private class TaskInfo{
        public String name;
        public LocalDate date;
        public int iteration;
        public TaskInfo(String name, LocalDate date, int iteration) {
            this.name = name;
            this.date = date;
            this.iteration = iteration;
        }
    }
    private HashMap<String,TaskInfo> getAllTasksInfo(){
        HashMap<String,TaskInfo> tasks = new HashMap<>();
        FileInputStream FIS;
        try {
            FIS = new FileInputStream(this.calendarFile);
            int index = 0;
            /*
                IIIIIIIIII:DD_MM_YYYY;C;name+/
            */
            // fill the arraylist with all the non-expired tasks
            while(index < calendarFile.length()){
                // Read the ID
                String readId = byteArrayToString(FIS.readNBytes(10));
                index+=11;
                FIS.skip(1);
                // Read the date
                LocalDate readDate = this.fileDateToLocalDate(FIS);
                index+=11;
                // Read the iteration
                int iteration = Integer.parseInt(byteArrayToString(FIS.readNBytes(1)));
                FIS.skip(1);
                index+=2;
                if((iteration!=this.NULL_ITERATION)){
                    char character =(char) FIS.read();
                    index++;
                    String name = "";
                    // Read the name
                    while(character != 47){
                        name+=character;
                        character =(char) FIS.read();
                        index++;
                    }
                    tasks.put(new String(readId), 
                        new TaskInfo(
                                new String(name),
                                readDate,
                                iteration
                        )
                    );
                }else{
                    char character =(char) FIS.read();
                    index++;
                    while(character != 47){
                        character =(char) FIS.read();
                        index++;
                    }
                }
            }
            FIS.close();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(CalendarManager.class.getName()).log(Level.SEVERE, null, ex);
            tasks = null;
        } catch (IOException ex) {
            Logger.getLogger(CalendarManager.class.getName()).log(Level.SEVERE, null, ex);
            tasks = null;
        }
        return tasks;
    }
    private void writeTask(String id, String name, String date, String iteration){
        String info = id+":"
                        // Date DD_MM_YYYY
                        +date
                        // Iteration number
                        +";"+iteration+";"
                        // task name
                        +name+"/"
                ;
        try(
            FileOutputStream FOS = new FileOutputStream(this.calendarFile, true);
        ){
            FOS.write(info.getBytes());
            FOS.close();
        }catch(IOException e){
            System.err.println("writeTask: "+e);
        }
    }
    public void importFile(File old_file, JFrame frame){
        CalendarManager cm = new CalendarManager(old_file);
        HashMap<String,TaskInfo> tasks = cm.getAllTasksInfo();
        if(tasks != null){
            DuplicatedTaskPanel panel = new DuplicatedTaskPanel();
            for(Entry<String,TaskInfo> entry : tasks.entrySet()){
                String id = entry.getKey();
                TaskInfo info = entry.getValue();
                try(
                    FileInputStream FIS = new FileInputStream(this.calendarFile);
                ){
                    if(!ignore_all){
                        boolean valid_name = true;
                        do{
                            if(this.getPositionOf(id, FIS) >= 0){
                                panel.reset_options();
                                panel.setTaskName(info.name);
                                createWarning(panel, frame);

                                if(panel.ignore_pressed){
                                    id = new_automatic_name(info);
                                }
                                if(panel.change_pressed){
                                    info.name = panel.getNewName();
                                    id = this.createID(info.name);
                                }
                            }
                            if(this.getPositionOf(id, new FileInputStream(this.calendarFile)) >= 0){
                                valid_name = false;
                            }
                            if(tasks.containsKey(id)){
                                valid_name = false;
                            }
                        }while(!valid_name);
                    }else{
                        id = new_automatic_name(info);
                    }
                    this.writeTask(id, info.name, this.dateToString(info.date, false), Integer.toString(info.iteration));
                }catch(IOException e){
                    System.err.println("importFile: "+e);
                }
            }
        }else{
            System.out.println("The old calendar file is corrupted or not valid. FocusMind can't import the old tasks");
        }
    }
    private String new_automatic_name(TaskInfo info) throws FileNotFoundException, IOException{
        String id = "";
        boolean valid_name = true;
        do{
            valid_name = true;
            if(info.name.contains("(")){
                try{
                    int num = Integer.valueOf(String.valueOf(info.name.charAt(info.name.lastIndexOf("(")+1)))+1;
                    info.name = info.name.substring(0, info.name.lastIndexOf("(")+1)+Integer.toString(num)+")";
                }catch(NumberFormatException e){
                    info.name += info.name.substring(0, info.name.lastIndexOf("("))+"1)";
                }
            }else{
                info.name += " (1)";
            }
            id = this.createID(info.name);
            if(this.getPositionOf(id, new FileInputStream(this.calendarFile)) >= 0){
                valid_name = false;
            }
        }while(!valid_name);
        
        return id;
    }
    private void createWarning(JPanel panel, JFrame frame){
        JDialog warning = new JDialog(frame, "Advertencia: Nombre duplicado", true);
        warning.setUndecorated(true);
        warning.add(panel);
        warning.pack();
        warning.setLocationRelativeTo(null);
        warning.setVisible(true);
    }
}
