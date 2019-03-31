package Controller;

import View.Client;
import java.awt.MouseInfo;
import java.awt.Point;
import java.rmi.RemoteException;
import java.util.logging.Level;
import java.util.logging.Logger;


public class Moving extends Thread{
    private static final int INTERVALO = 20;
    
    private int id;
    private GestorPlayer gp;
    private Client wg;
    private double xFinal,yFinal;
    
    public Moving(int id, GestorPlayer gp, Client wg){
        this.gp = gp;
        this.id = id;
        this.wg = wg;
        this.xFinal = this.yFinal = 0;
    }
    
    @Override
    public void run(){
        while(true){
            try {
                this.updatePositionMouse();
                gp.mover(id, xFinal, yFinal);
                boolean sendgv=false;
                
                if(wg.isGaming()) {
                	
                	 sendgv=gp.checkCollisionVirus(id);
                }
                wg.sendPlayer(id,sendgv);
                Thread.sleep(INTERVALO);
                gp.incrementTimeDuration(id, INTERVALO);
                gp.fusion(id);
            } catch (InterruptedException ex) {
                Logger.getLogger(Moving.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    private void updatePositionMouse(){
        Point mouse = MouseInfo.getPointerInfo().getLocation();
        Point window = wg.getLocationOnScreen();
        this.xFinal = mouse.x - window.x;
        this.yFinal = mouse.y - window.y;
    }
}
