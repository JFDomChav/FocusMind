package focusmind;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CalendarManager {
    private final String CALENDAR_FILE_NAME = "calendar.txt";
    private final File calendarFile;
    /*
        Esta clase debe guardar en un fichero el tema a estudiar, la fecha, si
        esta retrasado y si es asi por cuantos dias.
        Esta clase se conectara directamente con ActiveRecallManager cuando
        una tarea termine su maximo de iteraciones se guardara en el fichero.
    
        La clase ya guarda en el fichero el tema, su id, la fecha para volver
        a estudiar y la iteracion actual. Ademas puede actualizar estos datos.
    
        Falta probar, hacer que cuando se alcance el maximo de iteraciones
        se elimine del fichero y calcular el retraso si no se estudio.
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
    
    public void updateTask(String idTaskCM){
        try {
            FileInputStream calendarFIS = new FileInputStream(this.calendarFile);
            int startIn = 0;
            boolean finded = false;
            while(!finded && (startIn < calendarFile.length())){
                // compare the ID
                byte[] id = calendarFIS.readNBytes(11);
                startIn+=11;
                if(Arrays.equals(id, idTaskCM.getBytes())){
                    calendarFIS.skip(1);
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
                    int iteration = Integer.parseInt(Byte.toString((byte)calendarFIS.read()))+1;
                    LocalDate newDate = 
                        LocalDate.of(year, month, day)
                                .plusDays(calcDayTime(iteration))
                    ;
                    changeDateIn(startIn, dateToString(newDate, true));
                    startIn+=12;
                    changeIterationIn(startIn, iteration);
                    finded = true;
                }
                // Search a "/"
                else{
                    /*
                        IIIIIIIIII:DD_MM_YYYY;C;name+
                        |        |             |
                        read (11) save jump (12)
                    */
                    calendarFIS.skipNBytes(12);
                    startIn+=12;
                    while(calendarFIS.read() != 47){
                        startIn++;
                    }
                }
                
            }
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
}
