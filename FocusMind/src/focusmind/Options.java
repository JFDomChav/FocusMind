package focusmind;

public class Options {
    private static Options OP_INSTANCE;
    
    private String CALENDAR_FILE_NAME = "calendar.txt";
    private int MAX_ITERATIONS;
    private int NULL_ITERATION;
    private int MAX_STUDY_ITERATIONS;
    private boolean IGNORE_DUPLICATED_NAMES = false;

    public boolean getIGNORE_DUPLICATED_NAMES() {
        return IGNORE_DUPLICATED_NAMES;
    }

    public void setIGNORE_DUPLICATED_NAMES(boolean IGNORE_DUPLICATED_NAMES) {
        this.IGNORE_DUPLICATED_NAMES = IGNORE_DUPLICATED_NAMES;
    }
    
    public final static String CONFIG_FILE_NAME = "config.txt";
    public static final String max_iterations_var_name = "MAX_ITERATIONS";
    public static final String null_iterator_var_name = "NULL_ITERATION";
    public static final String max_study_iterations_var_name = "MAX_STUDY_ITERATIONS";
    public static final String calendar_file_name_var_name = "CALENDAR_FILE_NAME";
    public static final String ignore_duplicated_names_var_name = "IGNORE_DUPLICATED_NAMES";
    public static final int ids_len = 10;
    
    private Options(){
        String value = ConfigFileManager.getInstance().getValueOf(max_iterations_var_name);
        if(value != null){
            this.MAX_ITERATIONS = Integer.parseInt(value);
        }else{
            System.err.println("The value for "+max_iterations_var_name+" not found in the configuration file");
        }
        value = ConfigFileManager.getInstance().getValueOf(null_iterator_var_name);
        if(value != null){
            this.NULL_ITERATION = Integer.parseInt(value);
        }else{
            System.err.println("The value for "+null_iterator_var_name+" not found in the configuration file");
        }
        value = ConfigFileManager.getInstance().getValueOf(max_study_iterations_var_name);
        if(value != null){
            this.MAX_STUDY_ITERATIONS = Integer.parseInt(value);
        }else{
            System.err.println("The value for "+max_study_iterations_var_name+" not found in the configuration file");
        }
        value = ConfigFileManager.getInstance().getValueOf(calendar_file_name_var_name);
        if(value != null){
            this.CALENDAR_FILE_NAME = value;
        }else{
            System.err.println("The value for "+calendar_file_name_var_name+" not found in the configuration file");
        }
        value = ConfigFileManager.getInstance().getValueOf(ignore_duplicated_names_var_name);
        if(value != null){
            this.IGNORE_DUPLICATED_NAMES = value.equals("1");
        }else{
            System.err.println("The value for "+ignore_duplicated_names_var_name+" not found in the configuration file");
        }
    }
    
    public static Options getInstance(){
        if(OP_INSTANCE == null){
            OP_INSTANCE = new Options();
        }
        return OP_INSTANCE;
    }

    public String getCALENDAR_FILE_NAME() {
        return CALENDAR_FILE_NAME;
    }

    public void setCALENDAR_FILE_NAME(String CALENDAR_FILE_NAME) {
        this.CALENDAR_FILE_NAME = CALENDAR_FILE_NAME;
    }

    public int getMAX_ITERATIONS() {
        return MAX_ITERATIONS;
    }

    public void setMAX_ITERATIONS(int MAX_ITERATIONS) {
        this.MAX_ITERATIONS = MAX_ITERATIONS;
    }

    public int getNULL_ITERATION() {
        return NULL_ITERATION;
    }

    public void setNULL_ITERATION(int NULL_ITERATION) {
        this.NULL_ITERATION = NULL_ITERATION;
    }

    public int getMAX_STUDY_ITERATIONS() {
        return MAX_STUDY_ITERATIONS;
    }

    public void setMAX_STUDY_ITERATIONS(int MAX_STUDY_ITERATIONS) {
        this.MAX_STUDY_ITERATIONS = MAX_STUDY_ITERATIONS;
    }
}
