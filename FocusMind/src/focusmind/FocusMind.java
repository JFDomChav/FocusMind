package focusmind;

import java.util.logging.Level;
import java.util.logging.Logger;

public class FocusMind {
    public static void main(String[] args) {
        /*
            Siguiente:
                -Estudiar con active recall
                -Calendario que muestre cuando y qué debo estudiar
            El programa checa cada segundo si hay alguna tarea nueva que se deba estudiar
            y si la hay entonces la agrega al arraylist y notifica a todos
            los hilos que esperan su respuesta.
            El programa puede poner mas tiempo cuando la tarea ya fue estudiada
            y eliminarla cuando alcanza el maximo de iteraciones.
        */
        ActiveRecallManager arm = new ActiveRecallManager(1);
        
        Thread hilo = new Thread(){
            @Override
            public void run(){
                while(true){
                    try {
                        synchronized(arm){
                            arm.wait();
                            // Esperar a que el hilo notifique
                        }
                    } catch (InterruptedException ex) {
                        Logger.getLogger(FocusMind.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        };
        arm.start();
        hilo.start();
        int id = arm.addTask();
        System.out.println(id);
    }
}
