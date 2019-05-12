package Controller;

import java.rmi.RemoteException;
import java.util.logging.Level;
import java.util.logging.Logger;

import ServerModel.ServerModel;

public class Collision extends Thread{
    private static final int INTERVAL = 10;
    
    private GestorPlayer gp;
    private ServerModel server;
    public Collision(ServerModel server){
    	this.server=server;
        this.gp = this.server.getGp();
    }
    
    public void run(){
        while(server.isGameStart()){
            try {
                gp.checkCollisions();
                   Thread.sleep(INTERVAL);
            } catch (RemoteException | InterruptedException ex) {
                Logger.getLogger(Collision.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
