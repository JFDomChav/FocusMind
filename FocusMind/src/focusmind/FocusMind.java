package focusmind;

public class FocusMind {
    public static void main(String[] args) {
        /*
            Siguiente:
                -Estudiar con active recall
                -Calendario que muestre cuando y qué debo estudiar
            El programa crea una tarea, espera el tiempo necesario, puede 
            renovar el tiempo y se elimina si el usuario lo pide o se terminan
            las iteraciones.
        */
        TasksView tv = new TasksView();
        tv.setVisible(true);
    }
}
