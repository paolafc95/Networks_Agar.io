package Controller;
 
import java.rmi.RemoteException;
import java.util.logging.Level;
import java.util.logging.Logger;

import ServerModel.ServerModel;
 
public class Infecting extends Thread {
    private static final int INTERVALO = 1000;  
   
   
    private ServerModel server;
    public Infecting(ServerModel server){
        
        this.server=server;
    }
   
    @Override
    public void run(){
        while(server.isGameStart()){
            try {
                boolean cc=server.getGc().createVirus();
                if(cc) {
                	server.sendFood();
                }
                
                Thread.sleep(INTERVALO);
            } catch ( InterruptedException ex) {
                Logger.getLogger(Infecting.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}