package focusmind;

public class Options {
    private static Options OP_INSTANCE;
    
    private String CALENDAR_FILE_NAME = "calendar.txt";
    private int MAX_ITERATIONS = 7;
    private int NULL_ITERATION = 9;
    private int MAX_STUDY_ITERATIONS = 5;
    
    private Options(){}
    
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
