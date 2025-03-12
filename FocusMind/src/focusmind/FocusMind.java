package focusmind;

public class FocusMind {
    public static void main(String[] args) {
        /*
            Siguiente:
                -Estudiar con active recall
                -Calendario que muestre cuando y qué debo estudiar
            El programa hace una lsita de tareas. Falta que el programa
            elimine las tareas cuando no se ocupen y suba hacia lo mas alto
            las tareas que ya terminaron su ciclo de espera
        */
        TasksView tv = new TasksView();
        tv.setVisible(true);
    }
}
