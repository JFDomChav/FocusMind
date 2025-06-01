package focusmind;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ConfigFileManager {
    private static ConfigFileManager cfm = null;
    private String CONFIG_FILE_NAME;
    private File config_file;
    private String[] parameter_list = {
        "CALENDAR_FILE_NAME", 
        "MAX_ITERATIONS", 
        "NULL_ITERATION", 
        "MAX_STUDY_ITERATIONS",
        "IGNORE_DUPLICATED_NAMES"
    };
    private String config_structure;
    
    public static ConfigFileManager getInstance(){
        if(cfm == null){
            cfm = new ConfigFileManager();
        }
        return cfm;
    }
    
    private ConfigFileManager(){
        this.CONFIG_FILE_NAME = Options.CONFIG_FILE_NAME;
        config_structure = "";
        for(String param : parameter_list){
            if(param.equals(Options.calendar_file_name_var_name)){
                config_structure += param+":calendar.txt;";
            }else if(param.equals(Options.ignore_duplicated_names_var_name)){
                config_structure += param+":0;";
            }else{
                config_structure += param+":1;";
            }
        }
        String actualRoute = System.getProperty("user.dir");
        this.config_file = new File(actualRoute+"/"+CONFIG_FILE_NAME);
        accessToFile();
        if(config_file.exists() && (config_file.length() < this.config_structure.getBytes().length)){
            writeStructure();
        }
    }
    public String getValueOf(String str_name){
        for(String param : this.parameter_list){
            if(param.equals(str_name)){
                return this.getValue(str_name);
            }
        }
        System.err.println(str_name+" is not a valid parameter");
        return null;
    }
    public void set(String str_name, String str_value){
        boolean find = false;
        for(String param : this.parameter_list){
            if(param.equals(str_name)){
                this.setValue(str_name, str_value);
                find = true;
                break;
            }
        }
        if(!find){
            System.err.println(str_name+" is not a valid parameter");
        }
    }
    private String getValue(String str_name){
        int index_start = searchByName(str_name);
        int index_end = index_start;
        try(
            FileInputStream FIS = new FileInputStream(this.config_file);
            FileInputStream FIS_RES = new FileInputStream(this.config_file);
        ){
            boolean find = false;
            char delimiter = ";".charAt(0);
            FIS.skip(index_start);
            while((this.config_file.length() >= index_end) && !find){
                char letter = (char) FIS.read();
                if(letter == delimiter){
                    find = true;
                }
                index_end++;
            }
            FIS.close();
            FIS_RES.skip(index_start+1);
            String res = new String(FIS_RES.readNBytes(((index_end-1)-(index_start+1))),StandardCharsets.UTF_8);
            if(res.isBlank()){
                res = null;
            }
            FIS_RES.close();
            return res;
        }catch(FileNotFoundException e){
            System.err.println("getValue: "+e);
        }catch(IOException e){
            System.err.println("getValue: "+e);
        }
        return null;
    }
    private void setValue(String str_name, String str_value){
        int index_write = searchByName(str_name);
        int index = index_write;
        if(index > 0){
            try(
                FileInputStream FIS = new FileInputStream(this.config_file);
            ){
                boolean find = false;
                char delimiter = ";".charAt(0);
                String str_start_remaining = new String(FIS.readNBytes(index+1), StandardCharsets.UTF_8);
                char last_letter_read = 0;
                while(this.config_file.length() >= index && !find){
                    last_letter_read = (char) FIS.read();
                    if(last_letter_read == delimiter){
                        find = true;
                    }
                    index++;
                }
                index--;
                int remaining = Math.toIntExact((config_file.length() - (Long.valueOf(index))));
                String str_remaining = String.valueOf(last_letter_read);
                str_remaining += new String(FIS.readNBytes(remaining), StandardCharsets.UTF_8);
                FileOutputStream FOS = new FileOutputStream(this.config_file, false);
                FOS.write(str_start_remaining.getBytes());
                FOS.write(str_value.getBytes(), 0, str_value.getBytes().length);
                FOS.write(str_remaining.getBytes());
                FIS.close();
                FOS.close();
            }catch(FileNotFoundException e){
                System.err.println("setValue: "+e);
            }catch(IOException e){
                System.err.println("setValue: "+e);
            }
        }else{
            System.err.println(str_name+" not finded in config file");
        }
    }
    /*
    Output (Search: VVVVVVV):
        XXXXXX:;VVVVVVV:;AAAAAAAAAAAA:;
                       ▲ (index)
    */
    private int searchByName(String str_search){
        try(
            FileInputStream FIS = new FileInputStream(this.config_file);
        ){
            boolean jump_to_next = false;
            char delimiter = ";".charAt(0);
            int str_index = 0;
            int index = 0;
            while(this.config_file.length() >= index){
                char actual_letter = (char) (FIS.read());
                
                index++;
                if(!jump_to_next){
                    if(actual_letter == str_search.charAt(str_index)){
                        str_index++;
                        if(str_index >= str_search.length()-1){
                            FIS.skip(1);
                            index++;
                            FIS.close();
                            return index;
                        }
                    }else{
                        str_index = 0;
                        jump_to_next = true;
                    }
                }else if(actual_letter == delimiter){
                    jump_to_next = false;
                }
            }
            FIS.close();
        }catch(FileNotFoundException e){
            System.err.println("searchByName: "+e);
        }catch(IOException e){
            System.err.println("searchByName: "+e);
        }
        return -1;
    }
    private void accessToFile(){
        if(!config_file.exists()){
            try {
                config_file.createNewFile();
                
            } catch (IOException ex) {
                Logger.getLogger(CalendarManager.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    private void writeStructure(){
        try(
            FileOutputStream FOS = new FileOutputStream(this.config_file, false);
        ){
            FOS.write(config_structure.getBytes());
        }catch(FileNotFoundException e){
            
        }catch(IOException e){
            
        }
    }
}
