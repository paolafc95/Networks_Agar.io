package View;

import Controller.GestorPlayer;
import Controller.GestorVirus;
import Model.Cell;
import Model.Player;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;


public class DrawingSpace extends Canvas{
    private Image dibujoAux;
    private Graphics gAux;
    private Dimension dimAux;
    private final Dimension dimPanel;
    
    private GestorPlayer players;
    private GestorVirus virus;
    private int id;
    private String TimeWait;
    private String timeGame;
    public DrawingSpace(GestorPlayer players,GestorVirus virus, Dimension d){
        this.players = players;
        this.virus = virus;
        this.setSize(d);
        this.dimPanel = d;
        this.id = -1;
        this.timeGame="5:00";
        this.TimeWait="2:00";
    }
    
    @Override
    public void update(Graphics g){
        paint(g);
    }
    
    @Override
    public void paint(Graphics g){
        if (gAux == null || dimAux == null || dimPanel.width != dimAux.width ||
                dimPanel.height != dimAux.height){
            dimAux = dimPanel;
            dibujoAux = createImage(dimAux.width,dimAux.height);
            gAux = dibujoAux.getGraphics();
        }

        //FONDO
        gAux.setColor(new Color(220, 220, 220));
        gAux.setColor(Color.WHITE);
        gAux.fillRect(0, 0, Client.WINDOW_WIDTH, Client.WINDOW_HEIGHT);
        
        gAux.setColor(new Color(220, 220, 220));
        

        int espacio = 40;
        for (int i=0; i < dimPanel.width; i+=espacio)
            gAux.drawLine(i, 0, i, dimPanel.height);                    
        for (int j=0; j < dimPanel.height; j+=espacio)
            gAux.drawLine(0, j, dimPanel.width, j);
        
        //DIBUJA JUGADORES
        
        if (this.players != null) {
            try {
                this.renderPlayers(this.players,gAux);
            } catch (RemoteException ex) {
                
            }
        } else {
            System.out.println("Players null");
        }
        if (this.virus != null){
            try {
                this.renderVirus(this.virus,gAux);
            } catch (RemoteException ex) {
               
            }
        }else {
            System.out.println("Virus null");
        }
        
        //DRAW LADERBOARD
        try {
            ArrayList<Player> tops = players.getTop(5);
            paintLaderBoard(tops, gAux);
            if (this.id != -1){
                this.paintOwnScore(gAux);
            }
        } catch (RemoteException ex) {
          
        }
        paintTime(gAux);
        g.drawImage(this.dibujoAux, 0, 0, this);
       
        g.dispose();
    }
    
    public void setID(int value){
        this.id = value;
    }
    public GestorPlayer getPlayers() {
		return players;
	}

	public void setPlayers(GestorPlayer players) {
		this.players = players;
	}

	public GestorVirus getVirus() {
		return virus;
	}

	public void setVirus(GestorVirus virus) {
		this.virus = virus;
	}

	private void paintOwnScore(Graphics g) throws RemoteException{
        g.setColor(Color.DARK_GRAY);
        g.setFont(new Font("Ubuntu",Font.BOLD,14));
        int score = this.players.getPlayerID(this.id).getMass()-150;
        g.drawString("Puntaje: "+ score,10,(int) dimPanel.getHeight() - 40);
    }
    
    private void paintLaderBoard(ArrayList<Player> tops, Graphics g){
        g.setColor(Color.DARK_GRAY);
        g.setFont(new Font("Ubuntu",Font.BOLD,15));
        g.drawString("Top jugadores", (int) dimPanel.getWidth()-150, 30);
        g.drawString("-----------------------", (int) dimPanel.getWidth()-150, 40);
        int i = 30;
        int pos = 1;
        for (Player p : tops) {
            i += 20;
            g.setFont(new Font("Ubuntu",Font.BOLD,12));
            g.drawString(pos+". "+p.getNickname() + ':', (int) dimPanel.getWidth()-150, i);
            g.drawString((p.getMass()-150)+ "", (int) dimPanel.getWidth()-75, i);
            pos += 1;
        }
    }
    private void paintTime(Graphics g){
        g.setColor(Color.BLACK);
        g.setFont(new Font("Ubuntu",Font.BOLD,20));
        if(!TimeWait.equals("--:--")) {
        	g.drawString(TimeWait, (int) dimPanel.getWidth()/2,(int) dimPanel.getHeight()/2);
            g.setFont(new Font("Ubuntu",Font.BOLD,15));
            g.drawString("Esperando jugadores", ((int) dimPanel.getWidth()/2)-35,((int) dimPanel.getHeight()/2)+20);
        }else {   
        g.setFont(new Font("Ubuntu",Font.BOLD,12));
        g.drawString("Tiempo de juego", ((int) dimPanel.getWidth()/2)-20,20);
        g.setFont(new Font("Ubuntu",Font.BOLD,14));
        g.drawString(timeGame, ((int) dimPanel.getWidth()/2)-10,35);
        }
    }
    private void renderPlayers(GestorPlayer players,Graphics g) throws RemoteException{
        for(int i = 0; i < players.size(); ++i){
            try {
                Player p = players.getPlayerIterator(i);
                p.render(g, 1);
            } catch(Exception ex){}
        }
    }
    
    private void renderVirus(GestorVirus virus,Graphics g) throws RemoteException{
        for(int i=0; i<virus.size(); ++i){
            try{
                Cell c = virus.getVirus(i);
                c.render(g, 1);
            } catch(Exception ex){}
        }
    }
    public void updateWait(String time) {
    	TimeWait=time;
    }
    public void updateGame(String time) {
    	timeGame=time;
    }
}