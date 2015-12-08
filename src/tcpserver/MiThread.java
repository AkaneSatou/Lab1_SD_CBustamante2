/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package tcpserver;

/**
 *
 * @author Clau
 */
public class MiThread extends Thread {
    private String mensaje;
   
    
    public MiThread(String fichero){ //constructor de los datos
        mensaje = fichero;
        
    }
    
    public void run(){
        try{   
            sleep(3);
        }catch(InterruptedException e){
        ;
        }
        System.out.println("me toca leer: "+mensaje);
    }
}
