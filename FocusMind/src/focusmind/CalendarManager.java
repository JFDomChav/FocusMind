package focusmind;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CalendarManager {
    private final String CALENDAR_FILE_NAME = "calendar.txt";
    private final File calendarFile;
    private final int MAX_ITERATIONS = 7;
    private final int NULL_ITERATION = 9;
    /*
        Esta clase debe guardar en un fichero el tema a estudiar, la fecha, si
        esta retrasado y si es asi por cuantos dias.
        Esta clase se conectara directamente con ActiveRecallManager cuando
        una tarea termine su maximo de iteraciones se guardara en el fichero.
    
        La clase ya guarda en el fichero el tema, su id, la fecha para volver
        a estudiar y la iteracion actual. Ademas puede actualizar estos datos.
    
        Falta probar.
    */
    public CalendarManager(){
        String actualRoute = System.getProperty("user.dir");
        this.calendarFile = new File(actualRoute+"/"+CALENDAR_FILE_NAME);
        accessToFile();
    }
    
    private long calcDayTime(int iteration){
        long ret = (long) Math.pow(2, iteration);
        return ret;
    }
    // Return the backwardness in days
    public int calcBackwardnessOf(String idTaskCM){
        FileInputStream calendarFIS;
        try {
            calendarFIS = new FileInputStream(this.calendarFile);
            getPositionOf(idTaskCM, calendarFIS); // index in date position
            calendarFIS.skip(1); // Skip to date position
            LocalDate date = fileDateToLocalDate(calendarFIS);
            if( !(date.equals(LocalDate.now())) && (date.compareTo(LocalDate.now()) < 0)){
                int days = LocalDate.now().getDayOfYear()-date.getDayOfYear();
                int yearsDiff = LocalDate.now().getYear()-date.getYear();
                int ret = days + (365*yearsDiff);
                calendarFIS.close();
                return ret;
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
    public void updateTask(String idTaskCM){
        try{
            FileInputStream calendarFIS = new FileInputStream(this.calendarFile);
            int startIn = getPositionOf(idTaskCM, calendarFIS)+1;// index in date position
            calendarFIS.skip(1); // Skip to date position
            LocalDate newDate = fileDateToLocalDate(calendarFIS);
            calendarFIS.skip(11); // Skip to iteration position
            int iteration = Integer.parseInt(Byte.toString((byte)calendarFIS.read()))+1;
            if(iteration<(this.MAX_ITERATIONS+1)){
                newDate = newDate.plusDays(iteration);
                changeDateIn(startIn, dateToString(newDate, true));
                startIn+=11;
                changeIterationIn(startIn, iteration);
            }else{
                startIn+=11;
                changeIterationIn(startIn, this.NULL_ITERATION);
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
    
    /*  Input FIS: IIIIIIIIII:DD_MM_YYYY;C;name+
                              ▲
        Output FIS: IIIIIIIIII:DD_MM_YYYY;C;name+
                                          ▲
    */
    private LocalDate fileDateToLocalDate(FileInputStream calendarFIS) throws IOException{
        int day = Integer.parseInt(
                    Byte.toString((byte)calendarFIS.read())
                    +Byte.toString((byte)calendarFIS.read())
            );
            calendarFIS.skip(1);
            int month = Integer.parseInt(
                    Byte.toString((byte)calendarFIS.read())
                    +Byte.toString((byte)calendarFIS.read())
            );
            calendarFIS.skip(1);
            int year = Integer.parseInt(
                    Byte.toString((byte)calendarFIS.read())
                    +Byte.toString((byte)calendarFIS.read())
                    +Byte.toString((byte)calendarFIS.read())
                    +Byte.toString((byte)calendarFIS.read())
            );
            calendarFIS.skip(1);
            LocalDate newDate = LocalDate.of(year, month, day);
            return newDate;
    }
    /*
        Output: IIIIIIIIII:DD_MM_YYYY;C;name+
                          ▲ (index)
    */
    private int getPositionOf(String idTaskCM, FileInputStream calendarFIS) throws IOException{
        int startIn = 0;
        while(startIn < calendarFile.length()){
            // compare the ID
            byte[] id = calendarFIS.readNBytes(11);
            startIn+=11;
            if(Arrays.equals(id, idTaskCM.getBytes())){
                return startIn;
            }
            // Search a "/"
            else{
                /*
                    IIIIIIIIII:DD_MM_YYYY;C;name+
                    |        |             |
                    read (11) save jump (14)
                */
                calendarFIS.skipNBytes(14);
                startIn+=12;
                while(calendarFIS.read() != 47){
                    startIn++;
                }
            }

        }
        return -1;
    }
    
    /*  Input: IIIIIIIIII:DD_MM_YYYY;C;name+, K
                                     ▲ (index)
        Result: IIIIIIIIII:DD_MM_YYYY;K;name+
    */
    private void changeIterationIn(int index, int newIteration) throws IOException{
        FileOutputStream calendarFOS = new FileOutputStream(this.calendarFile);
        String it = ""+newIteration;
        calendarFOS.write(it.getBytes(), index, it.getBytes().length);
        calendarFOS.close();
    }
    
    private String dateToString(LocalDate date, boolean unspaced){
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
    
    private void changeDateIn(int index, String newDate) throws FileNotFoundException, IOException{
        FileOutputStream calendarFOS = new FileOutputStream(this.calendarFile);
        // DATE: DD_MM_YYYY = 10
        calendarFOS.write(newDate.getBytes(), index, 10);
        calendarFOS.close();
    }
    
    private String createID(String nameTask){
        String id = nameTask.substring(0, 2)+dateToString(LocalDate.now(), false);
        return id;
    }
    
    public boolean putTask(String nameTask){
        boolean status = false;
        if(this.calendarFile.canWrite()){
            try {
                FileOutputStream calendarFOS = new FileOutputStream(this.calendarFile);
                String id = createID(nameTask);
                
                String info = id+":"
                        // Date DD_MM_YYYY
                        +dateToString(LocalDate.now(), true)
                        // Iteration number
                        +";"+"0"
                        // task name
                        +nameTask+"/"
                ;
                calendarFOS.write(info.getBytes());
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
        return status;
    }
    private void accessToFile(){
        if(!calendarFile.exists()){
            createFile();
        }
    }
    
    private boolean createFile(){
        try {
            return calendarFile.createNewFile();
        } catch (IOException ex) {
            Logger.getLogger(CalendarManager.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }
    // Delete tasks that have already expired
    public void clearFile(){
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
            public static String byteArrayToString(byte[] array){
                String ret = "";
                for(int i = 0; i<array.length; i++){
                    ret += Byte.toString(array[i]);
                }
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
                actualTaskInfo.id = TaskInfo.byteArrayToString(FIS.readNBytes(10));
                FIS.skip(1);
                // Read the date
                actualTaskInfo.date = TaskInfo.byteArrayToString(FIS.readNBytes(10));
                FIS.skip(1);
                // Read the iteration
                actualTaskInfo.iteration = TaskInfo.byteArrayToString(FIS.readNBytes(1));
                if(Integer.parseInt(actualTaskInfo.iteration) < NULL_ITERATION){
                    FIS.skip(1);
                    char character =(char) FIS.read();
                    String name = "";
                    // Read the name
                    while(character != 47){
                        name+=character;
                        character =(char) FIS.read();
                    }
                    actualTaskInfo.name = name;
                    TaskInfo add = new TaskInfo(actualTaskInfo);
                    tasks.add(add);
                    actualTaskInfo = new TaskInfo();
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
}
